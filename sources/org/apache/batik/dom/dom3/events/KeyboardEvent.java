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
 *
 * The original version of this file is available at:
 *   http://www.w3.org/TR/2003/NOTE-DOM-Level-3-Events-20031107/java-binding.zip
 */

package org.apache.batik.dom.dom3.events;

/**
 *  The <code>KeyboardEvent</code> interface provides specific contextual 
 * information associated with keyboard devices. Each keyboard event 
 * references a key using an identifier. Keyboard events are commonly 
 * directed at the element that has the focus. 
 * <p> The <code>KeyboardEvent</code> interface provides convenient attributes 
 * for some common modifiers keys: <code>KeyboardEvent.ctrlKey</code>, 
 * <code>KeyboardEvent.shiftKey</code>, <code>KeyboardEvent.altKey</code>, 
 * <code>KeyboardEvent.metaKey</code>. These attributes are equivalent to 
 * use the method 
 * <code>KeyboardEvent.getModifierState(keyIdentifierArg)</code> with 
 * "Control", "Shift", "Alt", or "Meta" respectively. 
 * <p> To create an instance of the <code>KeyboardEvent</code> interface, use 
 * the <code>DocumentEvent.createEvent("KeyboardEvent")</code> method call. 
 * <p>See also the <a href='http://www.w3.org/TR/2003/NOTE-DOM-Level-3-Events-20031107'>Document Object Model (DOM) Level 3 Events Specification</a>.
 * @since DOM Level 3
 */
public interface KeyboardEvent
        extends UIEvent, org.w3c.dom.events.KeyboardEvent {
}
