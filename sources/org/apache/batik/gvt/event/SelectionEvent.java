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

package org.apache.batik.gvt.event;

import java.awt.Shape;

/**
 * An event which indicates that a selection is being made or has been made.
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @author <a href="mailto:tkormann@ilog.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class SelectionEvent {

    /**
     * The id for the "selection changing" event.
     * (Selection process is under way)
     */
    public static final int SELECTION_CHANGED = 1;

    /**
     * The id for the "selection cleared" event.
     */
    public static final int SELECTION_CLEARED = 3;

    /**
     * The id for the "selection started" event.
     */
    public static final int SELECTION_STARTED = 4;

    /**
     * The id for the "selection completed" event.
     * (Selection process is complete).
     */
    public static final int SELECTION_DONE = 2;

    /** The shape enclosing the selection */
    protected Shape highlightShape;

    /** The object which composes the selection */
    protected Object selection;

    /** The event type of the current selection event */
    protected int id;

    /**
     * Constructs a new graphics node paint event.
     * @param selection the selection
     * @param id the id of this event
     * @param highlightShape a user-space shape enclosing the selection.
     */
    public SelectionEvent(Object selection, int id, Shape highlightShape ) {
        this.id = id;
        this.selection = selection;
        this.highlightShape = highlightShape;
    }

    /**
     * Returns a shape in user space that encloses the current selection.
     */
    public Shape getHighlightShape() {
        return highlightShape;
    }

    /**
     * Returns the selection associated with this event.
     * Only guaranteed current for events of type SELECTION_DONE.
     */
    public Object getSelection() {
        return selection;
    }

    /**
     * Returns the event's selection event type.
     * @see org.apache.batik.gvt.event.SelectionEvent#SELECTION_CHANGED
     * @see org.apache.batik.gvt.event.SelectionEvent#SELECTION_CLEARED
     * @see org.apache.batik.gvt.event.SelectionEvent#SELECTION_DONE
     */
    public int getID() {
        return id;
    }
}
