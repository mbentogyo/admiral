package com.ThePod.Admirals.network.callback;

public interface DataCallback<T> {
    void onReceive(T data);
}
