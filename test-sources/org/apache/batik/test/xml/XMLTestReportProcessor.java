/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import java.net.URL;

import org.apache.batik.test.TestReport;
import org.apache.batik.test.TestReportProcessor;
import org.apache.batik.test.TestException;

import org.apache.batik.dom.svg.SVGDOMImplementation;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMImplementation;

/**
 * This implementation of the <tt>TestReportProcessor</tt> interface
 * converts the <tt>TestReports</tt> it processes into an 
 * XML document that it outputs in a directory. The directory
 * used by the object can be configured at creation time.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class XMLTestReportProcessor 
    implements TestReportProcessor,
               XTRConstants {
    /**
     * Prefix for the files created by this processor
     */
    public static final String XML_TEST_REPORT_PREFIX
        = Messages.formatMessage("XMLTestReportProcessor.config.xml.test.report.prefix", null);

    /**
     * Prefix for the resource files created by this processor. Resource files are 
     * copies of files found as Entries in TestReports.
     */
    public static final String XML_TEST_REPORT_RESOURCE_PREFIX
        = Messages.formatMessage("XMLTestReportProcessor.config.xml.test.report.resource.prefix", null);

    /**
     * Suffix for the files created by this processor
     */
    public static final String XML_TEST_REPORT_SUFFIX
        = Messages.formatMessage("XMLTestReportProcessor.config.xml.test.report.suffix", null);

    /**
     * Default report directory
     */
    public static final String XML_TEST_REPORT_DEFAULT_DIRECTORY 
        = Messages.formatMessage("XMLTestReportProcessor.config.xml.test.report.default.directory", null);

    /**
     * Default report resources directory
     */
    public static final String XML_TEST_REPORT_RESOURCES_DEFAULT_DIRECTORY 
        = Messages.formatMessage("XMLTestReportProcessor.config.xml.test.report.resources.default.directory", null);

    /**
     * Recursively processes the input <tt>TestReport</tt> and
     * any of its children.
     */
    public void processReport(TestReport report) 
        throws TestException {

        try {
            /**
             * Create a new document and build the root
             * <testReport> element. Then, process the 
             * TestReports recursively.
             */
            DOMImplementation impl 
                = SVGDOMImplementation.getDOMImplementation();
            
            Document document =
                impl.createDocument(XTR_NAMESPACE_URI, 
                                    XTR_TEST_REPORT_TAG, null);
            Element root = document.getDocumentElement();
            
            processReport(report, root, document);
            
            serializeReport(root);

        } catch(Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            throw new TestException(INTERNAL_ERROR,
                                    new Object[] { e.getClass().getName(),
                                                   e.getMessage(),
                                                   sw.toString() },
                                    e);
        }
    }

    /**
     * By default, the report directory is given by a configuration 
     * variable.
     */
    public File getReportDirectory()
        throws IOException {
        return new File(XML_TEST_REPORT_DEFAULT_DIRECTORY);
    }

    /**
     * By default, the report resources directory is
     * given by a configuration variable.
     */
    public File getReportResourcesDirectory()
        throws IOException {
        return new File(XML_TEST_REPORT_RESOURCES_DEFAULT_DIRECTORY);
    }

    /**
     * Recursively processes the input <tt>TestReport</tt> adding
     * the report information to the input element.
     */
    protected void processReport(TestReport report,
                                 Element reportElement,
                                 Document reportDocument) throws IOException {
        if(report == null){
            throw new IllegalArgumentException();
        }

        reportElement.setAttributeNS(null,
                                     XTR_TEST_NAME_ATTRIBUTE,
                                     report.getTest().getName());

        String status = report.hasPassed() 
            ? XTR_PASSED_VALUE
            : XTR_FAILED_VALUE;
        
        reportElement.setAttributeNS(null,
                                     XTR_STATUS_ATTRIBUTE,
                                     status);

        if(!report.hasPassed()){
            reportElement.setAttributeNS(null,
                                         XTR_ERROR_CODE_ATTRIBUTE,
                                         report.getErrorCode());
        }
        
        TestReport.Entry[] entries = report.getDescription();
        int n = entries != null ? entries.length : 0;

        if (n>0) {
            Element descriptionElement
                = reportDocument.createElementNS(null,
                                                 XTR_DESCRIPTION_TAG);
            
            reportElement.appendChild(descriptionElement);

            for(int i=0; i<n; i++){
                processEntry(entries[i],
                             descriptionElement,
                             reportDocument);

            }
        }
    }

    protected void processEntry(TestReport.Entry entry,
                                Element descriptionElement,
                                Document reportDocument) throws IOException {

        Object value = entry.getValue();
        String key   = entry.getKey();

        if(value instanceof TestReport){
            Element reportElement 
                = reportDocument.createElementNS(XTR_NAMESPACE_URI,
                                                 XTR_TEST_REPORT_TAG);

            descriptionElement.appendChild(reportElement);
            processReport((TestReport)entry.getValue(),
                          reportElement,
                          reportDocument);
        }
        else if(value instanceof URL){
            Element entryElement 
                = reportDocument.createElementNS(XTR_NAMESPACE_URI,
                                                 XTR_URI_ENTRY_TAG);

            descriptionElement.appendChild(entryElement);
            
            entryElement.setAttributeNS(null,
                                        XTR_KEY_ATTRIBUTE,
                                        key.toString());
            
            entryElement.setAttributeNS(null, 
                                        XTR_VALUE_ATTRIBUTE,
                                        value.toString());

        }
        else if(value instanceof File){
            //
            // The entry is a potentially temporary File. Copy
            // the file to the repository and create a file entry
            // referencing that file copy.
            //
            File tmpFile = (File)value;

            File tmpFileCopy = File.createTempFile(XML_TEST_REPORT_RESOURCE_PREFIX,
                                                   tmpFile.getName(),
                                                   getReportResourcesDirectory());

            copy(tmpFile, tmpFileCopy);

            Element entryElement 
                = reportDocument.createElementNS(XTR_NAMESPACE_URI,
                                                 XTR_FILE_ENTRY_TAG);

            descriptionElement.appendChild(entryElement);
            
            entryElement.setAttributeNS(null,
                                        XTR_KEY_ATTRIBUTE,
                                        key.toString());
            
            entryElement.setAttributeNS(null, 
                                        XTR_VALUE_ATTRIBUTE,
                                        tmpFileCopy.getAbsolutePath());

        }
        else {
           
            Element entryElement 
                = reportDocument.createElementNS(XTR_NAMESPACE_URI,
                                                 XTR_GENERIC_ENTRY_TAG);

            descriptionElement.appendChild(entryElement);
            
            entryElement.setAttributeNS(null,
                                        XTR_KEY_ATTRIBUTE,
                                        key.toString());
            
            entryElement.setAttributeNS(null, 
                                        XTR_VALUE_ATTRIBUTE,
                                        value.toString());

        }
    }

    /**
     * Utility method. Copies in to out
     */
    protected void copy(File in, File out) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(in));
        OutputStream os = new BufferedOutputStream(new FileOutputStream(out));
        
        final byte[] b = new byte[1024];
        int n = -1;
        while( (n = is.read(b)) != -1 ){
            os.write(b, 0, n);
        }
        
        is.close();
        os.close();
    }

    /**
     * Saves the XML document into a file
     */
    protected void serializeReport(Element reportElement) throws IOException {
        //
        // First, create a new File
        //
        File tmpFile = File.createTempFile(XML_TEST_REPORT_PREFIX,
                                           XML_TEST_REPORT_SUFFIX,
                                           getReportDirectory());

        FileWriter fw = new FileWriter(tmpFile);


        serializeElement(reportElement,
                         "",
                         fw);

        fw.close();
    }

    
    static private String EOL;
    static private final String TAB = "    ";
    static private final String OPEN_TAG_END_CHILDREN = " >";
    static private final String OPEN_TAG_END_NO_CHILDREN = " />";
    static private final String OPEN_TAG_START = "<";
    static private final String CLOSE_TAG_START = "</";
    static private final String CLOSE_TAG_END = ">";
    static private final String SPACE = " ";
    static private final String EQUAL_SIGN = "=";
    static private final String DOUBLE_QUOTE = "\"";

    static private final String PROPERTY_LINE_SEPARATOR = "line.separator";
    static private final String PROPERTY_LINE_SEPARATOR_DEFAULT = "\n";

    static {
        String  temp;
        try { 
            temp = System.getProperty (PROPERTY_LINE_SEPARATOR, 
                                       PROPERTY_LINE_SEPARATOR_DEFAULT); 
        } catch (SecurityException e) { 
            temp = PROPERTY_LINE_SEPARATOR_DEFAULT;
        }
        EOL = temp;
    }

    protected void serializeElement(Element element,
                                    String  prefix,
                                    Writer  writer) throws IOException {
        writer.write(prefix);
        writer.write(OPEN_TAG_START);
        writer.write(element.getTagName());
        
        serializeAttributes(element,
                            writer);
        
        NodeList children = element.getChildNodes();
        if(children != null && children.getLength() > 0){
            writer.write(OPEN_TAG_END_CHILDREN);
            writer.write(EOL);
            int n = children.getLength();
            for(int i=0; i<n; i++){
                Node child = children.item(i);
                if(child.getNodeType() == Node.ELEMENT_NODE){
                    serializeElement((Element)child,
                                     prefix + TAB,
                                     writer);
                }
            }
            writer.write(prefix);
            writer.write(CLOSE_TAG_START);
            writer.write(element.getTagName());
            writer.write(CLOSE_TAG_END);
        }
        else{
            writer.write(OPEN_TAG_END_NO_CHILDREN);
        }

        writer.write(EOL);

    }

    protected void serializeAttributes(Element element,
                                       Writer  writer) throws IOException{
        NamedNodeMap attributes = element.getAttributes();
        if (attributes != null){
            int nAttr = attributes.getLength();
            for(int i=0; i<nAttr; i++){
                Attr attr = (Attr)attributes.item(i);
                writer.write(SPACE);
                writer.write(attr.getName());
                writer.write(EQUAL_SIGN);
                writer.write(DOUBLE_QUOTE);
                writer.write(attr.getValue());
                writer.write(DOUBLE_QUOTE);
            }
        }
    }
}
