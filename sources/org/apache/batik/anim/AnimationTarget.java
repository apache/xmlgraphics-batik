package org.apache.batik.anim;

import org.apache.batik.anim.values.AnimatableValue;

import org.w3c.dom.Element;

/**
 * An interface for targets of animation to provide context information.
 */
public interface AnimationTarget {

    // Constants for percentage interpretation.
    static final int PERCENTAGE_FONT_SIZE       = 0;
    static final int PERCENTAGE_VIEWPORT_WIDTH  = 1;
    static final int PERCENTAGE_VIEWPORT_HEIGHT = 2;
    static final int PERCENTAGE_VIEWPORT_SIZE   = 3;

    /**
     * Returns the element.
     */
    Element getElement();

    /**
     * Updates a property value in this target.
     */
    void updatePropertyValue(String pn, AnimatableValue val);

    /**
     * Updates an attribute value in this target.
     */
    void updateAttributeValue(String ns, String ln, AnimatableValue val);

    /**
     * Gets how percentage values are interpreted by the given attribute
     * or property.
     */
    int getPercentageInterpretation(String ns, String an, boolean isCSS);

    /**
     * Returns whether color interpolations should be done in linear RGB
     * color space rather than sRGB.
     */
    boolean useLinearRGBColorInterpolation();

    /**
     * Converts a length from one unit to another.  The {@code fromType}
     * and {@code toType} parameters should be one of the constants defined
     * in {@link org.w3c.dom.svg.SVGLength}.
     */
    float convertLength(int fromType, float value, int toType);

    // Listeners

    /**
     * Adds a listener for changes to the given attribute value.
     */
    void addTargetListener(String attributeName, boolean isCSS,
                           AnimationTargetListener l);

    /**
     * Removes a listener for changes to the given attribute value.
     */
    void removeTargetListener(String attributeName, boolean isCSS,
                              AnimationTargetListener l);
}
