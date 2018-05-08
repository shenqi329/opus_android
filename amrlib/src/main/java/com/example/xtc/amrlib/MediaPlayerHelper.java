package com.xtc.weichat.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

/**
 * 单例模式MediaPlayer
 * Created by zyz on 2017/6/1.
 */

public class MediaPlayerHelper {

    private MediaPlayer mMediaPlayer;
    private MediaPlayer.OnPreparedListener mPreparedListener;
    private MediaPlayer.OnCompletionListener mCompletionListener;

    private static class InstanceHolder {
        private static final MediaPlayerHelper INSTANCE = new MediaPlayerHelper();
    }

    public static MediaPlayerHelper getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private MediaPlayerHelper() {
        mMediaPlayer = new MediaPlayer();
        mPreparedListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        };
    }

    public void play(Context context, Uri uri) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(context, uri);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
