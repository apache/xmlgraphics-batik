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

package org.apache.batik.script;

import org.apache.batik.bridge.BridgeContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This interface represents the 'window' object defined in the global
 * environment of a SVG document.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface Window {
    /**
     * Evaluates the given string repeatedly after the given amount of
     * time.  This method does not stall the script: the evaluation is
     * scheduled and the script continues its execution.
     * @return an object representing the interval created.
     */
    Object setInterval(String script, long interval);

    /**
     * Calls the 'run' method of the given Runnable repeatedly after
     * the given amount of time.  This method does not stall the
     * script: the evaluation is scheduled and the script continues
     * its execution.
     * @return an object representing the interval created.
     */
    Object setInterval(Runnable r, long interval);

    /**
     * Cancels an interval that was set by a call to 'setInterval'.
     */
    void clearInterval(Object interval);

    /**
     * Evaluates the given string after the given amount of time.
     * This method does not stall the script: the evaluation is
     * scheduled and the script continues its execution.
     * @return an object representing the timeout created.
     */
    Object setTimeout(String script, long timeout);

    /**
     * Calls the 'run' method of the given Runnable after the given
     * amount of time.  This method does not stall the script: the
     * evaluation is scheduled and the script continues its execution.
     * @return an object representing the timeout created.
     */
    Object setTimeout(Runnable r, long timeout);

    /**
     * Cancels an timeout that was set by a call to 'setTimeout'.
     */
    void clearTimeout(Object timeout);

    /**
     * Parses the given XML string into a DocumentFragment of the
     * given document or a new document if 'doc' is null.
     * @return The document fragment or null on error.
     */
    Node parseXML(String text, Document doc);

    /**
     * Gets data from the given URI.
     * @param uri The URI where the data is located.
     * @param h A handler called when the data is available.
     */
    void getURL(String uri, GetURLHandler h);

    /**
     * Gets data from the given URI.
     * @param uri The URI where the data is located.
     * @param h A handler called when the data is available.
     * @param enc The character encoding of the data.
     */
    void getURL(String uri, GetURLHandler h, String enc);

    /**
     * To handle the completion of a 'getURL()' call.
     */
    public interface GetURLHandler {
        
        /**
         * Called before 'getURL()' returns.
         * @param success Whether the data was successfully retreived.
         * @param mime The data MIME type.
         * @param content The data.
         */
        void getURLDone(boolean success, String mime, String content);
    }

    /**
     * Displays an alert dialog box.
     */
    void alert(String message);

    /**
     * Displays a confirm dialog box.
     */
    boolean confirm(String message);

    /**
     * Displays an input dialog box.
     * @return The input of the user, or null if the dialog was cancelled.
     */
    String prompt(String message);

    /**
     * Displays an input dialog box, given the default value.
     * @return The input of the user, or null if the dialog was cancelled.
     */
    String prompt(String message, String defVal);

    /**
     * Returns the current BridgeContext. This object given a deep
     * access to the viewer internals.
     */
    BridgeContext getBridgeContext();

    /**
     * Returns the associated interpreter.
     */
    Interpreter getInterpreter();
}
