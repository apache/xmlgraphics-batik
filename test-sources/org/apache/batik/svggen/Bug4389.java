/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.ImageIcon;

/**
 * This test validates drawImage conversions.
 *
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class Bug4389 implements Painter {
    public void paint(Graphics2D g){
        ImageIcon image = new ImageIcon(ClassLoader.getSystemResource("org/apache/batik/svggen/resources/vangogh.png"));
        g.translate(40,40);
        g.drawImage(image.getImage(), new AffineTransform(), null);
    }
}
