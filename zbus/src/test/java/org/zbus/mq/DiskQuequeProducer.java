package org.zbus.mq;

public class DiskQuequeProducer {


	public static void main(String[] args) throws Exception { 
		DiskQueque.init("c://test"); // 初始化MQ所在路径
		DiskQueque diskq = new DiskQueque("MyMQ");
		diskq.offer("test is ".getBytes());
		System.out.println(diskq.size());
		DiskQueque.destory(); // 清理掉Q环境
	}

}
