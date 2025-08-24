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
package org.apache.batik.test.xml;

import org.apache.commons.io.FileUtils;

import org.apache.batik.script.rhino.RhinoClassShutter;
import org.apache.batik.test.DefaultTestSuite;
import org.apache.batik.test.Test;
import org.apache.batik.test.TestException;
import org.apache.batik.test.TestReport;

import org.apache.batik.test.TestReportProcessor;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class JUnitRunnerTestCase {

    @BeforeClass
    public static void beforeClass() throws IOException {
        String sm = "grant { permission java.security.AllPermission; };";
        File tmp = File.createTempFile("batik", "sm");
        FileOutputStream fos = new FileOutputStream(tmp);
        fos.write(sm.getBytes());
        fos.close();
        tmp.deleteOnExit();
        System.setProperty("java.security.policy", tmp.getAbsolutePath());
        RhinoClassShutter.WHITELIST.addAll(Arrays.asList("java.io.PrintStream", "java.lang.System", "java.net.URL",
                ".*Permission", "org.w3c.dom.*", "org.apache.batik.w3c.*", "org.apache.batik.anim.*",
                "org.apache.batik.dom.*", "org.apache.batik.css.*"));
    }

    @Parameterized.Parameters
    public static Collection<Test[]> data() throws ParserConfigurationException, SAXException, TestException, IOException {
        return getTests();
    }

    private static Collection<Test[]> getTests() throws ParserConfigurationException, IOException, SAXException, TestException {
        new File("test-references/org/apache/batik/ext/awt/geom/candidate").mkdir();
        new File("test-references/org/apache/batik/ext/awt/geom/variation").mkdir();
        for (File file : FileUtils.listFiles(new File("test-references"), new String[]{"png"}, true)) {
            file = file.getParentFile();
            if (!file.getName().contains("candidate")) {
                new File(file, "candidate-variation").mkdir();
                new File(file, "candidate-reference").mkdir();
            }
        }
        File uriStr = new File("test-resources/org/apache/batik/test/regard.xml");
        URL url = uriStr.toURI().toURL();
        DocumentBuilder docBuilder
                = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = docBuilder.parse(url.toString());

        final List<Test[]> tests = new ArrayList<Test[]>();

        XMLTestSuiteRunner r = new XMLTestSuiteRunner() {
            protected TestReport runTest(Test test) {
                addTests(test, tests);
                return null;
            }

            protected TestReportProcessor[] extractTestReportProcessor(Element element) throws TestException {
                return null;
            }
        };
        r.run(doc, null);
        return tests;
    }

    private static void addTests(Test test, List<Test[]> tests) {
        if (System.getProperty("os.name").startsWith("Windows")) {
            EXCLUDE = new ArrayList<>(EXCLUDE);
            EXCLUDE.add("PerformanceTestValidator");
            EXCLUDE.add("PerformanceTestSanity");
        }
        if (test instanceof DefaultTestSuite) {
            for (Test child : ((DefaultTestSuite) test).getChildrenTests()) {
                if (!EXCLUDE.contains(getId(test))) {
                    addTests(child, tests);
                }
            }
            return;
        }
        if (!EXCLUDE.contains(getId(test))) {
            tests.add(new Test[]{test});
        }
    }

    private static String getId(Test test) {
        String id = test.getId();
        if (id == null || id.length() == 0) {
            id = test.getName();
        }
        return id;
    }

    private Test test;

    public JUnitRunnerTestCase(Test test) {
        this.test = test;
    }
    
    @org.junit.Test
    public void test() throws ParserConfigurationException, SAXException, TestException, IOException {
        String id = getId(test);
        System.out.println("Running: " + id);
        TestReport report = test.run();
        StringBuilder error = new StringBuilder();
        if (!report.hasPassed()) {
            error.append("Failed: ").append(id).append("\n");
            if (report.getDescription() != null) {
                for (TestReport.Entry entry : report.getDescription()) {
                    error.append(entry.getKey()).append(" ").append(entry.getValue()).append("\n");
                }
            }
        }
        Assert.assertTrue(error.toString(), report.hasPassed());
    }

    private static List<String> EXCLUDE = Arrays.asList(
"ShowSVG",
"org.apache.batik.svggen.SVGAccuracyTestValidator$SameAsReferenceImage",
"Color1.renderingCheck",
"Lookup.renderingCheck",
"Rescale.renderingCheck",
"TextSpacePreserve.renderingCheck",
"NullSetSVGDocumentTest",
"samples/tests/spec/scripting/memoryLeak1.svg",
"samples/tests/spec/scripting/primaryDoc.svg",
"NullURITest",
"DoubleStringPerformanceTest",
"text.selection.latin",
"text.selection.latin-ext",
"text.selection.cyrillic",
"text.selection.greek",
"text.selection.hebrew",
"text.selection.arabic",
"text.selection.svgpath.middle50",
"text.selection.svgpath.start35",
"text.selection.vertpath.supersub",
"text.selection.vertpath.beforeafter",
"text.selection.vertpath.negpos",
"text.selection.vert.orient0",
"text.selection.vert.orient90",
"text.selection.vert.orient180",
"text.selection.vert.orient270",
"SetSVGDocumentTest",
"samples/tests/resources/wmf/black_shapes.wmf",
"samples/tests/resources/wmf/imageWMF.wmf",
"samples/tests/resources/wmf/negApmText1.wmf",
"samples/tests/resources/wmf/negApmText2.wmf",
"samples/tests/resources/wmf/testChart.wmf",
"samples/tests/resources/wmf/textGreek.wmf",
"transcoder.image.uri",
"transcoder.image.istream",
"transcoder.image.genericdocument",
"transcoder.image.reader",
"transcoder.image.dom2",
"transcoder.image.hints.width200",
"transcoder.image.hints.height200",
"transcoder.image.hints.widthheight200",
"transcoder.image.hints.width600",
"transcoder.image.hints.height600",
"transcoder.image.hints.widthheight600",
"transcoder.image.hints.width200.noViewBox",
"transcoder.image.hints.height200.noViewBox",
"transcoder.image.hints.widthheight200.noViewBox",
"transcoder.image.hints.width600.noViewBox",
"transcoder.image.hints.height600.noViewBox",
"transcoder.image.hints.widthheight600.noViewBox",
"transcoder.image.hints.maxWidth200",
"transcoder.image.hints.maxHeight200",
"transcoder.image.hints.maxWidthHeight200",
"transcoder.image.hints.maxWidth200.overrideHints",
"transcoder.image.hints.maxHeight200.overrideHints",
"transcoder.image.hints.maxWidthHeight200.overrideHints",
"transcoder.image.hints.language.en",
"transcoder.image.hints.language.fr",
"transcoder.image.hints.language.default",
"transcoder.image.hints.aoi.NW",
"transcoder.image.hints.aoi.NE",
"transcoder.image.hints.aoi.SW",
"transcoder.image.hints.aoi.SE",
"transcoder.image.hints.aoi.C",
"transcoder.image.hints.media.screen",
"transcoder.image.hints.defaultFontFamily.Arial",
"transcoder.image.hints.defaultFontFamily.Times",
"trancoder.image.hints.defaultFontFamily.TotoTimes",
"transcoder.image.hints.alternateStylesheet.s1",
"transcoder.image.hints.alternateStylesheet.s2",
"transcoder.image.hints.alternateStylesheet.s3",
"transcoder.image.hints.alternateStylesheet.s4",
"transcoder.image.hints.alternateStylesheet.s5",
"transcoder.image.hints.px2mm.96dpi",
"transcoder.image.hints.px2mm.72dpi",
"samples/anne.svg",
"samples/asf-logo.svg",
"samples/barChart.svg",
"samples/batik3D.svg",
"samples/batikBatik.svg",
"samples/batikFX.svg",
"samples/batikLogo.svg",
"samples/chessboard.svg",
"samples/gradients.svg",
"samples/logoTexture.svg",
"samples/mapSpain.svg",
"samples/mapWaadt.svg",
"samples/moonPhases.svg",
"samples/sizeOfSun.svg",
"samples/sunRise.svg",
"samples/sydney.svg",
"samples/textRotate.svg",
"samples/tests/spec12/text/flowBidi.svg",
"samples/tests/spec12/text/flowText.svg",
"samples/tests/spec12/text/flowText2.svg",
"samples/tests/spec12/text/flowText3.svg",
"samples/tests/spec12/text/flowText4.svg",
"samples/tests/spec12/text/flowText5.svg",
"samples/tests/spec12/text/flowRegionBreak.svg",
"samples/tests/spec12/text/lineHeightFontShorthand.svg",
"samples/tests/spec12/structure/multi.svg",
"samples/tests/spec12/structure/multi2.svg",
"samples/tests/spec12/structure/opera/opera-subImage.svg",
"samples/tests/spec12/structure/opera/opera-subImageRef.svg",
"samples/tests/spec12/paints/solidColor.svg",
"samples/tests/spec12/paints/solidColor2.svg",
"samples/tests/spec12/filters/filterRegion.svg",
"samples/tests/spec12/filters/filterRegionDetailed.svg",
"samples/extensions/colorSwitch.svg",
"samples/extensions/histogramNormalization.svg",
"samples/extensions/regularPolygon.svg",
"samples/extensions/star.svg",
"samples/extensions/flowText.svg",
"samples/extensions/flowTextAlign.svg",
"samples/tests/spec/color/colorProfile.svg",
"samples/tests/spec/color/colors.svg",
"samples/tests/spec/color/systemColors.svg",
"samples/tests/spec/coordinates/percentagesAndUnits.svg",
"samples/tests/spec/coordinates/em.svg",
"samples/tests/spec/filters/enableBackground.svg",
"samples/tests/spec/filters/feColorMatrix.svg",
"samples/tests/spec/filters/feComponentTransfer.svg",
"samples/tests/spec/filters/feComponentTransfer2.svg",
"samples/tests/spec/filters/feComposite.svg",
"samples/tests/spec/filters/feConvolveMatrix.svg",
"samples/tests/spec/filters/feDisplacementMap.svg",
"samples/tests/spec/filters/feGaussianDefault.svg",
"samples/tests/spec/filters/feImage.svg",
"samples/tests/spec/filters/feImage2.svg",
"samples/tests/spec/filters/feMerge.svg",
"samples/tests/spec/filters/feMorphology.svg",
"samples/tests/spec/filters/feTileTarget.svg",
"samples/tests/spec/filters/feTurbulence.svg",
"samples/tests/spec/filters/filterRegions.svg",
"samples/tests/spec/filters/svgEnableBackground.svg",
"samples/batikCandy.svg#svgView(transform(scale(0.01)))-BumpMap1",
"samples/tests/spec/fonts/batikFont.svg",
"samples/tests/spec/fonts/fontAltGlyph.svg",
"samples/tests/spec/fonts/fontAltGlyph2.svg",
"samples/tests/spec/fonts/fontArabic.svg",
"samples/tests/spec/fonts/fontBounds.svg",
"samples/tests/spec/fonts/fontChoice.svg",
"samples/tests/spec/fonts/fontDecorations.svg",
"samples/tests/spec/fonts/fontExternalFont.svg",
"samples/tests/spec/fonts/fontFace.svg",
"samples/tests/spec/fonts/fontGlyphChoice.svg",
"samples/tests/spec/fonts/fontGlyphsBoth.svg",
"samples/tests/spec/fonts/fontGlyphsChildSVG.svg",
"samples/tests/spec/fonts/fontGlyphsD.svg",
"samples/tests/spec/fonts/fontKerning.svg",
"samples/tests/spec/fonts/fontOnPath.svg",
"samples/tests/spec/fonts/fontStyling.svg",
"samples/tests/spec/linking/anchorInsideText.svg",
"samples/tests/spec/linking/anchor.svg",
"samples/tests/spec/linking/linkingTransform.svg",
"samples/tests/spec/linking/linkingViewBox.svg",
"samples/tests/spec/linking/pointerEvents.svg",
"samples/tests/spec/linking/pointerEvents2.svg",
"samples/anne.svg#svgView(viewBox(0,0,100,200))-ViewBox1",
"samples/anne.svg#svgView(viewBox(100,50,100,200))-ViewBox2",
"samples/anne.svg#svgView(transform(translate(-100,-50)))-Transform1",
"samples/anne.svg#svgView(transform(translate(225,250)rotate(45)translate(-225,-250)))-Transform2",
"samples/anne.svg#svgView(transform(rotate(45,225,250)))-Transform3",
"samples/tests/spec/masking/clip.svg",
"samples/tests/spec/masking/maskRegions.svg",
"samples/tests/spec/masking/clipTransform.svg",
"samples/tests/spec/painting/bboxOnText.svg",
"samples/tests/spec/painting/display.svg",
"samples/tests/spec/painting/image-rendering.svg",
"samples/tests/spec/painting/markersExt.svg",
"samples/tests/spec/painting/markersMisc.svg",
"samples/tests/spec/painting/markersOrientA.svg",
"samples/tests/spec/painting/markersOrientB.svg",
"samples/tests/spec/painting/markersPreserveAspectRatio.svg",
"samples/tests/spec/painting/markersShapes.svg",
"samples/tests/spec/painting/stroke-rendering.svg",
"samples/tests/spec/painting/shape-rendering.svg",
"samples/tests/spec/painting/text-rendering.svg",
"samples/tests/spec/painting/text-rendering2.svg",
"samples/tests/spec/painting/visibility.svg",
"samples/tests/spec/paints/externalPaints.svg",
"samples/tests/spec/paints/gradientLimit.svg",
"samples/tests/spec/paints/linearGradientOrientation.svg",
"samples/tests/spec/paints/linearGradientLine.svg",
"samples/tests/spec/paints/linearGradientRepeat.svg",
"samples/tests/spec/paints/radialGradientLine.svg",
"samples/tests/spec/paints/gradientPoint.svg",
"samples/tests/spec/paints/patternPreserveAspectRatioA.svg",
"samples/tests/spec/paints/patternRegionA.svg",
"samples/tests/spec/paints/patternRegionB.svg",
"samples/tests/spec/paints/patternRegions.svg",
"samples/tests/spec/paints/radialGradient.svg",
"samples/tests/spec/paints/radialGradient2.svg",
"samples/tests/spec/paints/radialGradient3.svg",
"samples/tests/spec/paints/radialGradientOrientation.svg",
"samples/tests/spec/rendering/opacity.svg",
"samples/tests/spec/rendering/opacity2.svg",
"samples/tests/spec/rendering/paintOpacity.svg",
"samples/tests/spec/shapes/zero.svg",
"samples/tests/spec/shapes/emptyShape.svg",
"samples/tests/spec/structure/dataProtocol.svg",
"samples/tests/spec/structure/externalUseCascading.svg",
"samples/tests/spec/structure/rasterImageViewBox.svg",
"samples/tests/spec/structure/rasterImageViewBoxClip.svg",
"samples/tests/spec/structure/rasterImageViewBoxOverflow.svg",
"samples/tests/spec/structure/svgImageViewBox.svg",
"samples/tests/spec/structure/svgImageViewBoxClip.svg",
"samples/tests/spec/structure/svgImageViewBoxOverflow.svg",
"samples/tests/spec/structure/symbolViewBox.svg",
"samples/tests/spec/structure/symbolViewBoxClip.svg",
"samples/tests/spec/structure/symbolViewBoxOverflow.svg",
"samples/tests/spec/structure/tiff.svg",
"samples/tests/spec/structure/toolTips.svg",
"samples/tests/spec/structure/useMultiple.svg",
"samples/tests/spec/structure/useMultipleURI.svg",
"samples/tests/spec/structure/useStylesheet.svg",
"samples/tests/spec/structure/useStyling.svg",
"samples/tests/spec/structure/useStylingURI.svg",
"samples/tests/spec/structure/useTargets.svg",
"samples/tests/spec/structure/useTargets2.svg",
"samples/tests/spec/structure/xmlBase.svg",
"samples/tests/spec/structure/xmlBaseStyling.svg",
"samples/tests/spec/structure/requiredFeatures.svg",
"samples/tests/spec/structure/requiredFeaturesCombo.svg",
"samples/tests/spec/structure/switch.svg",
"samples/tests/spec/structure/systemLanguage.svg",
"samples/tests/spec/structure/systemLanguageDialect.svg",
"samples/tests/spec/styling/alternateStylesheet.svg",
"samples/tests/spec/styling/cssMedia.svg",
"samples/tests/spec/styling/cssMediaList.svg",
"samples/tests/spec/styling/emptyStyle.svg",
"samples/tests/spec/styling/fontShorthand.svg",
"samples/tests/spec/styling/important.svg",
"samples/tests/spec/styling/smiley.svg",
"samples/tests/spec/styling/styleElement.svg",
"samples/tests/spec/styling/cssMedia.svg-Screen",
"samples/tests/spec/styling/cssMedia.svg-Print",
"samples/tests/spec/styling/cssMedia.svg-Projection",
"samples/tests/spec/styling/alternateStylesheet.svg-Hot",
"samples/tests/spec/styling/alternateStylesheet.svg-Cold",
"samples/tests/spec/styling/smiley.svg-Smiling",
"samples/tests/spec/styling/smiley.svg-Basic Sad",
"samples/tests/spec/styling/smiley.svg-Wow!",
"samples/tests/spec/styling/smiley.svg-Grim",
"samples/tests/spec/styling/smiley.svg-Oups",
"samples/tests/spec/text/longTextOnPath.svg",
"samples/tests/spec/text/textAnchor.svg",
"samples/tests/spec/text/textAnchor2.svg",
"samples/tests/spec/text/textAnchor3.svg",
"samples/tests/spec/text/textBiDi.svg",
"samples/tests/spec/text/textBiDi2.svg",
"samples/tests/spec/text/textDecoration.svg",
"samples/tests/spec/text/textDecoration2.svg",
"samples/tests/spec/text/textEffect.svg",
"samples/tests/spec/text/textEffect2.svg",
"samples/tests/spec/text/textEffect3.svg",
"samples/tests/spec/text/textLayout.svg",
"samples/tests/spec/text/textLayout2.svg",
"samples/tests/spec/text/textLength.svg",
"samples/tests/spec/text/textOnPath.svg",
"samples/tests/spec/text/textOnPath2.svg",
"samples/tests/spec/text/textOnPath3.svg",
"samples/tests/spec/text/textOnPathSpaces.svg",
"samples/tests/spec/text/textPCDATA.svg",
"samples/tests/spec/text/textProperties.svg",
"samples/tests/spec/text/textProperties2.svg",
"samples/tests/spec/text/textStyles.svg",
"samples/tests/spec/text/verticalText.svg",
"samples/tests/spec/text/verticalTextOnPath.svg",
"samples/tests/spec/text/textPosition.svg",
"samples/tests/spec/text/textPosition2.svg",
"samples/tests/spec/text/textGlyphOrientationHorizontal.svg",
"samples/tests/spec/text/xmlSpace.svg",
"samples/tests/spec/scripting/add.svg",
"samples/tests/spec/scripting/bbox.svg",
"samples/tests/spec/scripting/circle.svg",
"samples/tests/spec/scripting/display.svg",
"samples/tests/spec/scripting/domSVGColor.svg",
"samples/tests/spec/scripting/ellipse.svg",
"samples/tests/spec/scripting/enclosureList.svg",
"samples/tests/spec/scripting/enclosureList2.svg",
"samples/tests/spec/scripting/fill.svg",
"samples/tests/spec/scripting/getElementById.svg",
"samples/tests/spec/scripting/imageraster.svg",
"samples/tests/spec/scripting/imagesvg.svg",
"samples/tests/spec/scripting/intersectionList.svg",
"samples/tests/spec/scripting/intersectionList2.svg",
"samples/tests/spec/scripting/line.svg",
"samples/tests/spec/scripting/nestedsvg.svg",
"samples/tests/spec/scripting/normalizedPathTest.svg",
"samples/tests/spec/scripting/paintType.svg",
"samples/tests/spec/scripting/path.svg",
"samples/tests/spec/scripting/pathLength.svg",
"samples/tests/spec/scripting/path_pathSegList_create.svg",
"samples/tests/spec/scripting/path_pathSegList1.svg",
"samples/tests/spec/scripting/path_pathSegList2.svg",
"samples/tests/spec/scripting/polygon.svg",
"samples/tests/spec/scripting/polygon_points1.svg",
"samples/tests/spec/scripting/polygon_points2.svg",
"samples/tests/spec/scripting/polyline.svg",
"samples/tests/spec/scripting/polyline_points1.svg",
"samples/tests/spec/scripting/polyline_points2.svg",
"samples/tests/spec/scripting/rect.svg",
"samples/tests/spec/scripting/relativeURI.svg",
"samples/tests/spec/scripting/remove.svg",
"samples/tests/spec/scripting/removeOnclick.svg",
"samples/tests/spec/scripting/text.svg",
"samples/tests/spec/scripting/textAllProperties.svg",
"samples/tests/spec/scripting/textProperties.svg",
"samples/tests/spec/scripting/text_children1.svg",
"samples/tests/spec/scripting/text_children2.svg",
"samples/tests/spec/scripting/text_children3.svg",
"samples/tests/spec/scripting/text_dxlist1.svg",
"samples/tests/spec/scripting/text_dxlist2.svg",
"samples/tests/spec/scripting/text_dylist1.svg",
"samples/tests/spec/scripting/text_dylist2.svg",
"samples/tests/spec/scripting/text_xlist1.svg",
"samples/tests/spec/scripting/text_xlist2.svg",
"samples/tests/spec/scripting/text_ylist1.svg",
"samples/tests/spec/scripting/text_ylist2.svg",
"samples/tests/spec/scripting/textpathProperties.svg",
"samples/tests/spec/scripting/transform.svg",
"samples/tests/spec/scripting/transform2.svg",
"samples/tests/spec/scripting/transform_create.svg",
"samples/tests/spec/scripting/transform_getTransform1.svg",
"samples/tests/spec/scripting/transform_getTransform2.svg",
"samples/tests/spec/scripting/trefProperties.svg",
"samples/tests/spec/scripting/tspan.svg",
"samples/tests/spec/scripting/tspanProperties.svg",
"samples/tests/spec/scripting/visibility.svg",
"samples/tests/spec/scripting/viewBoxOnLoad.svg",
"samples/tests/spec/scripting/xyModifOnLoad.svg",
"samples/tests/spec/scripting/zeroSize.svg",
"test-resources/org/apache/batik/test/svg/bug19363.svg",
"samples/tests/spec/scripting/boundsTransformChange.svg",
"samples/tests/spec/scripting/eventAttrAdd.svg",
"samples/tests/spec/scripting/markerUpdate.svg",
"samples/tests/spec/scripting/rootSizeChange.svg",
"samples/tests/spec/scripting/rectResizeOnClick.svg",
"samples/tests/spec/scripting/setProperty.svg",
"samples/tests/spec/scripting/styling.svg",
"samples/tests/spec/scripting/svgFontMove.svg",
"samples/tests/spec/scripting/text_content.svg",
"samples/tests/spec/scripting/textProperties2.svg",
"samples/tests/spec/scripting/visibilityOnClick.svg",
"ATransform.configuredContextGeneration",
"ATransform.renderingCheck",
"ATransform.ContextrenderingCheck",
"AttributedCharacterIterator.defaultContextGeneration",
"AttributedCharacterIterator.configuredContextGeneration",
"AttributedCharacterIterator.renderingCheck",
"AttributedCharacterIterator.ContextrenderingCheck",
"BasicShapes.configuredContextGeneration",
"BasicShapes.renderingCheck",
"BasicShapes.ContextrenderingCheck",
"BasicShapes2.configuredContextGeneration",
"BasicShapes2.renderingCheck",
"BasicShapes2.ContextrenderingCheck",
"BStroke.configuredContextGeneration",
"BStroke.renderingCheck",
"BStroke.ContextrenderingCheck",
"Bug4389.defaultContextGeneration",
"Bug4389.configuredContextGeneration",
"Bug4945.configuredContextGeneration",
"Bug4945.renderingCheck",
"Bug4945.ContextrenderingCheck",
"Bug6535.configuredContextGeneration",
"Bug6535.renderingCheck",
"Bug17965.configuredContextGeneration",
"Clip.defaultContextGeneration",
"Clip.configuredContextGeneration",
"Clip.renderingCheck",
"Clip.ContextrenderingCheck",
"Color1.configuredContextGeneration",
"Color1.ContextrenderingCheck",
"Color2.configuredContextGeneration",
"Color2.renderingCheck",
"Color2.ContextrenderingCheck",
"DrawImage.defaultContextGeneration",
"DrawImage.configuredContextGeneration",
"DrawImage.renderingCheck",
"DrawImage.ContextrenderingCheck",
"Font1.defaultContextGeneration",
"Font1.configuredContextGeneration",
"Font1.renderingCheck",
"Font1.ContextrenderingCheck",
"Font2.defaultContextGeneration",
"Font2.configuredContextGeneration",
"Font2.renderingCheck",
"Font2.ContextrenderingCheck",
"GVector.defaultContextGeneration",
"GVector.configuredContextGeneration",
"GVector.renderingCheck",
"GVector.ContextrenderingCheck",
"Gradient.configuredContextGeneration",
"Gradient.renderingCheck",
"Gradient.ContextrenderingCheck",
"GraphicObjects.defaultContextGeneration",
"GraphicObjects.configuredContextGeneration",
"GraphicObjects.renderingCheck",
"GraphicObjects.ContextrenderingCheck",
"IdentityTest.configuredContextGeneration",
"Lookup.defaultContextGeneration",
"Lookup.configuredContextGeneration",
"Lookup.ContextrenderingCheck",
"NegativeLengths.configuredContextGeneration",
"NegativeLengths.renderingCheck",
"NegativeLengths.ContextrenderingCheck",
"Paints.defaultContextGeneration",
"Paints.configuredContextGeneration",
"Paints.renderingCheck",
"Paints.ContextrenderingCheck",
"RHints.defaultContextGeneration",
"RHints.configuredContextGeneration",
"RHints.renderingCheck",
"RHints.ContextrenderingCheck",
"Rescale.defaultContextGeneration",
"Rescale.configuredContextGeneration",
"Rescale.ContextrenderingCheck",
"ShearTest.configuredContextGeneration",
"ShearTest.renderingCheck",
"ShearTest.ContextrenderingCheck",
"Texture.defaultContextGeneration",
"Texture.configuredContextGeneration",
"Texture.renderingCheck",
"Texture.ContextrenderingCheck",
"TextSpacePreserve.configuredContextGeneration",
"TextSpacePreserve.ContextrenderingCheck",
"TransformCollapse.configuredContextGeneration",
"TransformCollapse.renderingCheck",
"TransformCollapse.ContextrenderingCheck",
"defaultTest",
"org.apache.batik.util.ApplicationSecurityEnforcerTest$CheckNoPolicyFile",
"ecmaCheckPermissionsDenied",
"ecmaCheckPermissionsDeniedFunction",
"ecmaCheckPermissionsDeniedEval",
"jarCheckLoadAny(scripts=application/java-archive)(scriptOrigin=any)(secure=true)",
"jarCheckLoadAny(scripts=application/java-archive)(scriptOrigin=any)(secure=false)",
"jarCheckLoadSameAsDocument(scripts=application/java-archive)(scriptOrigin=any)(secure=true)",
"jarCheckLoadSameAsDocument(scripts=application/java-archive)(scriptOrigin=any)(secure=false)",
"jarCheckLoadSameAsDocument(scripts=application/java-archive)(scriptOrigin=document)(secure=true)",
"jarCheckLoadSameAsDocument(scripts=application/java-archive)(scriptOrigin=document)(secure=false)",
"jarCheckPermissionsGranted",
"Bug6535.ContextrenderingCheck"
    );
}
