/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/
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


