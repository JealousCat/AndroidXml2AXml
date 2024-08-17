package android.xml2axml.chunks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
/**
 * Created by JealousCat on 2024-08-17.
 * &#064;email 3147359496@qq.com
 *<p>
 * Chunk 二进制块
 */
public abstract class Chunk {
    //Chunk types
    public static final int RES_NULL_TYPE = 0x0000;
    public static final int RES_STRING_POOL_TYPE = 0x0001;
    public static final int RES_TABLE_TYPE = 0x0002;
    public static final int RES_XML_TYPE = 0x0003;

    // Chunk types in RES_XML_TYPE
    public static final int RES_XML_FIRST_CHUNK_TYPE = 0x0100;
    public static final int RES_XML_START_NAMESPACE_TYPE = 0x0100;
    public static final int RES_XML_END_NAMESPACE_TYPE = 0x0101;
    public static final int RES_XML_START_ELEMENT_TYPE = 0x0102;
    public static final int RES_XML_END_ELEMENT_TYPE = 0x0103;
    public static final int RES_XML_CDATA_TYPE = 0x0104;
    public static final int RES_XML_LAST_CHUNK_TYPE = 0x017f;

    public static final int RES_XML_RESOURCE_MAP_TYPE = 0x0180;

    public static final int RES_TABLE_PACKAGE_TYPE = 0x0200;
    public static final int RES_TABLE_TYPE_TYPE = 0x0201;
    public static final int RES_TABLE_TYPE_SPEC_TYPE = 0x0202;
    public static final int RES_TABLE_LIBRARY_TYPE = 0x0203;
    public static final int RES_TABLE_OVERLAYABLE_TYPE = 0x0204;
    public static final int RES_TABLE_OVERLAYABLE_POLICY_TYPE = 0x0205;
    public static final int RES_TABLE_STAGED_ALIAS_TYPE = 0x0206;

    /**
    chunk 头
     **/
    public static class Header{
        /**
         * Chunk 类型
         */
        public short type;
        /**
         * Chunk头大小
         */
        public short headerSize;
        /**
        Chunk大小
        **/
        public int chunkSize;

        /**
         sizeof 计算块大小
         **/
        public int sizeof(){
            return 2*2+4;
        }

        /**
         * write 写出块头
         * @param buf 字节缓冲区，采用小端输出
         */
        public void write(ByteBuffer buf){
            buf.putShort(type);
            buf.putShort(headerSize);
            buf.putInt(chunkSize);
        }
    }

    /**
     * 当前块的子块，主要用于存放xml除头外的字节，方便计算块大小
     **/
    public byte[] chunk;
    /**
     * 当前块的大小，该计算会将头的大小也包含在内
     **/
    public abstract int sizeof();

    /**
     * 向字节输出流写出内容
     * @param out 输出流
     * @throws IOException IO报错
     */
    public abstract void write(ByteArrayOutputStream out) throws IOException;
}
