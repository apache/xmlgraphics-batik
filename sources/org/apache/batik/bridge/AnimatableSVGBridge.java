package org.apache.batik.bridge;

import org.apache.batik.anim.AnimationTarget;
import org.apache.batik.anim.AnimationTargetListener;
import org.apache.batik.anim.values.AnimatableValue;
import org.apache.batik.dom.AbstractStylableDocument;

import org.w3c.dom.Element;
import org.w3c.dom.css.CSSStyleDeclaration;

/**
 * Abstract bridge class for those elements that can be animated.
 */
public abstract class AnimatableSVGBridge extends AbstractSVGBridge
        implements AnimationTarget {

    /**
     * The element that has been handled by this bridge.
     */
    protected Element e;

    /**
     * The bridge context to use for dynamic updates.
     */
    protected BridgeContext ctx;

    // AnimationTarget ///////////////////////////////////////////////////////

    /**
     * Returns the element.
     */
    public Element getElement() {
        return e;
    }

    /**
     * Updates a property value in this target.
     */
    public void updatePropertyValue(String pn, AnimatableValue val) {
        AbstractStylableDocument doc =
            (AbstractStylableDocument) e.getOwnerDocument();
        CSSStyleDeclaration over = doc.getOverrideStyle(e, null);
        //System.err.println(e.getAttributeNS(null, "id") + "." + pn + " val is " + val);
        if (val == null) {
            over.removeProperty(pn);
        } else {
            over.setProperty(pn, val.getCssText(), "");
        }
    }

    /**
     * Updates an attribute value in this target.
     */
    public void updateAttributeValue(String ns, String ln,
                                     AnimatableValue val) {
        // XXX ...
    }

    /**
     * Gets how percentage values are interpreted by the given attribute
     * or property.
     */
    public int getPercentageInterpretation(String ns, String an, boolean isCSS) {
        // XXX
        return 0;
    }

    /**
     * Returns whether color interpolations should be done in linear RGB
     * color space rather than sRGB.
     */
    public boolean useLinearRGBColorInterpolation() {
        // XXX
        return false;
    }

    /**
     * Converts a length from one unit to another.  The {@code fromType}
     * and {@code toType} parameters should be one of the constants defined
     * in {@link org.w3c.dom.svg.SVGLength}.
     */
    public float convertLength(int fromType, float value, int toType) {
        // XXX
        return 0;
    }

    /**
     * Adds a listener for changes to the given attribute value.
     */
    public void addTargetListener(String attributeName, boolean isCSS,
                                  AnimationTargetListener l) {
        // XXX
    }

    /**
     * Removes a listener for changes to the given attribute value.
     */
    public void removeTargetListener(String attributeName, boolean isCSS,
                                     AnimationTargetListener l) {
        // XXX
    }
}
