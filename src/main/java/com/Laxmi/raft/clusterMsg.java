package com.cisco.raft;

import java.io.Serializable;

import org.jgroups.Address;
import org.jgroups.Message;

public class clusterMsg implements Serializable {
	public enum type {
		vote_req, vote_resp, heart_beat
	};

	private type msg_type;

	private String data;

	private Address src;

	public clusterMsg(type msg_type, String data) {
		super();
		this.msg_type = msg_type;
		this.data = data;
		this.src = null;
	}

	public type getMsg_type() {
		return msg_type;
	}

	public void setMsg_type(type msg_type) {
		this.msg_type = msg_type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public static Message getMsg(type ctype, String data) {
		Message msg = new Message();
		msg.setObject(new clusterMsg(ctype, data));
		return msg;
	}

	public static clusterMsg getClusterMsg(Message msg) {
		clusterMsg cls_msg = (clusterMsg) msg.getObject();
		cls_msg.setSrc(msg.getSrc());
		return cls_msg;
	}

	public void setSrc(Address src) {
		this.src = src;
	}

	public Address getSrc() {
		return this.src;
	}
}
