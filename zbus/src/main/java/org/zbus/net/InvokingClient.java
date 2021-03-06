/**
 * The MIT License (MIT)
 * Copyright (c) 2009-2015 HONG LEIMING
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.zbus.net;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.zbus.net.Sync.Id;
import org.zbus.net.Sync.ResultCallback;
import org.zbus.net.Sync.Ticket;
import org.zbus.net.core.Dispatcher;
import org.zbus.net.core.Session;

public class InvokingClient<REQ extends Id, RES extends Id> 
		extends Client<REQ, RES> implements Invoker<REQ, RES> {	
	
	protected final Sync<REQ, RES> sync = new Sync<REQ, RES>();
	
	public InvokingClient(String host, int port, Dispatcher dispatcher) {
		super(host, port, dispatcher); 
	} 
	
	public InvokingClient(String address, Dispatcher dispatcher) {
		super(address, dispatcher); 
	} 
	 
	public void send(REQ req) throws IOException{
		if(req.getId() == null){
			req.setId(Ticket.nextId());
		} 
    	super.send(req);
    } 
	
	@Override
    protected void onMessage(Object obj, Session sess) throws IOException {  
		@SuppressWarnings("unchecked")
		RES res = (RES)obj; 
    	//先验证是否有Ticket处理
    	Ticket<REQ, RES> ticket = sync.removeTicket(res.getId());
    	if(ticket != null){
    		ticket.notifyResponse(res); 
    		return;
    	}  
    	
    	super.onMessage(obj, sess);
	}
	
	public void invokeAsync(REQ req, ResultCallback<RES> callback) throws IOException { 
    	connectSyncIfNeed();
    	
		Ticket<REQ, RES> ticket = null;
		if(callback != null){
			ticket = sync.createTicket(req, readTimeout, callback);
		} else {
			if(req.getId() == null){
				req.setId(Ticket.nextId());
			}
		} 
		try{
			session.write(req);  
		} catch(IOException e) {
			if(ticket != null){
				sync.removeTicket(ticket.getId());
			}
			throw e;
		}  
	} 
	
	public RES invokeSync(REQ req) throws IOException, InterruptedException {
		return this.invokeSync(req, this.readTimeout);
	}

	@Override
	protected void onSessionDestroyed(Session sess) throws IOException { 
		super.onSessionDestroyed(sess);
		sync.clearTicket();
	}
	
	public RES invokeSync(REQ req, int timeout) throws IOException, InterruptedException {
		Ticket<REQ, RES> ticket = null;
		try {
			connectSyncIfNeed();
			ticket = sync.createTicket(req, timeout);
			session.write(req);

			if (!ticket.await(timeout, TimeUnit.MILLISECONDS)) {
				if (!session.isActive()) {
					throw new IOException("Connection reset by peer");
				} else {
					return null;
				}
			}
			return ticket.response();
		} finally {
			if (ticket != null) {
				sync.removeTicket(ticket.getId());
			}
		}
	} 
	
}
