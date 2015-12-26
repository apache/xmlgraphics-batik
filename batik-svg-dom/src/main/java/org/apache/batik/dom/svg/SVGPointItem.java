package org.apache.batik.dom.svg;

import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGPoint;

/**
 * An {@link SVGPoint} in the list.
 */
public class SVGPointItem extends AbstractSVGItem implements SVGPoint {

    /**
     * The x value.
     */
    protected float x;

    /**
     * The y value.
     */
    protected float y;

    /**
     * Creates a new SVGPointItem.
     */
    public SVGPointItem(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Return a String representation of this SVGPoint.
     */
    protected String getStringValue() {
        return Float.toString( x )
                + ','
                + Float.toString( y );
    }

    /**
     * <b>DOM</b>: Implements {@link SVGPoint#getX()}.
     */
    public float getX() {
        return x;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGPoint#getY()}.
     */
    public float getY() {
        return y;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGPoint#setX(float)}.
     */
    public void setX(float x) {
        this.x = x;
        resetAttribute();
    }

    /**
     * <b>DOM</b>: Implements {@link SVGPoint#setY(float)}.
     */
    public void setY(float y) {
        this.y = y;
        resetAttribute();
    }

    /**
     * <b>DOM</b>: Implements {@link SVGPoint#matrixTransform(SVGMatrix)}.
     */
    public SVGPoint matrixTransform(SVGMatrix matrix) {
        return SVGOMPoint.matrixTransform(this, matrix);
    }
}