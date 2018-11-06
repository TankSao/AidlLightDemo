package com.example.administrator.aidllightclient;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.aidllightserver.LightInterface;

public class MainActivity extends AppCompatActivity {

    private ImageView light;
    private String state = "close";
    private LightInterface lightInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            //6.0
            requestAllPower();
        }
        initLightInterface();
        light = (ImageView) findViewById(R.id.light);
        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isInstalledServer()){
                    Toast.makeText(MainActivity.this,"服务未安装",Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        if (state.equals("close")) {
                            //开灯
                            try {
                                if (lightInterface.openLight()) {
                                    state = "open";
                                    light.setImageResource(R.mipmap.open);
                                }else{
                                    Log.e("error","开启失败");
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                Log.e("error","开启失败"+e.getMessage());
                            }
                        } else {
                            //关灯
                            try {
                                if (lightInterface.closeLight()) {
                                    state = "close";
                                    light.setImageResource(R.mipmap.close);
                                }else{
                                    Log.e("error","关闭失败");
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                Log.e("error","关闭失败"+e.getMessage());
                            }
                        }
                    }catch (Exception e){
                        Log.e("error",e.getMessage());
                        Toast.makeText(MainActivity.this,"服务未启动",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //检测服务是否安装
    private boolean isInstalledServer() {
        String packageName = "com.example.administrator.aidllightserver";
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            ApplicationInfo info = getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void initLightInterface() {
        try {
            Intent intent = new Intent();
            intent.setAction("gt.light.demo");
            intent.setPackage("com.example.administrator.aidllightserver");
            bindService(intent, new ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {

                    lightInterface = LightInterface.Stub.asInterface(service);
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            }, BIND_AUTO_CREATE);
        }catch (Exception e){
            Log.e("error",e.getMessage());
        }
    }

    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
    }
}
