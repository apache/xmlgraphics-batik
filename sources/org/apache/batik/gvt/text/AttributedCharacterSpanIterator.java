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

package org.apache.batik.gvt.text;

import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.util.Map;
import java.util.Set;

/**
 * AttributedCharacterSpanIterator
 *
 * Used to provide ACI functionality to a "substring" of an AttributedString.
 * In this way a TextLayout can be created which only uses a substring of
 * AttributedString.
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */

public class AttributedCharacterSpanIterator implements
                                   AttributedCharacterIterator {

    private AttributedCharacterIterator aci;
    private int begin;
    private int end;

    /**
     * Construct a AttributedCharacterSpanIterator from a subinterval of
     * an existing AttributedCharacterIterator.
     * @param aci The source AttributedCharacterIterator
     * @param start the first index of the subinterval
     * @param stop the index of the first character after the subinterval
     */
    public AttributedCharacterSpanIterator(AttributedCharacterIterator aci, 
                                           int start, int stop) {
        this.aci = aci;
        end = Math.min(aci.getEndIndex(), stop);
        begin = Math.max(aci.getBeginIndex(), start);
        this.aci.setIndex(begin);
    }

    //From java.text.AttributedCharacterIterator

    /**
     * Get the keys of all attributes defined on the iterator's text range.
     */
    public Set getAllAttributeKeys() {
        return aci.getAllAttributeKeys();
        // FIXME: not if there are atts outside the substring!
    }

    /**
     * Get the value of the named attribute for the current
     *     character.
     */
    public Object getAttribute(AttributedCharacterIterator.Attribute attribute) {
        return aci.getAttribute(attribute);
    }

    /**
     * Returns a map with the attributes defined on the current
     * character.
     */
    public Map getAttributes() {
        return aci.getAttributes();
    }

    /**
     * Get the index of the first character following the
     *     run with respect to all attributes containing the current
     *     character.
     */
    public int getRunLimit() {
        return Math.min(aci.getRunLimit(), end);
    }

    /**
     * Get the index of the first character following the
     *      run with respect to the given attribute containing the current
     *      character.
     */
    public int getRunLimit(AttributedCharacterIterator.Attribute attribute) {
        return Math.min(aci.getRunLimit(attribute), end);
    }

    /**
     * Get the index of the first character following the
     *     run with respect to the given attributes containing the current
     *     character.
     */
    public int getRunLimit(Set attributes) {
        return Math.min(aci.getRunLimit(attributes), end);
    }

    /**
     * Get the index of the first character of the run with
     *    respect to all attributes containing the current character.
     */
    public int getRunStart() {
        return Math.max(aci.getRunStart(), begin);
    }

    /**
     * Get the index of the first character of the run with
     *      respect to the given attribute containing the current character.
     * @param attribute The attribute for whose appearance the first offset
     *      is requested.
     */
    public int getRunStart(AttributedCharacterIterator.Attribute attribute) {
        return Math.max(aci.getRunStart(attribute), begin);
    }

    /**
     * Get the index of the first character of the run with respect to
     * the given attributes containing the current character.
     * @param attributes the Set of attributes which begins at the
     * returned index.  
     */
    public int getRunStart(Set attributes) {
        return Math.max(aci.getRunStart(attributes), begin);
    }

    //From CharacterIterator

    /**
     * Create a copy of this iterator
     */
    public Object clone() {
        return new AttributedCharacterSpanIterator(
                      (AttributedCharacterIterator) aci.clone(), begin, end);
    }

    /**
     * Get the character at the current position (as returned
     *      by getIndex()).
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char current() {
        return aci.current();
    }

    /**
     * Sets the position to getBeginIndex().
     * @return the character at the start index of the text.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char first() {
        return aci.setIndex(begin);
    }

    /**
     * Get the start index of the text.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public int getBeginIndex() {
        return begin;
    }

    /**
     * Get the end index of the text.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public int getEndIndex() {
        return end;
    }

    /**
     * Get the current index.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public int getIndex() {
        return aci.getIndex();
    }

    /**
     * Sets the position to getEndIndex()-1 (getEndIndex() if
     * the text is empty) and returns the character at that position.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char last() {
        return setIndex(end-1);
    }

    /**
     * Increments the iterator's index by one, returning the next character.
     * @return the character at the new index.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char next() {
        if (getIndex() < end-1 ) {
            return aci.next();
        } else {
            return setIndex(end);
        }
    }

    /**
     * Decrements the iterator's index by one and returns
     * the character at the new index.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char previous() {
        if (getIndex() > begin) {
            return aci.previous();
        } else {
            return CharacterIterator.DONE;
        }
    }

    /**
     * Sets the position to the specified position in the text.
     * @param position The new (current) index into the text.
     * @return the character at new index <em>position</em>.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char setIndex(int position) {
        int ndx = Math.max(position, begin);
        ndx = Math.min(ndx, end);
        char c = aci.setIndex(ndx);
        if (ndx == end) {
            c = CharacterIterator.DONE;
        }
        return c;
    }
}






