package com.diet.mnagement.datas;

import java.util.ArrayList;


public class DateLogData
{
    private String date;
    private String weight;
    private String goalWeight;

    private String goalKcal;
    private String dateStr;

    private ArrayList<FoodData> ateFoodDatas;


    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getWeight()
    {
        return weight;
    }

    public void setWeight(String weight)
    {
        this.weight = weight;
    }

    public String getGoalWeight()
    {
        return goalWeight;
    }

    public void setGoalWeight(String goalWeight)
    {
        this.goalWeight = goalWeight;
    }

    public String getGoalKcal()
    {
        return goalKcal;
    }

    public void setGoalKcal(String goalKcal)
    {
        this.goalKcal = goalKcal;
    }

    public ArrayList<FoodData> getAteFoodDatas()
    {
        if (ateFoodDatas == null) ateFoodDatas = new ArrayList<FoodData>();
        return ateFoodDatas;
    }

    public void setAteFoodDatas(ArrayList<FoodData> ateFoodDatas)
    {
        this.ateFoodDatas = ateFoodDatas;
    }

    public String getDateStr()
    {
        return dateStr;
    }

    public void setDateStr(String dateStr)
    {
        this.dateStr = dateStr;
    }

    public void initData(final String selectedDataString,
                         final UserInfoData currentUserInfoData)
    {
        // 등록된 데이트 로그가 없으면 새로운 데이터 로그 생성
        this.setDate(selectedDataString);
        this.setWeight(currentUserInfoData.getCurrentWeight());
        this.setGoalWeight(currentUserInfoData.getGoalWeight());
        this.setGoalKcal(currentUserInfoData.getGoalKcal());
        ArrayList<FoodData> foodDatas = new ArrayList<FoodData>();
        this.setAteFoodDatas(foodDatas);

    }
}
