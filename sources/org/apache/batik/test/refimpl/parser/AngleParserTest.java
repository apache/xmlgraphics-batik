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
 * To test the angle parser.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class AngleParserTest {
    /**
     * The main method.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
	AngleParser parser = new ConcreteAngleParser();
	parser.setAngleHandler(new TestAngleHandler());

	parser.parse(new StringReader("1.2"));
	parser.parse(new StringReader("1.2deg"));
	parser.parse(new StringReader("1e+3rad"));
	parser.parse(new StringReader("-1.23e2grad"));

        System.out.println(" ***** TEST OK *****");
    }

    static class TestAngleHandler implements AngleHandler {
	public void startAngle() throws ParseException {
	    System.out.println(" %%% start");
	}

	public void endAngle() throws ParseException {
	    System.out.println("\n %%% end");
	}

	public void angleValue(float v) throws ParseException {
	    System.out.print(v);
	}

	public void deg() throws ParseException {
	    System.out.print("deg");
	}

	public void grad() throws ParseException {
	    System.out.print("grad");
	}

	public void rad() throws ParseException {
	    System.out.print("rad");
	}
    }
}
