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

package org.apache.batik.gvt.font;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

/**
 * This class represents a doubly indexed hash table, which holds
 * soft references to the contained glyph geometry informations.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @author <a href="mailto:tkormann@ilog.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class AWTGlyphGeometryCache {

    /**
     * The initial capacity
     */
    protected final static int INITIAL_CAPACITY = 71;

    /**
     * The underlying array
     */
    protected Entry[] table;

    /**
     * The number of entries
     */
    protected int count;

    /**
     * The reference queue.
     */
    protected ReferenceQueue referenceQueue = new ReferenceQueue();

    /**
     * Creates a new AWTGlyphGeometryCache.
     */
    public AWTGlyphGeometryCache() {
        table = new Entry[INITIAL_CAPACITY];
    }

    /**
     * Creates a new AWTGlyphGeometryCache.
     * @param c The inital capacity.
     */
    public AWTGlyphGeometryCache(int c) {
        table = new Entry[c];
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
    public Value get(char c) {
        int hash  = hashCode(c) & 0x7FFFFFFF;
        int index = hash % table.length;

        for (Entry e = table[index]; e != null; e = e.next) {
            if ((e.hash == hash) && e.match(c)) {
                return (Value)e.get();
            }
        }
        return null;
    }

    /**
     * Sets a new value for the given variable
     * @return the old value or null
     */
    public Value put(char c, Value value) {
        removeClearedEntries();

        int hash  = hashCode(c) & 0x7FFFFFFF;
        int index = hash % table.length;

        Entry e = table[index];
        if (e != null) {
            if ((e.hash == hash) && e.match(c)) {
                Object old = e.get();
                table[index] = new Entry(hash, c, value, e.next);
                return (Value)old;
            }
            Entry o = e;
            e = e.next;
            while (e != null) {
                if ((e.hash == hash) && e.match(c)) {
                    Object old = e.get();
                    e = new Entry(hash, c, value, e.next);
                    o.next = e;
                    return (Value)old;
                }

                o = e;
                e = e.next;
            }
        }

        // The key is not in the hash table
        int len = table.length;
        if (count++ >= (len * 3) >>> 2) {
            rehash();
            index = hash % table.length;
        }

        table[index] = new Entry(hash, c, value, table[index]);
        return null;
    }

    /**
     * Clears the table.
     */
    public void clear() {
        table = new Entry[INITIAL_CAPACITY];
        count = 0;
        referenceQueue = new ReferenceQueue();
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
    protected int hashCode(char c) {
        return c;
    }

    /**
     * Removes the cleared entries.
     */
    protected void removeClearedEntries() {
        Entry e;
        while ((e = (Entry)referenceQueue.poll()) != null) {
            int index = e.hash % table.length;
            Entry t = table[index];
            if (t == e) {
                table[index] = e.next;
            } else {
                loop: for (;t!=null;) {
                    Entry c = t.next;
                    if (c == e) {
                        t.next = e.next;
                        break loop;
                    }
                    t = c;
                }
            }
            count--;
        }
    }

    /**
     * The object that holds glyph geometry.
     */
    public static class Value {

        protected Shape outline;
        protected Rectangle2D gmB;
        protected Rectangle2D outlineBounds;

        /**
         * Constructs a new Value with the specified parameter.
         */
        public Value(Shape outline, Rectangle2D gmB) {
            this.outline = outline;
            this.outlineBounds = outline.getBounds2D();
            this.gmB = gmB;
        }

        /**
         * Returns the outline of the glyph.
         */
        public Shape getOutline() {
            return outline;
        }

        /**
         * Returns the bounds of the glyph according to its glyph metrics.
         */
        public Rectangle2D getBounds2D() {
            return gmB;
        }

        /**
         * Returns the bounds of the outline.
         */
        public Rectangle2D getOutlineBounds2D() {
            return outlineBounds;
        }
    }

    /**
     * To manage collisions
     */
    protected class Entry extends SoftReference {

        /**
         * The hash code
         */
        public int hash;

        /**
         * The character
         */
        public char c;

        /**
         * The next entry
         */
        public Entry next;

        /**
         * Creates a new entry
         */
        public Entry(int hash, char c, Value value, Entry next) {
            super(value, referenceQueue);
            this.hash  = hash;
            this.c  = c;
            this.next  = next;
        }

        /**
         * Whether this entry match the given keys.
         */
        public boolean match(char o2) {
            return (c == o2);
        }
    }
}
