package com.example.signrecogapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Preview mPreview;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.CAMERA}, 1);
            }
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mPreview = new Preview(this);
        setContentView(mPreview);

        View topView = new View(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.height = 5;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

        topView.setBackgroundColor(getResources().getColor(android.R.color.black));
        addContentView(topView, layoutParams);

        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) topView.getLayoutParams();
        marginLayoutParams.setMargins(0, 200, 0, 0);

        View bottomView = new View(this);
        layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.height = 5;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

        bottomView.setBackgroundColor(getResources().getColor(android.R.color.black));
        addContentView(bottomView, layoutParams);

        marginLayoutParams = (ViewGroup.MarginLayoutParams) bottomView.getLayoutParams();
        marginLayoutParams.setMargins(0, 800, 0, 0);

    }


}
