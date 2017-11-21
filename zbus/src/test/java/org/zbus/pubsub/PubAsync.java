package org.zbus.pubsub;

import org.zbus.broker.Broker;
import org.zbus.broker.BrokerConfig;
import org.zbus.broker.SingleBroker;
import org.zbus.mq.Producer;
import org.zbus.mq.Protocol.MqMode;
import org.zbus.net.Sync.ResultCallback;
import org.zbus.net.http.Message;

/**
 * 异步调用，无序，不属于应答模式
 * @author Jikey
 * @version 
 * @className: PubAsync <br/>
 * @date: 2016-1-7 下午2:11:12 <br/>
 * @since JDK 1.7
 *
 */
public class PubAsync {
	public static void main(String[] args) throws Exception{   
		BrokerConfig config = new BrokerConfig();
		config.setServerAddress("192.168.1.196:15555");
		final Broker broker = new SingleBroker(config);
		Producer producer = new Producer(broker, "123", MqMode.MQ);
		producer.createMQ();  
		Message msg = new Message();
		//设置消息题头，不然传输不了消息
		msg.setMq("123"); 
		msg.setBody("测试消息队列能不能使用");
		producer.invokeAsync(msg, new ResultCallback<Message>() {
			@Override
			public void onReturn(Message result) {
				System.out.println(result.getId());
				//ignore
			}
		});
		Thread.sleep(5000); //safe message sending out
		broker.close();
	} 
}
