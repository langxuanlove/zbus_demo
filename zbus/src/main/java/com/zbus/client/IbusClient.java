package com.zbus.client;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import org.zbus.broker.Broker;
import org.zbus.broker.BrokerConfig;
import org.zbus.broker.SingleBroker;
import org.zbus.mq.Consumer;
import org.zbus.mq.Protocol.MqMode;
import org.zbus.net.core.Session;
import org.zbus.net.http.Message;
import org.zbus.net.http.Message.MessageHandler;



/**
 * 
 * 初始化ebus接收队列消息,同时发送ebus的队列名称到ibus上去
 * 
 * @author Jikey
 * @version 
 * @className: ZbusClient <br/>
 * @date: 2016-1-27 下午4:55:09 <br/>
 * @since JDK 1.7
 *
 *	如果不注册成服务没法实例化
 *
 */

public class IbusClient implements ServletContextListener{

	private Logger logger=Logger.getLogger(getClass().getName());
	private Watched watched = Watched.getInstance();
	
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		logger.info("停止接受消息........");
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		try {
			getQueueMsg();
			logger.info("客户端启动消息成功............");
		} catch (Exception e) {
			logger.info("客户端启动消息失败............");
		}
		
	}
	public void getTopicMsg()throws Exception{
		//1）创建Broker代表
		BrokerConfig config = new BrokerConfig();
		config.setServerAddress(PropertiesUtil.getKeyValue("ZBUS_SERVER")); 
		final Broker broker = new SingleBroker(config);
		//2) 创建消费者 
		Consumer c = new Consumer(broker, PropertiesUtil.getKeyValue("MQ_TOPIC"), MqMode.PubSub); 
		c.setTopic(PropertiesUtil.getKeyValue("MQ_TOPIC").trim()); 
		final AtomicLong counter = new AtomicLong(0);
		c.onMessage(new MessageHandler() { 
			@Override
			public void handle(Message msg, Session sess) throws IOException {
				logger.info("获取消息:"+msg.getBodyString());
				watched.setData(msg.getBodyString());
				logger.info("MyListener观察者数量为：" + watched.countObservers());
				logger.info("消息序号："+counter.incrementAndGet());
			}
		});
		c.start(); 
	}
	
	public void getQueueMsg()throws Exception{
		//1）创建Broker代表
		BrokerConfig config = new BrokerConfig();
		config.setServerAddress(PropertiesUtil.getKeyValue("ZBUS_SERVER")); 
		final Broker broker = new SingleBroker(config);
		//2) 创建消费者 
		Consumer c = new Consumer(broker, PropertiesUtil.getKeyValue("MQ_QUEUE"), MqMode.MQ); 
		final AtomicLong counter = new AtomicLong(0);
		c.onMessage(new MessageHandler() { 
			@Override
			public void handle(Message msg, Session sess) throws IOException {
				logger.info("获取消息:"+msg.getBodyString());
				watched.setData(msg.getBodyString());
				logger.info("MyListener观察者数量为：" + watched.countObservers());
				logger.info("消息序号："+counter.incrementAndGet());
			}
		});
		c.start(); 
	}
	
}
