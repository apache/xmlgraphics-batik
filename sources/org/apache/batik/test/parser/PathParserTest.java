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
 * To test the path parser.
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class PathParserTest {
    /**
     * The application main method.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) throws Exception {
	PathParser parser = new PathParser();
	parser.setPathHandler(new TestPathHandler());

	parser.parse(new StringReader("m 1 2"));
	parser.parse(new StringReader("m 1 2 3 4 5 6"));
	parser.parse(new StringReader("m 1 2 l 3 4"));
	parser.parse(new StringReader("m 1 2 l 3 4 5 6"));
	parser.parse(new StringReader("M1 2 3 4L 5 6"));
	parser.parse(new StringReader("M1 2 h 4"));
	parser.parse(new StringReader("M1 2 h 4 5"));
	parser.parse(new StringReader("M1-2 H 4"));
	parser.parse(new StringReader("M1 2 v 4 5"));
	parser.parse(new StringReader("M1+2e+3 V 4z"));
	parser.parse(new StringReader("m 1 2 c 3 4 5 6 7 8"));
	parser.parse(new StringReader("m 1 2 c 3 4 5 6 7 8 9 10 11 12 13 14"));
	parser.parse(new StringReader("m 1 2 q 3 4 5 6"));
	parser.parse(new StringReader("m 1 2 a 3 4 5 0 0 6 7"));
	parser.parse(new StringReader("m 1 2 A 3 4 5 1 1 6 7"));
	parser.parse(new StringReader("m 1 2 a 3,4 5 1 1 6 7z "));

        System.out.println(" ***** TEST OK *****");
    }

    static class TestPathHandler implements PathHandler {
	public void startPath() throws ParseException {
	    System.out.println(" %%% start");
	}

	public void endPath() throws ParseException {
	    System.out.println(" %%% end");
	}

	public void movetoRel(float x, float y) throws ParseException {
	    System.out.println("m " + x + " " + y);
	}

	public void movetoAbs(float x, float y) throws ParseException {
	    System.out.println("M " + x + " " + y);
	}

	public void closePath() throws ParseException {
	    System.out.println("z");
	}

	public void linetoRel(float x, float y) throws ParseException {
	    System.out.println("l " + x + " " + y);
	}

	public void linetoAbs(float x, float y) throws ParseException {
	    System.out.println("L " + x + " " + y);
	}

	public void linetoHorizontalRel(float x) throws ParseException {
	    System.out.println("h " + x);
	}

	public void linetoHorizontalAbs(float x) throws ParseException {
	    System.out.println("H " + x);
	}

	public void linetoVerticalRel(float y) throws ParseException {
	    System.out.println("v " + y);
	}

	public void linetoVerticalAbs(float y) throws ParseException {
	    System.out.println("V " + y);
	}

	public void curvetoCubicRel(float x1, float y1, 
				    float x2, float y2, 
				    float x, float y) throws ParseException {
	    System.out.println("c " + x1 + " " + y1 +
			       " "  + x2 + " " + y2 +
			       " "  + x  + " " + y);
	}

	public void curvetoCubicAbs(float x1, float y1, 
				    float x2, float y2, 
				    float x, float y) throws ParseException {
	    System.out.println("C " + x1 + " " + y1 +
			       " "  + x2 + " " + y2 +
			       " "  + x  + " " + y);
	}

	public void curvetoCubicSmoothRel(float x2, float y2, 
					  float x, float y)
            throws ParseException {
	    System.out.println("s " + x2 + " " + y2 + " "  + x  + " " + y);
	}

	public void curvetoCubicSmoothAbs(float x2, float y2, 
					  float x, float y)
            throws ParseException {
	    System.out.println("S " + x2 + " " + y2 + " "  + x  + " " + y);
	}

	public void curvetoQuadraticRel(float x1, float y1, 
					float x, float y)
            throws ParseException {
	    System.out.println("q " + x1 + " " + y1 + " "  + x  + " " + y);
	}

	public void curvetoQuadraticAbs(float x1, float y1, 
					float x, float y)
            throws ParseException {
	    System.out.println("Q " + x1 + " " + y1 + " "  + x  + " " + y);
	}

	public void curvetoQuadraticSmoothRel(float x, float y)
            throws ParseException {
	    System.out.println("t " + x  + " " + y);
	}

	public void curvetoQuadraticSmoothAbs(float x, float y)
            throws ParseException {
	    System.out.println("T " + x  + " " + y);
	}

	public void arcRel(float rx, float ry, 
			   float xAxisRotation, 
			   boolean largeArcFlag, boolean sweepFlag, 
			   float x, float y) throws ParseException {
	    System.out.println("a " + rx  + " " + ry + " " + xAxisRotation +
			       " "  + largeArcFlag + " " + sweepFlag +
			       " "  + x + " " + y);
	}

	public void arcAbs(float rx, float ry, 
			   float xAxisRotation, 
			   boolean largeArcFlag, boolean sweepFlag, 
			   float x, float y) throws ParseException {
	    System.out.println("A " + rx  + " " + ry + " " + xAxisRotation +
			       " "  + largeArcFlag + " " + sweepFlag +
			       " "  + x + " " + y);
	}
    }
}
