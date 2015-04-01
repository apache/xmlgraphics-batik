package org.apache.batik.dom.svg;

import org.w3c.dom.svg.SVGNumberList;

/**
 * Representation of the item SVGNumber.
 */
public class SVGNumberItem extends AbstractSVGNumber implements SVGItem {

    /**
     * The {@link SVGNumberList} this item belongs to.
     */
    protected AbstractSVGList parentList;

    /**
     * Creates a new SVGNumberItem.
     */
    public SVGNumberItem(float value) {
        this.value = value;
    }

    /**
     * Returns a String representation of the number.
     */
    public String getValueAsString() {
        return Float.toString(value);
    }

    /**
     * Associates this item to the given {@link SVGNumberList}.
     */
    public void setParent(AbstractSVGList list) {
        parentList = list;
    }

    /**
     * Returns the list the item belongs to.
     */
    public AbstractSVGList getParent() {
        return parentList;
    }

    /**
     * Notifies the parent {@link SVGNumberList} that this item's value
     * has changed.
     */
    protected void reset() {
        if (parentList != null) {
            parentList.itemChanged();
        }
    }
}