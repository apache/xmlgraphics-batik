/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package org.apache.batik.svggen.font.table;

import java.io.ByteArrayInputStream;
import java.util.Vector;

/**
 * Glyph description for composite glyphs.  Composite glyphs are made up of one
 * or more simple glyphs, usually with some sort of transformation applied to
 * each.
 *
 * @version $Id$
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 */
public class GlyfCompositeDescript extends GlyfDescript {

    private Vector components = new Vector();

    public GlyfCompositeDescript(GlyfTable parentTable, ByteArrayInputStream bais) {
        super(parentTable, (short) -1, bais);
        
        // Get all of the composite components
        GlyfCompositeComp comp;
        int firstIndex = 0;
        int firstContour = 0;
        do {
            components.addElement(comp = new GlyfCompositeComp(firstIndex, firstContour, bais));
            firstIndex += parentTable.getDescription(comp.getGlyphIndex()).getPointCount();
            firstContour += parentTable.getDescription(comp.getGlyphIndex()).getContourCount();
        } while ((comp.getFlags() & GlyfCompositeComp.MORE_COMPONENTS) != 0);

        // Are there hinting intructions to read?
        if ((comp.getFlags() & GlyfCompositeComp.WE_HAVE_INSTRUCTIONS) != 0) {
            readInstructions(bais, (int)(bais.read()<<8 | bais.read()));
        }
    }

    public int getEndPtOfContours(int i) {
        GlyfCompositeComp c = getCompositeCompEndPt(i);
        if (c != null) {
            GlyphDescription gd = parentTable.getDescription(c.getGlyphIndex());
            return gd.getEndPtOfContours(i - c.getFirstContour()) + c.getFirstIndex();
        }
        return 0;
    }

    public byte getFlags(int i) {
        GlyfCompositeComp c = getCompositeComp(i);
        if (c != null) {
            GlyphDescription gd = parentTable.getDescription(c.getGlyphIndex());
            return gd.getFlags(i - c.getFirstIndex());
        }
        return 0;
    }

    public short getXCoordinate(int i) {
        GlyfCompositeComp c = getCompositeComp(i);
        if (c != null) {
            GlyphDescription gd = parentTable.getDescription(c.getGlyphIndex());
            int n = i - c.getFirstIndex();
            int x = gd.getXCoordinate(n);
            int y = gd.getYCoordinate(n);
            short x1 = (short) c.scaleX(x, y);
            x1 += c.getXTranslate();
            return (short) x1;
        }
        return 0;
    }

    public short getYCoordinate(int i) {
        GlyfCompositeComp c = getCompositeComp(i);
        if (c != null) {
            GlyphDescription gd = parentTable.getDescription(c.getGlyphIndex());
            int n = i - c.getFirstIndex();
            int x = gd.getXCoordinate(n);
            int y = gd.getYCoordinate(n);
            short y1 = (short) c.scaleY(x, y);
            y1 += c.getYTranslate();
            return (short) y1;
        }
        return 0;
    }

    public boolean isComposite() {
        return true;
    }

    public int getPointCount() {
        GlyfCompositeComp c = (GlyfCompositeComp) components.elementAt(components.size()-1);
        return c.getFirstIndex() + parentTable.getDescription(c.getGlyphIndex()).getPointCount();
    }

    public int getContourCount() {
        GlyfCompositeComp c = (GlyfCompositeComp) components.elementAt(components.size()-1);
        return c.getFirstContour() + parentTable.getDescription(c.getGlyphIndex()).getContourCount();
    }

    public int getComponentIndex(int i) {
        return ((GlyfCompositeComp)components.elementAt(i)).getFirstIndex();
    }

    public int getComponentCount() {
        return components.size();
    }

    protected GlyfCompositeComp getCompositeComp(int i) {
        GlyfCompositeComp c;
        for (int n = 0; n < components.size(); n++) {
            c = (GlyfCompositeComp) components.elementAt(n);
            GlyphDescription gd = parentTable.getDescription(c.getGlyphIndex());
            if (c.getFirstIndex() <= i && i < (c.getFirstIndex() + gd.getPointCount())) {
                return c;
            }
        }
        return null;
    }

    protected GlyfCompositeComp getCompositeCompEndPt(int i) {
        GlyfCompositeComp c;
        for (int j = 0; j < components.size(); j++) {
            c = (GlyfCompositeComp) components.elementAt(j);
            GlyphDescription gd = parentTable.getDescription(c.getGlyphIndex());
            if (c.getFirstContour() <= i && i < (c.getFirstContour() + gd.getContourCount())) {
                return c;
            }
        }
        return null;
    }
}
