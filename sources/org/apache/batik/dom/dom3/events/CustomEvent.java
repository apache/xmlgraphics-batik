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
 *  The <code>CustomEvent</code> interface gives access to the attributes 
 * <code>Event.currentTarget</code> and <code>Event.eventPhase</code>. It is 
 * intended to be used by the DOM Events implementation to access the 
 * underlying current target and event phase while dispatching a custom 
 * <code>Event</code> in the tree; it is also intended to be implemented, 
 * and <em>not used</em>, by DOM applications. 
 * <p> The methods contained in this interface are not intended to be used by 
 * a DOM application, especially during the dispatch on the 
 * <code>Event</code> object. Changing the current target or the current 
 * phase may result in unpredictable results of the event flow. The DOM 
 * Events implementation should ensure that both methods return the 
 * appropriate current target and phase before invoking each event listener 
 * on the current target to protect DOM applications from malicious event 
 * listeners. 
 * <p ><b>Note:</b>  If this interface is supported by the event object, 
 * <code>Event.isCustom()</code> must return <code>true</code>. 
 * <p>See also the <a href='http://www.w3.org/TR/2003/NOTE-DOM-Level-3-Events-20031107'>Document Object Model (DOM) Level 3 Events Specification</a>.
 * @since DOM Level 3
 */
public interface CustomEvent extends Event, org.w3c.dom.events.CustomEvent {
}
