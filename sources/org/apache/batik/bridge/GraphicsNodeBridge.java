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

/**
 * Bridge class for creating, building, and updating a <tt>GraphicsNode</tt>
 * according to an <tt>Element</tt>.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public interface GraphicsNodeBridge extends Bridge {

    /**
     * Creates a <tt>GraphicsNode</tt> according to the specified parameters.
     * This is called before children have been added to the
     * returned GraphicsNode (obviously since you construct and return it).
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @return a graphics node that represents the specified element
     */
    GraphicsNode createGraphicsNode(BridgeContext ctx, Element e);

    /**
     * Builds using the specified BridgeContext and element, the
     * specified graphics node.  This is called after all the children
     * of the node have been constructed and added, so it is safe to
     * do work that depends on being able to see your children nodes
     * in this method.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @param node the graphics node to build
     */
    void buildGraphicsNode(BridgeContext ctx, Element e, GraphicsNode node);

    /**
     * Returns true if the bridge handles container element, false
     * otherwise.
     */
    boolean isComposite();

    /**
     * Returns true if the graphics node has to be displayed, false
     * otherwise.
     */
    boolean getDisplay(Element e);

    /**
     * <!> FIX ME: Move to Bridge 
     * 
     * Returns the Bridge instance to be used for a single DOM 
     * element. For example, a static Bridge (i.e., a Bridge for
     * static SVG content) will always return the same instance.
     * A dynamic Bridge will return a new instance on each call.
     */
    Bridge getInstance();

}
