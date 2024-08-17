package android.xml2axml;

import android.content.Context;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
/**
 * Created by JealousCat on 2024-08-17.
 * &#064;email 3147359496@qq.com
 * <P>
 * 字符串池
 */
public class XMLBuilder {

    /**
     * 编译XML
     * @param context 上下文
     * @param str XML文件路径或XML文本或XML二进制文本
     * @return 编译结果
     * @throws XmlPullParserException XML解析报错
     * @throws IOException IO报错
     */
    public static byte[] compileXml(Context context,String str) throws XmlPullParserException, IOException {
        XMLNode root = XMLNode.parse(context, str.getBytes(StandardCharsets.UTF_8));
        return compileXml(context, root);
    }

    /**
     * 编译XML
     * @param context 上下文
     * @param file XML文件
     * @return 编译结果
     * @throws XmlPullParserException XML解析报错
     * @throws IOException IO报错
     */
    public static byte[] compileXml(Context context, File file) throws XmlPullParserException, IOException {
        XMLNode root = XMLNode.parse(context,file);
        return compileXml(context,root);
    }

    /**
     * 编译XML
     * @param context 上下文
     * @param root XML节点树的根节点
     * @return 编译结果
     * @throws IOException IO报错
     */
    public static byte[] compileXml(Context context, XMLNode root) throws IOException {
        root.assignResourceIds();
        root.parseValues();
        return root.flatten();
    }
}
