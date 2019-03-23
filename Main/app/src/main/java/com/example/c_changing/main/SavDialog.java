package com.example.c_changing.main;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

public class SavDialog extends AlertDialog implements View.OnClickListener {
    private EditText name;
    private Button mBtnYes;
    private Button mBtnDel;
    private Button mBtnCan;
    private Context mContext;
    public int position;
    public String savename;
    private LoopActivity activity;

    public SavDialog(Context context, LoopActivity act) {
        super(context);
        mContext = context;
        activity = act;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sav_layout);
        name = (EditText) findViewById(R.id.name);

        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.setCancelable(false);
        mBtnYes = (Button) findViewById(R.id.sav);
        mBtnDel = (Button) findViewById(R.id.del);
        mBtnCan = (Button) findViewById(R.id.cancel);
        mBtnYes.setOnClickListener(this);
        mBtnDel.setOnClickListener(this);
        mBtnCan.setOnClickListener(this);

    }

    @Override


    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.sav:
                if (TextUtils.isEmpty(name.getText())) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("警告").setMessage("姓名输入不能为空，请重新输入！！！")
                            .setPositiveButton("确定", null).show();
                    return;
                } else {
                    savename = name.getText().toString();
                    String c = activity.TempMap.get(position+".wav");
                    activity.copyFile(activity.tempPath+"/"+c,activity.rootPath+"/"+c.substring(0,c.indexOf("-")+1)+savename+".wav");
                    break;
                }
            case R.id.del:
                File file = new File(activity.tempPath+"/"+activity.TempMap.get(position+".wav"));
                file.delete();
                activity.adapter.notifyDataSetChanged();
                break;
            case R.id.cancel:
                break;
        }
        this.dismiss();
    }

}
