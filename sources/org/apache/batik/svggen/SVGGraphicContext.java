/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.svggen;

import java.util.HashMap;
import java.util.Map;

import org.apache.batik.ext.awt.g2d.TransformStackElement;
import org.apache.batik.util.SVGConstants;

/**
 * Represents the SVG equivalent of a Java 2D API graphic
 * context attribute.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGGraphicContext implements SVGConstants, ErrorConstants {
    // this properties can only be set of leaf nodes =>
    // if they have default values they can be ignored
    private static final String leafOnlyAttributes[] = {
        SVG_OPACITY_ATTRIBUTE,
        SVG_FILTER_ATTRIBUTE,
        SVG_CLIP_PATH_ATTRIBUTE
    };

    private static final String defaultValues[] = {
        "1",
        SVG_NONE_VALUE,
        SVG_NONE_VALUE
    };

    private Map context;
    private Map groupContext;
    private Map graphicElementContext;
    private TransformStackElement transformStack[];

    /**
     * @param context Set of style attributes in this context.
     * @param transformStack Sequence of transforms that where
     *        applied to create the context's current transform.
     */
    public SVGGraphicContext(Map context,
                             TransformStackElement transformStack[]) {
        if (context == null)
            throw new SVGGraphics2DRuntimeException(ERR_MAP_NULL);
        if (transformStack == null)
            throw new SVGGraphics2DRuntimeException(ERR_TRANS_NULL);
        this.context = context;
        this.transformStack = transformStack;
        computeGroupAndGraphicElementContext();
    }

    /**
     * @param groupContext Set of attributes that apply to group
     * @param graphicElementContext Set of attributes that apply to
     *        elements but not to groups (e.g., opacity, filter).
     * @param transformStack Sequence of transforms that where
     *        applied to create the context's current transform.
     */
    public SVGGraphicContext(Map groupContext, Map graphicElementContext,
                             TransformStackElement transformStack[]) {
        if (groupContext == null || graphicElementContext == null)
            throw new SVGGraphics2DRuntimeException(ERR_MAP_NULL);
        if (transformStack == null)
            throw new SVGGraphics2DRuntimeException(ERR_TRANS_NULL);

        this.groupContext = groupContext;
        this.graphicElementContext = graphicElementContext;
        this.transformStack = transformStack;
        computeContext();
    }


    /**
     * @return set of all attributes.
     */
    public Map getContext() {
        return context;
    }

    /**
     * @return set of attributes that can be set on a group
     */
    public Map getGroupContext() {
        return groupContext;
    }

    /**
     * @return set of attributes that can be set on leaf node
     */
    public Map getGraphicElementContext() {
        return graphicElementContext;
    }

    /**
     * @return set of TransformStackElement for this context
     */
    public TransformStackElement[] getTransformStack() {
        return transformStack;
    }

    private void computeContext() {
        if (context != null)
            return;

        context = new HashMap(groupContext);
        context.putAll(graphicElementContext);
    }

    private void computeGroupAndGraphicElementContext() {
        if (groupContext != null)
            return;
        //
        // Now, move attributes that only apply to
        // leaf elements to a separate map.
        //
        groupContext = new HashMap(context);
        graphicElementContext = new HashMap();
        for (int i=0; i< leafOnlyAttributes.length; i++) {
            Object attrValue = groupContext.get(leafOnlyAttributes[i]);
            if (attrValue != null){
                if (!attrValue.equals(defaultValues[i]))
                    graphicElementContext.put(leafOnlyAttributes[i], attrValue);
                groupContext.remove(leafOnlyAttributes[i]);
            }
        }
    }
}
