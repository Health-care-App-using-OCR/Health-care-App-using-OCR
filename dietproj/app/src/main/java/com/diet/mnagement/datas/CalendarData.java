package com.diet.mnagement.datas;



public class CalendarData
{
    private String mYear, mMonth, mDay = "";
    private boolean mIsTake;
    private int mWeek = 0;

    public CalendarData(String y, String m, boolean isTake)
    {
        mYear = y;
        mMonth = m;
        mIsTake = isTake;
    }

    public CalendarData(String y, String m, String d, int week, boolean isTake)
    {
        mYear = y;
        mMonth = m;
        mDay = d;
        mWeek = week;
        mIsTake = isTake;
    }

    public String year()
    {
        return mYear;
    }

    public String month()
    {
        return mMonth;
    }

    public String day()
    {
        return mDay;
    }

    public Boolean isTake()
    {
        return mIsTake;
    }

    public int week()
    {
        return mWeek;
    }
}
