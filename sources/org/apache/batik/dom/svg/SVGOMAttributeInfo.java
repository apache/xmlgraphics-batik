package org.apache.batik.dom.svg;

/**
 * This helper-class is a wrapper to hold the svgType and the isAnimatable-flag.
 * This data is stored in a lookup-map within most SVGOMxxx-classes.
 *
 * @author DvHolten
 * @version $Id: DyPATransfer.java 1325 2005-11-04 12:59:57Z dvholten $
 */
final class SVGOMAttributeInfo {

    final int svgType;

    final boolean isAnimatable;

    SVGOMAttributeInfo( int typ, boolean flagAnim ){
        svgType = typ;
        isAnimatable = flagAnim;
    }

    int getSVGType(){
        return svgType;
    }

    boolean getIsAnimatable(){
        return isAnimatable;
    }

}
