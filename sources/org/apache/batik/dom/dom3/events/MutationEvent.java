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
 * The <code>MutationEvent</code> interface provides specific contextual 
 * information associated with Mutation events. 
 * <p> To create an instance of the <code>MutationEvent</code> interface, use 
 * the <code>DocumentEvent.createEvent("MutationEvent")</code> method call. 
 * <p>See also the <a href='http://www.w3.org/TR/2003/NOTE-DOM-Level-3-Events-20031107'>Document Object Model (DOM) Level 3 Events Specification</a>.
 * @since DOM Level 2
 */
public interface MutationEvent
        extends Event, org.w3c.dom.events.MutationEvent {

    /**
     *  The <code>initMutationEventNS</code> method is used to initialize the 
     * value of a <code>MutationEvent</code> object and has the same 
     * behavior as <code>Event.initEventNS()</code>. 
     * @param namespaceURI  Refer to the <code>Event.initEventNS()</code> 
     *   method for a description of this parameter. 
     * @param typeArg  Refer to the <code>Event.initEventNS()</code> method 
     *   for a description of this parameter. 
     * @param canBubbleArg  Refer to the <code>Event.initEventNS()</code> 
     *   method for a description of this parameter. 
     * @param cancelableArg  Refer to the <code>Event.initEventNS()</code> 
     *   method for a description of this parameter. 
     * @param relatedNodeArg  Refer to the 
     *   <code>MutationEvent.initMutationEvent()</code> method for a 
     *   description of this parameter. 
     * @param prevValueArg  Refer to the 
     *   <code>MutationEvent.initMutationEvent()</code> method for a 
     *   description of this parameter. 
     * @param newValueArg  Refer to the 
     *   <code>MutationEvent.initMutationEvent()</code> method for a 
     *   description of this parameter. 
     * @param attrNameArg  Refer to the 
     *   <code>MutationEvent.initMutationEvent()</code> method for a 
     *   description of this parameter. 
     * @param attrChangeArg  Refer to the 
     *   <code>MutationEvent.initMutationEvent()</code> method for a 
     *   description of this parameter. 
     * @since DOM Level 3
     */
    public void initMutationEventNS(String namespaceURI, 
                                    String typeArg, 
                                    boolean canBubbleArg, 
                                    boolean cancelableArg, 
                                    org.w3c.dom.Node relatedNodeArg, 
                                    String prevValueArg, 
                                    String newValueArg, 
                                    String attrNameArg, 
                                    short attrChangeArg);
}
