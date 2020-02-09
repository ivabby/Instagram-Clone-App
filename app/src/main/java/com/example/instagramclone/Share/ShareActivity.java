package com.example.instagramclone.Share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.instagramclone.R;
import com.example.instagramclone.Utils.BottomNavigationViewHelper;
import com.example.instagramclone.Utils.Permissions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = "ShareActivity";
    private Context mContext = ShareActivity.this;
    private static final int ACTIVITY_NUM = 2;
    private static final int VERIFY_PERMISSIONS = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG , "onCreate started");

        if(checkPermissionsArray(Permissions.PERMISSION)){

        } else {
            verifyPermissions(Permissions.PERMISSION);
        }


//        setupBottomNavigationView();
    }

    private void verifyPermissions(String[] permission) {
        Log.d(TAG, "verifyPermissions: verifying permissions");
        ActivityCompat.requestPermissions(
                ShareActivity.this,
                permission,
                VERIFY_PERMISSIONS
                );
    }


    /**
     * Check array of permissions
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permission array");

        for(int i=0;i<permissions.length;i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    /**
     * Check a single permission is verified or not
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission");

        int permissionRequest = ActivityCompat.checkSelfPermission(ShareActivity.this , permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission not granted for " + permission);
            return false;
        }

        return true;
    }

    /*
        BottomNavigationView Setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG , "setting up BottomNavigationView");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext , bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
