package com.example.test.instagram.Home;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.test.instagram.R;
import com.example.test.instagram.Utils.Preview;


public class CameraFragment extends Fragment {

    private static final String TAG = "CameraFragment";

    private TextureView mCameraTextureView;
    private ImageButton mButton;

    private Preview mPreview;

    public static final int REQUEST_CAMERA = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        mCameraTextureView = view.findViewById(R.id.textureView);
        mPreview = new Preview(view.getContext(), mCameraTextureView);
        mButton = view.findViewById(R.id.btnCapture);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPreview.takePicture();
            }
        });
        return  view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    if (permission.equals(Manifest.permission.CAMERA)) {
                        if(grantResult == PackageManager.PERMISSION_GRANTED) {
                            mCameraTextureView = (TextureView) getActivity().findViewById(R.id.textureView);
                            mPreview = new Preview(getActivity(), mCameraTextureView);
                            mPreview.openCamera();
                            Log.d(TAG,"mPreview set");
                        } else {
                            Toast.makeText(getActivity(),"Should have camera permission to run", Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPreview.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPreview.onPause();
    }

}