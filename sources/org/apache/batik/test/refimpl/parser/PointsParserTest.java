/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test.refimpl.parser;

import java.io.*;
import org.apache.batik.parser.*;
import org.apache.batik.refimpl.parser.*;

/**
 * To test the points parser.
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */

public class PointsParserTest {
    /**
     * The application main method.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) throws Exception {
	PointsParser parser = new ConcretePointsParser();
	parser.setPointsHandler(new TestPointsHandler());

	parser.parse(new StringReader("1 2"));
	parser.parse(new StringReader("1,2 3,4"));
	parser.parse(new StringReader("1-2+3-4"));
	parser.parse(new StringReader("  1e-2+3-4e+5-6  "));

        System.out.println(" ***** TEST OK *****");
    }

    static class TestPointsHandler implements PointsHandler {
	public void startPoints() throws ParseException {
	    System.out.println(" %%% start");
	}

	public void endPoints() throws ParseException {
	    System.out.println(" %%% end");
	}

	public void point(float x, float y) throws ParseException {
	    System.out.println(x + " " + y);
	}
    }
}
