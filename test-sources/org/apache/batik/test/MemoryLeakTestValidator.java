/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.test;

import org.apache.batik.test.TestReport;

/**
 * One line Class Desc
 *
 * Complete Class Desc
 *
 * @author <a href="mailto:deweese@apache.org>l449433</a>
 * @version $Id$
 */
public class MemoryLeakTestValidator extends MemoryLeakTest {
    public MemoryLeakTestValidator() {
    }

    Link start;
    public TestReport doSomething() throws Exception {
        for (int i=0; i<20; i++) 
            registerObjectDesc(new Object(), "Obj#"+i);
        for (int i=0; i<10; i++) {
            Pair p1 = new Pair();
            Pair p2 = new Pair();
            p1.mate(p2);
            registerObjectDesc(p2, "Pair#"+i);
        }
        Link p = null;
        for (int i=0; i<10; i++) {
            p = new Link(p);
            registerObjectDesc(p, "Link#"+i);
        }
        // Uncomment this to make the test fail with all the links.
        // start = p;
        return null;
    }
    
    public static class Pair {
        Pair myMate;
        public Pair() { };
        public void mate(Pair p) {
            this.myMate = p;
            p.myMate    = this;
        }
    }

    public static class Link {
        public Link prev;
        public Link(Link prev) {
            this.prev = prev;
        }
    }

};
