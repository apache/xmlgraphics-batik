/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.svg;

import org.apache.batik.util.XMLConstants;
import org.apache.batik.util.SVGConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.net.URL;


/**
 * Checks for regressions in rendering of a document with a given
 * alternate stylesheet.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGTextContentRenderingAccuracyTest
    extends ParametrizedRenderingAccuracyTest {

    protected String script; //null
    protected String onload; //null
    protected String parameter; //null

    public void setScript(String script){
        this.script = script;
    }

    public void setOnLoadFunction(String onload){
        this.onload = onload;
    }

    public void setParameter(String parameter){
        this.parameter = parameter;
    }

    protected Document manipulateSVGDocument(Document doc) {

        Element root = doc.getDocumentElement();
        String function;
        if ( parameter == null ){
            function = onload+"()";
        }
        else{
            function = onload+"("+parameter+")";
        }
        root.setAttributeNS(null,"onload",function);

        Element scriptElement = doc.createElementNS
            (SVGConstants.SVG_NAMESPACE_URI,SVGConstants.SVG_SCRIPT_TAG);

        scriptElement.setAttributeNS
            (XMLConstants.XLINK_NAMESPACE_URI,SVGConstants.SVG_HREF_ATTRIBUTE,
             script);

        root.appendChild(scriptElement);

        return doc;
    }

}
