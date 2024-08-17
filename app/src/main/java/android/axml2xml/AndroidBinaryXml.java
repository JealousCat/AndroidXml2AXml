package android.axml2xml;

import android.axml2xml.chunk.EndTagChunk;
import android.axml2xml.chunk.NamespaceChunk;
import android.axml2xml.chunk.ResourceChunk;
import android.axml2xml.chunk.StartTagChunk;
import android.axml2xml.chunk.StringChunk;
import android.axml2xml.chunk.base.BaseChunk;
import android.axml2xml.chunk.base.ChunkType;
import android.content.Context;
import android.xml2axml.util.FileUtils;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sens on 2021/8/27.
 * {@see <a href="https://github.com/senswrong/AndroidBinaryXml">AndroidBinaryXml</a>}
 */
public class AndroidBinaryXml {
    public short fileType;
    public short headerSize;
    public int fileSize;
    public StringChunk stringChunk;
    public ResourceChunk resourceChunk;
    public List<BaseChunk> structList = new ArrayList<>();

    public AndroidBinaryXml(Context context, File androidManifest) {
        this(context, FileUtils.readFileToByteArray(androidManifest));
    }

    public AndroidBinaryXml(Context context, byte[] datas) {
        int available = datas.length;
        ByteBuffer byteBuffer = ByteBuffer.wrap(datas);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        fileType = byteBuffer.getShort();
        headerSize = byteBuffer.getShort();
        fileSize = byteBuffer.getInt();
        List<NamespaceChunk> namespaceChunkList = new ArrayList<>();
        while (byteBuffer.position() < available) {
            short Type = byteBuffer.getShort();
            ChunkType chunkType = ChunkType.valueOf(Type);
            if (chunkType == null) break;
            BaseChunk chunk = null;
            switch (chunkType) {
                case CHUNK_STRING:
                    stringChunk = new StringChunk(byteBuffer);
                    chunk = stringChunk;
                    break;
                case CHUNK_RESOURCE:
                    resourceChunk = new ResourceChunk(byteBuffer);
                    chunk = resourceChunk;
                    break;
                case CHUNK_START_NAMESPACE:
                    chunk = new NamespaceChunk(byteBuffer, stringChunk);
                    namespaceChunkList.add((NamespaceChunk)chunk);
                    structList.add(chunk);
                    break;
                case CHUNK_START_TAG:
                    chunk = new StartTagChunk(byteBuffer, stringChunk, namespaceChunkList);
                    structList.add(chunk);
                    break;
                case CHUNK_END_TAG:
                    chunk= new EndTagChunk(byteBuffer, stringChunk);
                    structList.add(chunk);
                    break;
                case CHUNK_END_NAMESPACE:
                    chunk = new NamespaceChunk(byteBuffer, stringChunk);
                    structList.add(chunk);
                    break;
            }
            chunk.context = context;
        }
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (stringChunk != null) stream.write(stringChunk.toBytes());
        if (resourceChunk != null) stream.write(resourceChunk.toBytes());
        for (BaseChunk chunk : structList)
            stream.write(chunk.toBytes());
        fileSize = 8 + stream.size();
        ByteBuffer byteBuffer = ByteBuffer.allocate(fileSize);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putShort(fileType);
        byteBuffer.putShort(headerSize);
        byteBuffer.putInt(fileSize);
        byteBuffer.put(stream.toByteArray());
        return byteBuffer.array();
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        boolean hasXmlns = false;
        for (BaseChunk baseChunk : structList) {
            if (!hasXmlns && baseChunk instanceof StartTagChunk) {
                ((StartTagChunk) baseChunk).addXmlns();
                hasXmlns = true;
            }
            sb.append(baseChunk);
        }
        return sb.toString();
    }
}
