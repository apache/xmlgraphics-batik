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
 * To test the length parser.
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class LengthParserTest {
    /**
     * The application main method.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) throws Exception {
	LengthParser parser = new LengthParser();
	parser.setLengthHandler(new TestLengthHandler());

	parser.parse(new StringReader("1.2"));
	parser.parse(new StringReader("1.2em"));
	parser.parse(new StringReader("1e+3em"));
	parser.parse(new StringReader("-1.23e2%"));
	parser.parse(new StringReader("1.2mm"));

	LengthListParser parser2 = new LengthListParser();
	parser2.setLengthListHandler(new TestLengthListHandler());

	parser2.parse(new StringReader("1.2"));
	parser2.parse(new StringReader("1.2em 3 4%"));

        System.out.println(" ***** TEST OK *****");
    }

    static class TestLengthHandler implements LengthHandler {
	public void startLength() throws ParseException {
	    System.out.println(" %%% start");
	}

	public void endLength() throws ParseException {
	    System.out.println("\n %%% end");
	}

	public void lengthValue(float v) throws ParseException {
	    System.out.print(v);
	}

	public void em() throws ParseException {
	    System.out.print("em ");
	}

	public void ex() throws ParseException {
	    System.out.print("ex ");
	}

	public void in() throws ParseException {
	    System.out.print("in ");
	}

	public void cm() throws ParseException {
	    System.out.print("cm ");
	}

	public void mm() throws ParseException {
	    System.out.print("mm ");
	}

	public void pc() throws ParseException {
	    System.out.print("pc ");
	}

	public void pt() throws ParseException {
	    System.out.print("pt ");
	}

	public void px() throws ParseException {
	    System.out.print("px ");
	}

	public void percentage() throws ParseException {
	    System.out.print("% ");
	}
    }

    static class TestLengthListHandler
	extends TestLengthHandler
	implements LengthListHandler {
	public void startLengthList() throws ParseException {
	    System.out.println(" %%% start list");
	}

	public void endLengthList() throws ParseException {
	    System.out.println("\n %%% end list");
	}
    }
}
