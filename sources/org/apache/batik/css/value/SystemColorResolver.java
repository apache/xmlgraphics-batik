/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

/**
 * This interface represents an object which can query system colors.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public interface SystemColorResolver {

    /**
     * Returns the active border color.
     */
    Color activeBorder();

    /**
     * Returns the active caption color.
     */
    Color activeCaption();

    /**
     * Returns the application workspace color.
     */
    Color appWorkspace();

    /**
     * Returns the desktop color.
     */
    Color background();

    /**
     * Returns the buttons color.
     */
    Color buttonFace();

    /**
     * Returns the button highlight color.
     */
    Color buttonHighlight();

    /**
     * Returns the button shadow color.
     */
    Color buttonShadow();

    /**
     * Returns the button text color.
     */
    Color buttonText();

    /**
     * Returns the caption text color.
     */
    Color captionText();

    /**
     * Returns the gray text color.
     */
    Color grayText();

    /**
     * Returns the highlight color.
     */
    Color highlight();

    /**
     * Returns the text color.
     */
    Color highlightText();

    /**
     * Returns the inactive border color.
     */
    Color inactiveBorder();

    /**
     * Returns the inactive caption color.
     */
    Color inactiveCaption();

    /**
     * Returns the inactive caption text color.
     */
    Color inactiveCaptionText();

    /**
     * Returns the info background color.
     */
    Color infoBackground();

    /**
     * Returns the info text color.
     */
    Color infoText();

    /**
     * Returns the menu background color.
     */
    Color menu();

    /**
     * Returns the menu text color.
     */
    Color menuText();

    /**
     * Returns the scrollbar background color.
     */
    Color scrollbar();

    /**
     * Returns the 3D dark shadow color.
     */
    Color threeDDarkShadow();

    /**
     * Returns the 3D face color.
     */
    Color threeDFace();

    /**
     * Returns the 3D highlight color.
     */
    Color threeDHighlight();

    /**
     * Returns the 3D light shadow color.
     */
    Color threeDLightShadow();

    /**
     * Returns the 3D shadow color.
     */
    Color threeDShadow();

    /**
     * Returns window's color.
     */
    Color window();

    /**
     * Returns window frame color.
     */
    Color windowFrame();

    /**
     * Returns window text color.
     */
    Color windowText();

    /**
     * To Store a CSS RGB color.
     */
    public interface Color {

        /**
         * Returns the red component.
         */
        int getRed();

        /**
         * Returns the green component.
         */
        int getGreen();

        /**
         * Returns the blue component.
         */
        int getBlue();
    }
}
