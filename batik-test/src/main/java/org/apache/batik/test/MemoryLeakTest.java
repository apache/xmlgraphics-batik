/*

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;

import org.apache.batik.util.CleanerThread;

/**
 * One line Class Desc
 *
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org">l449433</a>
 * @version $Id$
 */
public abstract class MemoryLeakTest  extends AbstractTest {

    // I know that 120 seems _really_ high but it turns out
    // That the "GraphicsNodeTree" was not being cleared when I
    // tested with as high as 60.  So I would leave it at 120
    // (why so large I don't know) - it will bail if the all
    // the objects of interest are collected sooner so the runtime
    // is really only a concern for failures.
    static final int NUM_GC=10;
    static final int MIN_MEMORY=200000; // 200KB
    static final int ALLOC_SZ=1000; // 100KB

    static final String ERROR_OBJS_NOT_CLEARED =
        "MemoryLeakTest.message.error.objs.not.cleared";

    static final String ERROR_DESCRIPTION =
        "TestReport.entry.key.error.description";

    public static String fmt(String key, Object []args) {
        return Messages.formatMessage(key, args);
    }

    public MemoryLeakTest() {
    }

    Map objs = new HashMap();
    List entries = new ArrayList();

    public void registerObject(Object o) {
        synchronized (objs) {
            String desc = o.toString();
            objs.put(desc, new WeakRef(o, desc));
        }
    }
    public void registerObjectDesc(Object o, String desc) {
        synchronized (objs) {
            objs.put(desc, new WeakRef(o, desc));
        }
    }

    public boolean checkObject(String desc) {
        String [] strs = new String[1];
        strs[0] = desc;
        return checkObjects(strs);
    }

    public boolean checkObjects(String [] descs) {
        Runtime rt = Runtime.getRuntime();
        List l = new ArrayList();
        int nBlock = (int)(rt.totalMemory()/(ALLOC_SZ*NUM_GC));
        try {
            while (true) {
                boolean passed = true;
                synchronized (objs) {
                    // System.err.println("FreeMemory: " + rt.freeMemory() +
                    //                    " of " +rt.totalMemory());

                    for (String desc : descs) {
                        WeakRef wr = (WeakRef) objs.get(desc);
                        if ((wr == null) || (wr.get() == null)) continue;
                        passed = false;
                        break;
                    }
                }
                if (passed) return true;

                List l2 = new ArrayList();
                for (int i=0; i<nBlock; i++) {
                    l2.add(new byte[ALLOC_SZ]);
                }
                l.add(l2);
            }
        } catch (OutOfMemoryError oom) {
        } finally {
            l = null;
        }

        for (int i=0; i<NUM_GC; i++)
            rt.gc();

        StringBuffer sb = new StringBuffer();
        boolean passed = true;
        synchronized (objs) {
            for (String desc : descs) {
                WeakRef wr = (WeakRef) objs.get(desc);
                if (wr == null) continue;
                Object o = wr.get();
                if (o == null) continue;
                if (!passed)
                    sb.append(",");
                passed = false;
                sb.append("'");
                sb.append(wr.getDesc());
                sb.append("'");
            }
            if (passed) return true;
        }

        String objStr = sb.toString();
        TestReport.Entry entry = new TestReport.Entry
            (fmt(ERROR_DESCRIPTION, null),
             fmt(ERROR_OBJS_NOT_CLEARED, new Object[]{objStr}));
        entries.add(entry);

        if (objStr.length() > 40)
            objStr = objStr.substring(0,40) + "..." ;
        System.err.println(">>>>> Objects not cleared: " + objStr);
        // System.err.println("Waiting 5 second for heap dump...");
        // try { Thread.sleep(5000); } catch (InterruptedException ie) { }
        return false;
    }

    public boolean checkObjectsList(List descs) {
        String [] strs = new String[descs.size()];
        descs.toArray(strs);
        return checkObjects(strs);
    }

    public boolean checkAllObjects() {
        Runtime rt = Runtime.getRuntime();
        List l = new ArrayList();
        int nBlock = (int)(rt.totalMemory()/(ALLOC_SZ*NUM_GC));
        try {
            while (true) {
                // System.err.println("FreeMemory: " + rt.freeMemory() +
                //                    " of " +rt.totalMemory());

                boolean passed = true;
                synchronized (objs) {
                    for (Object o : objs.values()) {
                        WeakRef wr = (WeakRef) o;
                        if ((wr != null) && (wr.get() != null)) {
                            passed = false;
                            break;
                        }
                    }
                }
                if (passed) return true;

                List l2 = new ArrayList();
                for (int i=0; i<nBlock; i++) {
                    l2.add(new byte[ALLOC_SZ]);
                }
                l.add(l2);
            }
        } catch (OutOfMemoryError oom) {
        } finally {
            l = null;
        }

        for (int i=0; i<NUM_GC; i++)
            rt.gc();


        StringBuffer sb = new StringBuffer();
        synchronized (objs) {
            boolean passed = true;
            for (Object o1 : objs.values()) {
                WeakRef wr = (WeakRef) o1;
                Object o = wr.get();
                if (o == null) continue;
                if (!passed)
                    sb.append(",");
                passed = false;
                sb.append("'");
                sb.append(wr.getDesc());
                sb.append("'");
            }
            if (passed) return true;
        }

        String objStr = sb.toString();

        TestReport.Entry entry = new TestReport.Entry
            (fmt(ERROR_DESCRIPTION, null),
             fmt(ERROR_OBJS_NOT_CLEARED, new Object[]{objStr}));
        entries.add(entry);

        if (objStr.length() > 40)
            objStr = objStr.substring(0,40) + "..." ;
        System.err.println(">>>>> Objects not cleared: " + objStr);
        // System.err.println("Waiting for 5 seconds for heap dump...");
        // try { Thread.sleep(5000); } catch (InterruptedException ie) { }
        return false;
    }

    public TestReport runImpl() throws Exception {
        TestReport ret = doSomething();
        if ((ret != null) && !ret.hasPassed())
            return ret;

        checkAllObjects();

        DefaultTestReport report = new DefaultTestReport(this);
        if (entries.size() == 0) {
            report.setPassed(true);
            return report;
        }
        report.setErrorCode(ERROR_OBJS_NOT_CLEARED);
        report.setDescription
            ((TestReport.Entry[])entries.toArray
             (new TestReport.Entry[entries.size()]));
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
                objs.remove(desc);
            }
        }

    }

}
