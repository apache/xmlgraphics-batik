/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.gui;

import java.awt.GridBagConstraints;

/**
 * This class extends the java.awt.GridBagConstraints in order to
 * provide some utility methods.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ExtendedGridBagConstraints extends GridBagConstraints {
    /**
     * Modifies gridx, gridy, gridwidth, gridheight.
     * @param x The value for gridx.
     * @param y The value for gridy.
     * @param width The value for gridwidth.
     * @param height The value for gridheight.
     */
    public void setGridBounds(int x, int y, int width, int height) {
	gridx = x;
	gridy = y;
	gridwidth = width;
	gridheight = height;
    }
}
