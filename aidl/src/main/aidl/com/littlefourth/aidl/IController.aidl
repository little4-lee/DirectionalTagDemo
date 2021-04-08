// IController.aidl
package com.littlefourth.aidl;
import com.littlefourth.aidl.ICallback;
import com.littlefourth.aidl.ICallbackContainer;
import com.littlefourth.aidl.State;

interface IController {
    void registerCallback(ICallback callback);
    void registerCallbackContainer(ICallbackContainer container);

    int transIn(in State state);
    int transOut(out State state);
    int transInOut(inout State state);

    oneway void registerAsync(ICallback callback);
}