/*
 * Copyright (c) 2003 World Wide Web Consortium,
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
 *     - Moved interface to org.apache.batik.dom.dom3.events package.
 *     - Removed methods and constants present in the DOM 2 interface.
 *
 * The original version of this file is available at:
 *   http://www.w3.org/TR/2003/NOTE-DOM-Level-3-Events-20031107/java-binding.zip
 */

package org.apache.batik.dom.dom3.events;

/**
 *  The <code>EventListener</code> interface is the primary way for handling 
 * events. Users implement the <code>EventListener</code> interface and 
 * register their event listener on an <code>EventTarget</code>. The users 
 * should also remove their <code>EventListener</code> from its 
 * <code>EventTarget</code> after they have completed using the listener. 
 * <p> Copying a <code>Node</code>, with methods such as 
 * <code>Node.cloneNode</code> or <code>Range.cloneContents</code>, does not 
 * copy the event listeners attached to it. Event listeners must be attached 
 * to the newly created <code>Node</code> afterwards if so desired. 
 * <p> Moving a <code>Node</code>, with methods <code>Document.adoptNode</code>
 * , <code>Node.appendChild</code>, or <code>Range.extractContents</code>, 
 * does not affect the event listeners attached to it. 
 * <p>See also the <a href='http://www.w3.org/TR/2003/NOTE-DOM-Level-3-Events-20031107'>Document Object Model (DOM) Level 3 Events Specification</a>.
 * @since DOM Level 2
 */
public interface EventListener extends org.w3c.dom.events.EventListener {
}
