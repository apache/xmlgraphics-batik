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

package org.apache.batik.apps.svgpp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.batik.i18n.LocalizableSupport;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;

/**
 * This class is the main class of the svgpp application.
 * <p>
 * svgpp is a pretty-printer for SVG source files.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class Main {

    /**
     * The application main method.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        new Main(args).run();
    }

    /**
     * The default resource bundle base name.
     */
    public final static String BUNDLE_CLASSNAME =
	"org.apache.batik.apps.svgpp.resources.Messages";

    /**
     * The localizable support.
     */
    protected static LocalizableSupport localizableSupport =
        new LocalizableSupport(BUNDLE_CLASSNAME, Main.class.getClassLoader());

    /**
     * The arguments.
     */
    protected String[] arguments;

    /**
     * The current index.
     */
    protected int index;

    /**
     * The option handlers.
     */
    protected Map handlers = new HashMap();
    {
        handlers.put("-doctype", new DoctypeHandler());
        handlers.put("-doc-width", new DocWidthHandler());
        handlers.put("-newline", new NewlineHandler());
        handlers.put("-public-id", new PublicIdHandler());
        handlers.put("-no-format", new NoFormatHandler());
        handlers.put("-system-id", new SystemIdHandler());
        handlers.put("-tab-width", new TabWidthHandler());
        handlers.put("-xml-decl", new XMLDeclHandler());
    }

    /**
     * The transcoder.
     */
    protected Transcoder transcoder = new SVGTranscoder();

    /**
     * Initializes the application.
     * @param args The command-line arguments.
     */
    public Main(String[] args) {
        arguments = args;
    }

    /**
     * Runs the pretty printer.
     */
    public void run() {
        if (arguments.length == 0) {
            printUsage();
            return;
        }
        try {
            for (;;) {
                OptionHandler oh = (OptionHandler)handlers.get(arguments[index]);
                if (oh == null) {
                    break;
                }
                oh.handleOption();
            }
            TranscoderInput in;
            in = new TranscoderInput(new java.io.FileReader(arguments[index++]));
            TranscoderOutput out;
            if (index < arguments.length) {
                out = new TranscoderOutput(new java.io.FileWriter(arguments[index]));
            } else {
                out = new TranscoderOutput(new java.io.OutputStreamWriter(System.out));
            }
            transcoder.transcode(in, out);
        } catch (Exception e) {
            e.printStackTrace();
            printUsage();
        }
    }

    /**
     * Prints the command usage.
     */
    protected void printUsage() {
        printHeader();
        System.out.println(localizableSupport.formatMessage("syntax", null));
        System.out.println();
        System.out.println(localizableSupport.formatMessage("options", null));
        Iterator it = handlers.keySet().iterator();
        while (it.hasNext()) {
            String s = (String)it.next();
            System.out.println(((OptionHandler)handlers.get(s)).getDescription());
        }
    }

    /**
     * Prints the command header.
     */
    protected void printHeader() {
        System.out.println(localizableSupport.formatMessage("header", null));
    }

    /**
     * This interface represents an option handler.
     */
    protected interface OptionHandler {
        /**
         * Handles the current option.
         */
        void handleOption();

        /**
         * Returns the option description.
         */
        String getDescription();
    }

    /**
     * To handle the '-doctype' option.
     */
    protected class DoctypeHandler implements OptionHandler {
        protected final Map values = new HashMap(6);
        {
            values.put("remove", SVGTranscoder.VALUE_DOCTYPE_REMOVE);
            values.put("change", SVGTranscoder.VALUE_DOCTYPE_CHANGE);
        }
        public void handleOption() {
            index++;
            if (index >= arguments.length) {
                throw new IllegalArgumentException();
            }
            Object val = values.get(arguments[index++]);
            if (val == null) {
                throw new IllegalArgumentException();
            }
            transcoder.addTranscodingHint(SVGTranscoder.KEY_DOCTYPE, val);
        }

        public String getDescription() {
            return localizableSupport.formatMessage("doctype.description", null);
        }
    }

    /**
     * To handle the '-newline' option.
     */
    protected class NewlineHandler implements OptionHandler {
        protected final Map values = new HashMap(6);
        {
            values.put("cr",    SVGTranscoder.VALUE_NEWLINE_CR);
            values.put("cr-lf", SVGTranscoder.VALUE_NEWLINE_CR_LF);
            values.put("lf",    SVGTranscoder.VALUE_NEWLINE_LF);
        }
        public void handleOption() {
            index++;
            if (index >= arguments.length) {
                throw new IllegalArgumentException();
            }
            Object val = values.get(arguments[index++]);
            if (val == null) {
                throw new IllegalArgumentException();
            }
            transcoder.addTranscodingHint(SVGTranscoder.KEY_NEWLINE, val);
        }

        public String getDescription() {
            return localizableSupport.formatMessage("newline.description", null);
        }
    }

    /**
     * To handle the '-no-format' option.
     */
    protected class NoFormatHandler implements OptionHandler {
        public void handleOption() {
            index++;
            transcoder.addTranscodingHint(SVGTranscoder.KEY_FORMAT, Boolean.FALSE);
        }

        public String getDescription() {
            return localizableSupport.formatMessage("no-format.description", null);
        }
    }

    /**
     * To handle the '-public-id' option.
     */
    protected class PublicIdHandler implements OptionHandler {
        public void handleOption() {
            index++;
            if (index >= arguments.length) {
                throw new IllegalArgumentException();
            }
            String s = arguments[index++];
            transcoder.addTranscodingHint(SVGTranscoder.KEY_PUBLIC_ID, s);
        }

        public String getDescription() {
            return localizableSupport.formatMessage("public-id.description", null);
        }
    }

    /**
     * To handle the '-system-id' option.
     */
    protected class SystemIdHandler implements OptionHandler {
        public void handleOption() {
            index++;
            if (index >= arguments.length) {
                throw new IllegalArgumentException();
            }
            String s = arguments[index++];
            transcoder.addTranscodingHint(SVGTranscoder.KEY_SYSTEM_ID, s);
        }

        public String getDescription() {
            return localizableSupport.formatMessage("system-id.description", null);
        }
    }

    /**
     * To handle the '-xml-decl' option.
     */
    protected class XMLDeclHandler implements OptionHandler {
        public void handleOption() {
            index++;
            if (index >= arguments.length) {
                throw new IllegalArgumentException();
            }
            String s = arguments[index++];
            transcoder.addTranscodingHint(SVGTranscoder.KEY_XML_DECLARATION, s);
        }

        public String getDescription() {
            return localizableSupport.formatMessage("xml-decl.description", null);
        }
    }

    /**
     * To handle the '-tab-width' option.
     */
    protected class TabWidthHandler implements OptionHandler {
        public void handleOption() {
            index++;
            if (index >= arguments.length) {
                throw new IllegalArgumentException();
            }
            transcoder.addTranscodingHint(SVGTranscoder.KEY_TABULATION_WIDTH,
                                          new Integer(arguments[index++]));
        }

        public String getDescription() {
            return localizableSupport.formatMessage("tab-width.description", null);
        }
    }

    /**
     * To handle the '-doc-width' option.
     */
    protected class DocWidthHandler implements OptionHandler {
        public void handleOption() {
            index++;
            if (index >= arguments.length) {
                throw new IllegalArgumentException();
            }
            transcoder.addTranscodingHint(SVGTranscoder.KEY_DOCUMENT_WIDTH,
                                          new Integer(arguments[index++]));
        }

        public String getDescription() {
            return localizableSupport.formatMessage("doc-width.description", null);
        }
    }
}
