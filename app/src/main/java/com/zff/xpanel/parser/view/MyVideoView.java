package com.zff.xpanel.parser.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.emp.xdcommon.android.log.LogUtils;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

public class MyVideoView extends SurfaceView {

    private String TAG=getClass().getSimpleName();
    private MediaPlayer mmediaPlayer;
    private LibVLC mLibVLC;

    public MyVideoView(Context context) {
        this(context,null);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getMediaPlayer().getVLCVout().setVideoView(this);
        getMediaPlayer().getVLCVout().attachViews();
        setSurfaceHolder(this);
    }

    public void setUrl(String url){
        setMedia(url);
    }

    //设置视频 surfaceHolder
    private void setSurfaceHolder(SurfaceView surfaceView) {
        SurfaceHolder mSurfaceHolder = surfaceView.getHolder();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String chroma = pref.getString("chroma_format", "");
//		if(LibVlcUtil.isGingerbreadOrLater() && chroma.equals("YV12")) {
//            mSurfaceHolder.setFormat(ImageFormat.YV12);
//        } else if (chroma.equals("RV16")) {
//            mSurfaceHolder.setFormat(PixelFormat.RGB_565);
//        } else {
//            mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
//        }
        mSurfaceHolder.addCallback(mSurfaceCallback);
    }
    private LibVLC getLibVLC() {
        if (mLibVLC == null) {
            mLibVLC = new LibVLC(getContext());
        }
        return mLibVLC;
    }

    private MediaPlayer getMediaPlayer() {
        if (mmediaPlayer == null) {
            mmediaPlayer = new MediaPlayer(getLibVLC());
        }
        return mmediaPlayer;
    }

    private void setMedia(String urlStr) {
        Uri uri = Uri.parse(urlStr);
        Media media = new Media(getLibVLC(), uri);
//		String[] options = null;
//		for(String option : options){
//			media.addOption(option);
//		}
        getMediaPlayer().setMedia(media);
//        media.release();
    }

    public void videoPlay() {
        getMediaPlayer().play();
        //mVideoView.start();
        Toast.makeText(getContext(), "video play", Toast.LENGTH_SHORT).show();
    }

    public void videoPause() {
        getMediaPlayer().pause();
        Toast.makeText(getContext(), "video pause", Toast.LENGTH_SHORT).show();
    }



    public void videoStop() {
        getMediaPlayer().stop();
        //mVideoView.pause();
        Toast.makeText(getContext(), "video stop", Toast.LENGTH_SHORT).show();
    }

    public void videoRelease() {
        getMediaPlayer().release();
        getLibVLC().release();
        getMediaPlayer().getVLCVout().detachViews();
        mLibVLC=null;
        mmediaPlayer=null;
        Toast.makeText(getContext(), "video release", Toast.LENGTH_SHORT).show();
    }


    /**
     * 视频 vlc
     * attach and disattach surface to the lib
     */
    private final SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (format == PixelFormat.RGBX_8888)
                Log.d(TAG, "Pixel format is RGBX_8888");
            else if (format == PixelFormat.RGB_565)
                Log.d(TAG, "Pixel format is RGB_565");
            else if (format == ImageFormat.YV12)
                Log.d(TAG, "Pixel format is YV12");
            else
                Log.d(TAG, "Pixel format is other/unknown");
            getMediaPlayer().getVLCVout().setWindowSize(width,height);
            LogUtils.e(TAG,"surfaceChanged");

//            mLibVLC.attachSurface(holder.getSurface(), VideoPlayerActivity.this);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            LogUtils.e(TAG,"surfaceCreated");
            videoPlay();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            //mLibVLC.detachSurface();
            LogUtils.e(TAG,"surfaceDestroyed");
            videoStop();
        }
    };


}
