package com.activemq.threadLocal;

import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import com.zbus.client.PropertiesUtil;

/**
 * 单例的获取链接
 * 
 * @author Jikey
 * @version 
 * @className: SingleConnection <br/>
 * @date: 2016-6-15 上午9:30:41 <br/>
 * @since JDK 1.7
 *
 */
public class SingleConnection{
	private static Logger logger=Logger.getLogger(SingleConnection.class.getName());
	private static String URL = PropertiesUtil.getKeyValue("ACTIVEMQ_URL");
	private static ConnectionFactory connectionFactory=new ActiveMQConnectionFactory(
			ActiveMQConnection.DEFAULT_USER,
			ActiveMQConnection.DEFAULT_PASSWORD, URL);
	private static Connection connection = null;
	private static SingleConnection instance = null;
	public static SingleConnection getInstance() {
		if (instance == null) {
			synchronized (SingleConnection.class) {
				if (instance == null) {
					instance = new SingleConnection();
				}
			}
		}
		return instance;
	}

	private SingleConnection() {
	}
	/**
	 * 获取单例的ConnectionFactory
	 * 
	 * @return
	 * @throws Exception
	 */
	public  static  synchronized Connection getConnectionFactory() throws Exception {
		logger.info("Connection:" + connection);
		if (connection != null)
			return connection;
		logger.info("getConnectionFactory create new");
		connection = connectionFactory.createConnection();
		logger.info("Connection:" + connection + "create success");
		//需要获取此链接的人自己去connection.start();
		return connection;
	}
}
