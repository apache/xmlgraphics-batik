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
 * handler of a <code>PreserveAspectRatioParser</code> instance in order to
 * be notified of parsing events.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface PreserveAspectRatioHandler {
    /**
     * Invoked when the PreserveAspectRatio parsing starts.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void startPreserveAspectRatio() throws ParseException;

    /**
     * Invoked when 'none' been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void none() throws ParseException;

    /**
     * Invoked when 'xMaxYMax' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMaxYMax() throws ParseException;

    /**
     * Invoked when 'xMaxYMid' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMaxYMid() throws ParseException;

    /**
     * Invoked when 'xMaxYMin' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMaxYMin() throws ParseException;

    /**
     * Invoked when 'xMidYMax' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMidYMax() throws ParseException;

    /**
     * Invoked when 'xMidYMid' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMidYMid() throws ParseException;

    /**
     * Invoked when 'xMidYMin' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMidYMin() throws ParseException;

    /**
     * Invoked when 'xMinYMax' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMinYMax() throws ParseException;

    /**
     * Invoked when 'xMinYMid' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMinYMid() throws ParseException;

    /**
     * Invoked when 'xMinYMin' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void xMinYMin() throws ParseException;

    /**
     * Invoked when 'meet' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void meet() throws ParseException;

    /**
     * Invoked when 'slice' has been parsed.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio 
     */
    void slice() throws ParseException;

    /**
     * Invoked when the PreserveAspectRatio parsing ends.
     * @exception ParseException if an error occured while processing
     * the PreserveAspectRatio
     */
    void endPreserveAspectRatio() throws ParseException;
}
