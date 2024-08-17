package android.xml2axml.util;

import java.util.ArrayList;
/**
 * Created by JealousCat on 2024-08-09.
 * &#064;email 3147359496@qq.com
 * <P>
 * 键值对列表，可存储键值对或者单独存储值，支持以索引、key、value三种形式获得对应元素
 */
public class Vector<K,V> extends ArrayList<Vector.Node<K,V>> {
    V defaultV;

    public Vector(V defaultValue){
        super();
        defaultV = defaultValue;
    }

    public Vector(){
        super();
    }

    public int indexOfKey(K key){
        for(int i=0;i<size();i++){
            Node<K,V> node = get(i);
            if(node!=null&&node.key!=null&&node.key.equals(key)){
                return i;
            }
        }
        return -1;
    }

    public boolean containsKey(K key){
        return indexOfKey(key)>=0;
    }

    public boolean containsValue(V value){
        for(int i=0;i<size();i++){
            Node<K,V> node = get(i);
            if(node!=null&&node.value!=null&&node.value.equals(value)){
                return true;
            }
        }
        return false;
    }

    public int indexOf(Node<K,V> node){
        if(node==null){
            return -1;
        }
        for(int i=0;i<size();i++){
           if(node.equals(get(i))){
               return i;
           }
        }
        return -1;
    }

    public V itemAt(int index){
        return valueAt(index);
    }

    public V valueAt(int index){
        if(index<0||index>=size()){
            return null;
        }
        Node<K,V> node = get(index);
        if(node!=null){
            return node.value;
        }
        return null;
    }

    public V valueFor(K key){
        int index = indexOfKey(key);
        if(index<0){
            return defaultV;
        }
        return get(index).value;
    }

    public K keyFor(V value){
        for(int i=0;i<size();i++){
            Node<K,V> node = get(i);
            if(node!=null&&node.value!=null&&node.value.equals(value)){
                return node.key;
            }
        }
        return null;
    }

    public int add(K key,V value){
        super.add(new Node<>(key,value));
        return size()-1;
    }

    public int put(V value){
        super.add(new Node<>(null,value));
        return size()-1;
    }

    public void pop(){
        int len = size();
        if(len>0) {
            remove(len-1);
        }
    }

    public void clear(){
        super.clear();
        defaultV = null;
    }

    public Node<K,V> removeItem(K key){
        int index = indexOfKey(key);
        if(index!=-1){
            return super.remove(index);
        }
        return null;
    }

    public Node<K,V> removeAt(int index){
        if(index<0||index>=size()){
            return null;
        }
        return remove(index);
    }

    public boolean removeAt(Node<K,V> item){
        return remove(item);
    }

    public void replaceAt(V value, int index){
        if(index<0||index>=size()){
            return;
        }
        Node<K,V> node = get(index);
        node.value = value;
    }

    public String toString(){
        int len = size();
        StringBuilder s = new StringBuilder("\n");
        for(int n=0;n<len;n++){
            Node<K,V> node = get(n);
            s.append("index=").append(n).append(", ").append(node).append("\n");
        }
        return s.toString();
    }

    public static class Node<K,V> {
        public K key;
        public V value;
        public Node(K key, V value){
            this.key = key;
            this.value = value;
        }

        public String toString(){
            return "key="+key+", value="+value;
        }
    }
}
