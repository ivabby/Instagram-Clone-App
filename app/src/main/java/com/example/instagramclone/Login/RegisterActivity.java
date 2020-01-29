package com.example.instagramclone.Login;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagramclone.R;
import com.example.instagramclone.Utils.FirebaseMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private Context mContext;
    private String email , username , password;
    private EditText mEmail , mPassword , mUsername;
    private TextView loadingPleaseWait;
    private Button btnRegister;
    private ProgressBar mProgressBar;
    private FirebaseMethods firebaseMethods;

    //  firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: started");

        initWidgets();
        setupFirebaseAuth();
        firebaseMethods = new FirebaseMethods(mContext);
        init();
    }

    private void init(){
        Log.d(TAG, "init: ");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();
                username = mUsername.getText().toString();

                if(checkInput(email , username , password)){
                    mProgressBar.setVisibility(View.VISIBLE);
                    loadingPleaseWait.setVisibility(View.VISIBLE);

                    firebaseMethods.registerNewEmail(email , password , username);
                }
            }
        });
    }

    private boolean checkInput(String email,String username,String password){
        Log.d(TAG, "checkInput: checking input of user data");
        if(email.equals("") || username.equals("") || password.equals("")){
            Toast.makeText(mContext , "All fields should be filled up" , Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /*
        Initialize the activity widgets
     */
    private void initWidgets(){
        Log.d(TAG, "initWidgets: Initializing Widgets");

        mProgressBar = findViewById(R.id.registerRequestLoadingProgressBar);
        loadingPleaseWait = findViewById(R.id.pleaseWait);
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        mUsername = findViewById(R.id.input_username);
        btnRegister = findViewById(R.id.btn_register);
        mContext = RegisterActivity.this;

        mProgressBar.setVisibility(View.GONE);
        loadingPleaseWait.setVisibility(View.GONE);
    }

    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: checking string is null");
        if(string.equals("")){
            return true;
        } else{
            return false;
        }
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
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //  Check if the current user is logged in
//                checkCurrentUser(user);


                if(user != null){
                    //  User is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in:" + user.getUid());
                } else{
                    //  User is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out ");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuth != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
