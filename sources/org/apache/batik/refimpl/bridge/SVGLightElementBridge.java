/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.refimpl.bridge;

import java.awt.Color;

import org.apache.batik.bridge.IllegalAttributeValueException;

import org.apache.batik.gvt.filter.Light;
import org.apache.batik.gvt.filter.DistantLight;
import org.apache.batik.gvt.filter.PointLight;
import org.apache.batik.gvt.filter.SpotLight;

import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Converts a light element into a Light implementation.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGLightElementBridge implements SVGConstants{
    /**
     * Utility routine for lighting filter bridges
     */
    public static Light createLight(Element elt, Color color){
        Light light = null;
        if(elt.getNodeName().equals(SVG_FE_DISTANT_LIGHT_TAG)){
            String azimuthStr 
                = elt.getAttributeNS(null, SVG_AZIMUTH_ATTRIBUTE);

            if(azimuthStr.equals("")){
                azimuthStr = SVG_DEFAULT_VALUE_FE_DISTANT_LIGHT_AZIMUTH;
            }

            double azimuth 
                = SVGUtilities.convertSVGNumber(SVG_AZIMUTH_ATTRIBUTE, 
                                                azimuthStr);

            String elevationStr 
                = elt.getAttributeNS(null, SVG_ELEVATION_ATTRIBUTE);

            if(elevationStr.equals("")){
                elevationStr = SVG_DEFAULT_VALUE_FE_DISTANT_LIGHT_ELEVATION;
            }

            double elevation
                = SVGUtilities.convertSVGNumber(SVG_ELEVATION_ATTRIBUTE, 
                                                elevationStr);

            light = new DistantLight(azimuth, elevation, color);
        }
        else if(elt.getNodeName().equals(SVG_FE_POINT_LIGHT_TAG)){
            String xStr
                = elt.getAttributeNS(null, SVG_X_ATTRIBUTE);

            if(xStr.equals("")){
                xStr = SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_X;
            }

            double x 
                = SVGUtilities.convertSVGNumber(SVG_X_ATTRIBUTE,
                                                xStr);

            String yStr
                = elt.getAttributeNS(null, SVG_Y_ATTRIBUTE);

            if(yStr.equals("")){
                yStr = SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_Y;
            }

            double y 
                = SVGUtilities.convertSVGNumber(SVG_Y_ATTRIBUTE,
                                                yStr);

            String zStr
                = elt.getAttributeNS(null, SVG_Z_ATTRIBUTE);

            if(zStr.equals("")){
                zStr = SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_Z;
            }

            double z 
                = SVGUtilities.convertSVGNumber(SVG_Z_ATTRIBUTE,
                                                zStr);

            light = new PointLight(x, y, z, color);
        }
        else if(elt.getNodeName().equals(SVG_FE_SPOT_LIGHT_TAG)){
            String xStr
                = elt.getAttributeNS(null, SVG_X_ATTRIBUTE);

            if(xStr.equals("")){
                xStr = SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_X;
            }

            double x 
                = SVGUtilities.convertSVGNumber(SVG_X_ATTRIBUTE,
                                                xStr);

            String yStr
                = elt.getAttributeNS(null, SVG_Y_ATTRIBUTE);

            if(yStr.equals("")){
                yStr = SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_Y;
            }

            double y 
                = SVGUtilities.convertSVGNumber(SVG_Y_ATTRIBUTE,
                                                yStr);

            String zStr
                = elt.getAttributeNS(null, SVG_Z_ATTRIBUTE);

            if(zStr.equals("")){
                zStr = SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_Z;
            }

            double z 
                = SVGUtilities.convertSVGNumber(SVG_Z_ATTRIBUTE,
                                                zStr);

            String pointsAtXStr
                = elt.getAttributeNS(null, SVG_POINTS_AT_X_ATTRIBUTE);

            if(pointsAtXStr.equals("")){
                pointsAtXStr = SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_POINTS_AT_X;
            }

            double pointsAtX 
                = SVGUtilities.convertSVGNumber(SVG_POINTS_AT_X_ATTRIBUTE,
                                                pointsAtXStr);

            String pointsAtYStr
                = elt.getAttributeNS(null, SVG_POINTS_AT_Y_ATTRIBUTE);

            if(pointsAtYStr.equals("")){
                pointsAtYStr = SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_POINTS_AT_Y;
            }

            double pointsAtY 
                = SVGUtilities.convertSVGNumber(SVG_POINTS_AT_Y_ATTRIBUTE,
                                                pointsAtYStr);

            String pointsAtZStr
                = elt.getAttributeNS(null, SVG_POINTS_AT_Z_ATTRIBUTE);

            if(pointsAtZStr.equals("")){
                pointsAtZStr = SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_POINTS_AT_Z;
            }

            double pointsAtZ 
                = SVGUtilities.convertSVGNumber(SVG_POINTS_AT_Z_ATTRIBUTE,
                                                pointsAtZStr);


            String specularExponentStr
                = elt.getAttributeNS(null, SVG_SPECULAR_EXPONENT_ATTRIBUTE);

            if(specularExponentStr.equals("")){
                specularExponentStr = SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_SPECULAR_EXPONENT;
            }

            double specularExponent 
                = SVGUtilities.convertSVGNumber(SVG_SPECULAR_EXPONENT_ATTRIBUTE,
                                                specularExponentStr);


            String limitingConeAngleStr
                = elt.getAttributeNS(null, SVG_LIMITING_CONE_ANGLE_ATTRIBUTE);

            if(limitingConeAngleStr.equals("")){
                limitingConeAngleStr = SVG_DEFAULT_VALUE_FE_SPOT_LIGHT_LIMITING_CONE_ANGLE;
            }

            double limitingConeAngle 
                = SVGUtilities.convertSVGNumber(SVG_LIMITING_CONE_ANGLE_ATTRIBUTE,
                                                limitingConeAngleStr);



            light = new SpotLight(x, y, z, pointsAtX, pointsAtY, pointsAtZ,
                                  specularExponent, limitingConeAngle, color);
        }
        else{
            throw new Error("Not implemented yet");
        }

        return light;
    }
}
