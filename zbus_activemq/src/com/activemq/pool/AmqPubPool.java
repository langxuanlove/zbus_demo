package com.activemq.pool;

import java.util.logging.Logger;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnection;
import org.apache.activemq.pool.PooledConnectionFactory;
import com.zbus.client.PropertiesUtil;


public class AmqPubPool {
  public static final Logger log = Logger.getLogger("AmqPubByPool");
  private static PooledConnectionFactory poolFactory;
  public static final String URL= PropertiesUtil.getKeyValue("ACTIVEMQ_URL");

  /**
   * 获取单例的PooledConnectionFactory
   *  @return
   */
  private static synchronized PooledConnectionFactory getPooledConnectionFactory() {
    log.info("getPooledConnectionFactory");
    if (poolFactory != null) return poolFactory;
    log.info("getPooledConnectionFactory create new");
    ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER, 
    		ActiveMQConnectionFactory.DEFAULT_PASSWORD, URL);
    poolFactory = new PooledConnectionFactory(factory);      
    // 池中借出的对象的最大数目
    poolFactory.setMaxConnections(5);
    poolFactory.setMaximumActiveSessionPerConnection(50);      
    //后台对象清理时，休眠时间超过了3000毫秒的对象为过期
    poolFactory.setTimeBetweenExpirationCheckMillis(3000);
    log.info("getPooledConnectionFactory create success");
    return poolFactory;
  }

  /**
   * 1.对象池管理connection和session,包括创建和关闭等
   * 2.PooledConnectionFactory缺省设置MaxIdle为1，
   *  官方解释Set max idle (not max active) since our connections always idle in the pool.   *
   *  @return   * @throws JMSException
   */
  public static Session createSession() throws JMSException {
    PooledConnectionFactory poolFactory = getPooledConnectionFactory();
    PooledConnection pooledConnection = (PooledConnection) poolFactory.createConnection();
    //false 参数表示 为非事务型消息，后面的参数表示消息的确认类型（见4.消息发出去后的确认模式）
    log.info("连接"+ pooledConnection);
    return pooledConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
  }

  public static void produce(String subject, String msg) {
    log.info("producer send msg: {} "+ msg);
    if ("".equals(msg)||msg==null) {
      log.info("发送消息不能为空。");
      return;
    }
    try {
      Session session = createSession();
      log.info("create session"+session);
      TextMessage textMessage = session.createTextMessage(msg);
      Destination destination = session.createQueue(subject);
      MessageProducer producer = session.createProducer(destination);
      producer.setDeliveryMode(DeliveryMode.PERSISTENT);
      producer.send(textMessage);
      log.info("create session success");
    } catch (JMSException e) {
      log.info(e.getMessage());
    }
  }

  public static void main(String[] args) {
	  for (int i = 0; i < 10; i++) {
		  AmqPubPool.produce("demo", "hello");
	}
  }
}
