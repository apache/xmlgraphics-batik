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
