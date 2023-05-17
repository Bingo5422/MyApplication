package com.example.myapplication.Utils.OkHttpProgress;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody {
    //The request body to be wrapped and decorated
    private final RequestBody requestBody;
    private final ProgressRequestListener progressListener;
    //The sink after decoration
    private BufferedSink bufferedSink;

    /**
     * Construct the class
     * @param requestBody The request body to be wrapped
     * @param progressListener Interface to be called back
     */
    public ProgressRequestBody(RequestBody requestBody, ProgressRequestListener progressListener) {
        this.requestBody = requestBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            //The bufferedSink after decoration
            bufferedSink = Okio.buffer(sink(sink));
        }
        // Write into the packed bufferedSink
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }


    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            //Number of bytes written
            long bytesWritten = 0L;
            //Total byte length
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    contentLength = contentLength();
                }
                //Increases the number of bytes currently written
                bytesWritten += byteCount;
                //callback
                progressListener.onRequestProgress(bytesWritten, contentLength, bytesWritten == contentLength);
            }
        };
    }
}
