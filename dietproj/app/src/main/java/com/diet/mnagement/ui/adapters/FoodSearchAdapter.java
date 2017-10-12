package com.diet.mnagement.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.diet.mnagement.R;
import com.diet.mnagement.datas.FoodDBData;

import java.util.ArrayList;



public class FoodSearchAdapter extends BaseAdapter
{

    private Context context;
    private ArrayList<FoodDBData> foodDBDatas = new ArrayList<>();

    public FoodSearchAdapter(Context context)
    {
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return foodDBDatas.size();
    }

    @Override
    public FoodDBData getItem(int position)
    {
        return foodDBDatas.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.item_foodsearch, parent, false);

        FoodDBData foodDBData = foodDBDatas.get(position);
        final TextView foodNameTextView = (TextView) convertView.findViewById(R.id.foodNameTextView);
        final TextView foodKcalTextView = (TextView) convertView.findViewById(R.id.foodKcalTextView);

        foodNameTextView.setText(foodDBData.getFOOD_NAME());
        foodKcalTextView.setText(String.valueOf(foodDBData.getFOOD_KCAL()) + "kcal");
        return convertView;
    }

    public void add(FoodDBData foodDBData)
    {
        foodDBDatas.add(foodDBData);
    }

    public void clear()
    {
        foodDBDatas.clear();
    }
}
