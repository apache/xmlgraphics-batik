/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package org.apache.batik.svggen.font.table;

/**
 * Specifies access to glyph description classes, simple and composite.
 * @version $Id$
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 */
public interface GlyphDescription {
    public int getEndPtOfContours(int i);
    public byte getFlags(int i);
    public short getXCoordinate(int i);
    public short getYCoordinate(int i);
    public short getXMaximum();
    public short getXMinimum();
    public short getYMaximum();
    public short getYMinimum();
    public boolean isComposite();
    public int getPointCount();
    public int getContourCount();
    //  public int getComponentIndex(int c);
    //  public int getComponentCount();
}
