package com.example.cameraipcapitalpartners.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.BaseEncoding;

public class ForwardAction extends ActionAdapter {
	//private static final String CAM = "http://cam.foobar/image.jpg";
	private static String sCam = null;
	
	@Override
	public String execute(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		String param = request.getParameter("url"); // /forward?url=URL
		String cam = request.getParameter("cam"); // /forward?cam=CAM
		if (cam != null) {
			//param = CAM.replace("cam", cam);
			param = getSCam(request).replace("cam", cam);
		}
		//System.out.println("param: " + param);
		
		response.setContentType("image/jpeg");
		BufferedOutputStream output = null;
		BufferedInputStream input = null;
		
		try {
			
			URL url = new URL(param);
			URLConnection conn = url.openConnection();
			
			/* HTTP Basic Authorization. Requires Java 8, Apache Commons Codec 
			 * or Google Guava. Usng Google Guava
			 */
			if (url.getUserInfo() != null) {
				byte[] bytes = url.getUserInfo().getBytes();
				String auth = BaseEncoding.base64().encode(bytes);
			    conn.setRequestProperty("Authorization", auth);
			}

			input = new BufferedInputStream(conn.getInputStream());
			output = new BufferedOutputStream(response.getOutputStream());
	        int ch = 0;
	        
	        while((ch = input.read()) != -1) {
	        	output.write(ch);
	        }
	        
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	private static synchronized String getSCam(HttpServletRequest request) {
		if (sCam == null) {
			String filename = "/WEB-INF/forward.properties";
			ServletContext context = request.getSession().getServletContext();
			InputStream input = context.getResourceAsStream(filename);
			Properties app_properties = new Properties();
			
			try {
				app_properties.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			sCam = app_properties.getProperty("cam");
			//System.out.println("sCam: " + sCam);
		}
		return sCam;
	}
}
