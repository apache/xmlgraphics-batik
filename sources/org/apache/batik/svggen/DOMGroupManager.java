/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.awt.font.*;

import org.w3c.dom.*;

/**
 * This class is used by the Graphics2D SVG Generator to manage
 * a group of Nodes that can later be added to the SVG DOM Tree
 * managed by the DOMTreeManager.
 *
 * There are two rules that control how children nodes are
 * added to the group managed by this class:
 *
 * + Children node are added to the group as long as
 *   there is no more than n graphic context overrides needed to
 *   describe the children style. A graphic context override
 *   happens when style attributes need to be added to a child
 *   node to reflect the state of the graphic context at the
 *   time the child was added. Note that the opacity is never
 *   reflected in a group node and therefore, is not accounted
 *   for in the number of overrides. The number of overrides can
 *   be configured and defaults to 2.
 * + Children nodes are added to the current group as long as
 *   the associated GraphicContext's transform stack is valid.
 *
 * When children nodes can no longer be added, the group is considered
 * complete and the associated DOMTreeManager is notified of the
 * availability of a completed group. Then, a new group is started.
 * <br>
 * The DOMTreeManager is also notified every thime a new element
 * is added to the current group. This is needed to let the
 * DOMTreeManager handle group managers that would be used concurrently.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class DOMGroupManager implements SVGSyntax{
    static final String ERROR_GC_NULL = "gc should not be null";
    static final String ERROR_DOMTREEMANAGER_NULL = "domTreeManager should not be null";

    /**
     * Reference to the GraphicContext this manager will use to
     * reflect style attributes in the tree nodes.
     */
    private GraphicContext gc;

    /**
     * DOMTreeManager that this group manager cooperates with
     */
    private DOMTreeManager domTreeManager;

    /**
     * Current group's SVG GraphicContext state
     */
    private SVGGraphicContext groupGC;

    /**
     * Current group node
     */
    private Element currentGroup;

    /**
     * Constructor
     * @param gc graphic context whose state will be reflected in the
     *           element's style attributes.
     * @param domTreeManager DOMTreeManager instance this group manager
     *        cooperates with.
     */
    public DOMGroupManager(GraphicContext gc, DOMTreeManager domTreeManager){
        if(gc == null)
            throw new IllegalArgumentException(ERROR_GC_NULL);

        if(domTreeManager == null)
            throw new IllegalArgumentException(ERROR_DOMTREEMANAGER_NULL);

        this.gc = gc;
        this.domTreeManager = domTreeManager;

        // Start with a new Top Level Group
        recycleCurrentGroup();

        // Build the default GC descriptor
        groupGC = domTreeManager.gcConverter.toSVG(gc);
    }

    /**
     * Reset the state of this object to handle a new currentGroup
     */
    void recycleCurrentGroup(){
        // Create new initial current group node
        currentGroup = domTreeManager.domFactory.createElement(SVG_G_TAG);
    }

    /**
     * Add a node to the current group, if possible
     * @param element child Element to add to the group
     */
    public void addElement(Element element){
        //
        // If this is the first child to be added to the
        // currentGroup, 'freeze' the style attributes.
        //
        if(!currentGroup.hasChildNodes()){
            currentGroup.appendChild(element);

            groupGC = domTreeManager.gcConverter.toSVG(gc);
            SVGGraphicContext deltaGC = processDeltaGC(groupGC, domTreeManager.defaultGC);
            setAttributes(currentGroup, deltaGC.getGroupContext());
            setAttributes(element, deltaGC.getGraphicElementContext());
            setTransform(currentGroup, deltaGC.getTransformStack());
            domTreeManager.appendGroup(currentGroup, this);
        }
        else{
            if(gc.isTransformStackValid()){
                                //
                                // There are children nodes already. Find
                                // out delta between current gc and group
                                // context
                                //
                SVGGraphicContext elementGC = domTreeManager.gcConverter.toSVG(gc);
                SVGGraphicContext deltaGC = processDeltaGC(elementGC, groupGC);

                                // If there are less than the maximum number
                                // of differences, then add the node to the current
                                // group and set its attributes
                trimContextForElement(deltaGC, element);
                if(countOverrides(deltaGC) <= domTreeManager.maxGCOverrides){
                    currentGroup.appendChild(element);
                    setAttributes(element, deltaGC.getGroupContext());
                    setAttributes(element, deltaGC.getGraphicElementContext());
                    setTransform(element, deltaGC.getTransformStack());
                }
                else{
                    //
                    // Need to create a new current group
                    //
                    currentGroup = domTreeManager.domFactory.createElement(SVG_G_TAG);
                    addElement(element);
                }
            }
            else{
                                //
                                // Transform stack is invalid. Create a new current
                                // group and validate the stack
                                //
                currentGroup = domTreeManager.domFactory.createElement(SVG_G_TAG);
                gc.validateTransformStack();
                addElement(element);
            }
        }
    }

    /**
     * Analyses the Map to define how many attributes constitute
     * overrides. Only differences in the group context are considered
     * overrides.
     */
    private int countOverrides(SVGGraphicContext deltaGC){
        return deltaGC.getGroupContext().size();
    }

    /**
     * Removes properties that do not apply for a specific element
     */
    private void trimContextForElement(SVGGraphicContext svgGC, Element element){
        String tag = element.getTagName();
        Map groupAttrMap = svgGC.getGroupContext();
        if(tag != null){
            // For each attribute, check if there is an attribute
            // descriptor. If there is, check if the attribute
            // applies to the input element. If there is none,
            // assume the attribute applies to the element.
            Iterator iter = groupAttrMap.keySet().iterator();
            while(iter.hasNext()){
                String attrName = (String)iter.next();
                SVGAttribute attr = SVGAttributeMap.get(attrName);
                if(attr != null && !attr.appliesTo(tag))
                    groupAttrMap.remove(attrName);
            }
        }
    }

    /**
     * Processes the transform attribute value corresponding to a
     * given transform stack
     */
    private void setTransform(Element element, TransformStackElement transformStack[]){
        String transform = domTreeManager.gcConverter.toSVG(transformStack).trim();
        if(transform.length() > 0)
            element.setAttributeNS(SVG_NAMESPACE_URI, ATTR_TRANSFORM, transform);
    }

    /**
     * Implementation helper: sets attributes in input attrMap
     * into input element. If the element's attribute is already
     * specified, it is *not* overridden.
     */
    private void setAttributes(Element element, Map attrMap){
        String tagName = element.getTagName();
        Iterator iter = attrMap.keySet().iterator();
        while(iter.hasNext()){
            String attrName = (String)iter.next();
            if(element.getAttributeNS(SVG_NAMESPACE_URI, attrName).length() == 0)
                element.setAttributeNS(SVG_NAMESPACE_URI, attrName, (String)attrMap.get(attrName));
        }
    }

    /**
     * Processes the difference between two graphic contexts. The values
     * in gc that are different from the values in referenceGc will be
     * present in the delta. Other values will no.
     */
    private SVGGraphicContext processDeltaGC(SVGGraphicContext gc, SVGGraphicContext referenceGc){
        Map groupDelta = processDeltaMap(gc.getGroupContext(),
                                         referenceGc.getGroupContext());
        Map graphicElementDelta = gc.getGraphicElementContext();

        TransformStackElement gcTransformStack[] = gc.getTransformStack();
        TransformStackElement referenceStack[] = referenceGc.getTransformStack();
        int deltaStackLength = gcTransformStack.length - referenceStack.length;
        TransformStackElement deltaTransformStack[] = new TransformStackElement[deltaStackLength];

        System.arraycopy(gcTransformStack, referenceStack.length, deltaTransformStack, 0, deltaStackLength);

        // System.err.println("gc transform stack length: " + gc.getTransformStack().length);
        // System.err.println("reference stack length   : " + referenceGc.getTransformStack().length);
        // System.err.println("delta stack length       : " + deltaTransformStack.length);
        /*
          TransformStackElement gcStack[] = gc.getTransformStack();
          for(int i=0; i<gcStack.length; i++)
          System.err.println("gcStack[" + i + "] = " + gcStack[i].toString());

          TransformStackElement refStack[] = referenceGc.getTransformStack();
          for(int i=0; i<refStack.length; i++)
          System.err.println("refStack[" + i + "] = " + refStack[i].toString());

          for(int i=0; i<deltaTransformStack.length; i++)
          System.err.println("deltaStack[" + i + "] = " + deltaTransformStack[i].toString());
        */

        SVGGraphicContext deltaGC = new SVGGraphicContext(groupDelta, graphicElementDelta, deltaTransformStack);
        // System.out.println("===>>> Reference GC");
        // SVGGraphicContextConverter.traceSVGGC(referenceGc, domTreeManager.gcConverter);

        // System.out.println("===>>> Delta GC");
        // SVGGraphicContextConverter.traceSVGGC(deltaGC, domTreeManager.gcConverter);
        return deltaGC;
    }

    /**
     * Processes the difference between two Maps. The code assumes
     * that the input Maps have the same key sets. Values in map that
     * are different from values in referenceMap are place in the
     * returned delta Map.
     */
    private Map processDeltaMap(Map map, Map referenceMap){
        Map mapDelta = new Hashtable();
        Iterator iter = map.keySet().iterator();
        while(iter.hasNext()){
            String key = (String)iter.next();
            String value = (String)map.get(key);
            String refValue = (String)referenceMap.get(key);
            if(!value.equals(refValue)){
                                /*if(key.equals(ATTR_TRANSFORM)){
                                  // Special handling for the transform attribute.
                                  // At this point in the processing, the transform
                                  // in map has to be a substring of the one in
                                  // referenceMap. see the addElement member.
                                  value = value.substring(refValue.length()).trim();
                                  }*/
                mapDelta.put(key, value);
            }
        }

        return mapDelta;
    }

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception {
        /*      Document domFactory = TestUtil.getDocumentPrototype();

                GraphicContext gc = new GraphicContext(new AffineTransform());
                DOMGroupManager domTreeManager
                = new DOMGroupManager(gc,
                domFactory,
                new DefaultExtensionHandler(),
                new DefaultImageHandler(),
                2);

                //
                // Do the following:
                // + Add one rect element
                // + Modify the Paint (modif 1)
                // + Add one ellipse element. Should be under the same group
                // + Modify the Composite (modif 2, ignored, as it does not apply to a group)
                // + Add one circle element. Should be under the same group
                // + Modify the Clip (modif 2bis)
                // + Modify the Transform (modif 3, over limit)
                // + Add one path element: Should be under a new group.
                // + Set the transform to a new transform (new group trigger)
                // + Add a polygon: should be under a new group
                //

                Element rect = domFactory.createElement(TAG_RECT);
                Element ellipse = domFactory.createElement(TAG_ELLIPSE);
                Element circle = domFactory.createElement(TAG_CIRCLE);
                Element path = domFactory.createElement(TAG_PATH);
                Element polygon = domFactory.createElement(TAG_POLYGON);

                rect.setAttributeNS(SVG_NAMESPACE_URI, ATTR_FILL, VALUE_NONE);
                polygon.setAttributeNS(SVG_NAMESPACE_URI, ATTR_STROKE, VALUE_NONE);

                domTreeManager.addElement(rect);

                // Modif 1
                gc.setPaint(Color.red);

                // Ellipse element
                domTreeManager.addElement(ellipse);

                // Modif 2
                gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, .5f));

                // Circle element
                domTreeManager.addElement(circle);

                // Modif 2bis
                gc.setClip(new Rectangle(30, 30, 60, 60));

                // Modif 3
                gc.translate(45, 45);

                // Path element (should be in a new group)
                domTreeManager.addElement(path);

                // Modify transform
                gc.setTransform(AffineTransform.getScaleInstance(45, 50));

                // Polygon element (should be in a new group as well).
                domTreeManager.addElement(polygon);

                //
                // Now, trace the resulting tree
                //
                Element topLevelGroup = domTreeManager.getTopLevelGroup();
                TestUtil.trace(topLevelGroup, System.out);*/
    }
}
