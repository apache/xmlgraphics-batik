/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.transcoder;

import org.apache.batik.transcoder.TranscodingHints;

/**
 * The default transcoder key and values.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class BatikHints {

    public static final TranscodingHints.Key KEY_XML_PARSER_CLASSNAME =
            new TranscodingHints.Key() {
        public boolean isCompatibleValue(Object v) {
            return (v instanceof String);
        }
    };

    public static final TranscodingHints.Key KEY_BACKGROUND =
            new TranscodingHints.Key() {
        public boolean isCompatibleValue(Object v) {
            return (v instanceof java.awt.Paint);
        }
    };

    public static final TranscodingHints.Key KEY_GVT_BUILDER =
            new TranscodingHints.Key() {
        public boolean isCompatibleValue(Object v) {
            return (v instanceof org.apache.batik.bridge.GVTBuilder);
        }
    };

    public static final TranscodingHints.Key KEY_DEFAULT_VIEWPORT =
            new TranscodingHints.Key() {
        public boolean isCompatibleValue(Object v) {
            return (v instanceof org.apache.batik.bridge.Viewport);
        }
    };

}

