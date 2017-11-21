package com.activemq.client;


import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import com.zbus.client.PropertiesUtil;


public class DurableSubscriberTopicEventListener implements ServletContextListener, MessageListener {
	private String URL = PropertiesUtil.getKeyValue("ACTIVEMQ_URL");
	private String TOPIC = PropertiesUtil.getKeyValue("ACTIVEMQ_TOPIC");
	private String clientId=PropertiesUtil.getKeyValue("ACTIVEMQ_CLIENT_ID");
	private AMQWatched watched = AMQWatched.getInstance();
	private Logger logger=Logger.getLogger(getClass().getName());
	
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.info("销毁程序");
	}

	public void contextInitialized(ServletContextEvent arg0) {
		logger.info("初始化......");
		try {
			getMessage();
			logger.info("启动消费者.....");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getMessage() throws Exception {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				ActiveMQConnection.DEFAULT_USER,
				ActiveMQConnection.DEFAULT_PASSWORD, URL);
		Connection connection = connectionFactory.createConnection();
		connection.setClientID(clientId);
		connection.start();
		Session session = connection.createSession(false,
				Session.AUTO_ACKNOWLEDGE);
		Topic topic = session.createTopic(TOPIC);
		MessageConsumer consumer = session.createDurableSubscriber(topic,clientId);
		consumer.setMessageListener(this);
		/** 方法是消息推的模式，即是activemq主动推送给客户端 */
		/** 开始 以下方法是消息拉的模式，适合一次性的调用，关闭连接，也就是一次性接受，节省线程上的损耗，即自己去activemq中主动获取消息 */
		// while (true) {
		// TextMessage message = (TextMessage) consumer.receive(10000);
		// if (message != null) {
		// logger.info("get message is :" + message.getText());
		// } else {
		// logger.info("get message is : null");
		// break;
		// }
		// }
		// session.close();
		// connection.close();
		/** 结束 */
	}

	/**
	 * 接受activemq的消息
	 */
	public void onMessage(Message message) {
		TextMessage msg = (TextMessage) message;
		try {
			logger.info("get message is :" + msg.getText());
			//消息必须变化,不然不会通知更新的.切记切记
			String info = msg.getText().toString();
//			String msgr="{\"msg_id\":\""+UUID.randomUUID().toString().replace("-", "")+"\",\"msg_body\":\""+info+"\"}";
			watched.setData(info);
			logger.info("观察者数量为：" + watched.countObservers());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception {
		DurableSubscriberTopicEventListener a=new DurableSubscriberTopicEventListener();
		a.getMessage();
	}
}
