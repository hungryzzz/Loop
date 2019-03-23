package com.example.c_changing.main;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class addfileAdapter extends BaseAdapter {
    public String ip = "10.0.2.2";
    private List<String> filename;
    private LayoutInflater mInflater = null;
    private ListActivity listActivity;
    public addfileAdapter(Context context, List<String> strings, ListActivity activity){
        this.mInflater = LayoutInflater.from(context);
        this.filename = strings;
        this.listActivity = activity;
    }
    private String rootpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SavAudio/";


    static class ViewHolder{
        public TextView fName;
        public ImageView download;
    }
    @Override
    public int getCount() { return filename.size(); }

    @Override
    public Object getItem(int position) { return filename.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        ViewHolder holder = null;
        final TextView head;
        final int posi = position;

        if (converView == null) {
            holder = new ViewHolder();

            converView = mInflater.inflate(R.layout.server_file_list, null);
            holder.fName = (TextView) converView.findViewById(R.id.server_file_name);
            holder.download = (ImageView) converView.findViewById(R.id.server_file_add);
            converView.setTag(holder);
        } else {
            holder = (ViewHolder) converView.getTag();
        }
        holder.fName.setText((filename.get(position).split("_")[1]).split("-")[1]);

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String name = filename.get(posi).split("_")[1];
                        String path = "http://" + ip + ":8080/TestWeb/" + filename.get(posi);
                        try {
                            URL url = new URL(path);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("GET");
                            connection.setConnectTimeout(5000);
                            if (connection.getResponseCode() == 200) {
                                InputStream inputStream = connection.getInputStream();
                                if (inputStream == null) {
                                    System.out.println("NOTHING");
                                } else {
                                    String filepath = rootpath + name;
                                    File file = new File(filepath);
                                    FileOutputStream fos = null;
                                    int index;
                                    byte[] bytes = new byte[1024];

                                    if (file.exists()) {
                                        file.delete();
                                    }
                                    fos = new FileOutputStream(file);
                                    while ((index = inputStream.read(bytes)) != -1) {
                                        fos.write(bytes, 0, index);
                                        fos.flush();
                                    }
                                    fos.close();
                                    inputStream.close();
                                    listActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(listActivity, "Download Success!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (e instanceof MalformedURLException) {
                                //Toast.makeText(MainActivity.this, "PATH WRONG", Toast.LENGTH_LONG).show();
                                System.out.println("PATH WRONG");
                            } else if (e instanceof TimeoutException) {
                                //Toast.makeText(MainActivity.this, "TIME OUT", Toast.LENGTH_LONG).show();
                                System.out.println("TIME OUT");
                            } else if (e instanceof IOException) {
                                //Toast.makeText(MainActivity.this, "DATA WRONG", Toast.LENGTH_LONG).show();
                                System.out.println("DATA WRONG");
                            } else {
                                //Toast.makeText(MainActivity.this, "SOMETHING WRONG", Toast.LENGTH_LONG).show();
                                //System.out.println("SOMETHING WRONG");
                                System.out.println(e.toString());
                            }

                        }
                    }
                }).start();
            }
        });


        return converView;
    }


}
