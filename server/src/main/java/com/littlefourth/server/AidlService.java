package com.littlefourth.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.littlefourth.aidl.ICallback;
import com.littlefourth.aidl.IController;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class AidlService extends Service {
    private static final String TAG = "directional tag";
    public AidlService() {
    }

    private Runnable callbackCommand = new Runnable() {
        @Override
        public void run() {
            if (callback != null) {
                try {
                    callback.onResult(1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private ICallback callback;
    private IController controller;

    private ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1);

    @Override
    public IBinder onBind(Intent intent) {
        if (controller == null) {
            controller = new IController.Stub() {
                @Override
                public void registerCallback(ICallback callback) throws RemoteException {
                    Log.d(TAG, "server register callback");
                    AidlService.this.callback = callback;
                    //2秒后回调callback方法
                    executorService.schedule(callbackCommand, 2, TimeUnit.SECONDS);
                }
            };
        }
        return controller.asBinder();
    }
}