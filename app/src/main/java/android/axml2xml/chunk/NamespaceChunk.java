package android.axml2xml.chunk;

import android.axml2xml.chunk.base.BaseContentChunk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Sens on 2021/8/27.
 */
public class NamespaceChunk extends BaseContentChunk {
    public final int prefix;
    public final int uri;

    public NamespaceChunk(ByteBuffer byteBuffer, StringChunk stringChunk) {
        super(byteBuffer, stringChunk);
        this.prefix = byteBuffer.getInt();
        this.uri = byteBuffer.getInt();
        byteBuffer.position(ChunkStartPosition + chunkSize);
    }

    @Override
    protected void toBytes(ByteArrayOutputStream stream) throws IOException {
        super.toBytes(stream);
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(this.prefix);
        byteBuffer.putInt(this.uri);
        stream.write(byteBuffer.array());
    }

    public String getXmlNameSpace() {
        return "xmlns:" + getString(prefix) + "=\"" + getString(uri) + '"';
    }

    @Override
    public String toString() {
        return "";
    }
}
