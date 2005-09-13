/*

   Copyright 2005  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.bridge.svg12;

import org.apache.batik.bridge.SVGTextElementBridge;
import org.apache.batik.dom.xbl.ShadowTreeEvent;

import org.w3c.dom.Element;

/**
 * Bridge class for SVG 'text' elements with support for text content
 * that has been specified with XBL.
 *
 * @author <a href="mailto:cam%40mcc%2eid%2eau">Cameron McCormack</a>
 * @version $Id$
 */
public class SVG12TextElementBridge
        extends SVGTextElementBridge
        implements SVG12BridgeUpdateHandler {

    // SVG12BridgeUpdateHandler //////////////////////////////////////////////

    /**
     * Invoked when a bindable element's binding has changed.
     */
    public void handleBindingEvent(Element bindableElement,
                                   Element shadowTree) {
    }
}
