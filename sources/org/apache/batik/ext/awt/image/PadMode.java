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
 * This is a typesafe enumeration of the standard Composite rules for
 * the CompositeRable operation. (over, in, out, atop, xor, arith)
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public final class PadMode implements java.io.Serializable {
      /** Pad edges with zeros */
    public static final int MODE_ZERO_PAD = 1;

      /** Pad edges by replicating edge pixels */
    public static final int MODE_REPLICATE = 2;

      /** Pad edges by wrapping around edge pixels */
    public static final int MODE_WRAP = 3;

      /** Pad edges with zeros */
    public static final PadMode ZERO_PAD = new PadMode(MODE_ZERO_PAD);

      /** Pad edges by replicating edge pixels */
    public static final PadMode REPLICATE = new PadMode(MODE_REPLICATE);

      /** Pad edges by replicating edge pixels */
    public static final PadMode WRAP = new PadMode(MODE_WRAP);

    /**
     * Returns the mode of this pad mode.
     */
    public int getMode() {
        return mode;
    }

      /**
       * The pad mode for this object.
       */
    private int mode;

    private PadMode(int mode) {
        this.mode = mode;
    }

    /**
     * This is called by the serialization code before it returns
     * an unserialized object. To provide for unicity of
     * instances, the instance that was read is replaced by its
     * static equivalent. See the serialiazation specification for
     * further details on this method's logic.
     */
    private Object readResolve() throws java.io.ObjectStreamException {
        switch(mode){
        case MODE_ZERO_PAD:
            return ZERO_PAD;
        case MODE_REPLICATE:
            return REPLICATE;
        case MODE_WRAP:
            return WRAP;
        default:
            throw new Error("Unknown Pad Mode type");
        }
    }
}
