package com.example.instagramclone.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instagramclone.Profile.AccountSettingsActivity;
import com.example.instagramclone.R;
import com.example.instagramclone.Utils.FilePaths;
import com.example.instagramclone.Utils.FileSearch;
import com.example.instagramclone.Utils.GridImageAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";

    //  Widgets
    private GridView gridView;
    private ImageView galleryImage;
    private ProgressBar progressBar;
    private Spinner spinner;

    //  vars
    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private String mSelectedImage;

    //  Constants
    private static final int NUM_GRID_COLUMN = 3;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: started");
        View view = inflater.inflate(R.layout.fragment_gallery , container , false);

        galleryImage = view.findViewById(R.id.galleryImageView);
        gridView = view.findViewById(R.id.gridView);
        spinner = view.findViewById(R.id.spinnerDirectory);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();

        ImageView backCross = view.findViewById(R.id.backCross);
        backCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment. ");
                getActivity().finish();
            }
        });

        TextView nextScreen = view.findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to next screen. ");

                if(isRootTask()) {
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    startActivity(intent);
                }
                 else{
                     Intent intent = new Intent(getActivity() , AccountSettingsActivity.class);
                     intent.putExtra(getString(R.string.selected_image) , mSelectedImage);
                     intent.putExtra(getString(R.string.return_to_fragment) , getString(R.string.edit_profile_fragment));
                     startActivity(intent);
                }
            }
        });

        init();
        return view;
    }

    private boolean isRootTask(){
        if(((ShareActivity)getActivity()).getTask() == 0)
            return true;
        return false;
    }

    private void init(){

        //  Check for other folders inside "storage/emulated/0/picture"
        FilePaths filePaths = new FilePaths();

        //  Check if picture folder exists
        if(FileSearch.getDirectoryPath(filePaths.PICTURES) != null){
            directories = FileSearch.getDirectoryPath(filePaths.PICTURES);
        }
        ArrayList<String> directoryName = new ArrayList<>();
        for(int i=0;i<directories.size();i++)
        {
            int index = directories.get(i).lastIndexOf('/');
            directoryName.add(directories.get(i).substring(index+1));
        }

        directories.add(filePaths.CAMERA);

        ArrayAdapter<String> adapter =  new ArrayAdapter<>(getActivity(),
                            android.R.layout.simple_spinner_item , directoryName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Log.d(TAG, "init: size of folders having image: "+directories.size());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: selected " + directories.get(position));

                //  setup image grid view
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupGridView(String selectedDirectory){
        Log.d(TAG, "setupGridView: setting up grid view with : " + selectedDirectory);
        final ArrayList<String> imageURLs = FileSearch.getFilePath(selectedDirectory);

        //  Finding the width of the screen and setting the grid width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth / NUM_GRID_COLUMN;
        gridView.setColumnWidth(imageWidth);

        //  use the grid adapter to adapt the images to grid view
        GridImageAdapter adapter = new GridImageAdapter(getActivity() , R.layout.layout_grid_imageview ,mAppend,imageURLs);
        gridView.setAdapter(adapter);

        // set the first image to be displayed when the activity fragment view is inflated
        setImage(imageURLs.get(0) , galleryImage , mAppend);
        mSelectedImage = imageURLs.get(0);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected image ");
                setImage(imageURLs.get(position) , galleryImage , mAppend);
                mSelectedImage = imageURLs.get(position);
            }
        });
    }

    private void setImage(String imgURL , ImageView image, String append){
        Log.d(TAG, "setImage: setting image. ");

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}




















