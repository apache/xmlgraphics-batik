/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Simple GUI tool to run a <tt>Test</tt>. This tool takes
 * a class name parameter as an input and provides a GUI to 
 * run an instance of the test. The generated <tt>TestReport</tt>
 * is printed to the standard output with the 
 * <tt>SimpleTestReportProcessor</tt>
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class SimpleTestRunner {
    /**
     * Error Messages. 
     */
    public static final String ERROR_CLASS_CAST =
        "Messages.SimpleTestRuner.error.class.cast";

    public static final String ERROR_CLASS_NOT_FOUND =
        "Messages.SimpleTestRuner.error.class.not.found";

    public static final String ERROR_INSTANTIATION =
        "Messages.SimpleTestRunner.error.instantiation";

    public static final String ERROR_ILLEGAL_ACCESS = 
        "Messages.SimpleTestRunner.error.illegal.access";

    /**
     * Usage for this tool
     */
    public static final String USAGE 
        = "Messages.SimpleTestRunner.usage";

    public static void main(String args[]) throws Exception{
        if(args.length < 1){
            System.err.println(Messages.formatMessage(USAGE, null));
            System.exit(0);
        }

        String className = args[0];

        Class cl = null;

        try{
            cl = Class.forName(className);
        }catch(ClassNotFoundException e){
            System.err.println(Messages.formatMessage(ERROR_CLASS_NOT_FOUND,
                                                      new Object[]{className,
                                                      e.getClass().getName(),
                                                      e.getMessage()}));
            System.exit(0);
        }

        Test t = null;

        try{
            t = (Test)cl.newInstance();
        }catch(ClassCastException e){
            System.err.println(Messages.formatMessage(ERROR_CLASS_CAST,
                                                      new Object[]{ className,
                                                                    e.getClass().getName(),
                                                                    e.getMessage()
                                                      }));
            System.exit(0);
        }catch(InstantiationException e){
            System.err.println(Messages.formatMessage(ERROR_INSTANTIATION,
                                                      new Object[]{ className,
                                                                    e.getClass().getName(),
                                                                    e.getMessage() } ));
            System.exit(0);
        }catch(IllegalAccessException e){
            System.err.println(Messages.formatMessage(ERROR_ILLEGAL_ACCESS,
                                                      new Object[] { className,
                                                                     e.getClass().getName(),
                                                                     e.getMessage() }));

            System.exit(0);
        }
                               

        //
        // Run test and process report with simple
        // text output.
        //
        TestReport tr = t.run();

        try{
            TestReportProcessor p 
                = new org.apache.batik.test.xml.XMLTestReportProcessor();
            
            p.processReport(tr);
        }catch(TestException e){
            System.out.println(e.getClass().getName());
            System.out.println(e.getMessage());
            Exception source = e.getSourceError();
            if(source != null) {
                System.out.println(source);
                System.out.println(source.getMessage());
                source.printStackTrace();
            }
        }
        System.exit(1);

    }
}
