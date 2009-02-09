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

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class represents a list of Java classes/packages to import
 * into a scripting environment.
 *
 * It can initializes it's self by reading a file,
 * from the classpath (META_INF/imports/script.xt).
 *
 * The format of the file is as follows:
 *
 * Anything after a '#' on a line is ignored.
 *
 * The first space delimited token on a line must be either 'class' or
 * 'package'.
 *
 * The remainder of a line is whitespace delimited, fully qualified,
 * Java class/package name (i.e.  java.lang.System).
 *
 * @author <a href="mailto:deweese@apache.org>deweese</a>
 * @version $Id: skel.el,v 1.1 2003/05/13 21:04:46 deweese Exp $
 */
public class ImportInfo {
    /** Default file to read imports from, can be overridden
     *  by setting the 'org.apache.batik.script.imports' System
     * property*/
    static final String defaultFile = "META-INF/imports/script.txt";

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

    /**
     * Returns the default ImportInfo instance.
     *
     * This instance is initialized by reading the file
     * identified by 'importFile'.
     */
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

    
    protected Set classes;
    protected Set packages;

    /**
     * Construct an empty ImportInfo instance
     */
    public ImportInfo() {
        classes = new HashSet();
        packages = new HashSet();
    }

    /**
     * Return an unmodifiable iterator over the list of classes 
     */
    public Iterator getClasses()  { 
        return Collections.unmodifiableSet(classes).iterator(); 
    }
    /**
     * Return an unmodifiable iterator over the list of packages 
     */
    public Iterator getPackages() { 
        return Collections.unmodifiableSet(packages).iterator(); 
    }

    /**
     * Add a class to the set of classes to import (must be
     * a fully qualified classname - "java.lang.System"). 
     */
    public void addClass  (String cls) { classes.add(cls); }
    /**
     * Add a package to the set of packages to import (must be
     * a fully qualified package - "java.lang"). 
     */
    public void addPackage(String pkg) { packages.add(pkg); }

    /**
     * Remove a class from the set of classes to import (must be
     * a fully qualified classname - "java.lang.System"). 
     * @return true if the class was present.
     */
    public boolean removeClass(String cls) { return classes.remove(cls); }
    /**
     * Remove a package from the set of packages to import (must be
     * a fully qualified package - "java.lang"). 
     * @return true if the package was present.
     */
    public boolean removePackage(String pkg) { return packages.remove(pkg); }


    static final String classStr    = "class";
    static final String packageStr  = "package";
    /**
     * Add imports read from a URL to this ImportInfo instance.
     * See the class documentation for the expected format of the file.
     */
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
