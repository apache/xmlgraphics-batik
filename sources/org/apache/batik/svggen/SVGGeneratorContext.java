/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import org.w3c.dom.Document;

/**
 *
 * @author <a href="mailto:cjolif@ilog.fr>Christophe Jolif</a>
 * @version $Id$
 */
final public class SVGGeneratorContext {
    /**
     * Error messages.
     */
    private static final String ERROR_DOM_FACTORY_NULL =
        "domFactory should not be null";
    private static final String ERROR_IMAGE_HANDLER_NULL =
        "imageHandler should not be null";
    private static final String ERROR_EXTENSION_HANDLER_NULL =
        "extensionHandler should not be null";
    private static final String ERROR_ID_GENERATOR_NULL =
        "idGenerator should not be null";

    // this fields are package access for read-only purposey

    /**
     * Factory used by this Graphics2D to create Elements
     * that make the SVG DOM Tree
     */
    Document domFactory;

    /**
     * Handler that defines how images are referenced in the
     * generated SVG fragment. This allows different strategies
     * to be used to handle images.
     * @see org.apache.batik.svggen.ImageHandler
     * @see org.apache.batik.svggen.ImageHandlerBase64Encoder
     * @see org.apache.batik.svggen.ImageHandlerPNGEncoder
     * @see org.apache.batik.svggen.ImageHandlerJPEGEncoder
     */
    ImageHandler imageHandler;

    /**
     * To deal with Java 2D extension (custom java.awt.Paint for example).
     */
    ExtensionHandler extensionHandler;

    /**
     * To generate consitent ids.
     */
    SVGIDGenerator idGenerator;

    protected SVGGeneratorContext(Document domFactory) {
        setDOMFactory(domFactory);
    }

    public static SVGGeneratorContext createDefault(Document domFactory) {
        SVGGeneratorContext ctx = new SVGGeneratorContext(domFactory);
        ctx.setIDGenerator(new SVGIDGenerator());
        ctx.setExtensionHandler(new DefaultExtensionHandler());
        ctx.setImageHandler(new ImageHandlerBase64Encoder(ctx));
        return ctx;
    }

    public SVGIDGenerator getIDGenerator() {
        return idGenerator;
    }

    protected void setIDGenerator(SVGIDGenerator idGenerator) {
        if (idGenerator == null)
            throw new IllegalArgumentException(ERROR_ID_GENERATOR_NULL);
        this.idGenerator = idGenerator;
    }

    public Document getDOMFactory() {
        return domFactory;
    }

    protected void setDOMFactory(Document domFactory) {
        if (domFactory == null)
            throw new IllegalArgumentException(ERROR_DOM_FACTORY_NULL);
        this.domFactory = domFactory;
    }

    public ExtensionHandler getExtensionHandler() {
        return extensionHandler;
    }

    protected void setExtensionHandler(ExtensionHandler extensionHandler) {
        if (extensionHandler == null)
            throw new IllegalArgumentException(ERROR_EXTENSION_HANDLER_NULL);
        this.extensionHandler = extensionHandler;
    }

    public ImageHandler getImageHandler() {
        return imageHandler;
    }

    protected void setImageHandler(ImageHandler imageHandler) {
        if (imageHandler == null)
            throw new IllegalArgumentException(ERROR_IMAGE_HANDLER_NULL);
        this.imageHandler = imageHandler;
    }
}
