package org.dodgybits.android.shuffle.server.tracks;

import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Handles communication to the REST service
 */
public class WebClient {
    private String sUserAgent;
    private byte[] sBuffer = new byte[512];
    private final String tracksUser;
    private final String tracksPassword;

    public WebClient(ContextWrapper context, String tracksUser,
            String tracksPassword) throws ApiException {
        this.tracksUser = tracksUser;
        this.tracksPassword = tracksPassword;
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException ignored) {

        }
        if (info != null) {
            sUserAgent = String.format("%1$s %2$s", info.packageName,
                    info.versionName);
        }

    }

    protected synchronized boolean deleteUrl(String url) throws ApiException {
        if (sUserAgent == null) {
            throw new ApiException("User-Agent string must be prepared");
        }

        // Create client and set our specific user-agent string
        HttpClient client = createClient();
        HttpDelete request = new HttpDelete(url);
        request.setHeader("User-Agent", sUserAgent);

        try {
            HttpResponse response = client.execute(request);

            // Check if server response is valid
            StatusLine status = response.getStatusLine();

            return status.getStatusCode() == HttpStatus.SC_OK;

        } catch (IOException e) {
            throw new ApiException("Problem communicating with API");
        }
    }

    private HttpClient createClient() {
        DefaultHttpClient client = new DefaultHttpClient();
        client.getCredentialsProvider().setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(tracksUser, tracksPassword));
        return client;
    }

    protected synchronized String getUrlContent(String url) throws ApiException {
        if (sUserAgent == null) {
            throw new ApiException("User-Agent string must be prepared");
        }

        // Create client and set our specific user-agent string
        HttpClient client = createClient();
        HttpGet request = new HttpGet(url);
        request.setHeader("User-Agent", sUserAgent);

        try {
            HttpResponse response = client.execute(request);

            // Check if server response is valid
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != HttpStatus.SC_OK) {
                throw new ApiException("Invalid response from server: "
                        + status.toString());
            }

            // Pull content stream from response
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();

            ByteArrayOutputStream content = new ByteArrayOutputStream();

            // Read response into a buffered stream
            int readBytes;
            while ((readBytes = inputStream.read(sBuffer)) != -1) {
                content.write(sBuffer, 0, readBytes);
            }
            return new String(content.toByteArray());

        } catch (IOException e) {
            throw new ApiException("Problem communicating with API");
        }
    }

    protected synchronized String postContentToUrl(String url, String content)
            throws ApiException {
        if (sUserAgent == null) {
            throw new ApiException("User-Agent string must be prepared");
        }

        // Create client and set our specific user-agent string
        HttpClient client = createClient();
        HttpPost request = new HttpPost(url);
        request.setHeader("User-Agent", sUserAgent);
        request.setHeader("Content-Type", "text/xml");

        HttpEntity ent;
        try {
            ent = new StringEntity(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ApiException("unsupported encoding set");
        }
        request.setEntity(ent);

        try {
            HttpResponse response = client.execute(request);

            // Check if server response is valid
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != HttpStatus.SC_CREATED) {
                throw new ApiException("Invalid response from server: "
                        + status.toString());
            }
            // Pull returnContent stream from response
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();

            ByteArrayOutputStream returnContent = new ByteArrayOutputStream();

            // Read response into a buffered stream
            int readBytes;
            while ((readBytes = inputStream.read(sBuffer)) != -1) {
                returnContent.write(sBuffer, 0, readBytes);
            }
            return new String(returnContent.toByteArray());

        } catch (IOException e) {
            throw new ApiException("Problem communicating with API");
        }
    }

    protected synchronized String putContentToUrl(String url, String content)
            throws ApiException {
        if (sUserAgent == null) {
            throw new ApiException("User-Agent string must be prepared");
        }

        // Create client and set our specific user-agent string
        HttpClient client = createClient();
        HttpPut request = new HttpPut(url);
        request.setHeader("User-Agent", sUserAgent);
        request.setHeader("Content-Type", "text/xml");

        HttpEntity ent;
        try {
            ent = new StringEntity(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ApiException("unsupported encoding");
        }
        request.setEntity(ent);

        try {
            HttpResponse response = client.execute(request);

            // Check if server response is valid
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() != HttpStatus.SC_OK) {
                throw new ApiException("Invalid response from server: "
                        + status.toString());
            }

            // Pull returnContent stream from response
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();

            ByteArrayOutputStream returnContent = new ByteArrayOutputStream();

            // Read response into a buffered stream
            int readBytes;
            while ((readBytes = inputStream.read(sBuffer)) != -1) {
                returnContent.write(sBuffer, 0, readBytes);
            }
            return new String(returnContent.toByteArray());

        } catch (IOException e) {
            throw new ApiException("Problem communicating with API");
        }
    }

    public class ApiException extends Exception {
        private final String reason;

        public ApiException(String reason) {
            // To change body of created methods use File | Settings | File
            // Templates.

            this.reason = reason;
        }

        public String getReason() {
            return reason;
        }
    }
}
