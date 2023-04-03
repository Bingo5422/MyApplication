package com.example.myapplication.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 文件操作工具类
 */
public class FileUtil {

	/**
	 * 读取文件内容为二进制数组
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static byte[] read(String filePath) throws IOException {

		InputStream in = new FileInputStream(filePath);
		byte[] data = inputStream2ByteArray(in);
		in.close();

		return data;
	}

	/**
	 * 流转二进制数组
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	private static byte[] inputStream2ByteArray(InputStream in) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 4];
		int n = 0;
		while ((n = in.read(buffer)) != -1) {
			out.write(buffer, 0, n);
		}
		return out.toByteArray();
	}

	/**
	 * 保存文件
	 * 
	 * @param filePath
	 * @param fileName
	 * @param content
	 */
	public static void save(String filePath, String fileName, byte[] content) {
		try {
			File filedir = new File(filePath);
			if (!filedir.exists()) {
				filedir.mkdirs();
			}
			File file = new File(filedir, fileName);
			OutputStream os = new FileOutputStream(file);
			os.write(content, 0, content.length);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解压缩zip
	 *
	 */
	public static void unzip(String filePath, String outputDir) {
		try {
			String name = "";
			BufferedOutputStream dest = null;
			BufferedInputStream is = null;
			ZipEntry entry;
			ZipFile zipfile = new ZipFile(filePath);

			Enumeration dir = zipfile.entries();
			while (dir.hasMoreElements()){
				entry = (ZipEntry) dir.nextElement();

				if( entry.isDirectory()){
					name = entry.getName();
					name = name.substring(0, name.length() - 1);
					File fileObject = new File(outputDir + name);
					fileObject.mkdir();
				}
			}

			Enumeration e = zipfile.entries();
			while (e.hasMoreElements()) {
				entry = (ZipEntry) e.nextElement();

				is = new BufferedInputStream(zipfile.getInputStream(entry));
				int count;
				byte[] dataByte = new byte[1024];
				FileOutputStream fos = new FileOutputStream(outputDir+"/"+entry.getName());
				dest = new BufferedOutputStream(fos, 1024);
				while ((count = is.read(dataByte, 0, 1024)) != -1) {
					dest.write(dataByte, 0, count);
				}
				dest.flush();
				dest.close();
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
