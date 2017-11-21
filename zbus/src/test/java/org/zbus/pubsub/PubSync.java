package org.zbus.pubsub;

import org.zbus.broker.Broker;
import org.zbus.broker.BrokerConfig;
import org.zbus.broker.SingleBroker;
import org.zbus.mq.Producer;
import org.zbus.mq.Protocol.MqMode;
import org.zbus.net.http.Message;
/**
 * 和mq的应答模式不一样
 * @className: PubSync <br/>
 *
 */
public class PubSync {
	
	public static void main(String[] args) throws Exception{   
		BrokerConfig config = new BrokerConfig();
		config.setServerAddress("192.168.1.196:15555");
		final Broker broker = new SingleBroker(config);
		Producer producer = new Producer(broker, "123", MqMode.PubSub);
		producer.createMQ(); 
		Message msg=new Message();
		msg.setRecver("IBD104");
		//设置消息题头，不然传输不了消息，设置主题topic,必须设置topic名字
		msg.setTopic("123"); 
//		for(int i=0;i<10;i++){
			msg.setBody("hello world");
			//客户端超时时间,如果有回执证明，zbus收到此消息.
			Message reMsg=producer.invokeSync(msg,10000);
			System.out.println("messageID是："+reMsg.getId());
			//以上逻辑可以实现注册成功机制.
//		} 
		broker.close();
	} 
}
