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
 * To test the PreserveAspectRatio parser.
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class PreserveAspectRatioParserTest {
    /**
     * The application main method.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) throws Exception {
	PreserveAspectRatioParser parser =
            new PreserveAspectRatioParser();
	parser.setPreserveAspectRatioHandler
            (new TestPreserveAspectRatioHandler());

	parser.parse(new StringReader("none"));
	parser.parse(new StringReader("xMinYMax"));
	parser.parse(new StringReader("xMidYMin slice"));
	parser.parse(new StringReader("xMidYMid  meet"));
	parser.parse(new StringReader(" xMinYMin meet "));

        System.out.println(" ***** TEST OK *****");
    }

    public static class TestPreserveAspectRatioHandler
	implements PreserveAspectRatioHandler {
	public void startPreserveAspectRatio() throws ParseException {
	    System.out.println(" %%% Start");
	}
    
	public void none() throws ParseException {
	    System.out.println("none");
	}

	public void xMaxYMax() throws ParseException {
	    System.out.println("xMaxYMax");
	}

	public void xMaxYMid() throws ParseException {
	    System.out.println("xMaxYMid");
	}

	public void xMaxYMin() throws ParseException {
	    System.out.println("xMaxYMin");
	}

	public void xMidYMax() throws ParseException {
	    System.out.println("xMidYMax");
	}

	public void xMidYMid() throws ParseException {
	    System.out.println("xMidYMid");
	}

	public void xMidYMin() throws ParseException {
	    System.out.println("xMidYMin");
	}

	public void xMinYMax() throws ParseException {
	    System.out.println("xMinYMax");
	}

	public void xMinYMid() throws ParseException {
	    System.out.println("xMinYMid");
	}

	public void xMinYMin() throws ParseException {
	    System.out.println("xMinYMin");
	}

	public void meet() throws ParseException {
	    System.out.println("meet");
	}

	public void slice() throws ParseException {
	    System.out.println("slice");
	}

	public void endPreserveAspectRatio() throws ParseException {
	    System.out.println(" %%% End");
	}
    }
}
