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

package org.apache.batik.dom.svg;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.MissingResourceException;

import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.TestReport;
import org.apache.batik.test.DefaultTestReport;

/**
 * This class tests that there is System Id for each public Id
 * in the dtdids.properties resource file.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @author $Id$
 */
public class SystemIdTest extends AbstractTest {
    public static final String ERROR_MISSING_SYSTEM_ID
        = "error.missing.system.id";

    public static final String KEY_MISSING_IDS
        = "key.missing.ids";

    public SystemIdTest() {
    }

    public TestReport runImpl() throws Exception {
        ResourceBundle rb = 
            ResourceBundle.getBundle(SAXSVGDocumentFactory.DTDIDS,
                                     Locale.getDefault());
        String dtdids = rb.getString(SAXSVGDocumentFactory.KEY_PUBLIC_IDS);
        
        StringTokenizer st = new StringTokenizer(dtdids, "-");
        int nIds = st.countTokens();
        String missingIds = "";
        for (int i=0; i<nIds; i++) {
            String publicId = st.nextToken();
            publicId = "-" + publicId.trim();
            System.out.println("Testing public id: " + publicId);
            try {
                rb.getString(SAXSVGDocumentFactory.KEY_SYSTEM_ID 
                              + publicId.replace(' ', '_'));
            } catch (MissingResourceException e) {
                missingIds += "[" + publicId + "]  -- ";
            }
        }
        
        if (!"".equals(missingIds)) {
            DefaultTestReport report = new DefaultTestReport(this);
            report.setErrorCode(ERROR_MISSING_SYSTEM_ID);
            report.addDescriptionEntry(KEY_MISSING_IDS, missingIds);
            report.setPassed(false);
            return report;
        }

        return reportSuccess();
    }
}
