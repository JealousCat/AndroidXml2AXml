package android.xml2axml.chunks;

import android.xml2axml.XMLNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/**
 * Created by JealousCat on 2024-08-17.
 * &#064;email 3147359496@qq.com
 *<p>
 * 结束标签的二进制块
 **/
public class EndElementChunk extends ElementChunk{
    /**
     * 创建一个结束标签的二进制块
     * @param node 当前Element节点
     */
    public EndElementChunk(XMLNode node) {
        super(node);
    }

    @Override
    public int sizeof() {
        return header.sizeof()+2*4;
    }

    @Override
    public void write(ByteArrayOutputStream out) throws IOException {
        header.header.type = RES_XML_END_ELEMENT_TYPE;
        header.header.headerSize = (short) header.sizeof();
        header.header.chunkSize = sizeof();
        header.lineNumber = node.mEndLineNumber;

        if (!node.mNamespaceUri.isEmpty()) {
            ns_index = StringPoolChunk.strings.indexOf(node.mNamespaceUri);
        } else {
            ns_index = -1;
        }

        name_index = StringPoolChunk.strings.indexOf(node.mElementName);

        ByteBuffer buf = ByteBuffer.allocate(sizeof());//按块大小创建缓冲区
        buf.order(ByteOrder.LITTLE_ENDIAN);//以小端编码写出
        header.write(buf);//header写出
        buf.putInt(ns_index);//写出命名空间索引
        buf.putInt(name_index);//写出标签名索引

        out.write(buf.array());//将缓冲区内容写到输出流
        out.flush();
    }
}
