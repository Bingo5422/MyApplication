package com.example.myapplication.Utils;

import android.util.Log;

import com.example.myapplication.Bean.RecognitionBean;
import com.example.myapplication.R;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apaches.commons.codec.binary.Base64;
import org.apaches.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecognitionUtil {
    // webapi 接口地址
    private static final String URL = "http://tupapi.xfyun.cn/v1/currency";
    // 应用ID
    private static final String APPID = "54d1647f";
    // 接口密钥
    private static final String API_KEY = "778bca817d2f90deb7ad79aebcc04186";
    // 图片名称
    private static final String IMAGE_NAME = "temp.jpg";
    // 图片url
    //private static final String IMAGE_URL = " ";

    // 图片地址
    //private static String PATH = "文件路径";

    //传入文件路径
    public static void startRecognition(String path,RecBack back){

        new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    Map<String, String> header = buildHttpHeader();
                    byte[] imageByteArray = FileUtil.read(path);
                    String result = HttpUtil.doPost1(URL, header, imageByteArray);
                    System.out.println("接口调用结果：" + result);
                    back.onFinished(result);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }.start();

    }
    /**
     * 组装http请求头
     */
    private static Map<String, String> buildHttpHeader() throws UnsupportedEncodingException {
        String curTime = System.currentTimeMillis() / 1000L + "";
        String param = "{\"image_name\":\"" + IMAGE_NAME + "\"}";
        String paramBase64 = new String(Base64.encodeBase64(param.getBytes("UTF-8")));
        String checkSum = DigestUtils.md5Hex(API_KEY + curTime + paramBase64);
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        header.put("X-Param", paramBase64);
        header.put("X-CurTime", curTime);
        header.put("X-CheckSum", checkSum);
        header.put("X-Appid", APPID);
        return header;
    }


    public static List<RecognitionBean> read(InputStream stream) {
        List<RecognitionBean> data = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(stream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            for (int r = 0; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
                int cellsCount = row.getPhysicalNumberOfCells();

                RecognitionBean bean = new RecognitionBean();
                String code = row.getCell(0).toString();
                String enName = row.getCell(1).toString();
                Cell cellName = row.getCell(2);
                String name = "";
                if (cellName != null) {
                    name = cellName.toString();
                }
                Cell cellCate = row.getCell(3);
                String cate = "";
                if (cellCate != null) {
                    cate = cellCate.toString();
                }
                int newCode = -1;
                try {
                    newCode = (int) Double.parseDouble(code);
                } catch (NumberFormatException e) {
                }
                bean.setCode(newCode + "");
                bean.setEnName(enName);
                bean.setName(name);
                bean.setCate(cate);
                data.add(bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }












}
