package android.xml2axml.util;

import java.util.Comparator;
/**
 * Created by JealousCat on 2024-08-17.
 * &#064;email 3147359496@qq.com
 * <p>
 * 对已获得的资源ID进行排序
 **/
public class ResIdsSort implements Comparator<Vector.Node<Integer, String>> {
    @Override
    public int compare(Vector.Node<Integer, String> node1, Vector.Node<Integer, String> node2) {
        return node1.key - node2.key;
    }
}
