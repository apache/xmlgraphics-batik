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

package org.apache.batik.swing;

import org.apache.batik.test.svg.JSVGRenderingAccuracyTest;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.dom.GenericDOMImplementation;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;

/**
 * Test setDocument on JSVGComponent with non-Batik SVGOMDocument.
 *
 * This test constructs a generic Document with SVG content then it
 * ensures that when this is passed to JSVGComponet.setDocument it is
 * properly imported to an SVGOMDocument and rendered from there.
 *
 * @author <a href="mailto:deweese@apache.org>l449433</a>
 * @version $Id$
 */
public class SetSVGDocumentTest extends JSVGRenderingAccuracyTest {
    public SetSVGDocumentTest() {
    }
    protected String[] breakSVGFile(String svgFile){
        if(svgFile == null) {
            throw new IllegalArgumentException(svgFile);
        }

        String [] ret = new String[3];
        ret[0] = "test-resources/org/apache/batik/test/svg/";
        ret[1] = "SetSVGDocumentTest";
        ret[2] = ".svg";
        return ret;
    }

    /* JSVGCanvasHandler.Delegate Interface */
    public boolean canvasInit(JSVGCanvas canvas) {
        DOMImplementation impl = 
            GenericDOMImplementation.getDOMImplementation();
        Document doc = impl.createDocument(SVGConstants.SVG_NAMESPACE_URI, 
                                           SVGConstants.SVG_SVG_TAG, null);
        Element e = doc.createElementNS(SVGConstants.SVG_NAMESPACE_URI, 
                                        SVGConstants.SVG_RECT_TAG);
        e.setAttribute("x", "10");
        e.setAttribute("y", "10");
        e.setAttribute("width", "100");
        e.setAttribute("height", "50");
        e.setAttribute("fill", "crimson");
        doc.getDocumentElement().appendChild(e);

        e = doc.createElementNS(SVGConstants.SVG_NAMESPACE_URI, 
                                SVGConstants.SVG_CIRCLE_TAG);
        e.setAttribute("cx", "55");
        e.setAttribute("cy", "35");
        e.setAttribute("r", "30");
        e.setAttribute("fill", "gold");
        doc.getDocumentElement().appendChild(e);
        
        canvas.setDocument(doc);
        return false; // We didn't trigger a load event.
    }

    public boolean canvasUpdated(JSVGCanvas canvas) {
        return true;
    }
}
