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

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 * @version $Id$
 */
public class GsubTable implements Table, LookupSubtableFactory {

    private ScriptList scriptList;
    private FeatureList featureList;
    private LookupList lookupList;
    
    protected GsubTable(DirectoryEntry de, RandomAccessFile raf) throws IOException {
        raf.seek(de.getOffset());

        // GSUB Header
        int version = raf.readInt();
        int scriptListOffset = raf.readUnsignedShort();
        int featureListOffset = raf.readUnsignedShort();
        int lookupListOffset = raf.readUnsignedShort();

        // Script List
        scriptList = new ScriptList(raf, de.getOffset() + scriptListOffset);

        // Feature List
        featureList = new FeatureList(raf, de.getOffset() + featureListOffset);
        
        // Lookup List
        lookupList = new LookupList(raf, de.getOffset() + lookupListOffset, this);
    }

    /**
     * 1 - Single - Replace one glyph with one glyph 
     * 2 - Multiple - Replace one glyph with more than one glyph 
     * 3 - Alternate - Replace one glyph with one of many glyphs 
     * 4 - Ligature - Replace multiple glyphs with one glyph 
     * 5 - Context - Replace one or more glyphs in context 
     * 6 - Chaining - Context Replace one or more glyphs in chained context
     */
    public LookupSubtable read(int type, RandomAccessFile raf, int offset)
    throws IOException {
        LookupSubtable s = null;
        switch (type) {
        case 1:
            s = SingleSubst.read(raf, offset);
            break;
        case 2:
//            s = MultipleSubst.read(raf, offset);
            break;
        case 3:
//            s = AlternateSubst.read(raf, offset);
            break;
        case 4:
            s = LigatureSubst.read(raf, offset);
            break;
        case 5:
//            s = ContextSubst.read(raf, offset);
            break;
        case 6:
//            s = ChainingSubst.read(raf, offset);
            break;
        }
        return s;
    }

    /** Get the table type, as a table directory value.
     * @return The table type
     */
    public int getType() {
        return GSUB;
    }

    public ScriptList getScriptList() {
        return scriptList;
    }

    public FeatureList getFeatureList() {
        return featureList;
    }

    public LookupList getLookupList() {
        return lookupList;
    }

    public String toString() {
        return "GSUB";
    }

}
