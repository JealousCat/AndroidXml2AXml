package android.xml2axml;

import android.content.Context;
import android.content.res.AttributeEntry;
import android.content.res.ResValue;
import android.xml2axml.chunks.EndElementChunk;
import android.xml2axml.chunks.NameSpaceChunk;
import android.xml2axml.chunks.ResIdsChunk;
import android.xml2axml.chunks.StartElementChunk;
import android.xml2axml.chunks.StringPoolChunk;
import android.xml2axml.chunks.XMLChunk;
import android.xml2axml.util.StringPoolSort;
import android.xml2axml.util.Vector;
import android.xml2axml.util.VectorList;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by JealousCat on 2024-08-09.
 * &#064;email 3147359496@qq.com
 * <p>
 * 解析XML为节点树
 * </p>
 */
public class XMLNode {
    public static String VERSION = "axml 0.1 version ©2024 妒猫";
    public static final String TAG = "XML Node";

    //命名空间
    public static final String RESOURCES_ROOT_NAMESPACE = "http://schemas.android.com/apk/res/";
    public static final String RESOURCES_ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android";
    public static final String RESOURCES_AUTO_PACKAGE_NAMESPACE = "http://schemas.android.com/apk/res-auto";
    public static final String RESOURCES_ROOT_PRV_NAMESPACE = "http://schemas.android.com/apk/prv/res/";

    public static final String RESOURCES_PREFIX = RESOURCES_ROOT_NAMESPACE;
    public static final String RESOURCES_PREFIX_AUTO_PACKAGE = RESOURCES_AUTO_PACKAGE_NAMESPACE;
    public static final String RESOURCES_PRV_PREFIX = RESOURCES_ROOT_PRV_NAMESPACE;
    public static final String RESOURCES_TOOLS_NAMESPACE = "http://schemas.android.com/tools";

    //xml节点类型
    public static final int TYPE_START_NAMESPACE = 0;
    public static final int TYPE_END_NAMESPACE = 1;
    public static final int TYPE_START_ELEMENT = 2;
    public static final int TYPE_END_ELEMENT = 3;
    public static final int TYPE_XML = 4;

    /**
     * 命名空间前缀
     */
    public String mNamespacePrefix = "";
    /**
     * 命名空间uri
     */
    public String mNamespaceUri = "";

    /**
     * 节点标签名
     **/
    public String mElementName = "";

    /**
     * 节点开始行号
     **/
    public int mStartLineNumber;
    /**
     * 节点结束行号
     **/
    public int mEndLineNumber;
    /**
     * 子节点
     **/
    public VectorList<XMLNode> mChildren = new VectorList<>();
    /**
     * 属性
     **/
    public VectorList<AttributeEntry> mAttributes = new VectorList<>();

    /**
     * 节点类型
     **/
    public int type;

    /**
     * 标记需要保留的字符串
     */
    public static HashMap<String, Boolean> needString = null;

    /**
     * 字符串池，仅在根节点中赋值
     **/
    public StringPool stringPool;

    /**
     * 存储非Attribute的原始值的字符串
     */
    public static StringPool otherStrPool;

    /**
     * 上下文环境，用于获取系统资源
     **/
    public Context context;

    //用于打印XMLNode的字段
    /**
     * 父节点
     **/
    public XMLNode parent;
    /**
     * 下一个节点，用于XMLNode 打印时将命名空间设置到紧挨着的Tag中
     **/
    public XMLNode next;
    /**
     * 当前节点深度，用于xml代码缩进
     **/
    public int deep;

    /**
     * 创建节点
     *
     * @param context 上下文
     * @param s1      命名空间前缀或uri，将根据type判断
     * @param s2      命名空间uri或标签名，将根据type判断
     * @param type    节点类型
     */
    public XMLNode(Context context, String s1, String s2, int type) {
        if (type < TYPE_START_ELEMENT) {
            mNamespacePrefix = s1;
            mNamespaceUri = s2;
        } else {
            mNamespaceUri = s1;
            mElementName = s2;
        }
        this.context = context;
        this.type = type;
    }

    /**
     * 创建命名空间节点
     *
     * @param context 上下文环境
     * @param prefix  命名空间前缀
     * @param uri     命名空间uri
     * @param type    节点类型
     * @return 命名空间节点
     */
    public static XMLNode newNamespace(Context context, String prefix, String uri, int type) {
        return new XMLNode(context, prefix, uri, type);
    }

    /**
     * 创建标签节点
     *
     * @param context 上下文环境
     * @param ns      命名空间
     * @param name    标签名
     * @param type    节点类型
     * @return 标签节点
     */
    public static XMLNode newElement(Context context, String ns, String name, int type) {
        return new XMLNode(context, ns, name, type);
    }

    /**
     * 解析XML文档为节点数
     *
     * @param context 上下文环境
     * @param data    xml字节块
     * @return 返回解析后的根节点
     * @throws XmlPullParserException 解析报错
     * @throws IOException            IO报错
     */
    public static XMLNode parse(Context context, byte[] data) throws XmlPullParserException, IOException {
        XmlPullParserFactory f = XmlPullParserFactory.newInstance();
        f.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        XmlPullParser parser = f.newPullParser();
        parser.setInput(new ByteArrayInputStream(data), "UTF-8");
        return parse(context, parser);
    }

    /**
     * 解析XML文档为节点数
     *
     * @param context 上下文环境
     * @param file    xml文件
     * @return 返回解析后的根节点
     * @throws XmlPullParserException 解析报错
     * @throws IOException            IO报错
     */
    public static XMLNode parse(Context context, File file) throws XmlPullParserException, IOException {
        XmlPullParserFactory f = XmlPullParserFactory.newInstance();
        f.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        XmlPullParser parser = f.newPullParser();
        FileInputStream fileInputStream = new FileInputStream(file);
        parser.setInput(fileInputStream, "UTF-8");
        return parse(context, parser);
    }

    /**
     * 解析XML文档为节点数
     *
     * @param context 上下文环境
     * @param parser  xml解析器
     * @return 返回解析后的根节点
     * @throws XmlPullParserException 解析报错
     * @throws IOException            IO报错
     */
    public static XMLNode parse(Context context, XmlPullParser parser) throws XmlPullParserException, IOException {

        StringPool pool = new StringPool();
        StringPool oStr = new StringPool();

        XMLNode root = new XMLNode(context, "", "", TYPE_XML);//根节点设置为整个文件
        root.stringPool = pool;
        otherStrPool = oStr;

        int i, n, start, end, count;
        String prefix, uri, nonNullPrefix;
        XMLNode node = null, current = root;
        String ns, name, value;
        for (i = parser.getEventType(); i != XmlPullParser.END_DOCUMENT; i = parser.nextToken()) {
            switch (i) {
                case XmlPullParser.START_TAG: {
                    //startSpaceName
                    start = parser.getNamespaceCount(parser.getDepth() - 1);
                    end = parser.getNamespaceCount(parser.getDepth());
                    for (n = start; n < end; n++) {
                        prefix = parser.getNamespacePrefix(n);
                        uri = parser.getNamespaceUri(n);

                        pool.add(prefix);
                        pool.add(uri);

                        oStr.add(prefix);
                        oStr.add(uri);

                        node = newNamespace(context, prefix, uri, TYPE_START_NAMESPACE);
                        node.mStartLineNumber = parser.getLineNumber();
                        node.parent = current;
                        node.deep = parser.getDepth();
                        XMLNode prev = current.mChildren.last();
                        if (prev != null) {
                            prev.next = node;
                        }
                        current.mChildren.put(node);
                    }
                    //startTag
                    ns = parser.getNamespace();
                    name = parser.getName();

                    pool.add(ns);
                    pool.add(name);

                    oStr.add(ns);
                    oStr.add(name);

                    node = newElement(context, ns, name, TYPE_START_ELEMENT);
                    node.mStartLineNumber = parser.getLineNumber();
                    node.parent = current;
                    node.deep = parser.getDepth();
                    XMLNode prev = current.mChildren.last();
                    if (prev != null) {
                        prev.next = node;
                    }

                    current.mChildren.put(node);
                    count = parser.getAttributeCount();
                    for (n = 0; n < count; n++) {
                        ns = parser.getAttributeNamespace(n);
                        prefix = parser.getAttributePrefix(n);
                        name = parser.getAttributeName(n);
                        value = parser.getAttributeValue(n);

                        AttributeEntry entry = new AttributeEntry();
                        entry.ns = ns;
                        entry.prefix = prefix;
                        entry.name = name;
                        entry.string = value;
                        node.mAttributes.put(entry);
                        pool.add(ns);
                        pool.add(prefix);
                        pool.add(name);
                        pool.add(value);

                        oStr.add(ns);
                        oStr.add(prefix);
                        oStr.add(name);
                    }

                    current = node;
                    break;
                }
                case XmlPullParser.END_TAG: {
                    //endTag
                    current = current.parent;
                    ns = parser.getNamespace();
                    name = parser.getName();

                    pool.add(ns);
                    pool.add(name);

                    oStr.add(ns);
                    oStr.add(name);

                    node = newElement(context, ns, name, TYPE_END_ELEMENT);
                    node.mEndLineNumber = parser.getLineNumber();
                    node.deep = parser.getDepth();
                    XMLNode prev = current.mChildren.last();
                    if (prev != null) {
                        prev.next = node;
                    }
                    current.mChildren.put(node);

                    //endSpaceName
                    start = parser.getNamespaceCount(parser.getDepth() - 1);
                    end = parser.getNamespaceCount(parser.getDepth());
                    for (n = end - 1; n >= start; n--) {
                        prefix = parser.getNamespacePrefix(n);
                        nonNullPrefix = prefix != null ? prefix : "";
                        uri = parser.getNamespaceUri(n);

                        pool.add(nonNullPrefix);
                        pool.add(uri);

                        oStr.add(nonNullPrefix);
                        oStr.add(uri);

                        node = newNamespace(context, nonNullPrefix, uri, TYPE_END_NAMESPACE);
                        node.mEndLineNumber = parser.getLineNumber();
                        node.deep = parser.getDepth();
                        prev = current.mChildren.last();
                        if (prev != null) {
                            prev.next = node;
                        }
                        current.mChildren.put(node);
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }
        return root;
    }

    /**
     * 预处理属性名ID
     */
    public void assignResourceIds() {
        if (type == TYPE_START_ELEMENT || type == TYPE_END_ELEMENT) {
            String attr = "attr";
            int N = mAttributes.size();
            for (int i = 0; i < N; i++) {
                AttributeEntry e = mAttributes.itemAt(i);

                if (e.ns.isEmpty()) continue;

                String pkg = e.prefix;

                if (pkg.isEmpty()) continue;

                e.nameResId = context.getResources().getIdentifier(e.name, attr, pkg);
            }
        }
        int N = mChildren.size();
        for (int i = 0; i < N; i++) {
            mChildren.itemAt(i).assignResourceIds();
        }
    }

    /**
     * 预处理资源值
     */
    public void parseValues() throws Exception{
        if (type == TYPE_START_ELEMENT || type == TYPE_END_ELEMENT) {
            int N = mAttributes.size();
            String[] defPackage = {context.getPackageName()};
            for (int i = 0; i < N; i++) {
                AttributeEntry e = mAttributes.itemAt(i);
                ResValue.stringToValue(context, e, defPackage);
            }
        }
        int N = mChildren.size();
        for (int i = 0; i < N; i++) {
            mChildren.itemAt(i).parseValues();
        }
    }

    /**
     * 根节点扁平化
     *
     * @return 返回整个XML布局的编译后的二进制块
     * @throws IOException IO报错
     */
    public byte[] flatten() throws IOException {
        //收集整理资源ID
        needString = new HashMap<>();
        Vector<Integer, String> resids = new Vector<>();
        collect_resid_strings(resids, true);//收集整理字符串池
        collect_resid_strings(resids, false);//收集整理ID池

        //从字符串池中移除不需要保留的字符串
        Set<String> set = needString.keySet();
        for(String str:set){
            boolean b = Boolean.TRUE.equals(needString.get(str));
            if(!b){
                stringPool.remove(str);
            }
        }
        stringPool.sort(new StringPoolSort(resids));//字符串池排序
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //写出StringPool
        StringPoolChunk strings = new StringPoolChunk(stringPool);
        strings.write(out);
        //写出ResIds
        ResIdsChunk ids = new ResIdsChunk(resids);
        ids.write(out);
        //写出其他节点
        int N = mChildren.size();
        for (int i = 0; i < N; i++) {
            XMLNode node = mChildren.itemAt(i);
            node.flatten_node(out);
        }

        out.flush();

        ByteArrayOutputStream dest = new ByteArrayOutputStream();

        //写出整个XML的二进制块
        XMLChunk xmlChunk = new XMLChunk();
        xmlChunk.chunk = out.toByteArray();
        xmlChunk.write(dest);

        byte[] results = dest.toByteArray();
        out.close();
        dest.close();
        return results;
    }

    /**
     * 处理字符串池和ID池
     *
     * @param outResIds ID池
     * @param collect   是否处理字符串池，true处理字符串池，false处理ID池
     */
    public void collect_resid_strings(Vector<Integer, String> outResIds, boolean collect) {
        collect_attr_strings(outResIds, collect);
        int NC = mChildren.size();
        for (int i = 0; i < NC; i++) {
            XMLNode node = mChildren.itemAt(i);
            node.collect_resid_strings(outResIds, collect);
        }
    }

    /**
     * 根据属性键值对，添加属性资源ID或标记需要保留的字符串值
     *
     * @param outResIds 资源ID池
     * @param collect   是否在处理字符串池，true处理字符串池，false处理ID池
     */
    public void collect_attr_strings(Vector<Integer, String> outResIds, boolean collect) {
        int NA = mAttributes.size();
        for (int i = 0; i < NA; i++) {
            AttributeEntry entry = mAttributes.itemAt(i);
            if (collect) {
                if (otherStrPool.contains(entry.string)) {
                    needString.put(entry.string, true);
                } else if (entry.needStringValue()) {
                    needString.put(entry.string, true);
                } else {
                    Boolean b = needString.get(entry.string);
                    if (b == null || (!b)) {
                        needString.put(entry.string, false);
                    }
                }
            } else {
                if (entry.nameResId != 0) {
                    if (!outResIds.containsValue(entry.name)) {
                        outResIds.add(entry.nameResId, entry.name);
                    }
                }
            }
        }
    }

    /**
     * 扁平化XML子节点
     *
     * @param out 输出流
     * @throws IOException IO报错
     */
    public void flatten_node(ByteArrayOutputStream out) throws IOException {
        switch (type) {
            case XMLNode.TYPE_START_ELEMENT: {
                StartElementChunk start = new StartElementChunk(this);
                start.write(out);
                int N = mChildren.size();
                for (int i = 0; i < N; i++) {
                    mChildren.itemAt(i).flatten_node(out);
                }
                break;
            }
            case XMLNode.TYPE_END_ELEMENT: {
                EndElementChunk end = new EndElementChunk(this);
                end.write(out);
                break;
            }
            case XMLNode.TYPE_START_NAMESPACE:
            case XMLNode.TYPE_END_NAMESPACE: {
                NameSpaceChunk endNameSpace = new NameSpaceChunk(this);
                endNameSpace.write(out);
                break;
            }
        }
    }

    /**
     * 前个节点的字符串形式
     */
    public StringBuilder front;
    public static StringBuilder child_front = new StringBuilder();

    @NonNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        StringBuilder ident = new StringBuilder();
        for (int i = 0; i < deep; i++) {
            ident.append("  ");
        }
        switch (type) {
            case TYPE_START_ELEMENT: {
                sb.append(ident).append("<").append(mElementName).append(" \n");
                if (front != null) {
                    sb.append(front);
                }
                int count = mAttributes.size();
                for (int i = 0; i < count; i++) {
                    AttributeEntry e = mAttributes.itemAt(i);
                    sb.append(ident).append("   ")
                            .append(e.prefix).append(':').append(e.name)
                            .append('=')
                            .append('"').append(e.string).append("\"");
                    if (i < count - 1) {
                        sb.append("\n");
                    }
                }
                count = mChildren.size();
                if (count > 0) {
                    sb.append(">\n");
                    for (int i = 0; i < count; i++) {
                        sb.append(mChildren.itemAt(i));
                    }
                } else {
                    sb.append("/>\n");
                    if (parent != null) {
                        int index = parent.mChildren.indexOfValue(this);
                        if (index >= 0) {
                            XMLNode end = parent.mChildren.itemAt(index + 1);
                            if (end != null) {
                                end.front = child_front;
                            }
                        }
                    }
                }
                break;
            }
            case TYPE_END_ELEMENT: {
                if (front == null) {
                    sb.append(ident).append("</").append(mElementName).append(">\n");
                }
                break;
            }
            case TYPE_START_NAMESPACE: {
                StringBuilder s = new StringBuilder();
                s.append(ident).append(" xmlns:").append(mNamespacePrefix).append("=\"").append(mNamespaceUri).append("\"\n");
                if (front != null) {
                    next.front = front.append(s);
                } else {
                    next.front = s;
                }
                break;
            }
            case TYPE_END_NAMESPACE: {
                sb.append(ident).append("<!-- ").append("xmlns:").append(mNamespacePrefix).append("=\"").append(mNamespaceUri).append("\" -->\n");
                break;
            }
            case TYPE_XML: {
                sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
                int count = mChildren.size();
                for (int i = 0; i < count; i++) {
                    sb.append(mChildren.itemAt(i));
                }
                break;
            }
        }
        return sb.toString();
    }
}
