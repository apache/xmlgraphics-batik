/*
 * Copyright (c) 2004 World Wide Web Consortium,
 *
 * (Massachusetts Institute of Technology, European Research Consortium for
 * Informatics and Mathematics, Keio University). All Rights Reserved. This
 * work is distributed under the W3C(r) Software License [1] in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * [1] http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231
 *
 * Modifications:
 *   February 21, 2005
 *     - Moved interface to org.apache.batik.dom.dom3 package.
 *
 * The original version of this file is available at:
 *   http://www.w3.org/TR/2003/NOTE-DOM-Level-3-Events-20031107/java-binding.zip
 */

package org.apache.batik.dom.dom3;

/**
 * This interface permits a DOM implementer to supply one or more 
 * implementations, based upon requested features and versions, as specified 
 * in . Each implemented <code>DOMImplementationSource</code> object is 
 * listed in the binding-specific list of available sources so that its 
 * <code>DOMImplementation</code> objects are made available.
 * <p>See also the <a href='http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407'>Document Object Model (DOM) Level 3 Core Specification</a>.
 * @since DOM Level 3
 */
public interface DOMImplementationSource
        extends org.w3c.dom.DOMImplementationSource {
}
