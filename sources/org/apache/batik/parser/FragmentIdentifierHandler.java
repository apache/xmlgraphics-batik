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

package org.apache.batik.parser;

/**
 * This interface must be implemented and then registred as the
 * handler of a <code>PreserveAspectRatioParser</code> instance
 * in order to be notified of parsing events.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface FragmentIdentifierHandler
    extends PreserveAspectRatioHandler,
            TransformListHandler {

    /**
     * Invoked when the fragment identifier starts.
     * @exception ParseException if an error occured while processing the
     *                           fragment identifier
     */
    void startFragmentIdentifier() throws ParseException;

    /**
     * Invoked when an ID has been parsed.
     * @param s The string that represents the parsed ID.
     * @exception ParseException if an error occured while processing the
     *                           fragment identifier
     */
    void idReference(String s) throws ParseException;

    /**
     * Invoked when 'viewBox(x,y,width,height)' has been parsed.
     * @param x&nbsp;y&nbsp;width&nbsp;height the coordinates of the viewbox.
     * @exception ParseException if an error occured while processing the
     *                           fragment identifier
     */
    void viewBox(float x, float y, float width, float height)
        throws ParseException;

    /**
     * Invoked when a view target specification starts.
     * @exception ParseException if an error occured while processing the
     *                           fragment identifier
     */
    void startViewTarget() throws ParseException;

    /**
     * Invoked when a identifier has been parsed within a view target
     * specification.
     * @param name the target name.
     * @exception ParseException if an error occured while processing the
     *                           fragment identifier
     */
    void viewTarget(String name) throws ParseException;

    /**
     * Invoked when a view target specification ends.
     * @exception ParseException if an error occured while processing the
     *                           fragment identifier
     */
    void endViewTarget() throws ParseException;

    /**
     * Invoked when a 'zoomAndPan' specification has been parsed.
     * @param magnify true if 'magnify' has been parsed.
     * @exception ParseException if an error occured while processing the
     *                           fragment identifier
     */
    void zoomAndPan(boolean magnify);

    /**
     * Invoked when the fragment identifier ends.
     * @exception ParseException if an error occured while processing the
     *                           fragment identifier
     */
    void endFragmentIdentifier() throws ParseException;
}
