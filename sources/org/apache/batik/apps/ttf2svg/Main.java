/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.ttf2svg;

import org.apache.batik.svggen.font.SVGFont;

/**
 * This test runs the True Type Font to SVG Font converter, the 
 * tool that allows some characters from a font to be converted
 * to the SVG Font format.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class Main {
    public static void main(String[] args){
        SVGFont.main(args);
    }
}

