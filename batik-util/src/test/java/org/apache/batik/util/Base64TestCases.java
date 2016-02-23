package org.apache.batik.util;

import org.junit.Test;
import static org.junit.Assert.fail;

public class Base64TestCases {

    @Test
    public void testZeroByteEncode() throws Exception {
        performTest("ENCODE", "zeroByte", "zeroByte.64");
    }

    @Test
    public void testZeroByteDecode() throws Exception {
        performTest("DECODE", "zeroByte.64", "zeroByte");
    }

    private void performTest(String action, String ifName, String ofName) {
    }

}
