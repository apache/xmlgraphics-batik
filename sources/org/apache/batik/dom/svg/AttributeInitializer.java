/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.apache.batik.dom.util.DoublyIndexedTable;

/**
 * This class is used by elements to initialize and reset their attributes.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class AttributeInitializer {

    /**
     * The list of namespaces, prefixes and names.
     */
    protected String[] keys;

    /**
     * The length of keys.
     */
    protected int length;

    /**
     * The attribute values table.
     */
    protected DoublyIndexedTable values = new DoublyIndexedTable();
    
    /**
     * Creates a new AttributeInitializer.
     */
    public AttributeInitializer(int capacity) {
        keys = new String[capacity * 3];
    }

    /**
     * Adds a default attribute value to the initializer.
     * @param ns The attribute namespace URI.
     * @param prefix The attribute's name prefix, or null.
     * @param ln The attribute's local name.
     * @param val The attribute's default value.
     */
    public void addAttribute(String ns, String prefix, String ln, String val) {
        int len = keys.length;
        if (length == len) {
            String[] t = new String[len * 2];
            for (int i = len - 1; i >= 0; --i) {
                t[i] = keys[i];
            }
            keys = t;
        }
        keys[length++] = ns;
        keys[length++] = prefix;
        keys[length++] = ln;
        values.put(ns, ln, val);
    }

    /**
     * Initializes the attributes of the given element.
     */
    public void initializeAttributes(AbstractElement elt) {
        for (int i = length - 1; i >= 2; i -= 3) {
            resetAttribute(elt, keys[i - 2], keys[i - 1], keys[i]);
        }
    }

    /**
     * Resets an attribute of the given element to its default value.
     * @param elt The element to modify.
     * @param ns The attribute namespace URI.
     * @param prefix The attribute's name prefix.
     * @param ln The attribute's local name.
     * @return true if a default value is known for the given attribute and
     *         if it was resetted.
     */
    public boolean resetAttribute(AbstractElement elt,
                                  String ns, String prefix, String ln) {
        String val = (String)values.get(ns, ln);
        if (val == null) {
            return false;
        }
        if (prefix != null) {
            StringBuffer sb = new StringBuffer(prefix.length() + ln.length() + 1);
            sb.append(prefix).append(':').append(ln);
            ln = sb.toString();
        }
        elt.setUnspecifiedAttribute(ns, ln, val);
        return true;
    }
}
