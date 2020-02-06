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

import com.example.instagramclone.Models.User;
import com.example.instagramclone.Models.UserAccountSettings;
import com.example.instagramclone.Models.UserSettings;
import com.example.instagramclone.R;
import com.example.instagramclone.Utils.FirebaseMethods;
import com.example.instagramclone.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

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
     *  Before doing so it checks to make sure the username chosen is unique
     */
    private void saveProfileSettings(){
        final String displayName = mName.getText().toString();
        final String username = mUsername.getText().toString();
        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmailAddress.getText().toString();
        final long phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //  Case 1 : user change their user name so check for uniqueness
                if(!mUserSettings.getUser().getUsername().equals(username)){

                    checkIfUsernameExist(username);


                } else{
                    //  Case 2 : user didnot change their user name

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
