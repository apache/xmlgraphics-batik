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

package org.apache.batik.transcoder.wmf;

/**
 * Contains the definitions of WMF constants.
 *
 * @author <a href="mailto:luano@asd.ie">Luan O'Carroll</a>
 * @version $Id$
 */
public interface WMFConstants
{
    public static final int META_ALDUS_APM              = 0x9ac6cdd7;

    public static final int META_DRAWTEXT               = 0x062F;
    public static final int META_SETBKCOLOR             = 0x0201;
    public static final int META_SETBKMODE              = 0x0102;
    public static final int META_SETMAPMODE             = 0x0103;
    public static final int META_SETROP2                = 0x0104;
    public static final int META_SETRELABS              = 0x0105;
    public static final int META_SETPOLYFILLMODE        = 0x0106;
    public static final int META_SETSTRETCHBLTMODE      = 0x0107;
    public static final int META_SETTEXTCHAREXTRA       = 0x0108;
    public static final int META_SETTEXTCOLOR           = 0x0209;
    public static final int META_SETTEXTJUSTIFICATION   = 0x020A;
    public static final int META_SETWINDOWORG           = 0x020B;
    public static final int META_SETWINDOWORG_EX        = 0x0000; // ???? LOOKS SUSPICIOUS
    public static final int META_SETWINDOWEXT           = 0x020C;
    public static final int META_SETVIEWPORTORG         = 0x020D;
    public static final int META_SETVIEWPORTEXT         = 0x020E;
    public static final int META_OFFSETWINDOWORG        = 0x020F;
    public static final int META_SCALEWINDOWEXT         = 0x0410;
    public static final int META_OFFSETVIEWPORTORG      = 0x0211;
    public static final int META_SCALEVIEWPORTEXT       = 0x0412;
    public static final int META_LINETO                 = 0x0213;
    public static final int META_MOVETO                 = 0x0214;
    public static final int META_EXCLUDECLIPRECT        = 0x0415;
    public static final int META_INTERSECTCLIPRECT      = 0x0416;
    public static final int META_ARC                    = 0x0817;
    public static final int META_ELLIPSE                = 0x0418;
    public static final int META_FLOODFILL              = 0x0419;
    public static final int META_PIE                    = 0x081A;
    public static final int META_RECTANGLE              = 0x041B;
    public static final int META_ROUNDRECT              = 0x061C;
    public static final int META_PATBLT                 = 0x061D;
    public static final int META_SAVEDC                 = 0x001E;
    public static final int META_SETPIXEL               = 0x041F;
    public static final int META_OFFSETCLIPRGN          = 0x0220;
    public static final int META_TEXTOUT                = 0x0521;
    public static final int META_BITBLT                 = 0x0922;
    public static final int META_STRETCHBLT             = 0x0B23;
    public static final int META_POLYGON                = 0x0324;
    public static final int META_POLYLINE               = 0x0325;
    public static final int META_ESCAPE                 = 0x0626;
    public static final int META_RESTOREDC              = 0x0127;
    public static final int META_FILLREGION             = 0x0228;
    public static final int META_FRAMEREGION            = 0x0429;
    public static final int META_INVERTREGION           = 0x012A;
    public static final int META_PAINTREGION            = 0x012B;
    public static final int META_SELECTCLIPREGION       = 0x012C;
    public static final int META_SELECTOBJECT           = 0x012D;
    public static final int META_SETTEXTALIGN           = 0x012E;
    public static final int META_CHORD                  = 0x0830;
    public static final int META_SETMAPPERFLAGS         = 0x0231;
    public static final int META_EXTTEXTOUT             = 0x0a32;
    public static final int META_SETDIBTODEV            = 0x0d33;
    public static final int META_SELECTPALETTE          = 0x0234;
    public static final int META_REALIZEPALETTE         = 0x0035;
    public static final int META_ANIMATEPALETTE         = 0x0436;
    public static final int META_SETPALENTRIES          = 0x0037;
    public static final int META_POLYPOLYGON            = 0x0538;
    public static final int META_RESIZEPALETTE          = 0x0139;
    public static final int META_DIBBITBLT              = 0x0940;
    public static final int META_DIBSTRETCHBLT          = 0x0b41;
    public static final int META_DIBCREATEPATTERNBRUSH  = 0x0142;
    public static final int META_STRETCHDIB             = 0x0f43;
    public static final int META_EXTFLOODFILL           = 0x0548;
    public static final int META_SETLAYOUT              = 0x0149;
    public static final int META_DELETEOBJECT           = 0x01f0;
    public static final int META_CREATEPALETTE          = 0x00f7;
    public static final int META_CREATEPATTERNBRUSH     = 0x01F9;
    public static final int META_CREATEPENINDIRECT      = 0x02FA;
    public static final int META_CREATEFONTINDIRECT     = 0x02FB;
    public static final int META_CREATEBRUSHINDIRECT    = 0x02FC;
    public static final int META_CREATEREGION           = 0x06FF;
    public static final int META_POLYBEZIER16           = 0x1000;
    public static final int META_CREATEBRUSH		  = 0x00F8;
    public static final int META_CREATEBITMAPINDIRECT	  = 0x02FD;
    public static final int META_CREATEBITMAP		  = 0x06FE;

    public static final int META_OBJ_WHITE_BRUSH        = 0;
    public static final int META_OBJ_LTGRAY_BRUSH       = 1;
    public static final int META_OBJ_GRAY_BRUSH         = 2;
    public static final int META_OBJ_DKGRAY_BRUSH       = 3;
    public static final int META_OBJ_BLACK_BRUSH        = 4;
    public static final int META_OBJ_NULL_BRUSH         = 5;
    public static final int META_OBJ_HOLLOW_BRUSH       = 5;
    public static final int META_OBJ_WHITE_PEN          = 6;
    public static final int META_OBJ_BLACK_PEN          = 7;
    public static final int META_OBJ_NULL_PEN           = 8;
    public static final int META_OBJ_OEM_FIXED_FONT     = 10;
    public static final int META_OBJ_ANSI_FIXED_FONT    = 11;
    public static final int META_OBJ_ANSI_VAR_FONT      = 12;
    public static final int META_OBJ_SYSTEM_FONT        = 13;
    public static final int META_OBJ_DEVICE_DEFAULT_FONT = 14;
    public static final int META_OBJ_DEFAULT_PALETTE    = 15;
    public static final int META_OBJ_SYSTEM_FIXED_FONT  = 16;
}
