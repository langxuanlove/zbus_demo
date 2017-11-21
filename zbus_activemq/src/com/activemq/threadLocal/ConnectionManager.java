package com.activemq.threadLocal;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.zbus.client.PropertiesUtil;

public class ConnectionManager {
	private static Logger logger=Logger.getLogger(ConnectionManager.class.getName());
	 //用于保存connection
    private static ThreadLocal<Connection> connectionHolder=new ThreadLocal<Connection>();  
    private static String URL = PropertiesUtil.getKeyValue("ACTIVEMQ_URL");
    private static ConnectionFactory connectionFactory= new ActiveMQConnectionFactory(
			ActiveMQConnection.DEFAULT_USER,
			ActiveMQConnection.DEFAULT_PASSWORD, URL);
    /** 
     * 获取连接 
     * @return 
     */  
    public static Connection GetConnection()  
    {  
        Connection conn=connectionHolder.get();  
        if(conn==null)  
        {  
            try {  
                conn=connectionFactory.createConnection();  
                logger.info("创建连接为:"+conn);
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
            connectionHolder.set(conn);
        } 
        logger.info("返回的连接为:"+conn);
        return conn;  
    } 
    
    
    
  //关闭连接  
    public static void closeConnection() {  
        Connection conn = connectionHolder.get();  
        if (conn != null) {  
            try {  
                conn.close();  
                //从ThreadLocal中清除Connection  
                logger.info("关闭连接"+conn);
                connectionHolder.remove();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }     
        }  
    }  
      
}
