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

package org.apache.batik.ext.awt.image.rendered;

import java.awt.RenderingHints;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;
import java.awt.image.WritableRaster;

import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.TransferFunction;

/**
 *
 * @author <a href="mailto:thomas.deweese@kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class ComponentTransferRed extends AbstractRed {
    LookupOp operation;

    /**
     * The constructor will instantiate a LookupOp instance using
     * a LookupOp, which is built using the four LUT
     * data obtained by the TransferFunction objects
     * funcs[0] : Alpha component transfer function
     * funcs[1] : Red component transfer function
     * funcs[2] : Green component transfer function
     * funcs[3] : Blue component transfer function
     */
    public ComponentTransferRed(CachableRed src,
                                TransferFunction [] funcs,
                                RenderingHints hints) {
        super(src, src.getBounds(), 
              GraphicsUtil.coerceColorModel(src.getColorModel(), false),
              src.getSampleModel(),
              null);

        byte [][] tableData = {funcs[1].getLookupTable(), 
                               funcs[2].getLookupTable(),
                               funcs[3].getLookupTable(), 
                               funcs[0].getLookupTable()};

        // Note that we create an anonymous subclass here.
        // For what ever reason this makes the Op work correctly.
        // If you remove this, it seems to get the color channels messed
        // up.  The downside is that I suspect that this means we are
        // falling into a more general, and hence slower case, but
        // at least it works....
        operation  =  new LookupOp(new ByteLookupTable(0, tableData), hints) 
            { };
    }
    
    public WritableRaster copyData(WritableRaster wr){
        CachableRed src = (CachableRed)getSources().elementAt(0);

        wr = src.copyData(wr);
        GraphicsUtil.coerceData(wr, src.getColorModel(), false);

        WritableRaster srcWR = wr.createWritableTranslatedChild(0,0);

        operation.filter(srcWR, srcWR);

        return wr;
    }
}
