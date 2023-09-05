package com.zff.xpanel.parser.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.emp.xdcommon.android.log.LogUtils;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IjkVideoView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "IjkVideoView";
    private IjkMediaPlayer mediaPlayer;
    private String videoUrl;

    public IjkVideoView(Context context) {
        this(context, null);
    }

    public IjkVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
    }

    private void createPlayer() {
        mediaPlayer = new IjkMediaPlayer();
//        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 8);
        mediaPlayer.setOption(1, "analyzemaxduration", 100L);
        mediaPlayer.setOption(1, "probesize", 10240L);
//        mediaPlayer.setOption(1, "flush_packets", 1L);
//        mediaPlayer.setOption(4, "packet-buffering", 0L);
//        mediaPlayer.setOption(4, "framedrop", 1L);
        try {
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.setOnPreparedListener(iMediaPlayer -> {
                LogUtils.e(TAG, "onPrepared");
//                iMediaPlayer.start();
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setVideoPath(String url) {
        videoUrl = url;
        if (getVisibility() == VISIBLE) {
            setVisibility(GONE);
            setVisibility(VISIBLE);
        }
//        if(mediaPlayer!=null){
//            release();
//            createPlayer();
//            mediaPlayer.setDisplay(getHolder());
//        }
    }

    public void start() {
        if (mediaPlayer != null)
            mediaPlayer.start();
    }

    public void pause() {
        if (mediaPlayer != null)
            mediaPlayer.pause();
    }

    public void resume() {
        if (mediaPlayer != null)
            mediaPlayer.reset();
    }

    public void stop() {
        if (mediaPlayer != null)
            mediaPlayer.stop();
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtils.e(TAG, "surfaceCreated");
        createPlayer();
        mediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        release();
    }
}
