/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.transcoder;

import java.awt.Color;
import java.awt.Paint;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.Viewport;
import org.apache.batik.css.CSSDocumentHandler;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDocumentFactory;
import org.apache.batik.refimpl.bridge.ConcreteGVTBuilder;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.ErrorHandler;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscodingHints;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A generic <tt>Transcoder</tt>.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class AbstractTranscoder implements Transcoder {

    static {
        CSSDocumentHandler.setParserClassName("org.w3c.flute.parser.Parser");
    }

    /** The transcoding hints. */
    protected TranscodingHints hints = new TranscodingHints();

    public AbstractTranscoder() {
        hints.put(BatikHints.KEY_XML_PARSER_CLASSNAME,
                  "org.apache.crimson.parser.XMLReaderImpl");
        hints.put(BatikHints.KEY_GVT_BUILDER,
                  new org.apache.batik.refimpl.bridge.ConcreteGVTBuilder());
        hints.put(BatikHints.KEY_DEFAULT_VIEWPORT,
                  new Viewport() {
            public float getWidth() { return 640f; }
            public float getHeight() { return 480f; }
        });
    }

    public void transcode(TranscoderInput input, TranscoderOutput output)
            throws TranscoderException {
        throw new Error();
    }

    /**
     * Creates a <tt>Document</tt> using the specified isource and
     * call the <tt>transcodeToStream(Document, OutputStream).
     */
    public void transcodeToStream(InputSource isource, OutputStream ostream)
            throws TranscoderException {
        SVGDocumentFactory f = new SAXSVGDocumentFactory(getParserClassName());
        try {
            transcodeToStream(f.createDocument(isource.getSystemId()), ostream);
        } catch (DOMException ex) {
            throw new TranscoderException(ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new TranscoderException(ex.getMessage(), ex);
        }
    }

    public abstract void transcodeToStream(Document document, OutputStream ostream)
            throws TranscoderException;

    public TranscodingHints getTranscodingHints() {
        return new TranscodingHints(hints);
    }

    public void addTranscodingHint(TranscodingHints.Key key, Object value) {
        hints.put(key, value);
    }

    public void setTranscodingHints(Map hints) {
        hints.putAll(hints);
    }

    /**
     * Sets the error handler this transcoder may use to report
     * warnings and errors.
     * @param handler to ErrorHandler to use
     */
    public void setErrorHandler(ErrorHandler handler) {
        throw new Error();
    }

    /**
     * Returns the error handler this transcoder uses to report
     * warnings and errors, or null if any.
     */
    public ErrorHandler getErrorHandler() {
        throw new Error();
    }

    /**
     * Returns the GVTBuilder to use.
     */
    protected GVTBuilder getGVTBuilder() {
        return (GVTBuilder) hints.get(BatikHints.KEY_GVT_BUILDER);
    }

    /**
     * Returns the default Viewport to use.
     */
    protected Viewport getDefaultViewport() {
        return (Viewport) hints.get(BatikHints.KEY_DEFAULT_VIEWPORT);
    }

    /**
     * Returns the Paint used to fill the background.
     */
    protected Paint getBackgroundPaint() {
        return (Paint) hints.get(BatikHints.KEY_BACKGROUND);
    }

    /**
     * Returns the classname of the XML parser to use.
     */
    protected String getParserClassName() {
        return (String) hints.get(BatikHints.KEY_XML_PARSER_CLASSNAME);
    }
}
