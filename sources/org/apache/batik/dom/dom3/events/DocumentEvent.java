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
 *  The <code>DocumentEvent</code> interface provides a mechanism by which the 
 * user can create an <code>Event</code> object of a type supported by the 
 * implementation. If the feature "Events" is supported by the 
 * <code>Document</code> object, the <code>DocumentEvent</code> interface 
 * must be implemented on the same object. If the feature "+Events" is 
 * supported by the <code>Document</code> object, an object that supports 
 * the <code>DocumentEvent</code> interface must be returned by invoking the 
 * method <code>Node.getFeature("+Events", "3.0")</code> on the 
 * <code>Document</code> object. 
 * <p>See also the <a href='http://www.w3.org/TR/2003/NOTE-DOM-Level-3-Events-20031107'>Document Object Model (DOM) Level 3 Events Specification</a>.
 * @since DOM Level 2
 */
public interface DocumentEvent extends org.w3c.dom.events.DocumentEvent {

    /**
     *  Test if the implementation can generate events of a specified type. 
     * @param namespaceURI  Specifies the <code>Event.namespaceURI</code> of 
     *   the event. 
     * @param type  Specifies the <code>Event.type</code> of the event. 
     * @return  <code>true</code> if the implementation can generate and 
     *   dispatch this event type, <code>false</code> otherwise. 
     * @since DOM Level 3
     */
    public boolean canDispatch(String namespaceURI, 
                               String type);
}
