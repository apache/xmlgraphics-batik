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

package org.apache.batik.apps.svgbrowser;

import javax.swing.Action;

/**
 * This interface represents a SVG viewer application.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface Application {

    /**
     * Creates and shows a new viewer frame.
     */
    JSVGViewerFrame createAndShowJSVGViewerFrame();

    /**
     * Closes the given viewer frame.
     */
    void closeJSVGViewerFrame(JSVGViewerFrame f);

    /**
     * Creates an action to exit the application.
     */
    Action createExitAction(JSVGViewerFrame vf);

    /**
     * Opens the given link in a new window.
     */
    void openLink(String url);

    /**
     * Returns the XML parser class name.
     */
    String getXMLParserClassName();

    /**
     * Returns true if the XML parser must be in validation mode, false
     * otherwise.
     */
    boolean isXMLParserValidating();

    /**
     * Shows the preference dialog.
     */
    void showPreferenceDialog(JSVGViewerFrame f);

    /**
     * Returns the user languages.
     */
    String getLanguages();

    /**
     * Returns the user stylesheet uri.
     * @return null if no user style sheet was specified.
     */
    String getUserStyleSheetURI();

    /**
     * Returns the default value for the CSS
     * "font-family" property
     */
    String getDefaultFontFamily();

    /**
     * Returns the CSS media to use.
     * @return empty string if no CSS media was specified.
     */
    String getMedia();

    /**
     * Returns true if the selection overlay is painted in XOR mode, false
     * otherwise.
     */
    boolean isSelectionOverlayXORMode();

    /**
     * Returns true if the input scriptType can be loaded in
     * this application.
     */
    boolean canLoadScriptType(String scriptType);

    /**
     * Returns the allowed origins for scripts.
     * @see ResourceOrigin
     */
    int getAllowedScriptOrigin();

    /**
     * Returns the allowed origins for external
     * resources. 
     *
     * @see ResourceOrigin
     */
    int getAllowedExternalResourceOrigin();

    /**
     * Notifies Application of recently visited URI
     */
    void addVisitedURI(String uri);

    /**
     * Asks Application for a list of recently visited URI
     */
    String[] getVisitedURIs();

}
