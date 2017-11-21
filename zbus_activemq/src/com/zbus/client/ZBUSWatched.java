package com.zbus.client;


import java.util.Observable;

/**
 * 
 * 此类为被观察者即topic
 * 
 * @author gnet
 * 
 */
public class ZBUSWatched extends Observable {
	private static ZBUSWatched instance = null;

	private ZBUSWatched() {
	};

	public static ZBUSWatched getInstance() {
		if (instance == null) {
			synchronized (ZBUSWatched.class) {
				if (instance == null) {
					instance = new ZBUSWatched();
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
