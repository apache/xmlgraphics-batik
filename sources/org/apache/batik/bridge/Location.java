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
package org.apache.batik.bridge;

import java.net.URL;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.AbstractDocument;

/**
 * This class implements the org.w3c.dom.Location interface for Batik
 *
 * @author <a href="mailto:gwadej@anomaly.org">G. Wade Johnson</a>
 * @version $Id: Location.java$
 */
public class Location implements org.w3c.dom.Location {
    private BridgeContext bridgeContext;

    /**
     * Creates a new Location.
     * @param ctx the bridge context
     */
    public Location(BridgeContext ctx) {
        bridgeContext = ctx;
    }


    /**
     * Invocation of this method causes the user agent to navigate to the
     * supplied location.
     *
     * @param url A string containing the URL where the user agent should
     *    navigate to.
     */
    public void assign(String url) {
        ((UserAgent)bridgeContext.getUserAgent()).loadDocument(url);
    }

    /**
     * The user agent reloads the current document.
     */
    public void reload() {
        String url = ((AbstractDocument) bridgeContext.getDocument())
                    .getDocumentURI();
        ((UserAgent)bridgeContext.getUserAgent()).loadDocument(url);
    }

    /**
     * Returns the URL of this location as a String.
     */
    public String toString() {
        return ((AbstractDocument) bridgeContext.getDocument())
                    .getDocumentURI();
    }
}

