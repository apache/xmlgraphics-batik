/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.gvt;

import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.GraphicsNodeRenderContext;
import org.apache.batik.gvt.event.EventDispatcher;

/**
 * Provides a way for a class to initialize a GraphicsNode and display
 * it using the <tt>GVTDemoLauncher</tt>.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public interface GVTDemoSetup {

    GraphicsNode createGraphicsNode();

    GraphicsNodeRenderContext createGraphicsContext();

    EventDispatcher createEventDispatcher();

}
