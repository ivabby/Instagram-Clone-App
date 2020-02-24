package com.example.instagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.instagramclone.R;
import com.example.instagramclone.Utils.FirebaseMethods;
import com.example.instagramclone.Utils.SectionStatePagerAdapter;

import java.util.ArrayList;

public class AccountSettingsActivity extends AppCompatActivity {

    private static final String TAG = "AccountSettingsActivity";
    private Context mContext = AccountSettingsActivity.this;

    private SectionStatePagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsettings);

        viewPager = findViewById(R.id.container);
        mRelativeLayout = findViewById(R.id.relativeLayout1);

        setupSettingsList();

        setupFragment();
        getIncomingIntent();

        //  Setup back arrow for navigating back to ProfileActivity
        ImageView backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG , "onClick : navigating back to ProfileActivity");
                finish();
            }
        });
    }

    private void getIncomingIntent(){
        Intent intent = getIntent();

        //  If there is an imageURL atttached as an extra then it was choosen from the gallery/photo fragment
        if(intent.hasExtra(mContext.getString(R.string.selected_image))){
            Log.d(TAG, "getIncomingIntent: incoming image url");
            if(intent.getStringExtra(mContext.getString(R.string.return_to_fragment)).equals(getString(R.string.edit_profile_fragment)))
            {
                //  Set new profile image
                FirebaseMethods firebaseMethods = new FirebaseMethods(AccountSettingsActivity.this);
                firebaseMethods.uploadNewPhoto(getString(R.string.profile_photo), null ,
                        0 , intent.getStringExtra(mContext.getString(R.string.selected_image)));
            }
        }


        if(intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "getIncomingIntent: recieved from incoming intent from "+getString(R.string.profile_activity));

            setViewPager(pagerAdapter.getFragmentNumber(getString(R.string.edit_profile_fragment)));
        }
    }

    private void setupFragment(){
        pagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(new EditProfileFragment() , getString(R.string.edit_profile_fragment)); //  fragment 0
        pagerAdapter.addFragment(new SignOutFragment() , getString(R.string.sign_out_fragment)); //  fragment 1
    }

    private void setViewPager(int fragmentNumber){
        mRelativeLayout.setVisibility(View.GONE);
        Log.d(TAG , "setViewPager : naviagting to " + fragmentNumber);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(fragmentNumber);
    }

    private void setupSettingsList(){
        Log.d(TAG , "setupSettingsList : started");
        ListView listView = findViewById(R.id.lvAccountSettings);

        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.edit_profile_fragment)); //   fragment 0
        options.add(getString(R.string.sign_out_fragment));  // fragment 1

        ArrayAdapter adapter = new ArrayAdapter(mContext , android.R.layout.simple_list_item_1 , options);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: navigating to fragment" + position);
                setViewPager(position);
            }
        });
    }
}
