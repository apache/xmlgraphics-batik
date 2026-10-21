/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.transcoder.wmf.tosvg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.wmf.WMFConstants;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Checks that a truncated WMF file declaring an oversized record does not
 * allocate memory up front for the declared length. The amount of memory
 * committed should be bounded by the bytes actually present in the stream,
 * so a record that claims a size much larger than the remaining input is
 * reported as a read error rather than attempting a huge allocation.
 */
public class WMFRecordStoreTest {

    /** Writes a 16-bit little-endian value. */
    private static void writeShort(ByteArrayOutputStream out, int v) {
        out.write(v & 0xff);
        out.write((v >> 8) & 0xff);
    }

    /** Writes a 32-bit little-endian value. */
    private static void writeInt(ByteArrayOutputStream out, int v) {
        out.write(v & 0xff);
        out.write((v >> 8) & 0xff);
        out.write((v >> 16) & 0xff);
        out.write((v >> 24) & 0xff);
    }

    /**
     * Builds a tiny (~50 byte) WMF file with a standard (non-Aldus) header
     * followed by a single record whose declared size is far larger than the
     * data that actually follows it.
     */
    private static byte[] buildOversizedRecordWMF() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Standard METAHEADER (no Aldus placeable header).
        writeShort(out, 1);         // mtType
        writeShort(out, 9);         // mtHeaderSize (in words)
        writeShort(out, 0x0300);    // mtVersion
        writeInt(out, 0);           // mtSize
        writeShort(out, 0);         // mtNoObjects
        writeInt(out, 0);           // mtMaxRecord
        writeShort(out, 0);         // mtNoParameters

        // One record with an oversized declared size. recSize is read as a
        // 32-bit word count and the reader subtracts 3, so 0x3FFFFFFF yields a
        // byte length of about 2 GB for META_DIBCREATEPATTERNBRUSH
        // (len = 2 * recSize - 4) while no bitmap data follows.
        writeInt(out, 0x3FFFFFFF);                            // recSize
        writeShort(out, WMFConstants.META_DIBCREATEPATTERNBRUSH); // functionId
        writeInt(out, 0);                                    // type field
        // No bitmap bytes follow: the stream ends here.

        return out.toByteArray();
    }

    @Test(timeout = 30000)
    public void oversizedRecordDoesNotExhaustMemory() throws Exception {
        byte[] wmf = buildOversizedRecordWMF();
        // Sanity check: the input file is tiny.
        assertTrue("expected a small input file", wmf.length < 100);

        TranscoderInput input =
            new TranscoderInput(new ByteArrayInputStream(wmf));
        TranscoderOutput output = new TranscoderOutput(new StringWriter());

        try {
            new WMFTranscoder().transcode(input, output);
            // Reaching the painting stage is acceptable too; the point is that
            // the truncated record must not allocate for its declared length.
        } catch (TranscoderException e) {
            // Expected: the truncated record is detected while reading and
            // reported as a transcoding failure.
        } catch (OutOfMemoryError e) {
            fail("oversized record declaration led to an OutOfMemoryError: "
                 + e.getMessage());
        }
    }
}
