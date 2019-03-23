package com.example.c_changing.main;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyListAdapter extends BaseAdapter{
    private String ip = "10.0.2.2";
    private List<ItemBean> mList;//数据源
    private LayoutInflater mInflater;//布局装载器对象
    private ListButtonCircleProgressBar processBar;
    private  ItemBean bean;
    private ListActivity listActivity;
    private String rootpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SavAudio/";
    public static final MediaType MEDIA_TYPE_WAV = MediaType.parse("audio/wav");

    // 通过构造方法将数据源与数据适配器关联起来
    // context:要使用当前的Adapter的界面对象
    public MyListAdapter(Context context, List<ItemBean> list, ListActivity activity) {
        this.mList = list;
        this.mInflater = LayoutInflater.from(context);
        this.listActivity = activity;
    }

    @Override
    //ListView需要显示的数据数量
    public int getCount() {
        return mList.size();
    }

    @Override
    //指定的索引对应的数据项
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    //指定的索引对应的数据项ID
    public long getItemId(int position) {
        return position;
    }

    @Override
    //返回每一项的显示内容
    public View getView(final int position, View convertView, ViewGroup parent) {
        //将布局文件转化为View对象
        View view = mInflater.inflate(R.layout.item_bean,null);

        /**
         * 找到item_bean布局文件中对应的控件
         */
        //ImageView imageView = (ImageView) view.findViewById(R.id.iv_image);
        bean = mList.get(position);
        final String contex = bean.itemContent;
        final String content = bean.sss;
        TextView contentTextView = (TextView) view.findViewById(R.id.file_name);
        processBar = (ListButtonCircleProgressBar)view.findViewById(R.id.listProgressBar);
        bean.mProgressBar = (ListButtonCircleProgressBar) processBar;
        processBar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                bean = mList.get(position);
                ListButtonCircleProgressBar progressBar = v.findViewById(R.id.listProgressBar);
                if(bean.mediaPlayer != null){
                    if(bean.mediaPlayer.isPlaying()){
                        bean.mHandler.removeMessages(0x110);
                        progressBar.setStatus(ListButtonCircleProgressBar.Status.End);
                        progressBar.setProgress(0);

                    }
                    bean.mediaPlayer.stop();
                    try {
                        bean.mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });
        processBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListButtonCircleProgressBar progressBar = view.findViewById(R.id.listProgressBar);
                bean = mList.get(position);
                //processBar.setProgress(0);
                bean.runmusic(progressBar);
                /*if (processBar.getStatus()== ButtonCircleProgressBar.Status.Starting){
                    processBar.setStatus(ButtonCircleProgressBar.Status.End);
                    bean.mHandler.removeMessages(bean.MSG_PROGRESS_UPDATE);
                }else{
                    bean.mHandler.sendEmptyMessage(bean.MSG_PROGRESS_UPDATE);
                    processBar.setStatus(ButtonCircleProgressBar.Status.Starting);
                }*/
            }
        });
        ImageView delete = (ImageView) view.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/SavAudio/"+mList.get(position).sss);
                f.delete();
                mList.remove(position);
                MyListAdapter.this.notifyDataSetChanged();
            }
        });

        ImageView upload = (ImageView)view.findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = rootpath + content;
                File file = new File(path);

                OkHttpClient mOkHttpClient = new OkHttpClient();

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("audio", file.getName(), RequestBody.create(MEDIA_TYPE_WAV, file))
                        .build();

                Request request = new Request.Builder()
                        .url("http://" + ip + ":8080/TestWeb/TestWeb")
                        .post(requestBody)
                        .build();
                Call call = mOkHttpClient.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        listActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(listActivity, "Upload Fail！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        listActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(listActivity, "Upload Success!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
        //获取相应索引的ItemBean对象

        /**
         * 设置控件的对应属性值
         */
        //imageView.setImageResource(bean.itemImageResId);
        //processBar.setProgress(0);
        contentTextView.setText(contex);

        return view;
    }

}
