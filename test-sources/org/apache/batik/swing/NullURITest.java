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
public class NullURITest extends AbstractTest {
    public static final String TEST_NON_NULL_URI
        = "file:samples/anne.svg";

    public static final String ERROR_COULD_NOT_RENDER_TEST_URI
        = "error.could.not.render.test.uri";

    public static final String ERROR_COULD_NOT_RENDER_NULL_URI
        = "error.could.not.render.null.uri";

    public TestReport runImpl() throws Exception {
        final JFrame f = new JFrame();
        final JSVGCanvas canvas = new JSVGCanvas();

        final String monitor = "monitor";

        f.getContentPane().add(canvas);
        canvas.setPreferredSize(new Dimension(450, 500));
        f.pack();
        f.setVisible(true);

        // This class is not fool-proof: it assumes that the
        // non-null uri will render properly
        class InitialRenderListener extends GVTTreeRendererAdapter {
            public boolean failed = true;

            public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
                failed = false;
                synchronized(monitor){
                    monitor.notifyAll();
                }
            }


            public void gvtRenderingCancelled(GVTTreeRendererEvent e) {
                synchronized(monitor){
                    monitor.notifyAll();
                }
            }

            public void gvtRenderingFailed(GVTTreeRendererEvent e) {
                synchronized(monitor){
                    monitor.notifyAll();
                }
            }
        }

        class LoadListener extends SVGDocumentLoaderAdapter {
            public boolean failed = false;
            public void documentLoadingFailed(SVGDocumentLoaderEvent e) {
                synchronized(monitor){
                    failed = true;
                    monitor.notifyAll();
                }
            }

            public void documentLoadingCancelled(SVGDocumentLoaderEvent e) {
                synchronized(monitor){
                    failed = true;
                    monitor.notifyAll();
                }
            }

            public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
                synchronized(monitor){
                    monitor.notifyAll();
                }
            }
        }

        InitialRenderListener l = new InitialRenderListener();
        LoadListener ll = new LoadListener();
        canvas.addGVTTreeRendererListener(l);

        canvas.setURI(TEST_NON_NULL_URI);
        canvas.setEnabled(false);

        synchronized(monitor){
            monitor.wait();
        }

        if (l.failed || ll.failed){
            f.setVisible(false);
            return reportError(ERROR_COULD_NOT_RENDER_TEST_URI);
        }

        // Now, wait on new rendering
        l.failed = true;
        canvas.setURI(null);
        canvas.setEnabled(false);

        synchronized(monitor){
            monitor.wait();
        }

        if (l.failed || ll.failed){
            f.setVisible(false);
            return reportError(ERROR_COULD_NOT_RENDER_NULL_URI);
        }

        canvas.setURI(TEST_NON_NULL_URI);
        canvas.setEnabled(false);
        synchronized(monitor){
            monitor.wait();
        }

        if (l.failed || ll.failed){
            f.setVisible(false);
            return reportError(ERROR_COULD_NOT_RENDER_TEST_URI);
        }

        
        f.dispose();

        synchronized(monitor){
            monitor.wait(10000);
        }

        return reportSuccess();
    
    }
}
