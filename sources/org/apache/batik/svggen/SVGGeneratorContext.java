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
 * This class contains all non graphical contextual information that
 * are needed by the {@link org.apache.batik.svggen.SVGGraphics2D} to
 * generate SVG from Java 2D primitives.
 * You can subclass it to change the defaults.
 *
 * @see org.apache.batik.svggen.SVGGraphics2D#SVGGraphics2D(SVGGeneratorContext,boolean)
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

    // this fields are package access for read-only purpose

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

    /**
     * Builds an instance of <code>SVGGeneratorContext</code> with the given
     * <code>domFactory</code> but let the user set later the other contextual
     * information.
     * @see #setIDGenerator
     * @see #setExtensionHandler
     * @see #setImageHandler
     */
    protected SVGGeneratorContext(Document domFactory) {
        setDOMFactory(domFactory);
    }

    /**
     * Creates an instance of <code>SVGGeneratorContext</code> with the
     * given <code>domFactory</code> and with the default values for the
     * other information.
     * @see org.apache.batik.svggen.SVGIDGenerator
     * @see org.apache.batik.svggen.DefaultExtensionHandler
     * @see org.apache.batik.svggen.ImageHandlerBase64Encoder
     */
    public static SVGGeneratorContext createDefault(Document domFactory) {
        SVGGeneratorContext ctx = new SVGGeneratorContext(domFactory);
        ctx.setIDGenerator(new SVGIDGenerator());
        ctx.setExtensionHandler(new DefaultExtensionHandler());
        ctx.setImageHandler(new ImageHandlerBase64Encoder(ctx));
        return ctx;
    }

    /**
     * Returns the {@link org.apache.batik.svggen.SVGIDGenerator} that
     * has been set.
     */
    public SVGIDGenerator getIDGenerator() {
        return idGenerator;
    }

    /**
     * Sets the {@link org.apache.batik.svggen.SVGIDGenerator}
     * to be used. It should not be <code>null</code>.
     */
    protected void setIDGenerator(SVGIDGenerator idGenerator) {
        if (idGenerator == null)
            throw new IllegalArgumentException(ERROR_ID_GENERATOR_NULL);
        this.idGenerator = idGenerator;
    }

    /**
     * Returns the DOM Factory that
     * has been set.
     */
    public Document getDOMFactory() {
        return domFactory;
    }

    /**
     * Sets the DOM Factory
     * to be used. It should not be <code>null</code>.
     */
    protected void setDOMFactory(Document domFactory) {
        if (domFactory == null)
            throw new IllegalArgumentException(ERROR_DOM_FACTORY_NULL);
        this.domFactory = domFactory;
    }

    /**
     * Returns the {@link org.apache.batik.svggen.ExtensionHandler} that
     * has been set.
     */
    public ExtensionHandler getExtensionHandler() {
        return extensionHandler;
    }

    /**
     * Sets the {@link org.apache.batik.svggen.ExtensionHandler}
     * to be used. It should not be <code>null</code>.
     */
    protected void setExtensionHandler(ExtensionHandler extensionHandler) {
        if (extensionHandler == null)
            throw new IllegalArgumentException(ERROR_EXTENSION_HANDLER_NULL);
        this.extensionHandler = extensionHandler;
    }

    /**
     * Returns the {@link org.apache.batik.svggen.ImageHandler} that
     * has been set.
     */
    public ImageHandler getImageHandler() {
        return imageHandler;
    }

    /**
     * Sets the {@link org.apache.batik.svggen.ImageHandler}
     * to be used. It should not be <code>null</code>.
     */
    protected void setImageHandler(ImageHandler imageHandler) {
        if (imageHandler == null)
            throw new IllegalArgumentException(ERROR_IMAGE_HANDLER_NULL);
        this.imageHandler = imageHandler;
    }
}
