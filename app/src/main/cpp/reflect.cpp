#include <jni.h>
#include <thread>
#include <string>
#include <sstream>
#include <fstream>
#include <future>
#include <android/log.h>

using namespace std;
JavaVM *vm_ = nullptr;

JNIEnv *attachCurrentThread() {
    JNIEnv *env;
    int res = vm_->AttachCurrentThread(&env, nullptr);
    __android_log_print(ANDROID_LOG_DEBUG, "native", "Found attached %d", res);
    return env;
}

void detachCurrentThread() {
    vm_->DetachCurrentThread();
}

static jobject getDeclaredMethod_internal(jobject clazz, jstring method_name, jobjectArray params) {
    JNIEnv *env = attachCurrentThread();
    jclass clazz_class = env->GetObjectClass(clazz);
    jmethodID get_declared_method_id = env->GetMethodID(clazz_class, "getDeclaredMethod",
                                                        "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;");
    jobject res = env->CallObjectMethod(clazz, get_declared_method_id, method_name, params);
    jobject global_res = nullptr;
    if (res != nullptr) {
        global_res = env->NewGlobalRef(res);
    }
    env->DeleteGlobalRef(clazz) ;
    env->DeleteGlobalRef(method_name) ;
    jthrowable throwable = env->ExceptionOccurred();
    if(throwable!= nullptr){
        env->ExceptionClear();
    }
    detachCurrentThread() ;
    return global_res;
}

static jobject getDeclaredMethods_internal(jobject clazz) {
    JNIEnv *env = attachCurrentThread();
    jclass clazz_class = env->GetObjectClass(clazz);
    jmethodID get_declared_method_id = env->GetMethodID(clazz_class, "getDeclaredMethods",
                                                        "()[Ljava/lang/reflect/Method;");
    jobject res = env->CallObjectMethod(clazz, get_declared_method_id);
    jobject global_res = nullptr;
    if (res != nullptr) {
        global_res = env->NewGlobalRef(res);
    }
    env->DeleteGlobalRef(clazz);
    jthrowable throwable = env->ExceptionOccurred();
    if(throwable!= nullptr){
        env->ExceptionClear();
    }
    detachCurrentThread();
    return global_res;
}

static jobject getDeclaredConstructor_internal(jobject clazz, jobjectArray params) {
    JNIEnv *env = attachCurrentThread();
    jclass clazz_class = env->GetObjectClass(clazz);
    jmethodID get_declared_method_id = env->GetMethodID(clazz_class, "getDeclaredConstructor",
                                                        "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;");
    jobject res = env->CallObjectMethod(clazz, get_declared_method_id, params);
    jobject global_res = nullptr;
    if (res != nullptr) {
        global_res = env->NewGlobalRef(res);
    }
    env->DeleteGlobalRef(clazz);
    jthrowable throwable = env->ExceptionOccurred();
    if(throwable!= nullptr){
        env->ExceptionClear();
    }
    detachCurrentThread();
    return global_res;
}

static jobject getDeclaredConstructors_internal(jobject clazz) {
    JNIEnv *env = attachCurrentThread();
    jclass clazz_class = env->GetObjectClass(clazz);
    jmethodID get_declared_method_id = env->GetMethodID(clazz_class, "getDeclaredConstructors",
                                                        "()[Ljava/lang/reflect/Constructor;");
    jobject res = env->CallObjectMethod(clazz, get_declared_method_id);
    jobject global_res = nullptr;
    if (res != nullptr) {
        global_res = env->NewGlobalRef(res);
    }
    env->DeleteGlobalRef(clazz);
    jthrowable throwable = env->ExceptionOccurred();
    if(throwable!= nullptr){
        env->ExceptionClear();
    }
    detachCurrentThread();
    return global_res;
}

static jobject getDeclaredField_internal(jobject object, jstring field_name) {

    JNIEnv *env = attachCurrentThread();
    jclass clazz_class = env->GetObjectClass(object);
    jmethodID methodId = env->GetMethodID(clazz_class, "getDeclaredField",
                                          "(Ljava/lang/String;)Ljava/lang/reflect/Field;");
    jobject res = env->CallObjectMethod(object, methodId, field_name);
    jobject global_res = nullptr;
    if (res != nullptr) {
        global_res = env->NewGlobalRef(res);
    }
    jthrowable throwable = env->ExceptionOccurred();
    if(throwable!= nullptr){
        env->ExceptionClear();
    }
    detachCurrentThread();
    return global_res;
}

static jobject getDeclaredFields_internal(jobject object) {
    JNIEnv *env = attachCurrentThread();
    jclass clazz_class = env->GetObjectClass(object);
    jmethodID methodId = env->GetMethodID(clazz_class, "getDeclaredFields",
                                          "()[Ljava/lang/reflect/Field;");
    jobject res = env->CallObjectMethod(object, methodId);
    jobject global_res = nullptr;
    if (res != nullptr) {
        global_res = env->NewGlobalRef(res);
    }
    jthrowable throwable = env->ExceptionOccurred();
    if(throwable!= nullptr){
        env->ExceptionClear();
    }
    detachCurrentThread();
    return global_res;
}

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    vm_ = vm ;
    return JNI_VERSION_1_6;
}


extern "C"
JNIEXPORT jobject JNICALL
Java_android_xml2axml_util_Reflect_getDeclaredMethod(JNIEnv *env, jclass clazz, jclass cls,
                                                     jstring method_name,
                                                     jobjectArray parameter_types) {
    std::thread test_thread ;
    jobject global_clazz = env->NewGlobalRef(cls) ;
    jstring global_method_name = static_cast<jstring>(env->NewGlobalRef(method_name)) ;
    jobjectArray global_params = nullptr;
    if (parameter_types != nullptr) {
        int arg_length = env->GetArrayLength(parameter_types);
        for (int i = 0; i < arg_length; i++) {
            jobject element = static_cast<jobject> (env->GetObjectArrayElement(parameter_types, i));
            jobject global_element = env->NewGlobalRef(element);
            env->SetObjectArrayElement(parameter_types, i, global_element);
        }
        global_params = (jobjectArray) env->NewGlobalRef(parameter_types);
    }
    auto future = std::async(&getDeclaredMethod_internal, global_clazz,
                             global_method_name,
                             global_params);
    auto result = future.get();
    if (global_params != nullptr) {
        env->DeleteGlobalRef(global_params) ;
    }
    return result ;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_android_xml2axml_util_Reflect_getDeclaredMethods(JNIEnv *env, jclass clazz, jclass cls) {
    std::thread test_thread ;
    jobject global_clazz = env->NewGlobalRef(cls) ;
    auto future = std::async(&getDeclaredMethods_internal, global_clazz);
    auto result = future.get();
    return (jobjectArray)result ;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_android_xml2axml_util_Reflect_getConstructor(JNIEnv *env, jclass clazz, jclass cls,
                                                  jobjectArray parameter_types) {
    std::thread test_thread ;
    jobject global_clazz = env->NewGlobalRef(cls) ;
    jobjectArray global_params = nullptr;
    if (parameter_types != nullptr) {
        int arg_length = env->GetArrayLength(parameter_types);
        for (int i = 0; i < arg_length; i++) {
            jobject element = static_cast<jobject> (env->GetObjectArrayElement(parameter_types, i));
            jobject global_element = env->NewGlobalRef(element);
            env->SetObjectArrayElement(parameter_types, i, global_element);
        }
        global_params = (jobjectArray) env->NewGlobalRef(parameter_types);
    }
    auto future = std::async(&getDeclaredConstructor_internal, global_clazz,global_params);
    auto result = future.get();
    if (global_params != nullptr) {
        env->DeleteGlobalRef(global_params) ;
    }
    return result ;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_android_xml2axml_util_Reflect_getConstructors(JNIEnv *env, jclass clazz, jclass cls) {
    std::thread test_thread ;
    jobject global_clazz = env->NewGlobalRef(cls) ;
    auto future = std::async(&getDeclaredConstructors_internal, global_clazz);
    auto result = future.get();
    return (jobjectArray)result ;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_android_xml2axml_util_Reflect_getDeclaredField(JNIEnv *env, jclass clazz, jclass cls,
                                                    jstring field_name) {
    auto global_clazz = env->NewGlobalRef(cls);
    jstring global_method_name = static_cast<jstring>(env->NewGlobalRef(field_name)) ;
    auto future = std::async(&getDeclaredField_internal, global_clazz, global_method_name);
    auto result = future.get();
    env->DeleteGlobalRef(global_clazz) ;
    env->DeleteGlobalRef(global_method_name) ;
    return result ;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_android_xml2axml_util_Reflect_getDeclaredFields(JNIEnv *env, jclass clazz, jclass cls) {
    auto global_clazz = env->NewGlobalRef(cls);
    auto future = std::async(&getDeclaredFields_internal, global_clazz);
    auto result = future.get();
    env->DeleteGlobalRef(global_clazz) ;
    return (jobjectArray)result ;
}