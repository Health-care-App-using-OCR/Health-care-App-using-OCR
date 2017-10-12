package com.diet.mnagement.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.diet.mnagement.R;
import com.diet.mnagement.datas.UserInfoData;
import com.diet.mnagement.libs.utils.VerifyUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class JoinActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        if (isNetWork() == false)
        {
            Toast.makeText(this, "인터넷연결을 확인 해 주세요", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 파이어베이스 인증, 데이터베이스 로드
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        final EditText nickEditText = (EditText) findViewById(R.id.nickEditText);
        final EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        final EditText pwEditText = (EditText) findViewById(R.id.pwEditText);
        final EditText pwChkEditText = (EditText) findViewById(R.id.pwChkEditText);

        final RadioGroup genderRadioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);

        final EditText ageEditText = (EditText) findViewById(R.id.ageEditText);
        final EditText currentWeightEditText = (EditText) findViewById(R.id.currentWeightEditText);
        final EditText goalKcalEditText = (EditText) findViewById(R.id.goalKcalEditText);
        final EditText goalWeightEditText = (EditText) findViewById(R.id.goalWeightEditText);

        final Button submitButton = (Button) findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String nickInputText = nickEditText.getText().toString();
                final String emailInputText = emailEditText.getText().toString();
                final String pwInputText = pwEditText.getText().toString();
                final String pwChkInputText = pwChkEditText.getText().toString();

                final int genderSelectedID = genderRadioGroup.getCheckedRadioButtonId();
                final String genderSelectedText = genderSelectedID == R.id.manRadio ? "male" : "female";

                final String ageInputText = ageEditText.getText().toString();
                final String currentWeightInputText = currentWeightEditText.getText().toString();
                final String goalKcalInputText = goalKcalEditText.getText().toString();
                final String goalWeightInputText = goalWeightEditText.getText().toString();

                if (!pwInputText.equals(pwChkInputText))
                {
                    Toast.makeText(JoinActivity.this, "비밀번호와 비밀번호 확인란이 다릅니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 문자열 체크
                if (!VerifyUtil.verifyStrings(nickInputText, emailInputText, pwInputText,
                        ageInputText, currentWeightInputText, goalKcalInputText, goalWeightInputText))
                {
                    Toast.makeText(JoinActivity.this, "모든 입력창을 채워주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 회원 추가정보 구성
                final UserInfoData registUserData = new UserInfoData();
                registUserData.setNickname(nickInputText);
                registUserData.setAge(ageInputText);
                registUserData.setCurrentWeight(currentWeightInputText);
                registUserData.setGender(genderSelectedText);
                registUserData.setGoalKcal(goalKcalInputText);
                registUserData.setGoalWeight(goalWeightInputText);

                mAuth.createUserWithEmailAndPassword(emailInputText, pwInputText)
                        .addOnCompleteListener(JoinActivity.this, new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                Log.d("signlog", "createUserWithEmail:onComplete:" + task.isSuccessful());

                                if (task.isSuccessful())
                                {
                                    // 회원가입 성공 -> 사용자 상세 정보등록과정
                                    final FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener()
                                    {
                                        @Override
                                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
                                        {
                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            if (user != null)
                                            {
                                                // 파이어베이스 어쓰를 통한 가입후 생성된 UID
                                                registUserData.setUid(user.getUid());

                                                mDatabase.child("users").child(user.getUid()).setValue(registUserData);
                                                Toast.makeText(JoinActivity.this, "회원가입완료",
                                                        Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(JoinActivity.this, MainActivity.class));
                                                finish();

                                                Log.d("signlog", "onAuthStateChanged:signed_in:" + user.getUid());
                                            }
                                            else
                                            {
                                                Toast.makeText(JoinActivity.this, "회원가입중 오류가 발생했습니다.",
                                                        Toast.LENGTH_SHORT).show();

                                                Log.d("signlog", "onAuthStateChanged:signed_out");
                                            }
                                            mAuth.removeAuthStateListener(this);
                                        }
                                    };
                                    mAuth.addAuthStateListener(mAuthListener);
                                }
                                else
                                {
                                    // 회원가입 실패
                                    Toast.makeText(JoinActivity.this, task.getException().getLocalizedMessage() + "",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });
            }
        });
    }

    private Boolean isNetWork()
    {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isMobileAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
        boolean isMobileConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        boolean isWifiAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
        boolean isWifiConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();

        if ((isWifiAvailable && isWifiConnect) || (isMobileAvailable && isMobileConnect))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
