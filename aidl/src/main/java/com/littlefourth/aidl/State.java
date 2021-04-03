package com.littlefourth.aidl;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import static com.littlefourth.aidl.TAG.T;

/**
 * @ClassName: State
 * @Author: zhangjianfei
 * @CreateDate: 2021/4/3 5:04 PM
 * @Description:
 */
public class State implements Parcelable {

    public static final int DEFAULT_VALUE = -1000;
    private int value;

    public State(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    protected State() {
        value = DEFAULT_VALUE;
    }

    protected State(Parcel in) {
        value = in.readInt();
    }

    public static final Creator<State> CREATOR = new Creator<State>() {
        @Override
        public State createFromParcel(Parcel in) {
            return new State(in);
        }

        @Override
        public State[] newArray(int size) {
            return new State[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(value);
    }

    public void readFromParcel(Parcel reply) {
        int temp = reply.readInt();
        Log.d(T, "read new value  " + temp);
        value = temp;
    }
}
