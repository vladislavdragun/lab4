package com.example.stu.model;

public interface Callback<T> {

    void onError(Throwable error);

    void onResults(T data);

}
