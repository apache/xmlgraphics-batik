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

package org.apache.batik.script;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * An hight level interface that represents an interpreter engine of
 * a particular scripting language.
 *
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public interface Interpreter extends org.apache.batik.i18n.Localizable {
    /**
     * This method should evaluate a piece of script associated to a given 
     * description.
     *
     * @param scriptreader a <code>java.io.Reader</code> on the piece of script
     * @param description description which can be later used (e.g., for error 
     *        messages).
     * @return if no exception is thrown during the call, should return the
     * value of the last expression evaluated in the script
     */
    public Object evaluate(Reader scriptreader, String description)
        throws InterpreterException, IOException;

    /**
     * This method should evaluate a piece of script.
     *
     * @param scriptreader a <code>java.io.Reader</code> on the piece of script
     * @return if no exception is thrown during the call, should return the
     * value of the last expression evaluated in the script
     */
    public Object evaluate(Reader scriptreader)
        throws InterpreterException, IOException;

    /**
     * This method should evaluate a piece of script using a <code>String</code>
     * instead of a <code>Reader</code>. This usually allows do easily do some
     * caching.
     *
     * @param script the piece of script
     * @return if no exception is thrown during the call, should return the
     * value of the last expression evaluated in the script
     */
    public Object evaluate(String script)
        throws InterpreterException;

    /**
     * This method should register a particular Java <code>Object</code> in
     * the environment of the interpreter.
     *
     * @param name the name of the script object to create
     * @param object the Java object
     */
    public void bindObject(String name, Object object);

    /**
     * This method should change the output <code>Writer</code> that will be
     * used when output function of the scripting langage is used.
     *
     * @param output the new out <code>Writer</code>.
     */
    public void setOut(Writer output);

    /**
     * This method can dispose resources used by the interpreter when it is
     * no longer used. Be careful, you SHOULD NOT use this interpreter instance
     * after calling this method.
     */
    public void dispose();
}
