/*

   Copyright 2000  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.dom.util;

/**
 * This class represents a stack of HashTable objects.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class HashTableStack {
    /**
     * The current link.
     */
    protected Link current = new Link(null);
    
    /**
     * Creates a new HashTableStack object.
     */
    public HashTableStack() {
    }

    /**
     * Pushes a new table on the stack.
     */
    public void push() {
	current = new Link(current);
    }

    /**
     * Removes the table on the top of the stack.
     */
    public void pop() {
	current = current.next;
    }

    /**
     * Creates a mapping in the table on the top of the stack.
     */
    public String put(String s, Object o) {
	return (String)current.table.put(s, o);
    }
    
    /**
     * Gets an item in the table on the top of the stack.
     */
    public String get(String s) {
	for (Link l = current; l != null; l = l.next) {
	    String uri = (String)l.table.get(s);
	    if (uri != null) {
		return uri;
	    }
	}
	return null;
    }
	
    /**
     * To store the hashtables.
     */
    protected static class Link {
	/**
	 * The table.
	 */
	public HashTable table;
	
	/**
	 * The next link.
	 */
	public Link next;
	
	/**
	 * Creates a new link.
	 */
	public Link(Link n) {
	    table = new HashTable();
	    next  = n;
	}
    }
}
