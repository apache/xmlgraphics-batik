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
 *     - Removed methods and constants present in the DOM 2 interface.
 *
 * The original version of this file is available at:
 *   http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407/java-binding.zip
 */

package org.apache.batik.dom.dom3;

/**
 * DOM operations only raise exceptions in "exceptional" circumstances, i.e., 
 * when an operation is impossible to perform (either for logical reasons, 
 * because data is lost, or because the implementation has become unstable). 
 * In general, DOM methods return specific error values in ordinary 
 * processing situations, such as out-of-bound errors when using 
 * <code>NodeList</code>.
 * <p>Implementations should raise other exceptions under other circumstances. 
 * For example, implementations should raise an implementation-dependent 
 * exception if a <code>null</code> argument is passed when <code>null</code>
 *  was not expected.
 * <p>Some languages and object systems do not support the concept of 
 * exceptions. For such systems, error conditions may be indicated using 
 * native error reporting mechanisms. For some bindings, for example, 
 * methods may return error codes similar to those listed in the 
 * corresponding method descriptions.
 * <p>See also the <a href='http://www.w3.org/TR/2004/REC-DOM-Level-3-Core-20040407'>Document Object Model (DOM) Level 3 Core Specification</a>.
 */
public class DOMException extends org.w3c.dom.DOMException {

    public DOMException(short code, String message) {
        super(code, message);
    }

    public static final short VALIDATION_ERR            = 16;
    public static final short TYPE_MISMATCH_ERR         = 17;
}
