package com.zbus.client;


import java.util.Observable;

/**
 * 
 * 此类为被观察者即topic
 * 
 * @author gnet
 * 
 */
public class Watched extends Observable {
	private static Watched instance = null;

	private Watched() {
	};

	public static Watched getInstance() {
		if (instance == null) {
			synchronized (Watched.class) {
				if (instance == null) {
					instance = new Watched();
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
