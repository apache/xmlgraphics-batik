/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.util.awt.svg;

import java.util.Map;

import org.w3c.dom.*;

/**
 * Represents the SVG equivalent of a Java 2D API graphic
 * context attribute.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGGraphicContext{
    public static final String ERROR_NULL_INPUT = "groupContext, graphicElementContext and transformStack should not be null";
    private Map groupContext;
    private Map graphicElementContext;
    private TransformStackElement transformStack[];

    /**
     * @param groupContext Set of attributes that apply to group
     * @param graphicElementContext Set of attributes that apply to
     *        elements but not to groups (e.g., opacity, filter).
     * @param transformStack Sequence of transforms that where
     *        applied to create the context's current transform.
     */
    public SVGGraphicContext(Map groupContext,
                             Map graphicElementContext,
                             TransformStackElement transformStack[]){
        if(groupContext == null || graphicElementContext == null)
            throw new IllegalArgumentException(ERROR_NULL_INPUT);

        this.groupContext = groupContext;
        this.graphicElementContext = graphicElementContext;
        this.transformStack = transformStack;
    }

    /**
     * @return set of attributes that can be set on a group
     */
    public Map getGroupContext(){
        return groupContext;
    }

    /**
     * @return set of attributes that can be set on leaf node
     */
    public Map getGraphicElementContext(){
        return graphicElementContext;
    }

    /**
     * @return set of TransformStackElement for this context
     */
    public TransformStackElement[] getTransformStack(){
        return transformStack;
    }
}
