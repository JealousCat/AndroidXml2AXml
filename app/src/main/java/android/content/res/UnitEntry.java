package android.content.res;
/**
 * Created by JealousCat on 2024-08-14.
 * &#064;email 3147359496@qq.com
 * <p>
 * 单位转换
 * </p>
 */
public class UnitEntry
{
    /**
     * 单位名
     */
    public String name;
    /**
     *单位名的长度
     */
    public int len;
    /**
     *单位类型
     */
    public byte type;
    /**
     *单位值
     */
    public int unit;
    /**
     *百分比缩放
     */
    public float scale;

    public UnitEntry(String n, int l,int t,int u,float s){
        name = n;
        len = l;
        type = (byte)t;
        unit = u;
        scale = s;
    }
};