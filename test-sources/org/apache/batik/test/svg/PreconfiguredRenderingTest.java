/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.svg;

import java.io.File;

/**
 * Convenience class for creating a SVGRenderingAccuracyTest with predefined
 * rules for the various configuration parameters.
 *
 * @author <a href="vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public abstract class PreconfiguredRenderingTest extends SVGRenderingAccuracyTest {
    /**
     * Generic constants
     */
    public static final String PNG_EXTENSION = ".png";

    public static final String SVG_EXTENSION = ".svg";

    public static final char PATH_SEPARATOR = '/';

    /**
     * For preconfigured tests, the configuration has to be 
     * derived from the test identifier. The identifier should
     * characterize the SVG file to be tested.
     */
    public void setId(String id){
        super.setId(id);

        String svgFile = id;

        String[] dirNfile = breakSVGFile(svgFile);

        setConfig(buildSVGURL(dirNfile[0], dirNfile[1]),
                  buildRefImgURL(dirNfile[0], dirNfile[1]));

        setVariationURL(buildVariationURL(dirNfile[0], dirNfile[1]));
        setSaveVariation(new File(buildSaveVariationFile(dirNfile[0], dirNfile[1])));
        setCandidateReference(new File(buildCandidateReferenceFile(dirNfile[0],dirNfile[1])));
    }

    /**
     * Make the name as simple as possible. For preconfigured SVG files, 
     * we use the test id, which is the relevant identifier for the test
     * user.
     */
    public String getName(){
        return getId();
    }

    /**
     * Gives a chance to the subclass to prepend a prefix to the 
     * svgFile name.
     * The svgURL is built as:
     * getSVGURLPrefix() + svgDir + svgFile
     */
    protected String buildSVGURL(String svgDir, String svgFile){
        return getSVGURLPrefix() + svgDir +
            svgFile + SVG_EXTENSION;
    }

    protected abstract String getSVGURLPrefix();

    
    /**
     * Gives a chance to the subclass to control the construction
     * of the reference PNG file from the svgFile name
     * The refImgURL is built as:
     * getRefImagePrefix() + svgDir + getRefImageSuffix() + svgFile
     */
    protected String buildRefImgURL(String svgDir, String svgFile){
        return getRefImagePrefix() + svgDir + getRefImageSuffix() + svgFile + PNG_EXTENSION;
    }

    protected abstract String getRefImagePrefix();

    protected abstract String getRefImageSuffix();

    /**
     * Gives a chance to the subclass to control the construction
     * of the variation URL, which is built as:
     * getVariationPrefix() + svgDir + getVariationSuffix() + svgFile + PNG_EXTENSION
     */
    public String buildVariationURL(String svgDir, String svgFile){
        return getVariationPrefix() + svgDir + getVariationSuffix() + svgFile + PNG_EXTENSION;
    }

    protected abstract String getVariationPrefix();

    protected abstract String getVariationSuffix();

    /**
     * Gives a chance to the subclass to control the construction
     * of the saveVariation URL, which is built as:
     * getSaveVariationPrefix() + svgDir + getSaveVariationSuffix() + svgFile + PNG_EXTENSION
     */
    public String  buildSaveVariationFile(String svgDir, String svgFile){
        return getSaveVariationPrefix() + svgDir + getSaveVariationSuffix() + svgFile + PNG_EXTENSION;
    }

    protected abstract String getSaveVariationPrefix();

    protected abstract String getSaveVariationSuffix();

    /**
     * Gives a chance to the subclass to control the construction
     * of the candidateReference URL, which is built as:
     * getCandidatereferencePrefix() + svgDir + getCandidatereferenceSuffix() + svgFile + PNG_EXTENSION
     */
    public String  buildCandidateReferenceFile(String svgDir, String svgFile){
        return getCandidateReferencePrefix() + svgDir + getCandidateReferenceSuffix() + svgFile + PNG_EXTENSION;
    }

    protected abstract String getCandidateReferencePrefix();

    protected abstract String getCandidateReferenceSuffix();


    protected String[] breakSVGFile(String svgFile){
        if(svgFile == null || !svgFile.endsWith(SVG_EXTENSION)){
            throw new IllegalArgumentException(svgFile);
        }

        svgFile = svgFile.substring(0, svgFile.length() - SVG_EXTENSION.length());

        int fileNameStart = svgFile.lastIndexOf(PATH_SEPARATOR);
        String svgDir = "";
        if(fileNameStart != -1){
            if(svgFile.length() < fileNameStart + 2){
                // Nothing after PATH_SEPARATOR
                throw new IllegalArgumentException(svgFile);
            }
            svgDir = svgFile.substring(0, fileNameStart + 1);
            svgFile = svgFile.substring(fileNameStart + 1);
        }

        return new String[]{svgDir, svgFile};
    }

}
