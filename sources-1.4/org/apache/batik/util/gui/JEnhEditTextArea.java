/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.util.gui;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.gjt.sp.jedit.textarea.TextAreaDefaults;

/**
 * The enhanced JEditTextArea. Has cut / copy / paste, select all and undo /
 * redo shortcuts added, as well as the mouse wheel scroll support (only for
 * JDKs greater then 1.3).
 *
 * @version $Id$
 */
public class JEnhEditTextArea extends AbstractJEnhEditTextArea {

    /**
     * Creates a new JEnhEditTextArea with the specified settings.
     * 
     * @param defaults
     *            The default settings
     */
    public JEnhEditTextArea(TextAreaDefaults defaults) {
        super(defaults);
        // Mouse wheel support
        addMouseWheelListener(new MouseWhellSupport());
    }
    
    //     Mouse wheel support
    /**
     * The mouse wheel listener.
     */
    protected class MouseWhellSupport implements MouseWheelListener {
        public void mouseWheelMoved(MouseWheelEvent e) {
            int wheelRotationCount = e.getWheelRotation();
            int lineToShow = getFirstLine() + wheelRotationCount;
            if (wheelRotationCount > 0) {
                lineToShow += getVisibleLines();
            }
            if (lineToShow < 0) {
                lineToShow = 0;
            }
            if (lineToShow >= getLineCount()) {
                lineToShow = getLineCount() - 1;
            }
            scrollTo(lineToShow, getLineStartOffset(lineToShow));
        }
    }
}
