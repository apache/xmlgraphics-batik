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

import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.XMLResourceDescriptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.svg.SVGDocument;

import static org.apache.batik.util.SVGConstants.SVG_SCRIPT_TYPE_JAVA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseScriptingEnvironmentTestCase {
    private static final String SVG = "<svg xmlns='http://www.w3.org/2000/svg' width='10' height='10'/>";
    private static final int[] REDIRECT_CODES = {300, 301, 302, 303, 305, 307, 308};

    private HttpServer origin;
    private HttpServer other;
    private final AtomicInteger otherHits = new AtomicInteger();
    private ScriptPolicyUserAgent userAgent;
    private BaseScriptingEnvironment scriptEnv;

    @Before
    public void setUp() throws IOException {
        InetAddress loopback = InetAddress.getByName("127.0.0.1");
        other = HttpServer.create(new InetSocketAddress(loopback, 0), 0);
        other.createContext("/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                otherHits.incrementAndGet();
                respond(exchange, 200, null);
            }
        });
        other.start();

        origin = HttpServer.create(new InetSocketAddress(loopback, 0), 0);
        origin.createContext("/script.jar", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                respond(exchange, 200, null);
            }
        });
        // /same/<code> and /cross/<code> answer with the given status code
        // and a Location on the same origin or on the other server.
        origin.createContext("/same/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                respond(exchange, statusCode(exchange), "/script.jar");
            }
        });
        origin.createContext("/cross/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                respond(exchange, statusCode(exchange), otherUrl("/evil.jar"));
            }
        });
        origin.createContext("/loop", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                respond(exchange, 302, "/loop");
            }
        });
        origin.start();

        userAgent = new ScriptPolicyUserAgent(other.getAddress().getPort());
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
        SVGDocument doc = factory.createSVGDocument(originUrl("/doc.svg"), new StringReader(SVG));
        BridgeContext mockContext = mock(BridgeContext.class);
        when(mockContext.getUserAgent()).thenReturn(userAgent);
        when(mockContext.getDocument()).thenReturn(doc);
        scriptEnv = new BaseScriptingEnvironment(mockContext);
    }

    @After
    public void tearDown() {
        origin.stop(0);
        other.stop(0);
    }

    private String originUrl(String path) {
        return "http://127.0.0.1:" + origin.getAddress().getPort() + path;
    }

    private String otherUrl(String path) {
        return "http://127.0.0.1:" + other.getAddress().getPort() + path;
    }

    private static int statusCode(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        return Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
    }

    private static void respond(HttpExchange exchange, int code, String location) throws IOException {
        if (location != null) {
            exchange.getResponseHeaders().set("Location", location);
        }
        exchange.sendResponseHeaders(code, -1);
        exchange.close();
    }

    @Test
    public void testNoRedirectDoesNotCheckPolicy() throws IOException {
        scriptEnv.checkScriptRedirects(SVG_SCRIPT_TYPE_JAVA, new URL(originUrl("/script.jar")));
        assertEquals("A response without redirect should not check the policy",
                Collections.emptyList(), userAgent.checked);
    }

    @Test
    public void testAllowedRedirectTargetIsChecked() throws IOException {
        for (int code : REDIRECT_CODES) {
            userAgent.checked.clear();
            scriptEnv.checkScriptRedirects(SVG_SCRIPT_TYPE_JAVA, new URL(originUrl("/same/" + code)));
            assertEquals("Target of the " + code + " redirect should be checked",
                    Collections.singletonList(originUrl("/script.jar")), userAgent.checked);
        }
    }

    @Test
    public void testRedirectRejectedByPolicy() throws IOException {
        for (int code : REDIRECT_CODES) {
            userAgent.checked.clear();
            final URL url = new URL(originUrl("/cross/" + code));
            assertThrows("Target of the " + code + " redirect rejected by the script policy must fail",
                    SecurityException.class,
                    () -> scriptEnv.checkScriptRedirects(SVG_SCRIPT_TYPE_JAVA, url));
            assertEquals("Target of the " + code + " redirect should be checked",
                    Collections.singletonList(otherUrl("/evil.jar")), userAgent.checked);
            assertEquals("Rejected target of the " + code + " redirect must not be contacted",
                    0, otherHits.get());
        }
    }

    @Test
    public void testNotModifiedIsNotARedirect() throws IOException {
        scriptEnv.checkScriptRedirects(SVG_SCRIPT_TYPE_JAVA, new URL(originUrl("/cross/304")));
        assertEquals("304 Not Modified is not a redirect and should not check the policy",
                Collections.emptyList(), userAgent.checked);
    }

    @Test
    public void testTooManyRedirects() {
        IOException e = assertThrows("Redirect loop must not be followed forever",
                IOException.class,
                () -> scriptEnv.checkScriptRedirects(SVG_SCRIPT_TYPE_JAVA, new URL(originUrl("/loop"))));
        assertTrue("Unexpected exception for redirect loop: " + e.getMessage(),
                e.getMessage().contains("Too many redirects"));
        assertEquals("Every redirect up to the limit should be checked",
                BaseScriptingEnvironment.MAX_SCRIPT_REDIRECTS, userAgent.checked.size());
    }

    @Test
    public void testNonHttpUrlIsIgnored() throws IOException {
        scriptEnv.checkScriptRedirects(SVG_SCRIPT_TYPE_JAVA, new URL("file:/nonexistent/script.jar"));
        assertEquals("A non HTTP URL should not check the policy",
                Collections.emptyList(), userAgent.checked);
    }

    private static class ScriptPolicyUserAgent extends UserAgentAdapter {
        final List<String> checked = new CopyOnWriteArrayList<>();
        private final int rejectedPort;

        ScriptPolicyUserAgent(int rejectedPort) {
            this.rejectedPort = rejectedPort;
        }

        @Override
        public void checkLoadScript(String scriptType, ParsedURL scriptURL, ParsedURL docURL) {
            checked.add(scriptURL.toString());
            if (scriptURL.getPort() == rejectedPort) {
                throw new SecurityException("Script from " + scriptURL + " is not allowed");
            }
        }
    }
}
