/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.lang.ref.WeakReference;
import org.apache.batik.dom.AbstractDocument;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGPathSegList;
import org.w3c.dom.svg.SVGPathElement;
import org.w3c.dom.svg.SVGPathSegArcAbs;
import org.w3c.dom.svg.SVGPathSegArcRel;
import org.w3c.dom.svg.SVGPathSegClosePath;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicRel;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticRel;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoCubicSmoothRel;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothAbs;
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticSmoothRel;
import org.w3c.dom.svg.SVGPathSegLinetoAbs;
import org.w3c.dom.svg.SVGPathSegLinetoRel;
import org.w3c.dom.svg.SVGPathSegLinetoHorizontalAbs;
import org.w3c.dom.svg.SVGPathSegLinetoHorizontalRel;
import org.w3c.dom.svg.SVGPathSegLinetoVerticalAbs;
import org.w3c.dom.svg.SVGPathSegLinetoVerticalRel;
import org.w3c.dom.svg.SVGPathSegMovetoAbs;
import org.w3c.dom.svg.SVGPathSegMovetoRel;
import org.w3c.dom.svg.SVGPoint;

/**
 * This class implements {@link org.w3c.dom.svg.SVGPathElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMPathElement
    extends    SVGGraphicsElement
    implements SVGPathElement {

    /**
     * Creates a new SVGOMPathElement object.
     */
    protected SVGOMPathElement() {
    }

    /**
     * Creates a new SVGOMPathElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMPathElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return "path";
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGPathElement#getPathLength()}.
     */
    public SVGAnimatedNumber getPathLength() {
        throw new RuntimeException(" !!! SVGOMPathElement#getPathLength()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGPathElement#getTotalLength()}.
     */
    public float getTotalLength() {
        throw new RuntimeException(" !!! SVGOMPathElement#getTotalLength()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGPathElement#getPointAtLength(float)}.
     */
    public SVGPoint getPointAtLength(float distance) {
        throw new RuntimeException(" !!! SVGOMPathElement#getPointAtLength()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGPathElement#getPathSegAtLength(float)}.
     */
    public int getPathSegAtLength(float distance) {
        throw new RuntimeException(" !!! SVGOMPathElement#getPathSegAtLength()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedPathData#getPathSegList()}.
     */
    public SVGPathSegList getPathSegList() {
        throw new RuntimeException(" !!! SVGOMPathElement#getPathSegList()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedPathData#getNormalizedPathSegList()}.
     */
    public SVGPathSegList getNormalizedPathSegList() {
        throw new RuntimeException(" !!! SVGOMPathElement#getNormalizedPathSegList()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedPathData#getAnimatedPathSegList()}.
     */
    public SVGPathSegList getAnimatedPathSegList() {
        throw new RuntimeException(" !!! SVGOMPathElement#getAnimatedPathSegList()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGAnimatedPathData#getAnimatedNormalizedPathSegList()}.
     */
    public SVGPathSegList getAnimatedNormalizedPathSegList() {
        throw new RuntimeException(" !!! SVGOMPathElement#getAnimatedNormalizedPathSegList()");
    }

    // Factory methods /////////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegClosePath()}.
     */
    public SVGPathSegClosePath createSVGPathSegClosePath() {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegClosePath()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegMovetoAbs(float,float)}.
     */
    public SVGPathSegMovetoAbs createSVGPathSegMovetoAbs(float x, float y) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegMovetoAbs()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegMovetoRel(float,float)}.
     */
    public SVGPathSegMovetoRel createSVGPathSegMovetoRel(float x, float y) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegMovetoRel()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegLinetoAbs(float,float)}.
     */
    public SVGPathSegLinetoAbs createSVGPathSegLinetoAbs(float x, float y) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegLinetoAbs()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegLinetoRel(float,float)}.
     */
    public SVGPathSegLinetoRel createSVGPathSegLinetoRel(float x, float y) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegLinetoRel()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegLinetoHorizontalAbs(float)}.
     */
    public SVGPathSegLinetoHorizontalAbs createSVGPathSegLinetoHorizontalAbs
        (float x) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegLinetoHorizontalAbs()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegLinetoHorizontalRel(float)}.
     */
    public SVGPathSegLinetoHorizontalRel createSVGPathSegLinetoHorizontalRel
        (float x) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegLinetoHorizontalRel()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegLinetoVerticalAbs(float)}.
     */
    public SVGPathSegLinetoVerticalAbs createSVGPathSegLinetoVerticalAbs
        (float y) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegLinetoVerticalAbs()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegLinetoVerticalRel(float)}.
     */
    public SVGPathSegLinetoVerticalRel createSVGPathSegLinetoVerticalRel
        (float y) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegLinetoVerticalRel()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegCurvetoCubicAbs(float,float,float,float,float,float)}.
     */
    public SVGPathSegCurvetoCubicAbs createSVGPathSegCurvetoCubicAbs
        (float x, float y, float x1, float y1, float x2, float y2) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegCurvetoCubicAbs()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegCurvetoCubicRel(float,float,float,float,float,float)}.
     */
    public SVGPathSegCurvetoCubicRel createSVGPathSegCurvetoCubicRel
        (float x, float y, float x1, float y1, float x2, float y2) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegCurvetoCubicRel()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegCurvetoQuadraticAbs(float,float,float,float)}.
     */
    public SVGPathSegCurvetoQuadraticAbs createSVGPathSegCurvetoQuadraticAbs
        (float x, float y, float x1, float y1) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegCurvetoQuadraticAbs()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegCurvetoQuadraticRel(float,float,float,float)}.
     */
    public SVGPathSegCurvetoQuadraticRel createSVGPathSegCurvetoQuadraticRel
        (float x, float y, float x1, float y1) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegCurvetoQuadraticRel()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegCurvetoCubicSmoothAbs(float,float,float,float)}.
     */
    public SVGPathSegCurvetoCubicSmoothAbs
            createSVGPathSegCurvetoCubicSmoothAbs
            (float x, float y, float x2, float y2) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegCurvetoCubicSmoothAbs()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegCurvetoCubicSmoothRel(float,float,float,float)}.
     */
    public SVGPathSegCurvetoCubicSmoothRel
            createSVGPathSegCurvetoCubicSmoothRel
            (float x, float y, float x2, float y2) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegCurvetoCubicSmoothRel()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegCurvetoQuadraticSmoothAbs(float,float)}.
     */
    public SVGPathSegCurvetoQuadraticSmoothAbs
            createSVGPathSegCurvetoQuadraticSmoothAbs(float x, float y) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegCurvetoQuadraticSmoothAbs()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegCurvetoQuadraticSmoothRel(float,float)}.
     */
    public SVGPathSegCurvetoQuadraticSmoothRel
            createSVGPathSegCurvetoQuadraticSmoothRel(float x, float y) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegCurvetoQuadraticSmoothRel()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegArcAbs(float,float,float,float,float,boolean,boolean)}.
     */
    public SVGPathSegArcAbs createSVGPathSegArcAbs
        (float x, float y, float r1, float r2, float angle,
         boolean largeArcFlag, boolean sweepFlag) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegArcAbs()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGPathElement#createSVGPathSegArcRel(float,float,float,float,float,boolean,boolean)}.
     */
    public SVGPathSegArcRel createSVGPathSegArcRel
        (float x, float y, float r1, float r2, float angle,
         boolean largeArcFlag, boolean sweepFlag) {
        throw new RuntimeException(" !!! SVGOMPathElement#createSVGPathSegArcRel()");
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMPathElement();
    }
}
