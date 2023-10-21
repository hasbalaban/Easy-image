#include <jni.h>
#include <string>


extern "C"
JNIEXPORT jint JNICALL
Java_com_example_easy_1image_view_MainActivity_myNativeFunction(JNIEnv* env, jobject obj, jint a, jint b) {
        for (int i = 0; i <= 10000; i++) {
            if (i == 999) {
                return i;
            }
        }
        return 0;
}