package android.xml2axml.chunks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/**
 * Created by JealousCat on 2024-08-17.
 * &#064;email 3147359496@qq.com
 * <p>
 * 整个XML文件 二进制块
 **/
public class XMLChunk extends Chunk{
    public Header header = new Header();

    @Override
    public int sizeof() {
        return header.sizeof() + chunk.length;
    }

    @Override
    public void write(ByteArrayOutputStream out) throws IOException {
        header.type = RES_XML_TYPE;
        header.headerSize = (short) header.sizeof();
        header.chunkSize = sizeof();

        ByteBuffer buf = ByteBuffer.allocate(header.chunkSize);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        header.write(buf);
        buf.put(chunk);

        out.write(buf.array());
        out.flush();
    }
}
