/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.parser;

import java.io.*;
import org.apache.batik.parser.*;

/**
 * To test the transform list parser.
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class TransformListParserTest {
    /**
     * The application main method.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) throws Exception {
	TransformListParser parser = new TransformListParser();
	parser.setTransformListHandler(new TestTransformListHandler());

	parser.parse(new StringReader("rotate(1.23)"));
	parser.parse(new StringReader("rotate (1.23e3 )"));
	parser.parse(new StringReader("rotate( 1.23e3, 0, 3)"));
	parser.parse(new StringReader("rotate(1.23e3, +0.05  -3 )"));
	parser.parse(new StringReader("rotate(1.23e3, +0.05 , -3 )"));
	parser.parse(new StringReader("matrix(1, 2, 3, 4, 5, 6)"));
	parser.parse(new StringReader("matrix(1 2 3 4 5 6)"));
	parser.parse(new StringReader("translate(1)"));
	parser.parse(new StringReader("translate(1,2)"));
	parser.parse(new StringReader("scale(1)"));
	parser.parse(new StringReader("scale(1.3e+3,2e-2)"));
	parser.parse(new StringReader("skewX(1)"));
	parser.parse(new StringReader("skewY(2)"));
	parser.parse(new StringReader
	    (" translate(123) skewX(3) rotate(321) "));
        
        System.out.println(" ***** TEST OK *****");
    }

    public static class TestTransformListHandler
	implements TransformListHandler {
	public void startTransformList() throws ParseException {
	    System.out.println(" %%% start");
	}

	public void matrix(float a, float b, float c,
			   float d, float e, float f)
	    throws ParseException {
	    System.out.println("matrix(" + a + ", " + b + ", " + c +
			            ", " + d + ", " + e + ", " + f + ")");
	}

	public void rotate(float theta) throws ParseException {
	    System.out.println("rotate(" + theta + ")");
	}

	public void rotate(float theta, float cx, float cy)
	    throws ParseException {
	    System.out.println
		("rotate(" + theta + ", " + cx + ", " + cy + ")");
	}

	public void translate(float tx) throws ParseException {
	    System.out.println("translate(" + tx + ")");
	}

	public void translate(float tx, float ty) throws ParseException {
	    System.out.println("translate(" + tx + ", " + ty + ")");
	}

	public void scale(float sx) throws ParseException {
	    System.out.println("scale(" + sx + ")");
	}

	public void scale(float sx, float sy) throws ParseException {
	    System.out.println("scale(" + sx + ", " + sy + ")");
	}

	public void skewX(float skx) throws ParseException {
	    System.out.println("skewX(" + skx + ")");
	}

	public void skewY(float sky) throws ParseException {
	    System.out.println("skewY(" + sky + ")");
	}

	public void endTransformList() throws ParseException {
	    System.out.println(" %%% end");
	}
    }
}
