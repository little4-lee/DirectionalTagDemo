package com.littlefourth.client;

import android.annotation.SuppressLint;
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
import com.littlefourth.aidl.ICallbackContainer;
import com.littlefourth.aidl.IController;
import com.littlefourth.aidl.State;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.littlefourth.aidl.TAG.T;

public class MainActivity extends AppCompatActivity {
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
            Log.d(T, "onServiceConnected");
            isServiceBind.set(true);
            controller = IController.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(T, "onServiceDisconnected");
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

    @SuppressLint("NonConstantResourceId")
    public void clickBtn(View view) {
        switch (view.getId()) {
            case R.id.btnVerityCallback:
                if (isServiceBind.get()) {
                    registerCallback();
                }
                break;
            case R.id.btnVerityCallbackAsync:
                if (isServiceBind.get()) {
                    registerCallbackAsync();
                }
                break;

            case R.id.btnVerityCallbackContainer:
                if (isServiceBind.get()) {
                    registerCallbackContainer();
                }
                break;
            case R.id.btnTransIn:
                if (isServiceBind.get()) {
                    transIn();
                }
                break;
            case R.id.btnTransOut:
                if (isServiceBind.get()) {
                    transOut();
                }
                break;

            case R.id.btnTransInOut:
                if (isServiceBind.get()) {
                    transInOut();
                }
                break;

            default:
                //do nothing
                break;
        }
    }

    private void registerCallbackContainer() {
        if (controller != null) {
            try {
                controller.registerCallbackContainer(container);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerCallbackAsync() {
        if (controller != null) {
            try {
                controller.registerAsync(callbackAsync);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void transInOut() {
        if (controller != null) {
            try {
                Log.d(T, "caller value before transInOut(): " + stateInOut.getValue());
                controller.transInOut(stateInOut);
                Log.d(T, "caller value after transInOut(): " + stateInOut.getValue());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void transOut() {
        if (controller != null) {
            try {
                Log.d(T, "caller value before transOut(): " + stateOut.getValue());
                controller.transOut(stateOut);
                Log.d(T, "caller value after transOut(): " + stateOut.getValue());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void transIn() {
        if (controller != null) {
            try {
                Log.d(T, "caller value before transIn(): " + stateIn.getValue());
                controller.transIn(stateIn);
                Log.d(T, "caller value after transIn(): " + stateIn.getValue());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private IController controller;
    private final ICallback callback = new ICallback.Stub() {

        @Override
        public void onResult(int result) throws RemoteException {
            Log.d(T, "sync client onResult: " + result);
            Log.d(T, "thread: " + Thread.currentThread().getName());
        }
    };

    private final ICallback callbackAsync =  new ICallback.Stub() {
        @Override
        public void onResult(int result) throws RemoteException {
            Log.d(T, "async client onResult: " + result);
            Log.d(T, "thread: " + Thread.currentThread().getName());
        }
    };

    private final ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1);

    private ICallback remoteCallback;
    private Runnable remoteCommand = new Runnable() {
        @Override
        public void run() {
            if (remoteCallback != null) {
                try {
                    remoteCallback.onResult(3);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private final ICallbackContainer container = new ICallbackContainer.Stub() {
        @Override
        public void addCallback(ICallback callback) throws RemoteException {
            remoteCallback = callback;
            Log.d(T, "client addCallback, thread: " + Thread.currentThread().getName());
            remoteCommand.run();
            executorService.schedule(remoteCommand, 2, TimeUnit.SECONDS);
        }
    };

    private final State stateIn = new State(1);
    private final State stateOut = new State(1);
    private final State stateInOut = new State(1);

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