/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.svggen.font.table;

import java.io.ByteArrayInputStream;

/**
 * @version $Id$
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 */
public class GlyfCompositeComp {

    public static final short ARG_1_AND_2_ARE_WORDS = 0x0001;
    public static final short ARGS_ARE_XY_VALUES = 0x0002;
    public static final short ROUND_XY_TO_GRID = 0x0004;
    public static final short WE_HAVE_A_SCALE = 0x0008;
    public static final short MORE_COMPONENTS = 0x0020;
    public static final short WE_HAVE_AN_X_AND_Y_SCALE = 0x0040;
    public static final short WE_HAVE_A_TWO_BY_TWO = 0x0080;
    public static final short WE_HAVE_INSTRUCTIONS = 0x0100;
    public static final short USE_MY_METRICS = 0x0200;

    private int firstIndex;
    private int firstContour;
    private short argument1;
    private short argument2;
    private short flags;
    private short glyphIndex;
    private double xscale = 1.0;
    private double yscale = 1.0;
    private double scale01 = 0.0;
    private double scale10 = 0.0;
    private int xtranslate = 0;
    private int ytranslate = 0;
    private int point1 = 0;
    private int point2 = 0;

    protected GlyfCompositeComp(int firstIndex, int firstContour, ByteArrayInputStream bais) {
        this.firstIndex = firstIndex;
        this.firstContour = firstContour;
        flags = (short)(bais.read()<<8 | bais.read());
        glyphIndex = (short)(bais.read()<<8 | bais.read());

        // Get the arguments as just their raw values
        if ((flags & ARG_1_AND_2_ARE_WORDS) != 0) {
            argument1 = (short)(bais.read()<<8 | bais.read());
            argument2 = (short)(bais.read()<<8 | bais.read());
        } else {
            argument1 = (short) bais.read();
            argument2 = (short) bais.read();
        }

        // Assign the arguments according to the flags
        if ((flags & ARGS_ARE_XY_VALUES) != 0) {
            xtranslate = argument1;
            ytranslate = argument2;
        } else {
            point1 = argument1;
            point2 = argument2;
        }

        // Get the scale values (if any)
        if ((flags & WE_HAVE_A_SCALE) != 0) {
            int i = (short)(bais.read()<<8 | bais.read());
            xscale = yscale = (double) i / (double) 0x4000;
        } else if ((flags & WE_HAVE_AN_X_AND_Y_SCALE) != 0) {
            short i = (short)(bais.read()<<8 | bais.read());
            xscale = (double) i / (double) 0x4000;
            i = (short)(bais.read()<<8 | bais.read());
            yscale = (double) i / (double) 0x4000;
        } else if ((flags & WE_HAVE_A_TWO_BY_TWO) != 0) {
            int i = (short)(bais.read()<<8 | bais.read());
            xscale = (double) i / (double) 0x4000;
            i = (short)(bais.read()<<8 | bais.read());
            scale01 = (double) i / (double) 0x4000;
            i = (short)(bais.read()<<8 | bais.read());
            scale10 = (double) i / (double) 0x4000;
            i = (short)(bais.read()<<8 | bais.read());
            yscale = (double) i / (double) 0x4000;
        }
    }

    public int getFirstIndex() {
        return firstIndex;
    }

    public int getFirstContour() {
        return firstContour;
    }

    public short getArgument1() {
        return argument1;
    }

    public short getArgument2() {
        return argument2;
    }

    public short getFlags() {
        return flags;
    }

    public short getGlyphIndex() {
        return glyphIndex;
    }

    public double getScale01() {
        return scale01;
    }

    public double getScale10() {
        return scale10;
    }

    public double getXScale() {
        return xscale;
    }

    public double getYScale() {
        return yscale;
    }

    public int getXTranslate() {
        return xtranslate;
    }

    public int getYTranslate() {
        return ytranslate;
    }

    /**
     * Transforms an x-coordinate of a point for this component.
     * @param x The x-coordinate of the point to transform
     * @param y The y-coordinate of the point to transform
     * @return The transformed x-coordinate
     */
    public int scaleX(int x, int y) {
        return Math.round((float)(x * xscale + y * scale10));
    }

    /**
     * Transforms a y-coordinate of a point for this component.
     * @param x The x-coordinate of the point to transform
     * @param y The y-coordinate of the point to transform
     * @return The transformed y-coordinate
     */
    public int scaleY(int x, int y) {
        return Math.round((float)(x * scale01 + y * yscale));
    }
}
