/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.TransformListHandler;
import org.apache.batik.parser.TransformListParser;
import org.apache.batik.refimpl.parser.ConcreteTransformListParser;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGStringList;
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
    implements SVGTransformList,
               LiveAttributeValue,
               TransformListHandler {
    /**
     * The list.
     */
    protected List list = new ArrayList();

    /**
     * The modification handler.
     */
    protected ModificationHandler modificationHandler;

    /**
     * Sets the associated modification handler.
     */
    public void setModificationHandler(ModificationHandler mh) {
	modificationHandler = mh;
    }

    /**
     * Called when the string representation of the value as been modified.
     * @param oldValue The old Attr node.
     * @param newValue The new Attr node.
     */
    public void valueChanged(Attr oldValue, Attr newValue) {
	parseTransform(newValue.getValue());
    }

    /**
     * <b>DOM</b>: Implements {@link SVGTransformList#getNumberOfItems()}.
     */
    public int getNumberOfItems() {
        return list.size();
    }

    /**
     * <b>DOM</b>: Implements {@link SVGTransformList#clear()}.
     */
    public void clear() {
        list.clear();
        if (modificationHandler != null) {
            modificationHandler.valueChanged(this, toString());
        }
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
     * <b>DOM</b>: Implements {@link SVGTransformList#initialize(SVGTransform)}.
     */
    public SVGTransform initialize(SVGTransform newItem)
        throws SVGException {
        list.clear();
        list.add(newItem);
        if (modificationHandler != null) {
            modificationHandler.valueChanged(this, toString());
        }
        return newItem;
     }

    /**
     * <b>DOM</b>: Implements {@link SVGTransformList#getItem(int)}.
     */
    public SVGTransform getItem(int i) throws DOMException {
        i--;
        if (i < 0 || i >= list.size()) {
            throw createDOMException(DOMException.INDEX_SIZE_ERR,
                                     "index.out.of.bounds",
                                     new Object[] { new Integer(i) });
        }
        return (SVGTransform)list.get(i);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGTransformList#insertItemBefore(SVGTransform,int)}.
     */
    public SVGTransform insertItemBefore(SVGTransform newItem, int index)
        throws SVGException {
        index--;
        if (index < 0) {
            list.add(0, newItem);
        } else if (index > list.size()) {
            list.add(list.size(), newItem);
        } else {
            list.add(index, newItem);
        }
        if (modificationHandler != null) {
            modificationHandler.valueChanged(this, toString());
        }
        return newItem;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * SVGTransformList#replaceItem(SVGTransform,int)}.
     */
    public SVGTransform replaceItem(SVGTransform newItem, int index)
        throws DOMException, SVGException {
        index--;
        if (index < 0 || index >= list.size()) {
            throw createDOMException(DOMException.INDEX_SIZE_ERR,
                                     "index.out.of.bounds",
                                     new Object[] { new Integer(index) });
        }
        list.set(index, newItem);
        if (modificationHandler != null) {
            modificationHandler.valueChanged(this, toString());
        }
        return newItem;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGTransformList#removeItem(int)}.
     */
    public SVGTransform removeItem(int index) throws DOMException {
        index--;
        if (index < 0 || index >= list.size()) {
            throw createDOMException(DOMException.INDEX_SIZE_ERR,
                                     "index.out.of.bounds",
                                     new Object[] { new Integer(index) });
        }
        Object result = list.remove(index);
        if (modificationHandler != null) {
            modificationHandler.valueChanged(this, toString());
        }
        return (SVGTransform)result;
    }
    
    /**
     * <b>DOM</b>: Implements {@link SVGTransformList#appendItem(SVGTransform)}.
     */
    public SVGTransform appendItem(SVGTransform np) throws SVGException {
        list.add(np);
        if (modificationHandler != null) {
            modificationHandler.valueChanged(this, toString());
        }
        return np;
    }

    /**
     * Returns a string representation of this list.
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        Iterator it = list.iterator();
        if (it.hasNext()) {
            result.append(it.next().toString());
        }
        while (it.hasNext()) {
            result.append(" ");
            result.append(it.next().toString());
        }
        return result.toString();
    }

    /**
     * Creates a localized DOM exception.
     */
    protected DOMException createDOMException(short type, String key,
                                              Object[] args) {
        return new DOMException(type, key);
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
