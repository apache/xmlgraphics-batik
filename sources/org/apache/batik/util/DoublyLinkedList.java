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

package org.apache.batik.util;

/**
 * A simple Doubly Linked list class, designed to avoid
 * O(n) behaviour on insert and delete.
 */
public class DoublyLinkedList {

    /**
     * Basic doubly linked list node interface.
     */
	public static class Node {
		private Node next = null;
		private Node prev = null;
			
		public final Node getNext() { return next; }
		public final Node getPrev() { return prev; }
						
		protected final void setNext(Node newNext) { next = newNext; }
		protected final void setPrev(Node newPrev) { prev = newPrev; }

        /**
         * Unlink this node from it's current list...
         */
		protected final void unlink() {
			if (getNext() != null)
				getNext().setPrev(getPrev());
			if (getPrev() != null)
				getPrev().setNext(getNext());
			
			setNext(null);
			setPrev(null);
		}
						
        /**
         * Link this node in, infront of nde (unlinks it's self
         * before hand if needed).
         * @param nde the node to link in before.
         */
		protected final void insertBefore(Node nde) {
			// Already here...
			if (this == nde) return;

			if (getPrev() != null)
                unlink();
			
			// Actually insert this node...
			if (nde == null) {
				// empty lst...
				setNext(this);
				setPrev(this);
			} else {
				setNext(nde);
				setPrev(nde.getPrev());
				nde.setPrev(this);
                if (getPrev() != null)
                    getPrev().setNext(this);
			}
		}
	}


    private Node head = null;
    private int  size = 0;
			
    public DoublyLinkedList() {}
			
    /**
     * Returns the number of elements currently in the list.
     */
    public synchronized int getSize() { return size; }

    /**
     * Removes all elements from the list.
     */
    public synchronized void empty() {
        while(size > 0) pop();
    }
			
    /**
     * Get the current head element
     * @return The current 'first' element in list.
     */
    public Node getHead() { return head; }
    /**
     * Get the current tail element
     * @return The current 'last' element in list.
     */
    public Node getTail() { return head.getPrev(); }

    /**
     * Moves <tt>nde</tt> to the head of the list (equivilent to
     * remove(nde); add(nde); but faster.
     */
    public void touch(Node nde) {
        if (nde == null) return;
        nde.insertBefore(head);
        head = nde;
    }

    public void add(int index, Node nde) {
        if (nde == null) return;
        Node after = head;
        while (index != 0) {
            after = after.getNext();
            index--;
        }
        nde.insertBefore(after);
        if (after == head) 
            head=nde;
        size++;
    }

    /**
     * Adds <tt>nde</tt> to the head of the list.
     * In perl this is called an 'unpop'.  <tt>nde</tt> should
     * not currently be part of any list.
     * @param nde the node to add to the list.
     */
    public void add(Node nde) {
        if (nde == null) return;
        nde.insertBefore(head);
        head = nde;
        size++;
    }
		
	/**
     * Removes nde from the list it is part of (should be this
     * one, otherwise results are undefined).  If nde is the
     * current head element, then the next element becomes head,
     * if there are no more elements the list becomes empty.
     * @param nde node to remove.
     */
    public void remove(Node nde) {
        if (nde == null) return;
        if (nde == head) {
            if (head.getNext() == head) 
                head = null;  // Last node...
            else
                head = head.getNext();
        }
        nde.unlink();
        size--;
    }

    /**
     * Removes 'head' from list and returns it. Returns null if list is empty.
     * @returns current head element, next element becomes head.
     */
    public Node pop() {
        if (head == null) return null;
			
        Node nde = head;
        remove(nde);
        return nde;
    }

    /**
     * Removes 'tail' from list and returns it. Returns null if list is empty.
     * @returns current tail element.
     */
    public Node unpush() {
        if (head == null) return null;
			
        Node nde = getTail();
        remove(nde);
        return nde;
    }



    /**
     * Adds <tt>nde</tt> to tail of list
     */
    public void push(Node nde) {
        nde.insertBefore(head);
        if (head == null) head = nde;
        size++;
    }

    /**
     * Adds <tt>nde</tt> to head of list
     */
    public void unpop(Node nde) {
        nde.insertBefore(head);
        head = nde;
        size++;
    }
}

