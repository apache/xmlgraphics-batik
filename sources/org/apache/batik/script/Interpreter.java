/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script;

import java.io.Reader;
import java.io.Writer;
import java.io.IOException;

/**
 * An hight level interface that represents an interpreter engine of
 * a particular scripting language.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public interface Interpreter extends org.apache.batik.i18n.Localizable {
    /**
     * This method should evaluate a piece of script.
     * @param scriptreader a <code>java.io.Reader</code> on the piece of script
     * @return if no exception is thrown during the call, should return the
     * value of the last expression evaluated in the script
     */
    public Object evaluate(Reader scriptreader)
        throws InterpreterException, IOException;
    /**
     * This method should register a particular Java <code>Object</code> in
     * the environment of the interpreter.
     * @param name the name of the script object to create
     * @param object the Java object
     */
    public void bindObject(String name, Object object);
    /**
     * This method should change the output <code>Writer</code> that will be
     * used when output function of the scripting langage is used.
     * @param output the new out <code>Writer</code>.
     */
    public void setOut(Writer output);
    /**
     * This method can dispose resources used by the interpreter when it is
     * no longer used. Be careful, you SHOULD NOT use this interpreter after
     * calling this method.
     */
    public void dispose();
}
