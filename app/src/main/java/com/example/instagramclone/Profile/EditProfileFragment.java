package com.example.instagramclone.Profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instagramclone.Dialogs.ConfirmPasswordDialog;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.Models.UserAccountSettings;
import com.example.instagramclone.Models.UserSettings;
import com.example.instagramclone.R;
import com.example.instagramclone.Utils.FirebaseMethods;
import com.example.instagramclone.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment implements ConfirmPasswordDialog.OnConfirmPasswordListener{

    @Override
    public void onConfirmPassword(String password) {
        Log.d(TAG, "onConfirmPassword: password captured is "+password);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
                AuthCredential credential = EmailAuthProvider
                        .getCredential(mAuth.getCurrentUser().getEmail(), password);

        // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    Log.d(TAG, "User re-authenticated.");

                                    //  If email is already not present in database
                                    mAuth.fetchSignInMethodsForEmail(mEmailAddress.getText().toString()).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<SignInMethodQueryResult> tasks) {
                                            if(tasks.isSuccessful()){
                                                try {
                                                    Log.d(TAG, "onComplete: " + tasks.toString() + " " + tasks.getResult().getSignInMethods().size());
                                                    if (tasks.getResult().getSignInMethods().size() > 0) {
                                                        Log.d(TAG, "onComplete: that email is already in use");
                                                        Toast.makeText(getActivity(), "That email is already in use", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Log.d(TAG, "onComplete: that email is available");

                                                        //  Email available update it
                                                        mAuth.getCurrentUser().updateEmail(mEmailAddress.getText().toString())
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Log.d(TAG, "User email address updated.");
                                                                            firebaseMethods.updateEmail(mEmailAddress.getText().toString());
                                                                            Toast.makeText(getActivity(), "Email Updated", Toast.LENGTH_SHORT).show();

                                                                        }
                                                                    }
                                                                });
                                                    }

                                                } catch (Exception e){
                                                    Log.e(TAG, "onComplete: NULLPOINTEREXCEPTION " + e.getMessage() );
                                                }
                                            }
                                            else{

                                            }
                                        }
                                    });
                                }
                                else{
                                    Log.d(TAG, "re-authentication failed.");
                                }
                            }
                        });
    }

    private static final String TAG = "EditProfileFragment";
    private ImageView mProfileImage;
    private EditText mName , mUsername , mWebsite , mDescription , mEmailAddress , mPhoneNumber;
    private CircleImageView mProfilePhoto;
    private FirebaseMethods firebaseMethods;
    private String userId;

    //  vars
    private UserSettings mUserSettings;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile , container , false);
        mProfilePhoto = view.findViewById(R.id.profile_photo);
        mName = view.findViewById(R.id.username);
        mUsername = view.findViewById(R.id.display_name);
        mWebsite = view.findViewById(R.id.website);
        mDescription = view.findViewById(R.id.description);
        mEmailAddress = view.findViewById(R.id.mail);
        mPhoneNumber = view.findViewById(R.id.phoneNumber);
        firebaseMethods = new FirebaseMethods(getActivity());

        setupFirebaseAuth();
//        initImageLoader();
//        setProfileImage();

        //  Cross Button to navigating back to ProfileActivity
        ImageView cross = view.findViewById(R.id.backCross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to Profile Activity");
                getActivity().finish();
            }
        });

        ImageView checkmark = view.findViewById(R.id.saveChanges);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save chagnes");
                saveProfileSettings();
            }
        });

        return view;
    }

    /**
     *  Retrieve the data contained in the widgets and submits it to the database
     *  Before doing so it checks to make sure the username chosen is unique and check for the email also
     */
    private void saveProfileSettings(){
        final String displayName = mName.getText().toString();
        final String username = mUsername.getText().toString();
        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmailAddress.getText().toString();
        final long phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());


        //  Case 1 : user change their user name so check for uniqueness
        if(!mUserSettings.getUser().getUsername().equals(username)){

            checkIfUsernameExist(username);


        }

        //  Case 2 : if user made a change to their email
        if(!mUserSettings.getUser().getEmail().equals(email)){

            //  Step 1 : Reauthenticate
            //  Confirm the password and email
            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
            dialog.show(getFragmentManager() , getString(R.string.confirm_password_dialog));
            dialog.setTargetFragment(EditProfileFragment.this , 1);

            //  Step 2 : check if the email is already registered
            //  fetchProvidersEmail(String email)


            //  Step 3 : change the email
            //  Submit the new email to firebase authentication
        }

        if(!mUserSettings.getUserAccountSettings().getDisplay_name().equals(displayName)){
            //  update display name
            firebaseMethods.updateUserAccountSettings(displayName , null , null , 0);
        }

        if(!mUserSettings.getUserAccountSettings().getWebsite().equals(website)){
            //  update website
            firebaseMethods.updateUserAccountSettings(null ,website , null , 0);
        }

        if(!mUserSettings.getUserAccountSettings().getDescription().equals(description)){
            //  update description
            firebaseMethods.updateUserAccountSettings(null, null,description,0);
        }
        if(mUserSettings.getUser().getPhone_number() != phoneNumber){
            //  update phonenumber
            firebaseMethods.updateUserAccountSettings(null,null,null,phoneNumber);
        }

    }


    /**
     *  Check if @param username already exists in database
     * @param username
     */
    private void checkIfUsernameExist(final String username) {
        Log.d(TAG, "checkIfUsernameExist: check if " + username + " already exists");

        DatabaseReference reference = firebaseDatabase.getInstance().getReference();
        Query query = reference
                    .child(getString(R.string.dbname_users))
                    .orderByChild(getString(R.string.field_username))
                    .equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    //  add the username
                    firebaseMethods.updateUsername(username);
                    Toast.makeText(getActivity() , "Adding username " , Toast.LENGTH_SHORT).show();
                }
                for(DataSnapshot singleSnapsot : dataSnapshot.getChildren()){
                    if(singleSnapsot.exists()){
                        Log.d(TAG, "onDataChange: FOUND A  MATCH "+singleSnapsot.getValue(User.class).getUsername());
                        Toast.makeText(getActivity() , "That username already exists" , Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setProfileWidgets(UserSettings userSettings){
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieved from firebase " + userSettings.toString());

        mUserSettings = userSettings;
//        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getUserAccountSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo() , mProfilePhoto , null , "");

        mName.setText(settings.getDisplay_name());
        mUsername.setText(userSettings.getUser().getUsername());
        mWebsite.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mEmailAddress.setText(userSettings.getUser().getEmail());
        mPhoneNumber.setText(String.valueOf(userSettings.getUser().getPhone_number()));

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
        userId = mAuth.getCurrentUser().getUid();

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

                //  retrieve user information from database
                 setProfileWidgets(firebaseMethods.getUserSettings(dataSnapshot));

                //  retrieve user images in gridview

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
//        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuth != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
