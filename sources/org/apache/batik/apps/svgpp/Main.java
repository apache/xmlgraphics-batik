/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.apps.svgpp;

import java.util.HashMap;
import java.util.Map;

import org.apache.batik.i18n.LocalizableSupport;

/**
 * This class is the main class of the svgpp application.
 * <p>
 * svgpp is a pretty-printer for SVG source files.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class Main {

    /**
     * The application main method.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        new Main().run(args);
    }

    /**
     * The default resource bundle base name.
     */
    public final static String BUNDLE_CLASSNAME =
	"org.apache.batik.apps.svgpp.resources.Messages";

    /**
     * The localizable support.
     */
    protected LocalizableSupport localizableSupport =
        new LocalizableSupport(BUNDLE_CLASSNAME);

    /**
     * Runs the pretty printer.
     * @param args The command-line arguments.
     */
    public void run(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }
    }

    /**
     * Prints the command usage.
     */
    protected void printUsage() {
        printHeader();
        System.out.println(localizableSupport.formatMessage("syntax", null));
        System.out.println(localizableSupport.formatMessage("options", null));
    }

    /**
     * Prints the command header.
     */
    protected void printHeader() {
        System.out.println(localizableSupport.formatMessage("header", null));
    }
}
