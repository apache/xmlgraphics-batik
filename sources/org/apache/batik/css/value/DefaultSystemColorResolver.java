/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.css.value;

import java.awt.SystemColor;

/**
 * This class provides a default implementation of a SystemColorResolver.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class DefaultSystemColorResolver implements SystemColorResolver {
    
    /**
     * Returns the active border color.
     */
    public Color activeBorder() {
        return new SystemColorWrapper(SystemColor.windowBorder);
    }

    /**
     * Returns the active caption color.
     */
    public Color activeCaption() {
        return new SystemColorWrapper(SystemColor.activeCaption);
    }

    /**
     * Returns the application workspace color.
     */
    public Color appWorkspace() {
        return new SystemColorWrapper(SystemColor.desktop);
    }

    /**
     * Returns the desktop color.
     */
    public Color background() {
        return new SystemColorWrapper(SystemColor.desktop);
    }

    /**
     * Returns the buttons color.
     */
    public Color buttonFace() {
        return new SystemColorWrapper(SystemColor.control);
    }

    /**
     * Returns the button highlight color.
     */
    public Color buttonHighlight() {
        return new SystemColorWrapper(SystemColor.controlLtHighlight);
    }

    /**
     * Returns the button shadow color.
     */
    public Color buttonShadow() {
        return new SystemColorWrapper(SystemColor.controlDkShadow);
    }

    /**
     * Returns the button text color.
     */
    public Color buttonText() {
        return new SystemColorWrapper(SystemColor.controlText);
    }

    /**
     * Returns the caption text color.
     */
    public Color captionText() {
        return new SystemColorWrapper(SystemColor.activeCaptionText);
    }

    /**
     * Returns the gray text color.
     */
    public Color grayText() {
        return new SystemColorWrapper(SystemColor.textInactiveText);
    }

    /**
     * Returns the highlight color.
     */
    public Color highlight() {
        return new SystemColorWrapper(SystemColor.textHighlight);
    }

    /**
     * Returns the highlight text color.
     */
    public Color highlightText() {
        return new SystemColorWrapper(SystemColor.textHighlightText);
    }

    /**
     * Returns the inactive border color.
     */
    public Color inactiveBorder() {
        return new SystemColorWrapper(SystemColor.windowBorder);
    }

    /**
     * Returns the inactive caption color.
     */
    public Color inactiveCaption() {
        return new SystemColorWrapper(SystemColor.inactiveCaption);
    }

    /**
     * Returns the inactive caption text color.
     */
    public Color inactiveCaptionText() {
        return new SystemColorWrapper(SystemColor.inactiveCaptionText);
    }

    /**
     * Returns the info background color.
     */
    public Color infoBackground() {
        return new SystemColorWrapper(SystemColor.info);
    }

    /**
     * Returns the info text color.
     */
    public Color infoText() {
        return new SystemColorWrapper(SystemColor.infoText);
    }

    /**
     * Returns the menu background color.
     */
    public Color menu() {
        return new SystemColorWrapper(SystemColor.menu);
    }

    /**
     * Returns the menu text color.
     */
    public Color menuText() {
        return new SystemColorWrapper(SystemColor.menuText);
    }

    /**
     * Returns the scrollbar background color.
     */
    public Color scrollbar() {
        return new SystemColorWrapper(SystemColor.scrollbar);
    }

    /**
     * Returns the 3D dark shadow color.
     */
    public Color threeDDarkShadow() {
        return new SystemColorWrapper(SystemColor.controlDkShadow);
    }

    /**
     * Returns the 3D face color.
     */
    public Color threeDFace() {
        return new SystemColorWrapper(SystemColor.control);
    }

    /**
     * Returns the 3D highlight color.
     */
    public Color threeDHighlight() {
        return new SystemColorWrapper(SystemColor.controlHighlight);
    }

    /**
     * Returns the 3D light shadow color.
     */
    public Color threeDLightShadow() {
        return new SystemColorWrapper(SystemColor.controlLtHighlight);
    }

    /**
     * Returns the 3D shadow color.
     */
    public Color threeDShadow() {
        return new SystemColorWrapper(SystemColor.controlShadow);
    }

    /**
     * Returns the window's color.
     */
    public Color window() {
        return new SystemColorWrapper(SystemColor.window);
    }

    /**
     * Returns the window frame color.
     */
    public Color windowFrame() {
        return new SystemColorWrapper(SystemColor.windowBorder);
    }

    /**
     * Returns the window text color.
     */
    public Color windowText() {
        return new SystemColorWrapper(SystemColor.windowText);
    }

    /**
     * To encapsulate a SystemColor.
     */
    protected static class SystemColorWrapper implements Color {

        /**
         * The wrapped color.
         */
        protected SystemColor systemColor;

        /**
         * Creates a new SystemColorWrapper.
         */
        public SystemColorWrapper(SystemColor c) {
            systemColor = c;
        }

        /**
         * Returns the red component.
         */
        public int getRed() {
            return systemColor.getRed();
        }

        /**
         * Returns the green component.
         */
        public int getGreen() {
            return systemColor.getGreen();
        }

        /**
         * Returns the blue component.
         */
        public int getBlue() {
            return systemColor.getBlue();
        }
    }
}
