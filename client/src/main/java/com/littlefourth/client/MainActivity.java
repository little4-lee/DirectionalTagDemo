package com.littlefourth.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.littlefourth.aidl.ICallback;
import com.littlefourth.aidl.IController;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "directional tag";
    private AtomicBoolean isServiceBind = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindService();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            isServiceBind.set(true);
            controller = IController.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            isServiceBind.set(false);
        }
    };

    private void bindService() {
        String serviceAction = "android.intent.action.MY_AIDL_ACTION";
        String servicePkg = "com.littlefourth.server";
        Intent intent = new Intent(serviceAction);
        intent.setPackage(servicePkg);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void clickBtn(View view) {
        switch (view.getId()) {
            case R.id.btnVerityCallbck:
                // TODO: 2021/4/3
                if (isServiceBind.get()) {
                    registerCallback();
                }
                break;

            default:
                //do nothing
                break;
        }
    }

    private IController controller;
    private ICallback callback = new ICallback.Stub() {

        @Override
        public void onResult(int result) throws RemoteException {
            Log.d(TAG, "client onResult: " + result);
        }
    };

    private void registerCallback() {
        if (controller != null) {
            try {
                controller.registerCallback(callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}