package android.xml2axml;

import android.os.Build;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/**
 * Created by JealousCat on 2024-08-10.
 * &#064;email 3147359496@qq.com
 * <P>
 * 字符串池
 */
public class StringPool {
    public ArrayList<String> pool = new ArrayList<>();
    public boolean hasEmpty = false;//标记已空字符只存储一次，减少比较

    /**
     * 存入字符串，不重复存入
     * @param val 字符串值
     */
    public void add(String val){
        if(val==null){
            val = "null";
        }

        if(val.isEmpty()){
            if (!hasEmpty){
                pool.add(val);
                hasEmpty = true;
            }
        }else{
            if(!pool.contains(val)){
                pool.add(val);
            }
        }
    }

    /**
     * 获取字符串池内的字符串
     * @param index 索引
     * @return 字符串
     */
    public String get(int index){
        int size = pool.size();
        if(index<0||index >=size){
            return null;
        }
        return pool.get(index);
    }

    /**
     * 移除字符串
     * @param val 要移除的字符串
     */
    public void remove(String val){
        pool.remove(val);
    }

    /**
     * 查找字符串，并返回其在字符串池中的索引
     * @param val 要查找的字符串
     * @return 索引
     */
    public int indexOf(String val){
        return pool.indexOf(val);
    }

    /**
     * 采用默认的排序方式对字符串池进行排序
     */
    public void sort(){
        Collections.sort(pool);
    }

    /**
     * 按规则对字符串池进行排序
     * @param comparator 排序方式
     */
    public void sort(Comparator<String> comparator){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pool.sort(comparator);
        }else{
            Collections.sort(pool,comparator);
        }
    }

    /**
     *字符串池大小
     */
    public int size(){
        return pool.size();
    }

    @NonNull
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<pool.size();i++){
            sb.append(i).append("> ").append(pool.get(i)).append('\n');
        }
        return sb.toString();
    }
}
