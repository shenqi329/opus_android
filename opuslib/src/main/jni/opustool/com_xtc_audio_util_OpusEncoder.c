#include "config.h"
#ifdef ANDROID_V

#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "com_xtc_audio_util_OpusEncoder.h"
#include "opus.h"
#include "log.h"
#include <errno.h>

#ifdef __cplusplus
extern "C" {
#endif

     JNIEXPORT jlong JNICALL Java_com_xtc_audio_util_OpusEncoder_init
     (JNIEnv *env, jobject obj,jint sampleRate,jint bitRate){
        int err;
        opus_int32 skip = 0;
        OpusEncoder *enc = opus_encoder_create(sampleRate,1,OPUS_APPLICATION_AUDIO,&err);
        if(err != OPUS_OK){
            LOGE("opus_encoder_create fail:%s",opus_strerror(err));
            return 0;
        }
        opus_encoder_ctl(enc, OPUS_SET_BANDWIDTH(OPUS_AUTO));
        opus_encoder_ctl(enc, OPUS_SET_BITRATE(bitRate));
        opus_encoder_ctl(enc, OPUS_SET_VBR(1));
        opus_encoder_ctl(enc, OPUS_SET_COMPLEXITY(10));
        opus_encoder_ctl(enc, OPUS_SET_INBAND_FEC(0));
        opus_encoder_ctl(enc, OPUS_SET_FORCE_CHANNELS(OPUS_AUTO));
        opus_encoder_ctl(enc, OPUS_SET_DTX(0));
        opus_encoder_ctl(enc, OPUS_SET_PACKET_LOSS_PERC(0));
        opus_encoder_ctl(enc, OPUS_GET_LOOKAHEAD(&skip));
        opus_encoder_ctl(enc, OPUS_SET_LSB_DEPTH(16));

        return (jlong)enc;
     }

     JNIEXPORT void JNICALL Java_com_xtc_audio_util_OpusEncoder_exit
     (JNIEnv *env, jobject obj,jlong handle){
        OpusEncoder *enc = (OpusEncoder*)handle;
        opus_encoder_destroy(enc);
        return;
     }

    JNIEXPORT jint Java_com_xtc_audio_util_OpusEncoder_encode
        (JNIEnv *env, jobject obj,jlong handle,jbyteArray pcm,jint frame_size,jbyteArray data,jint max_data_bytes){
        OpusEncoder *enc = (OpusEncoder*)handle;
        signed char* pBuffer = (*env)->GetByteArrayElements(env,pcm,NULL);
        unsigned char* pData =  (unsigned char*)(*env)->GetByteArrayElements(env,data,NULL);
        if(pBuffer == NULL){
            LOGE("GetByteArrayElements Failed!");
            return -1;
        }
        opus_int16 *frame = (opus_int16 *) pBuffer;
        int nbytes = opus_encode(enc,frame,frame_size,pData + sizeof(unsigned short),max_data_bytes);
        if(nbytes < 0){
            LOGE("opus_encode Failed!");
            return -1;
        }
        LOGD("opus_encode nbytes = %d",nbytes);
        //前两个字节为编码后数据的大小，用来在解码的时候使用
        ((unsigned short*)pData)[0] = nbytes;
        (*env)->ReleaseByteArrayElements(env,pcm,pBuffer,0);
        (*env)->ReleaseByteArrayElements(env,data,(signed char*)pData,0);

        return nbytes + sizeof(unsigned short);
    }

#ifdef __cplusplus
}
#endif

#endif
