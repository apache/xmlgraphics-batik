/*

   Copyright 2002  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.bridge;

/**
 * An interface that allows UserAgents to describe the security 
 * constraints desired for external resources.
 *
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public interface ExternalResourceSecurity {
    /**
     * Controls whether the external resource should be loaded or not.
     *
     * @throws SecurityException if the resource should not be loaded.
     */
    void checkLoadExternalResource();
}


    
