package com.diet.mnagement.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.diet.mnagement.R;
import com.diet.mnagement.datas.PostData;
import com.diet.mnagement.ui.adapters.PostAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CommunityActivity extends AppCompatActivity
{
    private ListView postList;
    private PostAdapter postAdapter;

    private DatabaseReference mDatabase;


    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        if (isNetWork() == false)
        {
            Toast.makeText(this, "인터넷연결을 확인 해 주세요", Toast.LENGTH_SHORT).show();
            finish();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        postAdapter = new PostAdapter();
        postList = (ListView) findViewById(R.id.list_community);
        postList.setAdapter(postAdapter);

        postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent viewIntent = new Intent(CommunityActivity.this, CommunityPostViewActivity.class);
                viewIntent.putExtra("postId", postAdapter.getItem(position).getPostId());
                startActivity(viewIntent);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CommunityActivity.this, CommunityWriteActivity.class));
            }
        });


        mDatabase.child("posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                PostData postData = dataSnapshot.getValue(PostData.class);
                postData.setPostId(dataSnapshot.getKey());
                postAdapter.addItem(postData);
                postAdapter.notifyDataSetChanged();
                Log.d("postKey", dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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

