/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.xml;

/**
 * Contains constants for elements and attributes used to
 * describe Java objects, constructor arguments and properties
 * in XML.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public interface XMLReflectConstants {
    /////////////////////////////////////////////////////////////////////////
    // Tags
    /////////////////////////////////////////////////////////////////////////
    String XR_ARG_TAG        = "arg";
    String XR_PROPERTY_TAG   = "property";

    /////////////////////////////////////////////////////////////////////////
    // XTS attributes
    /////////////////////////////////////////////////////////////////////////
    String XR_CLASS_ATTRIBUTE = "class";
    String XR_NAME_ATTRIBUTE  = "name";
    String XR_VALUE_ATTRIBUTE = "value";
}
