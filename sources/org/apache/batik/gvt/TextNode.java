/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.gvt;

import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;

/**
 * A graphics node that represents text.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface TextNode extends LeafGraphicsNode {

    /**
     * Sets the location of this raster image node.
     * @param newLocation the new location of this raster image node
     */
    void setLocation(Point2D newLocation);

    /**
     * Returns the location of this raster image node.
     * @return the location of this raster image node
     */
    Point2D getLocation();

    /**
     * Sets the attributed character iterator of this text node.
     * @param newAci the new attributed character iterator
     */
    void setAttributedCharacterIterator(AttributedCharacterIterator newAci);

    /**
     * Returns the attributed character iterator of this text node.
     * @return the attributed character iterator
     */
    AttributedCharacterIterator getAttributedCharacterIterator();

    /**
     * Defines where the text of a <tt>TextNode</tt> can be anchored
     * relative to its location.
     *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
     */
    public static final class Anchor implements java.io.Serializable {

        /**
         * The type of the START anchor.
         */
        public static final int ANCHOR_START  = 0;
        /**
         * The type of the MIDDLE anchor.
         */
        public static final int ANCHOR_MIDDLE = 1;
        /**
         * The type of the END anchor.
         */
        public static final int ANCHOR_END    = 2;

        /**
         * The anchor which enables the rendered characters to be
         * aligned such that the start of the text string is at the
         * initial current text location.
         */
        public static final Anchor START = new Anchor(ANCHOR_START);
        /**
         * The anchor which enables the rendered characters to be
         * aligned such that the middle of the text string is at the
         * initial current text location.
         */
        public static final Anchor MIDDLE = new Anchor(ANCHOR_MIDDLE);
        /**
         * The anchor which enables the rendered characters to be
         * aligned such that the end of the text string is at the
         * initial current text location.
         */
        public static final Anchor END = new Anchor(ANCHOR_END);

        private int type;

        /** No instance of this class. */
        private Anchor(int type) {
            this.type = type;
        }

        /**
         * Returns the type of this anchor.
         */
        public int getType() {
            return type;
        }

        /**
         * This is called by the serialization code before it returns
         * an unserialized object. To provide for unicity of
         * instances, the instance that was read is replaced by its
         * static equivalent. See the serialiazation specification for
         * further details on this method's logic.
         */
        private Object readResolve() throws java.io.ObjectStreamException {
            switch(type){
            case ANCHOR_START:
                return START;
            case ANCHOR_MIDDLE:
                return MIDDLE;
            case ANCHOR_END:
                return END;
            default:
                throw new Error("Unknown Anchor type");
            }
        }
    }
}
