package com.webadminservice.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class fileDownload extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(fileDownload.class);

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// リクエストパラメータを取得する
		String fileName = req.getParameter("fileName");
		String proxy = req.getParameter("proxy");

		if(proxy != null && !proxy.isEmpty() && fileName != null && !fileName.isEmpty()){
			try{
				HttpURLConnection conn = null;
				try {
					// HTTP通信開始
					String urlStr = proxy + "?fileName=" + URLEncoder.encode(fileName,"UTF-8");
					URL url = new URL(urlStr);

					conn = (HttpURLConnection)url.openConnection();
					conn.setUseCaches(false);
					conn.connect();

					// レスポンス受信
					InputStream is = conn.getInputStream();

					String headerFileName = conn.getHeaderField("filename");
					if(headerFileName != null){
						resp.setHeader("Expires:", "0"); // eliminates browser caching
						resp.setHeader("filename", headerFileName);
						try {
							resp.setHeader("Content-Disposition","attachment; filename="+headerFileName);
						} catch (Exception e1) {
							throw e1;
						}
						try{
							OutputStream outStream = resp.getOutputStream();
							byte buf[] = new byte[256];
							int len;
							while((len = is.read(buf)) !=-1){
								outStream.write(buf, 0, len);
							}
							outStream.flush();
						} catch (Exception e) {
							throw e;
						}
					}
					is.close();
				} catch (Exception e) {
					throw e;
				}
			}catch (Exception e) {
			}
		}else{
			if(fileName != null && !fileName.isEmpty()){
				File file = new File(fileName);
				if(file.exists()){
					FileInputStream fis = new FileInputStream(file);

					resp.setContentLength((int)file.length());
					resp.setHeader("Expires:", "0"); // eliminates browser caching
					resp.setHeader("filename", file.getName());
					try {
						resp.setHeader("Content-Disposition","attachment; filename="+file.getName());
					} catch (Exception e1) {
					}
					try{
						OutputStream outStream = resp.getOutputStream();
						byte buf[] = new byte[256];
						int len;
						while((len = fis.read(buf)) !=-1){
							outStream.write(buf, 0, len);
						}
						outStream.flush();
					} catch (Exception e) {
					}
					fis.close();
				}
			}
		}
	}
}
