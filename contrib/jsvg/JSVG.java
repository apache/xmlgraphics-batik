/*

   Copyright 2001-2003  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */

package org.apache.batik.apps.jsvg;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.svg.SVGUserAgentGUIAdapter;

/**
 * Simplest "complete" SVG Viewer using Batik.
 *
 * This is about as simple as an SVG viewer application can get.
 * It shuts it's self down when all windows are closed.
 * It reports errors interactively, and it takes a list of URI's
 * to open.
 *
 * @author <a href="mailto:Thomas.DeWeese@Kodak.com">deweese</a>
 * @version $Id$
 */
public class JSVG extends JFrame{
    static int windowCount=0;
    public JSVG(String url) {
        super(url);
        JSVGCanvas canvas = new JSVGCanvas(new SVGUserAgentGUIAdapter(this),
                                           true, true) {
                /**
                 * This method is called when the component knows the desired
                 * size of the window (based on width/height of outermost SVG
                 * element). We override it to immediately pack this frame.
                 */
                public void setMySize(Dimension d) {
                    setPreferredSize(d);
                    invalidate();
                    JSVG.this.pack();
                }
            };

        getContentPane().add(canvas, BorderLayout.CENTER);
        canvas.setURI(url);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    windowCount--;
                    if (windowCount == 0)
                        System.exit(0);
                }
            });
        windowCount++;
    }

    public static void main(String args[]) {
        for (int i=0; i<args.length; i++) {
            new JSVG(args[i]);
        }
    }
};
