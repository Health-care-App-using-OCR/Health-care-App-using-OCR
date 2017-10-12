package com.diet.mnagement.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.diet.mnagement.R;
import com.diet.mnagement.datas.PostData;
import com.diet.mnagement.datas.UserInfoData;
import com.diet.mnagement.libs.utils.VerifyUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;



public class CommunityWriteActivity extends AppCompatActivity
{
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private Uri selectedImageUri = null;

    private Button uploadImageButton;
    private UserInfoData currentUserInfoData;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_write);
        storage = FirebaseStorage.getInstance();

        if (isNetWork() == false)
        {
            Toast.makeText(this, "인터넷연결을 확인 해 주세요", Toast.LENGTH_SHORT).show();
            finish();
        }

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

        final ProgressDialog mProgressDialog = ProgressDialog.show(this, "",
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
                    mProgressDialog.dismiss();
                }
                else
                {
                    finish();
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d("databaselog", databaseError.getDetails());
            }
        });

        final EditText postTitleEditText = (EditText) findViewById(R.id.postTitleEditText);
        final EditText postContentEditText = (EditText) findViewById(R.id.postContentEditText);
        uploadImageButton = (Button) findViewById(R.id.uploadImageButton);
        uploadImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 5005);
            }
        });


        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!VerifyUtil.verifyStrings(postTitleEditText.getText().toString(), postContentEditText.getText().toString(),
                        currentUserInfoData.getNickname(), currentUserInfoData.getUid()))
                {
                    return;
                }

                final PostData newPost = new PostData();
                newPost.setTitle(postTitleEditText.getText().toString());
                newPost.setContent(postContentEditText.getText().toString());
                newPost.setWriterName(currentUserInfoData.getNickname());
                newPost.setWriterUid(currentUserInfoData.getUid());

                if (selectedImageUri != null)
                {
                    final ProgressDialog mProgressDialog = ProgressDialog.show(CommunityWriteActivity.this, "",
                            "잠시만 기다려주세요", true);
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://projdiet-a9b02.appspot.com");
                    StorageReference riversRef = storageRef.child("images/" + selectedImageUri.getLastPathSegment());
                    UploadTask uploadTask = riversRef.putFile(selectedImageUri);
                    uploadTask.addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception exception)
                        {
                            // Handle unsuccessful uploads
                            mProgressDialog.dismiss();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                            final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            newPost.setImage(downloadUrl + "");
                            post(newPost);
                            mProgressDialog.dismiss();
                        }
                    });
                }
                else
                {
                    post(newPost);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5005)
        {
            selectedImageUri = data.getData();
            uploadImageButton.setText(selectedImageUri.getLastPathSegment());
        }
    }

    public void post(PostData postData)
    {
        mDatabase.child("posts").push().setValue(postData);
        finish();
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

