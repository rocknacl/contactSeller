package models;

import sys.SystemConfig;

public class ContactSellerTask {
	String taskDataFolder;
	String messageFilePath;
	String[] attachmentFileNames;
	Amazon_Country country;
	String name;

	public ContactSellerTask(String name,String taskDataFolder, String messageFilePath, String[] attachmentFileNames,
			Amazon_Country country) {
		super();
		this.name = name;
		this.taskDataFolder = SystemConfig.SystemContext+taskDataFolder;
		this.messageFilePath = messageFilePath;
		this.attachmentFileNames = attachmentFileNames;
		this.country = country;
	}


	public String getMessageFilePath() {
		return messageFilePath;
	}

	public String getTaskDataFolder() {
		return taskDataFolder;
	}

	public String[] getAttachmentFileNames() {
		return attachmentFileNames;
	}


	public Amazon_Country getCountry() {
		return country;
	}


	public String getName() {
		return name;
	}
}
