package com.example.c_changing.main;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;

import java.io.IOException;

public class item {
    public String itemContent;
    public int num = 0;
    public int itemImageResId;
    public boolean exist;
    public int isclicked;
    private int id;
    public MediaPlayer mediaPlayer;
    public MediaPlayer mediaPlayer_2;
    public MediaPlayer mediaPlayer_3;
    private int bpm;
    private LoopActivity activity;
    public int[] position = new int[2];
    public String item;
    public ButtonCircleProgressBar mProgressBar;
    public static final int MSG_PROGRESS_UPDATE = 0x110;
    private int progress;
    private  int maxd;
    private Boolean close = false;
    public CanvasView canvasView;
    public item(int i, LoopActivity activ) {
        this.itemImageResId = 0;
        this.itemContent = "";
        this.isclicked = 0;
        this.exist = false;
        this.id = i;
        this.activity = activ;
    }

    public Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        public void handleMessage(android.os.Message msg) {
            //progress = mProgressBar.getProgress();
            //mProgressBar.setProgress(++progress);
            if(!close) {
                if (activity.tflag > 0) {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        mediaPlayer.setVolume(activity.getseekB(id), activity.getseekB(id));
                    }
                }

                mProgressBar.setProgress(mediaPlayer.getCurrentPosition() * 100 / maxd);
                if(mediaPlayer.isPlaying()) {
                    if (mediaPlayer_2 == null) {
                        mediaPlayer_2 = new MediaPlayer();
                        try {
                            mediaPlayer_2.setDataSource(item);
                            mediaPlayer_2.prepare();
                            bpm = activity.getbpm();
                            mediaPlayer_2.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed((float) (maxd / (60000.0 / bpm * num * 4))));
                            mediaPlayer_2.pause();
                            if (mediaPlayer_3 != null) {
                                mediaPlayer_3.stop();
                                mediaPlayer_3.reset();
                                mediaPlayer_3.release();
                                mediaPlayer_3 = null;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (mediaPlayer.getCurrentPosition() * 100 / maxd >= 90 && activity.tflag > 0) {
                    //mHandler.removeMessages(MSG_PROGRESS_UPDATE);
                    //mProgressBar.setStatus(ButtonCircleProgressBar.Status.End);
                    //mProgressBar.setProgress(0);
                    mediaPlayer_2.start();
                    mediaPlayer_2.setVolume(activity.getseekB(id), activity.getseekB(id));
                    mediaPlayer_3 = mediaPlayer;
                    mediaPlayer = mediaPlayer_2;
                    mediaPlayer_2 = null;
                    mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 20);
                } else {
                    if (mediaPlayer.isPlaying()) {
                        if (mediaPlayer.getCurrentPosition() * 100 / maxd >= 90)
                            mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 10);
                        else
                            mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 70);
                    }
                    else
                        mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 30);
                }
            }

            else{
                if(activity.tflag > 0){
                    mProgressBar.setStatus(ButtonCircleProgressBar.Status.End);
                    mProgressBar.setProgress(0);
                    mediaPlayer.stop();
                    mHandler.removeMessages(MSG_PROGRESS_UPDATE);
                    if(mediaPlayer_2 != null) {
                        mediaPlayer_2.stop();
                        mediaPlayer_2.reset();
                        mediaPlayer_2.release();
                        mediaPlayer_2 = null;
                    }
                    if(mediaPlayer_3 != null){
                        mediaPlayer_3.stop();
                        mediaPlayer_3.reset();
                        mediaPlayer_3.release();
                        mediaPlayer_3 = null;
                    }
                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    close = false;
                }
                else{
                    mProgressBar.setProgress(mediaPlayer.getCurrentPosition() * 100 / maxd);
                    mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 50);
                }
            }

        };
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void runmusic(ButtonCircleProgressBar ProgressBar){
        this.item = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Temp/" + this.activity.TempMap.get(id+ ".wav");
        mProgressBar = ProgressBar;
        if (mediaPlayer == null) {
            mediaPlayer =  new MediaPlayer();
            //mediaPlayer.setLooping(true);
            try {
                mediaPlayer.setDataSource(item);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            maxd = mediaPlayer.getDuration()-100;
            /*progressBar.setMax(mediaPlayer.getDuration());
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                        progressBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            },0,50);*/
        }

        if(mediaPlayer.isPlaying()) {
            close = true;
        }
        else {
            bpm = activity.getbpm();
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed((float)(maxd/(60000.0/bpm*num*4))));
            mediaPlayer.pause();
            mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 10);
            mProgressBar.setStatus(ButtonCircleProgressBar.Status.Starting);
        }
    }

    public void change(){
        String item = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Temp/" + activity.TempMap.get(id+ ".wav");
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mHandler.removeMessages(0x110);
                mProgressBar.setStatus(ButtonCircleProgressBar.Status.End);
                mProgressBar.setProgress(0);
            }
            mediaPlayer.stop();
            mediaPlayer.release();
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(item);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            maxd = mediaPlayer.getDuration();
        }
    }
}
