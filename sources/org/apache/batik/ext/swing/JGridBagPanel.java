/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/
package org.apache.batik.ext.swing;

import javax.swing.JPanel;
import java.awt.LayoutManager;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.AWTError;
import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;

/**
 * An implementation of JPanel that uses the GridBagLayout.
 * 
 * @author  <a href="mailto:vincent.hardy@eng.sun.com>Vincent Hardy</a>
 * @version $Id$
 */

public class JGridBagPanel extends JPanel {
    /**
     * Provides insets desired for a given grid cell
     */
    public static interface InsetsManager{
        /**
         * Returns the insets for cell (gridx, gridy);
         */
        public Insets getInsets(int gridx, int gridy);
    }

    /**
     * Always use 0 insets
     */
    private static class ZeroInsetsManager implements InsetsManager{
        private Insets insets = new Insets(0, 0, 0, 0);

        public Insets getInsets(int gridx, int gridy){
            return insets;
        }
    }

    /**
     * Default implemenation
     */
    private static class DefaultInsetsManager implements InsetsManager{
        /**
         * leftInset is the space used by default as a padding to the 
         * left of each grid cell.
         */
        int leftInset=5;
        
        /**
         * topInset is the space used by default as a padding to the 
         * top of each grid cell.
         */
        int topInset=5;
        
        public Insets positiveInsets = new Insets(topInset, leftInset, 0, 0);
        public Insets leftInsets = new Insets(topInset, 0, 0, 0);
        public Insets topInsets = new Insets(0, leftInset, 0, 0);
        public Insets topLeftInsets = new Insets(0, 0, 0, 0);

        public Insets getInsets(int gridx, int gridy){
            if(gridx > 0){
                if(gridy > 0)
                    return positiveInsets;
                else
                    return topInsets;
            }
            else{
                if(gridy > 0)
                    return leftInsets;
                else
                    return topLeftInsets;
            }
        }
    }

    /**
     * An InsetsManager that uses zero insets
     */
    public static final InsetsManager ZERO_INSETS = new ZeroInsetsManager();

    /**
     * An InsetsManager that uses padding for inside cells
     */
    public static final InsetsManager DEFAULT_INSETS = new DefaultInsetsManager();

    /**
     * Specify that this component is the 
     * last component in its column or row. 
     * @since   JDK1.0
     */
    public static final int REMAINDER = GridBagConstraints.REMAINDER;

    /**
     * Do not resize the component. 
     * @since   JDK1.0
     */
    public static final int NONE = GridBagConstraints.NONE;

    /**
     * Resize the component both horizontally and vertically. 
     * @since   JDK1.0
     */
    public static final int BOTH = GridBagConstraints.BOTH;

    /**
     * Resize the component horizontally but not vertically. 
     * @since   JDK1.0
     */
    public static final int HORIZONTAL = GridBagConstraints.HORIZONTAL;

    /**
     * Resize the component vertically but not horizontally. 
     * @since   JDK1.0
     */
    public static final int VERTICAL = GridBagConstraints.VERTICAL;

    /**
     * Put the component in the center of its display area.
     * @since    JDK1.0
     */
    public static final int CENTER = GridBagConstraints.CENTER;

    /**
     * Put the component at the top of its display area,
     * centered horizontally. 
     * @since   JDK1.0
     */
    public static final int NORTH = GridBagConstraints.NORTH;

    /**
     * Put the component at the top-right corner of its display area. 
     * @since   JDK1.0
     */
    public static final int NORTHEAST = GridBagConstraints.NORTHEAST;

    /**
     * Put the component on the left side of its display area, 
     * centered vertically.
     * @since    JDK1.0
     */
    public static final int EAST = GridBagConstraints.EAST;

    /**
     * Put the component at the bottom-right corner of its display area. 
     * @since   JDK1.0
     */
    public static final int SOUTHEAST = GridBagConstraints.SOUTHEAST;

    /**
     * Put the component at the bottom of its display area, centered 
     * horizontally. 
     * @since   JDK1.0
     */
    public static final int SOUTH = GridBagConstraints.SOUTH;

    /**
     * Put the component at the bottom-left corner of its display area. 
     * @since   JDK1.0
     */
    public static final int SOUTHWEST = GridBagConstraints.SOUTHWEST;

    /**
     * Put the component on the left side of its display area, 
     * centered vertically.
     * @since    JDK1.0
     */
    public static final int WEST = GridBagConstraints.WEST;

    /**
     * Put the component at the top-left corner of its display area. 
     * @since   JDK1.0
     */
    public static final int NORTHWEST = GridBagConstraints.NORTHWEST;

    /**
     * Used to get insets at any given cell location
     */
    public InsetsManager insetsManager;

    /**
     * Sets the layout manager to GridBagLayout
     */
    public JGridBagPanel(){
        this(new DefaultInsetsManager());
    }

    /**
     * Initializes panel with a given insets manager
     */
    public JGridBagPanel(InsetsManager insetsManager){
        super(new GridBagLayout());

        if(insetsManager != null)
            this.insetsManager = insetsManager;
        else
            this.insetsManager = new DefaultInsetsManager();
    }

    /**
     * This method only takes effect if the LayoutManager is a GridBagLayout
     */
    public void setLayout(LayoutManager layout){
        if(layout instanceof GridBagLayout)
            super.setLayout(layout);
    }

    /**
     * This version uses default insets and assumes that components are added in
     * positive cell coordinates. Top inset for components added to the top
     * is 0. Left inset for components added to the left is 0. For compoents at
     * index gridx more than zero and index gridy more than zero, the insets 
     * are set to a default value.
     *
     * @param cmp Component to add to the panel
     * @param gridx x position of the cell into which component should be added
     * @param gridy y position of the cell into which component should be added
     * @param gridwidth width, in cells, of the space occupied by the component in the grid
     * @param gridheight height, in cells, of the space occupied by the component in the grid
     * @param anchor placement of the component in its allocated space: WEST, NORTH, SOUTH, NORTHWEST, ...
     * @param fill out should the component be resized within its space? NONE, BOTH, HORIZONTAL, VERTICAL.
     * @param weightx what amount of extra horizontal space, if any, should be given to this component?
     * @param weighty what amount of extra vertical space, if any, should be given to this component?
     */
    public void add(Component cmp, int gridx, int gridy,
                    int gridwidth, int gridheight, int anchor, int fill, 
                    double weightx, double weighty){
        Insets insets = insetsManager.getInsets(gridx, gridy);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = gridwidth;
        constraints.gridheight = gridheight;
        constraints.anchor = anchor;
        constraints.fill = fill;
        constraints.weightx = weightx;
        constraints.weighty = weighty;
        constraints.insets = insets;
        add(cmp, constraints);
    }

}
