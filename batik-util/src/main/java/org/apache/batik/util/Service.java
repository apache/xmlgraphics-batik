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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * This class handles looking up service providers on the class path.
 * It implements the system described in:
 *
 * <a href="http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html#Service%20Provider">JAR
 * File Specification Under Service Provider</a>. Note that this
 * interface is very similar to the one they describe which seems to
 * be missing in the JDK.
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class Service {

    /**
     * Returns an iterator where each element should implement the
     * interface (or subclass the baseclass) described by cls.  The
     * Classes are found by searching the classpath for service files
     * named: 'META-INF/services/&lt;fully qualified classname&gt; that list
     * fully qualifted classnames of classes that implement the
     * service files classes interface.  These classes must have
     * default constructors.
     *
     * @param cls The class/interface to search for providers of.
     */
    public static synchronized Iterator providers(Class cls) {
        
        ServiceLoader serviceLoader=ServiceLoader.load(cls);
        return serviceLoader.iterator();
    }
}
