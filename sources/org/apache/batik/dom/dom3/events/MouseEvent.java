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

import org.w3c.dom.views.AbstractView;

/**
 * The <code>MouseEvent</code> interface provides specific contextual 
 * information associated with Mouse events.
 * <p> In the case of nested elements mouse events are always targeted at the 
 * most deeply nested element. Ancestors of the targeted element may use 
 * bubbling to obtain notification of mouse events which occur within theirs 
 * descendent elements. 
 * <p> To create an instance of the <code>MouseEvent</code> interface, use the 
 * <code>DocumentEvent.createEvent("MouseEvent")</code> method call. 
 * <p ><b>Note:</b>  When initializing <code>MouseEvent</code> objects using 
 * <code>initMouseEvent</code> or <code>initMouseEventNS</code>, 
 * implementations should use the client coordinates <code>clientX</code> 
 * and <code>clientY</code> for calculation of other coordinates (such as 
 * target coordinates exposed by DOM Level 0 implementations). 
 * <p>See also the <a href='http://www.w3.org/TR/2003/NOTE-DOM-Level-3-Events-20031107'>Document Object Model (DOM) Level 3 Events Specification</a>.
 * @since DOM Level 2
 */
public interface MouseEvent extends UIEvent, org.w3c.dom.events.MouseEvent {

    /**
     *  The <code>initMouseEventNS</code> method is used to initialize the 
     * value of a <code>MouseEvent</code> object and has the same behavior 
     * as <code>UIEvent.initUIEventNS()</code>. 
     * @param namespaceURI  Refer to the <code>UIEvent.initUIEventNS()</code> 
     *   method for a description of this parameter. 
     * @param typeArg  Refer to the <code>UIEvent.initUIEventNS()</code> 
     *   method for a description of this parameter. 
     * @param canBubbleArg  Refer to the <code>UIEvent.initUIEventNS()</code> 
     *   method for a description of this parameter. 
     * @param cancelableArg  Refer to the <code>UIEvent.initUIEventNS()</code>
     *    method for a description of this parameter. 
     * @param viewArg  Refer to the <code>UIEvent.initUIEventNS()</code> 
     *   method for a description of this parameter. 
     * @param detailArg  Refer to the <code>UIEvent.initUIEventNS()</code> 
     *   method for a description of this parameter. 
     * @param screenXArg  Refer to the 
     *   <code>MouseEvent.initMouseEvent()</code> method for a description 
     *   of this parameter. 
     * @param screenYArg  Refer to the 
     *   <code>MouseEvent.initMouseEvent()</code> method for a description 
     *   of this parameter. 
     * @param clientXArg  Refer to the 
     *   <code>MouseEvent.initMouseEvent()</code> method for a description 
     *   of this parameter. 
     * @param clientYArg  Refer to the 
     *   <code>MouseEvent.initMouseEvent()</code> method for a description 
     *   of this parameter. 
     * @param buttonArg  Refer to the <code>MouseEvent.initMouseEvent()</code>
     *    method for a description of this parameter. 
     * @param relatedTargetArg  Refer to the 
     *   <code>MouseEvent.initMouseEvent()</code> method for a description 
     *   of this parameter. 
     * @param modifiersList  A <a href='http://www.w3.org/TR/2000/REC-xml-20001006#NT-S'>white space</a> separated list of modifier key identifiers to be activated on this 
     *   object. As an example, <code>"Control Alt"</code> will activated 
     *   the control and alt modifiers. 
     * @since DOM Level 3
     */
    public void initMouseEventNS(String namespaceURI, 
                                 String typeArg, 
                                 boolean canBubbleArg, 
                                 boolean cancelableArg, 
                                 AbstractView viewArg, 
                                 int detailArg, 
                                 int screenXArg, 
                                 int screenYArg, 
                                 int clientXArg, 
                                 int clientYArg, 
                                 short buttonArg, 
                                 org.w3c.dom.events.EventTarget relatedTargetArg,
                                 String modifiersList);
}
