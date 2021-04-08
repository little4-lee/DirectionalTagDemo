// ICallbackContainer.aidl
package com.littlefourth.aidl;
import com.littlefourth.aidl.ICallback;

interface ICallbackContainer {

    oneway void addCallback(ICallback callback);
}