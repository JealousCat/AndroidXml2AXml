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
 * 命名空间的二进制块
 **/
public class NameSpaceChunk extends NodeChunk {
    /**
     * NameSpaceChunk 头
     */
    protected H header = new H();
    /**
     *命名空间前缀 在字符串池中的索引
     */
    public int prefix_index;
    /**
     *命名空间uri 在字符串池中的索引
     */
    public int uri_index;
    /**
     * 创建命名空间节点二进制块
     * @param node 当前命名空间节点
     */
    public NameSpaceChunk(XMLNode node) {
        super(node);
    }

    @Override
    public int sizeof() {
        return header.sizeof() + 2 * 4;
    }

    @Override
    public void write(ByteArrayOutputStream out) throws IOException {
        switch (node.type) {
            case XMLNode.TYPE_START_NAMESPACE:{//命名空间的开始
                header.header.type = RES_XML_START_NAMESPACE_TYPE;
                header.lineNumber = node.mStartLineNumber;
                break;
            }
            case XMLNode.TYPE_END_NAMESPACE:{//命名空间的结束
                header.header.type = RES_XML_END_NAMESPACE_TYPE;
                header.lineNumber = node.mEndLineNumber;
                break;
            }
        }
        header.header.headerSize = (short) header.sizeof();
        header.header.chunkSize = sizeof();
        prefix_index = StringPoolChunk.strings.indexOf(node.mNamespacePrefix);
        uri_index = StringPoolChunk.strings.indexOf(node.mNamespaceUri);

        ByteBuffer buf = ByteBuffer.allocate(sizeof());//分配缓冲区
        buf.order(ByteOrder.LITTLE_ENDIAN);
        header.write(buf);
        buf.putInt(prefix_index);
        buf.putInt(uri_index);

        out.write(buf.array());//写出
        out.flush();
    }
}
