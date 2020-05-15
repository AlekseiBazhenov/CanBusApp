package ru.test.testjni.callback;

public interface ResultReceiverCallback<T>{
    void onSuccess(T data);
    void onError(Exception exception);
}
