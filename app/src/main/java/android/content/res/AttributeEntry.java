package android.content.res;
/**
 * Created by JealousCat on 2024-08-14.
 * &#064;email 3147359496@qq.com
 * <p>
 *  属性键值对
 * </p>
 */
public class AttributeEntry {
    /**
     * 属性的命名空间前缀
     */
    public String prefix = "";
    /**
     *属性的命名空间
     */
    public String ns = "";
    /**
     *属性名
     */
    public String name = "";
    /**
     *属性值的字符串形式
     */
    public String string = "";
    /**
     *属性值
     */
    public ResValue value = new ResValue();
    /**
     *属性索引
     */
    public int index;
    /**
     *属性名的资源ID
     */
    public int nameResId;

    public AttributeEntry() {
        index = ~0;
        nameResId = 0;
        value.dataType = ResValue.TYPE_NULL;
    }

    /**
     *是否需要字符串作为原值
     */
    public boolean needStringValue(){
        return nameResId == 0
                || value.dataType == ResValue.TYPE_NULL
                || value.dataType == ResValue.TYPE_STRING;
    }
}
