/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.script;

import org.w3c.dom.Document;

/**
 * An interface allowing to create an <code>Interpreter</code> corresponding
 * to a particular language and for a particular <code>Document</code>.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
public interface InterpreterPool {

    /**
     * Should return a unique instance of an implementation of <code>Interpreter
     * </code> interface that match the given language and that provides
     * a "document" script object binding the given <code>Document</code>
     * instance.
     * @param language a mimeType like string describing the language to use
     * (i.e. "text/ecmascript" for ECMAScript interpreter).
     * @param document the <code>Document instance</code>.
     */
    public Interpreter getInterpreter(String language, Document document);

    /**
     * Should allow to register an <code>InterpreterFactory</code> for the given
     * language.
     * @param language the language for which the factory is registered.
     * @param factory the <code>InterpreterFactory</code> that will allow to
     * create a interpreter for the language.
     */
    public void putInterpreterFactory(String language,
                                      InterpreterFactory factory);

    /**
     * Should allow to unregister the <code>InterpreterFactory</code> of the
     * given language.
     * @param language the language for which the factory should be
     * unregistered.
     */
    public void removeInterpreterFactory(String language);
}
