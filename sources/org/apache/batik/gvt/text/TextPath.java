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

package org.apache.batik.gvt.text;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import org.apache.batik.ext.awt.geom.PathLength;

/**
 * A text path describes a path along which some text will be rendered.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class TextPath {

    private PathLength pathLength;
    private float startOffset;

    /**
     * Constructs a TextPath based on the specified path.
     *
     * @param path The general path along which text is to be laid.
     */
    public TextPath(GeneralPath path) {
        pathLength = new PathLength(path);
        startOffset = 0;
    }

    /**
     * Sets the offset along the path where the first glyph should be rendered.
     *
     * @param startOffset An offset from the start of the path.
     */
    public void setStartOffset(float startOffset) {
        this.startOffset = startOffset;
    }

    /**
     * Returns the start offset of this text path.
     *
     * @return The start offset of this text path.
     */
    public float getStartOffset() {
        return startOffset;
    }

    /**
     * Returns the total length of the path.
     *
     * @return The lenght of the path.
     */
    public float lengthOfPath() {
        return pathLength.lengthOfPath();
    }

    /**
     * Returns the angle at the specified length
     * along the path.
     *
     * @param length The length along the path.
     * @return The angle.
     */
    public float angleAtLength(float length) {
        return pathLength.angleAtLength(length);
    }

    /**
     * Returns the point that is at the specified length
     * along the path.
     *
     * @param length The length along the path.
     * @return The point.
     */
    public Point2D pointAtLength(float length) {
        return pathLength.pointAtLength(length);
    }
}
