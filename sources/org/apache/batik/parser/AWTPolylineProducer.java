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

package org.apache.batik.parser;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.io.Reader;

/**
 * This class produces a polyline shape from a reader.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class AWTPolylineProducer implements PointsHandler, ShapeProducer {
    /**
     * The current path.
     */
    protected GeneralPath path;

    /**
     * Is the current path a new one?
     */
    protected boolean newPath;

    /**
     * The winding rule to use to construct the path.
     */
    protected int windingRule;

    /**
     * Utility method for creating an ExtendedGeneralPath.
     * @param r The reader used to read the path specification.
     * @param wr The winding rule to use for creating the path.
     */
    public static Shape createShape(Reader r, int wr)
        throws IOException,
               ParseException {
        PointsParser p = new PointsParser();
        AWTPolylineProducer ph = new AWTPolylineProducer();

        ph.setWindingRule(wr);
        p.setPointsHandler(ph);
        p.parse(r);

        return ph.getShape();
    }

    /**
     * Sets the winding rule used to construct the path.
     */
    public void setWindingRule(int i) {
        windingRule = i;
    }

    /**
     * Returns the current winding rule.
     */
    public int getWindingRule() {
        return windingRule;
    }

    /**
     * Returns the Shape object initialized during the last parsing.
     * @return the shape or null if this handler has not been used by
     *         a parser.
     */
    public Shape getShape() {
        return path;
    }

    /**
     * Implements {@link PointsHandler#startPoints()}.
     */
    public void startPoints() throws ParseException {
        path = new GeneralPath(windingRule);
        newPath = true;
    }

    /**
     * Implements {@link PointsHandler#point(float,float)}.
     */
    public void point(float x, float y) throws ParseException {
        if (newPath) {
            newPath = false;
            path.moveTo(x, y);
        } else {
            path.lineTo(x, y);
        }
    }

    /**
     * Implements {@link PointsHandler#endPoints()}.
     */
    public void endPoints() throws ParseException {
    }
}
