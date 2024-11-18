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
package org.apache.batik.test.svg;

import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.image.ImageTranscoder;

public class SVGAnimatedRotateCenterTest extends ParametrizedRenderingAccuracyTest {
    private float viewCenter = 100;
    private float shapeCenter = 50;
    private float animDur = 4;

    private float cx;
    private float cy;
    private float angle;

    public SVGAnimatedRotateCenterTest() {
        super.setValidating(false);
    }

    @Override
    public void setId(String id) {
        super.setId(id);

        String[] split = parameter.split(",", 3);
        try {
            cx = Float.parseFloat(split[0]);
            cy = Float.parseFloat(split[1]);
            angle = Float.parseFloat(split[2]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(id, e);
        }
    }

    public void setViewCenter(float viewCenter) {
        this.viewCenter = viewCenter;
    }

    public void setShapeCenter(float shapeCenter) {
        this.shapeCenter = shapeCenter;
    }

    public void setAnimDur(float dur) {
        this.animDur = dur;
    }

    @Override
    protected String buildRefImgURL(String svgDir, String svgFile) {
        String parameterSuffx = Pattern.quote(parameter + PNG_EXTENSION) + "$";
        // Use the same reference image for all cases
        return super.buildRefImgURL(svgDir, svgFile)
                    .replaceFirst(parameterSuffx, PNG_EXTENSION);
    }

    @Override
    protected Document manipulateSVGDocument(Document doc) {
        doc.getDocumentElement().setAttribute("shape-rendering", "crispEdges");

        Element container = doc.getElementById("container");
        Element rect = getElement(container, "rect", 0);
        Element transform = getElement(rect, "animateTransform", 0);

        container.setAttribute("transform", "translate("
                + (viewCenter - cx) + " " + (viewCenter - cy) + ")");

        rect.setAttribute("x", String.valueOf(cx - shapeCenter));
        rect.setAttribute("y", String.valueOf(cy - shapeCenter));

        transform.setAttribute("from", "0 " + cx + " " + cy);
        transform.setAttribute("to", "360 " + cx + " " + cy);

        return doc;
    }

    private static Element getElement(Element container, String name, int index) {
        if (container == null) {
            throw new IllegalStateException("null container element");
        }
        NodeList elements = container.getElementsByTagName(name);
        if (index >= elements.getLength()) {
            throw new IllegalStateException("Could not find "
                    + name + "[" + index + "] inside" + container.getTagName());
        }
        return (Element) elements.item(index);
    }

    @Override
    public ImageTranscoder getTestImageTranscoder() {
        ImageTranscoder transcoder = super.getTestImageTranscoder();
        float snapshotTime = angle * animDur / 360;
        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_SNAPSHOT_TIME, snapshotTime);
        return transcoder;
    }

}
