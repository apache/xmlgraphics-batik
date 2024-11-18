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
package org.apache.batik.css.parser;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.LexicalUnit;

import java.util.ArrayList;
import java.util.List;

public class ParserTestCase {
    @Test
    public void testStyleDeclaration() throws Exception {
        Parser parser = new Parser();
        List<String> properties = new ArrayList<>();
        parser.setDocumentHandler(new DefaultDocumentHandler() {
            public void property(String name, LexicalUnit value, boolean important) throws CSSException {
                properties.add(name);
            }
        });
        parser.parseStyleDeclaration("a:b;-inkscape-font-specification:'Calibri, Normal';c:d");
        Assert.assertTrue(properties.toString(), properties.contains("c"));
    }
}
