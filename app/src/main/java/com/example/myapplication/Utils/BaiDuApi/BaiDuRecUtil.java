package com.example.myapplication.Utils.BaiDuApi;
import java.net.URLEncoder;
import com.example.myapplication.Utils.BaiDuApi.FileUtil;
import com.example.myapplication.Utils.RecBack;


/**
 * 通用物体和场景识别
 */
public class BaiDuRecUtil {
    static String result = null;
    public static String baiduRec(String path, RecBack back) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/image-classify/v2/advanced_general";
        try {
            // 本地文件路径
            String filePath = path;
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
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
