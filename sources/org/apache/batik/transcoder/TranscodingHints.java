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

package org.apache.batik.transcoder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The <tt>TranscodingHints</tt> class defines a way to pass
 * transcoding parameters or options to any transcoders.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class TranscodingHints extends HashMap {

    /**
     * Constructs a new empty <tt>TranscodingHints</tt>.
     */
    public TranscodingHints() {
        this(null);
    }

    /**
     * Constructs a new <tt>TranscodingHints</tt> with keys and values
     * initialized from the specified Map object (which may be null).
     *
     * @param init a map of key/value pairs to initialize the hints
     *          or null if the object should be empty
     */
    public TranscodingHints(Map init) {
        super(7);
        if (init != null) {
            putAll(init);
        }
    }

    /**
     * Returns <tt>true</tt> if this <tt>TranscodingHints</tt> contains a
     * mapping for the specified key, false otherwise.
     *
     * @param key key whose present in this <tt>TranscodingHints</tt>
     * is to be tested.
     * @exception ClassCastException key is not of type
     * <tt>TranscodingHints.Key</tt>
     */
    public boolean containsKey(Object key) {
        return super.containsKey(key);
    }

    /**
     * Returns the value to which the specified key is mapped.
     *
     * @param key a trancoding hint key
     * @exception ClassCastException key is not of type
     * <tt>TranscodingHints.Key</tt>
     */
    public Object get(Object key) {
        return super.get(key);
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
                                               " incompatible with "+
                                               key);
        }
        return super.put(key, value);
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
        return super.remove(key);
    }

    /**
     * Copies all of the keys and corresponding values from the
     * specified <tt>TranscodingHints</tt> object to this
     * <tt>TranscodingHints</tt> object.
     */
    public void putAll(TranscodingHints hints) {
        super.putAll(hints);
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
            putAll(((TranscodingHints) m));
        } else {
            Iterator iter = m.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Defines the base type of all keys used to control various
     * aspects of the transcoding operations.
     */
    public abstract static class Key {

        /**
         * Construcst a key.
         */
        protected Key() { }

        /**
         * Returns true if the specified object is a valid value for
         * this key, false otherwise.
         */
        public abstract boolean isCompatibleValue(Object val);
    }
}
