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

package org.apache.batik.dom;

import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.TestReport;

import org.apache.batik.util.SVGConstants;
import org.apache.batik.dom.svg.SVGDOMImplementation;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.util.DOMUtilities;

import org.apache.batik.util.XMLResourceDescriptor;

import org.w3c.dom.*;

import java.io.*;

/**
 * Checks that Text nodes can be properly written and read.
 * This test creates a Document with a CDATA section and checks
 * that the CDATA section content can be written out and then read
 * without being altered.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 */
public class DOMUtilitiesCharacterEscaping extends AbstractTest {
    public TestReport runImpl() throws Exception {
        DOMImplementation impl = new SVGDOMImplementation();
        Document doc = impl.createDocument(SVGConstants.SVG_NAMESPACE_URI,
                                           "svg", null);

        Element svg = doc.getDocumentElement();
        Element text = doc.createElementNS(SVGConstants.SVG_NAMESPACE_URI,
                                           "text");
        svg.appendChild(text);

        text.setAttributeNS(null, "id", "myText");
        String unescapedContent = "You should not escape: & # \" ...";
        CDATASection cdata = doc.createCDATASection(unescapedContent);

        text.appendChild(cdata);

        Writer stringWriter = new StringWriter();

        DOMUtilities.writeDocument(doc, stringWriter);
        
        String docString = stringWriter.toString();
        System.err.println(">>>>>>>>>>> Document content \n\n" + docString + "\n\n<<<<<<<<<<<<<<<<");

        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        doc = f.createDocument("http://xml.apache.org/batik/foo.svg", 
                               new StringReader(stringWriter.toString()));

        text = doc.getElementById("myText");
        cdata = (CDATASection)text.getFirstChild();
        if (cdata.getData().equals(unescapedContent)) {
            return reportSuccess();
        } 

        TestReport report = reportError("Unexpected CDATA read-back");
        report.addDescriptionEntry("expected cdata", unescapedContent);
        report.addDescriptionEntry("actual cdata", cdata.getData());
        return report;
    }
}
