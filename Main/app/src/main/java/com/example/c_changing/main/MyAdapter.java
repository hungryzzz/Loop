package com.example.c_changing.main;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

public class MyAdapter extends BaseAdapter {
    private List<item> mList;//数据源
    private LayoutInflater mInflater;//布局装载器对象
    private item bean;
    private ImageView img;
    private CanvasView canvasView;
    private int clickTemp = -1;
    private int lastPosition;
    private LoopActivity activity;
    private ButtonCircleProgressBar processBar;
    public MyAdapter(Context context, List<item> list, LoopActivity act) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        activity = act;
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSeclection(int position) {
        clickTemp = position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item,null);
        bean = mList.get(position);
        //canvasView = view.findViewById(R.id.view);
        //img = view.findViewById(R.id.imageView);
        if (clickTemp == position) {
            if(bean.isclicked == 1) {
                view.setBackgroundResource(R.drawable.changed2);
            }
            else{
                view.setBackgroundResource(R.drawable.tu);
            }
        } else {
            bean = mList.get(position);
            bean.isclicked = 0;
            //canvasView = view.findViewById(R.id.view);
            //img = view.findViewById(R.id.imageView);
            //img.setImageResource(bean.Img);
            view.setBackgroundResource(R.drawable.tu);
        }
        String c = activity.TempMap.get(position+".wav");
        File rootPath =  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Temp/" + c);
        processBar = (ButtonCircleProgressBar)view.findViewById(R.id.progressBar);
        if(bean.mProgressBar != processBar) {
            //if(processBar.getStatus() == ButtonCircleProgressBar.Status.Starting)
            if (bean.mProgressBar!=null) {
                processBar.setStatus(bean.mProgressBar.getStatus());
                processBar.setProgress(bean.mProgressBar.getProgress());
            }
            bean.mProgressBar = (ButtonCircleProgressBar) processBar;
        }

        bean.exist = rootPath.exists();
        if(bean.exist){
            bean.mProgressBar.State = 1;
            int d = c.indexOf("-");
            bean.num = Integer.valueOf(c.substring(0,d));
        }
        else {
            bean.mProgressBar.State = -1;
        }
        processBar.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onLongClick(View v) {
                bean = mList.get(position);
                /*if(bean.mProgressBar.State != -1) {
                    ButtonCircleProgressBar progressBar = v.findViewById(R.id.progressBar);
                    if (bean.mediaPlayer != null) {
                        if (bean.mediaPlayer.isPlaying()) {
                            bean.mHandler.removeMessages(0x110);
                        }
                        bean.mProgressBar.setStatus(ButtonCircleProgressBar.Status.End);
                        bean.mProgressBar.setProgress(0);
                        bean.mediaPlayer.stop();
                        try {
                            bean.mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }*/
                if (bean.mProgressBar.State != -1) {
                    ButtonCircleProgressBar progressBar = v.findViewById(R.id.progressBar);
                    if(bean.mediaPlayer != null && bean.mediaPlayer.isPlaying())
                        bean.runmusic(progressBar);
                    activity.saveDialog.position = position;
                    activity.saveDialog.show();
                    setSeclection(position);
                    if (bean.isclicked == 0) {
                        bean.isclicked = 1;
                        activity.setText(" Select: Track" + (position / 4 + 1) + ", " + (position % 4 + 1));
                        //textView.setText(" Select: Track"+(arg2/4+1)+", "+(arg2%4+1));
                    }
                    notifyDataSetChanged();
                    return true;
                }
                return true;
            }
        });

        processBar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                ButtonCircleProgressBar progressBar = view.findViewById(R.id.progressBar);
                bean = mList.get(position);
                if(bean.mProgressBar.State != -1) {
                    //processBar.setProgress(0);
                    bean.runmusic(progressBar);
                    if(bean.mediaPlayer==null || !bean.mediaPlayer.isPlaying()) {
                        stopline(position);
                    }
                /*if (processBar.getStatus()== ButtonCircleProgressBar.Status.Starting){
                    processBar.setStatus(ButtonCircleProgressBar.Status.End);
                    bean.mHandler.removeMessages(bean.MSG_PROGRESS_UPDATE);
                }else{
                    bean.mHandler.sendEmptyMessage(bean.MSG_PROGRESS_UPDATE);
                    processBar.setStatus(ButtonCircleProgressBar.Status.Starting);
                }*/
                }
                setSeclection(position);
                if(bean.isclicked == 0) {
                    bean.isclicked = 1;
                    activity.setText(" Select: Track"+(position/4+1)+", "+(position%4+1));
                    //textView.setText(" Select: Track"+(arg2/4+1)+", "+(arg2%4+1));
                }
                notifyDataSetChanged();
            }
        });
        return view;
    }

    public void change(int pos){
        bean = mList.get(pos);
        bean.change();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void stopline(int pos){
        for(int i = 4*(pos/4); i <4*(pos/4)+4; i++){
            if(i != pos){
                bean = mList.get(i);
                if(bean.mProgressBar.State != -1)
                    if(bean.mediaPlayer!=null && bean.mediaPlayer.isPlaying())
                        bean.runmusic(bean.mProgressBar);
            }
        }
    }
}
