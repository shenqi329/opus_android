package com.xtc.audio.util;

/**
 * Created by liujunshi on 2018/5/23.
 */

public class OpusEncoder {
    static {
        try {
            System.loadLibrary("opus");
        } catch (UnsatisfiedLinkError error) {
            error.printStackTrace();
        }
    }

    /**
     * 初始化
     * @param sampleRate 采样率，目前只支持8000、16000、24000、48000
     * @param bitRate 比特率，6-510kbps
     * @return 句柄
     */
    public static native long init(int sampleRate,int bitRate);

    /**
     * 退出
     * @param handle 句柄
     */
    public static native void exit(long handle);

    /**
     * 编码
     * @param handle 句柄
     * @param pcm 原始采样数据
     * @param frame_size 16000采样率对应320,24000采样率对应480,48000采样率对应960
     * @param out 传出数据
     * @return
     */
    public static native int encode(long handle, byte[] pcm, int frame_size, byte[] out);
}
