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

package org.apache.batik.transcoder.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage; 

import java.io.ByteArrayOutputStream;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;

import org.apache.batik.dom.svg.SVGDOMImplementation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.DOMImplementation;

/**
 * Test the ImageTranscoder input with a DOM tree.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class DOMTest extends AbstractImageTranscoderTest {

    /**
     * Constructs a new <tt>DOMTest</tt>.
     */
    public DOMTest() {
    }

    /**
     * Creates the <tt>TranscoderInput</tt>.
     */
    protected TranscoderInput createTranscoderInput() {
	DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
	String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
	Document doc = impl.createDocument(svgNS, "svg", null);

	Element root = doc.getDocumentElement();

	root.setAttributeNS(null, "width", "400");
	root.setAttributeNS(null, "height", "400");

	Element r = doc.createElementNS(svgNS, "rect");
	r.setAttributeNS(null, "x", "0");
	r.setAttributeNS(null, "y", "0");
	r.setAttributeNS(null, "width", "400");
	r.setAttributeNS(null, "height", "400");
	r.setAttributeNS(null, "style", "fill:black");
	root.appendChild(r);

	r = doc.createElementNS(svgNS, "rect");
	r.setAttributeNS(null, "x", "100");
	r.setAttributeNS(null, "y", "50");
	r.setAttributeNS(null, "width", "100");
	r.setAttributeNS(null, "height", "50");
	r.setAttributeNS(null, "style", "stroke:red; fill:none");
	root.appendChild(r);

	return new TranscoderInput(doc);
    }

    /**
     * Returns the reference image for this test.
     */
    protected byte [] getReferenceImageData() {
        try {
            BufferedImage img = new BufferedImage
                (400, 400, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = img.createGraphics();
            g2d.setColor(Color.black);
            g2d.fillRect(0, 0, 400, 400);
            g2d.setColor(Color.red);
            g2d.drawRect(100, 50, 100, 50);
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            PNGTranscoder t = new PNGTranscoder();
            TranscoderOutput output = new TranscoderOutput(ostream);
            t.writeImage(img, output);
            return ostream.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("DOMTest error");
        }
    }
}
