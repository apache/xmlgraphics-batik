/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.dom.events;

import org.w3c.dom.events.UIEvent;
import org.w3c.dom.views.AbstractView;

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
