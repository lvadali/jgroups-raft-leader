package com.cisco.raft;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.jgroups.Address;
import org.jgroups.Message;

public class Leader extends State {
	private static Logger LOG = Logger.getLogger(Leader.class);

	private Timer heartBeatSendingtimer = new Timer(true);

	@Override
	public void begin() {
		startHeartBeatSendingThread();
	}

	@Override
	public void end() {
		stopHeartBeatSendingThread();
	}

	@Override
	public void msgReceived(Message msg) {
		clusterMsg cl_msg = clusterMsg.getClusterMsg(msg);
		LOG.info("msg Received Type:" + cl_msg.getMsg_type().name());
		if (cl_msg.getMsg_type().equals(clusterMsg.type.heart_beat)) {
			processHeartBeat(cl_msg);
		} else if (cl_msg.getMsg_type().equals(clusterMsg.type.vote_req)) {
			processVoteReq(cl_msg);
		} else if (cl_msg.getMsg_type().equals(clusterMsg.type.vote_resp)) {
			processVoteResp(cl_msg);
		}
	}

	private void processVoteResp(clusterMsg cl_msg) {
		// log an error
		return;
	}

	private void processVoteReq(clusterMsg cl_msg) {
		int term = Integer.valueOf(cl_msg.getData());
		if (term > LeContext.getInstance().getTerm()) {
			msgUtil.sendVoteRespMsg(cl_msg.getSrc());
			LeContext.getInstance().changeState(new Candidate());
		} else if (term <= LeContext.getInstance().getTerm()) {
			// send heart beat msg to specific instance
			msgUtil.sendHearBeatMsg(cl_msg.getSrc());
		}
	}

	private void processHeartBeat(clusterMsg cl_msg) {
		int term = Integer.valueOf(cl_msg.getData());
		if (cl_msg.getSrc() == LeContext.getInstance().getJgrps().getAddress())
			return;
		if (term > LeContext.getInstance().getTerm()) {
			LeContext.getInstance().changeState(new Follower());
		} else {
			msgUtil.sendHearBeatMsg(cl_msg.getSrc());
		}
		return;
	}

	private void startHeartBeatSendingThread() {
		int heartbeatFrequency = raftProperties.getInstance().getHeartbeatFrequency();
		heartBeatSendingtimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				LOG.info("Sending Heartbeat");
				msgUtil.sendHearBeatMsg();
			}
		}, 0, heartbeatFrequency * 1000);
		System.out.println("Heartbeat Sending Thread started");
	}

	private void stopHeartBeatSendingThread() {
		heartBeatSendingtimer.cancel();
	}

	@Override
	public String getName() {
		return "Leader";
	}
}
