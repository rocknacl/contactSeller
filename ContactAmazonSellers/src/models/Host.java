package models;

import java.io.Serializable;

import helpers.LocalhostInfo;

public class Host implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String hostName;
	private String ip;

	public Host() {
		hostName = LocalhostInfo.getLocalHostName();
		ip = LocalhostInfo.getLocalHostIP();
	}

	public String getHostName() {
		return hostName;
	}

	public String getIp() {
		return ip;
	}
}
