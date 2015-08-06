package com.cisco.raft;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.jgroups.Address;
import org.jgroups.Message;

public class Candidate extends State {
	private static Logger LOG = Logger.getLogger(Candidate.class);

	private Timer electionTimer;
	private boolean electionInProgress = false;
	private int currentElectionCnt = 0;
	private boolean triggeredElection = false;
	private Set<Address> respondedlist;

	@Override
	public void begin() {
		triggeredElection = LeContext.getInstance().isTriggeredElection();
		if (triggeredElection) {
			respondedlist = new HashSet<>();
			LeContext.getInstance().incrementTerm();
			startElection();
		}
	}

	@Override
	public void end() {
		if (triggeredElection) {
			stopElection();
		}
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
		int term = Integer.valueOf(cl_msg.getData());
		if (!triggeredElection)
			return;

		LOG.info("process resp msg term="+term+"Current term="+LeContext.getInstance().getTerm());
		if (term == LeContext.getInstance().getTerm()) {
			LOG.info("adding to list..");
			respondedlist.add(cl_msg.getSrc());

			int respondedCnt = respondedlist.size();
			int ClusterSize = LeContext.getInstance().getJgrps()
					.getClusterSize();

			LOG.info("adding to list. respondedCnt="+respondedCnt+"ClusterSize="+ClusterSize);
			if ((respondedCnt + 1) * 2 > ClusterSize) {
				LOG.info("Reached success point...");
				// consensus reached
				LeContext.getInstance().changeState(new Leader());
			}
		}
	}

	private void processVoteReq(clusterMsg cl_msg) {
		int term = Integer.valueOf(cl_msg.getData());
		LOG.info("Request term="+term);
		if (term > LeContext.getInstance().getTerm()) {
			// subscribe to that election
			LeContext.getInstance().setTerm(term);
			msgUtil.sendVoteRespMsg(cl_msg.getSrc());
			LeContext.getInstance().setTriggeredElection(false);
			LeContext.getInstance().changeState(new Candidate());
		} else {
			// neglect requests,log
			if(!triggeredElection && term == LeContext.getInstance().getTerm()) {
				msgUtil.sendVoteRespMsg(cl_msg.getSrc());
			}
		}
	}

	private void processHeartBeat(clusterMsg cl_msg) {
		int term = Integer.valueOf(cl_msg.getData());
		if (term > LeContext.getInstance().getTerm()) {
			// found a leader
			LeContext.getInstance().setTerm(term);
			LeContext.getInstance().changeState(new Follower());
		} else if (term == LeContext.getInstance().getTerm()
				&& !triggeredElection) {
			LeContext.getInstance().changeState(new Follower());
		}
	}

	private void startElection() {
		LOG.info("Election started..");
		currentElectionCnt++;
		if (currentElectionCnt > raftProperties.getInstance().getMaxelectionlimit()) {
			// stop the process as consensus can not be reached,log error
			

		}
		electionInProgress = true;
		msgUtil.sendVoteReqMsg();
		electionTimer = new Timer(true);
		int electionTimeOut = getElcetionTimeout();
		electionTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				LOG.info("Election Timeout task Entered");
				if (electionInProgress) {
					// re-start election
					restartElection();
				}

			}
		}, electionTimeOut*1000);
	}

	private int getElcetionTimeout() {
		Random rand = new Random();
		int temp = rand.nextInt(raftProperties.getInstance().getMaxelectionlimit() - raftProperties.getInstance().getMinelectionTimeOut() + 1);		
		return temp+raftProperties.getInstance().getMinelectionTimeOut();
	}

	public void stopElection() {
		LOG.info("Election stopped");
		electionInProgress = false;
		electionTimer.cancel();
	}

	private void restartElection() {
		stopElection();
		startElection();
	}

	@Override
	public String getName() {
		return "Candidate";
	}

}
