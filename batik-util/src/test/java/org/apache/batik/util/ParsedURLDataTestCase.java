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
package org.apache.batik.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class ParsedURLDataTestCase {
    private static final int[] REDIRECT_CODES = {300, 301, 302, 303, 307, 308};

    private HttpServer origin;
    private HttpServer other;
    private final AtomicInteger otherHits = new AtomicInteger();

    @Before
    public void setUp() throws IOException {
        InetAddress loopback = InetAddress.getByName("127.0.0.1");
        other = HttpServer.create(new InetSocketAddress(loopback, 0), 0);
        other.createContext("/", new HttpHandler() {
            public void handle(HttpExchange ex) throws IOException {
                otherHits.incrementAndGet();
                respond(ex, 200, null, "other");
            }
        });
        other.start();

        origin = HttpServer.create(new InetSocketAddress(loopback, 0), 0);
        // /same/<code> and /cross/<code> answer with the given status code
        // and a Location on the same origin or on the other server.
        origin.createContext("/same/", new HttpHandler() {
            public void handle(HttpExchange ex) throws IOException {
                respond(ex, statusCode(ex), "/target", "");
            }
        });
        origin.createContext("/cross/", new HttpHandler() {
            public void handle(HttpExchange ex) throws IOException {
                respond(ex, statusCode(ex), otherUrl("/secret"), "");
            }
        });
        origin.createContext("/target", new HttpHandler() {
            public void handle(HttpExchange ex) throws IOException {
                respond(ex, 200, null, "target");
            }
        });
        origin.createContext("/loop", new HttpHandler() {
            public void handle(HttpExchange ex) throws IOException {
                respond(ex, 302, "/loop", "");
            }
        });
        origin.start();
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

    private static int statusCode(HttpExchange ex) {
        String path = ex.getRequestURI().getPath();
        return Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
    }

    private static void respond(HttpExchange ex, int code, String location,
                                String body) throws IOException {
        if (location != null) {
            ex.getResponseHeaders().set("Location", location);
        }
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(code, bytes.length == 0 ? -1 : bytes.length);
        OutputStream os = ex.getResponseBody();
        os.write(bytes);
        os.close();
    }

    @Test
    public void testSameOriginRedirectIsFollowed() throws IOException {
        for (int code : REDIRECT_CODES) {
            ParsedURL purl = new ParsedURL(originUrl("/same/" + code));
            assertEquals("Same origin " + code + " redirect should be followed",
                    "target", IOUtils.toString(purl.openStream(), StandardCharsets.UTF_8));
            assertEquals("Post connection URL should be the target of the " + code + " redirect",
                    originUrl("/target"), purl.getPostConnectionURL());
        }
    }

    @Test
    public void testCrossOriginRedirectIsRejected() {
        for (int code : REDIRECT_CODES) {
            ParsedURL purl = new ParsedURL(originUrl("/cross/" + code));
            IOException e = assertThrows("Cross origin " + code + " redirect should not be followed",
                    IOException.class, purl::openStream);
            assertTrue("Unexpected exception for " + code + " redirect: " + e.getMessage(),
                    e.getMessage().contains("different origin"));
            assertEquals("Other origin should not be contacted for " + code + " redirect",
                    0, otherHits.get());
        }
    }

    @Test
    public void testUseProxyIsNotFollowed() throws IOException {
        InputStream is = new ParsedURL(originUrl("/cross/305")).openStream();
        is.close();
        assertEquals("305 Use Proxy should not be followed", 0, otherHits.get());
    }

    @Test
    public void testTooManyRedirects() {
        ParsedURL purl = new ParsedURL(originUrl("/loop"));
        IOException e = assertThrows("Redirect loop should not be followed",
                IOException.class, purl::openStream);
        assertTrue("Unexpected exception for redirect loop: " + e.getMessage(),
                e.getMessage().contains("Too many redirects"));
    }

    @Test
    public void testMaxRedirectsIsNotNegative() {
        // A negative value would skip the redirect loop entirely, leaving the
        // JDK to follow redirects without the same origin check.
        assertTrue("MAX_REDIRECTS must not be negative: " + ParsedURLData.MAX_REDIRECTS,
                ParsedURLData.MAX_REDIRECTS >= 0);
    }
}
