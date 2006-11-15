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

package org.apache.batik;

/**
 * This class defines the Batik version number.
 *
 * @author <a href="mailto:vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public final class Version {

    /**
     * Returns the Batik version.
     * <p>
     *   This is based on the 'HeadURL' keyword.  This will be substituted with
     *   the URL of this file, which is then inspected to determine if this
     *   file was compiled from the trunk, a tag (a release verison), or a
     *   branch.  The format of the returned string will be one of the
     *   following:
     * </p>
     * <table>
     *   <tr>
     *     <th>Source</th>
     *     <th>Version string</th>
     *   </tr>
     *   <tr>
     *     <td>Release version</td>
     *     <td><em>version</em></td>
     *   </tr>
     *   <tr>
     *     <td>Trunk</td>
     *     <td>SVN+<em>yyyymmdd</em></td>
     *   </tr>
     *   <tr>
     *     <td>Branch</td>
     *     <td><em>branch-name</em> branch; SVN+<em>yyyymmdd</em></td>
     *   </tr>
     *   <tr>
     *     <td>Unknown</td>
     *     <td>development version</td>
     *   </tr>
     * </table>
     * <p>
     *   Prior to SVN+20060704 and release 1.6, the version string would
     *   be the straight tag (e.g. <code>"batik-1_6"</code>) or the
     *   string <code>"development.version"</code>.
     * </p>
     */
    public static String getVersion() {
        String version = "development version";
        String headURL = "$HeadURL$";
        String prefix = "$HeadURL: ";
        String suffix = "/sources/org/apache/batik/Version.java $";
        if (headURL.startsWith(prefix) && headURL.endsWith(suffix)) {
            headURL = headURL.substring
                (prefix.length(), headURL.length() - suffix.length());
            if (headURL.endsWith("/trunk")) {
                // SVN trunk
                version = "SVN+" + getDate();
            } else {
                int index1 = headURL.lastIndexOf('/');
                int index2 = headURL.lastIndexOf('/', index1 - 1);
                String name = headURL.substring(index1 + 1);
                String type = headURL.substring(index2 + 1, index1);
                String tagPrefix = "batik-";
                if (type.equals("tags") && name.startsWith(tagPrefix)) {
                    // Release
                    version = name.substring(tagPrefix.length())
                                  .replace('_', '.');
                } else if (type.equals("branches")) {
                    // SVN branch
                    version = name + "; SVN+" + getDate();
                }
            }
        }

        return version;
    }

    /**
     * Returns the last modified date of this file in <em>YYYYMMDD</em>
     * format.
     */
    protected static String getDate() {
        String date = "$Date$";
        String prefix = "$Date: ";
        if (date.startsWith(prefix)) {
            return date.substring(7, 11)
                 + date.substring(12, 14)
                 + date.substring(15, 17);
        }
        return "unknown";
    }
}
