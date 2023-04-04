package org.apache.batik.svggen;

import java.awt.color.ColorSpace;

/**
 * @author christian.kriebel This is not a real implementation of a CMYK ColorSpace. It's used as a
 *         placeholder.
 */

public class SpotColorSpace extends ColorSpace {

    public SpotColorSpace() {
        super(9, 4);
    }

    private String spotColorName;

    public static SpotColorSpace getInstance() {
        return new SpotColorSpace();
    }

    private String tint;

    /**
     * @return Returns the spotColorName.
     */
    public String getSpotColorName() {
        return spotColorName;
    }

    /**
     * @param spotColorName The spotColorName to set.
     */
    public void setSpotColorName(String spotColorName) {
        this.spotColorName = spotColorName;
    }

    /**
     * @return Returns the tint.
     */
    public String getTint() {
        return tint;
    }

    /**
     * @param tint The tint to set.
     */
    public void setTint(String tint) {
        this.tint = tint;
    }

    public float[] toRGB(float[] colorvalue) {
        return colorvalue;
    }

    public float[] fromRGB(float[] rgbvalue) {
        return rgbvalue;
    }

    public float[] toCIEXYZ(float[] colorvalue) {
        return colorvalue;
    }

    public float[] fromCIEXYZ(float[] colorvalue) {
        return colorvalue;
    }

}