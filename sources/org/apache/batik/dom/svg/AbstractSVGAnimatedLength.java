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

import org.apache.batik.parser.UnitProcessor;
import org.w3c.dom.Attr;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGLength;

/**
 * This class provides an implementation of the {@link
 * SVGAnimatedLength} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class AbstractSVGAnimatedLength
    implements SVGAnimatedLength,
               LiveAttributeValue {

    /**
     * This constant represents horizontal lengths.
     */
    public final static short HORIZONTAL_LENGTH =
        UnitProcessor.HORIZONTAL_LENGTH;

    /**
     * This constant represents vertical lengths.
     */
    public final static short VERTICAL_LENGTH =
        UnitProcessor.VERTICAL_LENGTH;

    /**
     * This constant represents other lengths.
     */
    public final static short OTHER_LENGTH =
        UnitProcessor.OTHER_LENGTH;

    /**
     * The associated element.
     */
    protected AbstractElement element;

    /**
     * The attribute's namespace URI.
     */
    protected String namespaceURI;

    /**
     * The attribute's local name.
     */
    protected String localName;

    /**
     * This length's direction.
     */
    protected short direction;

    /**
     * The base value.
     */
    protected BaseSVGLength baseVal;

    /**
     * Whether the value is changing.
     */
    protected boolean changing;

    /**
     * Creates a new SVGAnimatedLength.
     * @param elt The associated element.
     * @param ns The attribute's namespace URI.
     * @param ln The attribute's local name.
     * @param dir The length's direction.
     */
    protected AbstractSVGAnimatedLength(AbstractElement elt,
                                        String ns,
                                        String ln,
                                        short dir) {
        element = elt;
        namespaceURI = ns;
        localName = ln;
        direction = dir;
    }

    /**
     * Returns the default value to use when the associated attribute
     * was not specified.
     */
    protected abstract String getDefaultValue();

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedLength#getBaseVal()}.
     */
    public SVGLength getBaseVal() {
        if (baseVal == null) {
            baseVal = new BaseSVGLength(direction);
        }
        return baseVal;
    }

    /**
     * <b>DOM</b>: Implements {@link SVGAnimatedLength#getAnimVal()}.
     */
    public SVGLength getAnimVal() {
        throw new RuntimeException("!!! TODO: getAnimVal()");
    }

    /**
     * Called when an Attr node has been added.
     */
    public void attrAdded(Attr node, String newv) {
        if (!changing && baseVal != null) {
            baseVal.invalidate();
        }
    }

    /**
     * Called when an Attr node has been modified.
     */
    public void attrModified(Attr node, String oldv, String newv) {
        if (!changing && baseVal != null) {
            baseVal.invalidate();
        }
    }

    /**
     * Called when an Attr node has been removed.
     */
    public void attrRemoved(Attr node, String oldv) {
        if (!changing && baseVal != null) {
            baseVal.invalidate();
        }
    }

    /**
     * This class represents the SVGLength returned by getBaseVal().
     */
    protected class BaseSVGLength extends AbstractSVGLength {

        /**
         * Whether this length is valid.
         */
        protected boolean valid;
        
        /**
         * Creates a new BaseSVGLength.
         */
        public BaseSVGLength(short direction) {
            super(direction);
        }

        /**
         * Invalidates this length.
         */
        public void invalidate() {
            valid = false;
        }

        /**
         * Resets the value of the associated attribute.
         */
        protected void reset() {
            try {
                changing = true;
                String value = getValueAsString();
                element.setAttributeNS(namespaceURI, localName, value);
            } finally {
                changing = false;
            }
        }

        /**
         * Initializes the length, if needed.
         */
        protected void revalidate() {
            if (valid) {
                return;
            }

            String s = null;

            Attr attr = element.getAttributeNodeNS(namespaceURI, localName);

            if (attr == null) {
                s = getDefaultValue();
            }
            else{
                s = attr.getValue();
            }

            parse(s);

            valid = true;
        }

        /**
         */
        protected SVGOMElement getAssociatedElement(){
            return (SVGOMElement)element;
        }
    }
}
