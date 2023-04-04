package org.apache.batik.bridge;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

import javax.xml.XMLConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

/**
 * This class wraps just an SVG node. That way it is possible to
 * pass a vector graphic via Graphics2D interface, although the Graphics2D
 * device has to handle it explicitly. This is currently used to pass
 * graphics from icePDF to XepGraphics2D. That way the two libs do not have
 * direct dependencies to each other. This was a fix, because icePDF
 * created a buffered image and passed it to the graphics device, which
 * resulted in low resolution graphics for PDF-IN
 */
public class SVGBufferedImage extends BufferedImage {

    private Element svgNode = null;

    private int width = 1;

    private int height = 1;

    public SVGBufferedImage(int i, int i1, int i2) {
        super(i, i1, i2);
    }

    public SVGBufferedImage(int i, int i1, int i2, IndexColorModel indexColorModel) {
        super(i, i1, i2, indexColorModel);
    }

    public SVGBufferedImage(ColorModel colorModel, WritableRaster writableRaster, boolean b,
                            Hashtable<?, ?> hashTable) {
        super(colorModel, writableRaster, b, hashTable);
    }

    public SVGBufferedImage(Element svgNode, int width, int height) {
        // no size and custom type, because this is just a wrapper so we can pass the image to
        // XepGraphics2D without changing the interface
        // Some images missing for TYPE_INT_RGB. For ticket CLASSIC-3806.
        // super(1, 1, BufferedImage.TYPE_INT_RGB);
        super(1, 1, BufferedImage.TYPE_INT_ARGB);
        this.svgNode = svgNode;
        this.width = width;
        this.height = height;
    }

    public Element getSvgNode() {
        return svgNode;
    }

    /**
     * returns the SVG as byte array. The resulting xml
     * omits the xml declaration, so it can be used directly in an existing
     * XML Stream
     * @return the SVG-XML as byte array
     * @throws Exception if the transformation from the XML Node to StreamResult fails.
     */
    public byte[] getSVGAsByteArray() throws Exception {
        Source source = new DOMSource(svgNode);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Result result = new StreamResult(out);
        TransformerFactory factory = getDefaultTransformerFactory();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty("omit-xml-declaration", "yes");
        transformer.transform(source, result);
        return out.toByteArray();
    }

    public static TransformerFactory getDefaultTransformerFactory() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        // For saxon or xalan transformer factory it is impossible to set external DTD, doctype declaration
        // and stylesheet properties
        if ("net.sf.saxon.TransformerFactoryImpl".equals(transformerFactory.getClass().getCanonicalName())) {
            return transformerFactory;
        }
        try {
            transformerFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (TransformerConfigurationException e) {
            System.err.println("could not apply security feature to the transformer factory" + e.getMessage());
        }
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        return transformerFactory;
    }

    public int getWidth() {
        return width;
    }

    public int getWidth(ImageObserver observer) {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getHeight(ImageObserver observer) {
        return height;
    }
}