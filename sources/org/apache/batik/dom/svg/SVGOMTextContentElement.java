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

package org.apache.batik.dom.svg;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.XMLSupport;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGPoint;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGStringList;

/**
 * This class provides a common superclass for all graphics elements.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMTextContentElement
    extends    SVGStylableElement {

    /**
     * The 'lengthAdjust' attribute values.
     */
    protected final static String[] LENGTH_ADJUST_VALUES = {
        "",
        SVG_SPACING_ATTRIBUTE,
        SVG_SPACING_AND_GLYPHS_VALUE
    };

    /**
     * Creates a new SVGOMTextContentElement.
     */
    protected SVGOMTextContentElement() {
    }

    /**
     * Creates a new SVGOMTextContentElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    protected SVGOMTextContentElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);

    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getTextLength()}.
     */
    public SVGAnimatedLength getTextLength() {
        SVGAnimatedLength result =
            (SVGAnimatedLength)getLiveAttributeValue
            (null, SVG_TEXT_LENGTH_ATTRIBUTE);
        if (result == null) {
            result = new AbstractSVGAnimatedLength
                (this, null, SVG_TEXT_LENGTH_ATTRIBUTE,
                 SVGOMAnimatedLength.HORIZONTAL_LENGTH) {
                    protected String getDefaultValue() {
                        throw new RuntimeException("!!! TODO");
                    }
                };
            putLiveAttributeValue(null, SVG_TEXT_LENGTH_ATTRIBUTE,
                                  (LiveAttributeValue)result);
        }
        return result;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getLengthAdjust()}.
     */
    public SVGAnimatedEnumeration getLengthAdjust() {
        return getAnimatedEnumerationAttribute
            (null, SVG_LENGTH_ADJUST_ATTRIBUTE,
             LENGTH_ADJUST_VALUES, (short)1);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getNumberOfChars()}.
     */
    public int getNumberOfChars() {
        return SVGTextContentSupport.getNumberOfChars(this);
        //throw new RuntimeException(" !!! SVGOMTextContentElement.getNumberOfChars()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getComputedTextLength()}.
     */
    public float getComputedTextLength() {
        return SVGTextContentSupport.getComputedTextLength(this);
        //throw new RuntimeException(" !!! SVGOMTextContentElement.getComputedTextLength()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getSubStringLength(int,int)}.
     */
    public float getSubStringLength(int charnum, int nchars)
        throws DOMException {
        return SVGTextContentSupport.getSubStringLength(this,charnum,nchars);
        //throw new RuntimeException(" !!! SVGOMTextContentElement.getSubStringLength()");
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getStartPositionOfChar(int)}.
     */
    public SVGPoint getStartPositionOfChar(int charnum) throws DOMException {
        //throw new RuntimeException(" !!! SVGOMTextContentElement.getStartPositionOfChar()");
        return SVGTextContentSupport.getStartPositionOfChar(this,charnum);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getEndPositionOfChar(int)}.
     */
    public SVGPoint getEndPositionOfChar(int charnum) throws DOMException {
        //throw new RuntimeException(" !!! SVGOMTextContentElement.getEndPositionOfChar()");
        return SVGTextContentSupport.getEndPositionOfChar(this,charnum);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getExtentOfChar(int)}.
     */
    public SVGRect getExtentOfChar(int charnum) throws DOMException {
        //throw new RuntimeException(" !!! SVGOMTextContentElement.getExtentOfChar()");
        return SVGTextContentSupport.getExtentOfChar(this,charnum);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getRotationOfChar(int)}.
     */
    public float getRotationOfChar(int charnum) throws DOMException {
        //throw new RuntimeException(" !!! SVGOMTextContentElement.getRotationOfChar()");
        return SVGTextContentSupport.getRotationOfChar(this,charnum);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#getCharNumAtPosition(SVGPoint)}.
     */
    public int getCharNumAtPosition(SVGPoint point) {
        //throw new RuntimeException(" !!! SVGOMTextContentElement.getCharNumAtPosition()");
        return SVGTextContentSupport.getCharNumAtPosition(this,point.getX(),point.getY());
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTextContentElement#selectSubString(int,int)}.
     */
    public void selectSubString(int charnum, int nchars)
        throws DOMException {
        SVGTextContentSupport.selectSubString(this,charnum, nchars);
        //throw new RuntimeException(" !!! SVGOMTextContentElement.getSubStringLength()");
    }

    // SVGExternalResourcesRequired support /////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGExternalResourcesRequired#getExternalResourcesRequired()}.
     */
    public SVGAnimatedBoolean getExternalResourcesRequired() {
	return SVGExternalResourcesRequiredSupport.
            getExternalResourcesRequired(this);
    }

    // SVGLangSpace support //////////////////////////////////////////////////
    
    /**
     * <b>DOM</b>: Returns the xml:lang attribute value.
     */
    public String getXMLlang() {
        return XMLSupport.getXMLLang(this);
    }

    /**
     * <b>DOM</b>: Sets the xml:lang attribute value.
     */
    public void setXMLlang(String lang) {
        setAttributeNS(XMLSupport.XML_NAMESPACE_URI,
                       XMLSupport.XML_LANG_ATTRIBUTE,
                       lang);
    }
    
    /**
     * <b>DOM</b>: Returns the xml:space attribute value.
     */
    public String getXMLspace() {
        return XMLSupport.getXMLSpace(this);
    }

    /**
     * <b>DOM</b>: Sets the xml:space attribute value.
     */
    public void setXMLspace(String space) {
        setAttributeNS(XMLSupport.XML_NAMESPACE_URI,
                       XMLSupport.XML_SPACE_ATTRIBUTE,
                       space);
    }

    // SVGTests support ///////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#getRequiredFeatures()}.
     */
    public SVGStringList getRequiredFeatures() {
	return SVGTestsSupport.getRequiredFeatures(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#getRequiredExtensions()}.
     */
    public SVGStringList getRequiredExtensions() {
	return SVGTestsSupport.getRequiredExtensions(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#getSystemLanguage()}.
     */
    public SVGStringList getSystemLanguage() {
	return SVGTestsSupport.getSystemLanguage(this);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGTests#hasExtension(String)}.
     */
    public boolean hasExtension(String extension) {
	return SVGTestsSupport.hasExtension(this, extension);
    }
}
