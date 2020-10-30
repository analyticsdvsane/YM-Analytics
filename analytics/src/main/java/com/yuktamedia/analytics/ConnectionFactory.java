package com.yuktamedia.analytics;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConnectionFactory {
    private static final int DEFAULT_READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
    private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
    static final String USER_AGENT = "analytics-android/" + BuildConfig.VERSION_NAME;

    private String authorizationHeader(String writeKey) {
        return "Bearer " + writeKey;
    }

    /** Return a {@link HttpURLConnection} that reads JSON formatted project settings. */
    public HttpURLConnection projectSettings(String writeKey) throws IOException {
        return openConnection("http://172.104.30.246:8182/cdp/v1/projects/settings?key=" + writeKey);
    }

    /**
     * Return a {@link HttpURLConnection} that writes batched payloads to {@code
     * http://brand.yuktamedia.com/v1/import}.
     */
    public HttpURLConnection upload(String writeKey) throws IOException {
//        HttpURLConnection connection = openConnection("https://analytics.yuktamedia.com/api/cdp/v1/datasync-android");
        HttpURLConnection connection = openConnection("http://172.104.30.246:8182/cdp/v1/datasync-android");
        connection.setRequestProperty("Authorization", authorizationHeader(writeKey));
        connection.setRequestProperty("Content-Encoding", "gzip");
        connection.setDoOutput(true);
        connection.setChunkedStreamingMode(0);
        return connection;
    }

    /**
     * Return a {@link HttpURLConnection} that writes gets attribution information from {@code
     * http://brand.yuktamedia.com/attribution}.
     */
    public HttpURLConnection attribution(String writeKey) throws IOException {
        HttpURLConnection connection =
//                openConnection("https://analytics.yuktamedia.com/api/cdp/v1/datasync-attibution");
                openConnection("http://172.104.30.246:8182/cdp/v1/datasync-attibution");
        connection.setRequestProperty("Authorization", authorizationHeader(writeKey));
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        return connection;
    }

    /**
     * Configures defaults for connections opened with {@link #upload(String)}, {@link
     * #attribution(String)} and {@link #projectSettings(String)}.
     */
    protected HttpURLConnection openConnection(String url) throws IOException {
        URL requestedURL;

        try {
            requestedURL = new URL(url);
        } catch (MalformedURLException e) {
            throw new IOException("Attempted to use malformed url: " + url, e);
        }

        HttpURLConnection connection = (HttpURLConnection) requestedURL.openConnection();
        connection.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS);
        connection.setReadTimeout(DEFAULT_READ_TIMEOUT_MILLIS);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setDoInput(true);
        return connection;
    }
}
