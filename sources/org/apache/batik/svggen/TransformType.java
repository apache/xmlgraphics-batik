/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

/**
 * Enumeration for transformation types.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class TransformType{
    /*
     * Transform type constants
     */
    public static final int TRANSFORM_TRANSLATE = 0;
    public static final int TRANSFORM_ROTATE = 1;
    public static final int TRANSFORM_SCALE = 2;
    public static final int TRANSFORM_SHEAR = 3;
    public static final int TRANSFORM_GENERAL = 4;

    /**
     * Strings describing the elementary transforms
     */
    public static final String TRANSLATE_STRING = "translate";
    public static final String ROTATE_STRING = "rotate";
    public static final String SCALE_STRING = "scale";
    public static final String SHEAR_STRING = "shear";
    public static final String GENERAL_STRING = "general";

    /**
     * TransformType values
     */
    public static final TransformType TRANSLATE = new TransformType(TRANSFORM_TRANSLATE, TRANSLATE_STRING);
    public static final TransformType ROTATE = new TransformType(TRANSFORM_ROTATE, ROTATE_STRING);
    public static final TransformType SCALE = new TransformType(TRANSFORM_SCALE, SCALE_STRING);
    public static final TransformType SHEAR = new TransformType(TRANSFORM_SHEAR, SHEAR_STRING);
    public static final TransformType GENERAL = new TransformType(TRANSFORM_GENERAL, GENERAL_STRING);

    /**
     * All values
     */
    private static final TransformType[] enumValues = { TRANSLATE,
                                                        ROTATE,
                                                        SCALE,
                                                        SHEAR,
                                                        GENERAL };

    private String desc;
    private int val;

    /**
     * Constructor is private so that no instances other than
     * the ones in the enumeration can be created.
     * @see #readResolve
     */
    private TransformType(int val, String desc){
        this.desc = desc;
        this.val = val;
    }

    /**
     * @return description
     */
    public String toString(){
        return desc;
    }

    /**
     * Convenience for enumeration switching
     * i.e. switch(transformType.toInt()){
     *       case TransformType.TRANSFORM_TRANSLATE:
     *        ....
     *       case TransformType.TRANSFORM_ROTATE:
     */
    public int toInt(){
        return val;
    }

    /**
     *  This is called by the serialization code before it returns an unserialized
     * object. To provide for unicity of instances, the instance that was read
     * is replaced by its static equivalent
     */
    public Object readResolve() {
        switch(val){
        case TRANSFORM_TRANSLATE:
            return TransformType.TRANSLATE;
        case TRANSFORM_ROTATE:
            return TransformType.ROTATE;
        case TRANSFORM_SCALE:
            return TransformType.SCALE;
        case TRANSFORM_SHEAR:
            return TransformType.SHEAR;
        case TRANSFORM_GENERAL:
            return TransformType.GENERAL;
        default:
            throw new Error("Unknown TransformType value");
        }
    }
}
