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

import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;

/**
 * Bridge class for the &lt;g> element.
 *
 * @author <a href="mailto:tkormann@apache.org">Thierry Kormann</a>
 * @version $Id$
 */
public class SVGGElementBridge extends AbstractGraphicsNodeBridge {

    /**
     * Constructs a new bridge for the &lt;g> element.
     */
    public SVGGElementBridge() {}

    /**
     * Returns 'g'.
     */
    public String getLocalName() {
        return SVG_G_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new SVGGElementBridge();
    }

    /**
     * Creates a <tt>GraphicsNode</tt> according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes the graphics node to build
     * @return a graphics node that represents the specified element
     */
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        CompositeGraphicsNode gn =
            (CompositeGraphicsNode)super.createGraphicsNode(ctx, e);
	if (gn == null) {
	    return null;
	}

        // 'color-rendering'
        RenderingHints hints = CSSUtilities.convertColorRendering(e, null);
        if (hints != null) {
            gn.setRenderingHints(hints);
        }

        // 'enable-background'
        Rectangle2D r = CSSUtilities.convertEnableBackground(e);
        if (r != null) {
            gn.setBackgroundEnable(r);
        }
        return gn;
    }

    /**
     * Creates a <tt>CompositeGraphicsNode</tt>.
     */
    protected GraphicsNode instantiateGraphicsNode() {
        return new CompositeGraphicsNode();
    }

    /**
     * Returns true as the &lt;g> element is a container.
     */
    public boolean isComposite() {
        return true;
    }

    // BridgeUpdateHandler implementation //////////////////////////////////

    /**
     * Invoked when an MutationEvent of type 'DOMNodeInserted' is fired.
     */
    public void handleDOMNodeInsertedEvent(MutationEvent evt) {
        if ( evt.getTarget() instanceof Element ){
            handleElementAdded((CompositeGraphicsNode)node, 
                               e, 
                               (Element)evt.getTarget());
        }
    }

    /**
     * Invoked when an MutationEvent of type 'DOMNodeInserted' is fired.
     */
    public void handleElementAdded(CompositeGraphicsNode gn, 
                                   Node parent, 
                                   Element childElt) {
        // build the graphics node
        GVTBuilder builder = ctx.getGVTBuilder();
        GraphicsNode childNode = builder.build(ctx, childElt);
        if (childNode == null) {
            return; // the added element is not a graphic element
        }
        
        // Find the index where the GraphicsNode should be added
        int idx = -1;
        for(Node ps = childElt.getPreviousSibling(); ps != null;
            ps = ps.getPreviousSibling()) {
            if (ps.getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element pse = (Element)ps;
            GraphicsNode psgn = ctx.getGraphicsNode(pse);
            while ((psgn != null) && (psgn.getParent() != gn)) {
                // In some cases the GN linked is
                // a child (in particular for images).
                psgn = psgn.getParent();
            }
            if (psgn == null)
                continue;
            idx = gn.indexOf(psgn);
            if (idx == -1)
                continue;
            break;
        }
        // insert after prevSibling, if
        // it was -1 this becomes 0 (first slot)
        idx++; 
        gn.add(idx, childNode);
    }
}
