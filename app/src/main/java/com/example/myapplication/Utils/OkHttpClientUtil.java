//package com.example.myapplication.Utils;
//
//
//import java.io.IOException;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//import okhttp3.ConnectionPool;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
/**
 * 先不用这个类
 *
 */
//
//
////@Slf4j
//public class OkHttpClientUtil {
//
//    private volatile static OkHttpClient client;
//
//    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//
//    private static final int MAX_IDLE_CONNECTION = 10;
//
//    private static final long KEEP_ALIVE_DURATION = 60;
//
//    private static final long CONNECT_TIMEOUT = 30;
//
//    private static final long READ_TIMEOUT = 30;
//
//    /**
//     * 单例模式(双重检查模式) 获取类实例
//     *
//     * @return client
//     */
//    private static OkHttpClient getInstance() {
//        if (client == null) {
//            synchronized (OkHttpClient.class) {
//                if (client == null) {
//                    client = new OkHttpClient.Builder()
//                            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
//                            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
//                            .connectionPool(new ConnectionPool(MAX_IDLE_CONNECTION, KEEP_ALIVE_DURATION, TimeUnit.MINUTES))
//                            .build();
//                }
//            }
//        }
//        return client;
//    }
//
//    public static String syncPost(String url, String json) {
//        RequestBody body = RequestBody.create(JSON, json);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//        try {
//            Response response = OkHttpClientUtil.getInstance().newCall(request).execute();
//            if (response.isSuccessful()) {
//                String result = response.body().string();
////                Log.info("syncPost response = {}, responseBody= {}", response, result);
//                return result;
//            }
//            String result = response.body().string();
////            log.info("syncPost response = {}, responseBody= {}", response, result);
//            throw new IOException("三方接口返回http状态码为" + response.code());
//        } catch (Exception e) {
////            log.error("syncPost() url:{} have a ecxeption {}", url, e);
//            throw new RuntimeException("syncPost() have a ecxeption {}" + e.getMessage());
//        }
//    }
//
//    public static String syncGet(String url, Map<String, Object> headParamsMap) {
//        Request request;
//        final Request.Builder builder = new Request.Builder().url(url);
//        try {
//            if (!CollectionUtils.isEmpty(headParamsMap)) {
//                final Iterator<Map.Entry<String, Object>> iterator = headParamsMap.entrySet().iterator();
//                while (iterator.hasNext()) {
//                    final Map.Entry<String, Object> entry = iterator.next();
//                    builder.addHeader(entry.getKey(), (String) entry.getValue());
//                }
//            }
//            request = builder.build();
//            Response response = OkHttpClientUtil.getInstance().newCall(request).execute();
//            String result = response.body().string();
////            log.info("syncGet response = {},responseBody= {}", response, result);
//            if (!response.isSuccessful()) {
//                throw new IOException("三方接口返回http状态码为" + response.code());
//            }
//            return result;
//        } catch (Exception e) {
////            log.error("remote interface url:{} have a ecxeption {}", url, e);
//            throw new RuntimeException("三方接口返回异常");
//        }
//    }
//
//
//}
