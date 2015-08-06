package com.cisco.raft;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.jgroups.Message;

public class Follower extends State {
	private static Logger LOG = Logger.getLogger(Follower.class);

	private Timer heartBeatMonitortimer = new Timer(true);
	private boolean heartbeatRcvd = false;

	@Override
	public void begin() {
		startHeartBeatMonitorThread();
	}

	@Override
	public void end() {
		stopHeartBeatMonitorThread();
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
		// error log
		return;
	}

	private void processVoteReq(clusterMsg cl_msg) {
		int term = Integer.valueOf(cl_msg.getData());
		if (term > LeContext.getInstance().getTerm()) {
			LeContext.getInstance().setTerm(term);
			msgUtil.sendVoteRespMsg(cl_msg.getSrc());
			LeContext.getInstance().changeState(new Candidate());
		} else {
			// neglect election
			// error scenario
		}

	}

	private void processHeartBeat(clusterMsg cl_msg) {
		int term = Integer.valueOf(cl_msg.getData());
		if (term >= LeContext.getInstance().getTerm()) {
			heartbeatRcvd = true;
			if (term > LeContext.getInstance().getTerm())
				LeContext.getInstance().setTerm(term);
		} else {
			// similar to not getting heart beat
			// soon will become candidate when it gets vote req or monitor time
			// expires
		}
	}

	private void startHeartBeatMonitorThread() {
		int heartbeatTimeout = raftProperties.getInstance().getHeartbeatTimeout();
		heartBeatMonitortimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				if (heartbeatRcvd) {
					heartbeatRcvd = false;
					LOG.info("Heart Beat Received Properly..");
				} else {
					LOG.info("No Heart Beat..Triggering election");
					LeContext.getInstance().setTriggeredElection(true);
					LeContext.getInstance().changeState(new Candidate());
				}
			}
		}, heartbeatTimeout * 1000, heartbeatTimeout * 1000);
		System.out.println("Heartbeat Monitor Thread started");
	}

	private void stopHeartBeatMonitorThread() {
		heartBeatMonitortimer.cancel();
		heartBeatMonitortimer = null;
	}

	@Override
	public String getName() {
		return "Follower";
	}

}
