package com.example.myapplication.Utils.BaiDuApi;
import java.net.URLEncoder;
import com.example.myapplication.Utils.BaiDuApi.FileUtil;
import com.example.myapplication.Utils.RecBack;


/**
 * General Object and Scene Recognition
 */
public class BaiDuRecUtil {
    static String result = null;
    public static String baiduRec(String path, RecBack back) {
        // request url
        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v2/advanced_general";
        try {
            // local file path
            String filePath = path;
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // Note that the access_token is obtained for each request to simplify the coding.
            // The access_token in the online environment has an expiration time.
            // The client can cache it by itself and obtain it again after it expires.
            String accessToken = "24.e7cd13935643a1de5e21c5ab5f29823d.2592000.1686961899.282335-32510584";


            new Thread(){
                @Override
                public void run() {
                    super.run();

                    try {
                        result = HttpUtil.post(url, accessToken, param);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    back.onFinished(result);
                    System.out.println(result);
                }
            }.start();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
