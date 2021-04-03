package com.littlefourth.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.littlefourth.aidl.ICallback;
import com.littlefourth.aidl.IController;
import com.littlefourth.aidl.State;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.littlefourth.aidl.TAG.T;


public class AidlService extends Service {
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
                    Log.d(T, "server register callback");
                    AidlService.this.callback = callback;
                    //2秒后回调callback方法
                    executorService.schedule(callbackCommand, 2, TimeUnit.SECONDS);
                }

                @Override
                public int transIn(State state) throws RemoteException {
                    Log.d(T, "callee transIn(), value: " + state.getValue());
                    int newValue = 2;
                    Log.d(T, "callee set value to " + newValue);
                    state.setValue(newValue);
                    return 1;
                }

                @Override
                public int transOut(State state) throws RemoteException {
                    Log.d(T, "callee transOut(), value: " + state.getValue());
                    int newValue = 2;
                    Log.d(T, "callee set value to " + newValue);
                    state.setValue(newValue);
                    return 2;
                }

                @Override
                public int transInOut(State state) throws RemoteException {
                    Log.d(T, "callee transInOut(), value: " + state.getValue());
                    int newValue = 2;
                    Log.d(T, "callee set value to " + newValue);
                    state.setValue(newValue);
                    return 3;
                }

                @Override
                public int testArray(List<String> arr) throws RemoteException {
                    return 0;
                }

            };
        }
        return controller.asBinder();
    }
}