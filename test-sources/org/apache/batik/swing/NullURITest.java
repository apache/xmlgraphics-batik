/*

   Copyright 2002-2003  The Apache Software Foundation 

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
package org.apache.batik.swing;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.TestReport;

/**
 * This test makes sure that setting the canvas's document uri to 
 * null does not cause a NullPointerException
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class NullURITest extends NullSetSVGDocumentTest {
    public String getName() { return getId(); }

    public Runnable getRunnable(final JSVGCanvas canvas) {
        return new Runnable () {
                public void run() {
                    canvas.setURI(null);
                }};
    }
}
