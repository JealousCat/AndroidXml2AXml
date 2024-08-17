package android.xml2axml.chunks;

import android.xml2axml.StringPool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created by JealousCat on 2024-08-17.
 * &#064;email 3147359496@qq.com
 * <p>
 * 字符串池 二进制块
 **/
public class StringPoolChunk extends Chunk {
    /**
     * 字符串池头
     */
    protected static class H {
        /**
         * StringPoolChunk头内头结构
         */
        public Chunk.Header header = new Chunk.Header();
        /**
         * 字符串总数
         */
        public int stringCount;
        /**
         *样式总数，默认0
         */
        public int styleCount;
        /**
         *字符串是否是以utf8形式写出，这里强制设置为utf8，后面不处理utf16的情况
         */
        public int flags = 1 << 8;
        /**
         *字符串块开始位置，其值是相对于字符串池第一个字节计算所得
         */
        public int stringsStart;
        /**
         *样式块开始位置，默认0
         */
        public int stylesStart;

        /**
         *头大小
         */
        public int sizeof() {
            return header.sizeof() + 5 * 4;
        }

        /**
         *写出头信息
         */
        public void write(ByteBuffer buf) {
            header.write(buf);
            buf.putInt(stringCount);
            buf.putInt(styleCount);
            buf.putInt(flags);
            buf.putInt(stringsStart);
            buf.putInt(stylesStart);
        }
    }

    /**
     *字符串偏移块
     */
    protected static class OffsetList extends Chunk {

        @Override
        public int sizeof() {
            return strings.size() * 4;
        }

        @Override
        public void write(ByteArrayOutputStream out) throws IOException {
            ByteBuffer buf = ByteBuffer.allocate(sizeof());
            buf.order(ByteOrder.LITTLE_ENDIAN);
            int offset = 0;
            for (int i = 0; i < strings.size(); i++) {
                buf.putInt(offset);
                String str = strings.pool.get(i);
                byte[] dat = str.getBytes(StandardCharsets.UTF_8);
                offset = offset + 2 + dat.length;
                offset = offset + (8 - (dat.length + 2) % 4);//处理任意情况下导致的字符串池结束位置的总字节数不是4的倍数的情况，即处理非整数边界报错。并重新结算当前字符串的偏移
            }
            data.header.chunkSize = offset;
            out.write(buf.array());
            out.flush();
        }
    }

    /**
     *字符串数据，其放在字符串偏移之后
     */
    protected static class DataList extends Chunk {

        public ArrayList<String> dat = StringPoolChunk.strings.pool;
        public Chunk.Header header = new Chunk.Header();

        @Override
        public int sizeof() {
            return header.chunkSize;
        }

        @Override
        public void write(ByteArrayOutputStream out) throws IOException {
            ByteBuffer buf = ByteBuffer.allocate(sizeof());
            buf.order(ByteOrder.LITTLE_ENDIAN);
            for (int i = 0; i < dat.size(); i++) {
                String str = dat.get(i);
                byte[] src = str.getBytes(StandardCharsets.UTF_8);//以UTF8编码形式写出
                int strSize = str.length();
                int encSize = src.length;
                ENCODE_LENGTH(buf, 1, strSize);//字符串的字符数
                ENCODE_LENGTH(buf, 1, encSize);//字符串的字节长度
                buf.put(src);
                int of = 0;
                of = 8 - (encSize + 2) % 4;//处理任意情况下导致的字符串池结束位置的总字节数不是4的倍数的情况，即处理非整数边界报错
                for (int j = 0; j < of; j++) {
                    buf.put((byte) 0);
                }
            }
            out.write(buf.array());
            out.flush();
        }
        /**
         *解码字符串实际长度
         */
        public void ENCODE_LENGTH(ByteBuffer str, int chrsz, int strSize) {
            int maxMask = 1 << (((chrsz) * 8) - 1);
            int maxSize = maxMask - 1;
            int maskLen = (maxMask | (((strSize) >> ((chrsz) * 8)) & maxSize));
            if (chrsz == 1) {//utf-8
                if ((strSize) > maxSize) {
                    str.put((byte) maskLen);
                }
                str.put((byte) strSize);
            } else if (chrsz == 2) {//utf16-le
                if ((strSize) > maxSize) {
                    str.putShort((short) maskLen);
                }
                str.putShort((short) strSize);
            }
        }
    }
    /**
     *字符串池
     */
    public static StringPool strings;
    /**
     *字符串偏移
     */
    protected static OffsetList offset;
    /**
     *字符串数据
     */
    protected static DataList data;
    /**
     *创建字符串池二进制块
     */
    public StringPoolChunk(StringPool pool) {
        strings = pool;
        offset = new OffsetList();
        data = new DataList();
    }
    /**
     * 字符串池二进制块 头
     */
    protected H header = new H();

    @Override
    public int sizeof() {
        return header.sizeof() + offset.sizeof() + data.sizeof();
    }

    @Override
    public void write(ByteArrayOutputStream out) throws IOException {
        ByteArrayOutputStream x = new ByteArrayOutputStream();
        offset.write(x);//strings offsets
        data.write(x);//strings data

        header.header.type = RES_STRING_POOL_TYPE;
        header.header.headerSize = (short) header.sizeof();
        header.header.chunkSize = sizeof();
        header.stringCount = strings.size();
        header.styleCount = 0;
        header.stringsStart = header.sizeof() + offset.sizeof();
        header.stylesStart = 0;

        ByteBuffer buf = ByteBuffer.allocate(header.sizeof());
        buf.order(ByteOrder.LITTLE_ENDIAN);
        header.write(buf);//header
        out.write(buf.array());
        out.write(x.toByteArray());
        out.flush();
        x.close();
    }
}
