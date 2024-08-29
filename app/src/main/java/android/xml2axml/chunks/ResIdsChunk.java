package android.xml2axml.chunks;

import android.xml2axml.util.Vector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/**
 * Created by JealousCat on 2024-08-17.
 * &#064;email 3147359496@qq.com
 *<p>
 *  资源ID池的二进制块，ID从XML 节点的attributes获得
 **/
public class ResIdsChunk extends Chunk {
    public Header header = new Header();
    public Vector<Integer, String> ids;//资源ID

    public ResIdsChunk(Vector<Integer, String> resids) {
        ids = resids;
    }

    @Override
    public int sizeof() {
        return header.sizeof() + ids.size() * 4;
    }

    @Override
    public void write(ByteArrayOutputStream out) throws IOException {
        header.type = RES_XML_RESOURCE_MAP_TYPE;
        header.headerSize = (short) header.sizeof();
        header.chunkSize = sizeof();

        ByteBuffer buf = ByteBuffer.allocate(sizeof());
        buf.order(ByteOrder.LITTLE_ENDIAN);
        header.write(buf);//header
        for (int i=0;i<ids.size();i++){//resource map
            buf.putInt(ids.get(i).key);
        }
        out.write(buf.array());
        out.flush();
    }
}
