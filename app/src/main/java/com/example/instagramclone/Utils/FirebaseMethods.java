package com.example.instagramclone.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.instagramclone.Models.User;
import com.example.instagramclone.Models.UserAccountSettings;
import com.example.instagramclone.Models.UserSettings;
import com.example.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";

    private Context mContext;

    //  firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private String userId;
    public FirebaseMethods(Context context){
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userId = mAuth.getCurrentUser().getUid();
        }
    }

    public void updateUsername(String username){
        Log.d(TAG, "updateUsername: updating username to: "+username);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userId)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userId)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }

//    public boolean checkIfUsernameExists(String username , DataSnapshot dataSnapshot){
//        Log.d(TAG, "checkIfUsernameExists: checking if " + username +" already exists.");
//
//        User user = new User();
//        for(DataSnapshot ds : dataSnapshot.child(userId).getChildren()){
//            Log.d(TAG, "checkIfUsernameExists: datasnapshot " + ds);
//
//            user.setUsername(ds.getValue(User.class).getUsername());
//            Log.d(TAG, "checkIfUsernameExists: username " + user.getUsername());
//
//            if(StringManipulation.expandUsername(user.getUsername()).equals(username)){
//                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + username);
//                return true;
//            }
//        }
//
//        return  false;
//    }



    /**
     * Register a new email and password for firebase authentication
     * @param email
     * @param password
     * @param username
     */
    public void registerNewEmail(String email,String password,String username){
        Log.d(TAG, "registerNewEmail: " + mContext);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            userId = mAuth.getCurrentUser().getUid();
                            sendVerificationEmail();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            } else{
                                Toast.makeText(mContext , "Couldn't send verification email" , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    public void addNewUser(String email , String username,String description,String website,String profile_photo){
        User user = new User(userId , email , StringManipulation.condenseUsername(username) , 1);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userId)
                .setValue(user);

        UserAccountSettings settings = new UserAccountSettings(
                description ,
                username ,
                0 ,
                0 ,
                0 ,
                profile_photo ,
                StringManipulation.condenseUsername(username) ,
                website);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userId)
                .setValue(settings);
    }

    /**
     * retrieve the user account settings for the currently logged in user
     * @param dataSnapshot
     * @return
     */
    public UserSettings getUserSettings(DataSnapshot dataSnapshot){
        Log.d(TAG, "getUserAccountSettings: retrieve user account settings from firebase");

        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        for(DataSnapshot ds : dataSnapshot.getChildren()){

            //  User Account settings node
            if(ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))){
                Log.d(TAG, "getUserAccountSettings: datasnapshot " + ds);

                try{

                    settings.setDisplay_name(
                            ds.child(userId)
                            .getValue(UserAccountSettings.class)
                            .getDisplay_name());

                    settings.setUsername(
                            ds.child(userId)
                            .getValue(UserAccountSettings.class)
                            .getUsername());


                    settings.setWebsite(
                            ds.child(userId)
                                    .getValue(UserAccountSettings.class)
                                    .getWebsite());


                    settings.setDescription(
                            ds.child(userId)
                                    .getValue(UserAccountSettings.class)
                                    .getDescription());


                    settings.setProfile_photo(
                            ds.child(userId)
                                    .getValue(UserAccountSettings.class)
                                    .getProfile_photo());


                    settings.setPosts(
                            ds.child(userId)
                                    .getValue(UserAccountSettings.class)
                                    .getPosts());


                    settings.setFollowers(
                            ds.child(userId)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowers());


                    settings.setFollowing(
                            ds.child(userId)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowing());


                    Log.d(TAG, "getUserAccountSettings: retrieved user account settings " + settings.toString());
                } catch (Exception e){
                    Log.e(TAG, "getUserAccountSettings: NullPointerxception" + e.getMessage());
                }
            }

            //  users node
            if(ds.getKey().equals(mContext.getString(R.string.dbname_users))){
                Log.d(TAG, "getUserAccountSettings: datasnapshot " + ds);

                user.setUsername(
                        ds.child(userId)
                            .getValue(User.class)
                            .getUsername());

                user.setEmail(
                        ds.child(userId)
                                .getValue(User.class)
                                .getEmail());

                user.setPhone_number(
                        ds.child(userId)
                                .getValue(User.class)
                                .getPhone_number());

                user.setUser_id(
                        ds.child(userId)
                                .getValue(User.class)
                                .getUser_id());

                Log.d(TAG, "getUserAccountSettings: retrieved user " + user.toString());
            }
        }

        return new UserSettings(user , settings);
    }
}























