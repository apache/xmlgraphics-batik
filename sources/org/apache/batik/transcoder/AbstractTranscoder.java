/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.transcoder;

import java.awt.geom.Rectangle2D;
import java.awt.Paint;

/**
 * This class can be the base class of a transcoder which may support
 * transcoding hints and/or error handler.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public abstract class AbstractTranscoder extends TranscoderSupport
    implements Transcoder {
    
    /**
     * Constructs a new <tt>AbstractTranscoder</tt>.
     */
    protected AbstractTranscoder() {}

    // --------------------------------------------------------------------
    // Keys definition
    // --------------------------------------------------------------------

    /**
     * The image width key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_WIDTH</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Float</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">The width of the top most svg element</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specify the width of the image to create.</TD></TR>
     * </TABLE> */
    public static final TranscodingHints.Key KEY_WIDTH
        = new LengthKey(0);
    public static final String KEY_WIDTH_STR = "width";

    /**
     * The image height key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_HEIGHT</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Float</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">The height of the top most svg element</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specify the height of the image to create.</TD></TR>
     * </TABLE> */
    public static final TranscodingHints.Key KEY_HEIGHT
        = new LengthKey(1);
    public static final String KEY_HEIGHT_STR = "height";

    /**
     * The area of interest key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_AOI</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Rectangle2D</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">The document's size</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specify the area of interest to render. The
     * rectangle coordinates must be specified in pixels and in the
     * document coordinates system.</TD></TR>
     * </TABLE>
     */
    public static final TranscodingHints.Key KEY_AOI
        = new Rectangle2DKey(0);
    public static final String KEY_AOI_STR = "aoi";

    /**
     * The language key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_LANGUAGE</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">String</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">"en"</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specify the preferred language of the document.
     * </TD></TR>
     * </TABLE>
     */
    public static final TranscodingHints.Key KEY_LANGUAGE
        = new StringKey(0);
    public static final String KEY_LANGUAGE_STR = "language";

    /**
     * The user stylesheet URI key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_USER_STYLESHEET_URI</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">String</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">null</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specify the user style sheet.</TD></TR>
     * </TABLE>
     */
    public static final TranscodingHints.Key KEY_USER_STYLESHEET_URI
        = new StringKey(1);
    public static final String KEY_USER_STYLESHEET_URI_STR = "userStylesheet";

    /**
     * The pixel to millimeter conversion factor key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_PIXEL_TO_MM</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Float</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">0.33</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specify the pixel to millimeter conversion factor.
     * </TD></TR>
     * </TABLE>
     */
    public static final TranscodingHints.Key KEY_PIXEL_TO_MM
        = new FloatKey(0);
    public static final String KEY_PIXEL_TO_MM_STR = "pixelToMm";

    /**
     * A transcoding Key represented as a string.
     */
    public static class StringKey extends TranscodingHints.Key {
        public StringKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            return (v instanceof String);
        }
    }

    /**
     * A transcoding Key represented as a boolean.
     */
    public static class BooleanKey extends TranscodingHints.Key {
        public BooleanKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            return (v != null && v instanceof Boolean);
        }
    }

    /**
     * A transcoding Key represented as a float.
     */
    public static class FloatKey extends TranscodingHints.Key {
        public FloatKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            return (v instanceof Float);
        }
    }

    /**
     * A transcoding Key represented as a length.
     */
    public static class LengthKey extends TranscodingHints.Key {
        public LengthKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            return (v instanceof Float && ((Float)v).floatValue() > 0);
        }
    }

    /**
     * A transcoding Key represented as a background paint.
     */
    public static class PaintKey extends TranscodingHints.Key {
        public PaintKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            return (v instanceof Paint);
        }
    }

    /**
     * A transcoding Key represented as a rectangle.
     */
    public static class Rectangle2DKey extends TranscodingHints.Key {
        public Rectangle2DKey(int privatekey) {
            super(privatekey);
        }
        public boolean isCompatibleValue(Object v) {
            return (v instanceof Rectangle2D);
        }
    }
}
