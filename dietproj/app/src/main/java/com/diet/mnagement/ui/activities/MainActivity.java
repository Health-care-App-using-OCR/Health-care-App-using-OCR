package com.diet.mnagement.ui.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.diet.mnagement.R;
import com.diet.mnagement.datas.CalendarData;
import com.diet.mnagement.datas.DateLogData;
import com.diet.mnagement.datas.UserInfoData;
import com.diet.mnagement.libs.CustomCalendarManager;
import com.diet.mnagement.libs.ocr.OcrListActivity;
import com.diet.mnagement.ui.dialogs.AddFoodDialog;
import com.diet.mnagement.ui.dialogs.AddOcrFoodDialog;
import com.diet.mnagement.ui.dialogs.ChangeInfoDialog;
import com.diet.mnagement.ui.dialogs.DateLogDialog;
import com.diet.mnagement.ui.dialogs.ProfileDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    Intent intent1;
    String listFood;

    private GridView calendarGridView;
    private TextView calendarMonitorView;
    private TextView goalKcalTextView, goalWeightTextView;
    private ImageButton loadLastButton, loadNextButton;

    private UserInfoData currentUserInfoData;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private AddFoodDialog addFoodDialog;
    private AddOcrFoodDialog addOcrFoodDialog;


    private FirebaseAuth.AuthStateListener mAuthListener;

    // 파이어베이스와 연동해 값을 가져온 상태인지
    private boolean userDataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("목표 달성 도우미");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (isNetWork() == false)
        {
            Toast.makeText(this, "인터넷연결을 확인 해 주세요", Toast.LENGTH_SHORT).show();
            finish();
        }

        PermissionListener permissionlistener = new PermissionListener()
        {
            @Override
            public void onPermissionGranted()
            {
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions)
            {
                Toast.makeText(MainActivity.this, "권한 거부시 앱사용 불가" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        new TedPermission(MainActivity.this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("앱사용을 위한 권한 필요합니다.")
                .setDeniedMessage("권한 거부시 정상적인 기능구동이 불가능합니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .check();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // 뷰 로드
        calendarMonitorView = (TextView) findViewById(R.id.calendarMonitorView);
        calendarGridView = (GridView) findViewById(R.id.gridview);
        loadLastButton = (ImageButton) findViewById(R.id.loadLastButton);
        loadNextButton = (ImageButton) findViewById(R.id.loadNextButton);
        goalKcalTextView = (TextView) findViewById(R.id.goalKcalTextView);
        goalWeightTextView = (TextView) findViewById(R.id.goalWeightTextView);

        // 파이어베이스 auth & 데이터베이스 로드
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("회원정보 로드중..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener()
        {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    if (!userDataLoaded)
                        mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    currentUserInfoData = dataSnapshot.getValue(UserInfoData.class);
                                    goalWeightTextView.setText(currentUserInfoData.getGoalWeight() + "kg");
                                    goalKcalTextView.setText(currentUserInfoData.getGoalKcal() + "kcal");

                                    if (!userDataLoaded)
                                    {
                                        Toast.makeText(MainActivity.this, currentUserInfoData.getNickname() + "님 환영합니다.",
                                                Toast.LENGTH_SHORT).show();
                                        userDataLoaded = true;
                                        constructCalendar();
                                        progressDialog.dismiss();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "올바르지 않은 계정생성입니다.",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {
                                Log.d("databaselog", databaseError.getDetails());
                            }
                        });
                    // User is signed in
                    Log.d("signlog", "onAuthStateChanged:signed_in:" + user.getUid());
                }
                else
                {
                    // 유저 출타 상태 -> 로그인
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    Log.d("signlog", "onAuthStateChanged:signed_out");
                }
            }
        };

        intent1 = new Intent();
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            listFood= extras.getString("listFood");
            addOcrFoodDialog = new AddOcrFoodDialog(MainActivity.this, currentUserInfoData, listFood);
            addOcrFoodDialog.show();

        }











    }

    public void constructCalendar()
    {
        final CustomCalendarManager calendarManager = new CustomCalendarManager(this);
        calendarManager.setCalendarMonitorTextView(calendarMonitorView);
        calendarManager.loadCalendar();

        calendarGridView.setAdapter(calendarManager.getCalendarAdapter());

        calendarManager.loadLastMonth();
        calendarManager.loadNextMonth();

        loadLastButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                calendarManager.loadLastMonth();
            }
        });

        loadNextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                calendarManager.loadNextMonth();
            }
        });

        calendarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
            {
                if (!calendarManager.getCalendarAdapter().getItem(position).isTake()) return;
                Log.d("calendarClick", position + "");
                if (calendarManager.getCalendarAdapter().isFutureItem(position, Calendar.getInstance()))
                {
                    Toast.makeText(MainActivity.this, "미래의 날짜는 열람할 수 없습니다", Toast.LENGTH_SHORT).show();
                    return;
                }
                final CalendarData selectedDate = calendarManager.getCalendarAdapter().getItem(position);
                final String selectedDataString = selectedDate.year() + "-" + selectedDate.month() + "-" + selectedDate.day();

                final Calendar currentCal = Calendar.getInstance();
                final String currentDateString = currentCal.get(Calendar.YEAR) + "-" + (currentCal.get(Calendar.MONTH) + 1) +
                        "-" + currentCal.get(Calendar.DAY_OF_MONTH);

                final DatabaseReference dateLogReference = mDatabase.child("dates").child(currentUserInfoData.getUid()).child(selectedDataString);
                dateLogReference.addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            final DateLogData dateLogData = dataSnapshot.getValue(DateLogData.class);
                            DateLogDialog dateLogDialog = new DateLogDialog(MainActivity.this, dateLogData);
                            dateLogDialog.show();
                            dateLogReference.removeEventListener(this);
                        }
                        else
                        {
                            // 해당날짜가 오늘일 경우 = 새로운 값생성, 이전이거나 미래일경우 = 오류 박ㄹ생
                            if (!selectedDataString.equals(currentDateString))
                            {
                                Toast.makeText(MainActivity.this, "해당 날짜의 기록이 없습니다", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            DateLogData dateLogData = new DateLogData();
                            dateLogData.initData(selectedDataString, currentUserInfoData);
                            dateLogReference.setValue(dateLogData);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        Log.d("databaselog", databaseError.getDetails());
                    }
                });
            }
        });

        mDatabase.child("dates").child(currentUserInfoData.getUid()).addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                final DateLogData dateLogData = dataSnapshot.getValue(DateLogData.class);
                dateLogData.setDateStr(dataSnapshot.getKey());
                calendarManager.getCalendarAdapter().addExistSyncData(dateLogData);
                calendarManager.getCalendarAdapter().notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                final DateLogData dateLogData = dataSnapshot.getValue(DateLogData.class);
                dateLogData.setDateStr(dataSnapshot.getKey());
                calendarManager.getCalendarAdapter().addExistSyncData(dateLogData);
                calendarManager.getCalendarAdapter().notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s)
            {
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }

    public void onFabClick(View v)
    {
        switch (v.getId())
        {
            case R.id.changeWeightButton:
                ChangeInfoDialog changeInfoDialog = new ChangeInfoDialog(MainActivity.this, currentUserInfoData);
                changeInfoDialog.show();
                break;

            case R.id.addByFoodButton:
                addFoodDialog = new AddFoodDialog(MainActivity.this, currentUserInfoData);
                addFoodDialog.show();
                break;
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account)
        {
            if (userDataLoaded)
            {
                ProfileDialog profileDialog = new ProfileDialog(this, currentUserInfoData);
                profileDialog.show();
            }
        }
        else if (id == R.id.nav_community)
        {
            startActivity(new Intent(MainActivity.this, CommunityActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
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