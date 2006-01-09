/*

   Copyright 2004 The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/

package org.apache.batik.gvt.flow;

import java.util.List;

import org.apache.batik.gvt.TextNode;
import org.apache.batik.gvt.TextPainter;

import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Iterator;

import java.awt.geom.Point2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.GVTLineMetrics;
import org.apache.batik.gvt.font.MultiGlyphVector;
import org.apache.batik.gvt.text.GlyphLayout;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.renderer.StrokingTextPainter.TextRun;

/**
 * One line Class Desc
 *
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org">deweese</a>
 * @version $Id$
 */
public class FlowTextNode extends TextNode{

    public FlowTextNode() {
        textPainter = FlowTextPainter.getInstance();
    }

    public void setTextPainter(TextPainter textPainter) {
        if (textPainter == null)
            this.textPainter = FlowTextPainter.getInstance();
        else
            this.textPainter = textPainter;
    }
};
