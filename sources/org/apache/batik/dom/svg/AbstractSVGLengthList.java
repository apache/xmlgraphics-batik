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

import org.apache.batik.parser.LengthListHandler;
import org.apache.batik.parser.LengthListParser;
import org.apache.batik.parser.ParseException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGLength;
import org.w3c.dom.svg.SVGLengthList;


/**
 * This class is the implementation of
 * <code>SVGLengthList</code>.
 *
 * @author <a href="mailto:nicolas.socheleau@bitflash.com">Nicolas Socheleau</a>
 * @version $Id$
 */
public abstract class AbstractSVGLengthList
    extends AbstractSVGList
    implements SVGLengthList {


    /**
     * This length list's direction.
     */
    protected short direction;

    /**
     * Separator for a length list.
     */
    public final static String SVG_LENGTH_LIST_SEPARATOR
        =" ";

    /**
     * Return the separator between values in the list.
     */
    protected String getItemSeparator(){
        return SVG_LENGTH_LIST_SEPARATOR;
    }

    /**
     * Create an SVGException when the checkItemType fails.
     *
     * @return SVGException
     */
    protected abstract SVGException createSVGException(short type,
                                                       String key,
                                                       Object[] args);

    /**
     * return the element owning this SVGLengthList.
     */
    protected abstract Element getElement();

    /**
     * Creates a new SVGLengthList.
     */
    protected AbstractSVGLengthList(short direction) {
        super();
        this.direction = direction;
    }

    /**
     */
    public SVGLength initialize ( SVGLength newItem )
        throws DOMException, SVGException {

        return (SVGLength)initializeImpl(newItem);
    }

    /**
     */
    public SVGLength getItem ( int index )
        throws DOMException {

        return (SVGLength)getItemImpl(index);
    }

    /**
     */
    public SVGLength insertItemBefore ( SVGLength newItem, int index )
        throws DOMException, SVGException {

        return (SVGLength)insertItemBeforeImpl(newItem,index);
    }

    /**
     */
    public SVGLength replaceItem ( SVGLength newItem, int index )
        throws DOMException, SVGException {

        return (SVGLength)replaceItemImpl(newItem,index);
    }

    /**
     */
    public SVGLength removeItem ( int index )
        throws DOMException {

        return (SVGLength)removeItemImpl(index);
    }

    /**
     */
    public SVGLength appendItem ( SVGLength newItem )
        throws DOMException, SVGException {

        return (SVGLength) appendItemImpl(newItem);
    }

    /**
     */
    protected SVGItem createSVGItem(Object newItem){
        
        SVGLength l = (SVGLength)newItem;

        return new SVGLengthItem(l.getUnitType(), l.getValueInSpecifiedUnits(),direction);
    }
    
    /**
     * Parse the attribute associated with this SVGLengthList.
     *
     * @param value attribute value
     * @param handler list handler
     */
    protected void doParse(String value, ListHandler handler)
        throws ParseException{

        LengthListParser lengthListParser = new LengthListParser();
        
        LengthListBuilder builder = new LengthListBuilder(handler);
        
        lengthListParser.setLengthListHandler(builder);
        lengthListParser.parse(value);
        
    }

    /**
     * Check if the item is an SVGLength.
     */
    protected void checkItemType(Object newItem)
        throws SVGException {
        if ( !( newItem instanceof SVGLength ) ){
            createSVGException(SVGException.SVG_WRONG_TYPE_ERR,
                               "expected SVGLength",
                               null);
        }
    }

    /**
     * Representation of the item SVGLength.
     */
    protected class SVGLengthItem 
        extends AbstractSVGLength 
        implements SVGItem {

        /**
         * Default Constructor.
         */
        public SVGLengthItem(short type, float value,short direction){
            super(direction);
            this.unitType = type;
            this.value = value;
        }

        /**
         */
        protected SVGOMElement getAssociatedElement(){
            return (SVGOMElement)AbstractSVGLengthList.this.getElement();
        }

        /**
         * SVGLengthList this item belongs to.
         */
        protected AbstractSVGList parentList;

        /**
         * Associates an item to an SVGXXXList
         *
         * @param list list the item belongs to.
         */
        public void setParent(AbstractSVGList list){
            parentList = list;
        }

        /**
         * Return the list the item belongs to.
         *
         * @return list the item belongs to. This
         *   could be if the item belongs to no list.
         */
        public AbstractSVGList getParent(){
            return parentList;
        }

        /**
         * When the SVGLength changes, notify
         * its parent.
         */
        protected void reset(){
            if ( parentList != null ){
                parentList.itemChanged();
            }
        }
        
    }

    /**
     * Helper class to interface the <code>LengthListParser</code>
     * and the <code>ListHandler</code>
     */
    protected class LengthListBuilder
        implements LengthListHandler {

        /**
         * list handler.
         */
        protected ListHandler listHandler;

        //current value being parsed
        protected float currentValue;
        //current type being parsed
        protected short currentType;
        
        /**
         */
        public LengthListBuilder(ListHandler listHandler){
            this.listHandler = listHandler;
        }

        /**
         */
        public void startLengthList() 
            throws ParseException{

            listHandler.startList();
        }
        /**
         * Implements {@link org.apache.batik.parser.LengthHandler#startLength()}.
         */
        public void startLength() throws ParseException {
            currentType = SVGLength.SVG_LENGTHTYPE_NUMBER;
            currentValue = 0.0f;
        }

        /**
         * Implements {@link org.apache.batik.parser.LengthHandler#lengthValue(float)}.
         */
        public void lengthValue(float v) throws ParseException {
            currentValue = v;
        }
        
        /**
         * Implements {@link org.apache.batik.parser.LengthHandler#em()}.
         */
        public void em() throws ParseException {
            currentType = SVGLength.SVG_LENGTHTYPE_EMS;
        }

        /**
         * Implements {@link org.apache.batik.parser.LengthHandler#ex()}.
         */
        public void ex() throws ParseException {
            currentType = SVGLength.SVG_LENGTHTYPE_EXS;
        }

        /**
         * Implements {@link org.apache.batik.parser.LengthHandler#in()}.
         */
        public void in() throws ParseException {
            currentType = SVGLength.SVG_LENGTHTYPE_IN;
        }
        
        /**
         * Implements {@link org.apache.batik.parser.LengthHandler#cm()}.
         */
        public void cm() throws ParseException {
            currentType = SVGLength.SVG_LENGTHTYPE_CM;
        }
        
        /**
         * Implements {@link org.apache.batik.parser.LengthHandler#mm()}.
         */
        public void mm() throws ParseException {
            currentType = SVGLength.SVG_LENGTHTYPE_MM;
        }
        
        /**
         * Implements {@link org.apache.batik.parser.LengthHandler#pc()}.
         */
        public void pc() throws ParseException {
            currentType = SVGLength.SVG_LENGTHTYPE_PC;
        }

        /**
         * Implements {@link org.apache.batik.parser.LengthHandler#pt()}.
         */
        public void pt() throws ParseException {
            currentType = SVGLength.SVG_LENGTHTYPE_EMS;
        }

        /**
         * Implements {@link org.apache.batik.parser.LengthHandler#px()}.
         */
        public void px() throws ParseException {
            currentType = SVGLength.SVG_LENGTHTYPE_PX;
        }

        /**
         * Implements {@link org.apache.batik.parser.LengthHandler#percentage()}.
         */
        public void percentage() throws ParseException {
            currentType = SVGLength.SVG_LENGTHTYPE_PERCENTAGE;
        }

        /**
         * Implements {@link org.apache.batik.parser.LengthHandler#endLength()}.
         */
        public void endLength() throws ParseException {
            listHandler.item(new SVGLengthItem(currentType,currentValue,direction));
        }
        
        /**
         */
        public void endLengthList() 
            throws ParseException {
            listHandler.endList();
        }
    }
   
}
