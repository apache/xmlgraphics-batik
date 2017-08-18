/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.transcoder.image;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.gvt.renderer.ConcreteImageRendererFactory;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.gvt.renderer.ImageRendererFactory;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.keys.BooleanKey;
import org.apache.batik.transcoder.keys.PaintKey;
import org.w3c.dom.Document;

/**
 * This class enables to transcode an input to an image of any format.
 *
 * <p>Two transcoding hints (<code>KEY_WIDTH</code> and
 * <code>KEY_HEIGHT</code>) can be used to respectively specify the image
 * width and the image height. If only one of these keys is specified,
 * the transcoder preserves the aspect ratio of the original image.
 *
 * <p>The <code>KEY_BACKGROUND_COLOR</code> defines the background color
 * to use for opaque image formats, or the background color that may
 * be used for image formats that support alpha channel.
 *
 * <p>The <code>KEY_AOI</code> represents the area of interest to paint
 * in device space.
 *
 * <p>Three additional transcoding hints that act on the SVG
 * processor can be specified:
 *
 * <p><code>KEY_LANGUAGE</code> to set the default language to use (may be
 * used by a &lt;switch> SVG element for example),
 * <code>KEY_USER_STYLESHEET_URI</code> to fix the URI of a user
 * stylesheet, and <code>KEY_MM_PER_PIXEL</code> to specify the number of
 * millimeters in each pixel .
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class ImageTranscoder extends SVGAbstractTranscoder {

    /**
     * Constructs a new <code>ImageTranscoder</code>.
     */
    protected ImageTranscoder() {
    }

    /**
     * Transcodes the specified Document as an image in the specified output.
     *
     * @param document the document to transcode
     * @param uri the uri of the document or null if any
     * @param output the ouput where to transcode
     * @exception TranscoderException if an error occured while transcoding
     */
    protected void transcode(Document document,
                             String uri,
                             TranscoderOutput output)
            throws TranscoderException {

        // Sets up root, curTxf & curAoi
        super.transcode(document, uri, output);

        // prepare the image to be painted
        int w = (int)(width+0.5);
        int h = (int)(height+0.5);

        // paint the SVG document using the bridge package
        // create the appropriate renderer
        ImageRenderer renderer = createRenderer();
        renderer.updateOffScreen(w, h);
        // curTxf.translate(0.5, 0.5);
        renderer.setTransform(curTxf);
        renderer.setTree(this.root);
        this.root = null; // We're done with it...

        try {
            // now we are sure that the aoi is the image size
            Shape raoi = new Rectangle2D.Float(0, 0, width, height);
            // Warning: the renderer's AOI must be in user space
            renderer.repaint(curTxf.createInverse().
                             createTransformedShape(raoi));
            BufferedImage rend = renderer.getOffScreen();
            renderer = null; // We're done with it...

            BufferedImage dest = createImage(w, h);

            Graphics2D g2d = GraphicsUtil.createGraphics(dest);
            if (hints.containsKey(KEY_BACKGROUND_COLOR)) {
                Paint bgcolor = (Paint)hints.get(KEY_BACKGROUND_COLOR);
                g2d.setComposite(AlphaComposite.SrcOver);
                g2d.setPaint(bgcolor);
                g2d.fillRect(0, 0, w, h);
            }
            if (rend != null) { // might be null if the svg document is empty
                g2d.drawRenderedImage(rend, new AffineTransform());
            }
            g2d.dispose();
            rend = null; // We're done with it...
            writeImage(dest, output);
        } catch (Exception ex) {
            throw new TranscoderException(ex);
        }
    }

    /**
     * Method so subclasses can modify the Renderer used to render document.
     */
    protected ImageRenderer createRenderer() {
        ImageRendererFactory rendFactory = new ConcreteImageRendererFactory();
        // ImageRenderer renderer = rendFactory.createDynamicImageRenderer();
        return rendFactory.createStaticImageRenderer();
    }

    /**
     * Converts an image so that viewers which do not support the
     * alpha channel will see a white background (and not a black
     * one).
     * @param img the image to convert
     * @param sppsm
     */
    protected void forceTransparentWhite(BufferedImage img, SinglePixelPackedSampleModel sppsm) {
        //
        // This is a trick so that viewers which do not support
        // the alpha channel will see a white background (and not
        // a black one).
        //
        int w = img.getWidth();
        int h = img.getHeight();
        DataBufferInt biDB=(DataBufferInt)img.getRaster().getDataBuffer();
        int scanStride = sppsm.getScanlineStride();
        int dbOffset = biDB.getOffset();
        int[] pixels = biDB.getBankData()[0];
        int p = dbOffset;
        int adjust = scanStride - w;
        int a=0, r=0, g=0, b=0, pel=0;
        for(int i=0; i<h; i++){
            for(int j=0; j<w; j++){
                pel = pixels[p];
                a = (pel >> 24) & 0xff;
                r = (pel >> 16) & 0xff;
                g = (pel >> 8 ) & 0xff;
                b =  pel        & 0xff;
                r = (255*(255 -a) + a*r)/255;
                g = (255*(255 -a) + a*g)/255;
                b = (255*(255 -a) + a*b)/255;
                pixels[p++] =
                    (a<<24 & 0xff000000) |
                    (r<<16 & 0xff0000) |
                    (g<<8  & 0xff00) |
                    (b     & 0xff);
            }
            p += adjust;
        }
    }

    /**
     * Creates a new image with the specified dimension.
     * @param width the image width in pixels
     * @param height the image height in pixels
     */
    public abstract BufferedImage createImage(int width, int height);

    /**
     * Writes the specified image to the specified output.
     * @param img the image to write
     * @param output the output where to store the image
     * @throws TranscoderException if an error occured while storing the image
     */
    public abstract void writeImage(BufferedImage img, TranscoderOutput output)
        throws TranscoderException;

    // --------------------------------------------------------------------
    // Keys definition
    // --------------------------------------------------------------------

    /**
     * The image background paint key.
     * <table summary="" border="0" cellspacing="0" cellpadding="1">
     *   <tr>
     *     <th valign="top" align="right">Key:</th>
     *     <td valign="top">KEY_BACKGROUND_COLOR</td>
     *   </tr>
     *   <tr>
     *     <th valign="top" align="right">Value:</th>
     *     <td valign="top">Paint</td>
     *   </tr>
     *   <tr>
     *     <th valign="top" align="right">Default:</th>
     *     <td valign="top">null</td>
     *   </tr>
     *   <tr>
     *     <th valign="top" align="right">Required:</th>
     *     <td valign="top">No</td>
     *   </tr>
     *   <tr>
     *     <th valign="top" align="right">Description:</th>
     *     <td valign="top">Specify the background color to use.
     *       The color is required by opaque image formats and is used by
     *       image formats that support alpha channel.</td>
     *   </tr>
     * </table>
     */
    public static final TranscodingHints.Key KEY_BACKGROUND_COLOR
        = new PaintKey();

    /**
     * The forceTransparentWhite key.
     *
     * <table summary="" border="0" cellspacing="0" cellpadding="1">
     *   <tr>
     *     <th valign="top" align="right">Key:</th>
     *     <td valign="top">KEY_FORCE_TRANSPARENT_WHITE</td>
     *   </tr>
     *   <tr>
     *     <th valign="top" align="right">Value:</th>
     *     <td valign="top">Boolean</td>
     *   </tr>
     *   <tr>
     *     <th valign="top" align="right">Default:</th>
     *     <td valign="top">false</td>
     *   </tr>
     *   <tr>
     *     <th valign="top" align="right">Required:</th>
     *     <td valign="top">No</td>
     *   </tr>
     *   <tr>
     *     <th valign="top" align="right">Description:</th>
     *     <td valign="top">It controls whether the encoder should force
     *       the image's fully transparent pixels to be fully transparent
     *       white instead of fully transparent black.  This is useful when
     *       the encoded file is displayed in a browser which does not
     *       support transparency correctly and lets the image display with
     *       a white background instead of a black background.
     *       <br>
     *       However, note that the modified image will display differently
     *       over a white background in a viewer that supports
     *       transparency.
     *       <br>
     *       Not all Transcoders use this key (in particular some formats
     *       can't preserve the alpha channel at all in which case this
     *       is not used).</td>
     *   </tr>
     * </table>
     */
    public static final TranscodingHints.Key KEY_FORCE_TRANSPARENT_WHITE
        = new BooleanKey();
}
