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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    private RunnableQueue rq;

    @Before
    public void setUp() {
        rq = RunnableQueue.createRunnableQueue();
        rq.resumeExecution();
    }

    @After
    public void tearDown() {
        if (rq != null && rq.getThread() != null) {
            rq.getThread().halt();
        }
    }

    private ScriptingEnvironment createEnvironment(RecordingUserAgent ua) throws IOException {
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(
                XMLResourceDescriptor.getXMLParserClassName());
        SVGDocument doc = f.createSVGDocument(DOC_URL, new StringReader(SVG));

        UpdateManager um = mock(UpdateManager.class);
        when(um.getUpdateRunnableQueue()).thenReturn(rq);

        BridgeContext ctx = mock(BridgeContext.class);
        when(ctx.getUserAgent()).thenReturn(ua);
        when(ctx.getDocument()).thenReturn(doc);
        when(ctx.getUpdateManager()).thenReturn(um);

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
        RecordingUserAgent ua = new RecordingUserAgent();
        ScriptingEnvironment se = createEnvironment(ua);

        Interpreter mockInterpreter = mock(Interpreter.class);
        Window window = se.getWindow(mockInterpreter, "text/ecmascript");

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
        assertTrue("checkLoadExternalResource must be called", ua.checked);
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
        volatile boolean checked;
        volatile ParsedURL checkedResource;

        @Override
        public void checkLoadExternalResource(ParsedURL resourceURL, ParsedURL docURL) throws SecurityException {
            checked = true;
            checkedResource = resourceURL;
            super.checkLoadExternalResource(resourceURL, docURL);
        }
    }
}
