/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.svg;

import org.apache.batik.util.SVGConstants;

import java.io.*;
import org.w3c.dom.*;

/**
 * Writes a Node as text output. Package access. This is
 * *not* a full Xml printout implementation. It only covers
 * what is needed by the Graphics2D class.
 * The code for this class draws heavily from the work done
 * for Sun's Project X by David Brownell.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
class XmlWriter implements SVGConstants {
    static private String       EOL;
    static private String TAG_END = " />";
    static private String TAG_START = "</";
    static private String SPACE = " ";

    static {
        String  temp;
        try { temp = System.getProperty ("line.separator", "\n"); }
        catch (SecurityException e) { temp = "\n"; }
        EOL = temp;
    }

    static class IndentWriter extends Writer {
        private Writer proxied;
        private int indentLevel;

        public IndentWriter(Writer proxied){
            if(proxied == null)
                throw new IllegalArgumentException();

            // if(proxied instanceof OutputStreamWriter)
            //  System.out.println("Encoding : " + ((OutputStreamWriter)proxied).getEncoding());

            this.proxied = proxied;
        }

        public void setIndentLevel(int indentLevel){
            this.indentLevel = indentLevel;
        }

        public int getIndentLevel(){
            return indentLevel;
        }

        public void printIndent() throws IOException{
            proxied.write(EOL);
            int temp = indentLevel;
            while(temp > 0){
                proxied.write(' ');
                temp--;
            }
        }

        public Writer getProxied(){
            return proxied;
        }

        public void write(int c) throws IOException {
            proxied.write(c);
        }

        public void write(char cbuf[]) throws IOException {
            proxied.write(cbuf);
        }

        public void write(char cbuf[], int off, int len) throws IOException{
            proxied.write(cbuf, off, len);
        }

        public void write(String str) throws IOException {
            proxied.write(str);
        }

        public void write(String str, int off, int len) throws IOException {
            proxied.write(str, off, len);
        }

        public void flush() throws IOException{
            proxied.flush();
        }

        public void close() throws IOException{
            proxied.close();
        }
    }

    private static void writeXml(Attr attr, IndentWriter out) throws IOException{
        String name = attr.getName();
        out.write (name);
        out.write ("=\"");
        writeChildrenXml(attr, out);
        out.write ('"');
    }

    /**
     * Writes the attribute's value.
     */
    private static void writeChildrenXml(Attr attr, IndentWriter out) throws IOException{
        String value = attr.getValue();
        for (int i = 0; i < value.length (); i++) {
            int c = value.charAt (i);
            switch (c) {
            case '<':  out.write ("&lt;"); continue;
            case '>':  out.write ("&gt;"); continue;
            case '&':  out.write ("&amp;"); continue;
            case '\'': out.write ("&apos;"); continue;
            case '"':  out.write ("&quot;"); continue;
            default:   out.write (c); continue;
            }
        }
    }

    /**
     * Writes out the comment.  Note that spaces may be added to
     * prevent illegal comments:  between consecutive dashes ("--")
     * or if the last character of the comment is a dash.
     */
    private static void writeXml(Comment comment, IndentWriter out) throws IOException {
        char data[] = comment.getData().toCharArray();
        out.write ("<!--");
        if (data != null) {
            boolean     sawDash = false;
            int         length = data.length;

            // "--" illegal in comments, expand it
            for (int i = 0; i < length; i++) {
                if (data [i] == '-') {
                    if (sawDash)
                        out.write (' ');
                    else {
                        sawDash = true;
                        out.write ('-');
                        continue;
                    }
                }
                sawDash = false;
                out.write (data [i]);
            }
            if (data [data.length - 1] == '-')
                out.write (' ');
        }
        out.write ("-->");
    }

    private static void writeXml(Text text, IndentWriter out) throws IOException{
        char data[] = text.getData().toCharArray();
        int     start = 0, last = 0;

        // XXX saw this once -- being paranoid
        if (data == null)
            { System.err.println ("Null text data??"); return; }

        while (last < data.length) {
            char c = data [last];

            //
            // escape markup delimiters only ... and do bulk
            // writes wherever possible, for best performance
            //
            // note that character data can't have the CDATA
            // termination "]]>"; escaping ">" suffices, and
            // doing it very generally helps simple parsers
            // that may not be quite correct.
            //
            if (c == '<') {                     // not legal in char data
                out.write (data, start, last - start);
                start = last + 1;
                out.write ("&lt;");
            } else if (c == '>') {              // see above
                out.write (data, start, last - start);
                start = last + 1;
                out.write ("&gt;");
            } else if (c == '&') {              // not legal in char data
                out.write (data, start, last - start);
                start = last + 1;
                out.write ("&amp;");
            }
            last++;
        }
        out.write (data, start, last - start);
    }

    private static void writeXml(CDATASection cdataSection, IndentWriter out) throws IOException{
        char[] data = cdataSection.getData().toCharArray();
        out.write ("<![CDATA[");
        for (int i = 0; i < data.length; i++) {
            char c = data [i];

            // embedded "]]>" needs to be split into adjacent
            // CDATA blocks ... can be split at either point
            if (c == ']') {
                if ((i + 2) < data.length
                    && data [i + 1] == ']'
                    && data [i + 2] == '>') {
                    out.write ("]]]]><![CDATA[>");
                    continue;
                }
            }
            out.write (c);
        }
        out.write ("]]>");
    }

    private static void writeXml(Element element, IndentWriter out) throws IOException{
        out.write (TAG_START, 0, 1);    // "<"
        out.write (element.getTagName());

        NamedNodeMap attributes = element.getAttributes();
        if (attributes != null){
            StringBuffer styleAttrBuffer = new StringBuffer();
            int nAttr = attributes.getLength();
            for(int i=0; i<nAttr; i++){
                Attr attr = (Attr)attributes.item(i);
                out.write(' ');
                writeXml(attr, out);
            }
        }

        //
        // Write empty nodes as "<EMPTY />" to make sure version 3
        // and 4 web browsers can read empty tag output as HTML.
        // XML allows "<EMPTY/>" too, of course.
        //
        if (!element.hasChildNodes())
            out.write(TAG_END, 0, 3);   // " />"
        else  {
            out.write(TAG_END, 2, 1);   // ">"
            writeChildrenXml(element, out);
            out.write (TAG_START, 0, 2);        // "</"
            out.write (element.getTagName());
            out.write (TAG_END, 2, 1);  // ">"
        }
    }

    private static void writeChildrenXml(Element element, IndentWriter out) throws IOException {
        NodeList children = element.getChildNodes();
        if (children == null)
            return;

        int length = children.getLength();
        int     oldIndent = 0;
        boolean preserve = true;
        boolean pureText = true;

        oldIndent = out.getIndentLevel();

        try {
            out.setIndentLevel(oldIndent + 2);
            for(int i = 0; i < length; i++) {
                if(children.item(i).getNodeType () != Node.TEXT_NODE) {
                    out.printIndent ();
                    pureText = false;
                }
                writeXml(children.item(i), out);
            }
        } finally {
            out.setIndentLevel(oldIndent);
            out.printIndent();          // for ETag
        }
    }

    private static void writeDocumentHeader(IndentWriter out) throws IOException {
        String  encoding = null;

        if(out.getProxied() instanceof OutputStreamWriter)
            encoding = java2std (((OutputStreamWriter)out.getProxied()).getEncoding ());

        out.write ("<?xml version=\"1.0\"");
        if (encoding != null) {
            out.write (" encoding=\"");
            out.write (encoding);
            out.write ('\"');
        }
        out.write ("?>");
        out.write (EOL);
        out.write (EOL);

        // Write DOCTYPE declaration here. Skip until specification is released.
        out.write ("<!DOCTYPE svg PUBLIC '");
        out.write (SVG_PUBLIC_ID);
        out.write ("' '");
        out.write (SVG_SYSTEM_ID);
        out.write ("'");
        
        out.write (">");
        out.write (EOL);
    }

    private static void writeXml (Document document, IndentWriter out) throws IOException{
        writeDocumentHeader(out);
        NodeList childList = document.getChildNodes();
        writeXml(childList, out);
    }

    private static void writeXml(NodeList childList, IndentWriter out) throws IOException{
        int     length = childList.getLength ();

        if (length == 0)
            return;
        for (int i = 0; i < length; i++) {
            Node child = childList.item(i);
            writeXml(child, out);
            out.write (EOL);
        }
    }

    static String java2std (String encodingName){
        if (encodingName == null)
            return null;

        //
        // ISO-8859-N is a common family of 8 bit encodings;
        // N=1 is the eight bit subset of UNICODE, and there
        // seem to be at least drafts for some N >10.
        //
        if (encodingName.startsWith ("ISO8859_"))       // JDK 1.2
            return "ISO-8859-" + encodingName.substring (8);
        if (encodingName.startsWith ("8859_"))          // JDK 1.1
            return "ISO-8859-" + encodingName.substring (5);

        // XXX seven bit encodings ISO-2022-* ...
        // XXX EBCDIC encodings ...

        if ("ASCII7".equalsIgnoreCase (encodingName)
            || "ASCII".equalsIgnoreCase (encodingName))
            return "US-ASCII";

        //
        // All XML parsers _must_ support UTF-8 and UTF-16.
        // (UTF-16 ~= ISO-10646-UCS-2 plus surrogate pairs)
        //
        if ("UTF8".equalsIgnoreCase (encodingName))
            return "UTF-8";
        if (encodingName.startsWith ("Unicode"))
            return "UTF-16";

        //
        // Some common Japanese character sets.
        //
        if ("SJIS".equalsIgnoreCase (encodingName))
            return "Shift_JIS";
        if ("JIS".equalsIgnoreCase (encodingName))
            return "ISO-2022-JP";
        if ("EUCJIS".equalsIgnoreCase (encodingName))
            return "EUC-JP";

        // else we can't really do anything
        return encodingName;
    }

    public static void writeXml(Node node, Writer writer) throws IOException {
        // System.out.println("Writing class: " + node.getClass().getName());
        IndentWriter out = null;
        if(writer instanceof IndentWriter)
            out = (IndentWriter)writer;
        else
            out = new IndentWriter(writer);

        if(node instanceof Attr)
            writeXml((Attr)node, out);
        else if(node instanceof Comment)
            writeXml((Comment)node, out);
        else if(node instanceof Text)
            writeXml((Text)node, out);
        else if(node instanceof CDATASection)
            writeXml((CDATASection)node, out);
        else if(node instanceof Document)
            writeXml((Document)node, out);
        else if(node instanceof DocumentFragment){
            writeDocumentHeader(out);
            NodeList childList = node.getChildNodes();
            writeXml(childList, out);
        }
        else if(node instanceof Element)
            writeXml((Element)node, out);
        else {
            System.err.println("Unable to write node of type: " + node.getClass().getName());
        }
    }

    /**
     * Unit testing
     */
    public static void main(String args[]) throws Exception {
        Document domFactory = TestUtil.getDocumentPrototype();

        Element root = domFactory.createElement(TAG_SVG);
        Element genericDefs = domFactory.createElement(TAG_DEFS);
        Element topLevelGroup = domFactory.createElement(TAG_G);
        Element topLevelDefs = domFactory.createElement(TAG_DEFS);
        Element groupA = domFactory.createElement(TAG_G);
        Element groupB = domFactory.createElement(TAG_G);
        Comment comment = domFactory.createComment("This is the topLevelGroup comment");

        topLevelGroup.appendChild(comment);
        topLevelGroup.appendChild(topLevelDefs);
        topLevelGroup.appendChild(groupA);
        topLevelGroup.appendChild(groupB);

        root.appendChild(genericDefs);
        root.appendChild(topLevelGroup);

        domFactory.appendChild(root);

        TestUtil.trace(root, System.out);

        System.out.println("\n=======================================");

        try{
            Writer out =  new OutputStreamWriter(System.out);
            XmlWriter.writeXml(domFactory, out);
            out.flush();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
}
