/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder.svg2svg;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.transcoder.AbstractTranscoder;
import org.apache.batik.transcoder.ErrorHandler;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;

import org.apache.batik.xml.scanner.LexicalException;

import org.w3c.dom.Document;

/**
 * This class is a trancoder from SVG to SVG.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGTranscoder extends AbstractTranscoder {

    /**
     * The default error handler.
     */
    public final static ErrorHandler DEFAULT_ERROR_HANDLER = new ErrorHandler() {
        public void error(TranscoderException ex) throws TranscoderException {
            throw ex;
        }
        public void fatalError(TranscoderException ex) throws TranscoderException {
            throw ex;
        }
        public void warning(TranscoderException ex) throws TranscoderException {
            // Do nothing
        }
    };

    /**
     * The key to specify the newline character sequence.
     */
    public final static TranscodingHints.Key KEY_NEWLINE = new NewlineKey(0);

    /**
     * The "\r" newline value.
     */
    public final static NewlineValue VALUE_NEWLINE_CR = new NewlineValue("\r");

    /**
     * The "\r\n" newline value.
     */
    public final static NewlineValue VALUE_NEWLINE_CR_LF = new NewlineValue("\r\n");

    /**
     * The "\n" newline value.
     */
    public final static NewlineValue VALUE_NEWLINE_LF = new NewlineValue("\n");

    /**
     * The key to specify whether to format the input.
     */
    public final static TranscodingHints.Key KEY_FORMAT = new BooleanKey(0);

    /**
     * The value to turn on formatting.
     */
    public final static Boolean VALUE_FORMAT_ON = Boolean.TRUE;

    /**
     * The value to turn off formatting.
     */
    public final static Boolean VALUE_FORMAT_OFF = Boolean.FALSE;

    /**
     * The key to specify the tabulation width.
     */
    public final static TranscodingHints.Key KEY_TABULATION_WIDTH = new IntegerKey(0);

    /**
     * The key to specify the document width.
     */
    public final static TranscodingHints.Key KEY_DOCUMENT_WIDTH = new IntegerKey(1);

    /**
     * The key to specify the doctype option.
     */
    public final static TranscodingHints.Key KEY_DOCTYPE = new DoctypeKey(0);

    /**
     * The doctype value to change the declaration.
     */
    public final static DoctypeValue VALUE_DOCTYPE_CHANGE =
        new DoctypeValue(PrettyPrinter.DOCTYPE_CHANGE);

    /**
     * The doctype value to remove the declaration.
     */
    public final static DoctypeValue VALUE_DOCTYPE_REMOVE =
        new DoctypeValue(PrettyPrinter.DOCTYPE_REMOVE);

    /**
     * The doctype value to keep unchanged the declaration.
     */
    public final static DoctypeValue VALUE_DOCTYPE_KEEP_UNCHANGED =
        new DoctypeValue(PrettyPrinter.DOCTYPE_KEEP_UNCHANGED);

    /**
     * The key to specify the public id.
     */
    public final static TranscodingHints.Key KEY_PUBLIC_ID = new StringKey(0);

    /**
     * The key to specify the system id.
     */
    public final static TranscodingHints.Key KEY_SYSTEM_ID = new StringKey(1);

    /**
     * Creates a new SVGTranscoder.
     */
    public SVGTranscoder() {
        setErrorHandler(DEFAULT_ERROR_HANDLER);
    }

    /**
     * Transcodes the specified input in the specified output.
     * @param input the input to transcode
     * @param output the ouput where to transcode
     * @exception TranscoderException if an error occured while transcoding
     */
    public void transcode(TranscoderInput input, TranscoderOutput output)
        throws TranscoderException {
        Reader r = input.getReader();
        Writer w = output.getWriter();

        if (r == null) {
            Document d = input.getDocument();
            if (d == null) {
                throw new Error("Reader or Document expected");
            }
            StringWriter sw = new StringWriter();
            try {
                DOMUtilities.writeDocument(d, sw);
            } catch (IOException e) {
                throw new Error("IO");
            }
            r = new StringReader(sw.toString());
        }
        if (w == null) {
            throw new Error("Writer expected");
        }
        prettyPrint(r, w);
    }


    /**
     * Pretty print the given reader.
     */
    protected void prettyPrint(Reader in, Writer out) throws TranscoderException {
        try {
            PrettyPrinter pp = new PrettyPrinter();
            NewlineValue nlv = (NewlineValue)hints.get(KEY_NEWLINE);
            if (nlv != null) {
                pp.setNewline(nlv.getValue());
            }
            Boolean b = (Boolean)hints.get(KEY_FORMAT);
            if (b != null) {
                pp.setFormat(b.booleanValue());
            }
            Integer i = (Integer)hints.get(KEY_TABULATION_WIDTH);
            if (i != null) {
                pp.setTabulationWidth(i.intValue());
            }
            i = (Integer)hints.get(KEY_DOCUMENT_WIDTH);
            if (i != null) {
                pp.setDocumentWidth(i.intValue());
            }
            DoctypeValue dtv = (DoctypeValue)hints.get(KEY_DOCTYPE);
            if (dtv != null) {
                pp.setDoctypeOption(dtv.getValue());
            }
            String s = (String)hints.get(KEY_PUBLIC_ID);
            if (s != null) {
                pp.setPublicId(s);
            }
            s = (String)hints.get(KEY_SYSTEM_ID);
            if (s != null) {
                pp.setSystemId(s);
            }

            pp.print(in, out);
            out.flush();
        } catch (IOException e) {
            getErrorHandler().fatalError(new TranscoderException(e.getMessage()));
        }
    }

    /**
     * To represent a newline key.
     */
    protected static class NewlineKey extends TranscodingHints.Key {
        public NewlineKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            return v instanceof NewlineValue;
        }
    }

    /**
     * To represent a newline value.
     */
    protected static class NewlineValue {
        protected String value;
        public NewlineValue(String val) {
            value = val;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * To represent a doctype key.
     */
    protected static class DoctypeKey extends TranscodingHints.Key {
        public DoctypeKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            return v instanceof DoctypeValue;
        }
    }

    /**
     * To represent a doctype value.
     */
    protected static class DoctypeValue {
        int value;
        public DoctypeValue(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    /**
     * To represent a boolean key.
     */
    protected static class BooleanKey extends TranscodingHints.Key {
        public BooleanKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            return v instanceof Boolean;
        }
    }

    /**
     * To represent a integer key.
     */
    protected static class IntegerKey extends TranscodingHints.Key {
        public IntegerKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            return v instanceof Integer;
        }
    }

    /**
     * To represent a string key.
     */
    protected static class StringKey extends TranscodingHints.Key {
        public StringKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            return v instanceof String;
        }
    }

}
