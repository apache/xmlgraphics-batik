/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.transcoder;

import java.util.Map;
import java.util.HashMap;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderFactory;

/**
 * The <tt>TranscoderFactory</tt> implementation.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class ConcreteTranscoderFactory implements TranscoderFactory {

    static Map transcoderMap = new HashMap();

    static {
        transcoderMap.put("image/jpeg", JpegTranscoder.class);
        transcoderMap.put("image/jpg",  JpegTranscoder.class); // alias to jpeg
        transcoderMap.put("image/png",  PngTranscoder.class);
    }

    private static TranscoderFactory singleton =
        new ConcreteTranscoderFactory();

    /**
     * Returns the TranscoderFactory instance.
     */
    public static TranscoderFactory getTranscoderFactoryImplementation() {
        return singleton;
    }

    public Transcoder createTranscoder(String mimeType) {
        Class transcoderClass = (Class) transcoderMap.get(mimeType);
        if (transcoderClass == null) {
            return null;
        } else {
            try {
                return (Transcoder) transcoderClass.newInstance();
            } catch (Throwable th) {
                return null;
            }
        }
    }
}
