/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.svggen;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;

import org.w3c.dom.Element;

/**
 * This class provides a default implementation of the ImageHandler
 * interface simply puts a place holder in the xlink:href
 * attribute and sets the width and height of the element.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 * @see             org.apache.batik.svggen.SVGGraphics2D
 */
public class DefaultImageHandler implements ImageHandler, ErrorConstants {
    // duplicate the string here to remove dependencies on
    // org.apache.batik.dom.util.XLinkSupport
    static final String XLINK_NAMESPACE_URI =
        "http://www.w3.org/1999/xlink";

    /**
     * Build a <code>DefaultImageHandler</code>.
     */
    public DefaultImageHandler() {
    }

    /**
     * The handler should set the xlink:href tag and the width and
     * height attributes.
     */
    public void handleImage(Image image, Element imageElement,
                            SVGGeneratorContext generatorContext) {
        //
        // First, set the image width and height
        //
        imageElement.setAttributeNS(null, SVG_WIDTH_ATTRIBUTE,
                                    "" + image.getWidth(null));
        imageElement.setAttributeNS(null, SVG_HEIGHT_ATTRIBUTE,
                                    "" + image.getHeight(null));

        //
        // Now, set the href
        //
        try {
            handleHREF(image, imageElement, generatorContext);
        } catch (SVGGraphics2DIOException e) {
            try {
                generatorContext.errorHandler.handleError(e);
            } catch (SVGGraphics2DIOException io) {
                // we need a runtime exception because
                // java.awt.Graphics2D method doesn't throw exceptions..
                throw new SVGGraphics2DRuntimeException(io);
            }
        }
    }

    /**
     * The handler should set the xlink:href tag and the width and
     * height attributes.
     */
    public void handleImage(RenderedImage image, Element imageElement,
                            SVGGeneratorContext generatorContext) {
        //
        // First, set the image width and height
        //
        imageElement.setAttributeNS(null, SVG_WIDTH_ATTRIBUTE,
                                    "" + image.getWidth());
        imageElement.setAttributeNS(null, SVG_HEIGHT_ATTRIBUTE,
                                    "" + image.getHeight());

        //
        // Now, set the href
        //
        try {
            handleHREF(image, imageElement, generatorContext);
        } catch (SVGGraphics2DIOException e) {
            try {
                generatorContext.errorHandler.handleError(e);
            } catch (SVGGraphics2DIOException io) {
                // we need a runtime exception because
                // java.awt.Graphics2D method doesn't throw exceptions..
                throw new SVGGraphics2DRuntimeException(io);
            }
        }
    }

    /**
     * The handler should set the xlink:href tag and the width and
     * height attributes.
     */
    public void handleImage(RenderableImage image, Element imageElement,
                            SVGGeneratorContext generatorContext) {
        //
        // First, set the image width and height
        //
        imageElement.setAttributeNS(null, SVG_WIDTH_ATTRIBUTE,
                                    "" + image.getWidth());
        imageElement.setAttributeNS(null, SVG_HEIGHT_ATTRIBUTE,
                                    "" + image.getHeight());

        //
        // Now, set the href
        //
        try {
            handleHREF(image, imageElement, generatorContext);
        } catch (SVGGraphics2DIOException e) {
            try {
                generatorContext.errorHandler.handleError(e);
            } catch (SVGGraphics2DIOException io) {
                // we need a runtime exception because
                // java.awt.Graphics2D method doesn't throw exceptions..
                throw new SVGGraphics2DRuntimeException(io);
            }
        }
    }

    /**
     * This template method should set the xlink:href attribute on the input
     * Element parameter
     */
    protected void handleHREF(Image image, Element imageElement,
                              SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {
        // Simply write a placeholder
        imageElement.setAttributeNS(XLINK_NAMESPACE_URI,
                                    ATTR_XLINK_HREF, image.toString());
    }

    /**
     * This template method should set the xlink:href attribute on the input
     * Element parameter
     */
    protected void handleHREF(RenderedImage image, Element imageElement,
                              SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {
        // Simply write a placeholder
        imageElement.setAttributeNS(XLINK_NAMESPACE_URI,
                                    ATTR_XLINK_HREF, image.toString());
    }

    /**
     * This template method should set the xlink:href attribute on the input
     * Element parameter
     */
    protected void handleHREF(RenderableImage image, Element imageElement,
                              SVGGeneratorContext generatorContext)
        throws SVGGraphics2DIOException {
        // Simply write a placeholder
        imageElement.setAttributeNS(XLINK_NAMESPACE_URI,
                                    ATTR_XLINK_HREF, image.toString());
    }
}

