package com.example.myapplication.Utils.OkHttpProgress;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class ProgressHelper {


    /**
     *
     * The callback that wraps the request body for uploading the file
     * @param requestBody
     * @param progressRequestListener Progress callback interface
     * @return The wrapped request body with callback progress
     */
    public static ProgressRequestBody addProgressRequestListener(
            RequestBody requestBody, ProgressRequestListener progressRequestListener){
        return new ProgressRequestBody(requestBody,progressRequestListener);
    }
}
