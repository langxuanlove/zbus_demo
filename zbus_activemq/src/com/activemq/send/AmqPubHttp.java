package com.activemq.send;

import java.util.HashMap;
import java.util.Map;
import com.util.HttpRequestor;
import com.util.LogUtil;




public class AmqPubHttp {
	/**
	 * 
	 * sendMessage:(post请求方式发送消息). <br/>
	 *
	 * @author Jikey
	 * @param address
	 * @param topic
	 * @param topicType
	 * @param message
	 * @return
	 */
	public  String sendMessage(String address,String topic,String topicType,String message){
		HttpRequestor httpTools =new  HttpRequestor();
		String result="fail";
		String res="";
		try {
			String url="http://"+address+"/api/message/"+topic+"?type="+topicType.toLowerCase()+"&body="+message;
			LogUtil.getLogger(getClass()).info(url);
			Map<String, Object> dataMap = new HashMap<String, Object>();
			res=httpTools.doPost(url, dataMap);
			LogUtil.getLogger(getClass()).info("返回的消息:"+res);
			if(res.contains("Message")&res.contains("sent")){
				result= "success";
			}else{
				result= "fail";
			}
		} catch (Exception e) {
			LogUtil.getLogger(getClass()).info("发送失败！");
			result="fail";
		}
		return result;		
	}
	public static void main(String[] args) {
		new AmqPubHttp().sendMessage("192.168.4.52:8161", "quueee", "queue", "demo");
	}
}
