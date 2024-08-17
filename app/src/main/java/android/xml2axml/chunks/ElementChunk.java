package android.xml2axml.chunks;

import android.xml2axml.XMLNode;
/**
 * Created by JealousCat on 2024-08-17.
 * &#064;email 3147359496@qq.com
 *<p>
 * ElementChunk tag元素二进制块
 **/
public abstract class ElementChunk extends NodeChunk{
    /**
     * ElementChunk 头
     */
    protected H header = new H();
    /**
     * Element所用命名空间的在字符串池中的索引
     */
    public int ns_index;
    /**
     * Element tag名字在字符串池中的索引
     */
    public int name_index;

    /**
     * 创建一个Element的二进制块
     * @param node 当前Element节点
     */
    public ElementChunk(XMLNode node) {
        super(node);
    }
}
