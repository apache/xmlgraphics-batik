/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.w3c.dom.*;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGOMDocument;

import org.apache.batik.util.SVGConstants;

import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import java.awt.font.TextAttribute;

/**
 * This class factorizes all unit tests of the SVG generator classes.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGGraphics2DUnitTester implements SVGConstants {
    private String[] args;

    public static void main(String[] arg) {
        new SVGGraphics2DUnitTester(arg).runTest();
    }

    protected SVGGraphics2DUnitTester(String[] arg) {
        args = arg;
    }

    protected Document getDocumentPrototype() {
        return new SVGOMDocument(null,
                                 SVGDOMImplementation.getDOMImplementation());
    }

    protected SVGGeneratorContext getContext(Document domFactory) {
        return SVGGeneratorContext.createDefault(domFactory);
    }

    protected void trace(Element element, OutputStream out)
        throws IOException {
        Writer writer = new OutputStreamWriter(out);
        XmlWriter.writeXml(element, writer);
        writer.flush();
    }

    /**
     * Runs the different tests of the test collection.
     */
    public void runTest() {
        // run the different tests
        Object[] args = {};
        Method[] methods = getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            String name = m.getName();
            if (isTestMethod(m)) {
                System.out.println();
                System.out.println("testing "+ name.substring(4, name.length()));
                System.out.println();
                try {
                    m.invoke(this, args);
                } catch (InvocationTargetException e) {
                    e.getTargetException().printStackTrace();
                } catch (IllegalAccessException never) {
                }
            }
        }
        System.exit(0);
    }

    /**
     * This method returns <code>true</code> if a method
     * should be considered a test method.
     * By default it returns <code>true</code> when the method
     * name starts with test string, is public, returns void
     * and has no parameter.
     */
    protected boolean isTestMethod(Method m) {
        return m.getName().startsWith("test")
            && m.getParameterTypes().length == 0
            && m.getReturnType().equals(Void.TYPE)
            && Modifier.isPublic(m.getModifiers());
    }

    public void testCustomization() throws Exception {
        SVGGraphics2D g2d = new SVGGraphics2D(getDocumentPrototype(),
                                              new DefaultImageHandler(),
                                              new DefaultExtensionHandler(), false );
    }

    public void testDOMGroupManager() throws Exception {
        // was commented => let it like that
        /*
          Document domFactory = getDocumentPrototype();

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

          Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI, TAG_RECT);
          Element ellipse = domFactory.createElementNS(SVG_NAMESPACE_URI, TAG_ELLIPSE);
          Element circle = domFactory.createElementNS(SVG_NAMESPACE_URI, TAG_CIRCLE);
          Element path = domFactory.createElementNS(SVG_NAMESPACE_URI, TAG_PATH);
          Element polygon = domFactory.createElementNS(SVG_NAMESPACE_URI, TAG_POLYGON);

          rect.setAttributeNS(null, ATTR_FILL, VALUE_NONE);
          polygon.setAttributeNS(null, ATTR_STROKE, VALUE_NONE);

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
          trace(topLevelGroup, System.out);
        */
    }

    public void testDOMTreeManager() throws Exception {
        Document domFactory = getDocumentPrototype();

        GraphicContext gc = new GraphicContext(new AffineTransform());
        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(domFactory);
        DOMTreeManager domTreeManager
            = new DOMTreeManager(gc,
                                 ctx,
                                 2);

        DOMGroupManager domGroupManager
            = new DOMGroupManager(gc, domTreeManager);

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

        Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                  SVG_RECT_TAG);
        Element ellipse = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                     SVG_ELLIPSE_TAG);
        Element circle = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                    SVG_CIRCLE_TAG);
        Element path = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                  SVG_PATH_TAG);
        Element polygon = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                     SVG_POLYGON_TAG);

        rect.setAttributeNS(null, SVG_FILL_ATTRIBUTE, SVG_NONE_VALUE);
        polygon.setAttributeNS(null, SVG_STROKE_ATTRIBUTE, SVG_NONE_VALUE);

        domGroupManager.addElement(rect);

        // Modif 1
        gc.setPaint(Color.red);

        // Ellipse element
        domGroupManager.addElement(ellipse);

        // Modif 2
        gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, .5f));

        // Circle element
        domGroupManager.addElement(circle);

        // Modif 2bis
        gc.setClip(new Rectangle(30, 30, 60, 60));

        // Modif 3
        gc.translate(45, 45);

        GraphicContext gc2 = (GraphicContext)gc.clone();
        DOMGroupManager domGroupManager2
            = new DOMGroupManager(gc2, domTreeManager);

        // Path element (should be in a new group)
        domGroupManager2.addElement(path);

        // Modify transform
        gc2.setTransform(AffineTransform.getScaleInstance(45, 50));

        // Polygon element (should be in a new group as well).
        domGroupManager2.addElement(polygon);

        //
        // Now, trace the resulting tree
        //
        Element topLevelGroup = domTreeManager.getTopLevelGroup();
        trace(topLevelGroup, System.out);
    }

    public void testImageHandlerBase64Encoder() throws Exception {
           BufferedImage buf = null;

           if (args.length == 0) {
               buf = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
               Graphics2D g = buf.createGraphics();
               g.setPaint(Color.red);
               g.fillRect(0, 0, 50, 50);
               g.fillRect(50, 50, 50, 50);
               g.dispose();
           } else {
               Component cmp = new Component(){};
               MediaTracker mediaTracker = new MediaTracker(cmp);
               Image img = Toolkit.getDefaultToolkit().createImage(args[0]);
               mediaTracker.addImage(img, 0);
               try {
                   mediaTracker.waitForAll();
               } catch(InterruptedException e) {
                   img = null;
               }

               if (img == null) {
                   System.err.println("Could not load : " + args[0]);
               }

               buf = new BufferedImage(img.getWidth(null),
                                       img.getHeight(null),
                                       BufferedImage.TYPE_INT_ARGB);
               Graphics2D g = buf.createGraphics();
               g.drawImage(img, 0, 0, null);
               g.dispose();
           }

           Document domFactory = getDocumentPrototype();
           ImageHandler imageHandler =
               new ImageHandlerBase64Encoder();
           Element imageElement = domFactory.createElementNS(SVG_NAMESPACE_URI, SVGSyntax.SVG_IMAGE_TAG);

           imageHandler.handleImage((RenderedImage)buf, imageElement,
                                    getContext(domFactory));

           System.out.println("<?xml version=\"1.0\" standalone=\"no\"?>");
           System.out.println("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20000802//EN\"");
           System.out.println("\"http://www.w3.org/TR/2000/CR-SVG-20000802/DTD/svg-20000802.dtd\">");
           System.out.println();
           System.out.println("<svg width=\"450\" height=\"500\">");
           System.out.println("    <rect width=\"100%\" height=\"100%\" fill=\"yellow\" />");
           System.out.println("    <image width=\"" + buf.getWidth() + "\" height=\"" +
                              buf.getHeight() + "\" xlink:href=\"" + XLinkSupport.getXLinkHref(imageElement) + "\" />");
           System.out.println("</svg>");
    }

    public void testImageHandlerJPEGEncoder() throws Exception {
        String imageDir;
        String urlRoot = null;
        if (args.length == 3) {
            imageDir = args[1];
            urlRoot = args[2];
        } else {
            imageDir = ".";
        }

        Document domFactory = getDocumentPrototype();
        ImageHandler imageHandler =
            new ImageHandlerJPEGEncoder(imageDir,urlRoot);
        Element imageElement = domFactory.createElementNS(SVG_NAMESPACE_URI, SVGSyntax.SVG_IMAGE_TAG);

        BufferedImage testImage = new BufferedImage(60, 40, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = testImage.createGraphics();
        g.setPaint(Color.white);
        g.fillRect(0, 0, 60, 40);
        g.setPaint(Color.green);
        g.fillRect(0, 0, 20, 40);
        g.setPaint(Color.red);
        g.fillRect(40, 0, 60, 40);
        g.dispose();

        imageHandler.handleImage((RenderedImage)testImage, imageElement,
                                 getContext(domFactory));
        System.out.println("Generated xlink:href is : " + imageElement.getAttributeNS(null, SVGSyntax.SVG_HREF_ATTRIBUTE));
    }

    public void testImageHandlerPNGEncoder() throws Exception {
        String imageDir;
        String urlRoot = null;
        if (args.length == 3) {
            imageDir = args[1];
            urlRoot = args[2];
        } else {
            imageDir = ".";
        }

        Document domFactory = getDocumentPrototype();
        ImageHandler imageHandler =
            new ImageHandlerPNGEncoder(imageDir, urlRoot);
        Element imageElement = domFactory.createElementNS(SVG_NAMESPACE_URI, SVGSyntax.SVG_IMAGE_TAG);

        BufferedImage testImage = new BufferedImage(60, 40, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = testImage.createGraphics();
        g.setPaint(Color.green);
        g.fillRect(0, 0, 20, 40);
        g.setPaint(Color.red);
        g.fillRect(40, 0, 60, 40);
        g.dispose();

        imageHandler.handleImage((RenderedImage)testImage, imageElement,
                                 getContext(domFactory));
        System.out.println("Generated xlink:href is : " + imageElement.getAttributeNS(null, SVGSyntax.ATTR_XLINK_HREF));
    }

    public void testSVGAlphaComposite() throws Exception {
        AlphaComposite ac = AlphaComposite.Src;
        AlphaComposite composites[] = { ac.SrcOver,
                                        ac.Src,
                                        ac.SrcIn,
                                        ac.SrcOut,
                                        ac.DstIn,
                                        ac.DstOut,
                                        ac.Clear,
                                        ac.getInstance(ac.SRC_OVER, .5f),
                                        ac.getInstance(ac.SRC, .5f),
                                        ac.getInstance(ac.SRC_IN, .5f),
                                        ac.getInstance(ac.SRC_OUT, .5f),
                                        ac.getInstance(ac.DST_IN, .5f),
                                        ac.getInstance(ac.DST_OUT, .5f),
                                        ac.getInstance(ac.CLEAR, .5f) };

        Document domFactory = getDocumentPrototype();
        SVGAlphaComposite converter =
            new SVGAlphaComposite(getContext(domFactory));

        Element groupOne = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        groupOne.setAttributeNS(null, SVG_ID_ATTRIBUTE, "groupOne");
        buildTestGroup(groupOne, composites, converter);

        Element groupTwo = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        groupTwo.setAttributeNS(null, SVG_ID_ATTRIBUTE, "group2");
        buildTestGroup(groupTwo, composites, converter);

        Element defs = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_DEFS_TAG);
        Iterator iter = converter.getAlphaCompositeFilterSet().iterator();
        while(iter.hasNext()){
            Element filter = (Element)iter.next();
            defs.appendChild(filter);
        }

        Element groupThree = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        groupThree.setAttributeNS(null, SVG_ID_ATTRIBUTE, "groupThree");
        SVGAlphaComposite newConverter =
            new SVGAlphaComposite(getContext(domFactory));
        buildTestGroup(groupThree, new AlphaComposite[]{ ac.SrcIn, ac.DstOut },
        newConverter);
        Element newDefs = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_DEFS_TAG);
        newDefs.setAttributeNS(null, SVG_ID_ATTRIBUTE, "alphaCompositeSubset");
        Iterator newIter = newConverter.getDefinitionSet().iterator();
        while(newIter.hasNext()){
            Element filter = (Element)newIter.next();
            newDefs.appendChild(filter);
        }

        groupThree.insertBefore(newDefs, groupThree.getFirstChild());

        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        group.appendChild(defs);
        group.appendChild(groupOne);
        group.appendChild(groupTwo);
        group.appendChild(groupThree);

        trace(group, System.out);
    }

    private void buildTestGroup(Element group, AlphaComposite composites[],
                                SVGAlphaComposite converter) {
        Document domFactory = group.getOwnerDocument();

        for(int i=0; i<composites.length; i++){
            SVGCompositeDescriptor compositeDesc = converter.toSVG(composites[i]);
            Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_RECT_TAG);
            rect.setAttributeNS(null, SVG_OPACITY_ATTRIBUTE, compositeDesc.getOpacityValue());
            if(compositeDesc.getDef() != null)
                rect.setAttributeNS(null, SVG_FILTER_ATTRIBUTE, compositeDesc.getFilterValue());
            group.appendChild(rect);
        }
    }

    public void testSVGBasicStroke() throws Exception {
        Document domFactory = getDocumentPrototype();

        BasicStroke strokes[] = { new BasicStroke(),
                                  new BasicStroke(2),
                                  new BasicStroke(4.5f),
                                  new BasicStroke(10, BasicStroke.CAP_BUTT,
                                                  BasicStroke.JOIN_MITER),
                                  new BasicStroke(10, BasicStroke.CAP_SQUARE,
                                                  BasicStroke.JOIN_MITER),
                                  new BasicStroke(10, BasicStroke.CAP_ROUND,
                                                  BasicStroke.JOIN_MITER),
                                  new BasicStroke(10, BasicStroke.CAP_BUTT,
                                                  BasicStroke.JOIN_BEVEL),
                                  new BasicStroke(10, BasicStroke.CAP_BUTT,
                                                  BasicStroke.JOIN_ROUND),
                                  new BasicStroke(50, BasicStroke.CAP_ROUND,
                                                  BasicStroke.JOIN_MITER, 100),
                                  new BasicStroke(75, BasicStroke.CAP_ROUND,
                                                  BasicStroke.JOIN_ROUND, 50,
                                                  new float[]{1, 2, 3, 4}, 0.5f),
                                  new BasicStroke(75, BasicStroke.CAP_ROUND,
                                                  BasicStroke.JOIN_ROUND, 60,
                                                  new float[]{10.1f, 2.4f, 3.5f, 4.2f},
                                                  10)
        };

        Element rectGroup = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);

        for(int i=0; i<strokes.length; i++){
            BasicStroke stroke = strokes[i];
            Map attrMap = SVGBasicStroke.toSVG(stroke).getAttributeMap(null);
            Element rectElement = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_RECT_TAG);
            Iterator iter = attrMap.keySet().iterator();
            while(iter.hasNext()){
                String attrName = (String)iter.next();
                String attrValue = (String)attrMap.get(attrName);
                rectElement.setAttributeNS(null, attrName, attrValue);
            }
            rectGroup.appendChild(rectElement);
        }
        trace(rectGroup, System.out);
    }

    public void testSVGBufferedImageOp() throws Exception {
        byte bi[] = new byte[256];
        for(int i=0; i<=255; i++)
            bi[i] = (byte)(0xff & (255-i));

        float kernelData[] = { 1, 1, 1,
                               2, 2, 2,
                               3, 3, 3 };
        Kernel kernel = new Kernel(3, 3, kernelData);

        BufferedImageOp ops[] = { new LookupOp(new ByteLookupTable(0, bi), null),
                                  new RescaleOp(4, 0, null),
                                  new ConvolveOp(kernel),
                                  new NullOp(),
        };

        Document domFactory = getDocumentPrototype();
        SVGBufferedImageOp converter =
            new SVGBufferedImageOp(getContext(domFactory));

        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                   SVG_G_TAG);
        Element defs = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                  SVG_DEFS_TAG);
        Element rectGroupOne = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                          SVG_G_TAG);
        Element rectGroupTwo = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                          SVG_G_TAG);

        for(int i=0; i<ops.length; i++){
            SVGFilterDescriptor filterDesc = converter.toSVG(ops[i], null);
            if(filterDesc != null){
                Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                          SVG_RECT_TAG);
                rect.setAttributeNS(null, SVG_FILTER_ATTRIBUTE, filterDesc.getFilterValue());
                rectGroupOne.appendChild(rect);
            }
        }

        for(int i=0; i<ops.length; i++){
            SVGFilterDescriptor filterDesc = converter.toSVG(ops[i], null);
            if(filterDesc != null){
                Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                          SVG_RECT_TAG);
                rect.setAttributeNS(null, SVG_FILTER_ATTRIBUTE, filterDesc.getFilterValue());
                rectGroupTwo.appendChild(rect);
            }
        }

        Iterator iter = converter.getDefinitionSet().iterator();
        while(iter.hasNext()){
            Element filterDef = (Element)iter.next();
            defs.appendChild(filterDef);
        }

        group.appendChild(defs);
        group.appendChild(rectGroupOne);
        group.appendChild(rectGroupTwo);
        trace(group, System.out);
    }

    public void testCSSStyler() throws Exception {
        SVGGraphics2D g = new SVGGraphics2D(getDocumentPrototype());
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                           java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        // Text
        g.setPaint(new java.awt.Color(103, 103, 152));
        g.fillRect(10, 10, 200, 50);
        g.setPaint(java.awt.Color.white);
        g.setFont(new java.awt.Font("SunSansCondensed-Heavy", java.awt.Font.PLAIN, 20));
        g.drawString("Hello Java 2D to SVG", 40f, 40f);

        g.stream(new java.io.OutputStreamWriter(System.out));
    }

    public void testSVGClip() throws Exception {
        Polygon polygon = new Polygon();
        polygon.addPoint(1, 1);
        polygon.addPoint(2, 1);
        polygon.addPoint(3, 2);
        polygon.addPoint(3, 3);
        polygon.addPoint(2, 4);
        polygon.addPoint(1, 3);
        polygon.addPoint(1, 2);

        GeneralPath square = new GeneralPath();
        square.moveTo(0, 0);
        square.lineTo(1, 0);
        square.lineTo(1, 1);
        square.lineTo(0, 1);
        square.closePath();

        Ellipse2D hole = new Ellipse2D.Double(0, 0, 1, 1);
        Area area = new Area(square);
        area.subtract(new Area(hole));

        ClipKey key1 = new ClipKey(new GeneralPath(polygon));
        ClipKey key2 = new ClipKey(new GeneralPath(polygon));

        System.out.println("key1.equals(key2) = " + key1.equals(key2));

        int hash1 = key1.hashCode();
        int hash2 = key2.hashCode();

        System.out.println("hash1 = " + hash1);
        System.out.println("hash2 = " + hash2);

        Shape clips[] = {
            // polygon
            polygon,

            // rect
            new Rectangle(10, 20, 30, 40),
            new Rectangle2D.Double(100., 200., 300., 400.),
            new Rectangle2D.Float(1000f, 2000f, 3000f, 4000f),
            new RoundRectangle2D.Double(15., 16., 17., 18., 30., 20.),
            new RoundRectangle2D.Float(35f, 45f, 55f, 65f, 25f, 45f),

            // Circle
            new Ellipse2D.Float(0, 0, 100, 100),
            new Ellipse2D.Double(40, 40, 240, 240),

            // Ellipse
            new Ellipse2D.Float(0, 0, 100, 200),
            new Ellipse2D.Float(40, 100, 240, 200),

            // line
            new Line2D.Double(1, 2, 3, 4),
            new Line2D.Double(10, 20, 30, 40),

            // path
            new QuadCurve2D.Float(20, 30, 40, 50, 60, 70),
            new CubicCurve2D.Float(15, 25, 35, 45, 55, 65, 75, 85),
            new Arc2D.Double(0, 0, 100, 100, 0, 90, Arc2D.OPEN),
            square,
            area
        };

        Document domFactory = getDocumentPrototype();
        SVGClip converter = new SVGClip(getContext(domFactory));

        Element topLevelGroup = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        Element defs = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_DEFS_TAG);

        Element groupOne = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        for(int i=0; i<clips.length; i++){
            Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_RECT_TAG);
            rect.setAttributeNS(null, SVG_ID_ATTRIBUTE, clips[i].getClass().getName());
            rect.setAttributeNS(null, SVG_CLIP_PATH_ATTRIBUTE,
                                (String)converter.toSVG(clips[i]).
                                getAttributeMap(null).get(SVG_CLIP_PATH_ATTRIBUTE));
            groupOne.appendChild(rect);
        }

        // Elements in groupTwo should have the same clip reference as
        // corresponding elements in groupOne, as the clip definition
        // has already be done and put in clipDefsMap.
        Element groupTwo = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        for(int i=0; i<clips.length; i++){
            Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_RECT_TAG);
            rect.setAttributeNS(null, SVG_ID_ATTRIBUTE, clips[i].getClass().getName());
            rect.setAttributeNS(null, SVG_CLIP_PATH_ATTRIBUTE,
                                (String)converter.toSVG(clips[i]).
                                getAttributeMap(null).get(SVG_CLIP_PATH_ATTRIBUTE));
            groupTwo.appendChild(rect);
        }

        Iterator defValuesIter = converter.getDefinitionSet().iterator();
        while(defValuesIter.hasNext()){
            Element clipPathElement = (Element)defValuesIter.next();
            defs.appendChild(clipPathElement);
        }

        topLevelGroup.appendChild(defs);
        topLevelGroup.appendChild(groupOne);
        topLevelGroup.appendChild(groupTwo);

        trace(topLevelGroup, System.out);
    }

    public void testSVGColor() throws Exception {
        Color  testColors[] = {
            new Color(0x00, 0xff, 0xff), // aqua
            new Color(0x00, 0x00, 0x00), // black
            new Color(0x00, 0x00, 0xff), // blue
            new Color(0xff, 0x00, 0xff), // fuchsia
            new Color(0x80, 0x80, 0x80), // gray
            new Color(0x00, 0x80, 0x00), // green
            new Color(0x00, 0xff, 0x00), // lime
            new Color(0x80, 0x00, 0x00), // maroon
            new Color(0x00, 0x00, 0x80), // navy
            new Color(0x80, 0x80, 00),   // olive
            new Color(0x80, 0x00, 0x80), // purple
            new Color(0xff, 0x00, 0x00), // red
            new Color(0xc0, 0xc0, 0xc0), // silver
            new Color(0x00, 0x80, 0x80), // teal
            new Color(0xff, 0xff, 0xff), // white
            new Color(0xff, 0xff, 0x00), // yellow
            new Color(30, 40, 50),       // arbitrary 1
            new Color(255, 30, 200),     // arbitraty 2
            new Color(0, 0, 0, 128),     // arbitrary with alpha
            new Color(255, 255, 255, 64),// arbitrary with alpha
        };

        Document domFactory = getDocumentPrototype();
        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        for(int i=0; i<testColors.length; i++){
            SVGPaintDescriptor paintDesc = SVGColor.toSVG(testColors[i]);
            Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                      SVG_RECT_TAG);
            rect.setAttributeNS(null, SVG_FILL_ATTRIBUTE, paintDesc.getPaintValue());
            rect.setAttributeNS(null, SVG_FILL_OPACITY_ATTRIBUTE, paintDesc.getOpacityValue());
            group.appendChild(rect);
        }

        trace(group, System.out);
    }

    public void testSVGConvolveOp() throws Exception {
        Document domFactory = getDocumentPrototype();

        Kernel k = new Kernel(5, 3, new float[] { 1, 1, 1, 1, 1,
                                                  2, 2, 2, 2, 2,
                                                  3, 3, 3, 3, 3 });
        ConvolveOp convolveOps[] = { new ConvolveOp(k),
                                     new ConvolveOp(k, ConvolveOp.EDGE_NO_OP, null),
                                     new ConvolveOp(k, ConvolveOp.EDGE_ZERO_FILL, null) };


        SVGConvolveOp converter = new SVGConvolveOp(getContext(domFactory));

        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        Element defs = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_DEFS_TAG);
        Element rectGroupOne = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        Element rectGroupTwo = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);

        for(int i=0; i<convolveOps.length; i++){
            SVGFilterDescriptor filterDesc = converter.toSVG(convolveOps[i]);
            Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_RECT_TAG);
            rect.setAttributeNS(null, SVG_FILTER_ATTRIBUTE, filterDesc.getFilterValue());
            rectGroupOne.appendChild(rect);
        }

        for(int i=0; i<convolveOps.length; i++){
            SVGFilterDescriptor filterDesc = converter.toSVG(convolveOps[i]);
            Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_RECT_TAG);
            rect.setAttributeNS(null, SVG_FILTER_ATTRIBUTE, filterDesc.getFilterValue());
            rectGroupTwo.appendChild(rect);
        }

        Iterator iter = converter.getDefinitionSet().iterator();
        while(iter.hasNext()){
            Element feConvolveMatrixDef = (Element)iter.next();
            defs.appendChild(feConvolveMatrixDef);
        }

        group.appendChild(defs);
        group.appendChild(rectGroupOne);
        group.appendChild(rectGroupTwo);

        trace(group, System.out);
    }

    public void testSVGEllipse() throws Exception {
        Ellipse2D ellipses [] = { new Ellipse2D.Float(0, 0, 100, 100),
                                  new Ellipse2D.Double(40, 40, 240, 240),
                                  new Ellipse2D.Float(0, 0, 100, 200),
                                  new Ellipse2D.Float(40, 100, 240, 200) };

        Document domFactory = getDocumentPrototype();
        SVGEllipse converter = new SVGEllipse(getContext(domFactory));
        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        for(int i=0; i<ellipses.length; i++)
            group.appendChild(converter.toSVG(ellipses[i]));

        trace(group, System.out);
    }

    public void testSVGFont() throws Exception {
        Font fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        // traceFonts(fonts);

        Object customFontAttributes[][] = {
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRA_LIGHT},
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_LIGHT },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMILIGHT },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_SEMIBOLD },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_MEDIUM },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_DEMIBOLD },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_HEAVY },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_EXTRABOLD },
            { TextAttribute.WEIGHT, TextAttribute.WEIGHT_ULTRABOLD },
            { TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR },
            { TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE }
        };

        Map defaultAttrMap = new Hashtable();
        defaultAttrMap.put(TextAttribute.SIZE, new Float(45));
        defaultAttrMap.put(TextAttribute.FAMILY, "Serif");
        defaultAttrMap.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
        defaultAttrMap.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);

        Font customFonts[] = new Font[customFontAttributes.length];
        for(int i=0; i<customFonts.length; i++){
            Map fontAttrMap = new Hashtable(defaultAttrMap);
            fontAttrMap.put(customFontAttributes[i][0],
                            customFontAttributes[i][1]);

            customFonts[i] = new Font(fontAttrMap);
        }

        // traceFonts(customFonts);

        Font logicalFonts[] = { new Font("dialog", Font.PLAIN, 12),
                                new Font("dialoginput", Font.PLAIN, 12),
                                new Font("monospaced", Font.PLAIN, 12),
                                new Font("serif", Font.PLAIN, 12),
                                new Font("sansserif", Font.PLAIN, 12),
                                new Font("symbol", Font.PLAIN, 12) };

        traceFonts(logicalFonts);
    }

    private void traceFonts(Font fonts[])
        throws Exception {
        Document domFactory = getDocumentPrototype();
        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        SVGFont converter = new SVGFont(getContext(domFactory));
        GraphicContext gc = new GraphicContext(new AffineTransform());

        for(int i=0; i<fonts.length; i++){
            Font font = fonts[i];
            Map attrMap = converter.toSVG(font, gc.getFontRenderContext()).getAttributeMap(null);
            Element textElement = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_TEXT_TAG);
            Iterator iter = attrMap.keySet().iterator();
            while(iter.hasNext()){
                String attrName = (String)iter.next();
                String attrValue = (String)attrMap.get(attrName);
                textElement.setAttributeNS(null, attrName, attrValue);
            }
            textElement.setAttributeNS(null, SVG_FONT_SIZE_ATTRIBUTE, "30");
            textElement.setAttributeNS(null, SVG_X_ATTRIBUTE, "30");
            textElement.setAttributeNS(null, SVG_Y_ATTRIBUTE, "" + (40*(i+1)));
            textElement.appendChild(domFactory.createTextNode(font.getFamily()));
            group.appendChild(textElement);
        }

        SVGCSSStyler.style(group);
        trace(group, System.out);
    }

    public void testSVGGraphicContextConverter() throws Exception {
        Document domFactory = getDocumentPrototype();

        //
        // Create a GraphicContext and do the following:
        // a. Dump list of default SVG attributes
        // b. Modify the value of each of the context attributes
        // c. Dump new list of SVG attributes
        // d. Dump list of defs
        //

        GraphicContext gc = new GraphicContext(new AffineTransform());
        SVGGraphicContextConverter converter =
            new SVGGraphicContextConverter(getContext(domFactory));
        SVGGraphicContext defaultSVGGC = converter.toSVG(gc);
        traceSVGGC(defaultSVGGC, converter);

        // Transform
        gc.translate(40, 40);

        // Paint
        gc.setPaint(new GradientPaint(0, 0, Color.yellow, 200, 200, Color.red));

        // Stroke
        gc.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND,
                                     BasicStroke.JOIN_BEVEL, 40, new float[]{ 4, 5, 6, 7 }, 3));

        // Composite
        gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, .25f));

        // Clip
        gc.setClip(new Ellipse2D.Double(20, 30, 40, 50));

        // Hints
        gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Font
        gc.setFont(new Font("French Script MT", Font.BOLD, 45));

        SVGGraphicContext modifiedSVGGC = converter.toSVG(gc);

        traceSVGGC(modifiedSVGGC, converter);

        java.util.List defSet = converter.getDefinitionSet();
        Iterator iter = defSet.iterator();
        Element defs = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_DEFS_TAG);
        while(iter.hasNext()){
            Element def = (Element)iter.next();
            defs.appendChild(def);
        }

        trace(defs, System.out);
    }

    /**
     * For unit testing only
     */
    static void traceSVGGC(SVGGraphicContext svgGC, SVGGraphicContextConverter converter){
        System.out.println("=============================================");
        Map groupAttrMap = svgGC.getGroupContext();
        Iterator iter = groupAttrMap.keySet().iterator();
        while(iter.hasNext()){
            String attrName = (String)iter.next();
            String attrValue = (String)groupAttrMap.get(attrName);
            System.out.println(attrName + " = " + attrValue);
        }

        System.out.println("++++++++++++++++++");

        Map geAttrMap = svgGC.getGraphicElementContext();
        iter = geAttrMap.keySet().iterator();
        while(iter.hasNext()){
            String attrName = (String)iter.next();
            String attrValue = (String)geAttrMap.get(attrName);
            System.out.println(attrName + " = " + attrValue);
        }

        System.out.println("++++++++++++++++++");
        System.out.println("transform: " + converter.toSVG(svgGC.getTransformStack()));

        System.out.println("=============================================");
    }

    public void testSVGLine() throws Exception {
        Line2D lines [] = { new Line2D.Double(1, 2, 3, 4),
                            new Line2D.Double(10, 20, 30, 40) };

        Document domFactory = getDocumentPrototype();
        SVGLine converter = new SVGLine(getContext(domFactory));
        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI,
                                                   SVG_G_TAG);
        for(int i=0; i<lines.length; i++)
            group.appendChild(converter.toSVG(lines[i]));

        trace(group, System.out);
    }

    public void testSVGLinearGradient() throws Exception {
        Document domFactory = getDocumentPrototype();

        GradientPaint gradient = new GradientPaint(20, 20,
                                                   Color.black,
                                                   300, 300,
                                                   new Color(220, 230, 240),
                                                   true);

        SVGLinearGradient converter =
            new SVGLinearGradient(getContext(domFactory));

        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        Element defs = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_DEFS_TAG);

        SVGPaintDescriptor gradientDesc = converter.toSVG(gradient);

        Iterator iter = converter.getDefinitionSet().iterator();
        while(iter.hasNext()){
            Element linearGradientDef = (Element)iter.next();
            defs.appendChild(linearGradientDef);
        }

        Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_RECT_TAG);
        rect.setAttributeNS(null, SVG_FILL_ATTRIBUTE, gradientDesc.getPaintValue());
        rect.setAttributeNS(null, SVG_FILL_OPACITY_ATTRIBUTE, gradientDesc.getOpacityValue());

        group.appendChild(defs);
        group.appendChild(rect);

        trace(group, System.out);
    }

    public void testSVGLookupOp() throws Exception {
          Document domFactory = getDocumentPrototype();

        byte bs[] = new byte[256];
        short s[] = new short[256];
        byte bi[] = new byte[256];
        short si[] = new short[256];

        for(int i=0; i<=255; i++){
            bi[i] = (byte)(0xff & (255-i));
            bs[i] = (byte)(0xff & i);
            si[i] = (short)(0xffff & (255-i));
            s[i] = (short)(0xffff & i);
        }

        byte incompleteByteArray[] = new byte[128];
        short incompleteShortArray[] = new short[128];

        for(int i=0; i<128; i++){
            incompleteByteArray[i] = (byte)(255-i);
            incompleteShortArray[i] = (short)(255-i);
        }

        LookupTable tables[] = { new ByteLookupTable(0, bs),
                                 new ByteLookupTable(0, new byte[][]{bi, bs, bi}),
                                 new ByteLookupTable(0, new byte[][]{bs, bi, bs, bi}),
                                 new ByteLookupTable(128, incompleteByteArray),
                                 new ShortLookupTable(0, s),
                                 new ShortLookupTable(0, new short[][]{si, s, si}),
                                 new ShortLookupTable(0, new short[][]{s, si, s, si}),
                                 new ShortLookupTable(128, incompleteShortArray),
        };

        LookupOp lookupOps[] = new LookupOp[tables.length];
        for(int i=0; i<tables.length; i++)
            lookupOps[i] = new LookupOp(tables[i], null);

        SVGLookupOp converter = new SVGLookupOp(getContext(domFactory));

        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        Element defs = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_DEFS_TAG);
        Element rectGroupOne = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        Element rectGroupTwo = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);

        for(int i=0; i<lookupOps.length; i++){
            SVGFilterDescriptor filterDesc = converter.toSVG(lookupOps[i]);
            Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_RECT_TAG);
            rect.setAttributeNS(null, SVG_FILTER_ATTRIBUTE, filterDesc.getFilterValue());
            rectGroupOne.appendChild(rect);
        }

        for(int i=0; i<lookupOps.length; i++){
            SVGFilterDescriptor filterDesc = converter.toSVG(lookupOps[i]);
            Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_RECT_TAG);
            rect.setAttributeNS(null, SVG_FILTER_ATTRIBUTE, filterDesc.getFilterValue());
            rectGroupTwo.appendChild(rect);
        }

        Iterator iter = converter.getDefinitionSet().iterator();
        while(iter.hasNext()){
            Element feComponentTransferDef = (Element)iter.next();
            defs.appendChild(feComponentTransferDef);
        }

        group.appendChild(defs);
        group.appendChild(rectGroupOne);
        group.appendChild(rectGroupTwo);

        trace(group, System.out);
    }

    public void testSVGPath() throws Exception {
        GeneralPath generalPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        generalPath.moveTo(0, 0);
        Shape shapes[] = { generalPath,
                           new Rectangle2D.Float(20, 30, 40, 50),
                           new Ellipse2D.Float(25, 35, 80, 60),
                           new Line2D.Float(30, 40, 50, 60),
                           new QuadCurve2D.Float(20, 30, 40, 50, 60, 70),
                           new CubicCurve2D.Float(15, 25, 35, 45, 55, 65, 75, 85)
        };

        Document domFactory = getDocumentPrototype();
        SVGPath converter = new SVGPath(getContext(domFactory));

        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        for(int i=0; i < shapes.length; i++) {
            Shape shape = shapes[i];
            Element path = converter.toSVG(shape);
            group.appendChild(path);
        }

        trace(group, System.out);
    }

    public void testSVGPolygon() throws Exception {
        Polygon polygon = new Polygon();
        polygon.addPoint(350, 75);
        polygon.addPoint(379, 161);
        polygon.addPoint(469, 161);
        polygon.addPoint(397, 215);
        polygon.addPoint(423, 301);
        polygon.addPoint(350, 250);
        polygon.addPoint(277, 301);
        polygon.addPoint(303, 215);
        polygon.addPoint(231, 161);
        polygon.addPoint(321, 161);

        SVGPolygon converter = new SVGPolygon(getContext(getDocumentPrototype()));
        Element svgPolygon = converter.toSVG(polygon);
        trace(svgPolygon, System.out);
        System.out.println();
    }

    public void testSVGRectangle() throws Exception {
        Document domFactory = getDocumentPrototype();
        SVGRectangle converter = new SVGRectangle(getContext(domFactory));

        Element rects[] = {
            converter.toSVG(new Rectangle(10, 20, 30, 40)),
            converter.toSVG(new Rectangle2D.Double(100., 200., 300., 400.)),
            converter.toSVG(new Rectangle2D.Float(1000f, 2000f, 3000f, 4000f)),
            converter.toSVG(new RoundRectangle2D.Double(15., 16., 17., 18., 30., 20.)),
            converter.toSVG(new RoundRectangle2D.Float(35f, 45f, 55f, 65f, 25f, 45f))
        };

        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        for(int i=0; i<rects.length; i++)
            group.appendChild(rects[i]);

        trace(group, System.out);
    }

    public void testSVGRenderingHints() throws Exception {
        Document domFactory = getDocumentPrototype();
        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        Map testMap = new Hashtable();

        // Various RENDERING values
        RenderingHints rh = new RenderingHints(null);
        RenderingHints renderingValues[] = {
            new RenderingHints(rh.KEY_RENDERING, rh.VALUE_RENDER_DEFAULT),
            new RenderingHints(rh.KEY_RENDERING, rh.VALUE_RENDER_QUALITY),
            new RenderingHints(rh.KEY_RENDERING, rh.VALUE_RENDER_SPEED),
        };
        testMap.put("rendering", renderingValues);

        // Various FRACTIONAL_METRICS
        RenderingHints fractionalMetricsValues[] = {
            new RenderingHints(rh.KEY_FRACTIONALMETRICS, rh.VALUE_FRACTIONALMETRICS_DEFAULT),
            new RenderingHints(rh.KEY_FRACTIONALMETRICS, rh.VALUE_FRACTIONALMETRICS_ON),
            new RenderingHints(rh.KEY_FRACTIONALMETRICS, rh.VALUE_FRACTIONALMETRICS_OFF),
        };
        testMap.put("fractionalMetrics", fractionalMetricsValues);

        // Various ANTIALIASING
        RenderingHints antialiasingValues[] = {
            new RenderingHints(rh.KEY_ANTIALIASING, rh.VALUE_ANTIALIAS_DEFAULT),
            new RenderingHints(rh.KEY_ANTIALIASING, rh.VALUE_ANTIALIAS_ON),
            new RenderingHints(rh.KEY_ANTIALIASING, rh.VALUE_ANTIALIAS_OFF),
        };
        testMap.put("antialiasing", antialiasingValues);

        // Various COLOR_RENDERING
        RenderingHints colorRenderingValues[] = {
            new RenderingHints(rh.KEY_COLOR_RENDERING, rh.VALUE_COLOR_RENDER_DEFAULT),
            new RenderingHints(rh.KEY_COLOR_RENDERING, rh.VALUE_COLOR_RENDER_SPEED),
            new RenderingHints(rh.KEY_COLOR_RENDERING, rh.VALUE_COLOR_RENDER_QUALITY),
        };
        testMap.put("colorRendering", colorRenderingValues);

        // Various INTERPOLATION
        RenderingHints interpolationValues[] = {
            new RenderingHints(rh.KEY_INTERPOLATION, rh.VALUE_INTERPOLATION_NEAREST_NEIGHBOR),
            new RenderingHints(rh.KEY_INTERPOLATION, rh.VALUE_INTERPOLATION_BILINEAR),
            new RenderingHints(rh.KEY_INTERPOLATION, rh.VALUE_INTERPOLATION_BICUBIC),
        };
        testMap.put("interpolation", interpolationValues);

        // Various TEST_ANTIALIASING
        RenderingHints textAntialiasingValues[] = {
            new RenderingHints(rh.KEY_TEXT_ANTIALIASING, rh.VALUE_TEXT_ANTIALIAS_DEFAULT),
            new RenderingHints(rh.KEY_TEXT_ANTIALIASING, rh.VALUE_TEXT_ANTIALIAS_OFF),
            new RenderingHints(rh.KEY_TEXT_ANTIALIASING, rh.VALUE_TEXT_ANTIALIAS_ON),
        };
        testMap.put("textAntialiasing", textAntialiasingValues);

        // Mixed settings. The second hint to take precedence over the first one.
        RenderingHints mixedA = new RenderingHints(rh.KEY_RENDERING, rh.VALUE_RENDER_DEFAULT);
        mixedA.put(rh.KEY_ANTIALIASING, rh.VALUE_ANTIALIAS_ON);
        RenderingHints mixedB = new RenderingHints(rh.KEY_ANTIALIASING, rh.VALUE_ANTIALIAS_ON);
        mixedB.put(rh.KEY_TEXT_ANTIALIASING, rh.VALUE_TEXT_ANTIALIAS_OFF);
        RenderingHints mixedValues[] = { mixedA, mixedB };
        testMap.put("mixed", mixedValues);

        Iterator iter = testMap.keySet().iterator();
        SVGRenderingHints converter = new SVGRenderingHints();

        while(iter.hasNext()){
            String testName = (String)iter.next();
            RenderingHints hints[] = (RenderingHints[])testMap.get(testName);
            Element testGroup = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
            testGroup.setAttributeNS(null, SVG_ID_ATTRIBUTE, testName);
            for(int i=0; i<hints.length; i++){
                Element testRect = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_RECT_TAG);
                Map attrMap = converter.toSVG(hints[i]).getAttributeMap(null);
                Iterator attrIter = attrMap.keySet().iterator();
                while(attrIter.hasNext()){
                    String attrName = (String)attrIter.next();
                    testRect.setAttributeNS(null, attrName, (String)attrMap.get(attrName));
                }
                testGroup.appendChild(testRect);
            }
            group.appendChild(testGroup);
        }
        trace(group, System.out);
    }

    public void testSVGRescaleOp() throws Exception {
            Document domFactory = getDocumentPrototype();

        RescaleOp rescaleOps[] = { new RescaleOp(3, 25, null),
                                   new RescaleOp(new float[]{ 1, 2, 3 },
                                                 new float[]{10, 20, 30}, null),
                                   new RescaleOp(new float[]{ 1, 2, 3, 4 },
                                                 new float[]{10, 20, 30, 40}, null),
        };

        SVGRescaleOp converter = new SVGRescaleOp(getContext(domFactory));

        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        Element defs = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_DEFS_TAG);
        Element rectGroupOne = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        Element rectGroupTwo = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);

        for(int i=0; i<rescaleOps.length; i++){
            SVGFilterDescriptor filterDesc = converter.toSVG(rescaleOps[i]);
            Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_RECT_TAG);
            rect.setAttributeNS(null, SVG_FILTER_ATTRIBUTE, filterDesc.getFilterValue());
            rectGroupOne.appendChild(rect);
        }

        for(int i=0; i< rescaleOps.length; i++) {
            SVGFilterDescriptor filterDesc = converter.toSVG(rescaleOps[i]);
            Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_RECT_TAG);
            rect.setAttributeNS(null, SVG_FILTER_ATTRIBUTE, filterDesc.getFilterValue());
            rectGroupTwo.appendChild(rect);
        }

        Iterator iter = converter.getDefinitionSet().iterator();
        while(iter.hasNext()){
            Element feComponentTransferDef = (Element)iter.next();
            defs.appendChild(feComponentTransferDef);
        }

        group.appendChild(defs);
        group.appendChild(rectGroupOne);
        group.appendChild(rectGroupTwo);

        trace(group, System.out);
    }

    public void testSVGShape() throws Exception {
        Polygon polygon = new Polygon();
        polygon.addPoint(1, 1);
        polygon.addPoint(2, 1);
        polygon.addPoint(3, 2);
        polygon.addPoint(3, 3);
        polygon.addPoint(2, 4);
        polygon.addPoint(1, 3);
        polygon.addPoint(1, 2);

        GeneralPath square = new GeneralPath();
        square.moveTo(0, 0);
        square.lineTo(1, 0);
        square.lineTo(1, 1);
        square.lineTo(0, 1);
        square.closePath();

        Ellipse2D hole = new Ellipse2D.Double(0, 0, 1, 1);
        Area area = new Area(square);
        area.subtract(new Area(hole));

        Shape shapes[] = {
            // polygon
            polygon,

            // rect
            new Rectangle(10, 20, 30, 40),
            new Rectangle2D.Double(100., 200., 300., 400.),
            new Rectangle2D.Float(1000f, 2000f, 3000f, 4000f),
            new RoundRectangle2D.Double(15., 16., 17., 18., 30., 20.),
            new RoundRectangle2D.Float(35f, 45f, 55f, 65f, 25f, 45f),

            // Circle
            new Ellipse2D.Float(0, 0, 100, 100),
            new Ellipse2D.Double(40, 40, 240, 240),

            // Ellipse
            new Ellipse2D.Float(0, 0, 100, 200),
            new Ellipse2D.Float(40, 100, 240, 200),

            // line
            new Line2D.Double(1, 2, 3, 4),
            new Line2D.Double(10, 20, 30, 40),

            // path
            new QuadCurve2D.Float(20, 30, 40, 50, 60, 70),
            new CubicCurve2D.Float(15, 25, 35, 45, 55, 65, 75, 85),
            new Arc2D.Double(0, 0, 100, 100, 0, 90, Arc2D.OPEN),
            square,
            area
        };

        Document domFactory = getDocumentPrototype();
        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        SVGShape converter = new SVGShape(getContext(domFactory));

        for(int i=0; i<shapes.length; i++)
            group.appendChild(converter.toSVG(shapes[i]));

        trace(group, System.out);
    }

    public void testTexturePaint() throws Exception {
        Document domFactory = getDocumentPrototype();

        BufferedImage buf = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        TexturePaint paint = new TexturePaint(buf, new Rectangle(0, 0, 200, 200));

        SVGTexturePaint converter =
            new SVGTexturePaint(getContext(domFactory));
        Element group = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        Element defs = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_DEFS_TAG);

        SVGPaintDescriptor patternDesc = converter.toSVG(paint);
        Iterator iter = converter.getDefinitionSet().iterator();
        while(iter.hasNext()){
            Element patternDef = (Element)iter.next();
            defs.appendChild(patternDef);
        }

        Element rect = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_RECT_TAG);
        rect.setAttributeNS(null, SVG_FILL_ATTRIBUTE, patternDesc.getPaintValue());
        rect.setAttributeNS(null, SVG_FILL_OPACITY_ATTRIBUTE, patternDesc.getOpacityValue());

        group.appendChild(defs);
        group.appendChild(rect);

        trace(group, System.out);
    }

    public void testSVGTransform() throws Exception {
        GraphicContext gc = new GraphicContext();
        gc.translate(20, 20);
        gc.rotate(Math.PI/4);
        gc.shear(.5, .5);
        gc.scale(20, 20);

        AffineTransform txf = new AffineTransform();
        txf.translate(60, 60);
        gc.transform(txf);

        String svgTransform = SVGTransform.toSVGTransform(gc);
        System.out.println("SVG Transform: " + svgTransform);

        gc.setTransform(new AffineTransform());
        gc.translate(45, 45);

        svgTransform = SVGTransform.toSVGTransform(gc);
        System.out.println("SVG Transform: " + svgTransform);

        gc.setTransform(new AffineTransform());
        gc.translate(10, 10);
        gc.translate(30, 30);
        gc.scale(2, 3);
        gc.scale(3, 2);
        gc.rotate(Math.PI/2);
        gc.rotate(Math.PI/2);
        gc.translate(100, 100);
        gc.translate(-100, -100);
        gc.scale(2, 2);
        gc.scale(.5, .5);
        gc.rotate(Math.PI/2);
        gc.rotate(-Math.PI/2);

        svgTransform = SVGTransform.toSVGTransform(gc);
        System.out.println("SVG Transform: " + svgTransform);
    }

    public void testXmlWriter() throws Exception {
        Document domFactory = getDocumentPrototype();

        Element root = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_SVG_TAG);
        Element genericDefs = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_DEFS_TAG);
        Element topLevelGroup = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        Element topLevelDefs = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_DEFS_TAG);
        Element groupA = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        Element groupB = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
        Comment comment = domFactory.createComment("This is the topLevelGroup comment");

        topLevelGroup.appendChild(comment);
        topLevelGroup.appendChild(topLevelDefs);
        topLevelGroup.appendChild(groupA);
        topLevelGroup.appendChild(groupB);

        root.appendChild(genericDefs);
        root.appendChild(topLevelGroup);

        domFactory.appendChild(root);

        trace(root, System.out);

        System.out.println("\n=======================================");

        try {
            Writer out =  new OutputStreamWriter(System.out);
            XmlWriter.writeXml(domFactory, out);
            out.flush();
            out.close();
        } catch(Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}

