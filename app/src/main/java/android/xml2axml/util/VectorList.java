package android.xml2axml.util;
/**
 * Created by JealousCat on 2024-08-09.
 * &#064;email 3147359496@qq.com
 * <P>
 * 值对列表，键将等同于元素索引
 */
public class VectorList<V> extends Vector<Integer,V> {

    @Override
    public int put(V value){
        super.add(new Node<>(size(),value));
        return size()-1;
    }

    public V last(){
        int index = size()-1;
        if(index>=0) {
            return get(index).value;
        }
        return null;
    }
}
