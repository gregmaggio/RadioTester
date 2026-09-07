package ca.datamagic.radiotester.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class CustomSslHelper {
    public static SSLContext createSslContextFromCertificate(Context context, int rawCertResId) throws CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        // 1. Load Certificate from res/raw
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream certInputStream = context.getResources().openRawResource(rawCertResId);
        Certificate ca;
        try {
            ca = cf.generateCertificate(certInputStream);
        } finally {
            certInputStream.close();
        }

        // 2. Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // 3. Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // 4. Create an SSLContext that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext;
    }
}
