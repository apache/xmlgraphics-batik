/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.gvt.font;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class holds
 * soft references to the contained glyph geometry information using a {@link java.util.concurrent.ConcurrentHashMap}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @author <a href="mailto:tkormann@ilog.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class AWTGlyphGeometryCache {

    /**
     * The initial capacity
     */
    protected static final int INITIAL_CAPACITY = 71;

    private static <T> T getIfSet(SoftReference<T> ref) {
        return ref == null ? null : ref.get();
    }

    /**
     * The underlying map
     */
    ConcurrentHashMap<Character, SoftReference<Value>> cache;

    /**
     * Creates a new AWTGlyphGeometryCache.
     */
    public AWTGlyphGeometryCache() {
        this(INITIAL_CAPACITY);
    }

    /**
     * Creates a new AWTGlyphGeometryCache.
     * @param c The inital capacity.
     */
    public AWTGlyphGeometryCache(int c) {
        cache = new ConcurrentHashMap<>(c);
    }

    /**
     * Returns the size of this table.
     */
    public int size() {
        return cache.size();
    }

    /**
     * Gets the value of a variable
     * @return the value or null
     */
    public Value get(char c) {
        return getIfSet(cache.get(c));
    }

    /**
     * Sets a new value for the given variable
     * @return the old value or null
     */
    public Value put(char c, Value value) {
        return getIfSet(cache.put(c, new SoftReference<>(value)));
    }

    /**
     * Clears the table.
     */
    public void clear() {
        cache.clear();
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
}
