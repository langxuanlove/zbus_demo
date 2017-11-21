package com.activemq.threadLocal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import com.util.LogUtil;
/**
 * 使用线程此类发送消息,不是按顺序进行发送的,走的全部都是线程,因此不能保证此消息的发送顺序.
 * 
 * 此类关联ConnectionManager使用的.  与SingleConnection暂时无关联.
 * 
 * @author Jikey
 * @version 
 * @className: AmqPubLocal <br/>
 * @date: 2016-6-15 上午10:01:22 <br/>
 * @since JDK 1.7
 *
 */
public class AmqPubLocal implements Runnable {
	private static Logger logger=Logger.getLogger(AmqPubLocal.class.getName());
	private String message;
	private String topic ;
	private boolean flag;
	public AmqPubLocal(String topic, String message, boolean flag) {
		super();
		this.message = message;
		this.topic = topic;
		this.flag = flag;
	}
	public void run() {
		try {
			produce(topic,message,flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
	public String produce(String topic, String msg, boolean flag)
			throws Exception {
		logger.info("producer send msg:" + msg);
		if (msg == null || "".equals(msg)) {
			LogUtil.getLogger(getClass()).info("发送消息不能为空。");
			return "fail";
		}
		try {
			//此方法可以,在ConnectionManager中使用单例的同时复制了连接,线程中互不影响.
			Connection connection = ConnectionManager.GetConnection();
			connection.start();
			Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			TextMessage textMessage = session.createTextMessage(msg);
			Destination destination = null;
			if (flag) {
				logger.info("queue名称:"+topic);
				destination = session.createQueue(topic);
			} else {
				logger.info("topic名称:"+topic);
				destination = session.createTopic(topic);
			}
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			producer.send(textMessage);
			session.close();
			ConnectionManager.closeConnection();
			return "success";
		} catch (JMSException e) {
			LogUtil.getLogger(getClass()).info(e.getMessage());
			return "fail";
		}
	}

	//业务逻辑线程池
    private final static ExecutorService queue = newBlockingExecutorsUseCallerRun(Runtime.getRuntime().availableProcessors() * 2);

    /**
     * 阻塞线程池ExecutorService
     * @param size
     * @return
     */
    public static ExecutorService newBlockingExecutorsUseCallerRun(int size) {
		return new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(), new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				try {
                    executor.getQueue().put(r);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
			}
		});
    }
    public static void sendMsg(String topic,String message,boolean flag){
		queue.execute(new AmqPubLocal(topic,message, flag));
	}
    
//	public static void main(String[] args) throws Exception {
//	for (int j = 0; j < 100; j++) {
//		AmqPubLocal.sendMsg("threadLocal", "demo"+j, false);//使用此方法发送消息.
//		new Thread(new AmqPubLocal("threadLocal","demo"+j, false)).start();
//	}
//}
    /**
     * 
     * main:(此方法效率最高，使用阻塞的线程队列). <br/>
     *
     * @author Jikey
     * @param args
     * @throws Exception
     */
	public static void main(String[] args) throws Exception {
	for (int j = 0; j < 10000; j++) {
		queue.execute(new AmqPubLocal("threadLocal","demo"+j, false));
	}
}
}
