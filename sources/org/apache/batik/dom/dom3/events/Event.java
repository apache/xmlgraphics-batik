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
 *  The <code>Event</code> interface is used to provide contextual information 
 * about an event to the listener processing the event. An object which 
 * implements the <code>Event</code> interface is passed as the parameter to 
 * an <code>EventListener</code>. More specific context information is 
 * passed to event listeners by deriving additional interfaces from 
 * <code>Event</code> which contain information directly relating to the 
 * type of event they represent. These derived interfaces are also 
 * implemented by the object passed to the event listener. 
 * <p> To create an instance of the <code>Event</code> interface, use the 
 * <code>DocumentEvent.createEvent("Event")</code> method call. 
 * <p>See also the <a href='http://www.w3.org/TR/2003/NOTE-DOM-Level-3-Events-20031107'>Document Object Model (DOM) Level 3 Events Specification</a>.
 * @since DOM Level 2
 */
public interface Event extends org.w3c.dom.events.Event {

    /**
     *  The namespace URI associated with this event at creation time, or 
     * <code>null</code> if it is unspecified. 
     * <br> For events initialized with a DOM Level 2 Events method, such as 
     * <code>Event.initEvent()</code>, this is always <code>null</code>. 
     * @since DOM Level 3
     */
    public String getNamespaceURI();

    /**
     *  This method will always return <code>false</code>, unless the event 
     * implements the <code>CustomEvent</code> interface. 
     * @return  <code>false</code>, unless the event object implements the 
     *   <code>CustomEvent</code> interface. 
     * @since DOM Level 3
     */
    public boolean isCustom();

    /**
     *  This method is used to prevent event listeners of the same group to be 
     * triggered and, unlike <code>stopPropagation</code> its effect is 
     * immediate (see ). Once it has been called, further calls to that 
     * method have no additional effect. 
     * <p ><b>Note:</b>  This method does not prevent the default action from 
     * being invoked; use <code>Event.preventDefault()</code> for that 
     * effect. 
     * @since DOM Level 3
     */
    public void stopImmediatePropagation();

    /**
     *  This method will return <code>true</code> if the method 
     * <code>Event.preventDefault()</code> has been called for this event, 
     * <code>false</code> otherwise. 
     * @return  <code>true</code> if <code>Event.preventDefault()</code> has 
     *   been called for this event. 
     * @since DOM Level 3
     */
    public boolean isDefaultPrevented();

    /**
     *  The <code>initEventNS</code> method is used to initialize the value of 
     * an <code>Event</code> object and has the same behavior as 
     * <code>Event.initEvent()</code>. 
     * @param namespaceURIArg  Specifies <code>Event.namespaceuRI</code>, the 
     *   namespace URI associated with this event, or <code>null</code> if 
     *   no namespace. 
     * @param eventTypeArg  Specifies <code>Event.type</code>, the local name 
     *   of the event type.
     * @param canBubbleArg  Refer to the <code>Event.initEvent()</code> 
     *   method for a description of this parameter.
     * @param cancelableArg  Refer to the <code>Event.initEvent()</code> 
     *   method for a description of this parameter. 
     * @since DOM Level 3
     */
    public void initEventNS(String namespaceURIArg, 
                            String eventTypeArg, 
                            boolean canBubbleArg, 
                            boolean cancelableArg);
}
