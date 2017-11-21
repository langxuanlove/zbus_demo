package com.activemq.send;

import java.util.Date;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import com.util.LogUtil;
import com.zbus.client.PropertiesUtil;

public class AmqPub {
	private static ConnectionFactory connectionFactory;
	private String URL = PropertiesUtil.getKeyValue("ACTIVEMQ_URL");
	private Connection connection = null;
	private static AmqPub instance = null;

	public static AmqPub getInstance() {
		if (instance == null) {
			synchronized (AmqPub.class) {
				if (instance == null) {
					instance = new AmqPub();
				}
			}
		}
		return instance;
	}

	private AmqPub() {
	}

	/**
	 * 获取单例的ConnectionFactory
	 * 
	 * @return
	 * @throws Exception
	 */
	private synchronized Connection getConnectionFactory() throws Exception {
		LogUtil.getLogger(getClass()).info("Connection:" + connection);
		if (connection != null)
			return connection;
		LogUtil.getLogger(getClass()).info("getConnectionFactory create new");
		connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, URL);
		connection = connectionFactory.createConnection();
		connection.start();
		LogUtil.getLogger(getClass()).info(
				"Connection:" + connection + "create success");
		return connection;
	}

	/**
	 * 
	 * produce:(发送消息). <br/>
	 * 
	 * @author Jikey
	 * @param msg
	 * @param topic
	 * @param flag
	 *            判断是队列模式还是主题模式发送,true:队列;false:主题
	 * @return
	 * @throws Exception
	 */
	public String produce(String msg, String topic, boolean flag)
			throws Exception {
		LogUtil.getLogger(getClass()).info("producer send msg:" + msg);
		if (msg == null || "".equals(msg)) {
			LogUtil.getLogger(getClass()).info("发送消息不能为空。");
			return "fail";
		}
		try {
			Connection connection = getConnectionFactory();
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			TextMessage textMessage = session.createTextMessage(msg);
			Destination destination = null;
			if (flag) {
				destination = session.createQueue(topic);
			} else {
				destination = session.createTopic(topic);
			}
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			producer.send(textMessage);
			session.close();
			return "success";
		} catch (JMSException e) {
			LogUtil.getLogger(getClass()).info(e.getMessage());
			return "fail";
		}finally{
			// 一般在项目中尽量不要关闭connection
//			LogUtil.getLogger(getClass()).info("关闭连接.");
//			connection.close();
		}
	}
}
