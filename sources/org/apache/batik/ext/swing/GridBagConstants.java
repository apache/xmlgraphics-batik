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
package org.apache.batik.ext.swing;

import java.awt.GridBagConstraints;

/**
 * <tt>GridBagConstraints</tt> constants.
 *
 * @author <a href="mailto:vhardy@eng.sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public interface GridBagConstants {
    /**
     * Specify that this component is the 
     * last component in its column or row. 
     * @since   JDK1.0
     */
    public static final int REMAINDER = GridBagConstraints.REMAINDER;

    /**
     * Do not resize the component. 
     * @since   JDK1.0
     */
    public static final int NONE = GridBagConstraints.NONE;

    /**
     * Resize the component both horizontally and vertically. 
     * @since   JDK1.0
     */
    public static final int BOTH = GridBagConstraints.BOTH;

    /**
     * Resize the component horizontally but not vertically. 
     * @since   JDK1.0
     */
    public static final int HORIZONTAL = GridBagConstraints.HORIZONTAL;

    /**
     * Resize the component vertically but not horizontally. 
     * @since   JDK1.0
     */
    public static final int VERTICAL = GridBagConstraints.VERTICAL;

    /**
     * Put the component in the center of its display area.
     * @since    JDK1.0
     */
    public static final int CENTER = GridBagConstraints.CENTER;

    /**
     * Put the component at the top of its display area,
     * centered horizontally. 
     * @since   JDK1.0
     */
    public static final int NORTH = GridBagConstraints.NORTH;

    /**
     * Put the component at the top-right corner of its display area. 
     * @since   JDK1.0
     */
    public static final int NORTHEAST = GridBagConstraints.NORTHEAST;

    /**
     * Put the component on the left side of its display area, 
     * centered vertically.
     * @since    JDK1.0
     */
    public static final int EAST = GridBagConstraints.EAST;

    /**
     * Put the component at the bottom-right corner of its display area. 
     * @since   JDK1.0
     */
    public static final int SOUTHEAST = GridBagConstraints.SOUTHEAST;

    /**
     * Put the component at the bottom of its display area, centered 
     * horizontally. 
     * @since   JDK1.0
     */
    public static final int SOUTH = GridBagConstraints.SOUTH;

    /**
     * Put the component at the bottom-left corner of its display area. 
     * @since   JDK1.0
     */
    public static final int SOUTHWEST = GridBagConstraints.SOUTHWEST;

    /**
     * Put the component on the left side of its display area, 
     * centered vertically.
     * @since    JDK1.0
     */
    public static final int WEST = GridBagConstraints.WEST;

    /**
     * Put the component at the top-left corner of its display area. 
     * @since   JDK1.0
     */
    public static final int NORTHWEST = GridBagConstraints.NORTHWEST;

}
