/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgbrowser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;

import javax.swing.border.BevelBorder;

import org.apache.batik.ext.swing.JGridBagPanel;
import org.apache.batik.ext.swing.GridBagConstants;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

/**
 * A dialog showing the revision of the Batik viewer as well
 * as the list of contributors.
 * The dialog can be dismissed by click or by escaping.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class AboutDialog extends JWindow
    implements GridBagConstants{

    public static final String ICON_BATIK_SPLASH 
        = "AboutDialog.icon.batik.splash";

    public static final String ICON_APACHE_LOGO
        = "AboutDialog.icon.apache.logo";

    public static final String LABEL_APACHE_BATIK_PROJECT
        = "AboutDialog.label.apache.batik.project";

    public static final String LABEL_CONTRIBUTORS
        = "AboutDialog.label.contributors";

    public static final String LABEL_DEVELOPMENT_BUILD
        = "AboutDialog.label.development.build";

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
        setLocationRelativeTo(owner);
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
        JGridBagPanel panel = new JGridBagPanel();
        panel.setBackground(Color.white);

        ClassLoader cl = this.getClass().getClassLoader();

        //
        // Top is made of the Apache feather, the 
        // name of the project and URL
        //
        JGridBagPanel projectPanel = new JGridBagPanel();
        projectPanel.setBackground(Color.white);
        projectPanel.add(new JLabel(new ImageIcon(cl.getResource(Resources.getString(ICON_APACHE_LOGO)))),
                         0, 0, 1, 1, WEST, NONE, 0, 0);
        projectPanel.add(new JLabel(Resources.getString(LABEL_APACHE_BATIK_PROJECT)),
                         1, 0, 1, 1, WEST, NONE, 0, 0);
        projectPanel.add(new JLabel(""),
                         2, 0, 1, 1, WEST, HORIZONTAL, 1, 0);

        panel.add(projectPanel, 0, 0, 1, 1, CENTER, HORIZONTAL, 1, 0);

        //
        // Add splash image
        //
        panel.add(new JLabel(new ImageIcon(cl.getResource(Resources.getString(ICON_BATIK_SPLASH)))),
                  0, 1, 1, 1, CENTER, NONE, 0, 0);

        //
        // Add exact revision information
        //
        String tagName = "$Name$";
        tagName = tagName.substring(6, tagName.length()-1);
        
        if(tagName.trim().intern().equals("")){
            tagName = Resources.getString(LABEL_DEVELOPMENT_BUILD);
        }

        panel.add(new JLabel(tagName),
                  0, 2, 1, 1, EAST, NONE, 0, 0);

        setBackground(Color.white);
        getContentPane().setBackground(Color.white);

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

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.white);
        p.add(panel, BorderLayout.CENTER);

        JTextArea contributors 
            = new JTextArea(Resources.getString(LABEL_CONTRIBUTORS)){ 
                    {setLineWrap(true); setWrapStyleWord(true); setEnabled(false);}
                };

        contributors.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        p.add(contributors,
              BorderLayout.SOUTH);
        p.setBorder
            (BorderFactory.createCompoundBorder
             (BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.gray, Color.black),
              BorderFactory.createCompoundBorder
             (BorderFactory.createCompoundBorder
              (BorderFactory.createEmptyBorder(3, 3, 3, 3),
               BorderFactory.createLineBorder(Color.black)),
              BorderFactory.createEmptyBorder(10, 10, 10, 10))));
        
        // getContentPane().setLayout(new FlowLayout());
        getContentPane().add(p);

        pack();
        pack(); // This is not a mistake but a work-around.
        // The layout is not done properly on the first pack();
    }

    public static void main(String args[]){
        AboutDialog dlg = new AboutDialog();
        dlg.setVisible(true);
    }
}
