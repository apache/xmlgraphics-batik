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

package org.apache.batik.apps.svgbrowser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.apache.batik.util.gui.resource.ResourceManager;

/**
 * This class represents a viewer status bar.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class StatusBar extends JPanel {

    /**
     * The gui resources file name
     */
    protected final static String RESOURCES =
        "org.apache.batik.apps.svgbrowser.resources.StatusBarMessages";

    /**
     * The resource bundle
     */
    protected static ResourceBundle bundle;

    /**
     * The resource manager
     */
    protected static ResourceManager rManager;
    static {
        bundle = ResourceBundle.getBundle(RESOURCES, Locale.getDefault());
        rManager = new ResourceManager(bundle);
    }

    /**
     * The x position/width label.
     */
    protected JLabel xPosition;

    /**
     * The y position/height label.
     */
    protected JLabel yPosition;

    /**
     * The zoom label.
     */
    protected JLabel zoom;

    /**
     * The message label
     */
    protected JLabel message;

    /**
     * The main message
     */
    protected String mainMessage;

    /**
     * The temporary message
     */
    protected String temporaryMessage;

    /**
     * The current display thread.
     */
    protected Thread displayThread;

    /**
     * Creates a new status bar
     * @param rm the resource manager that finds the message
     */
    public StatusBar() {
        super(new BorderLayout(5, 5));

        JPanel p = new JPanel(new BorderLayout(0, 0));
        add("West", p);

        xPosition = new JLabel();
        BevelBorder bb;
        bb = new BevelBorder(BevelBorder.LOWERED,
                             getBackground().brighter().brighter(),
                             getBackground(),
                             getBackground().darker().darker(),
                             getBackground());
        xPosition.setBorder(bb);
        xPosition.setPreferredSize(new Dimension(110, 16));
        p.add("West", xPosition);

        yPosition = new JLabel();
        yPosition.setBorder(bb);
        yPosition.setPreferredSize(new Dimension(110, 16));
        p.add("Center", yPosition);

        zoom = new JLabel();
        zoom.setBorder(bb);
        zoom.setPreferredSize(new Dimension(70, 16));
        p.add("East", zoom);

        p = new JPanel(new BorderLayout(0, 0));
        message = new JLabel();
        message.setBorder(bb);
        p.add(message);
        add(p);
        setMainMessage(rManager.getString("Panel.default_message"));
    }

    /**
     * Sets the x position.
     */
    public void setXPosition(float x) {
        xPosition.setText("x: " + x);
    }

    /**
     * Sets the width.
     */
    public void setWidth(float w) {
        xPosition.setText(rManager.getString("Position.width_letters") +
                          " " + w);
    }

    /**
     * Sets the y position.
     */
    public void setYPosition(float y) {
        yPosition.setText("y: " + y);
    }

    /**
     * Sets the height.
     */
    public void setHeight(float h) {
        yPosition.setText(rManager.getString("Position.height_letters") +
                          " " + h);
    }

    /**
     * Sets the zoom factor.
     */
    public void setZoom(float f) {
        f = (f > 0) ? f : -f;
        if (f == 1) {
            zoom.setText("1:1");
        } else if (f >= 1) {
            String s = Float.toString(f);
            if (s.length() > 6) {
                s = s.substring(0, 6);
            }
            zoom.setText("1:" + s);
        } else {
            String s = Float.toString(1 / f);
            if (s.length() > 6) {
                s = s.substring(0, 6);
            }
            zoom.setText(s + ":1");
        }
    }

    /**
     * Sets a temporary message
     * @param s the message
     */
    public void setMessage(String s) {
        setPreferredSize(new Dimension(0, getPreferredSize().height));
        if (displayThread != null) {
            displayThread.interrupt();
        }
        temporaryMessage = s;
        displayThread = new DisplayThread();
        displayThread.start();
    }

    /**
     * Sets the main message
     * @param s the message
     */
    public void setMainMessage(String s) {
        mainMessage = s;
        message.setText(mainMessage = s);
        if (displayThread != null) {
            displayThread.interrupt();
            displayThread = null;
        }
        setPreferredSize(new Dimension(0, getPreferredSize().height));
    }

    /**
     * To display the main message
     */
    protected class DisplayThread extends Thread {
        public DisplayThread() {
            setPriority(Thread.MIN_PRIORITY);
        }

        public void run() {
            message.setText(temporaryMessage);
            try {
                Thread.sleep(5000);
            } catch(InterruptedException e) {
            }
            message.setText(mainMessage);
        }
    }
}
