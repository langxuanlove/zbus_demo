package com.activemq.client;


import java.util.Observable;

/**
 * 
 * 此类为被观察者即topic
 * 
 * @author gnet
 * 
 */
public class AMQWatched extends Observable {
	private static AMQWatched instance = null;

	private AMQWatched() {
	};

	public static AMQWatched getInstance() {
		if (instance == null) {
			synchronized (AMQWatched.class) {
				if (instance == null) {
					instance = new AMQWatched();
				}
			}
		}
		return instance;
	}

	private String data = "";

	public String getData() {
		return data;
	}

	public void setData(String data) {
		if (!this.data.equals(data)) {
			this.data = data;
			setChanged();
		}
		notifyObservers();
	}
}
