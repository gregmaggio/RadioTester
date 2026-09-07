package ca.datamagic.radiotester.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class IOUtils {
    private static Logger logger = Logger.getLogger(IOUtils.class.getName());
    private static final int BUFFER_SIZE = 4096;

    public static String readEntireString(InputStream inputStream) throws IOException {
        StringBuffer textBuffer = new StringBuffer();
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = 0;
        while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) > 0) {
            textBuffer.append(new String(buffer, 0, bytesRead));
        }
        return textBuffer.toString();
    }

    public static byte[] readEntireByteArray(InputStream inputStream) throws IOException {
        List<Byte> list = new ArrayList<>();
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = 0;
        while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) > 0) {
            for (int ii = 0; ii < bytesRead; ii++) {
                list.add(buffer[ii]);
            }
        }
        buffer = new byte[list.size()];
        for (int ii = 0; ii < list.size(); ii++) {
            buffer[ii] = list.get(ii).byteValue();
        }
        return buffer;
    }

    public static void closeQuietly(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException ex) {
            logger.warning("IOException: " + ex.getMessage());
        }
    }

    public static void closeQuietly(OutputStream outputStream) {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException ex) {
            logger.warning("IOException: " + ex.getMessage());
        }
    }

    public static void closeQuietly(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ex) {
            logger.warning("IOException: " + ex.getMessage());
        }
    }
}
