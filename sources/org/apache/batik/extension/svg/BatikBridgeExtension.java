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

package org.apache.batik.extension.svg;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeExtension;
import org.w3c.dom.Element;

/**
 * This is a Service interface for classes that want to extend the
 * functionality of the Bridge, to support new tags in the rendering tree.
 */
public class BatikBridgeExtension implements BridgeExtension {

    /**
     * Return the priority of this Extension.  Extensions are
     * registered from lowest to highest priority.  So if for some
     * reason you need to come before/after another existing extension
     * make sure your priority is lower/higher than theirs.  
     */
    public float getPriority() { return 1f; }

    /**
     * This should return the list of extensions implemented
     * by this BridgeExtension.
     * @return An iterator containing strings one for each implemented
     *         extension.
     */
    public Iterator getImplementedExtensions() {
        String [] extensions = {
            "http://xml.apache.org/batik/ext/poly/1.0" ,
            "http://xml.apache.org/batik/ext/star/1.0" ,
            "http://xml.apache.org/batik/ext/histogramNormalization/1.0" ,
            "http://xml.apache.org/batik/ext/solidColor/1.0" ,
            "http://xml.apache.org/batik/ext/colorSwitch/1.0" ,
            "http://xml.apache.org/batik/ext/flowText/1.0" ,
            "http://xml.apache.org/batik/ext/multi-image/1.0" ,
        };
        Vector v = new Vector(extensions.length);
        for (int i=0; i<extensions.length; i++) {
            v.add(extensions[i]);
        }
        return Collections.unmodifiableList(v).iterator();
    }

    /**
     * This should return the individual or company name responsible
     * for the this implementation of the extension.
     */
    public String getAuthor() {
        return "Thomas DeWeese";
    }

    /**
     * This should contain a contact address (usually an e-mail address).
     */
    public String getContactAddress() {
        return "deweese@apache.org";
    }

    /**
     * This should return a URL where information can be obtained on
     * this extension.
     */
    public String getURL() {
        return "http://xml.apache.org/batik";
    }

    /**
     * Human readable description of the extension.
     * Perhaps that should be a resource for internationalization?
     * (although I suppose it could be done internally)
     */
    public String getDescription() {
        return "Example extension to standard SVG shape tags";
    }

    /**
     * This method should update the BridgeContext with support
     * for the tags in this extension.  In some rare cases it may
     * be necessary to replace existing tag handlers, although this
     * is discouraged.
     *
     * @param ctx The BridgeContext instance to be updated
     */
    public void registerTags(BridgeContext ctx) {
        ctx.putBridge(new BatikRegularPolygonElementBridge());
        ctx.putBridge(new BatikStarElementBridge());
        ctx.putBridge(new BatikHistogramNormalizationElementBridge());
        ctx.putBridge(new BatikMultiImageElementBridge());
        ctx.putBridge(new SolidColorBridge());
        ctx.putBridge(new ColorSwitchBridge());
        ctx.putBridge(new SVGFlowTextElementBridge());
    }

    /**
     * Whether the presence of the specified element should cause
     * the document to be dynamic.  If this element isn't handled
     * by this BridgeExtension, just return false.
     *
     * @param e The element to check.
     */
    public boolean isDynamicElement(Element e) {
        return false;
    }
}
