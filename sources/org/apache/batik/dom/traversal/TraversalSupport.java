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

package org.apache.batik.dom.traversal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.TreeWalker;

/**
 * This class provides support for traversal.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class TraversalSupport {
    
    /**
     * The iterators list.
     */
    protected List iterators;

    /**
     * Creates a new TraversalSupport.
     */
    public TraversalSupport() {
    }

    /**
     * Creates a new tree walker.
     */
    public static TreeWalker createTreeWalker(AbstractDocument doc,
                                              Node root,
                                              int whatToShow, 
                                              NodeFilter filter, 
                                              boolean entityReferenceExpansion) {
        if (root == null) {
            throw doc.createDOMException
                (DOMException.NOT_SUPPORTED_ERR, "null.root",  null);
        }
        return new DOMTreeWalker(root, whatToShow, filter,
                                 entityReferenceExpansion);
    }

    /**
     * Creates a new node iterator.
     */
    public NodeIterator createNodeIterator(AbstractDocument doc,
                                           Node root,
                                           int whatToShow, 
                                           NodeFilter filter, 
                                           boolean entityReferenceExpansion)
        throws DOMException {
        if (root == null) {
            throw doc.createDOMException
                (DOMException.NOT_SUPPORTED_ERR, "null.root",  null);
        }
        NodeIterator result = new DOMNodeIterator(doc, root, whatToShow,
                                                  filter,
                                                  entityReferenceExpansion);
        if (iterators == null) {
            iterators = new LinkedList();
        }
        iterators.add(result);

        return result;
    }

    /**
     * Called by the DOM when a node will be removed from the current document.
     */
    public void nodeToBeRemoved(Node removedNode) {
        if (iterators != null) {
            Iterator it = iterators.iterator();
            while (it.hasNext()) {
                ((DOMNodeIterator)it.next()).nodeToBeRemoved(removedNode);
            }
        }
    }

    /**
     * Detaches the given node iterator.
     */
    public void detachNodeIterator(NodeIterator it) {
        iterators.remove(it);
    }
}
