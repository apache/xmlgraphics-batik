/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 * @version $Id$
 */
public class ScriptList {

    private int scriptCount = 0;
    private ScriptRecord[] scriptRecords;
    private Script[] scripts;
    
    /** Creates new ScriptList */
    protected ScriptList(RandomAccessFile raf, int offset) throws IOException {
        raf.seek(offset);
        scriptCount = raf.readUnsignedShort();
        scriptRecords = new ScriptRecord[scriptCount];
        scripts = new Script[scriptCount];
        for (int i = 0; i < scriptCount; i++) {
            scriptRecords[i] = new ScriptRecord(raf);
        }
        for (int i = 0; i < scriptCount; i++) {
            scripts[i] = new Script(raf, offset + scriptRecords[i].getOffset());
        }
    }

    public int getScriptCount() {
        return scriptCount;
    }
    
    public ScriptRecord getScriptRecord(int i) {
        return scriptRecords[i];
    }
    
    public Script findScript(String tag) {
        if (tag.length() != 4) {
            return null;
        }
        int tagVal = (int)((tag.charAt(0)<<24)
            | (tag.charAt(1)<<16)
            | (tag.charAt(2)<<8)
            | tag.charAt(3));
        for (int i = 0; i < scriptCount; i++) {
            if (scriptRecords[i].getTag() == tagVal) {
                return scripts[i];
            }
        }
        return null;
    }

}

