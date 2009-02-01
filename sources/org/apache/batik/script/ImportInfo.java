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

package org.apache.batik.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import java.util.List;
import java.util.LinkedList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 * This class represents a list of classes/packages to import.
 *
 * It initializes it's self by reading a file from the classpath.
 *
 * @author <a href="mailto:deweese@apache.org>deweese</a>
 * @version $Id: skel.el,v 1.1 2003/05/13 21:04:46 deweese Exp $
 */
public class ImportInfo {
    static final String defaultFile = "META-INF/imports/script.txt";
    static final String classStr    = "class";
    static final String packageStr  = "package";

    static String importFile;
    static {
        importFile = defaultFile;
        try {
            importFile = System.getProperty
                ("org.apache.batik.script.imports", defaultFile);
        } catch (SecurityException se) {
        } catch (NumberFormatException nfe) {
        }
    }

    static ImportInfo defaultImports=null;

    static public ImportInfo getImports() {
        if (defaultImports == null) 
            defaultImports = readImports();
        return defaultImports;    
    }

    static ImportInfo readImports() {
        ImportInfo ret = new ImportInfo();

        // Can always request your own class loader. But it might be 'null'.
        ClassLoader cl = ImportInfo.class.getClassLoader();

        // No class loader so we can't find 'serviceFile'.
        if (cl == null) return ret;

        Enumeration e;
        try {
            e = cl.getResources(importFile);
        } catch (IOException ioe) {
            return ret;
        }

        while (e.hasMoreElements()) {
            try {
                URL url = (URL)e.nextElement();
                ret.addImports(url);
            } catch (Exception ex) {
                // Just try the next file...
                // ex.printStackTrace();
            }
        }

        return ret;
    }

    
    protected List classes;
    protected List packages;

    public ImportInfo() {
        classes = new LinkedList();
        packages = new LinkedList();
    }
    public Iterator getClasses()  { return classes.iterator(); }
    public Iterator getPackages() { return packages.iterator(); }

    public void addClass(String cls) {
        classes.add(cls);
    }

    public void addPackage(String pkg) {
        packages.add(pkg);
    }

    public void addImports(URL src) throws IOException
    {
        InputStream    is = null;
        Reader         r  = null;
        BufferedReader br = null;
        try {
            is = src.openStream();
            r  = new InputStreamReader(is, "UTF-8");
            br = new BufferedReader(r);

            String line;
            while ((line = br.readLine()) != null) {
                // First strip any comment...
                int idx = line.indexOf('#');
                if (idx != -1)
                    line = line.substring(0, idx);

                // Trim whitespace.
                line = line.trim();
                // If nothing left then loop around...
                if (line.length() == 0) continue;
                
                // Line must start with 'class ' or 'package '.
                idx = line.indexOf(' ');
                if (idx == -1) continue; 

                String prefix = line.substring(0,idx);
                line = line.substring(idx+1);
                boolean isPackage = packageStr.equals(prefix);
                boolean isClass   = classStr.equals(prefix);

                if (!isPackage && !isClass) continue;

                while (line.length() != 0) {
                    idx = line.indexOf(' ');
                    String id;
                    if (idx == -1) {
                        id = line;
                        line = ""; 
                    } else {
                        id   = line.substring(0, idx);
                        line = line.substring(idx+1);
                    }
                    if (id.length() == 0) continue;

                    if (isClass) addClass(id);
                    else         addPackage(id);
                }
            }
        }
        finally {
            // close and release all io-resources to avoid leaks
            if ( is != null ){
                try {
                    is.close();
                } catch ( IOException ignored ){}
                is = null;
            }
            if ( r != null ){
                try{
                    r.close();
                } catch ( IOException ignored ){}
                r = null;
            }
            if ( br == null ){
                try{
                    br.close();
                } catch ( IOException ignored ){}
                br = null;
            }
        }
    }
};
