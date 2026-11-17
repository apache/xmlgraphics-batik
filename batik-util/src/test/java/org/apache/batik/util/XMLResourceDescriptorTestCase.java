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
package org.apache.batik.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class XMLResourceDescriptorTestCase {

    /** BATIK-1400 */
    @Test
    public void testGetParserPropsClosesResourceStream() throws Exception {
        TrackingClassLoader loader = new TrackingClassLoader(getClass().getClassLoader());
        byte[] classBytes;
        try (InputStream in = XMLResourceDescriptor.class.getResourceAsStream("XMLResourceDescriptor.class")) {
            classBytes = IOUtils.toByteArray(in);
        }
        Class<?> descriptorClass = loader.define(classBytes);
        Method getParserProps = descriptorClass.getDeclaredMethod("getParserProps");
        getParserProps.setAccessible(true);
        getParserProps.invoke(null);
        Assert.assertTrue("getParserProps() did not close the InputStream (BATIK-1400)", loader.streamClosed);
    }

    private static class TrackingClassLoader extends ClassLoader {
        private boolean streamClosed;

        private TrackingClassLoader(ClassLoader parent) {
            super(parent);
        }

        private Class<?> define(byte[] bytes) {
            return defineClass(null, bytes, 0, bytes.length);
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            InputStream in = super.getResourceAsStream(name);
            if (in == null || !name.endsWith("XMLResourceDescriptor.properties")) return in;
            return new FilterInputStream(in) {
                @Override
                public void close() throws IOException {
                    streamClosed = true;
                    super.close();
                }
            };
        }
    }
}
