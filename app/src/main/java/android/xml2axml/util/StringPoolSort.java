package android.xml2axml.util;

import java.util.Comparator;
/**
 * Created by JealousCat on 2024-08-17.
 * &#064;email 3147359496@qq.com
 * <p>
 * 对字符串池内的字符串进行排序，如果是属性名将优先排在前面
 **/
public class StringPoolSort implements Comparator<String> {

    private Vector<Integer,String> resids = null;
    public StringPoolSort(Vector<Integer,String> ids){
        this.resids = ids;
    }

    //attribute Name优先排在前面
    @Override
    public int compare(String o1, String o2) {
        int i1 = resids.indexOfValue(o1);
        int i2 = resids.indexOfValue(o2);
        if(i1!=-1 && i2!=-1){
            return Integer.compare(i1,i2);
        } else if (i1 != -1) {
            return -1;
        }else if (i2!=-1){
            return 1;
        }else{
            return 0;
        }
    }
}
