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

package org.apache.batik.swing.svg;

import org.apache.batik.bridge.ExternalResourceSecurity;
import org.apache.batik.bridge.ScriptSecurity;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;

/**
 * This interface must be implemented to provide client services to
 * a JSVGComponent.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface SVGUserAgent {
    
    /**
     * Displays an error message.
     */
    void displayError(String message);
    
    /**
     * Displays an error resulting from the specified Exception.
     */
    void displayError(Exception ex);

    /**
     * Displays a message in the User Agent interface.
     * The given message is typically displayed in a status bar.
     */
    void displayMessage(String message);

    /**
     * Shows an alert dialog box.
     */
    void showAlert(String message);

    /**
     * Shows a prompt dialog box.
     */
    String showPrompt(String message);

    /**
     * Shows a prompt dialog box.
     */
    String showPrompt(String message, String defaultValue);

    /**
     * Shows a confirm dialog box.
     */
    boolean showConfirm(String message);

    /**
     * Returns the size of a px CSS unit in millimeters.
     */
    float getPixelUnitToMillimeter();

    /**
     * Returns the size of a px CSS unit in millimeters.
     * This will be removed after next release.
     * @see #getPixelUnitToMillimeter()
     */
    float getPixelToMM();

    /**
     * Returns the default font family.
     */
    String getDefaultFontFamily();

    /** 
     * Returns the  medium font size. 
     */
    float getMediumFontSize();

    /**
     * Returns a lighter font-weight.
     */
    float getLighterFontWeight(float f);

    /**
     * Returns a bolder font-weight.
     */
    float getBolderFontWeight(float f);

    /**
     * Returns the language settings.
     */
    String getLanguages();

    /**
     * Returns the user stylesheet uri.
     * @return null if no user style sheet was specified.
     */
    String getUserStyleSheetURI();

    /**
     * Returns the class name of the XML parser.
     */
    String getXMLParserClassName();

    /**
     * Returns true if the XML parser must be in validation mode, false
     * otherwise.
     */
    boolean isXMLParserValidating();

    /**
     * Returns this user agent's CSS media.
     */
    String getMedia();

    /**
     * Returns this user agent's alternate style-sheet title.
     */
    String getAlternateStyleSheet();

    /**
     * Opens a link in a new component.
     * @param uri The document URI.
     * @param newc Whether the link should be activated in a new component.
     */
    void openLink(String uri, boolean newc);

    /**
     * Tells whether the given extension is supported by this
     * user agent.
     */
    boolean supportExtension(String s);

    /**
     * Notifies the UserAgent that the input element 
     * has been found in the document. This is sometimes
     * called, for example, to handle &lt;a&gt; or
     * &lt;title&gt; elements in a UserAgent-dependant
     * way.
     */
    void handleElement(Element elt, Object data);


    /**
     * Returns the security settings for the given script
     * type, script url and document url
     * 
     * @param scriptType type of script, as found in the 
     *        type attribute of the &lt;script&gt; element.
     * @param scriptURL url for the script, as defined in
     *        the script's xlink:href attribute. If that
     *        attribute was empty, then this parameter should
     *        be null
     * @param docURL url for the document into which the 
     *        script was found.
     */
    ScriptSecurity getScriptSecurity(String scriptType,
                                     ParsedURL scriptURL,
                                     ParsedURL docURL);
        
    /**
     * This method throws a SecurityException if the script
     * of given type, found at url and referenced from docURL
     * should not be loaded.
     * 
     * This is a convenience method to call checkLoadScript
     * on the ScriptSecurity strategy returned by 
     * getScriptSecurity.
     *
     * @param scriptType type of script, as found in the 
     *        type attribute of the &lt;script&gt; element.
     * @param scriptURL url for the script, as defined in
     *        the script's xlink:href attribute. If that
     *        attribute was empty, then this parameter should
     *        be null
     * @param docURL url for the document into which the 
     *        script was found.
     */
    void checkLoadScript(String scriptType,
                         ParsedURL scriptURL,
                         ParsedURL docURL) throws SecurityException;

    /**
     * Returns the security settings for the given 
     * resource url and document url
     * 
     * @param resourceURL url for the resource, as defined in
     *        the resource's xlink:href attribute. If that
     *        attribute was empty, then this parameter should
     *        be null
     * @param docURL url for the document into which the 
     *        resource was found.
     */
    ExternalResourceSecurity 
        getExternalResourceSecurity(ParsedURL resourceURL,
                                    ParsedURL docURL);
        
    /**
     * This method throws a SecurityException if the resource
     * found at url and referenced from docURL
     * should not be loaded.
     * 
     * This is a convenience method to call checkLoadExternalResource
     * on the ExternalResourceSecurity strategy returned by 
     * getExternalResourceSecurity.
     *
     * @param scriptURL url for the script, as defined in
     *        the script's xlink:href attribute. If that
     *        attribute was empty, then this parameter should
     *        be null
     * @param docURL url for the document into which the 
     *        script was found.
     */
    void checkLoadExternalResource(ParsedURL resourceURL,
                                   ParsedURL docURL) throws SecurityException;

}
