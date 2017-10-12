package com.diet.mnagement.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.diet.mnagement.R;
import com.diet.mnagement.datas.ReplyData;

import java.util.ArrayList;



public class ReplyAdapter extends BaseAdapter
{
    private ArrayList<ReplyData> replyDatas = new ArrayList<ReplyData>();

    private TextView replyContentTextView, replyWriterTextView;

    public ReplyAdapter()
    {

    }

    @Override
    public int getCount()
    {
        return replyDatas.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_community_reply, parent, false);
        }

        replyContentTextView = (TextView) convertView.findViewById(R.id.reply_content);
        replyWriterTextView = (TextView) convertView.findViewById(R.id.reply_writer);

        ReplyData replyData = replyDatas.get(position);

        replyContentTextView.setText(replyData.getContent());
        replyWriterTextView.setText(replyData.getWriterName());

        return convertView;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public Object getItem(int position)
    {
        return replyDatas.get(position);
    }

    public void addItem(ReplyData replyData)
    {
        replyDatas.add(replyData);
    }
}