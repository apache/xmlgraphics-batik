/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.util.Iterator;
import java.util.Stack;

/**
 * This class iterates over a GVT tree, returning each node starting at the
 * most distant leaves.  The root node is returned last.
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */
public class GraphicsNodeTreeIterator implements Iterator {

        GraphicsNode root;
        GraphicsNode current = null;
        Iterator currentIter;
        Stack iterStack;
        Stack nodeStack;

        int icount;

        public GraphicsNodeTreeIterator(GraphicsNode root) {
            icount = 0;
            this.root = root;
            if (root instanceof CompositeGraphicsNode) {
                currentIter = ((CompositeGraphicsNode) root).getChildren().iterator();
            }
            iterStack = new Stack();
            nodeStack = new Stack();
        }

        public boolean hasNext() {
            if (current == root) System.out.println("Nodecount: "+icount);
            return (current != root);
        }

        public Object next() {
            if (currentIter.hasNext()) {
                current = (GraphicsNode) currentIter.next();
                while (current instanceof CompositeGraphicsNode) {
                    iterStack.push(currentIter);
                    nodeStack.push(current);
                    currentIter = ((CompositeGraphicsNode) current).getChildren().iterator();
                    if (currentIter.hasNext()) {
                        current = (GraphicsNode) currentIter.next();
                    } else {
                        currentIter = (Iterator) iterStack.pop();
                        current = (GraphicsNode) nodeStack.pop();
                        break;
                    }
                }
            } else {
                if (!iterStack.empty()) {
                    do {
                        currentIter = (Iterator) iterStack.pop();
                        current = (GraphicsNode) nodeStack.pop();
                    } while (!currentIter.hasNext() && !iterStack.isEmpty());
                } else {
                    current = root;
                }
            }
            ++icount;
            return current;
        }

        public void remove() {
            ; // FIXME: should throw an exception, probably
        }
    }
