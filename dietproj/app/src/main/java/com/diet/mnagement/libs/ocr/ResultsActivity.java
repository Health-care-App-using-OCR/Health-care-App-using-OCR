package com.diet.mnagement.libs.ocr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ResultsActivity extends Activity
{

    String outputPath;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        tv = new TextView(this);
        setContentView(tv);

        String imageUrl = "unknown";

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            imageUrl = extras.getString("IMAGE_PATH");
            outputPath = extras.getString("RESULT_PATH");
        }

        // Starting recognition process
        new AsyncProcessTask(this).execute(imageUrl, outputPath);
    }

    public void updateResults(Boolean success)
    {
        if (!success)
            return;
        try
        {
            StringBuffer contents = new StringBuffer();

            FileInputStream fis = openFileInput(outputPath);
            try
            {
                Reader reader = new InputStreamReader(fis, "UTF-8");
                BufferedReader bufReader = new BufferedReader(reader);
                String text = null;
                while ((text = bufReader.readLine()) != null)
                {
                    contents.append(text).append(System.getProperty("line.separator"));
                }
            } finally
            {
                fis.close();
            }

           // displayMessage(contents.toString());

            String resultContents = contents.toString();

            Intent intent1=new Intent(this, OcrListActivity.class);
            intent1.putExtra("resultContents", resultContents);
            startActivity(intent1);






        } catch (Exception e)
        {
            displayMessage("Error: " + e.getMessage());
        }
    }

    public void displayMessage(String text)
    {
        tv.post(new MessagePoster(text));
    }


    class MessagePoster implements Runnable
    {
        public MessagePoster(String message)
        {
            _message = message;
        }

        public void run()
        {
            tv.append(_message + "\n");
            setContentView(tv);
        }

        private final String _message;
    }
}
