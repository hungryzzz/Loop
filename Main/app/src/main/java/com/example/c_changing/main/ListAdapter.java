package com.example.c_changing.main;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class
ListAdapter extends BaseAdapter {
    private List<Map<String, Object>> mlist;
    private LayoutInflater mInflater = null;
    private LoopActivity activity;
    private String temp;
    public ListAdapter(Context context, List<Map<String, Object>> list, LoopActivity act){
        mInflater = LayoutInflater.from(context);
        mlist = list;
        activity = act;
    }
    static class ViewHolder{
        public TextView title;
    }

    @Override
    public int getCount() { return mlist.size(); }

    @Override
    public Object getItem(int position) { return mlist.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View converView, ViewGroup parent){
        ViewHolder holder = null;
        final TextView head;

        if(converView == null){
            holder = new ViewHolder();

            converView = mInflater.inflate(R.layout.list_item, null);
            holder.title = (TextView)converView.findViewById(R.id.title);
            converView.setTag(holder);
        } else{
            holder = (ViewHolder)converView.getTag();
        }
        temp = (String)mlist.get(position).get("title");
        int c = temp.indexOf("-");
        holder.title.setText(temp.substring(c+1));
        head = (TextView)converView.findViewById(R.id.title);
        head.setTag("qq"+ position);

        head.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item((CharSequence)v.getTag());

                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData dragData = new ClipData(v.getTag().toString(),
                        mimeTypes, item);

                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(head);
                activity.tmp_s = activity.SavMap.get((String)head.getText());
                activity.tmp = head;
                v.startDrag(dragData, myShadow, null, 0);

                return true;
            }
        });

        return converView;
    }
}
