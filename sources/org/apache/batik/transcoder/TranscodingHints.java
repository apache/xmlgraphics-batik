/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The <tt>TranscodingHints</tt> class defines a way to pass
 * transcoding parameters or options to any transcoders.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class TranscodingHints implements Map, Cloneable {

    /** The transcoding hints. */
    private HashMap hintMap = new HashMap(7);

    /**
     * Constructs a new empty <tt>TranscodingHints</tt>.
     */
    public TranscodingHints() {
    }

    /**
     * Constructs a new <tt>TranscodingHints</tt> with keys and values
     * initialized from the specified Map object (which may be null).
     *
     * @param init a map of key/value pairs to initialize the hints
     *          or null if the object should be empty
     */
    public TranscodingHints(Map init) {
        if (init != null) {
            hintMap.putAll(init);
        }
    }

    /**
     * Constructs a new <tt>TranscodingHints</tt> with the specified
     * key/value pair.
     *
     * @param key the key of the particular hint property
     * @param value the value of the hint property specified with
     * <tt>key</tt>
     */
    public TranscodingHints(Key key, Object value) {
        hintMap.put(key, value);
    }

    /**
     * Returns the number of key-value mappings in this
     * <tt>TranscodingHints</tt>.
     */
    public int size() {
        return hintMap.size();
    }

    /**
     * Returns <tt>true</tt> if this <tt>TranscodingHints</tt> contains
     * no key-value mappings, false otherwise.
     */
    public boolean isEmpty() {
        return hintMap.isEmpty();
    }

    /**
     * Returns <tt>true</tt> if this <tt>TranscodingHints</tt> contains a
     * mapping for the specified key, false otherwise.
     *
     * @param key key whose present in this <tt>TranscodingHints</tt>
     * is to be tested.
     */
    public boolean containsKey(Object key) {
        return hintMap.containsKey(key);
    }

    /**
     * Returns <tt>true</tt> if this <tt>TranscodingHints</tt> maps
     * one or more keys to the specified value.
     *
     * @param value value whose presence in this <tt>TranscodingHints</tt>
     * is to be tested.
     */
    public boolean containsValue(Object value) {
        return hintMap.containsValue(value);
    }

    /**
     * Returns the value to which the specified key is mapped.
     *
     * @param key a trancoding hint key
     * @exception ClassCastException key is not of type
     * <tt>TranscodingHints.Key</tt>
     */
    public Object get(Object key) {
        return hintMap.get((Key) key);
    }

    /**
     * Maps the specified <tt>key</tt> to the specified <tt>value</tt>
     * in this <tt>TranscodingHints</tt> object.
     *
     * @param key the trancoding hint key.
     * @param value the trancoding hint value.
     * @exception <tt>IllegalArgumentException</tt> value is not
     * appropriate for the specified key.
     * @exception ClassCastException key is not of type
     * <tt>TranscodingHints.Key</tt>
     */
    public Object put(Object key, Object value) {
        if (!((Key) key).isCompatibleValue(value)) {
            throw new IllegalArgumentException(value+
                                               " incompatible with"+
                                               key);
        }
        return hintMap.put((Key) key, value);
    }

    /**
     * Adds all of the keys and corresponding values from the
     * specified <tt>TranscodingHints</tt> object to this
     * <tt>TranscodingHints</tt> object.
     */
    public void add(TranscodingHints hints) {
        hintMap.putAll(hints);
    }

    /**
     * Clears this <tt>TranscodingHints</tt> object of all key/value pairs.
     */
    public void clear() {
        hintMap.clear();
    }

    /**
     * Removes the key and its corresponding value from this
     * <tt>TranscodingHints</tt> object.
     *
     * @param key the trancoding hints key that needs to be removed
     * @exception ClassCastException key is not of type
     * <tt>TranscodingHints.Key</tt>
     */
    public Object remove(Object key) {
        return hintMap.remove((Key) key);
    }

    /**
     * Copies all of the mappings from the specified <tt>Map</tt>
     * to this <tt>TranscodingHints</tt>.
     *
     * @param t mappings to be stored in this <tt>TranscodingHints</tt>.
     * @exception ClassCastException key is not of type
     * <tt>TranscodingHints.Key</tt>
     */
    public void putAll(Map m) {
        if (m instanceof TranscodingHints) {
            hintMap.putAll(((TranscodingHints) m).hintMap);
        } else {
            Iterator iter = m.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Returns a <tt>Set</tt> view of the Keys contained in this
     * <tt>TranscodingHints</tt>.
     */
    public Set keySet() {
        return hintMap.keySet();
    }

    /**
     * Returns a <tt>Collection</tt> view of the values contained in this
     * <tt>TranscodingHints</tt>.
     */
    public Collection values() {
        return hintMap.values();
    }

    /**
     * Returns a <tt>Set</tt> view of the mappings contained
     * in this <tt>TranscodingHints</tt>.
     */
    public Set entrySet() {
        return Collections.unmodifiableMap(hintMap).entrySet();
    }

    /**
     * Compares the specified <tt>Object</tt> with this
     * <tt>TranscodingHints</tt> for equality.
     */
    public boolean equals(Object o) {
        if (o instanceof TranscodingHints) {
            return hintMap.equals(((TranscodingHints) o).hintMap);
        } else if (o instanceof Map) {
            return hintMap.equals(o);
        }
        return false;
    }

    /**
     * Returns the hash code value for this <tt>TranscodingHints</tt>.
     */
    public int hashCode() {
        return hintMap.hashCode();
    }

    /**
     * Creates a clone of this <tt>TranscodingHints</tt> object that
     * has the same contents as this <tt>TranscodingHints</tt> object.
     */
    public Object clone() {
        TranscodingHints rh;
        try {
            rh = (TranscodingHints) super.clone();
            if (hintMap != null) {
                rh.hintMap = (HashMap) hintMap.clone();
            }
        } catch (CloneNotSupportedException e) {
            // should not be reached
            throw new InternalError();
        }
        return rh;
    }


    /**
     * Defines the base type of all keys used to control various
     * aspects of the transcoding operations. Instances of this class
     * are immutable and unique which means that tests for matches can
     * be made using the == operator instead of the more expensive
     * equals() method.
     */
    public abstract static class Key {

        private static Map identitymap = new HashMap(17);

        private String getIdentity() {
            return "Instance("+privatekey+") of "+getClass().getName();
        }

        private synchronized static void recordIdentity(Key k) {
            Object identity = k.getIdentity();
            if (identitymap.containsKey(identity)) {
                throw new IllegalArgumentException(identity+
                                                   " already registered");
            }
            identitymap.put(identity, k);
        }

        private int privatekey;

        /**
         * Construcst a key using the indicated private key.  Each
         * subclass of Key maintains its own unique domain of integer
         * keys. No two objects with the same integer key and of the
         * same specific subclass can be constructed.  An exception
         * will be thrown if an attempt is made to construct another
         * object of a given class with the same integer key as a
         * pre-existing instance of that subclass of Key.
         */
        protected Key(int privatekey) {
            this.privatekey = privatekey;
            recordIdentity(this);
        }

        /**
         * Returns true if the specified object is a valid value for
         * this key, false otherwise.
         */
        public abstract boolean isCompatibleValue(Object val);

        /**
         * Returns the private integer key that the subclass
         * instantiated this Key with.
         */
        protected final int intKey() {
            return privatekey;
        }

        /**
         * The hash code for all Key objects will be the same as the
         * system identity code of the object as defined by the
         * System.identityHashCode() method.
         */
        public final int hashCode() {
            return System.identityHashCode(this);
        }

        /**
         * The equals method for all Key objects will return the same
         * result as the equality operator '=='.
         */
        public final boolean equals(Object o) {
            return this == o;
        }
    }
}
