/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.util;

import org.w3c.dom.Element;

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
	return count;
    }
    
    /**
     * Returns the location in the source file of the end element.
     * @return zero if the information is unknown.
     */
    public int getLocationLine(Element elt) {
	int hash = elt.hashCode() & 0x7FFFFFFF;
	int index = hash % table.length;
	
	for (Entry e = table[index]; e != null; e = e.next) {
	    if ((e.hash == hash) && e.element.equals(elt)) {
		return e.locationLine;
	    }
	}
        return 0;
    }
    
    /**
     * Sets the location in the source file of the end element.
     */
    public void setLocationLine(Element elt, int line) {
	int hash  = elt.hashCode() & 0x7FFFFFFF;
	int index = hash % table.length;
	
	for (Entry e = table[index]; e != null; e = e.next) {
	    if ((e.hash == hash) && e.element.equals(elt)) {
		e.locationLine = line;
	    }
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
    protected static class Entry {
	/**
	 * The hash code
	 */
	public int hash;
	
	/**
	 * The element
	 */
	public Element element;
	
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
	    this.hash         = hash;
	    this.element      = element;
	    this.locationLine = locationLine;
	    this.next         = next;
	}
    }
}
