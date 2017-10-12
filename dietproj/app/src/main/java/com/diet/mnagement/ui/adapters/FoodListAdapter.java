package com.diet.mnagement.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.diet.mnagement.R;
import com.diet.mnagement.datas.FoodData;

import java.util.ArrayList;
import java.util.Collections;



public class FoodListAdapter extends BaseAdapter
{
    private ArrayList<FoodData> foodDatas = new ArrayList<FoodData>();

    private TextView tvETH, tvETM, tvEF, tvEK;
    private Context context;

    public FoodListAdapter(Context context, ArrayList<FoodData> foodDatas)
    {
        this.context = context;
        this.foodDatas = foodDatas;
        Collections.reverse(this.foodDatas);
    }

    @Override
    public int getCount()
    {
        return foodDatas.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final int pos = position;
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_food, parent, false);
        }

        tvETH = (TextView) convertView.findViewById(R.id.eat_hour);
        tvETM = (TextView) convertView.findViewById(R.id.eat_min);
        tvEF = (TextView) convertView.findViewById(R.id.eat_food);
        tvEK = (TextView) convertView.findViewById(R.id.eat_kcal);

        FoodData foodData = foodDatas.get(position);

        tvETH.setText(foodData.getTimeHour());
        tvETM.setText(foodData.getTimeMin());
        tvEF.setText(foodData.getName());
        tvEK.setText(foodData.getKcal());

        return convertView;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public FoodData getItem(int position)
    {
        return foodDatas.get(position);
    }

    public int getKcalSum()
    {
        int kcalSum = 0;
        for (int fi = 0; foodDatas.size() > fi; fi++)
        {
            kcalSum += Integer.parseInt(foodDatas.get(fi).getKcal());
        }
        return kcalSum;
    }
}