package com.example.c_changing.main;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class BpmDialog extends AlertDialog implements View.OnClickListener {
    private EditText mbpm;
    private EditText mbar;
    private Button mBtnYes;
    private Context mContext;
    public int bpm = 80;
    public int num = 2;
    private LoopActivity activity;

    public BpmDialog(Context context,LoopActivity activ){
        super(context);
        mContext = context;
        this.activity = activ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout);
        mbpm = (EditText)findViewById(R.id.bpm);
        mbar = (EditText)findViewById(R.id.bar);

        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.setCancelable(false);
        mBtnYes = (Button)findViewById(R.id.yes);
        mBtnYes.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        int bb = 0;
        this.dismiss();
        if(TextUtils.isEmpty(mbpm.getText()))
            bb = 80;
        else bb = Integer.parseInt(mbpm.getText().toString());

        if(TextUtils.isEmpty(mbar.getText()))
            num = 2;
        else num = Integer.parseInt(mbar.getText().toString());
        if(bb != bpm){
            bpm = bb;
            activity.StopMetronome();
        }

    }

}
