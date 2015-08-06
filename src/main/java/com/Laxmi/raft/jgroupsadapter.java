package com.cisco.raft;

import org.apache.log4j.Logger;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

public class jgroupsadapter extends ReceiverAdapter {
	private static Logger LOG = Logger.getLogger(jgroupsadapter.class);

	private LeContext leContext;
	private Address address;
	private JChannel channel;


	public jgroupsadapter(LeContext leContext) {
		this.leContext = leContext;
		init();
	}

	private void init() {
		try {
			this.channel = new JChannel(raftProperties.getInstance().getjgroups_xml());
			this.channel.setReceiver(this);
			this.channel.connect(raftProperties.getInstance().getChannel_name());
			this.address = this.channel.getAddress();
			this.channel.setDiscardOwnMessages(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Address getAddress() {
		return address;
	}

	@Override
	public void receive(Message msg) {
		LOG.info("Received Cluster msg..");
		leContext.getCurrent_state().msgReceived(msg);
	}

	@Override
	public void viewAccepted(View view) {
		int clusterSize = view.getMembers().size();
		LOG.info("Cluster event has happened!" + clusterSize);
	}

	public void send(Message msg) {
		try {
			this.channel.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(Message tmp_msg, Address dest) {
		try {
			this.channel.send(dest, tmp_msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getClusterSize() {
		return this.channel.getView().getMembers().size();
	}

}
