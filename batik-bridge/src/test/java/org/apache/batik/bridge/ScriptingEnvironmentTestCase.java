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
import java.io.OutputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.script.Interpreter;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.RunnableQueue;
import org.apache.batik.util.XMLResourceDescriptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.svg.SVGDocument;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScriptingEnvironmentTestCase {

    private static final String DOC_URL = "http://example.com/doc.svg";
    private static final String EVIL_URL = "file://etc/passwd";
    private static final String SVG = "<svg xmlns='http://www.w3.org/2000/svg' width='10' height='10'/>";

    private RunnableQueue runnableQueue;

    @Before
    public void setUp() {
        runnableQueue = RunnableQueue.createRunnableQueue();
        runnableQueue.resumeExecution();
    }

    @After
    public void tearDown() {
        if (runnableQueue != null && runnableQueue.getThread() != null) {
            runnableQueue.getThread().halt();
        }
    }

    private ScriptingEnvironment createEnvironment(RecordingUserAgent userAgent) throws IOException {
        return createEnvironment(userAgent, DOC_URL);
    }

    private ScriptingEnvironment createEnvironment(RecordingUserAgent userAgent, String docUrl) throws IOException {
        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName());
        SVGDocument doc = factory.createSVGDocument(docUrl, new StringReader(SVG));

        UpdateManager updateManager = mock(UpdateManager.class);
        when(updateManager.getUpdateRunnableQueue()).thenReturn(runnableQueue);

        BridgeContext ctx = mock(BridgeContext.class);
        when(ctx.getUserAgent()).thenReturn(userAgent);
        when(ctx.getDocument()).thenReturn(doc);
        when(ctx.getUpdateManager()).thenReturn(updateManager);

        return new ScriptingEnvironment(ctx);
    }

    @Test
    public void testGetURLBlocksCrossOriginRequest() throws Exception {
        RecordingUserAgent userAgent = new RecordingUserAgent();
        ScriptingEnvironment scriptEnv = createEnvironment(userAgent);

        Interpreter mockInterpreter = mock(Interpreter.class);
        Window window = scriptEnv.getWindow(mockInterpreter, "text/ecmascript");

        final CountDownLatch done = new CountDownLatch(1);
        URLDoneInfo info = new URLDoneInfo();

        window.getURL(EVIL_URL,
                new Window.URLResponseHandler() {
                    public void getURLDone(boolean success, String mime, String content) {
                        info.setSuccess(success);
                        info.setContent(content);
                        done.countDown();
                    }
                });

        assertTrue("getURL callback was never invoked", done.await(10, TimeUnit.SECONDS));
        assertTrue("checkLoadExternalResource must be called", userAgent.checked);
        assertEquals(EVIL_URL, userAgent.checkedResource == null ? null : userAgent.checkedResource.toString());
        assertFalse("cross-origin getURL() must not succeed", info.success);
        assertNull("no content should be returned for a blocked request", info.content);
    }

    @Test
    public void testGetURLAllowsSameOriginResource() throws Exception {
        RecordingUserAgent userAgent = new RecordingUserAgent();
        ScriptingEnvironment scriptingEnvironment = createEnvironment(userAgent);

        Interpreter mockInterpreter = mock(Interpreter.class);
        Window window = scriptingEnvironment.getWindow(mockInterpreter, "text/ecmascript");

        final CountDownLatch done = new CountDownLatch(1);
        URLDoneInfo info = new URLDoneInfo();

        // data: URIs are always permitted by DefaultExternalResourceSecurity.
        window.getURL("data:text/plain,hello",
                new Window.URLResponseHandler() {
                    public void getURLDone(boolean success, String mime, String content) {
                        info.setSuccess(success);
                        info.setContent(content);
                        done.countDown();
                    }
                });

        assertTrue("getURL callback was never invoked", done.await(10, TimeUnit.SECONDS));
        assertTrue("checkLoadExternalResource must be called", userAgent.checked);
        assertTrue("same-origin/data getURL() should succeed", info.success);
        assertEquals("data: resource content should be returned to the callback", "hello", info.content);
    }

    @Test
    public void testPostURLBlocksCrossOriginRequest() throws Exception {
        RecordingUserAgent userAgent = new RecordingUserAgent();
        ScriptingEnvironment scriptEnv = createEnvironment(userAgent);

        Interpreter mockInterpreter = mock(Interpreter.class);
        Window window = scriptEnv.getWindow(mockInterpreter, "text/ecmascript");

        final CountDownLatch done = new CountDownLatch(1);
        URLDoneInfo info = new URLDoneInfo();

        window.postURL(EVIL_URL, "attacker-controlled-body",
                new Window.URLResponseHandler() {
                    public void getURLDone(boolean success, String mime, String content) {
                        info.setSuccess(success);
                        info.setContent(content);
                        done.countDown();
                    }
                },
                "application/x-malicious", null);

        assertTrue("postURL callback was never invoked", done.await(10, TimeUnit.SECONDS));
        assertTrue("checkLoadExternalResource must be called", userAgent.checked);
        assertEquals(EVIL_URL, userAgent.checkedResource == null ? null : userAgent.checkedResource.toString());
        assertFalse("cross-origin postURL() must not succeed", info.success);
        assertNull("no content should be returned for a blocked request", info.content);
    }

    @Test
    public void testPostURLDoesNotReportRedirectAsSuccess() throws Exception {
        final AtomicInteger resultHits = new AtomicInteger();
        HttpServer server = HttpServer.create(
                new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0), 0);
        // /submit/<code> answers with the given redirect status code.
        server.createContext("/submit/", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                exchange.getRequestBody().close();
                String path = exchange.getRequestURI().getPath();
                int code = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
                exchange.getResponseHeaders().set("Location", "/result");
                exchange.sendResponseHeaders(code, -1);
                exchange.close();
            }
        });
        server.createContext("/result", new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                resultHits.incrementAndGet();
                byte[] body = "result".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, body.length);
                OutputStream outputStream = exchange.getResponseBody();
                outputStream.write(body);
                outputStream.close();
            }
        });
        server.start();
        try {
            String base = "http://127.0.0.1:" + server.getAddress().getPort();
            RecordingUserAgent userAgent = new RecordingUserAgent();
            ScriptingEnvironment scriptEnv = createEnvironment(userAgent, base + "/doc.svg");

            Interpreter mockInterpreter = mock(Interpreter.class);
            Window window = scriptEnv.getWindow(mockInterpreter, "text/ecmascript");

            for (int code : new int[] {300, 301, 302, 303, 305, 307, 308}) {
                final CountDownLatch done = new CountDownLatch(1);
                URLDoneInfo info = new URLDoneInfo();

                window.postURL(base + "/submit/" + code, "body",
                        new Window.URLResponseHandler() {
                            public void getURLDone(boolean success, String mime, String content) {
                                info.setSuccess(success);
                                info.setContent(content);
                                done.countDown();
                            }
                        },
                        "text/plain", null);

                assertTrue("postURL callback was never invoked for " + code,
                        done.await(10, TimeUnit.SECONDS));
                assertFalse("a " + code + " response must not be reported as success", info.success);
                assertNull("no content should be returned for a " + code + " response", info.content);
            }
            assertTrue("checkLoadExternalResource must be called", userAgent.checked);
            assertEquals("redirect target must not be requested", 0, resultHits.get());
        } finally {
            server.stop(0);
        }
    }

    private static class URLDoneInfo {
        boolean success;
        String content;

        public void setSuccess(boolean success) {
            this.success = success;
        }
        public void setContent(String content) {
            this.content = content;
        }
    }

    private static class RecordingUserAgent extends UserAgentAdapter {
        private boolean checked;
        private ParsedURL checkedResource;

        @Override
        public void checkLoadExternalResource(ParsedURL resourceURL, ParsedURL docURL) throws SecurityException {
            checked = true;
            checkedResource = resourceURL;
            super.checkLoadExternalResource(resourceURL, docURL);
        }
    }
}
