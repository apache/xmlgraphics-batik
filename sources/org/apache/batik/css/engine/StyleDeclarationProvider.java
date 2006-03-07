package org.apache.batik.css.engine;

/**
 * An interface for {@link org.w3c.dom.css.CSSStyleDeclaration} objects to
 * expose their underlying {@link StyleDeclaration} objects storing the
 * properties.
 */
public interface StyleDeclarationProvider {

    /**
     * Returns the object storing the properties of this style declaration.
     */
    StyleDeclaration getStyleDeclaration();

    /**
     * Sets the object storing the properties of this style declaration.
     */
    void setStyleDeclaration(StyleDeclaration sd);
}
