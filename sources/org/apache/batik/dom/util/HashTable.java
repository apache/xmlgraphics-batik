/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.util;

/**
 * A simple hashtable, not synchronized, with fixed load factor.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public class HashTable {
    /**
     * The load factor
     */
    protected final static float LOAD_FACTOR = 0.75f;
	    
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
     * The resizing threshold
     */
    protected int threshold;
	    
    /**
     * Creates a new table.
     */
    public HashTable() {
	table     = new Entry[INITIAL_CAPACITY];
	threshold = (int)(INITIAL_CAPACITY * LOAD_FACTOR);
    }

    /**
     * Creates a copy of the given HashTable object.
     * @param t The table to copy.
     */
    public HashTable(HashTable t) {
	threshold = t.threshold;
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
	if (count++ >= threshold) {
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
	table     = new Entry[INITIAL_CAPACITY];
	threshold = (int)(INITIAL_CAPACITY * LOAD_FACTOR);
	count     = 0;
    }

    /**
     * Rehash the table
     */
    protected void rehash () {
	Entry[] oldTable = table;
	
	table     = new Entry[oldTable.length * 2 + 1];
	threshold = (int)(table.length * LOAD_FACTOR);
	
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
    protected static class Entry {
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
