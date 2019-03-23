package com.example.c_changing.main;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LoopActivity extends Activity {
    File rootPath =  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/SavAudio");
    File tempPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Temp");
    private int mState = -1;
    private GridView gridView;
    private List<item> dataList;
    public MyAdapter adapter;
    private TextView textView;
    private ImageView recode;
    private ImageView sound;
    private ImageView setting;
    private ImageView time;
    private ImageView loop;
    private CustomSeekBar seekBar1;
    private CustomSeekBar seekBar2;
    private CustomSeekBar seekBar3;
    private CustomSeekBar seekBar4;
    private int loc = -1;
    private int rec = -1;
    private int tag;
    // drawerlayout && listmenu
    private ImageView right_button;
    private DrawerLayout mDrawerLayout;
    private ListView rightmenu;
    private Boolean is_recording;
    private LinearLayout rightdrawer;
    private List<Map<String, Object>> data;
    public String tmp_s;
    public TextView tmp;
    private Timer mTimer;
    private Boolean flag = false;
    private Boolean record_flag = false;
    private TimerTask mTimerTask;
    public int mBeatSoundId = -1;
    public int tflag = 0;
    private Boolean is_open = false;
    public HashMap<String,String> TempMap = new HashMap<String, String>();
    public HashMap<String,String> SavMap = new HashMap<String, String>();
    private BpmDialog bpmDialog;
    public SavDialog saveDialog;
    public SoundPool mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

    private ImageView quitGame;

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        super.onResume();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!rootPath.exists()){
            rootPath.mkdir();
        }
        if(!tempPath.exists()){
            tempPath.mkdir();
        }

        quitGame = (ImageView) findViewById(R.id.quit);
        quitGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = dataList.size();
                for(int i = 0; i < size; i++){
                    if(dataList.get(i).mediaPlayer != null){
                        if(dataList.get(i).mediaPlayer.isPlaying())
                            dataList.get(i).mHandler.removeMessages(0x110);
                        dataList.get(i).mediaPlayer.stop();
                        dataList.get(i).mediaPlayer.release();
                    }
                    if(dataList.get(i).mediaPlayer_2 != null){
                        if(dataList.get(i).mediaPlayer_2.isPlaying())
                            dataList.get(i).mHandler.removeMessages(0x110);
                        dataList.get(i).mediaPlayer_2.stop();
                        dataList.get(i).mediaPlayer_2.release();
                    }
                    if(dataList.get(i).mediaPlayer_3 != null) {
                        if (dataList.get(i).mediaPlayer_3.isPlaying())
                            dataList.get(i).mHandler.removeMessages(0x110);
                        dataList.get(i).mediaPlayer_3.stop();
                        dataList.get(i).mediaPlayer_3.release();
                    }
                }
                finish();
            }
        });

        gridView = (GridView) findViewById(R.id.gridview);
        textView = findViewById(R.id.textView);
        recode = findViewById(R.id.recode);
        sound = findViewById(R.id.sound);
        loop = findViewById(R.id.loop);
        setting = findViewById(R.id.setting);
        time = findViewById(R.id.time);
        seekBar1 = findViewById(R.id.seekbar1);
        seekBar2 = findViewById(R.id.seekbar2);
        seekBar3 = findViewById(R.id.seekbar3);
        seekBar4 = findViewById(R.id.seekbar4);

        // drawerlayout
        right_button = findViewById(R.id.right_button);
        rightmenu = (ListView)findViewById(R.id.right_menu);
        rightdrawer = (LinearLayout)findViewById(R.id.right_drawer);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.dl_right);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        recode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_recording = false;
                mHandler.sendEmptyMessageDelayed(0x110, 10);
            }
        });

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_open = is_open ^ true;

            }
        });


        initData();

        setdrawerListener();

        data = getData();
        ListAdapter listadapter = new ListAdapter(this, data, this);
        rightmenu.setAdapter(listadapter);

        dragListener draglistener = new dragListener();
        mBeatSoundId = mSoundPool.load(this, R.raw.metronome2, 1);

        bpmDialog = new BpmDialog(this,this);
        saveDialog = new SavDialog(this,this);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bpmDialog.show();
            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == false){
                    time.setImageResource(R.drawable.logod);
                    StartMetronome();
                }
                else{
                    StopMetronome();
                }
            }
        });


        mDrawerLayout.setOnDragListener(draglistener);
        //int[] to={R.id.img,R.id.text};
        //adapter=new SimpleAdapter(this, dataList, R.layout.item, from, to);
        gridView.setAdapter(adapter = new MyAdapter(this, dataList, this));
        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return MotionEvent.ACTION_MOVE == event.getAction() ? true
                        : false;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override

            //当某个元素被点击时调用该方法

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                /*adapter.setSeclection(arg2);
                if(dataList.get(arg2).isclicked == 0) {
                    dataList.get(arg2).isclicked = 1;
                    textView.setText(" Select: Track"+(arg2/4+1)+", "+(arg2%4+1));
                }
                else {
                    dataList.get(arg2).isclicked = 0;
                    textView.setText(" Select:");
                }
                adapter.notifyDataSetChanged();*/
            }
        });
        listFile();
        adapter.notifyDataSetChanged();
    }

    private void setdrawerListener(){
        right_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data = getData();
                        ListAdapter listadapter = new ListAdapter(LoopActivity.this, data, LoopActivity.this);
                        rightmenu.setAdapter(listadapter);
                        mDrawerLayout.openDrawer(Gravity.RIGHT);
                        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);


                    }
                }
        );
        mDrawerLayout.addDrawerListener(
                new DrawerLayout.SimpleDrawerListener() {
                    @Override
                    public void onDrawerClosed(View drawerView){
                        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
                    }
                });
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if(record_flag){
                is_recording = true;
                record();
                mHandler.sendEmptyMessageDelayed(0x110, (long)((60000.0/getbpm()*4*bpmDialog.num)+100));
                record_flag =false;
            }
            else if(!is_recording) {
                if (tflag > 0) {
                    record_flag = true;
                    mHandler.sendEmptyMessageDelayed(0x110, 290);
                }
                else
                    mHandler.sendEmptyMessageDelayed(0x110, 10);
            }
            else{
                stop();
                is_recording = false;
            }
        };
    };

    public Handler lHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if(tflag > 0){
                tflag -= 7;
            }
            switch ((gettag()+4)%4){
                case 0:
                    loop.setImageResource(R.drawable.v4);
                    break;
                case 1:
                    loop.setImageResource(R.drawable.v1);
                    break;
                case 2:
                    loop.setImageResource(R.drawable.v2);
                    break;
                case 3:
                    loop.setImageResource(R.drawable.v3);
                    break;
            }
        };
    };

    private List<Map<String, Object>> getData(){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        //......
        if(!rootPath.exists()){
            rootPath.mkdir();
        }else {
            File[] files = rootPath.listFiles();
            int seq = 0;
            for(File spec : files){
                seq++;
                map = new HashMap<String, Object>();
                map.put("title", spec.getName());
                list.add(map);
            }
        }

        return list;
    }

    public void listFile(){
        File[] files = tempPath.listFiles();
        String c;
        int t;
        for(File spec : files){
            c = (String)spec.getName();
            t = c.indexOf("-");
            TempMap.put(c.substring(t+1), c);
        }
        File[] file = rootPath.listFiles();
        for(File spec : file){
            c = (String)spec.getName();
            t = c.indexOf("-");
            SavMap.put(c.substring(t+1), c);
        }
    }

    class dragListener implements View.OnDragListener{
        @Override
        public boolean onDrag(View v, DragEvent event){
            final int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    return true;
                case DragEvent.ACTION_DROP:
                    /*tmp.setX(event.getX() - tmp.getWidth()/2);
                    tmp.setY(event.getY() - tmp.getHeight()/2);*/
                    //System.out.println(event.getX() - tmp.getWidth()/2 );
                    //System.out.println(event.getY() - tmp.getHeight()/2 );
                    int item=gridView.pointToPosition((int) (event.getX() - tmp.getWidth()/2 -170), (int) (event.getY() - tmp.getHeight()/2-190));
                    //System.out.println(item);

                    copyFile(rootPath+"/"+tmp_s,tempPath+"/"+tmp_s.substring(0,tmp_s.indexOf("-")+1)+item+".wav");
                    listFile();
                    adapter.notifyDataSetChanged();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    return true;
                default:
                    break;
            }
            return false;
        }
    }
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    //System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    private void StartMetronome(){
        tag = -4;
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if(mSoundPool != null && mBeatSoundId != -1 && is_open){
                    mSoundPool.play(mBeatSoundId, 1.0f, 1.0f, 0, 0, 1.0f);
                    System.out.println(1);
                }
                tag++;
                if (tag == 5) {
                    tag = 1;
                }
                if(tag == 1){
                    tflag = 14;
                    lHandler.sendEmptyMessageDelayed(0x110, 150);
                }
                lHandler.sendEmptyMessageDelayed(0x110, 10);
            }
        };
        mTimer.schedule(mTimerTask, 0, Math.round(60000.0 / bpmDialog.bpm));
        flag = true;
    }

    public void StopMetronome(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
            flag = false;
            loop.setImageResource(R.drawable.v0);
            time.setImageResource(R.drawable.logoo);
            tag = -4;
        }
    }

    public int getbpm(){
        return bpmDialog.bpm;
    }
    public int gettag(){
        return this.tag;
    }
    public float getseekB(int loc){
        switch (loc/4){
            case 0:
                return seekBar1.getCurrentDegrees()/100;
            case 1:
                return seekBar2.getCurrentDegrees()/100;
            case 2:
                return seekBar3.getCurrentDegrees()/100;
            case 3:
                return seekBar4.getCurrentDegrees()/100;
        }
        return 0;
    }

    void initData() {
        dataList = new ArrayList<item>();
        for (int i = 0; i <16; i++) {
            //Map<String, Object> map=new HashMap<String, Object>();
            //map.put("img", icno[i]);
            //map.put("text",name[i]);
            item Item = new item(i,this);
            dataList.add(Item);
        }
    }
    public void setText(String str){
        textView.setText(str);
    }

    private int  findSelected(){
        for(int i =0; i < dataList.size(); i++ ){
            if(dataList.get(i).isclicked == 1){
                return i;
            }
        }
        return -1;
    }

    private void record(){

        int mResult = -1;
        loc = findSelected();
        if(loc == -1){
            mResult = -2;
        }
        else {
            if (mState != -1) {
                Message msg = new Message();
                Bundle b = new Bundle();// 存放数据
                b.putInt("cmd", CMD_RECORDFAIL);
                b.putInt("msg", ErrorCode.E_STATE_RECODING);
                msg.setData(b);
                return;
            }
            AudioRecordFunc mRecord_1 = AudioRecordFunc.getInstance();
            mResult = mRecord_1.startRecordAndFile(loc, bpmDialog.num);
            recode.setImageResource(R.drawable.lui);

        }
        if(mResult == ErrorCode.SUCCESS){
            mState = 0;
            rec = loc;
        }else{
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putInt("cmd",CMD_RECORDFAIL);
            b.putInt("msg", mResult);
            msg.setData(b);
        }
    }

    private void stop(){
        if(mState != -1){
            AudioRecordFunc mRecord_1 = AudioRecordFunc.getInstance();
            mRecord_1.stopRecordAndFile();
            recode.setImageResource(R.drawable.lu);
            Message msg = new Message();
            Bundle b = new Bundle();// 存放数据
            b.putInt("cmd",CMD_STOP);
            b.putInt("msg", mState);
            adapter.change(rec);
            listFile();
            adapter.notifyDataSetChanged();
            msg.setData(b);
            mState = -1;
        }
    }

    private final static int CMD_RECORDING_TIME = 2000;
    private final static int CMD_RECORDFAIL = 2001;
    private final static int CMD_STOP = 2002;




}
