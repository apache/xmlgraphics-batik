/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.util.StringTokenizer;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;

import org.w3c.dom.svg.SVGRect;

/**
 * This class implements the {@link SVGRect} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMRect implements SVGRect, LiveAttributeValue {
    
    /**
     * The x coordinate of the rectangle.
     */
    protected float x;
    
    /**
     * The y coordinate of the rectangle.
     */
    protected float y;
    
    /**
     * The width of the rectangle.
     */
    protected float width;
    
    /**
     * The height of the rectangle.
     */
    protected float height;

    /**
     * The associated modification handler.
     */
    protected ModificationHandler modificationHandler;

    /**
     * Whether or not the current change is due to an internal change.
     */
    protected boolean internalChange;

    /**
     * Sets the associated attribute modifier.
     */
    public void setModificationHandler(ModificationHandler mh) {
        modificationHandler = mh;
    }

    /**
     * Returns the x coordinate of the rectangle.
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the x coordinate of the rectangle.
     */
    public void setX(float x) throws DOMException {
        this.x = x;
        if (modificationHandler != null) {
            internalChange = true;
            modificationHandler.valueChanged(this,
                                             Float.toString(x) + " " +
                                             Float.toString(y) + " " +
                                             Float.toString(width) + " " +
                                             Float.toString(height));
            internalChange = false;
        }
    }

    /**
     * Returns the x coordinate of the rectangle.
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the x coordinate of the rectangle.
     */
    public void setY(float y) throws DOMException {
        this.y = y;
        if (modificationHandler != null) {
            internalChange = true;
            modificationHandler.valueChanged(this,
                                             Float.toString(x) + " " +
                                             Float.toString(y) + " " +
                                             Float.toString(width) + " " +
                                             Float.toString(height));
            internalChange = false;
        }
    }

    /**
     * Returns the width of this rectangle.
     */
    public float getWidth() {
        return width;
    }

    /**
     * Sets the width of this rectangle.
     */
    public void setWidth(float width) throws DOMException {
        this.width = width;
        if (modificationHandler != null) {
            internalChange = true;
            modificationHandler.valueChanged(this,
                                             Float.toString(x) + " " +
                                             Float.toString(y) + " " +
                                             Float.toString(width) + " " +
                                             Float.toString(height));
            internalChange = false;
        }
    }

    /**
     * Returns the width of this rectangle.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Sets the height of this rectangle.
     */
    public void setHeight(float height) throws DOMException {
        this.width = width;
        if (modificationHandler != null) {
            internalChange = true;
            modificationHandler.valueChanged(this,
                                             Float.toString(x) + " " +
                                             Float.toString(y) + " " +
                                             Float.toString(width) + " " +
                                             Float.toString(height));
            internalChange = false;
        }
    }

    /**
     * Parses the given string.
     */
    public void parseValue(String val) {
        if (!internalChange) {
            try {
                StringTokenizer st = new StringTokenizer(val, " ,");
                if (!st.hasMoreTokens()) {
                    throw new DOMException(DOMException.SYNTAX_ERR, "");
                }
                float nx = Float.parseFloat(st.nextToken());
                if (!st.hasMoreTokens()) {
                    throw new DOMException(DOMException.SYNTAX_ERR, "");
                }
                float ny = Float.parseFloat(st.nextToken());
                if (!st.hasMoreTokens()) {
                    throw new DOMException(DOMException.SYNTAX_ERR, "");
                }
                float nw = Float.parseFloat(st.nextToken());
                if (!st.hasMoreTokens()) {
                    throw new DOMException(DOMException.SYNTAX_ERR, "");
                }
                float nh = Float.parseFloat(st.nextToken());
                x = nx;
                y = ny;
                width = nw;
                height = nh;
            } catch (NumberFormatException e) {
                throw new DOMException(DOMException.SYNTAX_ERR, e.getMessage());
            }
        }
    }

    // LiveAttributeValue ///////////////////////////////////////////////////

    /**
     * Called when the string representation of the value as been modified.
     * @param oldValue The old Attr node.
     * @param newValue The new Attr node.
     */
    public void valueChanged(Attr oldValue, Attr newValue) {
        parseValue(newValue.getValue());
    }
}
