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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class contains utility methods to manipulate Java classes.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ClassFileUtilities {

    // Constant pool info tags
    public static final byte CONSTANT_UTF8_INFO                = 1;
    public static final byte CONSTANT_INTEGER_INFO             = 3;
    public static final byte CONSTANT_FLOAT_INFO               = 4;
    public static final byte CONSTANT_LONG_INFO                = 5;
    public static final byte CONSTANT_DOUBLE_INFO              = 6;
    public static final byte CONSTANT_CLASS_INFO               = 7;
    public static final byte CONSTANT_STRING_INFO              = 8;
    public static final byte CONSTANT_FIELDREF_INFO            = 9;
    public static final byte CONSTANT_METHODREF_INFO           = 10;
    public static final byte CONSTANT_INTERFACEMETHODREF_INFO  = 11;
    public static final byte CONSTANT_NAMEANDTYPE_INFO         = 12;

    /**
     * This class does not need to be instantiated.
     */
    protected ClassFileUtilities() {
    }

    /**
     * Program that computes the dependencies between the Batik jars.
     * <p>
     *   Run this from the main Batik distribution directory, after building
     *   the jars.  For every class file in the classes/ directory, it will
     *   determine which jar it lives in and then determine which jar files
     *   directly depend on which others.  The output is lines of the form:
     * </p>
     * <pre>  <i>number</i>,<i>from</i>,<i>to</i></pre>
     * <p>
     *   where mean that the <i>from</i> jar has <i>number</i> class files
     *   that depend on class files in the <i>to</i> jar.
     * </p>
     */
    public static void main(String[] args) {
        File classesDir = new File("classes");
        if (!classesDir.isDirectory()) {
            System.out.println("Directory 'classes' not found in current directory!");
            return;
        }
        File cwd = new File(".");
        File buildDir = null;
        String[] cwdFiles = cwd.list();
        for (int i = 0; i < cwdFiles.length; i++) {
            if (cwdFiles[i].startsWith("batik-")) {
                buildDir = new File(cwdFiles[i]);
                if (!buildDir.isDirectory()) {
                    buildDir = null;
                } else {
                    break;
                }
            }
        }
        if (buildDir == null || !buildDir.isDirectory()) {
            System.out.println("Directory 'batik-xxx' not found in current directory!");
            return;
        }

        HashSet classes = new HashSet();
        collectClassFiles(classesDir, classes);

        Set classpath = new HashSet();
        classpath.add(classesDir.getPath());

        Map cs = new HashMap();
        Map js = new HashMap();
        collectJars(buildDir, js);

        try {
            Iterator i = classes.iterator();
            while (i.hasNext()) {
                String from = (String) i.next();
                ClassFile cf = (ClassFile) cs.get(from);
                if (cf == null) {
                    cf = new ClassFile();
                    cf.name = from;
                    cs.put(from, cf);
                }
                Set result = getClassDependencies(from, classpath, false);
                Iterator j = result.iterator();
                while (j.hasNext()) {
                    String to = (String) j.next();
                    if (!from.equals(to)) {
                        ClassFile toFile = (ClassFile) cs.get(to);
                        if (toFile == null) {
                            toFile = new ClassFile();
                            toFile.name = to;
                            cs.put(to, toFile);
                        }
                        cf.deps.add(toFile);
                    }
                }
            }

            i = js.values().iterator();
            while (i.hasNext()) {
                Jar j = (Jar) i.next();
                JarFile jf = new JarFile(j.file);
                Enumeration entries = jf.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry ze = (ZipEntry) entries.nextElement();
                    String path = new File(classesDir, ze.getName()).getPath();
                    ClassFile cf = (ClassFile) cs.get(path);
                    if (cf != null) {
                        cf.jar = j;
                        j.files.add(cf);
                    }
                }
            }

            i = cs.values().iterator();
            while (i.hasNext()) {
                ClassFile fromFile = (ClassFile) i.next();
                Iterator j = fromFile.deps.iterator();
                while (j.hasNext()) {
                    ClassFile toFile = (ClassFile) j.next();
                    Jar fromJar = fromFile.jar;
                    Jar toJar = toFile.jar;
                    if (toJar == fromJar) {
                        continue;
                    }
                    Integer n = (Integer) fromJar.deps.get(toJar);
                    if (n == null) {
                        fromJar.deps.put(toJar, new Integer(1));
                    } else {
                        fromJar.deps.put(toJar, new Integer(n.intValue() + 1));
                    }
                }
            }

            List triples = new ArrayList(10);
            i = js.values().iterator();
            while (i.hasNext()) {
                Jar fromJar = (Jar) i.next();
                Iterator j = fromJar.deps.keySet().iterator();
                while (j.hasNext()) {
                    Jar toJar = (Jar) j.next();
                    Triple t = new Triple();
                    t.from = fromJar;
                    t.to = toJar;
                    t.count = ((Integer) fromJar.deps.get(toJar)).intValue();
                    triples.add(t);
                }
            }
            Collections.sort(triples);

            i = triples.iterator();
            while (i.hasNext()) {
                Triple t = (Triple) i.next();
                System.out.println(t.count + "," + t.from.name + "," + t.to.name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static class ClassFile {
        public String name;
        public List deps = new ArrayList(10);
        public Jar jar;
    }

    protected static class Jar {
        public String name;
        public File file;
        public Map deps = new HashMap();
        public Set files = new HashSet();
    }

    protected static class Triple implements Comparable {
        public Jar from;
        public Jar to;
        public int count;
        public int compareTo(Object o) {
            return ((Triple) o).count - count;
        }
    }

    private static void collectClassFiles(File dir, Collection classFiles) {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            String n = files[i].getName();
            if (n.endsWith(".class") && files[i].isFile()) {
                classFiles.add(files[i].getPath());
            } else if (files[i].isDirectory()) {
                collectClassFiles(files[i], classFiles);
            }
        }
    }

    private static void collectJars(File dir, Map jars) {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            String n = files[i].getName();
            if (n.endsWith(".jar") && files[i].isFile()) {
                Jar j = new Jar();
                j.name = files[i].getPath();
                j.file = files[i];
                jars.put(j.name, j);
            } else if (files[i].isDirectory()) {
                collectJars(files[i], jars);
            }
        }
    }

    /**
     * Returns the dependencies of the given class.
     * @param path The root class path.
     * @param classpath The set of directories (Strings) to scan.
     * @return a list of paths representing the used classes.
     */
    public static Set getClassDependencies(String path, Set classpath)
        throws IOException {
        InputStream is = new FileInputStream(path);

        Set result = new HashSet();
        Set done = new HashSet();

        computeClassDependencies(is, classpath, done, result);

        return result;
    }

    private static void computeClassDependencies(InputStream is,
                                                 Set classpath,
                                                 Set done,
                                                 Set result) throws IOException {
        Iterator it = getClassDependencies(is).iterator();
        while (it.hasNext()) {
            String s = (String)it.next();
            if (!done.contains(s)) {
                done.add(s);

                Iterator cpit = classpath.iterator();
                while (cpit.hasNext()) {
                    String root = (String)cpit.next();
                    StringBuffer sb = new StringBuffer(root);
                    sb.append('/').append(s).append(".class");
                    String path = sb.toString();

                    File f = new File(path);
                    if (f.isFile()) {
                        result.add(path);

                        computeClassDependencies(new FileInputStream(f),
                                                 classpath,
                                                 done,
                                                 result);
                    }
                }
            }
        }
    }

    /**
     * Returns the dependencies of the given class.
     * @return a list of strings representing the used classes.
     */
    public static Set getClassDependencies(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);

        if (dis.readInt() != 0xcafebabe) {
            throw new IOException("Invalid classfile");
        }

        dis.readInt();

        int len = dis.readShort();
        String[] strs = new String[len];
        Set classes = new HashSet();
        Set desc = new HashSet();

        for (int i = 1; i < len; i++) {
            int constCode = dis.readByte() & 0xff;
            switch ( constCode ) {
            case CONSTANT_LONG_INFO:
            case CONSTANT_DOUBLE_INFO:
                dis.readLong();
                i++;
                break;

            case CONSTANT_FIELDREF_INFO:
            case CONSTANT_METHODREF_INFO:
            case CONSTANT_INTERFACEMETHODREF_INFO:
            case CONSTANT_INTEGER_INFO:
            case CONSTANT_FLOAT_INFO:
                dis.readInt();
                break;

            case CONSTANT_CLASS_INFO:
                classes.add(new Integer(dis.readShort() & 0xffff));
                break;

            case CONSTANT_STRING_INFO:
                dis.readShort();
                break;

            case CONSTANT_NAMEANDTYPE_INFO:
                dis.readShort();
                desc.add(new Integer(dis.readShort() & 0xffff));
                break;

            case CONSTANT_UTF8_INFO:
                strs[i] = dis.readUTF();
                break;

            default:
                throw new RuntimeException("unexpected data in constant-pool:" + constCode );
            }
        }

        Set result = new HashSet();

        Iterator it = classes.iterator();
        while (it.hasNext()) {
            result.add(strs[((Integer)it.next()).intValue()]);
        }

        it = desc.iterator();
        while (it.hasNext()) {
            result.addAll(getDescriptorClasses(strs[((Integer)it.next()).intValue()]));
        }

        return result;
    }

    /**
     * Returns the classes contained in a field or method desciptor.
     */
    protected static Set getDescriptorClasses(String desc) {
        Set result = new HashSet();
        int  i = 0;
        char c = desc.charAt(i);
        switch (c) {
        case '(':
            loop: for (;;) {
                c = desc.charAt(++i);
                switch (c) {
                case '[':
                    do {
                        c = desc.charAt(++i);
                    } while (c == '[');
                    if (c != 'L') {
                        break;
                    }

                case 'L':
                    c = desc.charAt(++i);
                    StringBuffer sb = new StringBuffer();
                    while (c != ';') {
                        sb.append(c);
                        c = desc.charAt(++i);
                    }
                    result.add(sb.toString());
                    break;

                default:
                    break;

                case ')':
                    break loop;
                }
            }
            c = desc.charAt(++i);
            switch (c) {
            case '[':
                do {
                    c = desc.charAt(++i);
                } while (c == '[');
                if (c != 'L') {
                    break;
                }

            case 'L':
                c = desc.charAt(++i);
                StringBuffer sb = new StringBuffer();
                while (c != ';') {
                    sb.append(c);
                    c = desc.charAt(++i);
                }
                result.add(sb.toString());
                break;

            default:
            case 'V':
            }
            break;

        case '[':
            do {
                c = desc.charAt(++i);
            } while (c == '[');
            if (c != 'L') {
                break;
            }

        case 'L':
            c = desc.charAt(++i);
            StringBuffer sb = new StringBuffer();
            while (c != ';') {
                sb.append(c);
                c = desc.charAt(++i);
            }
            result.add(sb.toString());
            break;

        default:
        }

        return result;
    }
}
