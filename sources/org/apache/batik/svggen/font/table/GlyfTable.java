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

package org.apache.batik.svggen.font.table;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @version $Id$
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 */
public class GlyfTable implements Table {

    private byte[] buf = null;
    private GlyfDescript[] descript;

    protected GlyfTable(DirectoryEntry de, RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());
        buf = new byte[de.getLength()];
        raf.read(buf);
/*
        TableMaxp t_maxp = (TableMaxp) td.getEntryByTag(maxp).getTable();
        TableLoca t_loca = (TableLoca) td.getEntryByTag(loca).getTable();
        descript = new TableGlyfDescript[t_maxp.getNumGlyphs()];
        for (int i = 0; i < t_maxp.getNumGlyphs(); i++) {
            raf.seek(tde.getOffset() + t_loca.getOffset(i));
            int len = t_loca.getOffset((short)(i + 1)) - t_loca.getOffset(i);
            if (len > 0) {
                short numberOfContours = raf.readShort();
                if (numberOfContours < 0) {
                    //          descript[i] = new TableGlyfCompositeDescript(this, raf);
                } else {
                    descript[i] = new TableGlyfSimpleDescript(this, numberOfContours, raf);
                }
            } else {
                descript[i] = null;
            }
        }

        for (int i = 0; i < t_maxp.getNumGlyphs(); i++) {
            raf.seek(tde.getOffset() + t_loca.getOffset(i));
            int len = t_loca.getOffset((short)(i + 1)) - t_loca.getOffset(i);
            if (len > 0) {
                short numberOfContours = raf.readShort();
                if (numberOfContours < 0) {
                    descript[i] = new TableGlyfCompositeDescript(this, raf);
                }
            }
        }
*/
    }

    public void init(int numGlyphs, LocaTable loca) {
        if (buf == null) {
            return;
        }
        descript = new GlyfDescript[numGlyphs];
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        for (int i = 0; i < numGlyphs; i++) {
            int len = loca.getOffset((short)(i + 1)) - loca.getOffset(i);
            if (len > 0) {
                bais.reset();
                bais.skip(loca.getOffset(i));
                short numberOfContours = (short)(bais.read()<<8 | bais.read());
                if (numberOfContours >= 0) {
                    descript[i] = new GlyfSimpleDescript(this, numberOfContours, bais);
                }
            } else {
                descript[i] = null;
            }
        }

        for (int i = 0; i < numGlyphs; i++) {
            int len = loca.getOffset((short)(i + 1)) - loca.getOffset(i);
            if (len > 0) {
                bais.reset();
                bais.skip(loca.getOffset(i));
                short numberOfContours = (short)(bais.read()<<8 | bais.read());
                if (numberOfContours < 0) {
                    descript[i] = new GlyfCompositeDescript(this, bais);
                }
            }
        }
        buf = null;
    }

    public GlyfDescript getDescription(int i) {
        return descript[i];
    }

    public int getType() {
        return glyf;
    }
}
