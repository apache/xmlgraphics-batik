/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.experiment;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.bridge.SVGBridgeContext;
import org.apache.batik.util.SVGConstants;

/**
 * This class contains utility methods for experiments.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class Experiments {
    /**
     * The experiment namespace URI.
     */
    public final static String EXPERIMENT_NAMESPACE_URI =
        "http://xml.apache.org/batik/experiment";

    /**
     * This class does not need to be instantiated.
     */
    protected Experiments() {
    }

    /**
     * Registers the bridges created for experiments.
     */
    public static void registerExperimentBridges() {
        SVGOMDocument.registerCustomElementFactory(EXPERIMENT_NAMESPACE_URI,
                                                   SVGConstants.TAG_TEXT_PATH,
                                                   new TextPathElementFactory());
        SVGBridgeContext.registerGlobalBridge(EXPERIMENT_NAMESPACE_URI,
                                              SVGConstants.TAG_TEXT_PATH,
                                              new TextPathElementBridge());
    }
}
