package com.diet.mnagement.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.diet.mnagement.R;
import com.diet.mnagement.datas.DateLogData;
import com.diet.mnagement.datas.FoodDBData;
import com.diet.mnagement.datas.FoodData;
import com.diet.mnagement.datas.UserInfoData;
import com.diet.mnagement.libs.ocr.OCRActivity;
import com.diet.mnagement.libs.utils.VerifyUtil;
import com.diet.mnagement.ui.adapters.FoodSearchAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

/**
 * Created by kmh on 2017-06-20.
 */

public class AddOcrFoodDialog extends Dialog implements Serializable {

    private Context context;
    private UserInfoData currentUserInfo;
    private DatabaseReference mDatabase;
    private FoodSearchAdapter foodSearchAdapter;
    private EditText foodNameEditText;
    private String listFood;

    public AddOcrFoodDialog(Context context, UserInfoData currentUserInfo, String listFood)
    {
        super(context);
        this.context = context;
        this.currentUserInfo = currentUserInfo;
        this.listFood = listFood;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_addfood);
        setDialogSize(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final Calendar currentCal = Calendar.getInstance();
        final String currentDateString = currentCal.get(Calendar.YEAR) + "-" + (currentCal.get(Calendar.MONTH) + 1) +
                "-" + currentCal.get(Calendar.DAY_OF_MONTH);

        foodNameEditText = (EditText) findViewById(R.id.foodNameEditText);

        final ImageButton foodVoiceButton = (ImageButton) findViewById(R.id.foodVoiceButton);
        final ImageButton foodSearchButton = (ImageButton) findViewById(R.id.foodSearchButton);
        final ImageButton foodOCRButton = (ImageButton) findViewById(R.id.foodOCRButton);

        foodNameEditText.setText(listFood);




       /* foodOCRButton.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                SharedPreferences prePref = context.getSharedPreferences("prename", 0);
                final SharedPreferences.Editor prePrefEditor = prePref.edit();

                AlertDialog.Builder preSetDialog = new AlertDialog.Builder(context);

                preSetDialog.setTitle("검색될 이름 설정");
                preSetDialog.setMessage("검색될 이름을 설정하십시오");

                final EditText preFoodNameEditText = new EditText(context);
                preFoodNameEditText.setText(prePref.getString("prename", ""));
                preSetDialog.setView(preFoodNameEditText);

                preSetDialog.setPositiveButton("확인", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        prePrefEditor.putString("prename", preFoodNameEditText.getText().toString());
                        prePrefEditor.apply();
                    }
                });

                preSetDialog.setNegativeButton("취소", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {

                    }
                });

                preSetDialog.show();
                return false;
            }
        });
        */
        // 검색 결과 리스트뷰
        foodSearchAdapter = new FoodSearchAdapter(context);
        final ListView searchFoodListView = (ListView) findViewById(R.id.searchFoodListView);
        searchFoodListView.setAdapter(foodSearchAdapter);
        searchFoodListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                final FoodDBData foodDBData = foodSearchAdapter.getItem(position);

                AlertDialog.Builder submitAlert = new AlertDialog.Builder(context);
                submitAlert.setMessage(foodDBData.getFOOD_NAME() + "를 등록하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                final FoodData newFood = new FoodData();
                                newFood.setTimeHour(currentCal.get(Calendar.HOUR_OF_DAY) + "");
                                newFood.setTimeMin(currentCal.get(Calendar.MINUTE) + "");
                                newFood.setName(foodDBData.getFOOD_NAME());
                                newFood.setKcal(String.valueOf(foodDBData.getFOOD_KCAL()));

                                final DatabaseReference dateLogReference = mDatabase.child("dates").child(currentUserInfo.getUid()).child(currentDateString);
                                dateLogReference.addValueEventListener(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        // 해당작업은 한번만 (변화체크리스너 제거)
                                        dateLogReference.removeEventListener(this);
                                        if (dataSnapshot.exists())
                                        {
                                            DateLogData dateLogData = dataSnapshot.getValue(DateLogData.class);
                                            dateLogData.getAteFoodDatas().add(newFood);
                                            dateLogReference.getRef().setValue(dateLogData);
                                        }
                                        else
                                        {
                                            // 해당 날짜에 생성된 데이트로그가 없는경우
                                            DateLogData dateLogData = new DateLogData();
                                            dateLogData.initData(currentDateString, currentUserInfo);
                                            dateLogData.getAteFoodDatas().add(newFood);
                                            dateLogReference.setValue(dateLogData);
                                        }
                                        dismiss();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {
                                        Log.d("databaselog", databaseError.getDetails());
                                    }
                                });
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = submitAlert.create();
                alert.show();
            }
        });

        foodOCRButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                ((Activity) context).startActivityForResult(intent, 1);*/

                Intent ocrIntent = new Intent(context, OCRActivity.class);
                ((Activity) context).startActivity(ocrIntent);

                /* SharedPreferences prePref = context.getSharedPreferences("prename", 0);
                searchFood(prePref.getString("prename", ""));
            */

            }




        });

        foodVoiceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                final ProgressDialog mProgressDialog = ProgressDialog.show(context, "",
                        "검색할 음식 이름을 말하세요", true);
                // 음성인식 리스너
                final RecognitionListener listener = new RecognitionListener()
                {

                    @Override
                    public void onRmsChanged(float rmsdB)
                    {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onResults(Bundle results)
                    {
                        mProgressDialog.dismiss();
                        String key = "";
                        key = SpeechRecognizer.RESULTS_RECOGNITION;
                        ArrayList<String> mResult = results.getStringArrayList(key);
                        String[] rs = new String[mResult.size()];
                        mResult.toArray(rs);
                        Log.d("sppechLog", rs[0]);
                        foodNameEditText.setText(rs[0]);
                        searchFood(rs[0]);
                    }

                    @Override
                    public void onReadyForSpeech(Bundle params)
                    {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onPartialResults(Bundle partialResults)
                    {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onEvent(int eventType, Bundle params)
                    {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onError(int error)
                    {
                        mProgressDialog.dismiss();

                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onEndOfSpeech()
                    {
                        mProgressDialog.dismiss();
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onBufferReceived(byte[] buffer)
                    {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onBeginningOfSpeech()
                    {
                        // TODO Auto-generated method stub

                    }
                };

                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

                SpeechRecognizer mRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
                mRecognizer.setRecognitionListener(listener);
                mRecognizer.startListening(i);
            }
        });

        foodSearchButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                searchFood(foodNameEditText.getText().toString());
            }
        });
    }

    public void searchFood(String searchQuery)
    {
        foodNameEditText.setText(searchQuery);
        foodSearchAdapter.clear();
        foodSearchAdapter.notifyDataSetChanged();

        if (!VerifyUtil.verifyStrings(searchQuery))
        {
            return;
        }

        Toast.makeText(context, "검색까지 많은 시간이 소요될 수 있습니다", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "잠시만 기다려주세요", Toast.LENGTH_SHORT).show();
        final DatabaseReference foodsReference = mDatabase.child("foods");
        final Query foodsQuery = foodsReference.orderByChild("FOOD_NAME")
                .startAt(searchQuery)
                .endAt(searchQuery + "\uf8ff")
                .limitToFirst(10);
        foodsQuery.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                FoodDBData foodDBData = new FoodDBData();
                foodDBData.setFOOD_NAME(dataSnapshot.child("FOOD_NAME").getValue(String.class));
                foodDBData.setFOOD_KCAL(dataSnapshot.child("FOOD_KCAL").getValue(Integer.class));
                Log.d("queryLog", foodDBData.getFOOD_NAME() + "|" + foodDBData.getFOOD_KCAL());
                foodSearchAdapter.add(foodDBData);
                foodSearchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {

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


    private void setDialogSize(int width, int height)
    {
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.width = width;
        params.height = height;
        getWindow().setAttributes(params);
    }
}