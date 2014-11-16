/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.svggen;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.batik.test.Test;
import org.apache.batik.test.DefaultTestSuite;
import org.apache.batik.test.svg.SVGRenderingAccuracyTestCase;
import org.apache.batik.test.util.ImageCompareTestCase;

/**
 * This test validates that a given rendering sequence, modeled
 * by a <code>Painter</code> by doing several subtests for a
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

    public static final String[] VARIATION_PLATFORMS =
        org.apache.batik.test.svg.PreconfiguredRenderingTestCase.DEFAULT_VARIATION_PLATFORMS;

    private static final String ACCEPTED_REF_DIR = "accepted-ref";

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
        ImageCompareTestCase t = new ImageCompareTestCase(testReferenceA, testReferenceB);
        t.setName(id + "-RenderingComparison");
        t.setId(id + ".renderingComparison");
        return t;
    }

    private Test makeSVGRenderingAccuracyTest(Painter painter, String id, String prefix){
        String cl = prefix + getNonQualifiedClassName(painter);
        String testSource = GENERATOR_REFERENCE_BASE + cl + SVG_EXTENSION;
        String testReference = GENERATOR_REFERENCE_BASE + RENDERING_DIR + "/" + cl + PNG_EXTENSION;
        String[] variationURLs = new String[VARIATION_PLATFORMS.length + 1];
        variationURLs[0] = GENERATOR_REFERENCE_BASE + RENDERING_DIR + "/" + ACCEPTED_VARIATION_DIR + "/" + cl + PNG_EXTENSION;
        for (int i = 0; i < VARIATION_PLATFORMS.length; i++) {
            variationURLs[i + 1] = GENERATOR_REFERENCE_BASE + RENDERING_DIR + "/" + ACCEPTED_VARIATION_DIR + "/" + cl + '_' + VARIATION_PLATFORMS[i] + PNG_EXTENSION;
        }
        String saveVariation = GENERATOR_REFERENCE_BASE + RENDERING_DIR + "/" + CANDIDATE_VARIATION_DIR + "/" + cl + PNG_EXTENSION;
        String candidateReference = GENERATOR_REFERENCE_BASE + RENDERING_DIR + "/" + RENDERING_CANDIDATE_REF_DIR + "/" + cl + PNG_EXTENSION;

        SVGRenderingAccuracyTestCase test = new SVGRenderingAccuracyTestCase(testSource, testReference);
        for (int i = 0; i < variationURLs.length; i++) {
            test.addVariationURL(variationURLs[i]);
        }
        test.setSaveVariation(new File(saveVariation));
        test.setCandidateReference(new File(candidateReference));

        test.setName(id + "-" + prefix + "RenderingCheck");
        test.setId(id + "." + prefix + "renderingCheck");
        return test;
    }

    private Test makeGeneratorContext(Painter painter, String id){
        String cl = CUSTOM_CONTEXT_GENERATION_PREFIX + getNonQualifiedClassName(painter);

        GeneratorContext test
            = new GeneratorContext(painter, getReferenceURL(painter, CUSTOM_CONTEXT_GENERATION_PREFIX));

        test.setSaveSVG(new File(GENERATOR_REFERENCE_BASE + CANDIDATE_REF_DIR + "/" + cl + SVG_EXTENSION));
        test.setName(id + "-ConfiguredContextGeneration");
        test.setId(id + ".configuredContextGeneration");
        return test;
    }

    private Test makeSVGAccuracyTest(Painter painter, String id){
        String cl = PLAIN_GENERATION_PREFIX + getNonQualifiedClassName(painter);

        SVGAccuracyTestCase test
            = new SVGAccuracyTestCase(painter, getReferenceURL(painter, PLAIN_GENERATION_PREFIX));

        test.setSaveSVG(new File(GENERATOR_REFERENCE_BASE + CANDIDATE_REF_DIR + "/" + cl + SVG_EXTENSION));
        test.setName(id + "-DefaultContextGeneration");
        test.setId(id + ".defaultContextGeneration");
        return test;
    }

    private String getNonQualifiedClassName(Painter painter){
        String cl = painter.getClass().getName();
        int n = cl.lastIndexOf('.');
        return cl.substring(n+1);
    }

    private URL getReferenceURL(Painter painter, String prefix){
        String suffix = prefix + getNonQualifiedClassName(painter) + SVG_EXTENSION;
        File acceptedReference = new File(GENERATOR_REFERENCE_BASE + ACCEPTED_REF_DIR + '/' + suffix);
        if (acceptedReference.exists()) {
            return file2URL(acceptedReference);
        } else {
            File reference = new File(GENERATOR_REFERENCE_BASE + suffix);
            return file2URL(reference);
        }
    }

    private URL file2URL(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}

