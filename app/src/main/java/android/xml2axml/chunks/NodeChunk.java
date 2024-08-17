package android.xml2axml.chunks;

import android.xml2axml.XMLNode;

import java.nio.ByteBuffer;
/**
 * Created by JealousCat on 2024-08-17.
 * &#064;email 3147359496@qq.com
 *<p>
 * NodeChunk 节点块，tag开始和结束、命名空间开始和结束都会生成节点二进制块
 **/
public abstract class NodeChunk extends Chunk {
    /**
     * NodeChunk 头
     */
    protected static class H {
        /**
         *Chunk通用头结构
         */
        public Chunk.Header header = new Chunk.Header();
        /**
         *当前行
         */
        public int lineNumber;
        /**
         *注释文本的索引，由于注释会被忽略，默认-1
         */
        public int comment_index = -1;
        /**
         *sizeof 计算NodeChunk头大小
         */
        public int sizeof() {
            return header.sizeof() + 2 * 4;
        }

        /**
         *写出当前头的内容
         * @param buf 缓冲区
         */
        public void write(ByteBuffer buf) {
            header.write(buf);
            buf.putInt(lineNumber);
            buf.putInt(comment_index);
        }
    }

    /**
     * 当前要编译的XML 节点
     */
    protected XMLNode node;

    /**
     * 创建一个XML 节点二进制块
     * @param node 当前节点
     */
    public NodeChunk(XMLNode node) {
        this.node = node;
    }
}
