package android.xml2axml;

import android.axml2xml.Decoder;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.xml2axml.util.FileUtils;

import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by JealousCat on 2024-08-14.
 * &#064;email 3147359496@qq.com
 * <p>
 * 编译XML文本源码为二进制XML后加载为View
 * </p>
 */
public class Loader {

    /**
     * XMLBlock类
     */
    public static Class<?> blockClass = null;
    /**
     * XMLBlock类的构造方法
     */
    public static Constructor<?> newBlock = null;
    /**
     * XMLBlock$Parser的创建方法
     */
    public static Method newParser = null;
    /**
     * 当前应用包名
     */
    public static String packageName = null;
    /**
     * 资源类实例
     */
    public static Resources res = null;
    /**
     * 用于网络请求的请求头列表，默认为空
     */
    public static Hashtable<String, String> empty_header = new Hashtable<>();

    /**
     * 收集tag属性作为ID，并记录每个tag在XML节点中的相对位置
     *
     * @param parser XML解析器
     * @return 返回记录的tag
     * @throws XmlPullParserException XML解析报错
     * @throws IOException            IO报错
     */
    public static ArrayList<String> collectId(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<String> tags = new ArrayList<>();
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String tag = parser.getAttributeValue(null, "android:tag");
                if (tag != null) {
                    tags.add(tag);
                }
            }
            eventType = parser.next();
        }
        return tags;
    }

    /**
     * 收集tag属性作为ID，并记录每个tag在XML节点中的相对位置
     *
     * @param xmlText XML文本
     * @return 返回记录的tag
     * @throws XmlPullParserException XML解析报错
     * @throws IOException            IO报错
     */
    public static ArrayList<String> collectId(String xmlText) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(xmlText));
        return collectId(parser);
    }

    /**
     * 收集tag属性作为ID，并记录每个tag在XML节点中的相对位置
     *
     * @param data XML文本字节
     * @return 返回记录的tag
     * @throws XmlPullParserException XML解析报错
     * @throws IOException            IO报错
     */
    public static ArrayList<String> collectId(byte[] data) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new ByteArrayInputStream(data), "UTF-8");
        return collectId(parser);
    }

    /**
     * 反射初始化XML加载为布局所必要的类和方法
     */
    public static void initMethod() throws ClassNotFoundException, NoSuchMethodException {
        if (blockClass == null) {
            blockClass = Class.forName("android.content.res.XmlBlock");
            newBlock = blockClass.getConstructor(byte[].class);
            newBlock.setAccessible(true);
            newParser = blockClass.getDeclaredMethod("newParser");
            newParser.setAccessible(true);
        }
    }

    /**
     * 加载一个外部或内部XML资源，返回其原始的字节内容用于后续处理
     *
     * @param context 上下文
     * @param source  XML文本路径 或 文本内容 或 XML 网络链接
     *                <p>
     *                <p>source以'/'开头，表示本地文件路径；
     *                <p>以'?'开头，表示当前apk内的XML，如assets内的一个a.xml文件的路径表示为 "?assets/a.xml"，
     *                <p>  如res/layout/下的a.xml文件路径表示为?res/layout/a
     *                <p>  需要注意的是, res下的文件需要给它设置文件资源ID，设置为ID名和xml文件名相同
     *                <p>  系统资源或者其他以加载的资源包中的xml文件，其引用方式参考Android的相关规则
     *                <p>以'h'开头，表示url网络链接，将进行网络请求获得xml内容
     * @return 加载所得字节，失败返回null
     */
    public static byte[] loadXmlResources(Context context, String source) throws Exception {
        if (context == null || source == null) {
            return null;
        }
        if (source.isEmpty()) {
            return null;
        }

        byte[] data = null;
        InputStream input = null;
        switch (source.charAt(0)) {
            case '/':
                return loadXmlResources(context, new File(source));
            case '?':
                if (source.startsWith("?assets/")) {
                    input = context.getAssets().open(source.substring(8));
                } else if (source.startsWith("?res/")) {
                    if (res == null) {
                        packageName = context.getPackageName();
                        res = context.getResources();
                    }
                    source = source.substring(5);
                    String[] list = source.split("/", -1);
                    if (list.length != 2) {
                        return null;
                    }
                    int id = res.getIdentifier(list[1], list[0], packageName);
                    input = res.openRawResource(id);
                } else {
                    int id = res.getIdentifier(source.substring(1), null, null);
                    input = res.openRawResource(id);
                }
                data = FileUtils.readInputToByteArray(input);
                break;
            case 'h':
                if (source.startsWith("http")) {
                    input = FileUtils.getUrlInput(source, empty_header);
                    data = FileUtils.readInputToByteArray(input);
                    break;
                } else {
                    return null;
                }
            default:
                data = source.getBytes();
                break;
        }
        return data;
    }

    /**
     * 加载一个外部XML文件资源，返回其原始的字节内容用于后续处理
     *
     * @param context 上下文
     * @param file    XML文件
     * @return 加载所得字节，失败返回null
     */
    public static byte[] loadXmlResources(Context context, File file) throws Exception {
        if (context == null || file == null) {
            return null;
        }
        if (!file.isFile()) {
            return null;
        }
        return FileUtils.readFileToByteArray(file);
    }

    /**
     * 加载一个外部或内部XML为View
     *
     * @param context 上下文
     * @param src     XML字符资源
     * @param globals ID池，一个tag表示一个ID，一个ID对应一个View
     * @return 加载所得布局，失败返回null
     */
    public static View loadXmlView(Context context, String src, HashMap<String, View> globals) throws Exception {
        return loadXmlView(context, loadXmlResources(context, src), globals);
    }

    /**
     * 加载一个外部或内部XML为View
     *
     * @param context 上下文
     * @param src     XML文件资源
     * @param globals ID池，一个tag表示一个ID，一个ID对应一个View
     * @return 加载所得布局，失败返回null
     */
    public static View loadXmlView(Context context, File src, HashMap<String, View> globals) throws Exception {
        return loadXmlView(context, loadXmlResources(context, src), globals);
    }

    /**
     * 加载一个外部或内部XML为View
     *
     * @param context 上下文
     * @param data    XML字节块
     * @param globals ID池，一个tag表示一个ID，一个ID对应一个View
     * @return 加载所得布局，失败返回null
     */
    public static View loadXmlView(Context context, byte[] data, HashMap<String, View> globals) throws Exception {
        if (context == null || data == null || globals == null) {
            return null;
        }
        initMethod();
        if (newParser == null) {
            return null;
        }
        ArrayList<String> ids = null;
        LayoutInflater l = LayoutInflater.from(context);
        XmlResourceParser xrp;
        if (data[0] != 0x03) {
            String source = new String(data);
            source = source.replaceAll("android:id", "android:tag").replaceAll("@\\+id/", "").replaceAll("@id/", "");
            ids = collectId(source);
            data = XMLBuilder.compileXml(context, source);
        } else {
            ids = collectId(Decoder.decode(context, data));
        }
        Object xmlBlock = newBlock.newInstance((Object) data);
        xrp = (XmlResourceParser) newParser.invoke(xmlBlock);
        View view = l.inflate(xrp, null);
        if (view != null) {
            if (!ids.isEmpty()) {
                for (String tag : ids) {
                    globals.put(tag, view.findViewWithTag(tag));
                }
            }
        }
        return view;
    }


    /**
     * 加载一个外部或内部XML为VectorDrawable
     *
     * @param context 上下文
     * @param src     XML字符资源
     * @return 加载所得Drawable，失败返回null
     */
    public static Drawable loadXmlVectorDrawable(Context context, String src) throws Exception {
        return loadXmlVectorDrawable(context, loadXmlResources(context, src));
    }

    /**
     * 加载一个外部或内部XML为VectorDrawable
     *
     * @param context 上下文
     * @param src     XML文件资源
     * @return 加载所得Drawable，失败返回null
     */
    public static Drawable loadXmlVectorDrawable(Context context, File src) throws Exception {
        return loadXmlVectorDrawable(context, loadXmlResources(context, src));
    }

    /**
     * 加载一个外部或内部XML为VectorDrawable
     *
     * @param context 上下文
     * @param data    XML字节块
     * @return 加载所得Drawable，失败返回null
     */
    public static Drawable loadXmlVectorDrawable(Context context, byte[] data) throws Exception {
        if (context == null || data == null) {
            return null;
        }
        initMethod();
        if (newParser == null) {
            return null;
        }
        XmlPullParser xrp;
        if (data[0] != 0x03) {
            String source = new String(data);
            data = XMLBuilder.compileXml(context, source);
        }
        Object xmlBlock = newBlock.newInstance((Object) data);
        xrp = (XmlPullParser) newParser.invoke(xmlBlock);
        if (xrp != null) {
            if (Build.VERSION.SDK_INT >= 24) {
                return Drawable.createFromXml(context.getResources(), xrp);
            }
            AttributeSet asAttributeSet = Xml.asAttributeSet(xrp);
            for (int next = xrp.next(); next != XmlPullParser.START_TAG; next = xrp.next()) {
            }
            return VectorDrawableCompat.createFromXmlInner(context.getResources(), xrp, asAttributeSet, (Resources.Theme) null);
        } else {
            return null;
        }
    }
}
