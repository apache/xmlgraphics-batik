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

import java.awt.Dimension;
import java.awt.Font;
import java.io.StringWriter;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext.GraphicContextDefaults;
import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.TestReport;
import org.apache.batik.util.SVGConstants;

/**
 * This test validates that the SVGGraphics2D generates the same result 
 * with the two versions of its getRoot method.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class GetRootTest extends AbstractTest implements SVGConstants {
    public static final Dimension CANVAS_SIZE
        = new Dimension(300, 400);

    public static final String ERROR_DIFFERENT_SVG_OUTPUT
        = "GetRootTest.error.different.svg.output";

    public static final String ENTRY_KEY_NO_ARG_OUTPUT 
        = "GetRootTest.entry.key.no.arg.output";

    public static final String ENTRY_KEY_SVG_ARG_OUTPUT
        = "GetRootTest.entry.key.svg.arg.output";

    public TestReport runImpl() throws Exception {
        // First, use the no-argument getRoot

        DOMImplementation impl = GenericDOMImplementation.getDOMImplementation();
        String namespaceURI = SVGConstants.SVG_NAMESPACE_URI;
        Document domFactory = impl.createDocument(namespaceURI, SVG_SVG_TAG, null);
        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(domFactory);
        GraphicContextDefaults defaults 
            = new GraphicContextDefaults();
        defaults.font = new Font("Arial", Font.PLAIN, 12);
        ctx.setGraphicContextDefaults(defaults);
        SVGGraphics2D g2d = new SVGGraphics2D(ctx, false);

        g2d.setSVGCanvasSize(CANVAS_SIZE);

        Painter painter = new BasicShapes();
        painter.paint(g2d);

        StringWriter swA = new StringWriter();
        g2d.stream(g2d.getRoot(), swA);

        // Now, use the getRoot with argument
        domFactory = impl.createDocument(namespaceURI, SVG_SVG_TAG, null);
        ctx = SVGGeneratorContext.createDefault(domFactory);
        ctx.setGraphicContextDefaults(defaults);
        g2d = new SVGGraphics2D(ctx, false);

        g2d.setSVGCanvasSize(CANVAS_SIZE);

        painter.paint(g2d);

        StringWriter swB = new StringWriter();
        g2d.stream(g2d.getRoot(domFactory.getDocumentElement()),
                   swB);

        // Compare the two output: they should be identical
        if (swA.toString().equals(swB.toString())) {
            return reportSuccess();
        } else {
            TestReport report = reportError(ERROR_DIFFERENT_SVG_OUTPUT);
            report.addDescriptionEntry(ENTRY_KEY_NO_ARG_OUTPUT,
                                       swA.toString());
            report.addDescriptionEntry(ENTRY_KEY_SVG_ARG_OUTPUT,
                                       swB.toString());
            return report;
        }
    }
}
