/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;

import org.apache.batik.util.CleanerThread;

/**
 * One line Class Desc
 *
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org>l449433</a>
 * @version $Id$
 */
public abstract class MemoryLeakTest  extends AbstractTest {

    // I know that 60 seems _really_ high but it turns out
    // That the GraphicsNodeTree was not being cleared when I
    // tested with as high as 36.  So I would leave it at 60
    // (why so large I don't know).
    final static int NUM_GC=60;
    final static String ERROR_OBJS_NOT_CLEARED = 
        "MemoryLeakTest.message.error.objs.not.cleared";

    final static String ERROR_DESCRIPTION = 
        "TestReport.entry.key.error.description";
        
    public static String fmt(String key, Object []args) {
        return Messages.formatMessage(key, args);
    }

    public MemoryLeakTest() {
    }
    
    Set objs = new HashSet();

    public void registerObject(Object o) {
        synchronized (objs) {
            objs.add(new WeakRef(o));
        }
    }
    public void registerObjectDesc(Object o, String desc) {
        synchronized (objs) {
            objs.add(new WeakRef(o, desc));
        }
    }
    

    public TestReport runImpl() throws Exception {
        TestReport ret = doSomething();
        if ((ret != null) && !ret.hasPassed())
            return ret;

        for (int i=0; i<NUM_GC; i++) {
            System.gc();
        }

        StringBuffer sb = new StringBuffer();
        int count = 0;
        synchronized (objs) {
            Iterator i = objs.iterator();
            while (i.hasNext()) {
                WeakRef wr = (WeakRef)i.next();
                Object o = wr.get();
                if (o == null) continue;
                if (count != 0)
                    sb.append(",");

                sb.append(wr.getDesc());
                count++;
            }
        }
        
        DefaultTestReport report = new DefaultTestReport(this);
        if (count == 0) {
            report.setPassed(true);
            return report;
        }
        String objStr = sb.toString();

        report.setErrorCode(ERROR_OBJS_NOT_CLEARED);
        report.setDescription(new TestReport.Entry[] { 
            new TestReport.Entry
            (fmt(ERROR_DESCRIPTION, null),
             fmt(ERROR_OBJS_NOT_CLEARED, new Object[]{objStr}))
        });
        if (objStr.length() > 40) 
            objStr = objStr.substring(0,40) + "..." ;
        System.err.print(">>>>> Objects not cleared: " + objStr + "\n");
        report.setPassed(false);
        return report;
    }

    public abstract TestReport doSomething() throws Exception;

    public class WeakRef extends CleanerThread.WeakReferenceCleared {
        String desc;
        public WeakRef(Object o) {
            super(o);
            this.desc = o.toString();
        }
        public WeakRef(Object o, String desc) {
            super(o);
            this.desc = desc;
        }

        public String getDesc() { return desc; }

        public void cleared() {
            synchronized (objs) {
                objs.remove(this);
            }
        }
        
    }
    
};
