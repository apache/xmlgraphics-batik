package org.apache.batik.util;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;

public class ParsedURLDataTimeoutUnitTest {

    private MockWebServer _mockWebServer;

    @Before
    public void setUp() throws IOException {
        _mockWebServer = new MockWebServer();
        _mockWebServer.start();
    }

    @After
    public void tearDown() throws IOException {
        _mockWebServer.shutdown();
    }

    /**
     * Test that a SocketTimeoutException is thrown when the server does not respond.
     * This test fails in case URLConnection in ParsedURLData has no timeout set.
     *
     * @throws Exception
     */
    @Test
    public void testThatTimeoutIsUsedForURLConnection() throws Exception {
        _mockWebServer.enqueue(new MockResponse()
                .setSocketPolicy(SocketPolicy.NO_RESPONSE));
        ParsedURLData data = new ParsedURLData(_mockWebServer.url("/test").url());
        List<String> mimeTypes = new ArrayList<>();
        mimeTypes.add("text/html");
        assertThrows(SocketTimeoutException.class, () -> {
            InputStream userAgent = data.openStream("userAgent", mimeTypes.iterator());
        });
    }
}
