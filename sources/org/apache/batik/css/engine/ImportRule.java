/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine;

import java.net.URL;

/**
 * This class represents a @import CSS rule.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ImportRule extends MediaRule {
    
    /**
     * The type constant.
     */
    public final static short TYPE = (short)2;

    /**
     * The URI of the imported stylesheet.
     */
    protected URL uri;

    /**
     * Creates a new ImportRule.
     * @param ss The imported style-sheet.
     */

    /**
     * Returns a constant identifying the rule type.
     */
    public short getType() {
        return TYPE;
    }

    /**
     * Sets the URI of the imported stylesheet.
     */
    public void setURI(URL u) {
        uri = u;
    }

    /**
     * Returns the URI of the imported stylesheet.
     */
    public URL getURI() {
        return uri;
    }

    /**
     * Returns a printable representation of this import rule.
     */
    public String toString(CSSEngine eng) {
        StringBuffer sb = new StringBuffer();
        sb.append("@import \"");
        sb.append(uri);
        sb.append("\"");
        if (mediaList != null) {
            for (int i = 0; i < mediaList.getLength(); i++) {
                sb.append(' ');
                sb.append(mediaList.item(i));
            }
        }
        sb.append(";\n");
        return sb.toString();
    }
}
