package com.diet.mnagement.libs.ocr;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.diet.mnagement.R;
import com.diet.mnagement.datas.UserInfoData;
import com.diet.mnagement.ui.activities.MainActivity;
import com.diet.mnagement.ui.adapters.FoodSearchAdapter;
import com.diet.mnagement.ui.dialogs.AddFoodDialog;

import java.util.ArrayList;
import java.util.StringTokenizer;

import static com.diet.mnagement.R.id.foodNameEditText;
import com.diet.mnagement.ui.dialogs.AddFoodDialog;
import com.diet.mnagement.ui.dialogs.AddOcrFoodDialog;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by PPB on 2017-06-19.
 */

public class OcrListActivity extends AppCompatActivity {

    private AddOcrFoodDialog addOcrFoodDialog;
    private UserInfoData currentUserInfoData;
    String listFood;
    Intent intent1 = new Intent(this, MainActivity.class);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ocrlist);

        Intent intent = getIntent();
        String resultContents = intent.getStringExtra("resultContents");

        StringTokenizer st = new StringTokenizer(resultContents, " ");
        final ArrayList<String> array_Result = new ArrayList<String>();

        while (st.hasMoreTokens()) {
            array_Result.add(st.nextToken());
        }


        final ArrayAdapter<String> ocrAdapter;
        ocrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array_Result);

        final ListView ocrListView = (ListView) findViewById(R.id.ocrListView);
        ocrListView.setAdapter(ocrAdapter);

        ocrListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listFood = array_Result.get(position);

                Toast.makeText(getBaseContext(), listFood + " 선택됨", Toast.LENGTH_SHORT).show();

                addOcrFoodDialog = new AddOcrFoodDialog(OcrListActivity.this, currentUserInfoData, listFood);
                addOcrFoodDialog.show();

            }
        });


    }

}