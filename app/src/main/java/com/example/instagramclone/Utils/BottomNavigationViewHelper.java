package com.example.instagramclone.Utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.instagramclone.Likes.LikesActivity;
import com.example.instagramclone.Home.MainActivity;
import com.example.instagramclone.Profile.ProfileActivity;
import com.example.instagramclone.R;
import com.example.instagramclone.Search.SearchActivity;
import com.example.instagramclone.Share.ShareActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationViewHelper {

    private static final String TAG = "NavigationHelper";

    public static void setUpBottomNavigationView(BottomNavigationView bottomNavigationViewEx){
        Log.d(TAG , "Setup bottom navigation view");
    }

    public static void enableNavigation(final Context context , BottomNavigationView view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_house:
                        Intent i1 = new Intent(context , MainActivity.class); // ACTIVITY_NUM = 0
                        context.startActivity(i1);
                        break;

                    case R.id.ic_search:
                        Intent i2 = new Intent(context , SearchActivity.class); //  ACTIVITY_NUM = 1
                        context.startActivity(i2);
                        break;

                    case R.id.ic_circle:
                        Intent i3 = new Intent(context , ShareActivity.class); // ACTIVITY_NUM = 2
                        context.startActivity(i3);
                        break;

                    case R.id.ic_alert:
                        Intent i4 = new Intent(context , LikesActivity.class); // ACTIVITY_NUM = 3
                        context.startActivity(i4);
                        break;

                    case R.id.ic_profile:
                        Intent i5 = new Intent(context , ProfileActivity.class); // ACTIVITY_NUM = 4
                        context.startActivity(i5);
                        break;
                }


                return false;
            }
        });
    }
}
