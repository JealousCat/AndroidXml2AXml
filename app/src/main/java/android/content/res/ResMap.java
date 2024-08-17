package android.content.res;
/**
 * Created by JealousCat on 2024-08-14.
 * &#064;email 3147359496@qq.com
 * <p></p>
 * entry资源解析所得的元素
 */
public class ResMap {
    /**
     * 资源元素名索引
     */
    public int name_ident;

    public static final int ATTR_TYPE = (0x01000000 | (0 & 0xFFFF)),

    ATTR_MIN = (0x01000000 | (1 & 0xFFFF)),

    ATTR_MAX = (0x01000000 | (2 & 0xFFFF)),

    ATTR_L10N = (0x01000000 | (3 & 0xFFFF)),

    ATTR_OTHER = (0x01000000 | (4 & 0xFFFF)),
            ATTR_ZERO = (0x01000000 | (5 & 0xFFFF)),
            ATTR_ONE = (0x01000000 | (6 & 0xFFFF)),
            ATTR_TWO = (0x01000000 | (7 & 0xFFFF)),
            ATTR_FEW = (0x01000000 | (8 & 0xFFFF)),
            ATTR_MANY = (0x01000000 | (9 & 0xFFFF));

    public static final int TYPE_ANY = 0x0000FFFF,

    TYPE_REFERENCE = 1 << 0,

    TYPE_STRING = 1 << 1,

    TYPE_INTEGER = 1 << 2,

    TYPE_BOOLEAN = 1 << 3,

    TYPE_COLOR = 1 << 4,

    TYPE_FLOAT = 1 << 5,

    TYPE_DIMENSION = 1 << 6,

    TYPE_FRACTION = 1 << 7,

    TYPE_ENUM = 1 << 16,

    TYPE_FLAGS = 1 << 17;

    public static final int L10N_NOT_REQUIRED = 0,
            L10N_SUGGESTED = 1;

    public ResValue value = new ResValue();

    public static boolean Res_INTERNALID(int resid) {
        return ((resid & 0xFFFF0000) != 0 && (resid & 0xFF0000) == 0);
    }

    public static int Res_GETPACKAGE(int id) {
        return ((id >> 24) - 1);
    }
}
