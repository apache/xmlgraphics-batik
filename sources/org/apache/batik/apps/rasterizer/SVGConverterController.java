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

package org.apache.batik.apps.rasterizer;

import java.io.File;
import java.util.Map;
import java.util.Vector;

import org.apache.batik.transcoder.Transcoder;

/**
 * Interface for controlling some aspectes of the 
 * <tt>SVGConverter</tt> operation.
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public interface SVGConverterController {
    /**
     * Invoked when the rasterizer has computed the 
     * exact description of what it should do. The controller 
     * should return true if the transcoding process should 
     * proceed or false otherwise.
     *
     * @param transcoder Transcoder which will be used 
     * @param hints set of hints that were set on the transcoder
     * @param sources list of SVG sources it will convert.
     * @param dest list of destination file it will use
     */
    public boolean proceedWithComputedTask(Transcoder transcoder,
                                           Map hints,
                                           Vector sources,
                                           Vector dest);

    /**
     * Invoked when the rasterizer is about to start transcoding
     * of a given source.
     * The controller should return true if the source should be
     * transcoded and false otherwise.
     */
    public boolean proceedWithSourceTranscoding(SVGConverterSource source,
                                                File dest);
        
    /**
     * Invoked when the rasterizer got an error while
     * transcoding the input source. 
     * The controller should return true if the transcoding process
     * should continue on other sources and it should return false
     * if it should not.
     *
     * @param errorCode see the {@link SVGConverter} error code descriptions.
     */
    public boolean proceedOnSourceTranscodingFailure(SVGConverterSource source,
                                                     File dest,
                                                     String errorCode);

    /**
     * Invoked when the rasterizer successfully transcoded
     * the input source.
     */
    public void onSourceTranscodingSuccess(SVGConverterSource source,
                                           File dest);

}

