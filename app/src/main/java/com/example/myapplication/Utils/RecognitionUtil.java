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
    // webapi interface address
    private static final String URL = "http://tupapi.xfyun.cn/v1/currency";
    // app ID
    private static final String APPID = "13bc7120";
    // app key
    private static final String API_KEY = "74a42b1433f879df65ed6bdc937b872e";

    private static final String IMAGE_NAME = "temp.jpg";

    //Pass in the file path
    public static void startRecognition(String path,RecBack back){

        new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    Map<String, String> header = buildHttpHeader();
                    byte[] imageByteArray = FileUtil.read(path);
                    String result = HttpUtil.doPost1(URL, header, imageByteArray);
                    System.out.println("Interface call result: " + result);
                    back.onFinished(result);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }.start();

    }
    /**
     * Assemble the http request header
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
            for (int r = 0; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
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
                int Code = -1;
                try {
                    Code = (int) Double.parseDouble(code);
                } catch (NumberFormatException e) {
                }
                bean.setCode(""+Code);
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
