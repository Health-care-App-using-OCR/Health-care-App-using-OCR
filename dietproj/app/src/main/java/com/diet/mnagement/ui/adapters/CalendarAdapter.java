package com.diet.mnagement.ui.adapters;



import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.diet.mnagement.R;
import com.diet.mnagement.datas.CalendarData;
import com.diet.mnagement.datas.DateLogData;

import java.util.ArrayList;
import java.util.Calendar;

public class CalendarAdapter extends BaseAdapter
{
    private ArrayList<CalendarData> calendarDatas = new ArrayList<CalendarData>();
    private ArrayList<DateLogData> existSyncDatas = new ArrayList<>();

    private LayoutInflater mInflater;
    private Context mContext;

    public CalendarAdapter(Context context)
    {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public void add(CalendarData item)
    {
        calendarDatas.add(item);
    }

    public void clear()
    {
        calendarDatas.clear();
    }

    @Override
    public int getCount()
    {
        return calendarDatas.size();
    }

    @Override
    public CalendarData getItem(int position)
    {
        return calendarDatas.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v;
        if (convertView == null)
        {
            v = mInflater.inflate(R.layout.item_main_calendar, null);
        }
        else
        {
            v = convertView;
        }

        final TextView day = (TextView) v.findViewById(R.id.day);
        final TextView dayEatTextView = (TextView) v.findViewById(R.id.dayEatTextView);
        final TextView dayKgTextView = (TextView) v.findViewById(R.id.dayKgTextView);
        ImageView take = (ImageView) v.findViewById(R.id.day_check);
        day.setText(calendarDatas.get(position).day());

        int week = calendarDatas.get(position).week();
        if (week == 1)
            day.setTextColor(Color.RED);
        else if (week == 7)
            day.setTextColor(Color.BLUE);

        day.setVisibility(View.VISIBLE);

        if (!calendarDatas.get(position).isTake())
        {
            return v;
        }

        final CalendarData selectedDate = getItem(position);
        final String selectedDataString = selectedDate.year() + "-" + selectedDate.month() + "-" + selectedDate.day();

        DateLogData dateLogData = getSyncData(selectedDataString);
        if (dateLogData != null)
        {
            take.setVisibility(View.VISIBLE);
            dayEatTextView.setVisibility(View.VISIBLE);
            dayKgTextView.setVisibility(View.VISIBLE);

            FoodListAdapter foodAdapter = new FoodListAdapter(mContext, dateLogData.getAteFoodDatas());
            dayEatTextView.setText("섭취 " + foodAdapter.getKcalSum());
            dayKgTextView.setText("체중 " + dateLogData.getWeight());
            try
            {
                if (Integer.parseInt(dateLogData.getGoalKcal()) > foodAdapter.getKcalSum())
                {
                    take.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_check_circle));
                }
                else
                {
                    take.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_cross_circle));
                }
            } catch (Exception e)
            {

            }
        }
        else
        {
            take.setVisibility(View.GONE);
            dayEatTextView.setVisibility(View.GONE);
            dayKgTextView.setVisibility(View.GONE);
        }
        return v;
    }


    public boolean isFutureItem(int position, Calendar targetCalendar)
    {
        final int year = targetCalendar.get(Calendar.YEAR);
        final int month = targetCalendar.get(Calendar.MONTH) + 1;
        final int day = targetCalendar.get(Calendar.DAY_OF_MONTH);

        CalendarData calendarData = calendarDatas.get(position);
        if (year < Integer.parseInt(calendarData.year()))
        {
            return true;
        }
        if (month < Integer.parseInt(calendarData.month()))
        {
            return true;
        }
        if (day < Integer.parseInt(calendarData.day()))
        {
            return true;
        }
        return false;
    }

    public DateLogData getSyncData(String dateStr)
    {
        for (int di = 0; existSyncDatas.size() > di; di++)
        {
            if (existSyncDatas.get(di).getDateStr().equals(dateStr)) return existSyncDatas.get(di);
        }
        return null;
    }

    public void addExistSyncData(DateLogData dateLogData)
    {
        DateLogData duplicateCheck = getSyncData(dateLogData.getDateStr());
        if (duplicateCheck != null) existSyncDatas.remove(duplicateCheck);

        existSyncDatas.add(dateLogData);
    }
}

