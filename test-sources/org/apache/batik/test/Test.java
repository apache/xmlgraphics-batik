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

/**
 * Defines the interface of a <tt>Test</tt> case. It is
 * highly recommended that implementations derive from the
 * <tt>AbstractTest</tt> class or follow the same implementation
 * approach, so that no exception is thrown from the 
 * <tt>run</tt> method, which is critical for the operation
 * of the test infrastructure.
 *
 * @author <a href="mailto:vhardy@apache.lorg">Vincent Hardy</a>
 * @version $Id$
 */
public interface Test {
    /**
     * Returns this <tt>Test</tt>'s name. 
     */
    public String getName();

    /**
     * Returns the <tt>Test</tt>'s qualified id, that is,
     * the string made of all the id's parents separated 
     * by ".". For example, if this test's id is "C", 
     * its parent id is "B" and its grand-parent id is 
     * "A", this method should return "A.B.C".
     */
    public String getQualifiedId();

    /**
     * Returns the <tt>Test</tt>'s id. The notion of 
     * identifier is left to the user of the <tt>Test</tt>
     * object, which explains why the user may set the
     * id.
     */
    public String getId();

    /**
     * Sets this <tt>Test</tt>'s id.
     */
    public void setId(String id);

    /**
     * Requests this <tt>Test</tt> to run and produce a 
     * report. It is critical for the test infrastructure
     * that implementations never throw exceptions 
     * from the run method, even if an error occurs internally
     * in the test. 
     *
     */
    public TestReport run();

    /**
     * Returns this <tt>Test</tt>'s parent, in case this 
     * <tt>Test</tt> is part of a <tt>TestSuite</tt>.
     * The returned value may be null.
     */
    public TestSuite getParent();

    /**
     * Set this <tt>Test</tt>'s parent.
     */
    public void setParent(TestSuite parent);
}
