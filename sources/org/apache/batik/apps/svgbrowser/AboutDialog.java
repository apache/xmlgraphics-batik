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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.apache.batik.Version;

/**
 * A dialog showing the revision of the Batik viewer as well
 * as the list of contributors.
 * The dialog can be dismissed by click or by escaping.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class AboutDialog extends JWindow {

    public static final String ICON_BATIK_SPLASH 
        = "AboutDialog.icon.batik.splash";

    public static final String ICON_APACHE_LOGO
        = "AboutDialog.icon.apache.logo";

    public static final String LABEL_APACHE_BATIK_PROJECT
        = "AboutDialog.label.apache.batik.project";

    public static final String LABEL_CONTRIBUTORS
        = "AboutDialog.label.contributors";

    /**
     * Default constructor
     */
    public AboutDialog(){
        super();
        buildGUI();
    }

    public AboutDialog(Frame owner){
        super(owner);
        buildGUI();

        addKeyListener(new KeyAdapter(){
                public void keyPressed(KeyEvent e){
                    if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                        setVisible(false);
                        dispose();
                    }
                }
            });

        addMouseListener(new MouseAdapter(){
                public void mousePressed(MouseEvent e){
                    setVisible(false);
                    dispose();
                }
            });
    }

    public void setLocationRelativeTo(Frame f) {
        Dimension invokerSize = f.getSize();
        Point loc = f.getLocation();
        Point invokerScreenLocation = new Point(loc.x, loc.y);

        Rectangle bounds = getBounds();
        int  dx = invokerScreenLocation.x+((invokerSize.width-bounds.width)/2);
        int  dy = invokerScreenLocation.y+((invokerSize.height - bounds.height)/2);
        Dimension screenSize = getToolkit().getScreenSize();

        if (dy+bounds.height>screenSize.height) {
            dy = screenSize.height-bounds.height;
            dx = invokerScreenLocation.x<(screenSize.width>>1) ? invokerScreenLocation.x+invokerSize.width :
                invokerScreenLocation.x-bounds.width;
        }
        if (dx+bounds.width>screenSize.width) {
            dx = screenSize.width-bounds.width;
        }

        if (dx<0) dx = 0;
        if (dy<0) dy = 0;
        setLocation(dx, dy);
    }

    /**
     * Populates this window
     */
    protected void buildGUI(){
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.white);

        ClassLoader cl = this.getClass().getClassLoader();

        //
        // Top is made of the Apache feather, the 
        // name of the project and URL
        //
        URL url = cl.getResource(Resources.getString(ICON_APACHE_LOGO));
        JLabel l = new JLabel(Resources.getString(LABEL_APACHE_BATIK_PROJECT),
                              new ImageIcon(url),
                              SwingConstants.LEFT);
        panel.add(BorderLayout.NORTH, l);

        //
        // Add splash image
        //
        url = cl.getResource(Resources.getString(ICON_BATIK_SPLASH));
        panel.add(BorderLayout.CENTER, new JLabel(new ImageIcon(url)));

        //
        // Add exact revision information
        //
        String tagName = Version.getVersion();

        panel.add(BorderLayout.SOUTH, new JLabel(tagName, SwingConstants.RIGHT));

        setBackground(Color.white);
        getContentPane().setBackground(Color.white);

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.white);
        p.add(panel, BorderLayout.CENTER);

        JTextArea contributors 
            = new JTextArea(Resources.getString(LABEL_CONTRIBUTORS)){ 
                    {setLineWrap(true); setWrapStyleWord(true); setEnabled(false); setRows(11); }
                };

        contributors.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        p.add(contributors,
              BorderLayout.SOUTH);
        ((JComponent)getContentPane()).setBorder
            (BorderFactory.createCompoundBorder
             (BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.gray, Color.black),
              BorderFactory.createCompoundBorder
             (BorderFactory.createCompoundBorder
              (BorderFactory.createEmptyBorder(3, 3, 3, 3),
               BorderFactory.createLineBorder(Color.black)),
              BorderFactory.createEmptyBorder(10, 10, 10, 10))));
        
        getContentPane().add(p);
        pack();
    }
}
