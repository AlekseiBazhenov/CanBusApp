package ru.test.testjni.callback;

import java.lang.ref.WeakReference;

import ru.test.testjni.canbus.response.CanBusResponse;
import ru.test.testjni.MainActivity;

public class CanBusDataReceiverCallback implements ResultReceiverCallback<CanBusResponse> {

    private WeakReference<MainActivity> activity;

    public CanBusDataReceiverCallback(MainActivity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    public void onSuccess(CanBusResponse data) {
        if (activity != null && activity.get() != null) {
            activity.get().updateView(data);
        }
    }

    @Override
    public void onError(Exception e) {
        if (activity != null && activity.get() != null) {
            activity.get().showMessage(e.getMessage());
        }
    }
}
