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

package org.apache.batik.ext.awt.image.rendered;

import org.apache.batik.util.DoublyLinkedList;

public class LRUCache {

    /**
     * Interface for object participating in the LRU Cache.  These
     * inform the object of key events in the status of the object in
     * the LRU cache.  
     */
	public interface LRUObj {
        /**
         * Called when the object first becomes active in the LRU cache.
         * @param nde The LRU cache node associated with this object.
         *            should be remembered so it can be returned by
         *            <tt>lruGet</tt>.  
         */
		public  void    lruSet(LRUNode nde);
        /**
         * Called to get the LRU node for this object.  Should return the
         * node passed in to lruSet.
         */
		public  LRUNode lruGet();
        /**
         * Called to inform the object that it is no longer in the cache.
         */
		public  void    lruRemove();
	}

    /**
     * Interface for nodes in the LRU cache, basicly nodes in a doubly
     * linked list.
     */
	public class LRUNode extends DoublyLinkedList.Node {
		private   LRUObj  obj  = null;
		public    LRUObj  getObj ()               { return obj; }
		protected void    setObj (LRUObj  newObj) { 
			if (obj != null) obj.lruRemove();

			obj = newObj;
			if (obj != null) obj.lruSet(this);
		}
	}

	private DoublyLinkedList free    = null;
	private DoublyLinkedList used    = null;
	private int     maxSize = 0;
		
	public LRUCache(int size) {
		if (size <= 0) size=1;
		maxSize = size;
		
		free = new DoublyLinkedList();
		used = new DoublyLinkedList();
		
		while (size > 0) {
			free.add(new LRUNode());
			size--;
		}
	}

	public int getUsed() {
		return used.getSize();
	}

	public synchronized void setSize(int newSz) {

		if (maxSize < newSz) {  // list grew...

			for (int i=maxSize; i<newSz; i++)
				free.add(new LRUNode());

		} else if (maxSize > newSz) {

			for (int i=used.getSize(); i>newSz; i--) {
				LRUNode nde = (LRUNode)used.getTail();
				used.remove(nde);
				nde.setObj(null);
			}
		}

		maxSize = newSz;
	}

	public synchronized void flush() {
		while (used.getSize() > 0) {
			LRUNode nde = (LRUNode)used.pop();
			nde.setObj(null);
			free.add(nde);
		}
	}

	public synchronized void remove(LRUObj obj) {
		LRUNode nde = obj.lruGet();
		if (nde == null) return;
		used.remove(nde);
		nde.setObj(null);
		free.add(nde);
	}

	public synchronized void touch(LRUObj obj) {
		LRUNode nde = obj.lruGet();
		if (nde == null) return;
		used.touch(nde);
	}

	public synchronized void add(LRUObj obj) {
		LRUNode nde = obj.lruGet();

		// already linked in...
		if (nde != null) {
			used.touch(nde);
			return;
		}

		if (free.getSize() > 0) {
			nde = (LRUNode)free.pop();
			nde.setObj(obj);
			used.add(nde);
		} else {
			nde = (LRUNode)used.getTail();
			nde.setObj(obj);
			used.touch(nde);
		}
	}

	protected synchronized void print() {
		System.out.println("In Use: " + used.getSize() +
						   " Free: " + free.getSize());
		LRUNode nde = (LRUNode)used.getHead();
        if (nde == null) return;
		do {
			System.out.println(nde.getObj());
			nde = (LRUNode)nde.getNext();
		} while (nde != used.getHead());
	}

}
