package android.xml2axml.util;

import android.content.res.AttributeEntry;

import java.util.Comparator;
/**
 * Created by JealousCat on 2024-08-29.
 * &#064;email 3147359496@qq.com
 * <p>
 * 对每个Tag中的Attribute进行排序，如果是资源ID小的将优先排在前面
 **/
public class AttributeSort<K,V> implements Comparator<Vector.Node<K,V>> {
    @Override
    public int compare(Vector.Node o1, Vector.Node o2) {
        if(o1!=null&&o2!=null){
            int id1 = ((AttributeEntry)o1.value).nameResId;
            int id2 = ((AttributeEntry)o2.value).nameResId;
            return Integer.compare(id1,id2);
        }else if (o1!=null){
            return -1;
        }else if(o2!=null){
            return 1;
        }else{
            return 0;
        }
    }
}
