/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.Graphics2D;

/**
 * Interface expected from object that can perform an arbitrary
 * rendering sequence. This is used to generate SVG content and
 * compare it to a reference.
 *
 * @author <a href="vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface Painter {
    public void paint(Graphics2D g);
}
