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

package org.apache.batik.ext.awt.image;


/**
 * TableTransfer.java
 *
 * This class defines the Table type transfer function for the
 * feComponentTransfer filter, as defined in chapter 15, section 11 of the SVG
 * specification.
 *
 * @author <a href="mailto:sheng.pei@sun.com">Sheng Pei</a>
 * @version $Id$ 
 */
public class TableTransfer implements TransferFunction {
    /**
     * This byte array stores the lookuptable data
     */
    public byte [] lutData;

    /**
     * This int array is the input table values from the user
     */
    public int [] tableValues;

    /*
     * The number of the input table's elements
     */
    private int n;

    /*
     * The input is an int array which will be used
     * later to construct the lut data
     */
    public TableTransfer(int [] tableValues){
        this.tableValues = tableValues;
        this.n = tableValues.length;
    }

    /*
     * This method will build the lut data. Each entry's
     * value will increase/decrease between the nearby
     * intervals.
     */
    private void buildLutData(){
        lutData = new byte [256];
        int j;
        float fi, r;
        int ffi, cfi;

        /*for (j=0; j<n; j++){
            System.out.println("tableValues[" + j + "] = " + tableValues[j]);
            }*/

        for (j=0; j<=255; j++){
            fi = j*(n-1)/255f;
            ffi = (int)Math.floor(fi);
            cfi = (ffi + 1)>(n-1)?(n-1):(ffi+1);
            r = fi - ffi;
            lutData[j] = (byte)((int)((tableValues[ffi] + r*(tableValues[cfi] - tableValues[ffi])))&0xff);
            // System.out.println("[" + j + "] : " + ffi + "/" + cfi + "/" + r);
        }
        
        /*for(j=0; j<=255; j++){
            System.out.print("[" + j + "] = " + (0xff & lutData[j]) + "  ");
            }

        System.out.println();
        System.out.println();*/
    }

    /**
     * This method will return the lut data in order
     * to construct a LookUpTable object
     */
    public byte [] getLookupTable(){
        buildLutData();
        return lutData;
    }
}
