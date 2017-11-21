package org.zbus.pubsub;

import java.io.IOException;

import org.zbus.mq.server.MqServer;
import org.zbus.mq.server.MqServerConfig;

/**
 * 异步调用，无序，不属于应答模式
 * @author Jikey
 * @version 
 * @className: PubAsync <br/>
 * @date: 2016-1-7 下午2:11:12 <br/>
 * @since JDK 1.7
 *
 */
public class Server {
	public static void main(String[] args) throws Exception{
		MqServerConfig config = new MqServerConfig();
		config.serverHost = "192.168.1.196";
		config.serverPort = 15555;
		config.selectorCount =  0; //0 means default to CPU/4
		config.executorCount = 64;
		config.verbose =  true;
		config.storePath = "c:/store";
		config.trackServerList =  null; 
		config.serverMainIpOrder =  null;
		final MqServer server = new MqServer(config);
		try {
			server.start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("zbus服务启动异常 ！！！！！！！");
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					server.close();
					System.out.println("Zbus shutdown completed");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
	} 
}
