package com.example.c_changing.main;

import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ItemBean {
    public int itemImageResId;//图像资源ID
    public String sss;
    public String itemContent;//内容
    public MediaPlayer mediaPlayer;
    public ListButtonCircleProgressBar mProgressBar;
    public static final int MSG_PROGRESS_UPDATE = 0x110;
    private int progress;
    private  int maxd;
    public ItemBean(int itemImageResId,String itemContent,String vv) {
        this.itemImageResId = itemImageResId;
        this.itemContent = itemContent;
        this.sss = vv;
    }
    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            //progress = mProgressBar.getProgress();
            //mProgressBar.setProgress(++progress);
            mProgressBar.setProgress(mediaPlayer.getCurrentPosition()*100/maxd);
            if (mediaPlayer.getCurrentPosition()*100/maxd >= 101) {
                mHandler.removeMessages(MSG_PROGRESS_UPDATE);
                mProgressBar.setStatus(ListButtonCircleProgressBar.Status.End);
                mProgressBar.setProgress(0);
            }else{
                mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 100);
            }

        };
    };

    public void runmusic(ListButtonCircleProgressBar ProgressBar){
        String item = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SavAudio/" + sss;
        mProgressBar = ProgressBar;
        if (mediaPlayer == null) {
            mediaPlayer =  new MediaPlayer();
            mediaPlayer.setLooping(true);
            try {
                mediaPlayer.setDataSource(item);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            maxd = mediaPlayer.getDuration();
            /*progressBar.setMax(mediaPlayer.getDuration());
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                        progressBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            },0,50);*/
        }
        if (mProgressBar.getStatus()== ListButtonCircleProgressBar.Status.Starting){
            mProgressBar.setStatus(ListButtonCircleProgressBar.Status.End);
            mHandler.removeMessages(MSG_PROGRESS_UPDATE);
        }else{
            mHandler.sendEmptyMessage(MSG_PROGRESS_UPDATE);
            mProgressBar.setStatus(ListButtonCircleProgressBar.Status.Starting);
        }
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else {
            mediaPlayer.start();
        }
    }
}
