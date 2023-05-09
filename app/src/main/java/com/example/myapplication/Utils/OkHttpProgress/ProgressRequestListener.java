package com.example.myapplication.Utils.OkHttpProgress;

public interface ProgressRequestListener {

    void onRequestProgress(long bytesWritten, long contentLength, boolean done);

}
