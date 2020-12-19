package com.liuyu.redis.lock.demo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadFile {

	public static void downloadFile(String remoteFilePath, String localFilePath) {
	    URL urlfile = null;
	    HttpURLConnection httpUrl = null;
	    BufferedInputStream bis = null;
	    BufferedOutputStream bos = null;
	    File f = new File(localFilePath);
	    try {
	        urlfile = new URL(remoteFilePath);
	        httpUrl = (HttpURLConnection) urlfile.openConnection();
	        httpUrl.connect();
	        bis = new BufferedInputStream(httpUrl.getInputStream());
	        bos = new BufferedOutputStream(new FileOutputStream(f));
	        int len = 2048;
	        byte[] b = new byte[len];
	        while ((len = bis.read(b)) != -1) {
	            bos.write(b, 0, len);
	        }
	        bos.flush();
	        bis.close();
	        httpUrl.disconnect();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            bis.close();
	            bos.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	public static void main(String[] args) {
		String remoteFilePath = "http://jyt.yn.gov.cn/resources/pdfjs/web/viewer.html?file=/userfiles/company/ynsjyt/zhoud/202012/20201203160904667.pdf";
		String localFilePath = "F:\\2.pdf";
		downloadFile(remoteFilePath, localFilePath);
	}
}
