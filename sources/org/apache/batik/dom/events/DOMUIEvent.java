/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.events;

import org.w3c.dom.views.AbstractView;
import org.w3c.dom.events.UIEvent;

/**
 * The UIEvent class provides specific contextual information
 * associated with User Interface events.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 */
public class DOMUIEvent extends AbstractEvent implements UIEvent {

    private AbstractView view;
    private int detail;

    /**
     * DOM: The <code>view</code> attribute identifies the
     * <code>AbstractView</code> from which the event was generated.  
     */
    public AbstractView getView() {
	return view;
    }

    /**
     * DOM: Specifies some detail information about the
     * <code>Event</code>, depending on the type of event.  
     */
    public int getDetail() {
	return detail;
    }

    /**
     * DOM: The <code>initUIEvent</code> method is used to initialize
     * the value of a <code>UIEvent</code> created through the
     * <code>DocumentEvent</code> interface.  This method may only be
     * called before the <code>UIEvent</code> has been dispatched via
     * the <code>dispatchEvent</code> method, though it may be called
     * multiple times during that phase if necessary.  If called
     * multiple times, the final invocation takes precedence.
     *
     * @param typeArg Specifies the event type.
     * @param canBubbleArg Specifies whether or not the event can bubble.
     * @param cancelableArg Specifies whether or not the event's default  
     *   action can be prevented.
     * @param viewArg Specifies the <code>Event</code>'s 
     *   <code>AbstractView</code>.
     * @param detailArg Specifies the <code>Event</code>'s detail.  
     */
    public void initUIEvent(String typeArg, 
			    boolean canBubbleArg, 
			    boolean cancelableArg, 
			    AbstractView viewArg, 
			    int detailArg) {
	initEvent(typeArg, canBubbleArg, cancelableArg);
	this.view = viewArg;
	this.detail = detailArg;
    }
}
