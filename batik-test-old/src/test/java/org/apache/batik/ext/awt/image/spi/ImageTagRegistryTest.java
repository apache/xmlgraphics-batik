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
package org.apache.batik.ext.awt.image.spi;

import org.apache.batik.test.AbstractTest;

public class ImageTagRegistryTest extends AbstractTest {
    @Override
    public boolean runImplBasic() throws Exception {
        ImageTagRegistry ir = new ImageTagRegistry();
        // Add a new registry entry with a HIGHER priority first
        ir.register(new AbstractRegistryEntry("Unit test", 100, "working", "application/working") {
        });
        // Ensure the first one is present:
        assertTrue(ir.getRegisteredMimeTypes().contains("application/working"));
        // Ensure the second is NOT YET present:
        assertTrue(!ir.getRegisteredMimeTypes().contains("application/missing"));
        // Add a new registry entry with a LOW priority later
        ir.register(new AbstractRegistryEntry("Unit test", 1, "missing", "application/missing") {
        });
        // This one still works - this is expected:
        assertTrue(ir.getRegisteredMimeTypes().contains("application/working"));
        // The second was not added because of BATIK-1203.
        assertTrue(ir.getRegisteredMimeTypes().contains("application/missing"));
        return true;
    }
}
