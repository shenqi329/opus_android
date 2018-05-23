package com.example.xtc.amrlib;

import android.media.MediaCodec;
import android.media.MediaFormat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by liujunshi on 2018/5/11.
 */

public class AmrEncoder {
    private MediaCodec mediaCodec;

    public void init(){
        try {
            mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AMR_NB);
            MediaFormat mediaFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AMR_NB,8000,1);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 4750);
            mediaCodec.configure(mediaFormat,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
            mediaCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit(){
        mediaCodec.stop();
        mediaCodec.release();
        mediaCodec = null;
    }

    public byte[] encodePCMData(byte[] pcmData,boolean isLast){
        int inputIndex = mediaCodec.dequeueInputBuffer(10000);
        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        if (inputIndex != MediaCodec.INFO_TRY_AGAIN_LATER){
            if (isLast){
                mediaCodec.queueInputBuffer(inputIndex,0 ,0,0,MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            }else{
                inputBuffers[inputIndex].put(pcmData);
                mediaCodec.queueInputBuffer(inputIndex,0,pcmData.length,1000*20,0);//20ms一帧
            }
        }

        MediaCodec.BufferInfo encodeBufferInfo = new MediaCodec.BufferInfo();
        int outputIndex = mediaCodec.dequeueOutputBuffer(encodeBufferInfo,10000);
        if(outputIndex < 0){
            return new byte[0];
        }
        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
        ByteBuffer outputBuffer = outputBuffers[outputIndex];
        outputBuffer.position(encodeBufferInfo.offset);
        outputBuffer.limit(encodeBufferInfo.offset + encodeBufferInfo.size);
        byte[] bytes = new byte[encodeBufferInfo.size];
        outputBuffer.get(bytes,0,encodeBufferInfo.size);
        mediaCodec.releaseOutputBuffer(outputIndex,false);
        return bytes;
    }
}
