package com.littlefourth.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.littlefourth.aidl.ICallback;
import com.littlefourth.aidl.ICallbackContainer;
import com.littlefourth.aidl.IController;
import com.littlefourth.aidl.State;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.littlefourth.aidl.TAG.T;


public class AidlService extends Service {
    public AidlService() {
    }

    private final Runnable callbackCommand = new Runnable() {
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

    private final Runnable callbackCommandAsync = new Runnable() {
        @Override
        public void run() {
            if (callbackAsync != null) {
                try {
                    callbackAsync.onResult(2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private final Runnable containerCommand = new Runnable() {
        @Override
        public void run() {
            if (container != null) {
                try {
                    Log.d(T, "server call addCallback, thread: " + Thread.currentThread().getName());
                    container.addCallback(innerCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private IController controller;

    private ICallback callbackAsync;
    private ICallback callback;

    private ICallbackContainer container;
    private ICallback innerCallback = new ICallback.Stub() {
        @Override
        public void onResult(int result) throws RemoteException {
            Log.d(T, "server onResult: " + result);
            Log.d(T, "thread: " + Thread.currentThread().getName());
        }
    };

    private final ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1);

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(T, "server onBind, Thread: " + Thread.currentThread().getName());
        if (controller == null) {
            controller = new IController.Stub() {
                @Override
                public void registerCallback(ICallback callback) throws RemoteException {
                    Log.d(T, "server register callback");
                    Log.d(T, "thread: " + Thread.currentThread().getName());
                    AidlService.this.callback = callback;
                    //2秒后回调callback方法
//                    try {
//                        TimeUnit.SECONDS.sleep(2);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    callbackCommand.run();
                    executorService.schedule(callbackCommand, 2, TimeUnit.SECONDS);
                }

                @Override
                public void registerCallbackContainer(ICallbackContainer container) throws RemoteException {
                    AidlService.this.container = container;
                    executorService.schedule(containerCommand, 2, TimeUnit.SECONDS);
                }

                @Override
                public int transIn(State state) throws RemoteException {
                    Log.d(T, "callee transIn(), value: " + state.getValue());
                    Log.d(T, "thread: " + Thread.currentThread().getName());
                    int newValue = 2;
                    Log.d(T, "callee set value to " + newValue);
                    state.setValue(newValue);
                    return 1;
                }

                @Override
                public int transOut(State state) throws RemoteException {
                    Log.d(T, "callee transOut(), value: " + state.getValue());
                    Log.d(T, "thread: " + Thread.currentThread().getName());;
                    int newValue = 2;
                    Log.d(T, "callee set value to " + newValue);
                    state.setValue(newValue);
                    return 2;
                }

                @Override
                public int transInOut(State state) throws RemoteException {
                    Log.d(T, "callee transInOut(), value: " + state.getValue());
                    Log.d(T, "thread: " + Thread.currentThread().getName());
                    int newValue = 2;
                    Log.d(T, "callee set value to " + newValue);
                    state.setValue(newValue);
                    return 3;
                }

                @Override
                public void registerAsync(ICallback callback) throws RemoteException {
                    Log.d(T, "server register async");
                    Log.d(T, "thread: " + Thread.currentThread().getName());
                    AidlService.this.callbackAsync = callback;
                    callbackCommandAsync.run();
//                    executorService.schedule(callbackCommandAsync, 2, TimeUnit.SECONDS);
                }

            };
        }
        return controller.asBinder();
    }
}