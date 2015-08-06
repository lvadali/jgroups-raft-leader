package com.cisco.raft;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class raftProperties {
	
	private static final Logger logger = Logger.getLogger(raftProperties.class);
	
	private static String propFileName="raft.properties";
	private static String jgroups_xml="jgroups.xml";
	
	private static String propFilePath=null;
	
	private int heartbeatFrequency;
	private int heartbeatTimeout;	
	private int minelectionTimeOut;
	private int maxelectionTimeOut;
	private int maxelectionlimit;
	private String channel_name;
	
    public String getChannel_name() {
		return channel_name;
	}

	public void setChannel_name(String channel_name) {
		this.channel_name = channel_name;
	}

	private static raftProperties singletonObject = null;

    public static raftProperties getInstance() {
        if (singletonObject == null) {
            singletonObject = new raftProperties();
        }
        return singletonObject;
    }
	
    private raftProperties(){
        // Initialize your object.;
		if(getPropFilePath() == null) {
			setPropFilePath(getPropBase()+"/"+propFileName);
		}
		logger.info("Properties File="+getPropFilePath());
    	loadProperties(false);
    }
    
	private void loadProperties(boolean reload) {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(getPropFilePath());
			
			// load a properties file
			prop.load(input);
			
			heartbeatFrequency = Integer.parseInt(prop.getProperty("heartbeatFrequency","10"));
			heartbeatTimeout = Integer.parseInt(prop.getProperty("heartbeatTimeout","30"));
			minelectionTimeOut = Integer.parseInt(prop.getProperty("minelectionTimeOut","45"));
			maxelectionTimeOut = Integer.parseInt(prop.getProperty("maxelectionTimeOut","60"));
			maxelectionlimit = Integer.parseInt(prop.getProperty("maxelectionlimit","3"));
			channel_name = prop.getProperty("channel_name","master-cluster");
			
			logger.info("heartbeatFrequency="+heartbeatFrequency);
			logger.info("heartbeatTimeout="+heartbeatTimeout);
			logger.info("minelectionTimeOut="+minelectionTimeOut);
			logger.info("maxelectionTimeOut"+maxelectionTimeOut);
			logger.info("maxelectionlimit="+maxelectionlimit);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getPropBase()
	{
		return System.getProperty("le_path");
	}

	public static String getPropFilePath() {
		return propFilePath;
	}

	public static void setPropFilePath(String propFilePath) {
		raftProperties.propFilePath = propFilePath;
	}
	
	
	public int getHeartbeatFrequency() {
		return heartbeatFrequency;
	}

	public void setHeartbeatFrequency(int heartbeatFrequency) {
		this.heartbeatFrequency = heartbeatFrequency;
	}

	public int getHeartbeatTimeout() {
		return heartbeatTimeout;
	}

	public void setHeartbeatTimeout(int heartbeatTimeout) {
		this.heartbeatTimeout = heartbeatTimeout;
	}

	public int getMinelectionTimeOut() {
		return minelectionTimeOut;
	}

	public void setMinelectionTimeOut(int minelectionTimeOut) {
		this.minelectionTimeOut = minelectionTimeOut;
	}

	public int getMaxelectionTimeOut() {
		return maxelectionTimeOut;
	}

	public void setMaxelectionTimeOut(int maxelectionTimeOut) {
		this.maxelectionTimeOut = maxelectionTimeOut;
	}

	public int getMaxelectionlimit() {
		return maxelectionlimit;
	}

	public void setMaxelectionlimit(int maxelectionlimit) {
		this.maxelectionlimit = maxelectionlimit;
	}
	
	public String getjgroups_xml()
	{
		return getPropBase()+"/"+jgroups_xml;
	}
	
}
