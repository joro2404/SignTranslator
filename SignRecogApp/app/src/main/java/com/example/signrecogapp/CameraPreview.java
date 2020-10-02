package com.example.signrecogapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

class Preview extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    Camera mCamera;
    int filename = 0;

    Preview(Context context) {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.

        Camera.PreviewCallback previewCallback = new Camera.PreviewCallback()
        {
            public void onPreviewFrame(byte[] data, Camera camera)
            {
                Camera.Parameters parameters = camera.getParameters();
                int imageFormat = parameters.getPreviewFormat();
                if (imageFormat == ImageFormat.NV21)
                {

                    int w = parameters.getPreviewSize().width;
                    int h = parameters.getPreviewSize().height;

                    Rect rect = new Rect(0, 0, w, h);
                    YuvImage img = new YuvImage(data, ImageFormat.NV21, w, h, null);
                    OutputStream outStream = null;
                    File file = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_MOVIES)
                    , "/" + filename + ".jpg");

                    if(filename > 20){
                        filename = 0;
                    }
                    filename++;

                    try
                    {
                        outStream = new FileOutputStream(file);
                        img.compressToJpeg(rect, 100, outStream);
                        outStream.flush();
                        outStream.close();
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }

        };


        mCamera = Camera.open();
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.setPreviewCallback(previewCallback);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        mCamera.stopPreview();
        mCamera = null;
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

        // You need to choose the most appropriate previewSize for your app
        //Camera.Size previewSize = previewSizes.get(15);

                parameters.setPreviewSize(1920, 1080);

        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();

    }



}
