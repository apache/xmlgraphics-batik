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
