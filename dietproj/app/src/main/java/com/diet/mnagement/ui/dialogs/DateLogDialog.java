package com.diet.mnagement.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.diet.mnagement.R;
import com.diet.mnagement.datas.DateLogData;
import com.diet.mnagement.ui.adapters.FoodListAdapter;



public class DateLogDialog extends Dialog
{

    private Context context;
    private DateLogData dateLogData;


    public DateLogDialog(Context context, DateLogData dateLogData)
    {
        super(context);
        this.context = context;
        this.dateLogData = dateLogData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_datelog);
        setDialogSize(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        final TextView dateTextView = (TextView) findViewById(R.id.dateTextView);
        final TextView weightTextView = (TextView) findViewById(R.id.currentWeightTextView);
        final TextView goalWeightTextView = (TextView) findViewById(R.id.goalWeightTextView);
        final TextView remainWeightTextView = (TextView) findViewById(R.id.remainWeightTextView);
        final TextView intakeKcalTextView = (TextView) findViewById(R.id.intakeKcalTextView);
        final TextView goalKcalTextView = (TextView) findViewById(R.id.goalKcalTextView);

        final ListView ateFoodListView = (ListView) findViewById(R.id.ateFoodListView);
        FoodListAdapter foodAdapter = new FoodListAdapter(context, dateLogData.getAteFoodDatas());
        ateFoodListView.setAdapter(foodAdapter);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.kcalProgress);
        progressBar.setMax(Integer.parseInt(dateLogData.getGoalKcal()));
        progressBar.setProgress(foodAdapter.getKcalSum());

        dateTextView.setText(dateLogData.getDate());
        weightTextView.setText(dateLogData.getWeight() + "kg");
        goalWeightTextView.setText(dateLogData.getGoalWeight() + "kg");
        remainWeightTextView.setText(Integer.parseInt(dateLogData.getWeight()) - Integer.parseInt(dateLogData.getGoalWeight()) + "kg");
        intakeKcalTextView.setText(String.valueOf(foodAdapter.getKcalSum()) + "kcal");
        goalKcalTextView.setText(dateLogData.getGoalKcal() + "kcal");
    }


    private void setDialogSize(int width, int height)
    {
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = width;
        params.height = height;
        getWindow().setAttributes(params);
    }

}