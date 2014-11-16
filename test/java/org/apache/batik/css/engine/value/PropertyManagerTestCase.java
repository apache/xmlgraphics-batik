/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.batik.css.engine.value;

import java.util.StringTokenizer;

import org.w3c.css.sac.LexicalUnit;

import org.apache.batik.css.engine.value.svg.MarkerManager;
import org.apache.batik.css.engine.value.svg.OpacityManager;
import org.apache.batik.css.engine.value.svg.SVGColorManager;
import org.apache.batik.css.engine.value.svg.SVGPaintManager;
import org.apache.batik.css.engine.value.svg.SpacingManager;
import org.apache.batik.css.parser.Parser;
import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;
import org.apache.batik.util.CSSConstants;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * The class to test the CSS properties's manager.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class PropertyManagerTestCase extends AbstractTest {

    private static final Object[] testAlignmentBaselineSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.AlignmentBaselineManager",
        Boolean.FALSE,
        "auto",
        "auto|baseline|before-edge|text-before-edge|middle|after-edge|text-after-edge|ideographic|alphabetic|hanging|mathematical"
    };

    private static final Object[] testBaselineShiftSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.BaselineShiftManager",
        Boolean.FALSE,
        "baseline",
        "baseline|sub|super"
    };

    private static final Object[] testClipSpec = new Object[] {
        "org.apache.batik.css.engine.value.css2.ClipManager",
        Boolean.FALSE,
        "auto",
        "auto"
    };

    private static final Object[] testClipPathSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.ClipPathManager",
        Boolean.FALSE,
        "none",
        "none"
    };

    private static final Object[] testClipRuleSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.ClipRuleManager",
        Boolean.TRUE,
        "nonzero",
        "nonzero|evenodd"
    };

    private static final Object[] testColorSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.ColorManager",
        Boolean.TRUE,
        "__USER_AGENT__",
        ""
    };

    private static final Object[] testColorInterpolationSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.ColorInterpolationManager",
        Boolean.TRUE,
        "sRGB",
        "auto|sRGB|linearRGB"
    };

    private static final Object[] testColorInterpolationFiltersSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.ColorInterpolationFiltersManager",
        Boolean.TRUE,
        "linearRGB",
        "auto|sRGB|linearRGB"
    };

    private static final Object[] testColorProfileSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.ColorProfileManager",
        Boolean.TRUE,
        "auto",
        "auto|sRGB"
    };

    private static final Object[] testColorRenderingSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.ColorRenderingManager",
        Boolean.TRUE,
        "auto",
        "auto|optimizeSpeed|optimizeQuality"
    };

    private static final Object[] testCursorSpec = new Object[] {
        "org.apache.batik.css.engine.value.css2.CursorManager",
        Boolean.TRUE,
        "auto",
        "auto|crosshair|default|pointer|move|e-resize|ne-resize|nw-resize|n-resize|se-resize|sw-resize|s-resize|w-resize| text|wait|help "
    };

    private static final Object[] testDirectionSpec = new Object[] {
        "org.apache.batik.css.engine.value.css2.DirectionManager",
        Boolean.TRUE,
        "ltr",
        "ltr|rtl"
    };

    private static final Object[] testDisplaySpec = new Object[] {
        "org.apache.batik.css.engine.value.css2.DisplayManager",
        Boolean.FALSE,
        "inline",
        "inline|block|list-item|run-in|compact|marker|table|inline-table|table-row-group|table-header-group|table-footer-group|table-row|table-column-group|table-column|table-cell|table-caption|none"
    };

    private static final Object[] testDominantBaselineSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.DominantBaselineManager",
        Boolean.FALSE,
        "auto",
        "auto|use-script|no-change|reset-size|alphabetic|hanging|ideographic|mathematical|central|middle|text-after-edge|text-before-edge|text-top|text-bottom"
    };

    private static final Object[] testEnableBackgroundSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.EnableBackgroundManager",
        Boolean.FALSE,
        "accumulate",
        "accumulate"
    };

    private static final Object[] testFillSpec = new Object[] {
        "org.apache.batik.css.engine.value.PropertyManagerTestCase$FillManager",
        Boolean.TRUE,
        "rgb(0, 0, 0)",
        ""
    };

    private static final Object[] testFillOpacitySpec = new Object[] {
        "org.apache.batik.css.engine.value.PropertyManagerTestCase$FillOpacityManager",
        Boolean.TRUE,
        "1",
        ""
    };

    private static final Object[] testFillRuleSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.FillRuleManager",
        Boolean.TRUE,
        "nonzero",
        "nonzero|evenodd"
    };

    private static final Object[] testFilterSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.FilterManager",
        Boolean.FALSE,
        "none",
        "none"
    };

    private static final Object[] testFloodColorSpec = new Object[] {
        "org.apache.batik.css.engine.value.PropertyManagerTestCase$FloodColorManager",
        Boolean.FALSE,
        "rgb(0, 0, 0)",
        "currentColor"
    };

    private static final Object[] testFloodOpacitySpec = new Object[] {
        "org.apache.batik.css.engine.value.PropertyManagerTestCase$FloodOpacityManager",
        Boolean.FALSE,
        "1",
        ""
    };

    private static final Object[] testFontSpec = new Object[] {
        "org.apache.batik.css.engine.value.css2.FontManager",
        Boolean.TRUE,
        "1",
        ""
    };

    private static final Object[] testFontFamilySpec = new Object[] {
        "org.apache.batik.css.engine.value.css2.FontFamilyManager",
        Boolean.TRUE,
        "__USER_AGENT__",
        ""
    };

    private static final Object[] testFontSizeSpec = new Object[] {
        "org.apache.batik.css.engine.value.css2.FontSizeManager",
        Boolean.TRUE,
        "medium",
        "medium"
    };

    private static final Object[] testFontSizeAdjustSpec = new Object[] {
        "org.apache.batik.css.engine.value.css2.FontSizeAdjustManager",
        Boolean.TRUE,
        "none",
        "none"
    };

    private static final Object[] testFontStretchSpec = new Object[] {
        "org.apache.batik.css.engine.value.css2.FontStretchManager",
        Boolean.TRUE,
        "normal",
        "normal|wider|narrower|ultra-condensed|extra-condensed|condensed|semi-condensed|semi-expanded|expanded|extra-expanded|ultra-expanded"
    };

    private static final Object[] testFontStyleSpec = new Object[] {
        "org.apache.batik.css.engine.value.css2.FontStyleManager",
        Boolean.TRUE,
        "normal",
        "normal|italic|oblique"
    };

    private static final Object[] testFontVariantSpec = new Object[] {
        "org.apache.batik.css.engine.value.css2.FontVariantManager",
        Boolean.TRUE,
        "normal",
        "normal|small-caps"
    };

    private static final Object[] testFontWeigthSpec = new Object[] {
        "org.apache.batik.css.engine.value.css2.FontWeightManager",
        Boolean.TRUE,
        "normal",
        "normal|bold|bolder|lighter|100|200|300|400|500|600|700|800|900"
    };

    private static final Object[] testGlyphOrientationHorizontalSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.GlyphOrientationHorizontalManager",
        Boolean.TRUE,
        "0deg",
        ""
    };

    private static final Object[] testGlyphOrientationVerticalSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.GlyphOrientationVerticalManager",
        Boolean.TRUE,
        "auto",
        "auto"
    };

    private static final Object[] testImageRenderingSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.ImageRenderingManager",
        Boolean.TRUE,
        "auto",
        "auto|optimizeSpeed|optimizeQuality"
    };

    private static final Object[] testKerningSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.KerningManager",
        Boolean.TRUE,
        "auto",
        "auto"
    };

    private static final Object[] testLetterSpacingSpec = new Object[] {
        "org.apache.batik.css.engine.value.PropertyManagerTestCase$LetterSpacingManager",
        Boolean.TRUE,
        "normal",
        "normal"
    };

    private static final Object[] testLightingColorSpec = new Object[] {
        "org.apache.batik.css.engine.value.PropertyManagerTestCase$LightingColorManager",
        Boolean.FALSE,
        "rgb(255, 255, 255)",
        "currentColor"
    };

    private static final Object[] testMarkerSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.MarkerManager",
        Boolean.TRUE,
        "",
        ""
    };

    private static final Object[] testMarkerStartSpec = new Object[] {
        "org.apache.batik.css.engine.value.PropertyManagerTestCase$MarkerStartManager",
        Boolean.TRUE,
        "none",
        "none"
    };

    private static final Object[] testMarkerMidSpec = new Object[] {
        "org.apache.batik.css.engine.value.PropertyManagerTestCase$MarkerMidManager",
        Boolean.TRUE,
        "none",
        "none"
    };

    private static final Object[] testMarkerEndSpec = new Object[] {
        "org.apache.batik.css.engine.value.PropertyManagerTestCase$MarkerEndManager",
        Boolean.TRUE,
        "none",
        "none"
    };

    private static final Object[] testMaskSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.MaskManager",
        Boolean.FALSE,
        "none",
        "none"
    };

    private static final Object[] testOpacitySpec = new Object[] {
        "org.apache.batik.css.engine.value.PropertyManagerTestCase$DefaultOpacityManager",
        Boolean.FALSE,
        "1",
        ""
    };

    private static final Object[] testOverflowSpec = new Object[] {
        "org.apache.batik.css.engine.value.css2.OverflowManager",
        Boolean.FALSE,
        "visible",
        "visible|hidden|scroll|auto"
    };

    private static final Object[] testPointerEventsSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.PointerEventsManager",
        Boolean.TRUE,
        "visiblePainted",
        "visiblePainted|visibleFill|visibleStroke|visible|painted|fill|stroke|all|none"
    };

    private static final Object[] testShapeRenderingSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.ShapeRenderingManager",
        Boolean.TRUE,
        "auto",
        "auto|optimizeSpeed|crispEdges|geometricPrecision"
    };

    private static final Object[] testStopColorSpec = new Object[] {
        "org.apache.batik.css.engine.value.PropertyManagerTestCase$StopColorManager",
        Boolean.FALSE,
        "rgb(0, 0, 0)",
        ""
    };

    private static final Object[] testStopOpacitySpec = new Object[] {
        "org.apache.batik.css.engine.value.PropertyManagerTestCase$StopOpacityManager",
        Boolean.FALSE,
        "1",
        ""
    };

    private static final Object[] testStrokeSpec = new Object[] {
        "org.apache.batik.css.engine.value.PropertyManagerTestCase$StrokeManager",
        Boolean.TRUE,
        "none",
        "none"
    };

    private static final Object[] testStrokeDashArraySpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.StrokeDasharrayManager",
        Boolean.TRUE,
        "none",
        "none"
    };

    private static final Object[] testStrokeDashOffsetSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.StrokeDashoffsetManager",
        Boolean.TRUE,
        "0",
        "0"
    };

    private static final Object[] testStrokeLineCapSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.StrokeLinecapManager",
        Boolean.TRUE,
        "butt",
        "butt|round|square"
    };

    private static final Object[] testStrokeLineJoinSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.StrokeLinejoinManager",
        Boolean.TRUE,
        "miter",
        "miter|round|bevel"
    };

    private static final Object[] testStrokeMiterLimitSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.StrokeMiterlimitManager",
        Boolean.TRUE,
        "4",
        ""
    };

    private static final Object[] testStrokeOpacitySpec = new Object[] {
        "org.apache.batik.css.engine.value.PropertyManagerTestCase$StrokeOpacityManager",
        Boolean.TRUE,
        "1",
        ""
    };

    private static final Object[] testStrokeWidthSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.StrokeWidthManager",
        Boolean.TRUE,
        "1",
        ""
    };

    private static final Object[] testTextAnchorSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.TextAnchorManager",
        Boolean.TRUE,
        "start",
        "start|middle|end"
    };

    private static final Object[] testTextDecorationSpec = new Object[] {
        "org.apache.batik.css.engine.value.css2.TextDecorationManager",
        Boolean.FALSE,
        "none",
        "none|underline|overline|line-through|blink"
    };

    private static final Object[] testTextRenderingSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.TextRenderingManager",
        Boolean.TRUE,
        "auto",
        "auto|optimizeSpeed|optimizeLegibility|geometricPrecision"
    };

    private static final Object[] testUnicodeBidiSpec = new Object[] {
        "org.apache.batik.css.engine.value.css2.UnicodeBidiManager",
        Boolean.FALSE,
        "normal",
        "normal|embed|bidi-override"
    };

    private static final Object[] testVisibilitySpec = new Object[] {
        "org.apache.batik.css.engine.value.css2.VisibilityManager",
        Boolean.TRUE,
        "visible",
        "visible|hidden|collapse"
    };


    private static final Object[] testWordSpacingSpec = new Object[] {
        "org.apache.batik.css.engine.value.PropertyManagerTestCase$WordSpacingManager",
        Boolean.TRUE,
        "normal",
        "normal"
    };

    private static final Object[] testWritingModeSpec = new Object[] {
        "org.apache.batik.css.engine.value.svg.WritingModeManager",
        Boolean.TRUE,
        "lr-tb",
        "lr-tb|rl-tb|tb-rl|lr|rl|tb"
    };

    public PropertyManagerTestCase() {}

    @Test
    public void testAlignmentBaseline() throws Exception {
        testPropertyManager(testAlignmentBaselineSpec);
    }

    @Test
    public void testBaselineShift() throws Exception {
        testPropertyManager(testBaselineShiftSpec);
    }

    @Test
    public void testClip() throws Exception {
        testPropertyManager(testClipSpec);
    }

    @Test
    public void testClipPath() throws Exception {
        testPropertyManager(testClipPathSpec);
    }

    @Test
    public void testClipRule() throws Exception {
        testPropertyManager(testClipRuleSpec);
    }

    @Test
    public void testColor() throws Exception {
        testPropertyManager(testColorSpec);
    }

    @Test
    public void testColorInterpolation() throws Exception {
        testPropertyManager(testColorInterpolationSpec);
    }

    @Test
    public void testColorInterpolationFilters() throws Exception {
        testPropertyManager(testColorInterpolationFiltersSpec);
    }

    @Test
    public void testColorProfile() throws Exception {
        testPropertyManager(testColorProfileSpec);
    }

    @Test
    public void testColorRendering() throws Exception {
        testPropertyManager(testColorRenderingSpec);
    }

    @Test
    public void testCursor() throws Exception {
        testPropertyManager(testCursorSpec);
    }

    @Test
    public void testDirection() throws Exception {
        testPropertyManager(testDirectionSpec);
    }

    @Test
    public void testDisplay() throws Exception {
        testPropertyManager(testDisplaySpec);
    }

    @Test
    public void testDominantBaseline() throws Exception {
        testPropertyManager(testDominantBaselineSpec);
    }

    @Test
    public void testEnableBackground() throws Exception {
        testPropertyManager(testEnableBackgroundSpec);
    }

    @Test
    public void testFill() throws Exception {
        testPropertyManager(testFillSpec);
    }

    @Test
    public void testFillOpacity() throws Exception {
        testPropertyManager(testFillOpacitySpec);
    }

    @Test
    public void testFillRule() throws Exception {
        testPropertyManager(testFillRuleSpec);
    }

    @Test
    public void testFilter() throws Exception {
        testPropertyManager(testFilterSpec);
    }

    @Test
    public void testFloodColor() throws Exception {
        testPropertyManager(testFloodColorSpec);
    }

    @Test
    public void testFloodOpacity() throws Exception {
        testPropertyManager(testFloodOpacitySpec);
    }

    @Ignore
    public void testFontly() throws Exception {
        testPropertyManager(testFontSpec);
    }

    @Test
    public void testFontFamily() throws Exception {
        testPropertyManager(testFontFamilySpec);
    }

    @Test
    public void testFontSize() throws Exception {
        testPropertyManager(testFontSizeSpec);
    }

    @Test
    public void testFontSizeAdjust() throws Exception {
        testPropertyManager(testFontSizeAdjustSpec);
    }

    @Test
    public void testFontStretch() throws Exception {
        testPropertyManager(testFontStretchSpec);
    }

    @Test
    public void testFontStyle() throws Exception {
        testPropertyManager(testFontStyleSpec);
    }

    @Test
    public void testFontVariant() throws Exception {
        testPropertyManager(testFontVariantSpec);
    }

    @Test
    public void testFontWeigth() throws Exception {
        testPropertyManager(testFontWeigthSpec);
    }

    @Test
    public void testGlyphOrientationHorizontal() throws Exception {
        testPropertyManager(testGlyphOrientationHorizontalSpec);
    }

    @Test
    public void testGlyphOrientationVertical() throws Exception {
        testPropertyManager(testGlyphOrientationVerticalSpec);
    }

    @Test
    public void testImageRendering() throws Exception {
        testPropertyManager(testImageRenderingSpec);
    }

    @Test
    public void testKerning() throws Exception {
        testPropertyManager(testKerningSpec);
    }

    @Test
    public void testLetterSpacing() throws Exception {
        testPropertyManager(testLetterSpacingSpec);
    }

    @Test
    public void testLightingColor() throws Exception {
        testPropertyManager(testLightingColorSpec);
    }

    @Ignore
    public void testMarker() throws Exception {
        testPropertyManager(testMarkerSpec);
    }

    @Test
    public void testMarkerStart() throws Exception {
        testPropertyManager(testMarkerStartSpec);
    }

    @Test
    public void testMarkerMid() throws Exception {
        testPropertyManager(testMarkerMidSpec);
    }

    @Test
    public void testMarkerEnd() throws Exception {
        testPropertyManager(testMarkerEndSpec);
    }

    @Test
    public void testMask() throws Exception {
        testPropertyManager(testMaskSpec);
    }

    @Test
    public void testOpacity() throws Exception {
        testPropertyManager(testOpacitySpec);
    }

    @Test
    public void testOverflow() throws Exception {
        testPropertyManager(testOverflowSpec);
    }

    @Test
    public void testPointerEvents() throws Exception {
        testPropertyManager(testPointerEventsSpec);
    }

    @Test
    public void testShapeRendering() throws Exception {
        testPropertyManager(testShapeRenderingSpec);
    }

    @Test
    public void testStopColor() throws Exception {
        testPropertyManager(testStopColorSpec);
    }

    @Test
    public void testStopOpacity() throws Exception {
        testPropertyManager(testStopOpacitySpec);
    }

    @Test
    public void testStroke() throws Exception {
        testPropertyManager(testStrokeSpec);
    }

    @Test
    public void testStrokeDashArray() throws Exception {
        testPropertyManager(testStrokeDashArraySpec);
    }

    @Test
    public void testStrokeDashOffset() throws Exception {
        testPropertyManager(testStrokeDashOffsetSpec);
    }

    @Test
    public void testStrokeLineCap() throws Exception {
        testPropertyManager(testStrokeLineCapSpec);
    }

    @Test
    public void testStrokeLineJoin() throws Exception {
        testPropertyManager(testStrokeLineJoinSpec);
    }

    @Test
    public void testStrokeMiterLimit() throws Exception {
        testPropertyManager(testStrokeMiterLimitSpec);
    }

    @Test
    public void testStrokeOpacity() throws Exception {
        testPropertyManager(testStrokeOpacitySpec);
    }

    @Test
    public void testStrokeWidth() throws Exception {
        testPropertyManager(testStrokeWidthSpec);
    }

    @Test
    public void testTextAnchor() throws Exception {
        testPropertyManager(testTextAnchorSpec);
    }

    @Test
    public void testTextDecoration() throws Exception {
        testPropertyManager(testTextDecorationSpec);
    }

    @Test
    public void testTextRendering() throws Exception {
        testPropertyManager(testTextRenderingSpec);
    }

    @Test
    public void testUnicodeBidi() throws Exception {
        testPropertyManager(testUnicodeBidiSpec);
    }

    @Test
    public void testVisibility() throws Exception {
        testPropertyManager(testVisibilitySpec);
    }

    @Test
    public void testWordSpacing() throws Exception {
        testPropertyManager(testWordSpacingSpec);
    }

    @Test
    public void testWritingMode() throws Exception {
        testPropertyManager(testWritingModeSpec);
    }

    private void testPropertyManager(Object[] spec) throws Exception {

        String managerClassName = (String) spec[0];
        boolean isInherited = (Boolean) spec[1];
        String defaultValue = (String) spec[2];
        String[] identValues = ((String) spec[3]).split("\\|");
        if ((identValues.length == 1) && identValues[0].isEmpty())
            identValues = null;
        else {
            for (int i = 0, n = identValues.length; i < n; ++i) {
                identValues[i] = identValues[i].trim();
            }
        }

        ValueManager manager;
        try {
            manager = createValueManager(managerClassName);
            assertNotNull(manager);
        } catch (Exception e) {
            fail("create value manager: " + e.getMessage());
            return;
        }
        
        // test default value if any
        if (!defaultValue.equals("__USER_AGENT__")) {
            String s = manager.getDefaultValue().getCssText();
            assertTrue(defaultValue.equalsIgnoreCase(s));
        }

        // test if the property is inherited or not
        assertTrue(manager.isInheritedProperty() == isInherited);

        Parser cssParser = new Parser();
        // see if the property supports the value 'inherit'
        try {
            LexicalUnit lu = cssParser.parsePropertyValue("inherit");
            Value v = manager.createValue(lu, null);
            String s = v.getCssText();
            assertTrue("inherit".equalsIgnoreCase(s));
        } catch (Exception e) {
            fail("parse 'inherit': " + e.getMessage());
        }

        // test all possible identifiers
        if ((identValues != null) && (identValues.length > 0)) {
            for (int i=0; i < identValues.length; ++i) {
                String value = identValues[i];
                try {
                    LexicalUnit lu = cssParser.parsePropertyValue(value);
                    Value v = manager.createValue(lu, null);
                    String s = v.getCssText();
                    assertTrue(value.equalsIgnoreCase(s));
                } catch (Exception e) {
                    fail("parse value '" + value + "': " + e.getMessage());
                }
            }
        }
    }

    private ValueManager createValueManager(String className) throws Exception {
        return (ValueManager)Class.forName(className).newInstance();
    }

    /**
     * Manager for 'fill'.
     */
    public static class FillManager extends SVGPaintManager {
        public FillManager() {
            super(CSSConstants.CSS_FILL_PROPERTY);
        }
    }

    /**
     * Manager for 'fill-opacity'.
     */
    public static class FillOpacityManager extends OpacityManager {
        public FillOpacityManager() {
            super(CSSConstants.CSS_FILL_OPACITY_PROPERTY, true);
        }
    }

    /**
     * Manager for 'flood-color'.
     */
    public static class FloodColorManager extends SVGColorManager {
        public FloodColorManager() {
            super(CSSConstants.CSS_FLOOD_COLOR_PROPERTY);
        }
    }

    /**
     * Manager for 'flood-opacity'.
     */
    public static class FloodOpacityManager extends OpacityManager {
        public FloodOpacityManager() {
            super(CSSConstants.CSS_FLOOD_OPACITY_PROPERTY, false);
        }
    }

    /**
     * Manager for 'letter-spacing'.
     */
    public static class LetterSpacingManager extends SpacingManager {
        public LetterSpacingManager() {
            super(CSSConstants.CSS_LETTER_SPACING_PROPERTY);
        }
    }

    /**
     * Manager for 'lighting-color'.
     */
    public static class LightingColorManager extends SVGColorManager {
        public LightingColorManager() {
            super(CSSConstants.CSS_LIGHTING_COLOR_PROPERTY, ValueConstants.WHITE_RGB_VALUE);
        }
    }

    /**
     * Manager for 'marker-end'.
     */
    public static class MarkerEndManager extends MarkerManager {
        public MarkerEndManager() {
            super(CSSConstants.CSS_MARKER_END_PROPERTY);
        }
    }

    /**
     * Manager for 'marker-mid'.
     */
    public static class MarkerMidManager extends MarkerManager {
        public MarkerMidManager() {
            super(CSSConstants.CSS_MARKER_MID_PROPERTY);
        }
    }

    /**
     * Manager for 'marker-start'.
     */
    public static class MarkerStartManager extends MarkerManager {
        public MarkerStartManager() {
            super(CSSConstants.CSS_MARKER_START_PROPERTY);
        }
    }

    /**
     * Manager for 'opacity'.
     */
    public static class DefaultOpacityManager extends OpacityManager {
        public DefaultOpacityManager() {
            super(CSSConstants.CSS_OPACITY_PROPERTY, false);
        }
    }

    /**
     * Manager for 'stop-color'.
     */
    public static class StopColorManager extends SVGColorManager {
        public StopColorManager() {
            super(CSSConstants.CSS_STOP_COLOR_PROPERTY);
        }
    }

    /**
     * Manager for 'stop-opacity'.
     */
    public static class StopOpacityManager extends OpacityManager {
        public StopOpacityManager() {
            super(CSSConstants.CSS_STOP_OPACITY_PROPERTY, false);
        }
    }

    /**
     * Manager for 'stroke'.
     */
    public static class StrokeManager extends SVGPaintManager {
        public StrokeManager() {
            super(CSSConstants.CSS_STROKE_PROPERTY, ValueConstants.NONE_VALUE);
        }
    }

    /**
     * Manager for 'stroke-opacity'.
     */
    public static class StrokeOpacityManager extends OpacityManager {
        public StrokeOpacityManager() {
            super(CSSConstants.CSS_STROKE_OPACITY_PROPERTY, true);
        }
    }

    /**
     * Manager for 'word-spacing'.
     */
    public static class WordSpacingManager extends SpacingManager {
        public WordSpacingManager() {
            super(CSSConstants.CSS_WORD_SPACING_PROPERTY);
        }
    }
}
