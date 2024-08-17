package android.xml2axml.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
/**
 * Created by JealousCat on 2024-08-09.
 * &#064;email 3147359496@qq.com
 * <p>
 *  文件读写和url请求
 * </p>
 */
public class FileUtils {
    public static byte[] empty = new byte[0];

    public static byte[] readInputToByteArray(InputStream inputStream) {
        try {
            byte[] buffer = new byte[10240];
            int len = 0;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.close();
            return bos.toByteArray();
        } catch (Exception e) {
            try {
                inputStream.close();
            } catch (Exception ignored) {
            }
            e.printStackTrace();
        }
        return empty;
    }

    public static byte[] readFileToByteArray(File file) {
        if (file == null) {
            return empty;
        }
        try {
            FileInputStream input = new FileInputStream(file);
            return readInputToByteArray(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empty;
    }

    public static void writeByteArrayToFile(File file, byte[] bs) {
        if (file == null) {
            return;
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(bs);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isBinaryXml(InputStream inputStream) {
        try {
            byte header = (byte) (inputStream.read() & 255);
            if (header == 0x03) {
                try {
                    inputStream.close();
                } catch (Exception ignored) {
                }
                return true;
            }
        } catch (IOException e) {
            try {
                inputStream.close();
            } catch (Exception ignored) {
            }
            throw new RuntimeException(e);
        }
        return false;
    }

    public static boolean isBinaryXml(String source) {
        return source.charAt(0) == 0x03;
    }

    public static InputStream getUrlInput(String str, Hashtable<String, String> table) {
        InputStream in = null;
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            if (table != null && !table.isEmpty()) {
                Set<Map.Entry<String, String>> set = table.entrySet();
                for (Map.Entry<String, String> entry : set) {
                    httpURLConnection.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            httpURLConnection.getResponseCode();
            String url = httpURLConnection.getURL().toString();
            if (url.length() > 5) {
                httpURLConnection.disconnect();
                httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
            }
            in = httpURLConnection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return in;
    }
}
