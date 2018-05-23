package com.example.xtc.amrlib;

import android.media.AmrInputStream;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by liujunshi on 2018/5/10.
 */

public class AudioDecoder implements AudioRecorder.IAudioDecoder {

    //private MediaCodec codec;
    AmrEncoder amrEncoder;
    private AmrInputStream inputStream;
    private String fileNameForAmr;
    FileOutputStream fos;

    public AudioDecoder(String fileNameForAmr){
        this.fileNameForAmr = fileNameForAmr;
    }

    @Override
    public void startDecoder(int sampleRate) {
        try {
//            codec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AMR_NB);
//            MediaFormat audioMediaFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AMR_NB,sampleRate,1);
//            audioMediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 4750);
//            codec.configure(audioMediaFormat,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
            amrEncoder = new AmrEncoder();
            amrEncoder.init();
            fos = new FileOutputStream(fileNameForAmr);
            fos.write("#!AMR\n".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decoderData(byte[] pcmData, int bufferReadResult) throws Exception {

//        byte[] outbytes = new byte[32];
//        InputStream inputStream = new ByteArrayInputStream(pcmData,0,bufferReadResult);
//        AmrInputStream amrInputStream = new AmrInputStream(inputStream);
//        int length = amrInputStream.read(outbytes,0,64);
//        amrInputStream.close();

        ByteBuffer byteBuffer = ByteBuffer.allocate(bufferReadResult);
        byteBuffer.put(pcmData,0,bufferReadResult);
        byte[] armData  = amrEncoder.encodePCMData(byteBuffer.array(),false);
        if (armData.length > 0){
            fos.write(armData);
        }
//        int inputIndex = codec.dequeueInputBuffer(10000);
//        ByteBuffer[] inputBuffers = codec.getInputBuffers();
//        if (inputIndex >= 0){
//            inputBuffers[inputIndex].put(pcmData,0,bufferReadResult);
//            codec.queueInputBuffer(inputIndex,0,bufferReadResult,1000*20,0);
//        }
//        MediaCodec.BufferInfo encodeBufferInfo = new MediaCodec.BufferInfo();
//        int outputIndex = codec.dequeueOutputBuffer(encodeBufferInfo,10000);
//        if(outputIndex >= 0){
//            ByteBuffer[] outputBuffers = codec.getOutputBuffers();
//            ByteBuffer outputBuffer = outputBuffers[outputIndex];
//            outputBuffer.position(encodeBufferInfo.offset);
//            outputBuffer.limit(encodeBufferInfo.offset + encodeBufferInfo.size);
//            byte[] bytes = new byte[encodeBufferInfo.size];
//            outputBuffer.get(bytes,0,encodeBufferInfo.size);
//            codec.releaseOutputBuffer(outputIndex,false);
//            fos.write(bytes);
//        }
    }

    @Override
    public void finishDecoder() {
        //inputStream.close();
        amrEncoder.exit();
    }

    @Override
    public void recoderState(boolean isPause) {

    }
}
