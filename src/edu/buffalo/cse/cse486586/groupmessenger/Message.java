package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.Serializable;

public class Message implements Serializable{

	public static final int MSG_NRM=1;
	public static final int MSG_SEQ=0;
	
	private int type;
	private String message;
	private String key;
	
	public Message(String k, String m,int t){
		this.key=k;
		this.message=m;
		this.type=t;
	}
	
	public Message(int t){
		this(null,null,t);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	
}
