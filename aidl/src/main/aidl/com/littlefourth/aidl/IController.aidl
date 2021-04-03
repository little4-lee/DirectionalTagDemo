// IController.aidl
package com.littlefourth.aidl;
import com.littlefourth.aidl.ICallback;
import com.littlefourth.aidl.State;

interface IController {
    void registerCallback(ICallback callback);

    int transIn(in State state);
    int transOut(out State state);
    int transInOut(inout State state);
    int testArray(inout List<String> arr);
}