/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.geom;

import java.awt.geom.Dimension2D;

/**
 * A Double precision implementation of the Dimension2D interface.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class Dimension2D_Double extends Dimension2D{

    double width, height;

    public Dimension2D_Double() {
    }

    public Dimension2D_Double(double width, double height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the width of this <code>Dimension</code> in double
     * precision.
     * @return the width of this <code>Dimension</code>.
     */
    public double getWidth(){
        return width;
    }

    /**
     * Returns the height of this <code>Dimension</code> in double
     * precision.
     * @return the height of this <code>Dimension</code>.
     */
    public double getHeight(){
        return height;
    }

    /**
     * Sets the size of this <code>Dimension</code> object to the
     * specified width and height.
     * This method is included for completeness, to parallel the
     * {@link java.awt.Component#getSize getSize} method of
     * {@link java.awt.Component}.
     * @param width  the new width for the <code>Dimension</code>
     * object
     * @param height  the new height for the <code>Dimension</code>
     * object
     */
    public void setSize(double width, double height){
        this.width = width;
        this.height = height;
    }
}
