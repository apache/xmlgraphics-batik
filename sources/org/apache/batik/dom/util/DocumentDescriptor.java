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

package org.apache.batik.dom.util;

import org.w3c.dom.Element;

import org.apache.batik.util.CleanerThread;

/**
 * This class contains informations about a document.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DocumentDescriptor {
	    
    /**
     * The table initial capacity
     */
    protected final static int INITIAL_CAPACITY = 101;

    /**
     * The underlying array
     */
    protected Entry[] table;
	    
    /**
     * The number of entries
     */
    protected int count;
	    
    /**
     * Creates a new table.
     */
    public DocumentDescriptor() {
	table = new Entry[INITIAL_CAPACITY];
    }

    /**
     * Returns the number of elements in the document.
     */
    public int getNumberOfElements() {
	synchronized (this) {
            return count;
        }
    }
    
    /**
     * Returns the location in the source file of the end element.
     * @return zero if the information is unknown.
     */
    public int getLocationLine(Element elt) {
        synchronized (this) {
            int hash = elt.hashCode() & 0x7FFFFFFF;
            int index = hash % table.length;
	
            for (Entry e = table[index]; e != null; e = e.next) {
                if (e.hash != hash) 
                    continue;
                Object o = e.get();
                if (o == elt) 
                    return e.locationLine;
            }
        }
        return 0;
    }
    
    /**
     * Sets the location in the source file of the end element.
     */
    public void setLocationLine(Element elt, int line) {
        synchronized (this) {
            int hash  = elt.hashCode() & 0x7FFFFFFF;
            int index = hash % table.length;
	
            for (Entry e = table[index]; e != null; e = e.next) {
                if (e.hash != hash) 
                    continue;
                Object o = e.get();
                if (o == elt)
                    e.locationLine = line;
            }
	
            // The key is not in the hash table
            int len = table.length;
            if (count++ >= (len * 3) >>> 2) {
                rehash();
                index = hash % table.length;
            }
	
            Entry e = new Entry(hash, elt, line, table[index]);
            table[index] = e;
        }
    }

    /**
     * Rehash the table
     */
    protected void rehash () {
	Entry[] oldTable = table;
	
	table = new Entry[oldTable.length * 2 + 1];
	
	for (int i = oldTable.length-1; i >= 0; i--) {
	    for (Entry old = oldTable[i]; old != null;) {
		Entry e = old;
		old = old.next;
		
		int index = e.hash % table.length;
		e.next = table[index];
		table[index] = e;
	    }
	}
    }

    protected void removeEntry(Entry e) {
        synchronized (this) {
            int hash = e.hash;
            int index = hash % table.length;
            Entry curr = table[index];
            Entry prev = null;
            while (curr != e) {
                prev = curr;
                curr = curr.next;
            }
            if (curr == null) return; // already remove???

            if (prev == null)
                // First entry.
                table[index] = curr.next;
            else 
                prev.next = curr.next;
            count--;
        }
    }

    /**
     * To manage collisions
     */
    protected class Entry extends CleanerThread.WeakReferenceCleared {
	/**
	 * The hash code
	 */
	public int hash;
	
	/**
	 * The line number.
	 */
	public int locationLine;
	
	/**
	 * The next entry
	 */
	public Entry next;
	
	/**
	 * Creates a new entry
	 */
	public Entry(int hash, Element element, int locationLine, Entry next) {
            super(element);
	    this.hash         = hash;
	    this.locationLine = locationLine;
	    this.next         = next;
	}

        public void cleared() {
            removeEntry(this);
        }
    }
}
