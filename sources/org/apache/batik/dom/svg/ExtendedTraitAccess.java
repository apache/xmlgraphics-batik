package org.apache.batik.dom.svg;

/**
 * Interface for SVG DOM classes to expose information about the traits
 * (XML attributes and CSS properties) their elements support.
 */
public interface ExtendedTraitAccess extends TraitAccess {

    /**
     * Returns whether the given CSS property is available on this element.
     */
    boolean hasProperty(String pn);

    /**
     * Returns whether the given trait is available on this element.
     */
    boolean hasTrait(String ns, String ln);

    /**
     * Returns whether the given CSS property is animatable.
     */
    boolean isPropertyAnimatable(String pn);

    /**
     * Returns whether the given XML attribute is animatable.
     */
    boolean isAttributeAnimatable(String ns, String ln);

    /**
     * Returns whether the given CSS property is additive.
     */
    boolean isPropertyAdditive(String pn);

    /**
     * Returns whether the given XML attribute is additive.
     */
    boolean isAttributeAdditive(String ns, String ln);

    /**
     * Returns whether the given trait is animatable.
     */
    boolean isTraitAnimatable(String ns, String tn);

    /**
     * Returns whether the given trait is additive.
     */
    boolean isTraitAdditive(String ns, String tn);
}
