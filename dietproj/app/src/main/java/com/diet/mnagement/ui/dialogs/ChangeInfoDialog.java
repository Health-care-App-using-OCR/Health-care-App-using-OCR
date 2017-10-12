package com.diet.mnagement.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.diet.mnagement.R;
import com.diet.mnagement.datas.DateLogData;
import com.diet.mnagement.datas.UserInfoData;
import com.diet.mnagement.libs.utils.VerifyUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;



public class ChangeInfoDialog extends Dialog
{

    private Context context;
    private UserInfoData userInfoData;

    private DatabaseReference mDatabase;


    public ChangeInfoDialog(Context context, UserInfoData userInfoData)
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
        setContentView(R.layout.dialog_changeinfo);
        setDialogSize(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final EditText currentWeightEditText = (EditText) findViewById(R.id.currentWeightEditText);
        final EditText goalWeightEditText = (EditText) findViewById(R.id.goalWeightEditText);
        final EditText goalKcalEditText = (EditText) findViewById(R.id.goalKcalEditText);
        final Button submitButton = (Button) findViewById(R.id.submitButton);

        currentWeightEditText.setText(userInfoData.getCurrentWeight());
        goalWeightEditText.setText(userInfoData.getGoalWeight());
        goalKcalEditText.setText(userInfoData.getGoalKcal());

        final Calendar currentCal = Calendar.getInstance();
        final String currentDateString = currentCal.get(Calendar.YEAR) + "-" + (currentCal.get(Calendar.MONTH) + 1) +
                "-" + currentCal.get(Calendar.DAY_OF_MONTH);

        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String currentWeightInputText = currentWeightEditText.getText().toString();
                final String goalWeightInputText = goalWeightEditText.getText().toString();
                final String goalKcalInputText = goalKcalEditText.getText().toString();

                if (!VerifyUtil.verifyStrings(currentWeightInputText, goalWeightInputText, goalKcalInputText))
                {
                    Toast.makeText(context, "모든필드가 채워져야 합니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 사용자 정보에 포함된 정보 업데이트 실행
                userInfoData.setCurrentWeight(currentWeightInputText);
                userInfoData.setGoalKcal(goalKcalInputText);
                userInfoData.setGoalWeight(goalWeightInputText);
                mDatabase.child("users").child(userInfoData.getUid()).setValue(userInfoData);

                final DatabaseReference dateLogReference = mDatabase.child("dates").child(userInfoData.getUid()).child(currentDateString);
                dateLogReference.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        // 해당작업은 한번만 (변화체크리스너 제거)
                        dateLogReference.removeEventListener(this);
                        if (dataSnapshot.exists())
                        {
                            dateLogReference.child("goalKcal").setValue(goalKcalInputText);
                            dateLogReference.child("goalWeight").setValue(goalWeightInputText);
                            dateLogReference.child("weight").setValue(currentWeightInputText);
                        }
                        else
                        {
                            // 해당 날짜에 생성된 데이트로그가 없는경우
                            DateLogData dateLogData = new DateLogData();
                            dateLogData.initData(currentDateString, userInfoData);
                            dateLogReference.setValue(dateLogData);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        Log.d("databaselog", databaseError.getDetails());
                    }
                });

                dismiss();
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