/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine;

import org.w3c.css.sac.SACMediaList;

/**
 * This class represents a @media CSS rule.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class MediaRule extends StyleSheet implements Rule {
    
    /**
     * The type constant.
     */
    public final static short TYPE = (short)1;

    /**
     * The media list.
     */
    protected SACMediaList mediaList;

    /**
     * Returns a constant identifying the rule type.
     */
    public short getType() {
        return TYPE;
    }

    /**
     * Sets the media list.
     */
    public void setMediaList(SACMediaList ml) {
        mediaList = ml;
    }

    /**
     * Returns the media list.
     */
    public SACMediaList getMediaList() {
        return mediaList;
    }

    /**
     * Returns a printable representation of this media rule.
     */
    public String toString(CSSEngine eng) {
        StringBuffer sb = new StringBuffer();
        sb.append("@media");
        if (mediaList != null) {
            for (int i = 0; i < mediaList.getLength(); i++) {
                sb.append(' ');
                sb.append(mediaList.item(i));
            }
        }
        sb.append(" {\n");
        for (int i = 0; i < size; i++) {
            sb.append(((Rule)rules[i]).toString(eng));
        }
        sb.append("}\n");
        return sb.toString();
    }
}
