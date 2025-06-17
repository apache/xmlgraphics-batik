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
package org.apache.batik.transcoder;

/**
 * A default <code>ErrorHandler</code> that throws a
 * <code>TranscoderException</code> when a fatal error occurred and display
 * a message when a warning or an error occurred.
 *
 * @author <a href="mailto:Thierry.Kormann@sophia.inria.fr">Thierry Kormann</a>
 * @version $Id$
 */
public class DefaultErrorHandler implements ErrorHandler {

    /**
     * Invoked when an error occurred while transcoding.
     * @param ex the error informations encapsulated in a TranscoderException
     * @exception TranscoderException if the method want to forward
     * the exception
     */
    public void error(TranscoderException ex) throws TranscoderException {
        System.err.println("ERROR: "+ex.getMessage());
    }

    /**
     * Invoked when an fatal error occurred while transcoding.
     * @param ex the fatal error informations encapsulated in a
     * TranscoderException
     * @exception TranscoderException if the method want to forward
     * the exception
     */
    public void fatalError(TranscoderException ex) throws TranscoderException {
        throw ex;
    }

    /**
     * Invoked when a warning occurred while transcoding.
     * @param ex the warning informations encapsulated in a TranscoderException
     * @exception TranscoderException if the method want to forward
     * the exception
     */
    public void warning(TranscoderException ex) throws TranscoderException {
        System.err.println("WARNING: "+ex.getMessage());
    }
}
