package com.xtc.audio.util;

/**
 * Created by liujunshi on 2018/5/23.
 */

public class OpusDecoder {
    /**
     * 初始化
     * @param sampleRate 采样率，目前只支持8000、16000、24000、48000，必须和编码设置的采样率一致
     * @return 句柄
     */
    public static native long init(int sampleRate);

    /**
     * 退出
     * @param handle 句柄
     */
    public static native void exit(long handle);

    /**
     * 解码
     * @param handle 句柄
     * @param opus 编码数据
     * @param opusLength 编码长度
     * @param out 传出数据
     * @param frame_size 16000采样率对应320,24000采样率对应480,48000采样率对应960，必须和编码的设置保持一致
     * @return
     */
    public static native void decode(long handle,byte[] opus,int opusLength,byte[] out,int frame_size);
}
