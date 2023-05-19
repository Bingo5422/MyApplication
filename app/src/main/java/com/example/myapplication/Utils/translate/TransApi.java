package com.example.myapplication.Utils.translate;

import java.util.HashMap;
import java.util.Map;

public class TransApi {
    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    private static String appid = "20230414001641348";
    private static String securityKey = "tSVZRUdaYcfIhiwCgKb0";


    public static String getTransResult(String query, String from, String to) {
        Map<String, String> params = buildParams(query, from, to);
        return HttpGet.get(TRANS_API_HOST, params);
    }

    private static Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", appid);

        // random number
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // sign
        String src = appid + query + salt + securityKey; // Original text before encryption
        params.put("sign", MD5.md5(src));

        return params;
    }

}
