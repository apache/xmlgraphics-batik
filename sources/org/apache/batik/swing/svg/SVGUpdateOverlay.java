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

package org.apache.batik.swing.svg;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics;

import org.apache.batik.swing.gvt.Overlay;

/**
 * One line Class Desc
 *
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org>deweese</a>
 * @version $Id$
 */
public class SVGUpdateOverlay implements Overlay {
    List rects = new LinkedList();
    int size, updateCount;
    int []counts;
    public SVGUpdateOverlay(int size, int numUpdates) {
        this.size = size;
        counts = new int[numUpdates];
    }

    public void addRect(Rectangle r) {
        rects.add(r);
        if (rects.size() > size)
            rects.remove(0);
        updateCount++;
    }

    public void endUpdate() {
        int i=0;
        int total =0;
        for (; i<counts.length-1; i++) {
            counts[i] = counts[i+1];
        }
        counts[i] = updateCount;
        updateCount = 0;
        
        int num = rects.size();
        for (i=counts.length-1; i>=0; i--) {
            if (counts[i] > num) {
                counts[i] = num;
            }
            num -= counts[i];
        }
        counts[0] += num;
    }

    public void paint(Graphics g) {
        Iterator i = rects.iterator();
        int count = 0;
        int idx = 0;
        int group = 0;
        while ((group < counts.length-1) && 
               (idx == counts[group]))
            group++;
        int cmax = counts.length-1;
        while (i.hasNext()) {
            Rectangle r = (Rectangle)i.next();
            Color c;
            c = new Color(1f, (cmax-group)/(float)cmax, 0,
                          (count+1f)/rects.size());
            g.setColor(c);
            g.drawRect(r.x, r.y, r.width, r.height);
            count++; idx++;
            while ((group < counts.length-1) && 
                   (idx == counts[group])) {
                group++;
                idx = 0;
            }
        }
    }
}
