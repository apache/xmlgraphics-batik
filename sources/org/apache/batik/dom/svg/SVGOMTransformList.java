/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.io.StringReader;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.TransformListHandler;
import org.apache.batik.parser.TransformListParser;
import org.apache.batik.refimpl.parser.ConcreteTransformListParser;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGList;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGTransform;
import org.w3c.dom.svg.SVGTransformList;

/**
 * This class provides an implementation of the
 * {@link org.w3c.dom.svg.SVGTransformList} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMTransformList
    extends    SVGOMList
    implements SVGTransformList,
               LiveAttributeValue,
               TransformListHandler {
    /**
     * Called when the string representation of the value as been modified.
     * @param oldValue The old Attr node.
     * @param newValue The new Attr node.
     */
    public void valueChanged(Attr oldValue, Attr newValue) {
	parseTransform(newValue.getValue());
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGTransformList#createSVGTransformFromMatrix(SVGMatrix)}.
     */
    public SVGTransform createSVGTransformFromMatrix(SVGMatrix matrix) {
        throw new RuntimeException(" !!! TODO: SVGTransformList#createSVGTransformFromMatrix()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGTransformList#consolidate()}.
     */
    public SVGTransform consolidate() {
        throw new RuntimeException(" !!! TODO: SVGTransformList#consolidate()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGList#createItem()}.
     */
    public Object createItem() {
        throw new RuntimeException(" !!! TODO: SVGTransformList#createItem()");
    }

    /**
     * Returns the list separator.
     */
    protected String getSeparator() {
        return " ";
    }

    /**
     * Checks the validity of an item.
     */
    protected void checkItem(Object item) throws SVGException {
        if (!(item instanceof SVGTransform)) {
            throw new SVGException(SVGException.SVG_WRONG_TYPE_ERR,
                                   " !!! wrong.item.type");
        }
    }

    /**
     * Parses the given transform representation.
     */
    protected void parseTransform(String text) {
	TransformListParser tlp = new ConcreteTransformListParser();
	tlp.setTransformListHandler(this);
	try {
	    tlp.parse(new StringReader(text));
	} catch (ParseException e) {
	    throw new DOMException(DOMException.SYNTAX_ERR, e.getMessage());
	}
    }

    // TransformListHandler //////////////////////////////////////////////

    /**
     * Implements {@link TransformListHandler#startTransformList()}.
     */
    public void startTransformList() throws ParseException {
    }

    /**
     * Implements {@link
     * TransformListHandler#matrix(float,float,float,float,float,float)}.
     */
    public void matrix(float a, float b, float c, float d, float e, float f)
	throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#rotate(float)}.
     */
    public void rotate(float theta) throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#rotate(float,float,float)}.
     */
    public void rotate(float theta, float cx, float cy) throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#translate(float)}.
     */
    public void translate(float tx) throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#translate(float,float)}.
     */
    public void translate(float tx, float ty) throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#scale(float)}.
     */
    public void scale(float sx) throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#scale(float,float)}.
     */
    public void scale(float sx, float sy) throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#skewX(float)}.
     */
    public void skewX(float skx) throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#skewY(float)}.
     */
    public void skewY(float sky) throws ParseException {
    }

    /**
     * Implements {@link TransformListHandler#endTransformList()}.
     */
    public void endTransformList() throws ParseException {
    }
}
