/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGAnimatedLengthList;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGLengthList;

/**
 * This class is the implementation of
 * the SVGAnimatedLengthList interface.
 *
 * @author <a href="mailto:nicolas.socheleau@bitflash.com">Nicolas Socheleau</a>
 * @version $Id$
 */
public class SVGOMAnimatedLengthList 
    implements SVGAnimatedLengthList,
               LiveAttributeValue {

    /**
     * The associated element.
     */
    protected AbstractElement element;

    /**
     * The attribute's namespace URI.
     */
    protected String namespaceURI;

    /**
     * The attribute's local name.
     */
    protected String localName;

    /**
     * Whether the list is changing.
     */
    protected boolean changing;

    /**
     * SVGPLengthList
     */
    protected AbstractSVGLengthList lengths;

    /**
     * Default value for the 'points' attribute.
     */
    protected String defaultValue;

    /**
     * This length list's direction.
     */
    protected short direction;

    /**
     */
    public SVGOMAnimatedLengthList(AbstractElement elt,
                                   String ns,
                                   String ln,
                                   String defaultValue,
                                   short direction){
        
        element = elt;
        namespaceURI = ns;
        localName = ln;
        this.defaultValue = defaultValue;
        this.direction = direction;
    }

    /**
     * return the SVGLengthList mapping
     * the static attribute
     * of the element
     *
     * @return a length list.
     */
    public SVGLengthList getBaseVal(){
        if ( lengths == null ){
            lengths = new SVGOMLengthList(direction);
        }
         return lengths;
    }

    /**
     */
    public SVGLengthList getAnimVal(){
        throw new RuntimeException("TODO :  getAnimVal() !!");
    }

    /**
     * Called when an Attr node has been added.
     */
    public void attrAdded(Attr node, String newv) {
        if (!changing && lengths != null) {
            lengths.invalidate();
        }
    }

    /**
     * Called when an Attr node has been modified.
     */
    public void attrModified(Attr node, String oldv, String newv) {
        if (!changing && lengths != null) {
            lengths.invalidate();
        }
    }

    /**
     * Called when an Attr node has been removed.
     */
    public void attrRemoved(Attr node, String oldv) {
        if (!changing && lengths != null) {
            lengths.invalidate();
        }
    }
    
    /**
     * SVGLengthList implementation.
     */
    public class SVGOMLengthList extends AbstractSVGLengthList {

        public SVGOMLengthList(short direction){
            super(direction);
        }

        /**
         * Create a DOMException.
         */
        protected DOMException createDOMException(short    type,
                                                  String   key,
                                                  Object[] args){
            return element.createDOMException(type,key,args);
        }

        /**
         * Create a SVGException.
         */
        protected SVGException createSVGException(short    type,
                                                  String   key,
                                                  Object[] args){

            return ((SVGOMElement)element).createSVGException(type,key,args);
        }

        /**
         */
        protected Element getElement(){
            return element;
        }

        /**
         * Retrieve the value of the attribute 'points'.
         */
        protected String getValueAsString(){
            Attr attr = element.getAttributeNodeNS(namespaceURI, localName);
            if (attr == null) {
                return defaultValue;
            }
            return attr.getValue();
        }

        /**
         * Set the value of the attribute 'points'
         */
        protected void setAttributeValue(String value){
            try{
                changing = true;
                element.setAttributeNS(namespaceURI, localName, value);
            }
            finally{
                changing = false;
            }
        }
    }
}
