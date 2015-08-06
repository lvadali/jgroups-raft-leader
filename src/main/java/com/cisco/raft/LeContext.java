package com.cisco.raft;

import org.apache.log4j.Logger;

public class LeContext {
	private static Logger LOG = Logger.getLogger(LeContext.class);
	private static LeContext instance = null;

	private State current_state = null;
	private jgroupsadapter jgrps = null;
	private int term = 1;
	private boolean triggeredElection = false;// did we trigger election?

	public static LeContext getInstance() {
		if (instance == null) {
			instance = new LeContext();
		}
		return instance;
	}

	private LeContext() {
		init();
	}

	private void init() {
		LOG.info("Context Started");
		jgrps = new jgroupsadapter(this);
		changeState(new Follower());
	}

	public State getCurrent_state() {
		return current_state;
	}

	public void setCurrent_state(State current_state) {
		this.current_state = current_state;
	}

	public jgroupsadapter getJgrps() {
		return jgrps;
	}

	public void changeState(State state) {
		if (current_state != null && state != null)
			LOG.info("from=" + current_state.getName() + " state to="
					+ state.getName());
		else if (current_state == null)
			LOG.info("from=null state to=" + state.getName());

		if (current_state != null)
			current_state.end();
		state.begin();
		current_state = state;
	}

	public int getTerm() {
		return term;
	}

	public int incrementTerm() {
		term++;
		return term;
	}

	public void setTerm(int term) {
		this.term = term;
	}

	public boolean isTriggeredElection() {
		return triggeredElection;
	}

	public void setTriggeredElection(boolean triggeredElection) {
		this.triggeredElection = triggeredElection;
	}

	public static void main(String[] args) {
		LeContext.getInstance();
		while(true)
		{
			//sleep for process to run
			LOG.info("sleeping");
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
