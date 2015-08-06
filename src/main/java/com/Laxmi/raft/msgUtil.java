package com.cisco.raft;

import org.jgroups.Address;
import org.jgroups.Message;
import org.apache.log4j.Logger;

public class msgUtil {
	private static Logger LOG = Logger.getLogger(msgUtil.class);
	public static void sendHearBeatMsg(Address dest) {
		LOG.info("send heart beat");
		Message tmp_msg = clusterMsg.getMsg(clusterMsg.type.heart_beat,
				String.valueOf(LeContext.getInstance().getTerm()));
		tmp_msg.setDest(dest);	
		try {
			LeContext.getInstance().getJgrps().send(tmp_msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendHearBeatMsg() {
		sendHearBeatMsg(null);
	}

	public static void sendVoteReqMsg() {
		LOG.info("send VoteReq Msg");
		Message tmp_msg = clusterMsg.getMsg(clusterMsg.type.vote_req,
				String.valueOf(LeContext.getInstance().getTerm()));
		try {
			LeContext.getInstance().getJgrps().send(tmp_msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendVoteRespMsg(Address dest) {
		LOG.info("send VoteResp Msg");
		
		Message tmp_msg = clusterMsg.getMsg(clusterMsg.type.vote_resp,
				String.valueOf(LeContext.getInstance().getTerm()));
		tmp_msg.setDest(dest);
		try {
			LeContext.getInstance().getJgrps().send(tmp_msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
