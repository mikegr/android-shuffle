package org.dodgybits.shuffle.android.synchronisation.tracks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
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

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Handles communication to the REST service
 *
 * @author Morten Nielsen
 */
public class WebClient {
    private String sUserAgent;
    private byte[] sBuffer = new byte[512];
    private final String tracksUser;
    private final String tracksPassword;
    private final String cTag = "WebClient";

    public WebClient(Context context, String tracksUser, String tracksPassword) throws ApiException {
        this.tracksUser = tracksUser;
        this.tracksPassword = tracksPassword;
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException ignored) {

        }
        if (info != null) {
            sUserAgent = String.format("%1$s %2$s",
                    info.packageName, info.versionName);
        }
    }

    protected synchronized boolean deleteUrl(String url) throws ApiException {
        if (sUserAgent == null) {
            throw new ApiException("User-Agent string must be prepared");
        }

        // Create client and set our specific user-agent string
        HttpClient client = CreateClient();
        java.net.URI uri = URI.create(url);
        HttpHost host = GetHost(uri);
        HttpDelete request = new HttpDelete(uri.getPath());
        request.setHeader("User-Agent", sUserAgent);

        try {
            HttpResponse response = client.execute(host, request);

            // Check if server response is valid
            StatusLine status = response.getStatusLine();
            Log.i(cTag, "delete with response " + status.toString());
            return status.getStatusCode() == HttpStatus.SC_OK;

        } catch (IOException e) {
            throw new ApiException("Problem communicating with API", e);
        }
    }

    private HttpClient CreateClient() {
        DefaultHttpClient client = new DefaultHttpClient();
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(tracksUser, tracksPassword));
        return client;
    }

    public synchronized WebResult getUrlContent(String url) throws ApiException {
        if (sUserAgent == null) {
            throw new ApiException("User-Agent string must be prepared");
        }

        // Create client and set our specific user-agent string
        HttpClient client = CreateClient();
        java.net.URI uri = URI.create(url);
        HttpHost host = GetHost(uri);


        HttpGet request = new HttpGet(uri.getPath());
        request.setHeader("User-Agent", sUserAgent);


        try {
            HttpResponse response = client.execute(host, request);

            // Check if server response is valid
            StatusLine status = response.getStatusLine();
            Log.i(cTag, "get with response " + status.toString());
            if (status.getStatusCode() != HttpStatus.SC_OK) {
            	return new WebResult(status, null);
     
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
            return new WebResult(status,new String(content.toByteArray()));

        } catch (IOException e) {
            throw new ApiException("Problem communicating with API", e);
        }
    }

    private HttpHost GetHost(URI uri) {
        HttpHost host;
        int port = uri.getPort();
        if (port == -1) {
            port = uri.getScheme().equalsIgnoreCase("https") ? 443 : 80;
        }

        host = new HttpHost(uri.getHost(), port, uri.getScheme());
        return host;
    }

    protected synchronized String postContentToUrl(String url, String content) throws ApiException {
        if (sUserAgent == null) {
            throw new ApiException("User-Agent string must be prepared");
        }

        // Create client and set our specific user-agent string
        HttpClient client = CreateClient();
        java.net.URI uri = URI.create(url);
        HttpHost host = GetHost(uri);
        HttpPost request = new HttpPost(uri.getPath());


        request.setHeader("User-Agent", sUserAgent);
        request.setHeader("Content-Type", "text/xml");


        HttpEntity ent;
        try {
            ent = new StringEntity(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ApiException("unsupported encoding set", e);
        }
        request.setEntity(ent);

        try {
            HttpResponse response = client.execute(host, request);

            // Check if server response is valid
            StatusLine status = response.getStatusLine();
            Log.i(cTag, "post with response " + status.toString());
            if (status.getStatusCode() != HttpStatus.SC_CREATED) {
                throw new ApiException("Invalid response from server: " +
                        status.toString() + " for content: " + content);
            }
            Header[] header = response.getHeaders("Location");
            if (header.length != 0) return header[0].getValue();
            else return null;


        } catch (IOException e) {
            throw new ApiException("Problem communicating with API", e);
        }
    }

    protected synchronized String putContentToUrl(String url, String content) throws ApiException {
        if (sUserAgent == null) {
            throw new ApiException("User-Agent string must be prepared");
        }

        // Create client and set our specific user-agent string
        HttpClient client = CreateClient();
        java.net.URI uri = URI.create(url);
        HttpHost host = GetHost(uri);
        HttpPut request = new HttpPut(uri.getPath());
        request.setHeader("User-Agent", sUserAgent);
        request.setHeader("Content-Type", "text/xml");

        HttpEntity ent;
        try {
            ent = new StringEntity(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ApiException("unsupported encoding", e);
        }
        request.setEntity(ent);

        try {
            HttpResponse response = client.execute(host, request);

            // Check if server response is valid
            StatusLine status = response.getStatusLine();

            Log.i(cTag, "put with response " + status.toString());
            if (status.getStatusCode() != HttpStatus.SC_OK) {
                throw new ApiException("Invalid response from server: " +
                        status.toString());
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
            throw new ApiException("Problem communicating with API", e);
        }
    }


}

