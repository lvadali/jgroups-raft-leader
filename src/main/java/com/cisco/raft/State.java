package com.cisco.raft;

import org.jgroups.Message;

abstract class State {

	public abstract String getName();

	public abstract void begin();

	public abstract void end();

	public abstract void msgReceived(Message msg);
}
