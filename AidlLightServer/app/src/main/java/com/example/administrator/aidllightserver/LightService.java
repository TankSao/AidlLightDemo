package com.example.administrator.aidllightserver;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import static android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
import static android.view.WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;

/**
 * Created by Administrator on 2018/11/6.
 */

public class LightService extends Service{
    private Camera camera;
    private CameraManager manager;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    class MyBinder extends LightInterface.Stub
    {

        @Override
        public boolean openLight() throws RemoteException {
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                Log.e("error","不支持闪光灯");
                return false;
            }
            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    //6.0
                    manager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
                    manager.setTorchMode("0", true);
                }else {
                    camera = Camera.open();
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameters);
                    camera.startPreview();
                }
            }catch (Exception e){
                return  false;
            }
            return true;
        }

        @Override
        public boolean closeLight() throws RemoteException {
            try {
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    Log.e("error","不支持闪光灯");
                    return false;
                }
                if (Build.VERSION.SDK_INT >= 23) {
                    //6.0
                    if (manager == null) {
                        return false;
                    }
                    manager.setTorchMode("0", false);
                } else {
                    if (camera == null) {
                        return false;
                    }
                    camera.stopPreview();
                    camera.release();
                }
            }catch (Exception e){
                return  false;
            }
            return true;
        }
    }
}
