// IController.aidl
package com.littlefourth.aidl;
import com.littlefourth.aidl.ICallback;

interface IController {
    void registerCallback(ICallback callback);
}