/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.batik.bridge.resources.Messages;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.FilterChainRable8Bit;
import org.apache.batik.ext.awt.image.renderable.FilterChainRable;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.filter.GraphicsNodeRable;
import org.apache.batik.gvt.filter.GraphicsNodeRableFactory;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.UnitProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.svg.SVGDocument;
import org.xml.sax.SAXException;

/**
 * This class bridges an SVG <tt>filter</tt> element with a concrete
 * <tt>Filter</tt>.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGFilterElementBridge implements FilterBridge, SVGConstants {

    /**
     * Returns the <tt>Filter</tt> that implements the filter
     * operation modeled by the input DOM element
     *
     * @param filteredNode the node to which the filter will be attached.
     * @param bridgeContext the context to use.
     * @param filterElement DOM element that represents a filter abstraction
     * @param filteredElement DOM element that is filtered.
     * @param in the <tt>Filter</tt> that represents the current
     *        filter input if the filter chain.
     * @param filterMap a map where the mediator can map a name to the
     *        <tt>Filter</tt> it creates. Other <tt>FilterBridge</tt>s
     *        can then access a filter node from the filterMap if they
     *        know its name.
     */
    public Filter create(GraphicsNode filteredNode,
                         BridgeContext bridgeContext,
                         Element filterElement,
                         Element filteredElement,
                         Filter in,
                         Rectangle2D filterRegion,
                         Map filterMap){

         GraphicsNodeRenderContext rc =
                     bridgeContext.getGraphicsNodeRenderContext();
        DocumentLoader loader = bridgeContext.getDocumentLoader();

        // Make the initial source as a RenderableImage
        GraphicsNodeRableFactory gnrFactory
            = bridgeContext.getGraphicsNodeRableFactory();

        GraphicsNodeRable sourceGraphic
            = gnrFactory.createGraphicsNodeRable(filteredNode, rc);

        // Get the filter region and resolution
        CSSStyleDeclaration cssDecl
            = CSSUtilities.getComputedStyle(filterElement);

        UnitProcessor.Context uctx
            = new DefaultUnitProcessorContext(bridgeContext, cssDecl);

        filterRegion = SVGUtilities.convertFilterChainRegion(filterElement,
                                                             filteredElement,
                                                             filteredNode,
                                                             rc,
                                                             uctx,
                                                             loader);

        // Build a FilterChainRable8Bit
        FilterChainRable filterChain
            = new FilterChainRable8Bit(sourceGraphic, filterRegion);

        // parse the filter resolution attribute
        String resStr = SVGUtilities.getChainableAttributeNS(filterElement,
                                                             null,
                                                             SVG_FILTER_RES_ATTRIBUTE,
                                                             loader);
        Float [] filterResolution = SVGUtilities.buildFloatPair(resStr);
        float filterResolutionX = -1; // -1 means undefined
        if (filterResolution[0] != null) {
            filterResolutionX = filterResolution[0].floatValue();
            if (filterResolutionX == 0) {
                return null; // zero value disable rendering of the filter
            }
            if (filterResolutionX < 0) {
                // A negative value is an error
                throw new IllegalAttributeValueException(
                    Messages.formatMessage("filter.filterResX.invalid", null));
            }
        }
        float filterResolutionY = filterResolutionX; // default is filterResX
        if (filterResolution[1] != null) {
            filterResolutionY = filterResolution[1].floatValue();
            if (filterResolutionY == 0) {
                return null; // zero value disable rendering of the filter
            }
            if (filterResolutionY < 0) {
                // A negative value is an error
                throw new IllegalAttributeValueException(
                    Messages.formatMessage("filter.filterResY.invalid", null));
            }
        }

        // Set resolution in filterChain
        filterChain.setFilterResolutionX((int)filterResolutionX);
        filterChain.setFilterResolutionY((int)filterResolutionY);

        // Now build the filter chain. Create a map for filter nodes
        // to advertise themselves as named sources.
        Map filterNodeMap = new HashMap();
        if (in == null) {
            // For the filter element, the in parameter is overridden
            in = sourceGraphic;
            filterNodeMap.put(VALUE_SOURCE_GRAPHIC, sourceGraphic);
        }

        Element e = filterElement;
        List refs = new LinkedList();
        boolean hasPrimitives = false;
        for (;;) {
            for (Node child=e.getFirstChild();
                 child != null;
                 child = child.getNextSibling()) {

                if (child.getNodeType() != Node.ELEMENT_NODE) {
                    continue; // skip node that is not an Element
                }
                hasPrimitives = true;
                Element elt = (Element)child;
                Bridge bridge = bridgeContext.getBridge(elt);
                if (bridge == null || !(bridge instanceof FilterPrimitiveBridge)) {
                    continue;
                }
                FilterPrimitiveBridge filterBridge =
                    (FilterPrimitiveBridge)bridge;
                Filter filterNode = filterBridge.create(filteredNode,
                                                        bridgeContext,
                                                        elt,
                                                        filteredElement,
                                                        in,
                                                        filterRegion,
                                                        filterNodeMap);
                if (filterNode == null) {
                    throw new IllegalAttributeValueException(
                        Messages.formatMessage("filter.subelement.invalid",
                                               new Object[] {elt.getLocalName()}));
                }
                in = filterNode;
            }
            if (hasPrimitives) {
                break; // exit if filter primitives have been found
            }
            String uriStr = XLinkSupport.getXLinkHref(e);
            if (uriStr.length() == 0) {
                break; // exit if no more xlink:href
            }
            SVGDocument svgDoc = (SVGDocument)e.getOwnerDocument();
            URL baseURL = ((SVGOMDocument)svgDoc).getURLObject();
            try {
                URL url = new URL(baseURL, uriStr);
                Iterator iter = refs.iterator();
                while (iter.hasNext()) {
                    URL urlTmp = (URL)iter.next();
                    if (urlTmp.sameFile(url) &&
                            urlTmp.getRef().equals(url.getRef())) {
                        throw new IllegalAttributeValueException(
                            "circular reference on "+e);
                    }
                }
                URIResolver resolver = new URIResolver(svgDoc, loader);
                e = resolver.getElement(url.toString());
                refs.add(url);
            } catch(MalformedURLException ex) {
                throw new IllegalAttributeValueException("bad url on "+uriStr);
            } catch(IOException ex) {
                throw new IllegalAttributeValueException("I/O error on "+uriStr);
            }
        }

        // Set the source on the filter node
        if(in != sourceGraphic){
            filterChain.setSource(in);
        } else {
            // No child filter node. Disable filter
            //filterChain.setSource(null);
            return null;
        }

        return filterChain;
    }

    /**
     * Update the <tt>Filter</tt> object to reflect the current
     * configuration in the <tt>Element</tt> that models the filter.
     */
    public void update(BridgeMutationEvent evt) {
        // <!> FIXME : TODO
    }
}

