/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

/*import org.apache.xalan.xslt.XSLTProcessorFactory;
import org.apache.xalan.xslt.XSLTInputSource;
import org.apache.xalan.xslt.XSLTResultTarget;
import org.apache.xalan.xslt.XSLTProcessor;*/

import org.apache.batik.test.TestException;

/**
 * This implementation of the <tt>XMLTestReportProcessor.XMLReportConsumer</tt>
 * interface simply applies an XSL transformation to the input
 * XML file and stores the result in a configurable directory.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class XSLXMLReportConsumer 
    implements XMLTestReportProcessor.XMLReportConsumer {
    /**
     * Error code used when the output directory cannot be used
     */
    public static final String ERROR_OUTPUT_DIRECTORY_UNUSABLE 
        = "xml.XSLXMLReportConsumer.error.output.directory.unusable";

    /**
     * Stylesheet URI
     */
    private String stylesheet;

    /**
     * Output directory, i.e., the directory where the result
     * of the XSL transformation will be stored.
     */
    private String outputDirectory;

    /**
     * Output file name
     */
    private String outputFileName;

    /**
     * Constructor
     * @param stylesheet URI for the stylesheet to apply to the XML report
     * @param outputDirectory directory where the result of the XSL transformation
     *                  should be written
     * @param outputFileName name of the output report.
     */
    public XSLXMLReportConsumer(String stylesheet,
                                String outputDirectory,
                                String outputFileName){
        this.stylesheet = stylesheet;
        this.outputDirectory = outputDirectory;
        this.outputFileName = outputFileName;
    }

    /**
     * When a new report has been generated, this consumer
     * applies the same stylesheet to the input XML document
     */
    public void onNewReport(File xmlReport, 
                            File reportDirectory)
        throws Exception{

        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer(new StreamSource(stylesheet));
        
        transformer.transform(new StreamSource(xmlReport.toURL().toString()), 
                              new StreamResult(new FileOutputStream(createNewReportOutput(reportDirectory).getAbsolutePath())));
    }
    
    /**
     * Returns a new file in the outputDirectory, with 
     * the requested report name.
     */
    public File createNewReportOutput(File reportDirectory) throws Exception{
        File dir = new File(reportDirectory, outputDirectory);
        checkDirectory(dir);
        return new File(dir, outputFileName);
    }

    /**
     * Checks that the input File represents a directory that
     * can be used. If the directory does not exist, this method
     * will attempt to create it.
     */
    public void checkDirectory(File dir) 
        throws TestException {
        boolean dirOK = false;
        try{
            if(!dir.exists()){
                dirOK = dir.mkdir();
            }
            else if(dir.isDirectory()){
                dirOK = true;
            }
        }finally{
            if(!dirOK){
                throw new TestException(ERROR_OUTPUT_DIRECTORY_UNUSABLE,
                                        new Object[] {dir.getAbsolutePath()},
                                        null);
                
            }
        }
    }

}
