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
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimationElement;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGStringList;

/**
 * This class provides an implementation of the SVGAnimationElement interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMAnimationElement
    extends SVGOMElement
    implements SVGAnimationElement {
    
    /**
     * Creates a new SVGOMAnimationElement.
     */
    protected SVGOMAnimationElement() {
    }

    /**
     * Creates a new SVGOMAnimationElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    protected SVGOMAnimationElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);

    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimationElement#getTargetElement()}.
     */
    public SVGElement getTargetElement() {
        throw new RuntimeException("!!! TODO: getTargetElement()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimationElement#getStartTime()}.
     */
    public float getStartTime() {
        throw new RuntimeException("!!! TODO: getStartTime()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimationElement#getCurrentTime()}.
     */
    public float getCurrentTime() {
        throw new RuntimeException("!!! TODO: getCurrentTime()");
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimationElement#getSimpleDuration()}.
     */
    public float getSimpleDuration() throws DOMException {
        throw new RuntimeException("!!! TODO: getSimpleDuration()");
    }

    // ElementTimeControl ////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.smil.ElementTimeControl#beginElement()}.
     */
    public boolean beginElement() throws DOMException {
        throw new RuntimeException("!!! TODO: beginElement()");
    }
    
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.smil.ElementTimeControl#beginElementAt(float)}.
     */
    public boolean beginElementAt(float offset) throws DOMException {
        throw new RuntimeException("!!! TODO: beginElementAt()");
    }
    
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.smil.ElementTimeControl#endElement()}.
     */
    public boolean endElement() throws DOMException {
        throw new RuntimeException("!!! TODO: endElement()");
    }
    
    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.smil.ElementTimeControl#endElementAt(float)}.
     */
    public boolean endElementAt(float offset) throws DOMException {
        throw new RuntimeException("!!! TODO: endElementAt(float)");
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
