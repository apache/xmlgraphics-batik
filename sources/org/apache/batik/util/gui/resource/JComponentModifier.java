/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.gui.resource;

import javax.swing.JComponent;

/**
 * This interface must be implemented by actions which need
 * to have an access to their associated component(s)
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface JComponentModifier {
    /**
     * Gives a reference to a component to this object
     * @param comp the component associed with this object
     */
    void addJComponent(JComponent comp);
}
