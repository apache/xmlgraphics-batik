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

package org.apache.batik.bridge;

import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

/**
 * Thrown when the bridge has detected an error.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class BridgeException extends RuntimeException {

    /** The element on which the error occured. */
    protected Element e;

    /** The error code. */
    protected String code;

    /** The paramters to use for the error message. */
    protected Object [] params;

    /** The line number on which the error occured. */
    protected int line;

    /** The graphics node that represents the current state of the GVT tree. */
    protected GraphicsNode node;

    /**
     * Constructs a new <tt>BridgeException</tt> with the specified parameters.
     *
     * @param e the element on which the error occured
     * @param code the error code
     * @param params the parameters to use for the error message
     */
    public BridgeException(Element e, String code, Object [] params) {
        this.e = e;
        this.code = code;
        this.params = params;
    }

    /**
     * Returns the element on which the error occurred.
     */
    public Element getElement() {
        return e;
    }

    /**
     * Returns the line number on which the error occurred.
     */
    public void setLineNumber(int line) {
        this.line = line;
    }

    /**
     * Sets the graphics node that represents the current GVT tree built.
     *
     * @param node the graphics node
     */
    public void setGraphicsNode(GraphicsNode node) {
        this.node = node;
    }

    /**
     * Returns the graphics node that represents the current GVT tree built.
     */
    public GraphicsNode getGraphicsNode() {
        return node;
    }

    /**
     * Returns the error message according to the error code and parameters.
     */
    public String getMessage() {
        String uri = ((SVGDocument)e.getOwnerDocument()).getURL();
        Object [] fullparams = new Object[params.length+3];
        fullparams[0] = uri;
        fullparams[1] = new Integer(line);
        fullparams[2] = e.getLocalName();
        for (int i=0; i < params.length; ++i) {
            fullparams[i+3] = params[i];
        }
        return Messages.formatMessage(code, fullparams);
    }

    /**
     * Returns the exception's error code
     */
    public String getCode() {
        return code;
    }
}
