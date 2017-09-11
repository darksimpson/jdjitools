package com.darksimpson.jdjitools.duml;

import java.nio.charset.Charset;

public class Printer1 {
	private String getModuleNameFromAddress(byte address) {
		switch (address) {
			case (byte) 0x01:
				return "Camera";
			case (byte) 0x02:
				return "Application";
			case (byte) 0x03:
				return "Flight controller";
			case (byte) 0x04:
				return "Gimbal";
			case (byte) 0x05:
				return "Center board";
			case (byte) 0x06:
				return "Remote controller";
			case (byte) 0x07:
				return "Aircraft Wi-Fi";
			case (byte) 0x08:
				return "Aircraft Linux";
			case (byte) 0x09:
				return "Aircraft 1765";
			case (byte) 0x0A:
				return "PC";
			case (byte) 0x0B:
				return "Smart battery";
			case (byte) 0x0C:
				return "ESC";
			case (byte) 0x0D:
				return "Ground Linux";
			case (byte) 0x0E:
				return "Ground 1765";
			case (byte) 0x0F:
				return "Aircraft 68013";
			case (byte) 0x10:
				return "Ground 68013";
			case (byte) 0x11:
				return "Monocular vision";
			case (byte) 0x12:
				return "Binocular vision";
			case (byte) 0x13:
				return "Aircraft FPGA";
			case (byte) 0x14:
				return "Ground FPGA";
			case (byte) 0x15:
				return "Simulator";
			case (byte) 0x16:
				return "D station";
			case (byte) 0x17:
				return "Air compt";
			case (byte) 0x18:
				return "GPS doc";
			case (byte) 0x19:
				return "GPS";
			case (byte) 0x1A:
				return "IMU";
			case (byte) 0x1B:
				return "Ground Wi-Fi";
			case (byte) 0x1C:
				return "Glass";
			case (byte) 0x1D:
				return "Blackbox";
			case (byte) 0x1E:
				return "Test";
			case (byte) 0x1F:
				return "(Broadcast)";
			case (byte) 0x28:
				return "Aircraft Linux";
			case (byte) 0x2A:
				return "PC";
			default:
				return "(unknown)";
		}
	}

	public String printDumlMessage(Message1 message) {
		// Format message
		String messageStr = String.format("Src: 0x%02X, Tgt: 0x%02X, Seq: 0x%04X, Flags: 0x%02X [%s %s], CmdSet: 0x%02X, CmdNum: 0x%02X\n",
			message.getMessageSource(), message.getMessageTarget(), message.getMessageSequenceAsInt(), message.getMessageFlags(),
			(message.getMessageFlagIsResponse() ? "RESP," : "REQ, "),
			(message.getMessageFlagWantResponse() ? "WACK " : "DWACK"),
			message.getMessageCommandSet(), message.getMessageCommandNum());

		messageStr += "Source: " + getModuleNameFromAddress(message.getMessageSource()) + "\n";
		messageStr += "Target: " + getModuleNameFromAddress(message.getMessageTarget()) + "\n";

		if (message.getMessageData().length > 0) {
			// Print data hex
			messageStr += "Data hex: 0x" + com.darksimpson.jdjitools.Utils.bytesToHexString(message.getMessageData()) + "\n";
			// Print data as string also
			messageStr += "Data text: " + new String(message.getMessageData(), Charset.defaultCharset()) + "\n";
		}

		// TODO: Run through additional beautifier/decoder if needed

		return messageStr;
	}
}
