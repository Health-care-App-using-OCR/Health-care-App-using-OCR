package com.diet.mnagement.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.diet.mnagement.R;
import com.diet.mnagement.datas.UserInfoData;



public class ProfileDialog extends Dialog
{

    private Context context;
    private UserInfoData userInfoData;


    public ProfileDialog(Context context, UserInfoData userInfoData)
    {
        super(context);
        this.context = context;
        this.userInfoData = userInfoData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_profile);
        setDialogSize(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        final TextView nicknameTextView = (TextView) findViewById(R.id.nicknameTextView);
        final TextView genderTextView = (TextView) findViewById(R.id.genderTextView);
        final TextView ageTextView = (TextView) findViewById(R.id.ageTextView);
        final TextView goalKcalTextView = (TextView) findViewById(R.id.goalKcalTextView);
        final TextView goalWeightTextView = (TextView) findViewById(R.id.goalWeightTextView);
        final Button submitButton = (Button) findViewById(R.id.submitButton);
        final Button changeButton = (Button) findViewById(R.id.changeButton);

        nicknameTextView.setText(userInfoData.getNickname());
        genderTextView.setText(userInfoData.getGender().equals("male") ? "남자" : "여자");
        ageTextView.setText(userInfoData.getAge());
        goalKcalTextView.setText(userInfoData.getGoalKcal());
        goalWeightTextView.setText(userInfoData.getGoalWeight());

        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
        changeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
                ChangeInfoDialog changeInfoDialog = new ChangeInfoDialog(context, userInfoData);
                changeInfoDialog.show();
            }
        });
    }

    private void setDialogSize(int width, int height)
    {
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = width;
        params.height = height;
        getWindow().setAttributes(params);
    }

}
