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

import java.io.Serializable;

/**
 * A simple hashtable, not synchronized, with fixed load factor.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public class HashTable implements Serializable {
	    
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
     * Creates a new table.
     */
    public HashTable() {
	table = new Entry[INITIAL_CAPACITY];
    }

    /**
     * Creates a new table.
     * @param c The initial capacity.
     */
    public HashTable(int c) {
	table = new Entry[c];
    }

    /**
     * Creates a copy of the given HashTable object.
     * @param t The table to copy.
     */
    public HashTable(HashTable t) {
	count = t.count;
	table = new Entry[t.table.length];
	for (int i = 0; i < table.length; i++) {
	    Entry e = t.table[i];
	    Entry n = null;
	    if (e != null) {
		n = new Entry(e.hash, e.key, e.value, null);
		table[i] = n;
		e = e.next;
		while (e != null) {
		    n.next = new Entry(e.hash, e.key, e.value, null);
		    n = n.next;
		    e = e.next;
		}
	    }
	}
    }

    /**
     * Returns the size of this table.
     */
    public int size() {
	return count;
    }
    
    /**
     * Gets the value of a variable
     * @return the value or null
     */
    public Object get(Object key) {
	int hash  = key.hashCode() & 0x7FFFFFFF;
	int index = hash % table.length;
	
	for (Entry e = table[index]; e != null; e = e.next) {
	    if ((e.hash == hash) && e.key.equals(key)) {
		return e.value;
	    }
	}
	return null;
    }
    
    /**
     * Sets a new value for the given variable
     * @return the old value or null
     */
    public Object put(Object key, Object value) {
	int hash  = key.hashCode() & 0x7FFFFFFF;
	int index = hash % table.length;
	
	for (Entry e = table[index]; e != null; e = e.next) {
	    if ((e.hash == hash) && e.key.equals(key)) {
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
	
	Entry e = new Entry(hash, key, value, table[index]);
	table[index] = e;
	return null;
    }

    /**
     * Removes an entry from the table.
     * @return the value or null.
     */
    public Object remove(Object key) {
	int hash  = key.hashCode() & 0x7FFFFFFF;
	int index = hash % table.length;
	
	Entry p = null;
	for (Entry e = table[index]; e != null; e = e.next) {
	    if ((e.hash == hash) && e.key.equals(key)) {
		Object result = e.value;
		if (p == null) {
		    table[index] = e.next;
		} else {
		    p.next = e.next;
		}
		count--;
		return result;
	    }
	    p = e;
	}
	return null;
    }

    /**
     * Returns the key at the given position or null.
     */
    public Object key(int index) {
	if (index < 0 || index >= count) {
	    return null;
	}
	int j = 0;
	for (int i = 0; i < table.length; i++) {
	    Entry e = table[i];
	    if (e == null) {
		continue;
	    }
	    do {
		if (j++ == index) {
		    return e.key;
		}
		e = e.next;
	    } while (e != null);
	}
	return null;
    }

    /**
     * Returns the item at the given position.
     */
    public Object item(int index) {
	if (index < 0 || index >= count) {
	    return null;
	}
	int j = 0;
	for (int i = 0; i < table.length; i++) {
	    Entry e = table[i];
	    if (e == null) {
		continue;
	    }
	    do {
		if (j++ == index) {
		    return e.value;
		}
		e = e.next;
	    } while (e != null);
	}
	return null;
    }

    /**
     * Clears the map.
     */
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
	count = 0;
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
     * To manage collisions
     */
    protected static class Entry implements Serializable {
	/**
	 * The hash code
	 */
	public int hash;
	
	/**
	 * The key
	 */
	public Object key;
	
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
	public Entry(int hash, Object key, Object value, Entry next) {
	    this.hash  = hash;
	    this.key   = key;
	    this.value = value;
	    this.next  = next;
	}
    }
}
