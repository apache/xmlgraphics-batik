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

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.batik.apps.svgbrowser.Main;

/**
 * Utility class for starting applications with a dynamic classpath setup for "java -jar".
 * This avoids having to hard-code the manifest's "Class-Path" entry. Instead, the library
 * JARs are automatically picked up from the "./lib" directory, or if that's not found, the
 * current directory.
 */
public class AppStarterUtil {

    private static final Class<?> STRING_ARRAY_CLASS = new String[0].getClass();

    /**
     * Starts the application derived from the "MainDynamic" class.
     * @param starterClass the "MainDynamic" class
     * @param args the command-line arguments
     */
    public static void startApp(Class<?> starterClass, String[] args) {
        String className = starterClass.getName();
        String mainClassName = className.substring(0, className.lastIndexOf("Dynamic"));
        startApp(mainClassName, args);
    }
    /**
     * Starts the application given the main class name.
     * @param mainClassName the main class name
     * @param args the command-line arguments
     */
    public static void startApp(String mainClassName, String[] args) {
        try {
            Class<?> mainClazz;
            try {
                //If the following class is not available, we need to dynamically build
                //the classpath. "-jar" was used.
                Class.forName("org.apache.batik.bridge.Bridge");
                mainClazz = Class.forName(mainClassName);
            } catch (ClassNotFoundException cnfe) {
                URL[] urls = getJARList();
                ClassLoader loader = new BatikFirstClassLoader(urls,
                        AppStarterUtil.class.getClassLoader());
                mainClazz = Class.forName(mainClassName, true, loader);
            }
            Method mainMethod = mainClazz.getMethod("main", STRING_ARRAY_CLASS);
            mainMethod.invoke(null, new Object[] {args});
        } catch (Exception e) {
            System.err.println("Unable to start " + Main.class.getName() + ":");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Determines the dynamic class path.
     * @return the array of JAR files for the class path
     * @throws MalformedURLException if there is a problem constructing URLs
     */
    private static URL[] getJARList() throws MalformedURLException {
        File baseDir = new File(".");
        List<URL> jars = new java.util.ArrayList<URL>();

        //Get JARs from the "normal" class path...
        String cp = System.getProperty("java.class.path");
        String[] cpEntries = cp.split(";");
        for (String entry : cpEntries) {
            File f = new File(baseDir, entry);
            jars.add(f.toURI().toURL());
        }

        //...and add JARs from the lib (or base) directory.
        File[] files;
        FileFilter filter = new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".jar");
            }
        };
        File libDir = new File(baseDir, "lib");
        if (!libDir.exists()) {
            libDir = baseDir;
        }
        files = libDir.listFiles(filter);
        if (files != null) {
            for (int i = 0, size = files.length; i < size; i++) {
                jars.add(files[i].toURI().toURL());
            }
        }
        URL[] urls = jars.toArray(new URL[jars.size()]);
        return urls;
    }

    /**
     * A special {@link URLClassLoader} subclass that loads Batik classes before before looking up
     * classes in the parent class loader.
     */
    private static class BatikFirstClassLoader extends URLClassLoader {

        public BatikFirstClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        /** {@inheritDoc} */
        @Override
        protected synchronized Class<?> loadClass(String name, boolean resolve)
                throws ClassNotFoundException {
            if (!name.startsWith("org.apache.batik.")) {
                return super.loadClass(name, resolve);
            }
            //Load all Batik-related classes before consulting the parent class loader
            try {
                Class<?> clazz = findLoadedClass(name);
                if (clazz == null) {
                    clazz = super.findClass(name);
                }
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            } catch (ClassNotFoundException cnfe) {
                return super.loadClass(name, resolve);
            }
        }

    }

}
