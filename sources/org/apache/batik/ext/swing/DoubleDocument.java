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
package org.apache.batik.ext.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Helper class. Only allows an Double value in the document.
 * 
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class DoubleDocument extends PlainDocument {
    /** 
     * Strip all non digit characters. '-' and '+' are only allowed as the
     * first character. Only one '.' is allowed.
     **/
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException { 
        if (str == null) {
            return;
        }

        // Get current value
        String curVal = getText(0, getLength());
        boolean hasDot = curVal.indexOf(".")!=-1;

        // Strip non digit characters
        char[] buffer = str.toCharArray();
        char[] digit = new char[buffer.length];
        int j = 0;

        if(offs==0 && buffer!=null && buffer.length>0 && buffer[0]=='-')
            digit[j++] = buffer[0];

        for (int i = 0; i < buffer.length; i++) {
            if(Character.isDigit(buffer[i]))
                digit[j++] = buffer[i];
            if(!hasDot && buffer[i]=='.'){
                digit[j++] = '.';
                hasDot = true;
            }
        }

        // Now, test that new value is within range.
        String added = new String(digit, 0, j);
        try{
            StringBuffer val = new StringBuffer(curVal);
            val.insert(offs, added);
            if(val.toString().equals(".") || val.toString().equals("-") || val.toString().equals("-."))
                super.insertString(offs, added, a);
            else{
                Double.valueOf(val.toString());
                super.insertString(offs, added, a);
            }
        }catch(NumberFormatException e){
            // Ignore insertion, as it results in an out of range value
        }
    }

    public void setValue(double d){
        try{
            remove(0, getLength());
            insertString(0, (new Double(d)).toString(), null);
        }catch(BadLocationException e){
            // Will not happen because we are sure
            // we use the proper range
        }
    }

    public double getValue(){
        try{
            String t = getText(0, getLength());
            if(t != null && t.length() > 0){
                return Double.parseDouble(t);
            }
            else{
                return 0;
            }
        }catch(BadLocationException e){
            // Will not happen because we are sure
            // we use the proper range
            throw new Error();
        }
    }
}


