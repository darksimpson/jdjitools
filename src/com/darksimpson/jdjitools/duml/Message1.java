package com.darksimpson.jdjitools.duml;

/**
 * Class to hold DUML message version 1 meaningful information (without CRCs at least) and
 * handle some helper things like setting/getting known flags.
 */
public class Message1 {
	private byte messageSource;
	private byte messageTarget;
	private byte[] messageSequence;
	private byte messageFlags;
	private byte messageCommandSet;
	private byte messageCommandNum;
	private byte[] messageData;

	public Message1() {
		// Init as completely empty message
		messageSequence = new byte[2];
		messageData = new byte[0];
	}

	public byte getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(byte messageSource) {
		this.messageSource = messageSource;
	}

	public byte getMessageTarget() {
		return messageTarget;
	}

	public void setMessageTarget(byte messageTarget) {
		this.messageTarget = messageTarget;
	}

	public byte[] getMessageSequence() {
		return this.messageSequence;
	}

	public void setMessageSequence(byte[] messageSequence) throws DumlException {
		if (messageSequence.length != 2) {
			throw new DumlException("Message sequence length must be exactly two bytes!");
		}
		this.messageSequence = messageSequence;
	}

	public int getMessageSequenceAsInt() {
		return ((this.messageSequence[1] << 8) & 0xFF00) | (this.messageSequence[0] & 0xFF);
	}

	public void setMessageSequenceAsInt(int messageSequence) throws DumlException {
		if (messageSequence < 0 || messageSequence > 65535) {
			throw new DumlException("Message sequence must be > 0 and < 65536!");
		}
		this.messageSequence = new byte[2];
		this.messageSequence[0] = (byte) (messageSequence & 0xFF);
		this.messageSequence[1] = (byte) ((messageSequence & 0xFF00) >> 8);
	}

	public byte getMessageFlags() {
		return messageFlags;
	}

	public void setMessageFlags(byte messageFlags) {
		this.messageFlags = messageFlags;
	}

	public boolean getMessageFlagIsResponse() {
		return ((this.messageFlags & (byte) 0x80) != 0);
	}

	public void setMessageFlagIsResponse(boolean messageFlagIsResponse) {
		if (messageFlagIsResponse) {
			this.messageFlags |= (byte) 0x80;
		} else {
			this.messageFlags &= ~(byte) 0x80;
		}
	}

	public boolean getMessageFlagWantResponse() {
		return ((this.messageFlags & (byte) 0x40) != 0);
	}

	public void setMessageFlagWantResponse(boolean messageFlagWantResponse) {
		if (messageFlagWantResponse) {
			this.messageFlags |= (byte) 0x40;
		} else {
			this.messageFlags &= ~(byte) 0x40;
		}
	}

	public byte getMessageCommandSet() {
		return messageCommandSet;
	}

	public void setMessageCommandSet(byte messageCommandSet) {
		this.messageCommandSet = messageCommandSet;
	}

	public byte getMessageCommandNum() {
		return messageCommandNum;
	}

	public void setMessageCommandNum(byte messageCommandNum) {
		this.messageCommandNum = messageCommandNum;
	}

	public byte[] getMessageData() {
		return messageData;
	}

	public void setMessageData(byte[] messageData) {
		this.messageData = messageData;
	}
}
