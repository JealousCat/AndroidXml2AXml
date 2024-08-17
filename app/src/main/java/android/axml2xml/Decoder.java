package android.axml2xml;

import android.content.Context;

import java.io.File;
import java.io.IOException;

public class Decoder {

    public static String decode(Context context, byte[] data) {
        AndroidBinaryXml manifest = new AndroidBinaryXml(context, data);
        return manifest.toString();
    }

    public static String decode(Context context,File file) throws IOException {
        AndroidBinaryXml androidBinaryXml = new AndroidBinaryXml(context,file);
        byte[] data = androidBinaryXml.toBytes();
        return decode(context,data);
    }
}
