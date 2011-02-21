package org.dodgybits.shuffle.android.synchronisation.tracks.ssl;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

public class TrustedSSLSocketFactory extends SSLSocketFactory {
        
	private static SSLContext sslContext = null;
	private static final TrustStrategy trustStrategy = new TrustSelfSignedStrategy();
    
	public TrustedSSLSocketFactory()
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException {
        super(null);
        if (sslContext == null) {
        	initSSLContext();
        }
	}
	
    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
    
    static void initSSLContext() throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException {
		sslContext = SSLContext.getInstance("TLS");
		
		KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmfactory.init(null, null); // use default key store
        KeyManager[] keymanagers =  kmfactory.getKeyManagers();
        TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmfactory.init((KeyStore)null);
        TrustManager[] trustmanagers = tmfactory.getTrustManagers();
        for (int i = 0; i < trustmanagers.length; i++) {
            TrustManager tm = trustmanagers[i];
            if (tm instanceof X509TrustManager) {
                trustmanagers[i] = new TrustManagerDecorator((X509TrustManager)tm, trustStrategy); 
            }
        }

		sslContext.init(keymanagers, trustmanagers, null);
    }
    
		
}
