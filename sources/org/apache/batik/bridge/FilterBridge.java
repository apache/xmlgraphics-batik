/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

/**
 * A tagging interface for the &lt;filter> element. Implementations of
 * this interface are able to bridge a specific filter, modeled by a
 * DOM element, to a concrete <tt>Filter<tt>.

 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface FilterBridge extends FilterPrimitiveBridge {

}
