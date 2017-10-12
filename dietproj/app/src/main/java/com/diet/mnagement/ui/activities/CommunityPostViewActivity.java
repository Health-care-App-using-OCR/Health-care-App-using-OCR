package com.diet.mnagement.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.diet.mnagement.R;
import com.diet.mnagement.datas.PostData;
import com.diet.mnagement.datas.ReplyData;
import com.diet.mnagement.datas.UserInfoData;
import com.diet.mnagement.libs.utils.VerifyUtil;
import com.diet.mnagement.ui.adapters.ReplyAdapter;
import com.diet.mnagement.ui.dialogs.ExpandImageViewDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class CommunityPostViewActivity extends AppCompatActivity
{

    private DatabaseReference mDatabase;
    private UserInfoData currentUserInfoData;
    private ListView replylist;
    private ReplyAdapter replyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_postview);

        if (isNetWork() == false)
        {
            Toast.makeText(this, "인터넷연결을 확인 해 주세요", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (getIntent() == null) finish();
        final String postId = getIntent().getStringExtra("postId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            // User is signed in
        }
        else
        {
            // No user is signed in
            finish();
        }

        replyAdapter = new ReplyAdapter();
        replylist = (ListView) findViewById(R.id.reply_list);
        replylist.setAdapter(replyAdapter);

        final TextView postTitleTextView = (TextView) findViewById(R.id.postTitleTextView);
        final TextView postContentTextView = (TextView) findViewById(R.id.postContentTextView);
        final ImageView postImg = (ImageView) findViewById(R.id.postImg);

        final EditText replyEditText = (EditText) findViewById(R.id.replyEditText);
        final ImageButton sendReplyButton = (ImageButton) findViewById(R.id.sendReplyButton);
        sendReplyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String replyText = replyEditText.getText().toString();
                if (!VerifyUtil.verifyString(replyText)) return;
                replyEditText.setText("");
                ReplyData replyData = new ReplyData();
                replyData.setContent(replyText);
                replyData.setWriterName(currentUserInfoData.getNickname());
                replyData.setWriterUid(currentUserInfoData.getUid());
                mDatabase.child("replys").child(postId).push().setValue(replyData);
            }
        });

        // 유저정보로드
        final ProgressDialog userLoadProgress = ProgressDialog.show(this, "",
                "잠시만 기다려주세요", true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    currentUserInfoData = dataSnapshot.getValue(UserInfoData.class);
                    userLoadProgress.dismiss();
                }
                else
                {
                    finish();
                    userLoadProgress.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d("databaselog", databaseError.getDetails());
            }
        });

        // 게시글 로드
        final ProgressDialog postLoadProgress = ProgressDialog.show(this, "",
                "게시글 로드중", true);
        mDatabase.child("posts").child(postId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final PostData postData = dataSnapshot.getValue(PostData.class);
                    postTitleTextView.setText(postData.getTitle());
                    postContentTextView.setText(postData.getContent());
                    if (postData.getImage() != null)
                    {
                        Glide.with(CommunityPostViewActivity.this).load(postData.getImage()).into(postImg);
                        postImg.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                ExpandImageViewDialog expandImageViewDialog = new ExpandImageViewDialog(CommunityPostViewActivity.this,
                                        postData.getImage());
                                expandImageViewDialog.show();
                            }
                        });
                    }

                    postLoadProgress.dismiss();
                }
                else
                {
                    finish();
                    postLoadProgress.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d("databaselog", databaseError.getDetails());
            }
        });

        // 댓글 로드
        mDatabase.child("replys").child(postId).addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                ReplyData replyData = dataSnapshot.getValue(ReplyData.class);
                replyAdapter.addItem(replyData);
                replyAdapter.notifyDataSetChanged();
                replylist.setSelection(replyAdapter.getCount() - 1);
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