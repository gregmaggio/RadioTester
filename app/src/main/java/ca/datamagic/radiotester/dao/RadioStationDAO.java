package ca.datamagic.radiotester.dao;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.PhantomReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import ca.datamagic.radiotester.dto.RadioStationDTO;
import ca.datamagic.radiotester.util.CustomSslHelper;

public class RadioStationDAO {
    private static final Logger logger = Logger.getLogger(RadioStationDAO.class.getName());
    private static final String API_URL = "https://radiostation-dot-api-project-378578942759.ue.r.appspot.com/api";
    private Context context = null;
    private Integer rawCertResId = null;

    public RadioStationDAO(Context context, Integer rawCertResId) {
        this.context = context;
        this.rawCertResId = rawCertResId;
    }
    public void insert(RadioStationDTO dto) throws IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpsURLConnection conn = null;
        try {
            logger.info("insert: " + dto.toString());

            Gson gson = new Gson();
            String json = gson.toJson(dto);
            logger.info("json: " + json);

            URL url = new URL(API_URL);
            conn = (HttpsURLConnection) url.openConnection();

            // Set the custom SSLSocketFactory
            SSLContext sslContext = CustomSslHelper.createSslContextFromCertificate(context, rawCertResId);
            conn.setSSLSocketFactory(sslContext.getSocketFactory());
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true); // Enables writing payload to body
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // 3. Write JSON bytes to output stream
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 4. Read response
            int responseCode = conn.getResponseCode();
            logger.info("responseCode: " + responseCode);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
