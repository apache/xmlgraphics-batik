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

import java.awt.Font;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext.GraphicContextDefaults;
import org.apache.batik.util.SVGConstants;

/**
 * Testing customization of the SVGGeneratorContext and generation of 
 * SVG Fonts.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public class GeneratorContext extends SVGAccuracyTest implements SVGConstants {
    public static class TestIDGenerator extends SVGIDGenerator {
        public String generateID(String prefix) {
            return "test"+super.generateID(prefix);
        }
    }

    public static class TestStyleHandler extends DefaultStyleHandler {
        private CDATASection styleSheet;
        public TestStyleHandler(CDATASection styleSheet) {
            this.styleSheet = styleSheet;
        }
        public void setStyle(Element element, Map styleMap,
                             SVGGeneratorContext generatorContext) {
            Iterator iter = styleMap.keySet().iterator();
            // create a new class id in the style sheet
            String id = generatorContext.getIDGenerator().generateID("C");
            styleSheet.appendData("."+id+" {");
            // append each key/value pairs
            while (iter.hasNext()) {
                String key = (String)iter.next();
                String value = (String)styleMap.get(key);
                styleSheet.appendData(key+":"+value+";");
            }
            styleSheet.appendData("}\n");
            // reference the class id of the style sheet on the element to be styled
            element.setAttribute("class", id);
        }
    }

    private Element topLevelGroup = null;

    public GeneratorContext(Painter painter,
                            URL refURL) {
        super(painter, refURL);
    }

    protected SVGGraphics2D buildSVGGraphics2D(){
        // Use Batik's DOM implementation to create a Document
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String namespaceURI = SVGDOMImplementation.SVG_NAMESPACE_URI;
        Document domFactory = impl.createDocument(namespaceURI, SVG_SVG_TAG, null);

        // Create a default context from our Document instance
        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(domFactory);
        
        // Set ID generator
        ctx.setIDGenerator(new TestIDGenerator());

        // Extension Handler to be done

        // Image Handler to be done
        GenericImageHandler ihandler = new CachedImageHandlerBase64Encoder();
        ctx.setGenericImageHandler(ihandler);

        // Set Style handler
        CDATASection styleSheet = domFactory.createCDATASection("");
        ctx.setStyleHandler(new TestStyleHandler(styleSheet));

        // Set the generator comment
        ctx.setComment("Generated by the Batik Test Framework. Test:\u00e9j");

        // Turn SVG Font embedding on.
        ctx.setEmbeddedFontsOn(true);

        // Set the default font to use
        GraphicContextDefaults defaults 
            = new GraphicContextDefaults();
        defaults.font = new Font("Arial", Font.PLAIN, 12);
        ctx.setGraphicContextDefaults(defaults);

        //
        // Build SVGGraphics2D with our customized context
        //
        SVGGraphics2D g2d = new SVGGraphics2D(ctx, false);

        // Append our stylesheet to the top level group.
        topLevelGroup = g2d.getTopLevelGroup();
        Element style = domFactory.createElementNS(SVG_NAMESPACE_URI, SVG_STYLE_TAG);
        style.setAttributeNS(null, SVG_TYPE_ATTRIBUTE, "text/css");
        style.appendChild(styleSheet);
        topLevelGroup.appendChild(style);

        return g2d;
    }

    protected void configureSVGGraphics2D(SVGGraphics2D g2d) {
        topLevelGroup.appendChild(g2d.getTopLevelGroup());
        g2d.setTopLevelGroup(topLevelGroup);
    }
}

