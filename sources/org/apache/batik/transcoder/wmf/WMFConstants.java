/*

   Copyright 1999-2003  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

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
    public static final int META_CREATEBRUSH            = 0x00F8;
    public static final int META_CREATEBITMAPINDIRECT   = 0x02FD;
    public static final int META_CREATEBITMAP           = 0x06FE;
    
    public static final int META_OBJ_WHITE_BRUSH         = 0;
    public static final int META_OBJ_LTGRAY_BRUSH        = 1;
    public static final int META_OBJ_GRAY_BRUSH          = 2;
    public static final int META_OBJ_DKGRAY_BRUSH        = 3;
    public static final int META_OBJ_BLACK_BRUSH         = 4;
    public static final int META_OBJ_NULL_BRUSH          = 5;
    public static final int META_OBJ_HOLLOW_BRUSH        = 5;
    public static final int META_OBJ_WHITE_PEN           = 6;
    public static final int META_OBJ_BLACK_PEN           = 7;
    public static final int META_OBJ_NULL_PEN            = 8;
    public static final int META_OBJ_OEM_FIXED_FONT      = 10;
    public static final int META_OBJ_ANSI_FIXED_FONT     = 11;
    public static final int META_OBJ_ANSI_VAR_FONT       = 12;
    public static final int META_OBJ_SYSTEM_FONT         = 13;
    public static final int META_OBJ_DEVICE_DEFAULT_FONT = 14;
    public static final int META_OBJ_DEFAULT_PALETTE     = 15;
    public static final int META_OBJ_SYSTEM_FIXED_FONT   = 16;
    
    /* New StretchBlt() Modes */    
    public static final int STRETCH_BLACKONWHITE = 1;
    public static final int STRETCH_WHITEONBLACK = 2;
    public static final int STRETCH_COLORONCOLOR = 3;
    public static final int STRETCH_HALFTONE = 4;
    public static final int STRETCH_ANDSCANS = 1;
    public static final int STRETCH_ORSCANS = 2;
    public static final int STRETCH_DELETESCANS = 3;    
    
    // new values for PATBLT value

    /** new constant for PATBLT. 
     */  
    public static final int META_PATCOPY                = 0x00F00021;
    /** new constant for PATBLT. 
     */    
    public static final int META_PATINVERT              = 0x005A0049;
    /** new constant for PATBLT. 
     */    
    public static final int META_DSTINVERT              = 0x00550009;
    /** new constant for PATBLT. 
     */    
    public static final int META_BLACKNESS              = 0x00000042;
    /** new constant for PATBLT. 
     */    
    public static final int META_WHITENESS              = 0x00FF0062;
    
    // new constants for pen styles 
    public static final int META_PS_SOLID = 0;
    public static final int META_PS_DASH = 1;
    public static final int META_PS_DOT = 2;
    public static final int META_PS_DASHDOT = 3;
    public static final int META_PS_DASHDOTDOT = 4;
    public static final int META_PS_NULL = 5;
    public static final int META_PS_INSIDEFRAME = 6;

    // new constants for charsets 
    
    /** ANSI charset WMF ID.
     */
    public static final int META_CHARSET_ANSI = 0;

    /** DEFAULT charset WMF ID.
     */
    public static final int META_CHARSET_DEFAULT = 1;

    /** SYMBOL charset WMF ID.
     */
    public static final int META_CHARSET_SYMBOL = 2;

    /** GREEK charset WMF ID.
     */
    public static final int META_CHARSET_GREEK = 161;

    /** HEBREW charset WMF ID.
     */
    public static final int META_CHARSET_HEBREW = 177;

    /** ARABIC charset WMF ID.
     */
    public static final int META_CHARSET_ARABIC = 178;

    /** RUSSIAN (CYRILLIC) charset WMF ID.
     */
    public static final int META_CHARSET_RUSSIAN = 204;
    
    // new constants for charset names, useful for decoding and encoding text.

    /** ANSI charset Java name, ie "ISO-8859-1" charset.
     */
    public static final String CHARSET_ANSI = "ISO-8859-1";

    /** DEFAULT charset Java name, by default taken as "US-ASCII" charset.
     */    
    public static final String CHARSET_DEFAULT = "US-ASCII";

    /** GREEK charset Java name, ie "windows-1253" charset.
     */    
    public static final String CHARSET_GREEK = "windows-1253";

    /** CYRILLIC charset Java name, ie "windows-1251" charset.
     */        
    public static final String CHARSET_CYRILLIC = "windows-1251";

    /** HEBREW charset Java name, ie "windows-1255" charset.
     */            
    public static final String CHARSET_HEBREW = "windows-1255";

    /** ARABIC charset Java name, ie "windows-1256" charset.
     */                
    public static final String CHARSET_ARABIC = "windows-1256";    
    
    /** conversion from inches to Millimeters
     */
    public static final float INCH_TO_MM = 25.4f;
        
    /** number of inches default values
     */
    public static final int DEFAULT_INCH_VALUE = 576;
    
    // constants concerning map modes
    public static final int MM_TEXT = 1;
    public static final int MM_LOMETRIC = 2;
    public static final int MM_HIMETRIC = 3;
    public static final int MM_LOENGLISH = 4;
    public static final int MM_HIENGLISH = 5;
    public static final int MM_HITWIPS = 6;
    public static final int MM_ISOTROPIC = 7;
    public static final int MM_ANISOTROPIC = 8;
    
    // other WMF constants.
    public static final int BS_SOLID = 0;
    public static final int BS_HOLLOW = 1;
    public static final int BS_NULL = 1;
    public static final int BS_HATCHED = 2;
    public static final int BS_PATTERN = 3;
    public static final int BS_DIBPATTERN = 5;
    public static final int HS_HORIZONTAL = 0;
    public static final int HS_VERTICAL = 1;
    public static final int HS_FDIAGONAL = 2;
    public static final int HS_BDIAGONAL = 3;
    public static final int HS_CROSS = 4;
    public static final int HS_DIAGCROSS = 5;
    public static final int DIB_RGB_COLORS = 0;
    public static final int DIB_PAL_COLORS = 1;
    public static final int FW_DONTCARE = 100;
    public static final int FW_THIN = 100;
    public static final int FW_NORMAL = 400;
    public static final int FW_BOLD = 700;
    public static final int FW_BLACK = 900;
    public static final byte ANSI_CHARSET = 0;
    public static final byte DEFAULT_CHARSET = 1;
    public static final byte SYMBOL_CHARSET = 2;
    public static final byte SHIFTJIS_CHARSET = -128;
    public static final byte OEM_CHARSET = -1;
    public static final byte OUT_DEFAULT_PRECIS = 0;
    public static final byte OUT_STRING_PRECIS = 1;
    public static final byte OUT_CHARACTER_PRECIS = 2;
    public static final byte OUT_STROKE_PRECIS = 3;
    public static final byte OUT_TT_PRECIS = 4;
    public static final byte OUT_DEVICE_PRECIS = 5;
    public static final byte OUT_RASTER_PRECIS = 6;
    public static final byte CLIP_DEFAULT_PRECIS = 0;
    public static final byte CLIP_CHARACTER_PRECIS = 1;
    public static final byte CLIP_STROKE_PRECIS = 2;
    public static final byte CLIP_MASK = 15;
    public static final byte CLIP_LH_ANGLES = 16;
    public static final byte CLIP_TT_ALWAYS = 32;
    public static final byte DEFAULT_QUALITY = 0;
    public static final byte DRAFT_QUALITY = 1;
    public static final byte PROOF_QUALITY = 2;
    public static final byte DEFAULT_PITCH = 0;
    public static final byte FIXED_PITCH = 1;
    public static final byte VARIABLE_PITCH = 2;
    public static final byte FF_DONTCARE = 0;
    public static final byte FF_ROMAN = 16;
    public static final byte FF_SWISS = 32;
    public static final byte FF_MODERN = 48;
    public static final byte FF_SCRIPT = 64;
    public static final byte FF_DECORATIVE = 80;
    public static final int TRANSPARENT = 1;
    public static final int OPAQUE = 2;
    public static final int ALTERNATE = 1;
    public static final int WINDING = 2;
    public static final int TA_TOP = 0;
    public static final int TA_BOTTOM = 8;
    public static final int TA_BASELINE = 24;
    public static final int TA_LEFT = 0;
    public static final int TA_RIGHT = 2;
    public static final int TA_CENTER = 6;
    public static final int TA_NOUPDATECP = 0;
    public static final int TA_UPDATECP = 1;
    public static final int R2_BLACK = 1;
    public static final int R2_NOTMERGEPEN = 2;
    public static final int R2_MASKNOTPENNOT = 3;
    public static final int R2_NOTCOPYPEN = 4;
    public static final int R2_MASKPENNOT = 5;
    public static final int R2_NOT = 6;
    public static final int R2_XORPEN = 7;
    public static final int R2_NOTMASKPEN = 8;
    public static final int R2_MASKPEN = 9;
    public static final int R2_NOTXORPEN = 10;
    public static final int R2_NOP = 11;
    public static final int R2_MERGENOTPEN = 12;
    public static final int R2_COPYPEN = 13;
    public static final int R2_MERGEPENNOT = 14;
    public static final int R2_MERGEPEN = 15;
    public static final int R2_WHITE = 16;
    public static final int ETO_OPAQUE = 2;
    public static final int ETO_CLIPPED = 4;
    public static final int BLACKNESS = 66;
    public static final int NOTSRCERASE = 0x1100a6;
    public static final int NOTSRCCOPY = 0x330008;
    public static final int SRCERASE = 0x440328;
    public static final int DSTINVERT = 0x550009;
    public static final int PATINVERT = 0x5a0049;
    public static final int SRCINVERT = 0x660046;
    public static final int SRCAND = 0x8800c6;
    public static final int MERGEPAINT = 0xbb0226;
    public static final int SRCCOPY = 0xcc0020;
    public static final int SRCPAINT = 0xee0086;
    public static final int PATCOPY = 0xf00021;
    public static final int PATPAINT = 0xfb0a09;
    public static final int WHITENESS = 0xff0062;        
}
