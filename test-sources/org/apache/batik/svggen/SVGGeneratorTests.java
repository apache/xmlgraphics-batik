/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.svggen;

import java.io.File;
import java.net.URL;

import org.apache.batik.test.Test;
import org.apache.batik.test.DefaultTestSuite;
import org.apache.batik.test.svg.SVGRenderingAccuracyTest;
import org.apache.batik.test.util.ImageCompareTest;

/**
 * This test validates that a given rendering sequence, modeled
 * by a <tt>Painter</tt> by doing several subtests for a 
 * single input class:
 * + SVGAccuracyTest with several configurations
 * + SVGRenderingAccuracyTest
 * + ImageComparisonTest between the rendering of the generated
 *   SVG for various configurations.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGGeneratorTests extends DefaultTestSuite {
    public static final String GENERATOR_REFERENCE_BASE 
        = "test-references/org/apache/batik/svggen/";

    public static final String RENDERING_DIR
        = "rendering";

    public static final String ACCEPTED_VARIATION_DIR 
        = "accepted-variation";

    public static final String CANDIDATE_VARIATION_DIR
        = "candidate-variation";

    public static final String CANDIDATE_REF_DIR
        = "candidate-ref";

    public static final String RENDERING_CANDIDATE_REF_DIR
        = "candidate-reference";

    public static final String PNG_EXTENSION
        = ".png";

    public static final String SVG_EXTENSION
        = ".svg";

    public static final String PLAIN_GENERATION_PREFIX = "";

    public static final String CUSTOM_CONTEXT_GENERATION_PREFIX = "Context";

    /**
     * @param name of the <tt>Painter</tt> class
     */
    public SVGGeneratorTests(){
    }

    /**
     * The id should be the Painter's class name
     * prefixed with the package name defined in
     * getPackageName
     */
    public void setId(String id){
        super.setId(id);
        String clName = getPackageName() + "." + id;
        Class cl = null;

        try{
            cl = Class.forName(clName);
        }catch(ClassNotFoundException e){
            throw new IllegalArgumentException(clName);
        }
        
        Object o = null;

        try {
            o = cl.newInstance();
        }catch(Exception e){
            throw new IllegalArgumentException(clName);
        }

        if(!(o instanceof Painter)){
            throw new IllegalArgumentException(clName);
        }

        Painter painter = (Painter)o;

        addTest(makeSVGAccuracyTest(painter, id));
        addTest(makeGeneratorContext(painter, id));
        addTest(makeSVGRenderingAccuracyTest(painter, id, PLAIN_GENERATION_PREFIX));
        addTest(makeSVGRenderingAccuracyTest(painter, id, CUSTOM_CONTEXT_GENERATION_PREFIX));
        addTest(makeImageCompareTest(painter, id, PLAIN_GENERATION_PREFIX, 
                                     CUSTOM_CONTEXT_GENERATION_PREFIX));
    }

    /**
     * For the Generator test, the relevant name is the id
     */
    public String getName(){
        return "SVGGeneratorTest - " + getId();
    }
        
    protected String getPackageName(){
        return "org.apache.batik.svggen";
    }

    private Test makeImageCompareTest(Painter painter,
                                      String id,
                                      String prefixA,
                                      String prefixB){
        String cl = getNonQualifiedClassName(painter);
        String clA = prefixA + cl;
        String clB = prefixB + cl;
        String testReferenceA = GENERATOR_REFERENCE_BASE + RENDERING_DIR + "/" + clA + PNG_EXTENSION;
        String testReferenceB = GENERATOR_REFERENCE_BASE + RENDERING_DIR + "/" + clB + PNG_EXTENSION;
        ImageCompareTest t = new ImageCompareTest(testReferenceA, testReferenceB);
        t.setName(id + "-RenderingComparison");
        t.setId(id + ".renderingComparison");
        return t;
    }

    private Test makeSVGRenderingAccuracyTest(Painter painter, String id, String prefix){
        String cl = prefix + getNonQualifiedClassName(painter);
        String testSource = GENERATOR_REFERENCE_BASE + cl + SVG_EXTENSION;
        String testReference = GENERATOR_REFERENCE_BASE + RENDERING_DIR + "/" + cl + PNG_EXTENSION;
        String variationURL = GENERATOR_REFERENCE_BASE + RENDERING_DIR + "/" + ACCEPTED_VARIATION_DIR + "/" + cl + PNG_EXTENSION;
        String saveVariation = GENERATOR_REFERENCE_BASE + RENDERING_DIR + "/" + CANDIDATE_VARIATION_DIR + "/" + cl + PNG_EXTENSION;
        String candidateReference = GENERATOR_REFERENCE_BASE + RENDERING_DIR + "/" + RENDERING_CANDIDATE_REF_DIR + "/" + cl + PNG_EXTENSION;

        SVGRenderingAccuracyTest test = new SVGRenderingAccuracyTest(testSource, testReference);
        test.setVariationURL(variationURL);
        test.setSaveVariation(new File(saveVariation));
        test.setCandidateReference(new File(candidateReference));

        test.setName(id + "-" + prefix + "RenderingCheck");
        test.setId(id + "." + prefix + "renderingCheck");
        return test;
    }

    private Test makeGeneratorContext(Painter painter, String id){
        String cl = CUSTOM_CONTEXT_GENERATION_PREFIX + getNonQualifiedClassName(painter);

        GeneratorContext test 
            = new GeneratorContext(painter, makeURL(painter, CUSTOM_CONTEXT_GENERATION_PREFIX));

        test.setSaveSVG(new File(GENERATOR_REFERENCE_BASE + CANDIDATE_REF_DIR + "/" + cl + SVG_EXTENSION));
        test.setName(id + "-ConfiguredContextGeneration");
        test.setId(id + ".configuredContextGeneration");
        return test;
    }

    private Test makeSVGAccuracyTest(Painter painter, String id){
        String cl = PLAIN_GENERATION_PREFIX + getNonQualifiedClassName(painter);

        SVGAccuracyTest test 
            = new SVGAccuracyTest(painter, makeURL(painter, PLAIN_GENERATION_PREFIX));

        test.setSaveSVG(new File(GENERATOR_REFERENCE_BASE + CANDIDATE_REF_DIR + "/" + cl + SVG_EXTENSION));
        test.setName(id + "-DefaultContextGeneration");
        test.setId(id + ".defaultContextGeneration");
        return test;
    }

    private String getNonQualifiedClassName(Painter painter){
        String cl = painter.getClass().getName();
        int n = cl.lastIndexOf(".");
        return cl.substring(n+1);
    }

    private URL makeURL(Painter painter, String prefix){
        String urlString = "file:" + GENERATOR_REFERENCE_BASE
            + prefix + getNonQualifiedClassName(painter) + SVG_EXTENSION;
        URL url = null;
        try{
            url = new URL(urlString);
        }catch(Exception e){
            throw new Error(); // Should not happen
        }

        return url;
    }
}

