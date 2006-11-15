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
package org.apache.batik.apps.svgbrowser;

/**
 * This interface defines constants for the possible resource
 * origins.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public interface ResourceOrigin {
    /**
     * Any origin
     */
    static final int ANY = 1;

    /**
     * Same as document
     */
    static final int DOCUMENT = 2;

    /**
     * Embeded into the document 
     */
    static final int EMBEDED = 4;

    /**
     * No origin is ok
     */
    static final int NONE = 8;
}
