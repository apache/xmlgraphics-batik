package org.apache.batik.css.dom;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.StyleDeclaration;
import org.apache.batik.css.engine.StyleDeclarationProvider;
import org.apache.batik.css.engine.value.Value;

/**
 * A class for SVG style declarations that store their properties in a
 * {@link org.apache.batik.css.engine.StyleDeclaration}.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public abstract class CSSOMStoredStyleDeclaration
    extends CSSOMSVGStyleDeclaration
    implements CSSOMStyleDeclaration.ValueProvider,
               CSSOMStyleDeclaration.ModificationHandler,
               StyleDeclarationProvider {

    /**
     * The object storing the properties.
     */
    protected StyleDeclaration declaration;

    /**
     * Creates a new CSSOMStoredStyleDeclaration.
     */
    public CSSOMStoredStyleDeclaration(CSSEngine eng) {
        super(null, null, eng);
        valueProvider = this;
        setModificationHandler(this);
    }

    /**
     * Returns the object storing the properties of this style declaration.
     */
    public StyleDeclaration getStyleDeclaration() {
        return declaration;
    }

    /**
     * Sets the object storing the properties of this style declaration.
     */
    public void setStyleDeclaration(StyleDeclaration sd) {
        declaration = sd;
    }

    // ValueProvider /////////////////////////////////////////////////////////

    /**
     * Returns the current value associated with this object.
     */
    public Value getValue(String name) {
        int idx = cssEngine.getPropertyIndex(name);
        for (int i = 0; i < declaration.size(); i++) {
            if (idx == declaration.getIndex(i)) {
                return declaration.getValue(i);
            }
        }
        return null;
    }

    /**
     * Tells whether the given property is important.
     */
    public boolean isImportant(String name) {
        int idx = cssEngine.getPropertyIndex(name);
        for (int i = 0; i < declaration.size(); i++) {
            if (idx == declaration.getIndex(i)) {
                return declaration.getPriority(i);
            }
        }
        return false;
    }

    /**
     * Returns the text of the declaration.
     */
    public String getText() {
        return declaration.toString(cssEngine);
    }

    /**
     * Returns the length of the declaration.
     */
    public int getLength() {
        return declaration.size();
    }

    /**
     * Returns the value at the given.
     */
    public String item(int idx) {
        return cssEngine.getPropertyName(declaration.getIndex(idx));
    }
}
