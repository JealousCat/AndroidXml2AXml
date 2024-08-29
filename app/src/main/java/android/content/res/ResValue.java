package android.content.res;

import android.content.Context;
import android.util.SparseArray;
import android.util.TypedValue;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by JealousCat on 2024-08-14.
 * &#064;email 3147359496@qq.com
 * <p>
 * 资源值
 */
public class ResValue {
    /**
     * 资源值大小
     */
    public short size = 8;
    /**
     *默认0
     */
    public byte res0 = 0;
    /**
     *资源值类型
     */
    public byte dataType;
    /**
     *整数资源值
     */
    public int data;
    /**
     *浮点数资源值
     */
    public float data_f;
    /**
     *资源值类型
     */
    public static final int TYPE_NULL = 0x00,
            TYPE_REFERENCE = 0x01,
            TYPE_ATTRIBUTE = 0x02,
            TYPE_STRING = 0x03,
            TYPE_FLOAT = 0x04,
            TYPE_DIMENSION = 0x05,
            TYPE_FRACTION = 0x06,
            TYPE_DYNAMIC_REFERENCE = 0x07,
            TYPE_DYNAMIC_ATTRIBUTE = 0x08,
            TYPE_FIRST_INT = 0x10,
            TYPE_INT_DEC = 0x10,
            TYPE_INT_HEX = 0x11,
            TYPE_INT_BOOLEAN = 0x12,
            TYPE_FIRST_COLOR_INT = 0x1c,
            TYPE_INT_COLOR_ARGB8 = 0x1c,
            TYPE_INT_COLOR_RGB8 = 0x1d,
            TYPE_INT_COLOR_ARGB4 = 0x1e,
            TYPE_INT_COLOR_RGB4 = 0x1f,
            TYPE_LAST_COLOR_INT = 0x1f,
            TYPE_LAST_INT = 0x1f;
    /**
     *整数资源值的类型
     */
    public static final int COMPLEX_UNIT_SHIFT = 0,
            COMPLEX_UNIT_MASK = 0xf,
            COMPLEX_UNIT_PX = 0,
            COMPLEX_UNIT_DIP = 1,
            COMPLEX_UNIT_SP = 2,
            COMPLEX_UNIT_PT = 3,
            COMPLEX_UNIT_IN = 4,
            COMPLEX_UNIT_MM = 5,
            COMPLEX_UNIT_FRACTION = 0,
            COMPLEX_UNIT_FRACTION_PARENT = 1,
            COMPLEX_RADIX_SHIFT = 4,
            COMPLEX_RADIX_MASK = 0x3,
            COMPLEX_RADIX_23p0 = 0,
            COMPLEX_RADIX_16p7 = 1,
            COMPLEX_RADIX_8p15 = 2,
            COMPLEX_RADIX_0p23 = 3,
            COMPLEX_MANTISSA_SHIFT = 8,
            COMPLEX_MANTISSA_MASK = 0xffffff;

    public static final int DATA_NULL_UNDEFINED = 0,
            DATA_NULL_EMPTY = 1;
    /**
     *资源值字节大小
     */
    public int sizeof(){
        return 2+2+4;
    }
    /**
     *写出资源值
     */
    public void write(ByteBuffer buf){
        buf.putShort(size);
        buf.put(res0);
        buf.put(dataType);
        if(dataType!=TYPE_FLOAT){
            buf.putInt(data);
        }else{
            buf.putFloat(data_f);
        }
    }
    /**
     *当前app资源包索引
     */
    public static final int APP_PACKAGE_ID = 0x7f;
    /**
     *系统资源包索引
     */
    public static final int SYS_PACKAGE_ID = 0x01;
    /**
     *字符串转资源值
     */
    public static boolean stringToValue(Context context, AttributeEntry entry, String[] defPackage) {
        ResValue outValue = entry.value;
        int attrType = ResMap.TYPE_ANY;
        int attrID = entry.nameResId;
        char[] s = entry.string.toCharArray();
        int len = s.length;

        int attrMin = 0x80000000, attrMax = 0x7fffffff;

        if (attrID != 0 && !ResMap.Res_INTERNALID(attrID)) {
            System.out.println("attrID!=0 entry string:"+entry.string);
            int p = getResourcePackageIndex(context, attrID);
            ArrayList<ResMap> map = new ArrayList<>();
            int bag = 0;
            int cnt = p >= 0 ? lockBag(context, attrID, map) : -1;
            if (cnt >= 0) {
                while (cnt > 0) {
                    switch (map.get(bag).name_ident) {
                        case ResMap.ATTR_TYPE:
                            attrType = map.get(bag).value.data;
                            break;
                        case ResMap.ATTR_MIN:
                            attrMin = map.get(bag).value.data;
                            break;
                        case ResMap.ATTR_MAX:
                            attrMax = map.get(bag).value.data;
                            break;
                        case ResMap.ATTR_L10N:
                            break;
                    }
                    bag++;
                    cnt--;
                }
            }
        }

        System.out.println("attrType:"+attrType);

        boolean canStringCoerce = (attrType & ResMap.TYPE_STRING) != 0;
        if (len==0){
            outValue.dataType = ResValue.TYPE_STRING;
            return true;
        }
        if (s[0] == '@') {
            outValue.dataType = ResValue.TYPE_REFERENCE;
            if (len == 5 && s[1] == 'n' && s[2] == 'u' && s[3] == 'l' && s[4] == 'l') {
                outValue.data = 0;
                return true;
            } else if (len == 6 && s[1] == 'e' && s[2] == 'm' && s[3] == 'p' && s[4] == 't' && s[5] == 'y') {
                outValue.dataType = ResValue.TYPE_NULL;
                outValue.data = ResValue.DATA_NULL_EMPTY;
                return true;
            } else {
                int resourceRefName = 0;
                int resourceNameLen;
                if (len > 2 && s[1] == '+') {
                    resourceRefName = 2;
                    resourceNameLen = len - 2;
                } else if (len > 2 && s[1] == '*') {
                    resourceRefName = 2;
                    resourceNameLen = len - 2;
                } else {
                    resourceRefName = 1;
                    resourceNameLen = len - 1;
                }
                String[] results = {null, null, null, "android"};
                if (!expandResourceRef(new String(s, resourceRefName, resourceNameLen), resourceNameLen, results, defPackage[0])) {
                    return false;
                }
                results[1] = results[1] == null ? "id" : results[1];
                results[2] = results[2] == null ? entry.string : results[2];
                String packageName = results[0];
                String type = results[1];
                String name = results[2];
                Resources resources = context.getResources();
                int rid = resources.getIdentifier(name, type, packageName);
                if (rid != 0) {
                    int packageId = ResMap.Res_GETPACKAGE(rid) + 1;
                    if (packageId != APP_PACKAGE_ID && packageId != SYS_PACKAGE_ID) {
                        outValue.dataType = ResValue.TYPE_DYNAMIC_REFERENCE;
                    }
                    outValue.data = rid;
                    return true;
                }

            }
            return false;
        }


        if (s[0] == '#') {
            int color = 0;
            boolean[] error = {false};
            if (len == 4) {
                outValue.dataType = ResValue.TYPE_INT_COLOR_RGB4;
                color |= 0xFF000000;
                color |= get_hex(s[1], error) << 20;
                color |= get_hex(s[1], error) << 16;
                color |= get_hex(s[2], error) << 12;
                color |= get_hex(s[2], error) << 8;
                color |= get_hex(s[3], error) << 4;
                color |= get_hex(s[3], error);
            } else if (len == 5) {
                outValue.dataType = ResValue.TYPE_INT_COLOR_ARGB4;
                color |= get_hex(s[1], error) << 28;
                color |= get_hex(s[1], error) << 24;
                color |= get_hex(s[2], error) << 20;
                color |= get_hex(s[2], error) << 16;
                color |= get_hex(s[3], error) << 12;
                color |= get_hex(s[3], error) << 8;
                color |= get_hex(s[4], error) << 4;
                color |= get_hex(s[4], error);
            } else if (len == 7) {
                outValue.dataType = ResValue.TYPE_INT_COLOR_RGB8;
                color |= 0xFF000000;
                color |= get_hex(s[1], error) << 20;
                color |= get_hex(s[2], error) << 16;
                color |= get_hex(s[3], error) << 12;
                color |= get_hex(s[4], error) << 8;
                color |= get_hex(s[5], error) << 4;
                color |= get_hex(s[6], error);
            } else if (len == 9) {
                outValue.dataType = ResValue.TYPE_INT_COLOR_ARGB8;
                color |= get_hex(s[1], error) << 28;
                color |= get_hex(s[2], error) << 24;
                color |= get_hex(s[3], error) << 20;
                color |= get_hex(s[4], error) << 16;
                color |= get_hex(s[5], error) << 12;
                color |= get_hex(s[6], error) << 8;
                color |= get_hex(s[7], error) << 4;
                color |= get_hex(s[8], error);
            } else {
                error[0] = true;
            }
            if (!error[0]) {
                if ((attrType & ResMap.TYPE_COLOR) == 0) {
                    if (!canStringCoerce) {
                        return false;
                    }
                } else {
                    outValue.data = color;
                    return true;
                }
            } else {
                if ((attrType & ResMap.TYPE_COLOR) != 0) {
                    return false;
                }
            }
        }

        if (s[0] == '?') {
            outValue.dataType = ResValue.TYPE_ATTRIBUTE;
            String[] results = {null, null, null, "attr"};
            if (!expandResourceRef(new String(s, 1, len - 1), len - 1, results, defPackage[0])) {
                return false;
            }
            results[1] = results[1] == null ? "id" : results[1];
            results[2] = results[2] == null ? entry.string : results[2];
            String packageName = results[0];
            String type = results[1];
            String name = results[2];
            Resources resources = context.getResources();

            int rid = resources.getIdentifier(name, type, packageName);
            if (rid != 0) {
                int packageId = ResMap.Res_GETPACKAGE(rid) + 1;
                if (packageId != APP_PACKAGE_ID && packageId != SYS_PACKAGE_ID) {
                    outValue.dataType = ResValue.TYPE_DYNAMIC_ATTRIBUTE;
                }
                outValue.data = rid;
                return true;
            }

            return false;
        }

        if (stringToInt(s, len, outValue)) {
            if ((attrType & ResMap.TYPE_INTEGER) == 0) {
                if (!canStringCoerce && (attrType & ResMap.TYPE_FLOAT) == 0) {
                    return false;
                }
            } else {
                return outValue.data >= attrMin && outValue.data <= attrMax;
            }
        }

        if (stringToFloat(s, len, outValue)) {
            if (outValue.dataType == ResValue.TYPE_DIMENSION) {
                if ((attrType & ResMap.TYPE_DIMENSION) != 0) {
                    return true;
                }
                if (!canStringCoerce) {
                    return false;
                }
            } else if (outValue.dataType == ResValue.TYPE_FRACTION) {
                if ((attrType & ResMap.TYPE_FRACTION) != 0) {
                    return true;
                }
                if (!canStringCoerce) {
                    return false;
                }
            } else if ((attrType & ResMap.TYPE_FLOAT) == 0) {
                if (!canStringCoerce) {
                    return false;
                }
            } else {
                return true;
            }
        }

        if (len == 4) {
            if ((s[0] == 't' || s[0] == 'T') &&
                    (s[1] == 'r' || s[1] == 'R') &&
                    (s[2] == 'u' || s[2] == 'U') &&
                    (s[3] == 'e' || s[3] == 'E')) {
                if ((attrType & ResMap.TYPE_BOOLEAN) == 0) {
                    if (!canStringCoerce) {
                        return false;
                    }
                } else {
                    outValue.dataType = ResValue.TYPE_INT_BOOLEAN;
                    outValue.data = -1;
                    return true;
                }
            }
        }

        if (len == 5) {
            if ((s[0] == 'f' || s[0] == 'F') &&
                    (s[1] == 'a' || s[1] == 'A') &&
                    (s[2] == 'l' || s[2] == 'L') &&
                    (s[3] == 's' || s[3] == 'S') &&
                    (s[4] == 'e' || s[4] == 'E')) {
                if ((attrType & ResMap.TYPE_BOOLEAN) == 0) {
                    if (!canStringCoerce) {
                        return false;
                    }
                } else {
                    outValue.dataType = ResValue.TYPE_INT_BOOLEAN;
                    outValue.data = 0;
                    return true;
                }
            }
        }

        if ((attrType & ResMap.TYPE_ENUM) != 0) {
            System.out.println("enum:");
            int p = getResourcePackageIndex(context, attrID);
            ArrayList<ResMap> map = new ArrayList<>();
            int bag = 0;
            int cnt = p >= 0 ? lockBag(context, attrID, map) : -1;
            if (cnt >= 0) {
                String[] rname = {null};
                while (cnt > 0) {
                    if (!ResMap.Res_INTERNALID(map.get(bag).name_ident)) {
                        if (getResourceName(context, map.get(bag).name_ident, rname)) {
                            if (strzcmp(s, 0, len, rname[0])) {
                                outValue.dataType = map.get(bag).value.dataType;
                                outValue.data = map.get(bag).value.data;
                                return true;
                            }
                        }
                    }
                    bag++;
                    cnt--;
                }
            }
        }

        if ((attrType & ResMap.TYPE_FLAGS) != 0) {
            System.out.println("flags:");
            int p = getResourcePackageIndex(context, attrID);
            ArrayList<ResMap> map = new ArrayList<>();
            int bag = 0;
            int cnt = p >= 0 ? lockBag(context, attrID, map) : -1;
            if (cnt >= 0) {
                boolean failed = false;
                String[] rname = {null};
                outValue.dataType = ResValue.TYPE_INT_HEX;
                outValue.data = 0;
                int pos = 0;
                while (pos < len && !failed) {
                    int start = pos;
                    pos++;
                    while (pos < len && s[pos] != '|') {
                        pos++;
                    }
                    int bagi = bag;
                    int i;
                    for (i = 0; i < cnt; i++, bagi++) {
                        if (!ResMap.Res_INTERNALID(map.get(bagi).name_ident)) {
                            if (getResourceName(context, map.get(bagi).name_ident, rname)) {
                                if (strzcmp(s, start, pos - start, rname[0])) {
                                    outValue.data |= map.get(bagi).value.data;
                                    break;
                                }
                            }
                        }
                    }
                    if (i >= cnt) {
                        failed = true;
                    }
                    if (pos < len) {
                        pos++;
                    }
                }
                if (!failed) {
                    return true;
                }
            }
        }

        if ((attrType & ResMap.TYPE_STRING) == 0) {
            return false;
        }
        System.out.println("string entry string:"+entry.string);
        outValue.dataType = ResValue.TYPE_STRING;
        if (!entry.string.isEmpty()) {
            return collectString(entry, s, len);
        }

        return true;
    }
    /**
     *重新整理字符串原值
     */
    private static boolean collectString(AttributeEntry e, char[] s, int len) {
        StringBuilder tmp = new StringBuilder();
        int s_pos = 0;
        int p = s_pos;
        while (p < (s_pos + len)) {
            while (p < (s_pos + len)) {
                char c = s[p];
                if (c == '\\') {
                    break;
                }
                p++;
            }
            if (p < (s_pos + len)) {
                if (p > s_pos) {
                    tmp.append(new String(s, 0, p - s_pos));
                }
                if (s[p] == '\\') {
                    p++;
                    if (p < (s_pos + len)) {
                        switch (s[p]) {
                            case 't':
                                tmp.append("\t");
                                break;
                            case 'n':
                                tmp.append("\n");
                                break;
                            case '#':
                                tmp.append("#");
                                break;
                            case '@':
                                tmp.append("@");
                                break;
                            case '?':
                                tmp.append("?");
                                break;
                            case '"':
                                tmp.append("\"");
                                break;
                            case '\'':
                                tmp.append("'");
                                break;
                            case '\\':
                                tmp.append("\\");
                                break;
                            case 'u': {
                                int chr = 0;
                                int i = 0;
                                while (i < 4 && p < s.length - 1) {
                                    p++;
                                    i++;
                                    int c;
                                    if (s[p] >= '0' && s[p] <= '9') {
                                        c = s[p] - '0';
                                    } else if (s[p] >= 'a' && s[p] <= 'f') {
                                        c = s[p] - 'a' + 10;
                                    } else if (s[p] >= 'A' && s[p] <= 'F') {
                                        c = s[p] - 'A' + 10;
                                    } else {
                                        return false;
                                    }
                                    chr = (chr << 4) | c;
                                }
                                tmp.append((char) chr);
                            }
                            break;
                            default:
                                break;
                        }
                        p++;
                    }
                }
                len -= (p - s_pos);
                s_pos = p;
            }
        }

        if (tmp.length() > 0) {
            if (len > 0) {
                tmp.append(new String(s, s_pos, len));
            }
            e.string = tmp.toString();
        } else {
            e.string = new String(s, s_pos, len);
        }

        return true;
    }
    /**
     *比较资源名字符串
     */
    private static boolean strzcmp(char[] s, int offset, int len, String name) {
        String x = new String(s, offset, len);
        if(name.startsWith("android:id/")){
            return ("android:id/"+x).equals(name);
        }
        return name.endsWith(x);
    }
    /**
     *资源名获取
     */
    private static boolean getResourceName(Context context, int resId, String[] results) {
        try {
            Resources resources = context.getResources();
            results[0] = resources.getResourceName(resId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    /**
     *HEX检查
     */
    public static int get_hex(char c, boolean[] outError) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        } else if (c >= 'a' && c <= 'f') {
            return c - 'a' + 0xa;
        } else if (c >= 'A' && c <= 'F') {
            return c - 'A' + 0xa;
        }
        outError[0] = true;
        return 0;
    }
    /**
     *资源包列表
     */
    public static SparseArray<String> resPackages = null;
    /**
     *获取资源包索引
     */
    private static int getResourcePackageIndex(Context context, int resID) {
        if (resPackages == null) {
            AssetManager assetManager = context.getAssets();
            try {
                Method m = AssetManager.class.getDeclaredMethod("getAssignedPackageIdentifiers");
                m.setAccessible(true);
                Object o = m.invoke(assetManager);
                if (o != null) {
                    resPackages = (SparseArray<String>) o;
                }
            } catch (Exception ignored) {
            }
        }
        if (resPackages == null) {
            return -75;//BAD_INDEX;
        }
        int packageId = ResMap.Res_GETPACKAGE(resID) + 1;
        if (resPackages.get(packageId) != null) {
            return packageId;
        }
        return -75;
    }
    /**
     *获取系统资源Bag
     */
    private static int lockBag(Context context, int resID, ArrayList<ResMap> bag) {
        AssetManager assetManager = context.getAssets();
        try {
            Method[] ms = AssetManager.class.getDeclaredMethods();
            Method m = null;
            for(Method method:ms){
                if(method.getName().equals("getStyleAttributes")){
                    m = method;
                    break;
                }
            }
            if(m==null){
                throw new RuntimeException("当前运行环境中android.content.res.AssetManager类没有getStyleAttributes方法");
            }
            m.setAccessible(true);
            Object o = m.invoke(assetManager, resID);
            if (o == null) {
                return -75;//ERROR
            }
            Resources resources = context.getResources();
            TypedArray array = resources.obtainTypedArray(resID);
            int[] name_ident_array = (int[]) o;
            TypedValue value = new TypedValue();
            for (int i = 0; i < name_ident_array.length; i++) {
                ResMap map = new ResMap();
                map.name_ident = name_ident_array[i];
                array.getValue(i, value);
                map.value.dataType = (byte) value.type;
                map.value.data = value.data;
                bag.add(map);
            }
            array.close();
            return name_ident_array.length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -75;
    }
    /**
     *资源索引解析
     */
    private static boolean expandResourceRef(String refStr, int refLen, String[] results, String defPackage) {
        char[] ref = refStr.toCharArray();
        int packageEnd = 0;
        int typeEnd = 0;
        int p = 0;
        while (p < refLen) {
            if (ref[p] == ':') {
                packageEnd = p;
            } else if (ref[p] == '/') {
                typeEnd = p;
                break;
            }
            p++;
        }
        p = 0;
        if (ref[p] == '@') {
            p++;
        }
        if (ref[p] == '*') {
            p++;
        }
        if (packageEnd != 0) {
            results[0] = new String(ref, p, packageEnd - p);
            p = packageEnd + 1;
        } else {
            if (defPackage == null) {
                return false;
            }
            results[0] = defPackage;
        }
        if (typeEnd != 0) {
            results[1] = new String(ref, p, typeEnd - p);
            p = typeEnd + 1;
        } else {
            if (results[3] == null) {
                return false;
            }
            results[1] = results[3];
        }
        results[2] = new String(ref, p, refLen - p);
        if (results[0] == null || results[0].isEmpty()) {
            return false;
        }
        if (results[1] == null || results[1].isEmpty()) {
            return false;
        }
        return !results[2].isEmpty();
    }

    /**
     *字符串转整数
     */
    private static boolean stringToInt(char[] s, int len, ResValue outValue) {
        int s_pos = 0;
        while (len > 0 && (s[s_pos]<=32)) {
            s_pos++;
            len--;
        }

        if (len <= 0) {
            return false;
        }

        int i = 0;
        long val = 0;
        boolean neg = false;

        if (s[s_pos] == '-') {
            neg = true;
            i++;
        }

        if (s[i] < '0' || s[i] > '9') {
            return false;
        }

        // Decimal or hex?
        boolean isHex;
        if (len > 1 && s[i] == '0' && s[i + 1] == 'x') {
            isHex = true;
            i += 2;

            if (neg) {
                return false;
            }

            if (i == len) {
                return false;
            }

            try {
                String hex = new String(s, i, s.length - i);
                long l = Long.parseLong(hex, 16);
                if (l > Integer.MAX_VALUE) {
                    return false;
                }
                val = (int) l;
            } catch (Exception e) {
                return false;
            }
        } else {
            isHex = false;
            try {
                String hex = new String(s, i, s.length - i);
                long l = Long.parseLong(hex, 10);
                if (l > Integer.MAX_VALUE) {
                    return false;
                }
                val = (int) l;
            } catch (Exception e) {
                return false;
            }
        }

        if (neg) {
            val = -val;
        }

        while (i < len && s[i]<=32) {
            i++;
        }

        if (i != len) {
            return false;
        }

        if (outValue != null) {
            outValue.dataType = (byte) (isHex ? ResValue.TYPE_INT_HEX : ResValue.TYPE_INT_DEC);
            outValue.data = (int) val;
        }
        return true;
    }

    /**
     *单位
     */
    private static final UnitEntry[] unitNames = {
            new UnitEntry("px", 2, ResValue.TYPE_DIMENSION, ResValue.COMPLEX_UNIT_PX, 1.0f),
            new UnitEntry("dip", 3, ResValue.TYPE_DIMENSION, ResValue.COMPLEX_UNIT_DIP, 1.0f),
            new UnitEntry("dp", 2, ResValue.TYPE_DIMENSION, ResValue.COMPLEX_UNIT_DIP, 1.0f),
            new UnitEntry("sp", 2, ResValue.TYPE_DIMENSION, ResValue.COMPLEX_UNIT_SP, 1.0f),
            new UnitEntry("pt", 2, ResValue.TYPE_DIMENSION, ResValue.COMPLEX_UNIT_PT, 1.0f),
            new UnitEntry("in", 2, ResValue.TYPE_DIMENSION, ResValue.COMPLEX_UNIT_IN, 1.0f),
            new UnitEntry("mm", 2, ResValue.TYPE_DIMENSION, ResValue.COMPLEX_UNIT_MM, 1.0f),
            new UnitEntry("%", 1, ResValue.TYPE_FRACTION, ResValue.COMPLEX_UNIT_FRACTION, 1.0f / 100),
            new UnitEntry("%p", 2, ResValue.TYPE_FRACTION, ResValue.COMPLEX_UNIT_FRACTION_PARENT, 1.0f / 100),
    };

    /**
     *单位转换
     */
    private static boolean parse_unit(ResValue outValue, float[] outScale, String end) {
        UnitEntry[] cur = unitNames;
        int cur_pos = 0;
        while (cur_pos < cur.length) {
            if (cur[cur_pos].name.equals(end)) {
                outValue.dataType = cur[cur_pos].type;
                outValue.data = cur[cur_pos].unit << ResValue.COMPLEX_UNIT_SHIFT;
                outScale[0] = cur[cur_pos].scale;
                return true;
            }
            cur_pos++;
        }
        return false;
    }

    /**
     *字符串转单精浮点数
     */
    private static boolean stringToFloat(char[] s, int len, ResValue outValue) {
        String end = null;
        switch (s[len - 1]) {
            case 'x': {
                if (s[len - 2] == 'p') {
                    end = "px";
                }
                break;
            }
            case 'p': {
                switch (s[len - 2]) {
                    case 'i': {
                        if (s[len - 3] == 'd') {
                            end = "dip";
                        }
                        break;
                    }
                    case 'd': {
                        end = "dp";
                        break;
                    }
                    case 's': {
                        end = "sp";
                        break;
                    }
                    case '%': {
                        end = "%p";
                        break;
                    }
                }
                break;
            }
            case 't': {
                if (s[len - 2] == 'p') {
                    end = "pt";
                }
                break;
            }
            case 'n': {
                if (s[len - 2] == 'i') {
                    end = "in";
                }
                break;
            }
            case 'm': {
                if (s[len - 2] == 'm') {
                    end = "mm";
                }
                break;
            }
            case '%': {
                end = "%";
                break;
            }
        }
        int unit_len;
        if (end == null) {
            unit_len = 0;
        } else {
            unit_len = end.length();
        }
        float f = 0.0f;
        String fl = new String(s, 0, len - unit_len).trim();
        try {
            f = Float.parseFloat(fl);
        } catch (Exception e) {
            return false;
        }
        if (end != null) {
            float[] scale = {0f};
            if (parse_unit(outValue, scale, end)) {
                f *= scale[0];
                boolean neg = f < 0;
                if (neg) {
                    f = -f;
                }
                long bits = (long) (f * (1 << 23) + .5f);
                int radix;
                int shift;
                if ((bits & 0x7fffff) == 0) {
                    radix = ResValue.COMPLEX_RADIX_23p0;
                    shift = 23;
                } else if ((bits & 0xffffffffff800000L) == 0) {
                    radix = ResValue.COMPLEX_RADIX_0p23;
                    shift = 0;
                } else if ((bits & 0xffffffff80000000L) == 0) {
                    radix = ResValue.COMPLEX_RADIX_8p15;
                    shift = 8;
                } else if ((bits & 0xffffff8000000000L) == 0) {
                    radix = ResValue.COMPLEX_RADIX_16p7;
                    shift = 16;
                } else {
                    radix = ResValue.COMPLEX_RADIX_23p0;
                    shift = 23;
                }
                int mantissa = (int) ((bits >> shift) & ResValue.COMPLEX_MANTISSA_MASK);
                if (neg) {
                    mantissa = (-mantissa) & ResValue.COMPLEX_MANTISSA_MASK;
                }
                outValue.data |= (radix << ResValue.COMPLEX_RADIX_SHIFT) | (mantissa << ResValue.COMPLEX_MANTISSA_SHIFT);
                return true;
            }
        }
        if (outValue != null) {
            outValue.dataType = (byte) ResValue.TYPE_FLOAT;
            outValue.data_f = f;
            outValue.data = Float.floatToRawIntBits(f);
            return true;
        }
        return false;
    }

}
