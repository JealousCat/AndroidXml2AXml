package android.xml2axml.util;

import android.os.Build;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflect {
    static {
        System.loadLibrary("reflect");
    }

    private final Class<?> target;

    public Reflect(Class<?> cls) {
        target = cls;
    }

    public static native Method getDeclaredMethod(Class<?> cls, String methodName, Class<?>[] parameterTypes) throws Exception;

    public static native Method[] getDeclaredMethods(Class<?> cls) throws Exception;

    public static native Constructor<?> getConstructor(Class<?> cls, Class<?>[] parameterTypes) throws Exception;

    public static native Constructor<?>[] getConstructors(Class<?> cls) throws Exception;

    public static native Field getDeclaredField(Class<?> cls, String fieldName) throws Exception;

    public static native Field[] getDeclaredFields(Class<?> cls) throws Exception;

    public Method getDeclaredMethod(String methodName, Class<?>[] parameterTypes) throws Exception {
        if (target != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                return Reflect.getDeclaredMethod(target, methodName, parameterTypes);
            } else {
                Method metaGetDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
                return (Method) metaGetDeclaredMethod.invoke(target, methodName, parameterTypes);
            }
        }
        return null;
    }

    public Method[] getDeclaredMethods() throws Exception {
        if (target != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                return Reflect.getDeclaredMethods(target);
            } else {
                Method metaGetDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethods");
                return (Method[]) metaGetDeclaredMethod.invoke(target);
            }
        }
        return null;
    }

    public Constructor<?> getConstructor(Class<?>[] parameterTypes) throws Exception {
        if (target != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                return Reflect.getConstructor(target, parameterTypes);
            } else {
                Method metaGetDeclaredMethod = Class.class.getDeclaredMethod("getConstructor", Class[].class);
                return (Constructor<?>) metaGetDeclaredMethod.invoke(target, (Object) parameterTypes);
            }
        }
        return null;
    }

    public Constructor<?>[] getConstructors() throws Exception {
        if (target != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                return Reflect.getConstructors(target);
            } else {
                Method metaGetDeclaredMethod = Class.class.getDeclaredMethod("getConstructors");
                return (Constructor<?>[]) metaGetDeclaredMethod.invoke(target);
            }
        }
        return null;
    }

    public Field getDeclaredField(String fieldName) throws Exception {
        if (target != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                return Reflect.getDeclaredField(target, fieldName);
            } else {
                Method metaGetDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredField", String.class);
                return (Field) metaGetDeclaredMethod.invoke(target, fieldName);
            }
        }
        return null;
    }

    public Field[] getDeclaredFields() throws Exception {
        if (target != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                return Reflect.getDeclaredFields(target);
            } else {
                Method metaGetDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredFields");
                return (Field[]) metaGetDeclaredMethod.invoke(target);
            }
        }
        return null;
    }
}
