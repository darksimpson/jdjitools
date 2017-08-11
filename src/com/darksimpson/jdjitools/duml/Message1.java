package com.darksimpson.jdjitools.duml;

public class Message1 {
	private int messageSource;
	private int messageTarget;
	private int messageSequence;
	private boolean messageFlagIsResponse;
	private boolean messageFlagWantResponse;
	//private boolean messageFlagError;
	private int messageCommandSet;
	private int messageCommandNum;
	private byte[] messageData;

	public Message1() {
	}

	public Message1(int messageSource, int messageTarget, int messageSequence, boolean messageFlagIsResponse, boolean messageFlagWantResponse, int messageCommandSet, int messageCommandNum, byte[] messageData) {
		this.messageSource = messageSource;
		this.messageTarget = messageTarget;
		this.messageSequence = messageSequence;
		this.messageFlagIsResponse = messageFlagIsResponse;
		this.messageFlagWantResponse = messageFlagWantResponse;
		this.messageCommandSet = messageCommandSet;
		this.messageCommandNum = messageCommandNum;
		this.messageData = messageData;
	}

	public int getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(int messageSource) {
		this.messageSource = messageSource;
	}

	public int getMessageTarget() {
		return messageTarget;
	}

	public void setMessageTarget(int messageTarget) {
		this.messageTarget = messageTarget;
	}

	public int getMessageSequence() {
		return messageSequence;
	}

	public void setMessageSequence(int messageSequence) {
		this.messageSequence = messageSequence;
	}

	public boolean getMessageFlagIsResponse() {
		return messageFlagIsResponse;
	}

	public void setMessageFlagIsResponse(boolean messageFlagIsResponse) {
		this.messageFlagIsResponse = messageFlagIsResponse;
	}

	public boolean getMessageFlagWantResponse() {
		return messageFlagWantResponse;
	}

	public void setMessageFlagWantResponse(boolean messageFlagWantResponse) {
		this.messageFlagWantResponse = messageFlagWantResponse;
	}

	public int getMessageCommandSet() {
		return messageCommandSet;
	}

	public void setMessageCommandSet(int messageCommandSet) {
		this.messageCommandSet = messageCommandSet;
	}

	public int getMessageCommandNum() {
		return messageCommandNum;
	}

	public void setMessageCommandNum(int messageCommandNum) {
		this.messageCommandNum = messageCommandNum;
	}

	public byte[] getMessageData() {
		return messageData;
	}

	public void setMessageData(byte[] messageData) {
		this.messageData = messageData;
	}

	// Debug

	private int debugMessageFlagsByte;

	public int debugGetMessageFlagsByte() {
		return debugMessageFlagsByte;
	}

	public void debugSetMessageFlagsByte(int messageFlags) {
		this.debugMessageFlagsByte = messageFlags;
	}
}
