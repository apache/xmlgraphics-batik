/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.bridge;

import java.util.EventObject;

import org.apache.batik.gvt.GraphicsNode;

import org.w3c.dom.Element;

/**
 * An event object that describes the modification to apply to an Element.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @author <a href="mailto:etissandier@ilog.fr">Emmanuel Tissandier</a>
 * @version $Id$
 */
public class BridgeMutationEvent extends EventObject {

    /** The mutation is a simple mutation on an attribute.*/
    public static final int PROPERTY_MUTATION_TYPE=0;
    /** The mutation is a mutation on a referenced style.*/
    public static final int STYLE_MUTATION_TYPE=1;
    
    /** The bridge context.*/
    private BridgeContext context;
    
    /** Type of the event.*/
    private int type;

    /** The name of the modified attribute.*/
    private String attrName;

    /** The new value of the modified attribute.*/
    private String attrNewValue;
    
    /** Graphic node impacted by the mutation*/
    private GraphicsNode graphicsNode;

    /** Sub event describing modification of a style element*/
    private BridgeMutationEvent styleMutationEvent;

    public BridgeMutationEvent(Object source, BridgeContext context, int type) {
        super(source);
        this.context = context;
        this.type = type;
    }

    /**
     * Returns the type of the mutation. The value is one
     * of <tt>PROPERTY_MUTATION_TYPE</tt> and <tt>STYLE_MUTATION_TYPE</tt>.
     */
    public int getType(){
        return type;
    }


    /**
     * Returns the element that was modified.
     */
    public Element getElement(){
        return (Element)source;
    }

  
    /**
     * Returns the name of the attribute that was modified.
     */
    public String getAttrName(){
        return attrName;
    }

    /**
     * Sets the name of the attribute that was modified.
     */
    public void setAttrName(String attrName){
        this.attrName = attrName;
    }
    
    /**
     * Returns the new value of the attribute that was modified.
     */
    public String getAttrNewValue(){
        return attrNewValue;
    }

    /**
     * Sets the new value of the attribute that was modified.
     */
    public void setAttrNewValue(String value){
        attrNewValue = value;
    }

    /**
     * Returns the new value of the attribute that was modified.
     */
    public GraphicsNode getGraphicsNode(){
        return graphicsNode;
    }

    /**
     * Sets the new value of the attribute that was modified.
     */
    public void setGraphicsNode(GraphicsNode node){
        graphicsNode = node;
    }

    /**
     * Returns the style mutation event.
     * This
     * value is valid only when the type is <tt>STYLE_MUTATION_TYPE</tt>.
     * It contains an event describing the mutation of the style.
     */
    public BridgeMutationEvent getStyleMutationEvent(){
        return styleMutationEvent;
    }

    /**
     * Sets a style mutation event. This
     * value is valid only when the type is <tt>STYLE_MUTATION_TYPE</tt>.
     * It contains an event describing the mutation of the style.
     */
    public void setStyleMutationEvent(BridgeMutationEvent event){
        styleMutationEvent = event;
    }


   
}
