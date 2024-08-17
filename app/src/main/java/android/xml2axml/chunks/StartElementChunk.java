package android.xml2axml.chunks;

import android.xml2axml.XMLNode;
import android.content.res.AttributeEntry;
import android.content.res.ResValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/**
 * Created by JealousCat on 2024-08-17.
 * &#064;email 3147359496@qq.com
 *<p>
 * StartElementChunk 二进制块
 **/
public class StartElementChunk extends ElementChunk{

    public StartElementChunk(XMLNode node) {
        super(node);
    }

    /**
     * attribute 属性块
     */
    protected static class AttrExt{
        /**
         * 属性命名空间在字符串池中的索引
         */
        public int ns_index;
        /**
         *属性名在字符串池中的索引
         */
        public int name_index;
        /**
         *属性原值在字符串池中的索引，无索引时设置-1
         */
        public int rawValue_index;
        /**
         *属性值
         */
        public ResValue typedValue = new ResValue();
        /**
         *属性块大小
         */
        public int sizeof(){
            return 3*4+ typedValue.sizeof();
        }

        /**
         * 写出属性块
         * @param buf 缓冲区
         */
        public void write(ByteBuffer buf) {
            buf.putInt(ns_index);
            buf.putInt(name_index);
            buf.putInt(rawValue_index);
            typedValue.write(buf);
        }
    }

    /**
     * 当前节点的节点属性块开始的第一个字节 相对于 节点块开始的第一个节点的距离，一般值为20
     */
    public short attributeStart;
    /**
     * 单个属性块的大小
     */
    public short attributeSize;
    /**
     *当前节点的属性数量
     */
    public short attributeCount;
    /**
     *id属性索引，默认0，不解析
     */
    public short idIndex;
    /**
     *class属性索引，默认0，不解析
     */
    public short classIndex;
    /**
     *样式索引，默认0，不解析
     */
    public short styleIndex;
    /**
     * 空属性块，用于计算属性块的大小
     */
    private static final AttrExt empty = new AttrExt();
    @Override
    public int sizeof() {
        return header.sizeof() + 2*4 + 6*2 + node.mAttributes.size() * empty.sizeof();
    }

    @Override
    public void write(ByteArrayOutputStream out) throws IOException {
        header.header.type = RES_XML_START_ELEMENT_TYPE;
        header.header.headerSize = (short) header.sizeof();
        header.header.chunkSize = sizeof();
        header.lineNumber = node.mStartLineNumber;
        if (!node.mNamespaceUri.isEmpty()) {
            ns_index = StringPoolChunk.strings.indexOf(node.mNamespaceUri);
        } else {
            ns_index = -1;
        }

        name_index = StringPoolChunk.strings.indexOf(node.mElementName);

        attributeStart = (short) 20;
        attributeSize = (short) empty.sizeof();
        int NA = node.mAttributes.size();
        attributeCount = (short) NA;

        for (int i = 0; i < NA; i++) {
            AttributeEntry ae = node.mAttributes.itemAt(i);
            if (ae.ns.isEmpty()) {
                if ("id".equals(ae.name)) {
                    idIndex = (short) (i + 1);
                } else if ("class".equals(ae.name)) {
                    classIndex = (short) (i + 1);
                } else if ("style".equals(ae.name)) {
                    styleIndex = (short) (i + 1);
                }
            }
        }

        ByteBuffer buf = ByteBuffer.allocate(sizeof());
        buf.order(ByteOrder.LITTLE_ENDIAN);
        header.write(buf);//header
        buf.putInt(ns_index);
        buf.putInt(name_index);
        buf.putShort(attributeStart);
        buf.putShort(attributeSize);
        buf.putShort(attributeCount);
        buf.putShort(idIndex);
        buf.putShort(classIndex);
        buf.putShort(styleIndex);

        for (int i = 0; i < NA; i++) { //attributes
            AttributeEntry ae = node.mAttributes.itemAt(i);
            AttrExt attr = new AttrExt();
            if (!ae.ns.isEmpty()) {
                attr.ns_index = StringPoolChunk.strings.indexOf(ae.ns);
            } else {
                attr.ns_index = -1;
            }
            attr.name_index = StringPoolChunk.strings.indexOf(ae.name);;
            if (ae.needStringValue()) {
                attr.rawValue_index = StringPoolChunk.strings.indexOf(ae.string);
            } else {
                attr.rawValue_index = -1;
            }
            attr.typedValue.size = (short) attr.typedValue.sizeof();
            if (ae.value.dataType == ResValue.TYPE_NULL || ae.value.dataType == ResValue.TYPE_STRING) {
                attr.typedValue.dataType = ResValue.TYPE_STRING;
                attr.typedValue.data = StringPoolChunk.strings.indexOf(ae.string);
            } else {
                attr.typedValue.dataType = ae.value.dataType;
                attr.typedValue.data = ae.value.data;
            }
            attr.typedValue.res0 = 0;
            attr.typedValue.data_f = ae.value.data_f;
            attr.write(buf);
        }

        out.write(buf.array());
        out.flush();
    }
}
