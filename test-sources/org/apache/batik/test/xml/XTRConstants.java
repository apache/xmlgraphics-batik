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

/**
 * Contains constants for the XML Test Report (XTR) syntax.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public interface XTRConstants extends XMLReflectConstants{
    String XTR_NAMESPACE_URI 
        = "http://xml.apache.org/xml-batik/test/xtr";

    
    /////////////////////////////////////////////////////////////////////////
    // XTR tags
    /////////////////////////////////////////////////////////////////////////
    String XTR_DESCRIPTION_TAG = "description";
    String XTR_FILE_ENTRY_TAG = "fileEntry";
    String XTR_GENERIC_ENTRY_TAG = "genericEntry";
    String XTR_TEST_REPORT_TAG = "testReport";
    String XTR_TEST_SUITE_REPORT_TAG = "testSuiteReport";
    String XTR_URI_ENTRY_TAG = "uriEntry";
    
    /////////////////////////////////////////////////////////////////////////
    // XTR attributes
    /////////////////////////////////////////////////////////////////////////
    String XTR_CLASS_ATTRIBUTE = "class";
    String XTR_DATE_ATTRIBUTE = "date";
    String XTR_KEY_ATTRIBUTE = "key";
    String XTR_ERROR_CODE_ATTRIBUTE = "errorCode";
    String XTR_ID_ATTRIBUTE = "id";
    String XTR_STATUS_ATTRIBUTE = "status";
    String XTR_TEST_NAME_ATTRIBUTE = "testName";
    String XTR_VALUE_ATTRIBUTE     = "value";

    /////////////////////////////////////////////////////////////////////////
    // XTR values
    /////////////////////////////////////////////////////////////////////////
    String XTR_PASSED_VALUE = "passed";
    String XTR_FAILED_VALUE = "failed";
}
