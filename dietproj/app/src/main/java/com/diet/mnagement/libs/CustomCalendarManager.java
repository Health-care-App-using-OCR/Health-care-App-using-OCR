package com.diet.mnagement.libs;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.diet.mnagement.datas.CalendarData;
import com.diet.mnagement.ui.adapters.CalendarAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Gyeongrok Kim on 2017-05-17.
 */

public class CustomCalendarManager
{
    private Context context;
    private CalendarAdapter calendarAdapter;
    private Calendar calendar;

    private TextView calendarMonitorTextView = null;

    public CustomCalendarManager(Context context)
    {
        this.context = context;
        this.calendarAdapter = new CalendarAdapter(context);

        this.calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH) + 1;
        this.calendar.set(year, month - 1, 1);
        loadCalendar();
    }

    public Calendar getCalendar()
    {
        return calendar;
    }

    public CalendarAdapter getCalendarAdapter()
    {
        return calendarAdapter;
    }

    public void setCalendarMonitorTextView(TextView calendarMonitorTextView)
    {
        this.calendarMonitorTextView = calendarMonitorTextView;
    }

    public void loadCalendar()
    {
        calendarAdapter.clear();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;

        Log.i("calendar", calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.DATE) + "");

        if (calendarMonitorTextView != null)
        {
            calendarMonitorTextView.setText(year + "년 " + month + "월");
        }

        // 1일의 요일
        int sWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // 빈 날짜 넣기
        for (int i = 1; i < sWeek; i++)
        {
            boolean isTake = false;
            CalendarData item = new CalendarData(Integer.toString(year), Integer.toString(month), isTake);
            calendarAdapter.add(item);
        }

        // 이번 달 마지막 날
        int lDay = getLastDay(year, month);

        for (int i = 1; i <= lDay; i++)
        {
            boolean isTake = true;

            calendar.set(year, calendar.get(Calendar.MONTH), i);
            int week = calendar.get(Calendar.DAY_OF_WEEK);

            CalendarData item = new CalendarData(Integer.toString(year), Integer.toString(month), Integer.toString(i), week, isTake);
            calendarAdapter.add(item);
        }
        calendarAdapter.notifyDataSetChanged();
    }

    // 이전 달
    public void loadLastMonth()
    {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) - 1;
        calendar.set(year, month, 1);

        loadCalendar();
    }

    // 다음 달
    public void loadNextMonth()
    {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        calendar.set(year, month, 1);

        loadCalendar();
    }

    // 특정월의 마지막 날짜
    private int getLastDay(int year, int month)
    {
        Date d = new Date(year, month, 1);
        d.setHours(d.getDay() - 1 * 24);
        SimpleDateFormat f = new SimpleDateFormat("dd");
        return Integer.parseInt(f.format(d));
    }
}
