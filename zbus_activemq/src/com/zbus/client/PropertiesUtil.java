package com.zbus.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
	public static String getKeyValue(String key){
		String message = "";
		try {
			InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream("config/global.properties");
			System.out.println(is);
			Properties props = new Properties(); 
			props.load(is);
			message = props.getProperty(key); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return message;
	}
}