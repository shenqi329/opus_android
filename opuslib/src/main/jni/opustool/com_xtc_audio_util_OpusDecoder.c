#include "config.h"
#ifdef ANDROID_V

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "com_xtc_audio_util_OpusDecoder.h"
#include "opus.h"
#include "log.h"
#include <errno.h>

#ifdef __cplusplus
extern "C" {
#endif
     JNIEXPORT jint JNICALL Java_com_xtc_audio_util_OpusDecoder_init
     (JNIEnv *env, jobject obj,jint sampleRate){
        int err;
        OpusDecoder *dec = opus_decoder_create(sampleRate, 1, &err);
        if (err != OPUS_OK) {
            LOGE("opus_decoder_create fail: %s\n", opus_strerror(err));
            dec = NULL;
            return -1;
        }
        return (jint)dec;
     }

     JNIEXPORT void JNICALL Java_com_xtc_audio_util_OpusDecoder_exit
     (JNIEnv *env, jobject obj,jint handle){
        OpusDecoder *dec = (OpusDecoder*)handle;
        opus_decoder_destroy(dec);
        return;
     }

    JNIEXPORT jint Java_com_xtc_audio_util_OpusDecoder_decode
                (JNIEnv *env, jobject obj,jint handle,jbyteArray opus,jint opusSize,jbyteArray out,jint frame_size){
        OpusDecoder *dec = (OpusDecoder*)handle;
        unsigned char* pOpus = (unsigned char*)(*env)->GetByteArrayElements(env,opus,NULL);
        unsigned char* pOut =  (unsigned char*)(*env)->GetByteArrayElements(env,out,NULL);

        int size = opus_decode(dec,pOpus,opusSize,(short *)pOut,frame_size,0);
        LOGD("opus_decode frame_size = %d size = %d",frame_size,size);

        return size;
    }

#ifdef __cplusplus
}
#endif

#endif
