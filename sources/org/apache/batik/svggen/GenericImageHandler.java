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
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;

import org.w3c.dom.Element;

/**
 * Extends the default ImageHandler interface with calls to
 * allow caching of raster images in generated SVG content.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface GenericImageHandler {
    /**
     * Sets the DomTreeManager this image handler may need to 
     * interact with.
     */
    public void setDOMTreeManager(DOMTreeManager domTreeManager);

    /**
     * Creates an Element suitable for referring to images.
     * Note that no assumptions can be made about the name of this Element.
     */
    public Element createElement(SVGGeneratorContext generatorContext);

    /**
     * The handler should set the xlink:href and return a transform
     *
     * @param image             the image under consideration
     * @param imageElement      the DOM Element for this image
     * @param x                 x coordinate
     * @param y                 y coordinate
     * @param width             width for rendering
     * @param height            height for rendering
     * @param generatorContext  the SVGGeneratorContext
     *
     * @return transform converting the image dimension to rendered dimension
     */
    public AffineTransform handleImage(Image image, Element imageElement,
                                       int x, int y,
                                       int width, int height,
                                       SVGGeneratorContext generatorContext);

    /**
     * The handler should set the xlink:href tag and return a transform
     *
     * @param image             the image under consideration
     * @param imageElement      the DOM Element for this image
     * @param x                 x coordinate
     * @param y                 y coordinate
     * @param width             width for rendering
     * @param height            height for rendering
     * @param generatorContext  the SVGGeneratorContext
     *
     * @return transform converting the image dimension to rendered dimension
     */
    public AffineTransform handleImage(RenderedImage image, Element imageElement,
                                       int x, int y,
                                       int width, int height,
                                       SVGGeneratorContext generatorContext);

    /**
     * The handler should set the xlink:href tag and return a transform
     *
     * @param image             the image under consideration
     * @param imageElement      the DOM Element for this image
     * @param x                 x coordinate
     * @param y                 y coordinate
     * @param width             width for rendering
     * @param height            height for rendering
     * @param generatorContext  the SVGGeneratorContext
     *
     * @return transform converting the image dimension to rendered dimension
     */
    public AffineTransform handleImage(RenderableImage image, Element imageElement,
                                       double x, double y,
                                       double width, double height,
                                       SVGGeneratorContext generatorContext);

}
