package com.darksimpson.jdjitools.duml;

public class Encoder1 {
	public byte[] encodeMessage(Message1 message) {
		byte[] outCmdBuf = new byte[message.getMessageData().length + 13];

		// Preamble
		outCmdBuf[0] = (byte) 0x55;
		// Length
		outCmdBuf[1] = (byte) (outCmdBuf.length & 0xFF);
		outCmdBuf[2] = (byte) (outCmdBuf.length >> 8 & 0x03);
		// Version
		outCmdBuf[2] |= (byte) 0x04;
		// Header CRC
		outCmdBuf[3] = Utils.calcDjiCrc8(outCmdBuf, 3);
		// Source
		outCmdBuf[4] = message.getMessageSource();
		// Target
		outCmdBuf[5] = message.getMessageTarget();
		// Sequence
		outCmdBuf[6] = message.getMessageSequence()[0];
		outCmdBuf[7] = message.getMessageSequence()[1];
		// Flags
		outCmdBuf[8] = message.getMessageFlags();
		// Command set
		outCmdBuf[9] = message.getMessageCommandSet();
		// Command num
		outCmdBuf[10] = message.getMessageCommandNum();
		// Data
		System.arraycopy(message.getMessageData(), 0, outCmdBuf, 11, message.getMessageData().length);
		// Message CRC
		int crc16 = Utils.calcDjiCrc16(outCmdBuf, outCmdBuf.length - 2);
		outCmdBuf[outCmdBuf.length - 2] = (byte) (crc16 & 0xFF);
		outCmdBuf[outCmdBuf.length - 1] = (byte) ((crc16 & 0xFF00) >> 8);

		return outCmdBuf;
	}
}
