package com.example.instagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.instagramclone.Home.MainActivity;
import com.example.instagramclone.Login.LoginActivity;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.Models.UserAccountSettings;
import com.example.instagramclone.Models.UserSettings;
import com.example.instagramclone.R;
import com.example.instagramclone.Utils.BottomNavigationViewHelper;
import com.example.instagramclone.Utils.FirebaseMethods;
import com.example.instagramclone.Utils.UniversalImageLoader;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private static final int ACTIVITY_NUM = 4;
    private TextView mPosts , mFollowing  , mFollowers, mDisplayName , mUsername , mWebiste , mDescription;
    private ProgressBar mProgressBar;
    private CircleImageView mProfilePhoto;
    private GridView mGridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationView bottomNavigationView;
    private Context mContext;
    private FirebaseMethods firebaseMethods;
    private RelativeLayout relativeLayout;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile , container , false);
        mDisplayName = view.findViewById(R.id.display_name);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        mUsername = view.findViewById(R.id.username);
        mWebiste = view.findViewById(R.id.website);
        mDescription = view.findViewById(R.id.description);
        mProfilePhoto = view.findViewById(R.id.profile_photo);
        mPosts = view.findViewById(R.id.tvPosts);
        mFollowers = view.findViewById(R.id.tvFollowers);
        mFollowing = view.findViewById(R.id.tvFollowing);
        mProgressBar = view.findViewById(R.id.profileProgressBar);
        mGridView = view.findViewById(R.id.gridView);
        toolbar = view.findViewById(R.id.profileToolbar);
        profileMenu = view.findViewById(R.id.profileMenu);
        bottomNavigationView = view.findViewById(R.id.bottomNavViewBar);
        mContext = getActivity();
        firebaseMethods = new FirebaseMethods(getActivity());
        relativeLayout.setVisibility(View.GONE);

        Log.d(TAG, "onCreateView: started");

        setupBottomNavigationView();
        setupToolbar();
        setupFirebaseAuth();

        return view;
    }

    private void setProfileWidgets(UserSettings userSettings){
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieved from firebase " + userSettings.toString());

//        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getUserAccountSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo() , mProfilePhoto , null , "");

        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mWebiste.setText(settings.getWebsite());
        mDescription.setText(settings.getDescription());
        mPosts.setText(Long.toString(settings.getPosts()));
        mFollowing.setText(Long.toString(settings.getFollowing()));
        mFollowers.setText(Long.toString(settings.getFollowers()));

        mProgressBar.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.VISIBLE);

    }

    /*
        Setting the toolbar for profile
     */
    private void setupToolbar(){

        ((ProfileActivity)getActivity()).setSupportActionBar(toolbar);


        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG , "onClick : Navigating to account settings");

                Intent intent = new Intent(mContext , AccountSettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
        BottomNavigationView Setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG , "setting up BottomNavigationView");

        BottomNavigationViewHelper.enableNavigation(mContext , bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
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
