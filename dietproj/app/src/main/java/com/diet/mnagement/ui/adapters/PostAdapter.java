package com.diet.mnagement.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.diet.mnagement.R;
import com.diet.mnagement.datas.PostData;

import java.util.ArrayList;



public class PostAdapter extends BaseAdapter
{
    private ArrayList<PostData> postDatas = new ArrayList<PostData>();

    private ImageView mImg;
    private TextView postTitleTextView, postWriterTextView;

    public PostAdapter()
    {

    }

    @Override
    public int getCount()
    {
        return postDatas.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_community, parent, false);
        }

        mImg = (ImageView) convertView.findViewById(R.id.img_community);
        postTitleTextView = (TextView) convertView.findViewById(R.id.main_tv_community);
        postWriterTextView = (TextView) convertView.findViewById(R.id.sub_tv_community);

        PostData postData = postDatas.get(position);

        mImg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_public));
        postTitleTextView.setText(postData.getTitle());
        postWriterTextView.setText(postData.getWriterName());

        return convertView;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public PostData getItem(int position)
    {
        return postDatas.get(position);
    }

    public void addItem(PostData postData)
    {
        postDatas.add(0, postData);
    }
}