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

/**
 * This class represents a doubly indexed hash table.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DoublyIndexedTable {
    
    /**
     * The initial capacity
     */
    protected final static int INITIAL_CAPACITY = 11;

    /**
     * The underlying array
     */
    protected Entry[] table;
	    
    /**
     * The number of entries
     */
    protected int count;
	    
    /**
     * Creates a new DoublyIndexedTable.
     */
    public DoublyIndexedTable() {
        table = new Entry[INITIAL_CAPACITY];
    }

    /**
     * Creates a new DoublyIndexedTable.
     * @param c The inital capacity.
     */
    public DoublyIndexedTable(int c) {
        table = new Entry[c];
    }

    /**
     * Returns the size of this table.
     */
    public int size() {
	return count;
    }
    
    /**
     * Puts a value in the table.
     * @return the old value or null
     */
    public Object put(Object o1, Object o2, Object value) {
        int hash  = hashCode(o1, o2) & 0x7FFFFFFF;
        int index = hash % table.length;
	
        for (Entry e = table[index]; e != null; e = e.next) {
            if ((e.hash == hash) && e.match(o1, o2)) {
                Object old = e.value;
                e.value = value;
                return old;
            }
        }
	
        // The key is not in the hash table
        int len = table.length;
        if (count++ >= (len * 3) >>> 2) {
            rehash();
            index = hash % table.length;
        }
            
        Entry e = new Entry(hash, o1, o2, value, table[index]);
        table[index] = e;
        return null;
    }

    /**
     * Gets the value of an entry
     * @return the value or null
     */
    public Object get(Object o1, Object o2) {
        int hash  = hashCode(o1, o2) & 0x7FFFFFFF;
        int index = hash % table.length;
	
        for (Entry e = table[index]; e != null; e = e.next) {
            if ((e.hash == hash) && e.match(o1, o2)) {
                return e.value;
            }
        }
        return null;
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

    /**
     * Computes a hash code corresponding to the given objects. 
     */
    protected int hashCode(Object o1, Object o2) {
        int result = (o1 == null) ? 0 : o1.hashCode();
        return result ^ ((o2 == null) ? 0 : o2.hashCode());
    }

    /**
     * To manage collisions
     */
    protected static class Entry {
	/**
	 * The hash code
	 */
	public int hash;
	
	/**
	 * The first key
	 */
	public Object key1;
	
	/**
	 * The second key
	 */
	public Object key2;
	
	/**
	 * The value
	 */
	public Object value;
	
	/**
	 * The next entry
	 */
	public Entry next;
	
	/**
	 * Creates a new entry
	 */
	public Entry(int hash, Object key1, Object key2,  Object value, Entry next) {
	    this.hash  = hash;
	    this.key1  = key1;
	    this.key2  = key2;
	    this.value = value;
	    this.next  = next;
	}

        /**
         * Whether this entry match the given keys.
         */
        public boolean match(Object o1, Object o2) {
            if (key1 != null) {
                if (!key1.equals(o1)) {
                    return false;
                }
            } else if (o1 != null) {
                return false;
            }
            if (key2 != null) {
                return key2.equals(o2);
            }
            return o2 == null;
        }
    }
}
