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
import android.widget.Toast;

import com.diet.mnagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity
{
    private Button loginButton, joinButton;
    private EditText emailEditText, pwEditText;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (isNetWork() == false)
        {
            Toast.makeText(this, "인터넷연결을 확인 해 주세요", Toast.LENGTH_SHORT).show();
            finish();
        }

        mAuth = FirebaseAuth.getInstance();

        loginButton = (Button) findViewById(R.id.btn_login);
        joinButton = (Button) findViewById(R.id.btn_join);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        pwEditText = (EditText) findViewById(R.id.pwEditText);

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String emailInputText = emailEditText.getText().toString();
                final String pwInputText = pwEditText.getText().toString();
                if (emailInputText == null || emailInputText.length() == 0) return;
                if (pwInputText == null || pwInputText.length() == 0) return;

                mAuth.signInWithEmailAndPassword(emailInputText, pwInputText)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                Log.d("signlog", "signInWithEmail:onComplete:" + task.isSuccessful());
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (task.isSuccessful())
                                {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class)); //메인 화면으로 이동
                                    finish();
                                }
                                else
                                {
                                    Log.w("signlog", "signInWithEmail:failed", task.getException());
                                    Toast.makeText(LoginActivity.this, "이메일 및 비밀번호를 확인하세요",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(LoginActivity.this, JoinActivity.class)); //사용자등록 화면으로 이동
                finish();
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
