package com.example.c_changing.main;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ListActivity extends Activity{
    private ImageView btn2;
    private ListView listView;
    private List<ItemBean> dataList;
    private List<String> paths, items;
    //private MyListAdapter myAdapter;
    private RelativeLayout rootView;
    private LinearLayout menuView;
    private LinearLayout openView;
    private static boolean isShow;
    String rootPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/SavAudio";
    MediaPlayer[] mMediaPlayer = new MediaPlayer[100];
    private List<ItemBean> itemBeanList = new ArrayList<>();
    private ImageView addfile;
    private addListDialog addlistdialog;
    public MyListAdapter myListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_list);
        btn2 = (ImageView) findViewById(R.id.sw_back);
        listView = (ListView) findViewById(R.id.video);
        addfile  = (ImageView) findViewById(R.id.addfile);
        addlistdialog = new addListDialog(ListActivity.this, this);
        addfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addlistdialog.show();
            }
        });

        getFileDir(rootPath);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = itemBeanList.size();
                for (int i = 0; i < size; i++) {
                    if(itemBeanList.get(i).mediaPlayer!=null) {
                        if(itemBeanList.get(i).mediaPlayer.isPlaying())
                            itemBeanList.get(i).mHandler.removeMessages(0x110);
                        itemBeanList.get(i).mediaPlayer.stop();
                        itemBeanList.get(i).mediaPlayer.release();
                    }
                }
                finish();
            }
        });

        myListAdapter = new MyListAdapter(this, itemBeanList, this);
        listView.setAdapter(myListAdapter);
       /* listView.setAdapter(new ArrayAdapter<String>(this,

                android.R.layout.simple_list_item_1, items));

        //listView注册一个元素点击事件监听器
        */

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override

            //当某个元素被点击时调用该方法

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                /*if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    //mMediaPlayer.reset();
                }*/

                //itemBeanList.get(arg2).runmusic();
                /*if(mMediaPlayer[arg2] == null) {
                    mMediaPlayer[arg2] = new MediaPlayer();
                    String item = rootPath + '/' + itemBeanList.get(arg2).itemContent;
                    try {
                        mMediaPlayer[arg2].setDataSource(item);
                        mMediaPlayer[arg2].prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //mMediaPlayer.prepareAsync() ;
                }
                if(mMediaPlayer[arg2].isPlaying())
                    mMediaPlayer[arg2].pause();
                //if(1 == 0){}
                else {
                    //MediaPlayer.OnCompletionListener listener =
                    //mMediaPlayer[arg2].setOnCompletionListener(listener);
                    mMediaPlayer[arg2].start();
                }
            }*/
            }
        });
    }


    public void getFileDir(String filePath) {
        try {
            items = new ArrayList<String>();
            paths = new ArrayList<String>();
            File f = new File(filePath);
            File [] files = f.listFiles();// 列出所有文件
            String str = new String();
            // 将所有文件存入list中
            itemBeanList.clear();
            if (files != null) {
                int count = files.length;// 文件个数
                for (int i = 0; i < count; i++) {
                    File file = files[i];
                    str = file.getName().split("-")[1];
                    itemBeanList.add(new ItemBean(R.mipmap.ic_launcher, str, file.getName()));
                    //items.add(file.getName());
                    paths.add(file.getPath());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
