/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class contains utility methods to manipulate Java classes.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class ClassFileUtilities {

    // Constant pool info tags
    public final static byte CONSTANT_UTF8_INFO                = 1;
    public final static byte CONSTANT_INTEGER_INFO             = 3;
    public final static byte CONSTANT_FLOAT_INFO               = 4;
    public final static byte CONSTANT_LONG_INFO                = 5;
    public final static byte CONSTANT_DOUBLE_INFO              = 6;
    public final static byte CONSTANT_CLASS_INFO               = 7;
    public final static byte CONSTANT_STRING_INFO              = 8;
    public final static byte CONSTANT_FIELDREF_INFO            = 9;
    public final static byte CONSTANT_METHODREF_INFO           = 10;
    public final static byte CONSTANT_INTERFACEMETHODREF_INFO  = 11;
    public final static byte CONSTANT_NAMEANDTYPE_INFO         = 12;

    /**
     * This class does not need to be instantiated.
     */
    protected ClassFileUtilities() {
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
            switch (dis.readByte() & 0xff) {
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
                throw new RuntimeException();
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
