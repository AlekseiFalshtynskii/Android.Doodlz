package com.example.aleksei.doodlz;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final int ACCELERATION_THRESHOLD = 100000;
    private static final int SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1;

    private DoodleView doodleView;
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;
    private boolean dialogOnScreen = false;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        doodleView = (DoodleView) view.findViewById(R.id.doodleView);
        acceleration = 0.00f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        enableAccelerometerListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        disableAccelerometerListening();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.doodle_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.color:
                ColorDialogFragment colorDialogFragment = new ColorDialogFragment();
                colorDialogFragment.show(getFragmentManager(), "color dialog");
                return true;
            case R.id.line_width:
                LineWidthDialogFragment lineWidthDialogFragment = new LineWidthDialogFragment();
                lineWidthDialogFragment.show(getFragmentManager(), "line width dialog");
                return true;
            case R.id.delete_drawing:
                confirmErase();
                return true;
            case R.id.save:
                saveImage();
                return true;
            case R.id.print:
                doodleView.printImage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void enableAccelerometerListening() {
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void disableAccelerometerListening() {
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    private void confirmErase() {
        EraseImageDialogFragment fragment = new EraseImageDialogFragment();
        fragment.show(getFragmentManager(), "erase dialog");
    }

    private void saveImage() {
        if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.permission_explanation);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        SAVE_IMAGE_PERMISSION_REQUEST_CODE
                                );
                            }
                        }
                );
                builder.create().show();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        SAVE_IMAGE_PERMISSION_REQUEST_CODE
                );
            }
        } else {
            doodleView.saveImage();
        }
    }

    public DoodleView getDoodleView() {
        return doodleView;
    }

    public void setDialogOnScreen(boolean visible) {
        dialogOnScreen = visible;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case SAVE_IMAGE_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doodleView.saveImage();
                }
                return;
        }
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (!dialogOnScreen) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                lastAcceleration = currentAcceleration;
                currentAcceleration = x * x + y * y + z * z;
                acceleration = currentAcceleration * (currentAcceleration - lastAcceleration);
                if (acceleration > ACCELERATION_THRESHOLD) {
                    confirmErase();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };
}
