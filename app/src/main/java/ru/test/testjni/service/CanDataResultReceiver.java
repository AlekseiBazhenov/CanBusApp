package ru.test.testjni.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import ru.test.testjni.callback.ResultReceiverCallback;

public class CanDataResultReceiver<T> extends ResultReceiver {

    static final int RESULT_CODE_OK = 1000;
    static final int RESULT_CODE_ERROR = 1001;

    static final String PARAM_RESULT = "RESULT";
    static final String PARAM_EXCEPTION = "EXCEPTION";

    private ResultReceiverCallback<T> receiverCallback;

    CanDataResultReceiver(Handler handler) {
        super(handler);
    }

    void setReceiverCallback(ResultReceiverCallback<T> receiver) {
        receiverCallback = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (receiverCallback == null) {
            throw new IllegalArgumentException("CanDataResultReceiver, receiverCallback == null");
        }

        if (resultCode == RESULT_CODE_OK) {
            receiverCallback.onSuccess((T) resultData.getSerializable(PARAM_RESULT));
        } else {
            receiverCallback.onError(new Exception(resultData.getString(PARAM_EXCEPTION)));
        }
    }
}
