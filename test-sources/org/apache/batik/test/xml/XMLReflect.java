/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.xml;

import java.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This helper class can be used to build Java object from their
 * XML description.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class XMLReflect implements XMLReflectConstants{
    /**
     * Implementation helper: builds a generic object
     */
    public static Object buildObject(Element element) throws Exception {

        String className
            = getInheritedClassAttribute(element);

        Class cl = Class.forName(className);
        Object[] argsArray = null;
        Class[]  argsClasses = null;

        NodeList children = element.getChildNodes();
        if(children != null && children.getLength() > 0){
            int n = children.getLength();
            Vector args = new Vector();
            for(int i=0; i<n; i++){
                Node child = children.item(i);
                if(child.getNodeType() == Node.ELEMENT_NODE){
                    Element childElement = (Element)child;
                    String tagName = childElement.getTagName().intern();
                    if(tagName == XR_ARG_TAG){
                        Object arg = buildArgument(childElement);
                        args.addElement(arg);
                    }
                }
            }

            if(args.size() > 0){
                argsArray = new Object[args.size()];
                args.copyInto(argsArray);

                argsClasses = new Class[args.size()];

                for(int i=0; i<args.size(); i++){
                    argsClasses[i] = argsArray[i].getClass();
                }
            }
        }

        Constructor constructor
            = getDeclaredConstructor(cl, argsClasses);

        return configureObject(constructor.newInstance(argsArray),
                               element);
    }

    /**
     * Implementation helper: configures a generic object
     */
    public static Object configureObject(Object obj,
                                         Element element) throws Exception {
        NodeList children = element.getChildNodes();
        if(children != null && children.getLength() > 0){
            int n = children.getLength();
            Vector args = new Vector();
            for(int i=0; i<n; i++){
                Node child = children.item(i);
                if(child.getNodeType() == Node.ELEMENT_NODE){
                    Element childElement = (Element)child;
                    String tagName = childElement.getTagName().intern();
                    if(tagName == XR_PROPERTY_TAG){
                        Object arg = buildArgument(childElement);
                        String propertyName
                            = childElement.getAttribute(XR_NAME_ATTRIBUTE);
                        setObjectProperty(obj, propertyName, arg);
                    }
                }
            }

        }

        return obj;
    }

    /**
     * Sets the property with given name on object to the input value
     */
    public static void setObjectProperty(Object obj,
                                          String propertyName,
                                          Object propertyValue)
        throws Exception {
        Class cl = obj.getClass();
        Method m = cl.getMethod("set" + propertyName,
                                new Class[]{propertyValue.getClass()});

        if(m != null){
            m.invoke(obj, new Object[]{propertyValue});
        }
    }


    /**
     * Returns a constructor that has can be used for the input class
     * types.
     */
    public static Constructor getDeclaredConstructor(Class cl,
                                                 Class[] argClasses){
        Constructor[] cs = cl.getDeclaredConstructors();
        for(int i=0; i<cs.length; i++){
            Class[] reqArgClasses = cs[i].getParameterTypes();
            if(reqArgClasses != null && reqArgClasses.length > 0){
                if(reqArgClasses.length == argClasses.length){
                    int j=0;
                    for(; j<argClasses.length; j++){
                        if(!reqArgClasses[j].isAssignableFrom(argClasses[j])){
                            break;
                        }
                    }
                    if(j == argClasses.length){
                        return cs[i];
                    }
                }
            }
            else{
                if(argClasses == null || argClasses.length == 0){
                    return cs[i];
                }
            }
        }

        return null;
    }

    /**
     * Limitation: Arguments *must* have a String based
     * constructor. Or be an object that takes a set of string
     * based arguments.
     */
    public static Object buildArgument(Element element) throws Exception {
        if(!element.hasChildNodes()){
            String classAttr = getInheritedClassAttribute(element);

            // String based argument
            Class cl = Class.forName(classAttr);

            if(element.hasAttribute(XR_VALUE_ATTRIBUTE)){
                String value = element.getAttribute(XR_VALUE_ATTRIBUTE);


                Constructor constructor
                    = cl.getDeclaredConstructor(new Class[] { String.class });

                return constructor.newInstance(new Object[] {value});
            }
            else{
                // Default constructor
                return cl.newInstance();
            }
        }
        else{
            return buildObject(element);
        }
    }

    /**
     * The class name to use is that of the element itself or, if not
     * specified, that of its closest ancestor which defines it
     * (through the XR_CLASS_ATTRIBUTE
     */
    public static String getInheritedClassAttribute(Element element){
        if(element != null){
            String classAttr = element.getAttribute(XR_CLASS_ATTRIBUTE);

            if(classAttr == null || "".equals(classAttr)){
                Node parent = element.getParentNode();
                if(parent != null && parent.getNodeType() == Node.ELEMENT_NODE){
                    return getInheritedClassAttribute((Element)parent);
                }
                else{
                    return null;
                }
            }
            
            return classAttr;

        }

        return null;
    }
}
