package com.example.c_changing.main;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class addListDialog extends AlertDialog implements View.OnClickListener{
    private String ip = "10.0.2.2";
    private ListView listView;
    private Button back;
    final static int BUFFER_SIZE = 4096;
    private String[] filename;
    private List<String> fileList;
    private int flag;
    private Context context;
    private ListActivity listActivity;

    public addListDialog(Context context, ListActivity activity){
        super(context);
        this.context = context;
        this.flag = 0;
        this.listActivity = activity;
    }

    protected List<String> getFileData(){
        List<String> list = new ArrayList<String>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = "http://" + ip + ":8080/TestWeb/index.txt";
                try{
                    URL url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    if(connection.getResponseCode() == 200) {
                        InputStream inputStream = connection.getInputStream();
                        if(inputStream == null){
                            System.out.println("NOTHING");
                        }
                        else{
                            System.out.println(1);
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            byte[] data = new byte[BUFFER_SIZE];
                            int count = -1;
                            while ((count = inputStream.read(data, 0, BUFFER_SIZE)) != -1){
                                outputStream.write(data, 0, count);
                            }
                            data = null;
                            filename = new String(outputStream.toByteArray(), "ISO-8859-1").split("#");
                            flag = 1;
                        }
                    }
                }catch (Exception e){
                    System.out.println(4);
                    e.printStackTrace();
                    if(e instanceof MalformedURLException){
                        //Toast.makeText(MainActivity.this, "PATH WRONG", Toast.LENGTH_LONG).show();
                        System.out.println("PATH WRONG");
                    }
                    else if(e instanceof TimeoutException){
                        //Toast.makeText(MainActivity.this, "TIME OUT", Toast.LENGTH_LONG).show();
                        System.out.println("TIME OUT");
                    }
                    else if(e instanceof IOException){
                        //Toast.makeText(MainActivity.this, "DATA WRONG", Toast.LENGTH_LONG).show();
                        System.out.println("DATA WRONG");
                    }
                    else {
                        //Toast.makeText(MainActivity.this, "SOMETHING WRONG", Toast.LENGTH_LONG).show();
                        System.out.println("SOMETHING WRONG");
                    }
                }
            }
        }).start();
        int num = 0;
        while (flag == 0){
            num+=1;
            if(num > 10000){
                System.out.println(6);
                num = 0;
            }
        }
        System.out.println(7);
        for(String str: filename){
            list.add(str);
        }
        flag = 0;
        return list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addfile_dialog);
        listView = (ListView)findViewById(R.id.server_file);
        back = (Button)findViewById(R.id.file_go_back);


        fileList = getFileData();
        listView.setAdapter(new addfileAdapter(this.context, fileList, listActivity));
        System.out.println(9);
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.setCancelable(false);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        this.dismiss();
        this.listActivity.getFileDir(this.listActivity.rootPath);
        this.listActivity.myListAdapter.notifyDataSetChanged();
    }
}
