package com.example.signrecogapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Preview mPreview;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

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
        float d = getApplicationContext().getResources().getDisplayMetrics().density;
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) topView.getLayoutParams();
        marginLayoutParams.setMargins(0, (int) (100*d), 0, 0);

        View bottomView = new View(this);
        layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.height = 5;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

        bottomView.setBackgroundColor(getResources().getColor(android.R.color.black));
        addContentView(bottomView, layoutParams);

        marginLayoutParams = (ViewGroup.MarginLayoutParams) bottomView.getLayoutParams();
        marginLayoutParams.setMargins(0, (int) (350*d), 0, 0);


        TextView textView = new TextView(this);
        layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.height = ViewGroup.LayoutParams.FILL_PARENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        textView.setBackgroundColor(getResources().getColor(R.color.trBlack));
        addContentView(textView, layoutParams);

        textView.setText("Lorem Ipsum");
        textView.setTextColor(getResources().getColor(R.color.white));
        marginLayoutParams = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
        marginLayoutParams.setMargins(0, (int) (450*d), 0, 0);


        Button button = new Button(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.height = (int) (45*d);
        layoutParams.width = (int) (45*d);

        button.setBackground(getResources().getDrawable(R.drawable.ellipse_1));
        addContentView(button, layoutParams);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        button.setTranslationX((width/2) - ((45*d)/2));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            button.setTextAppearance(R.style.some_id);
        }

        button.setText(R.string.back_button);
        marginLayoutParams = (ViewGroup.MarginLayoutParams) button.getLayoutParams();
        marginLayoutParams.setMargins(0, (int) (600*d), 0, 0);
    }


}
