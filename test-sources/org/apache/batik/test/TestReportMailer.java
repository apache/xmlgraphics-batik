/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;

import java.util.Properties;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A simple implementation of the <tt>TestReportProcessor</tt> interface
 * that can email the details about failed test reports to 
 * their respective owners.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class TestReportMailer implements TestReportProcessor {
    /**
     * System property used to specify the SMTP server
     */
    public static final String SYSTEM_PROPERTY_SMTP_HOST 
        = "mail.smtp.host";

    /**
     * Email title message:
     * {0} TestSuite name.
     */
    public static final String EMAIL_REPORT_SUBJECT 
        = "TestReportMailer.messages.email.report.subject";

    /**
     * Address used as the sender's address in the out-going message.
     */
    private String reportSourceAddress = null;

    /**
     * SMTP host email address
     */
    private String mailServer = null;

    /**
     * Destination email address
     */
    private String reportDestinationAddress;

    /**
     * Constructor
     */
    public TestReportMailer(String reportSourceAddress,
                            String reportDestinationAddress,
                            String mailServer){
        this.reportSourceAddress = reportSourceAddress;
        this.reportDestinationAddress = reportDestinationAddress;
        this.mailServer = mailServer;
    }

    /**
     * Recursively prints out the entries of the input 
     * report and its children reports, if any.
     */
    public void processReport(TestReport report)
        throws TestException {
        try{
            StringWriter sw = new StringWriter();
            SimpleTestReportProcessor stp 
                = new SimpleTestReportProcessor();
            PrintWriter pw = new PrintWriter(sw);
            stp.setPrintWriter(pw);
            
            stp.processReport(report);

            /**
             * Email result
             */
            InternetAddress from 
                = new InternetAddress(reportSourceAddress);
            
            Properties props = System.getProperties();
            props.put(SYSTEM_PROPERTY_SMTP_HOST, mailServer);
            
            Session session = Session.getDefaultInstance(props, 
                                                         null);
            session.setDebug(true);
            
            MimeMessage msg = new MimeMessage(session);
            InternetAddress[] dest 
                = {new InternetAddress(reportDestinationAddress)};
            msg.setRecipients(Message.RecipientType.TO, dest);

            String mailReportSubject 
                = Messages.formatMessage(EMAIL_REPORT_SUBJECT,
                                         new Object[]{report.getTest().getName()});

            msg.setSubject(mailReportSubject);
            
            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setText(sw.toString());

            System.out.println("report : " + sw.toString());
            
            MimeMultipart mp = new MimeMultipart();
            mp.addBodyPart(mbp);

            msg.setContent(mp);
            
            // Send message
            Transport.send(msg);
        }catch(Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            throw new TestException(INTERNAL_ERROR,
                                    new Object[] { e.getClass().getName(),
                                                   e.getMessage(),
                                                   sw.toString() },
                                    e);
        }
    }


}


