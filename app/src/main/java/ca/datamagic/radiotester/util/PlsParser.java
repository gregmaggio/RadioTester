package ca.datamagic.radiotester.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

public class PlsParser {
    private static final Logger logger = Logger.getLogger(PlsParser.class.getName());

    public static String getDirectStreamUrl(String plsUrl) {
        try {
            URL url = new URL(plsUrl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // Look for the line containing File1=, File2=, etc.
                if (line.toLowerCase().startsWith("file1=")) {
                    reader.close();
                    return line.substring(6).trim(); // Extract the actual audio URL
                }
            }
            reader.close();
        } catch (Throwable t) {
            logger.warning("Throwable: " + t.getMessage());
        }
        return null;
    }
}
