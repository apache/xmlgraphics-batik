/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.engine;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

/**
 * This interface represents a DOM node which must be set as parent
 * of an imported node to allow a mecanism similar to the SVG <use>
 * element to work.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface CSSImportedElementRoot extends DocumentFragment {
    
    /**
     * Returns the parent of the imported element, from the CSS
     * point of view.
     */
    Element getCSSParentElement();

}
