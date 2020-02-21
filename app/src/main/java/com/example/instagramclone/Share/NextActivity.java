package com.example.instagramclone.Share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagramclone.R;
import com.example.instagramclone.Utils.FirebaseMethods;
import com.example.instagramclone.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NextActivity extends AppCompatActivity {
    private static final String TAG = "NextActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //  vars
    private String mAppend = "file:/";
    private int imageCount = 0;
    private String imgURL;

    //  Widgets
    private EditText mCaption;

    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        Log.d(TAG, "onCreate: started. ");

        mFirebaseMethods = new FirebaseMethods(NextActivity.this);
        mCaption = findViewById(R.id.caption);
        mContext = this;

        setupFirebaseAuth();


        ImageView backArrow = findViewById(R.id.backCross);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Go back to share activity");
                finish();
            }
        });

        TextView share = findViewById(R.id.tvNext);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Share the photo and save on database
                Log.d(TAG, "onClick: navigating to final share screen");

                //  Upload the image to firebase
                Toast.makeText(NextActivity.this , "Attempting to upload new photo " + imageCount,Toast.LENGTH_SHORT).show();

                String caption = mCaption.getText().toString();
                mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo) , caption , imageCount , imgURL);
            }
        });
        setImage();

    }

    private void someMethod(){
        /**
         *  Step 1 - Create data model for photos
         *
         *  Step 2 - Add properties to the Photo Objects ( captions , date , imageUrl , photo_id , tags , user_id)
         *
         *  Step 3 - Count the number of photos that the user already have
         *
         *  Step 4 -
         *  a) Upload the photo to firebase and insert two new nodes in the Firebase Database
         *  b) insert into 'photo' node
         *  c) insert into 'user_photos' node
         *
         *
         */



    }

    /**
     * Sets the image url from incoming intent and displays the chosen image
     */
    public void setImage(){
        Intent intent = getIntent();
        ImageView imageView = findViewById(R.id.imageShare);
        imgURL = intent.getStringExtra(getString(R.string.selected_image));
        UniversalImageLoader.setImage(intent.getStringExtra(getString(R.string.selected_image)) , imageView , null , mAppend);
    }



    /**
     * ********************************** firebase ************************************************
     */

    /**
     * Setup the firebase auth
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase Auth");
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();


        Log.d(TAG, "Image count " + imageCount);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    //  User is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());
                } else{
                    //  User is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out ");
                }
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);

                imageCount = (int) dataSnapshot.child(mContext.getString(R.string.dbname_user_photos))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getChildrenCount();

                Log.d(TAG, "onDataChange: image count " + imageCount);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
