/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

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
