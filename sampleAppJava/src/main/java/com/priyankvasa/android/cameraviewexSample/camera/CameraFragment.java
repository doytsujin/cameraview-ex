package com.priyankvasa.android.cameraviewexSample.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.priyankvasa.android.cameraviewex.CameraView;
import com.priyankvasa.android.cameraviewex.Modes;
import com.priyankvasa.android.cameraviewexSample.R;

import kotlin.Unit;
import timber.log.Timber;

public class CameraFragment extends Fragment {

    private CameraView camera;
    private ImageView ivCapture;
    private ImageView ivFlashSwitch;
    private ImageView ivPhoto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        camera = view.findViewById(R.id.camera);
        ivCapture = view.findViewById(R.id.ivCaptureButton);
        ivFlashSwitch = view.findViewById(R.id.ivFlashSwitch);
        ivPhoto = view.findViewById(R.id.ivPhoto);

        ivCapture.setOnClickListener((View v) -> camera.capture());

        ivFlashSwitch.setOnClickListener((View v) -> {

            @DrawableRes int flashDrawableId;

            switch (camera.getFlash()) {

                case Modes.Flash.FLASH_OFF:
                    flashDrawableId = R.drawable.ic_flash_auto;
                    camera.setFlash(Modes.Flash.FLASH_AUTO);
                    break;

                case Modes.Flash.FLASH_AUTO:
                    flashDrawableId = R.drawable.ic_flash_on;
                    camera.setFlash(Modes.Flash.FLASH_ON);
                    break;

                case Modes.Flash.FLASH_ON:
                    flashDrawableId = R.drawable.ic_flash_off;
                    camera.setFlash(Modes.Flash.FLASH_OFF);
                    break;

                default:
                    return;
            }

            ivFlashSwitch.setImageDrawable(ActivityCompat.getDrawable(requireContext(), flashDrawableId));
        });

        ivPhoto.setOnClickListener((View v) -> v.setVisibility(View.GONE));

        setupCamera();
    }

    private void setupCamera() {

        View view = getView();
        if (view == null) return;

        camera.addCameraOpenedListener(() -> {
            Timber.i("Camera opened.");
            return Unit.INSTANCE;
        });

        camera.addPictureTakenListener((byte[] imageData) -> {
            ivPhoto.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imageData)
                    .into(ivPhoto);
            return Unit.INSTANCE;
        });

        camera.setPreviewFrameListener((Image image) -> {
            Timber.i("Preview frame available.");
            return Unit.INSTANCE;
        });

        camera.addCameraClosedListener(() -> {
            Timber.i("Camera closed.");
            return Unit.INSTANCE;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!camera.isCameraOpened()
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            camera.start();
        }
    }

    @Override
    public void onPause() {
        if (camera.isCameraOpened()) camera.stop(false);
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (camera.isCameraOpened()) camera.stop(true);
        super.onDestroyView();
    }
}