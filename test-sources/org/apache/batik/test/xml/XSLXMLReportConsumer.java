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
