package com.darksimpson.jdjitools;

import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.Shell;
import asg.cliche.ShellFactory;
import com.darksimpson.jdjitools.duml.*;
import com.darksimpson.jdjitools.primitives.DecryptFTPFile;
import com.darksimpson.jdjitools.primitives.DjiDeriveKey;
import com.fazecast.jSerialComm.SerialPort;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class CommandShell {
	private static final String JDJITOOLS_VERSION = "1.0";
	private static final String JDJITOOLS_COPYRIGHT = "Dark Simpson, 2017";
	private static final String JDJITOOLS_BANNER =
		"       _ ____      ________              __    \n" +
		"      (_) __ \\    / /  _/ /_____  ____  / /____\n" +
		"     / / / / /_  / // // __/ __ \\/ __ \\/ / ___/\n" +
		"    / / /_/ / /_/ // // /_/ /_/ / /_/ / (__  ) \n" +
		" __/ /_____/\\____/___/\\__/\\____/\\____/_/____/  \n" +
		"/___/ ver. " + JDJITOOLS_VERSION + ", (c) " + JDJITOOLS_COPYRIGHT + "\n";

	private Shell thisShell;

	private static final long RESP_WAIT_TIMEOUT_MS = 10000;

	public static void main(String[] args) {
		try {
			CommandShell cs = new CommandShell();
			cs.thisShell = ShellFactory.createConsoleShell("jdt", "Java DJI Tools", cs);
			//cs.thisShell.redirectErrToOut(true);
			cs.thisShell.outputSimple(JDJITOOLS_BANNER);
			cs.thisShell.outputSimple("Type \"?help\" if you are new to this tool\n");
			cs.thisShell.commandLoop();
		} catch (IOException e) {
			System.err.println("IO exception during command shell session");
			e.printStackTrace();
		}
	}

	@Command(description="Show information about Java DJI Tools", abbrev="ab")
	public String cliAbout() {
		return
			"Java DJI Tools version " + JDJITOOLS_VERSION + "\n" +
			"Copyright (c) " + JDJITOOLS_COPYRIGHT + "\n\n" +
			"Great thanks to all the folks digging deep into DJI stuff aka\n" +
			"#DeejayeyeHackingClub / \"The OG's\" (Original Gangsters),\n" +
			"especially: @freaky123, @hostile, @the_lord, @jezzab and others...";
	}

	@Command(name="util-dji-derive-key", abbrev="uddk", description="DJI primitive for ciphering/scrambling/authentication key derivation (variable derived key length)")
	public String cliUtilDjiDeriveKey(@Param(name="src-key", description="Source key (32 symbols hexadecimal string)") String srcKey,
														 @Param(name="src-param", description="Source parameter (any length string)") String srcParam,
														 @Param(name="out-key-len", description="Wanted output (derived) key length (from 1 to 32)") Integer outKeyLen) throws JDTException {
		byte[] keyBytes;

		try {
			keyBytes = Utils.hexStringToBytes(srcKey);
		} catch (Exception e) {
			throw new JDTException("Error converting key " + srcKey + " from hexadecimal string to binary: " + e.getMessage());
		}

		DjiDeriveKey dk = new DjiDeriveKey();

		thisShell.outputSimple("Derived key is:");

		return Utils.bytesToHexString(dk.deriveKey(keyBytes, srcParam, outKeyLen));
	}

	@Command(name="util-dji-derive-key", abbrev="uddk", description="DJI primitive for ciphering/scrambling/authentication key derivation (derived key length of 16 bytes)")
	public String cliUtilDjiDeriveKey(@Param(name="src-key", description="Source key (32 symbols hexadecimal string)") String srcKey,
														 @Param(name="src-param", description="Source parameter (any length string)") String srcParam) throws JDTException {
		return cliUtilDjiDeriveKey(srcKey, srcParam, 16);
	}

	@Command(name="util-decrypt-ftp-file", abbrev="udff", description="Decrypt file downloaded from DJI device on-board FTP daemon (different input and output files)")
	public void cliUtilDecryptFTPFile(@Param(name="key-number", description="Key number to decrypt data (can be 1 or 2)") int keyNumber,
																		@Param(name="in-file-name", description="Encrypted (input) file name to read data from") String inFileName,
																    @Param(name="out-file-name", description="Decrypted (output) file name to write data to") String outFileName) throws JDTException {
		thisShell.outputSimple("Decrypting file '" + inFileName + "'...");

		DecryptFTPFile df = new DecryptFTPFile();

		df.decryptFTPFile(keyNumber, new File(inFileName), new File(outFileName));

		thisShell.outputSimple("Done decrypting to '" + outFileName + "'");
	}

	@Command(name="util-decrypt-ftp-file", abbrev="udff", description="Decrypt file downloaded from DJI device on-board FTP daemon (same input and output file)")
	public void cliUtilDecryptFTPFile(@Param(name="key-number", description="Key number to decrypt data (can be 1 or 2)") int keyNumber,
																		@Param(name="file-name", description="Encrypted file name, contents will be overwritten with decrypted data") String fileName) throws JDTException {
		cliUtilDecryptFTPFile(keyNumber, fileName, fileName);
	}

	@Command(name="serial-list-serial-ports", abbrev="slsp", description="List serial ports available in your system")
	public void cliSerialListSerialPorts() throws JDTException {
		SerialPort[] ports = SerialPort.getCommPorts();

		if (ports.length > 0) {
			thisShell.outputSimple("Name, Description");
			thisShell.outputSimple("-----------------");

			for (SerialPort port : ports) {
				thisShell.outputSimple(port.getSystemPortName() + ", " + Utils.singleByteStringToString(port.getDescriptivePortName()));
			}
		} else {
			thisShell.outputSimple("No serial ports found!");
		}
	}

	@Command(name="duml-monitor-serial-port", abbrev="dmsp", description="Monitor, decode and print all DUML packets goes to serial port (output to file)")
	public void cliDumlMonitorSerialPort(@Param(name="in-serial-name", description="Input serial port name") String inSerialName,
														           @Param(name="out-file-name", description="Output text file name") String outFileName) throws JDTException {
		TextOutputter out;

		// Create file or shell outputter
		out = TextOutputter.createOutputter(thisShell, outFileName);

		SerialPort port = SerialPort.getCommPort(inSerialName);

		// Try to open port firstly
		if (port.openPort()) {
			thisShell.outputSimple("Monitoring incoming DUML on serial port...");

			// Start new DUML monitoring thread
			class DumlMonitorThread extends Thread {
				public void run() {
					Decoder1 decoder1 = new Decoder1();
					Printer1 printer1 = new Printer1();

					try {
						while (!this.isInterrupted())
						{
							// Sleep this thread till some bytes will be available on input serial input
							while (port.bytesAvailable() == 0) {
								try {
									Thread.sleep(20);
								} catch (InterruptedException e) {
									thisShell.outputSimple("Monitoring was interrupted");
									return; // We need to exit from this thread worker explicitly!
								}
							}

							// Read input data
							byte[] readBuffer = new byte[port.bytesAvailable()];
							int numRead = port.readBytes(readBuffer, readBuffer.length);

							// Enqueue data to Decoder1 (and implicitly try to decode it)
							decoder1.enqueueBytes(readBuffer, numRead);

							// Print out some messages if it was decoded and enqueued
							while (decoder1.getMessagesCount() > 0) {
								out.outputWriteLine(printer1.printDumlMessage(decoder1.getMessage()));
							}
						}
					} catch (Exception e) {
						thisShell.outputSimple("Exception while monitoring DUML: " + e.getMessage());
						//e.printStackTrace();
					}
				}
			}
			DumlMonitorThread monitorThread = new DumlMonitorThread();
			monitorThread.setName("DUML monitor/decode thread");
			monitorThread.start();

			// Pause this thread (wait for input) if monitoring from serial port
			thisShell.inputSimple("Press return to stop ");
			thisShell.outputSimple("");

			// Interrupt and kill monitoring thread if monitoring from serial port
			monitorThread.interrupt();

			// Wait till thread finishes its work
			try {
				monitorThread.join();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Close serial port
			port.closePort();
		} else {
			thisShell.outputSimple("Can't open specified serial port!");
		}

		// Close outputter
		out.outputFinish();
	}

	@Command(name="duml-monitor-serial-port", abbrev="dmsp", description="Monitor, decode and print all DUML packets goes to serial port (output to console)")
	public void cliDumlMonitorSerialPort(@Param(name="in-serial-name", description="Input serial port name") String inSerialName) throws JDTException {
		cliDumlMonitorSerialPort(inSerialName, null);
	}

	@Command(name="duml-decode-binary-file", abbrev="ddbf", description="Read, decode and print all DUML packets read from binary file (output to file)")
	public void cliDumlDecodeBinaryFile(@Param(name="in-file-name", description="Input binary file name") String inFileName,
														          @Param(name="out-file-name", description="Output text file name") String outFileName) throws JDTException {
		TextOutputter out;

		// Create file or shell outputter
		out = TextOutputter.createOutputter(thisShell, outFileName);

		FileInputStream fileInputStream;

		File inputFile = new File(inFileName);
		try {
			fileInputStream = new FileInputStream(inputFile);
		} catch (Exception e) {
			throw new JDTException("Can't open input file '" + inputFile.getAbsolutePath() +"'!");
		}

		thisShell.outputSimple("Decoding incoming DUML from file...");

		Decoder1 decoder1 = new Decoder1();
		Printer1 printer1 = new Printer1();

		try {
			while (true)
			{
				// Check that there is still some data to read from input file
				int availToRead;
				try {
					availToRead = (fileInputStream.available() >= 1024) ? 1024 : fileInputStream.available();
				} catch (Exception e) {
					throw new JDTException("Error getting available bytes to read from file!");
				}
				if (availToRead == 0) {
					thisShell.outputSimple("End of input file reached");
					return;
				}

				// Read input data
				byte[] readBuffer = new byte[availToRead];
				int numRead;
				try {
					numRead = fileInputStream.read(readBuffer);
				} catch (IOException e) {
					throw new JDTException("Error reading data from file!");
				}

				// Enqueue data to Decoder1 (and implicitly try to decode it)
				decoder1.enqueueBytes(readBuffer, numRead);

				// Print out some messages if it was decoded and enqueued
				while (decoder1.getMessagesCount() > 0) {
					out.outputWriteLine(printer1.printDumlMessage(decoder1.getMessage()));
				}
			}
		} catch (Exception e) {
			thisShell.outputSimple("Exception while decoding DUML: " + e.getMessage());
			//e.printStackTrace();
		}

		// Close input file
		try {
			fileInputStream.close();
		} catch (Exception e) {
			// Do nothing
		}

		// Close outputter
		out.outputFinish();;
	}

	@Command(name="duml-decode-binary-file", abbrev="ddbf", description="Read, decode and print all DUML packets read from binary file (output to console)")
	public void cliDumlDecodeBinaryFile(@Param(name="in-file-name", description="Input binary file name") String inFileName) throws JDTException {
		cliDumlDecodeBinaryFile(inFileName, null);
	}

	@Command(name="duml-send-arbitrary-message", abbrev="dsam", description="Send any DUML message to device and (if you need) wait/decode response")
	public void cliDumlSendArbitraryMessage(@Param(name="serial-name", description="Serial port name") String serialName,
																					@Param(name="duml-msg-src", description="DUML message source (hex starting with '0x' or integer)") String dumlMsgSrc,
																					@Param(name="duml-msg-tgt", description="DUML message target (hex starting with '0x' or integer)") String dumlMsgTgt,
																					@Param(name="duml-msg-seq", description="DUML message sequence (hex starting with '0x' or integer)") String dumlMsgSeq,
																					@Param(name="duml-msg-want-rsp", description="DUML message 'want response' (WACK) flag ('true' if you want response, 'false' otherwise)") boolean dumlMsgWantRsp,
																					@Param(name="duml-msg-cmd-set", description="DUML message command set (hex starting with '0x' or integer)") String dumlMsgCmdSet,
																					@Param(name="duml-msg-cmd-num", description="DUML message command number (hex starting with '0x' or integer)") String dumlMsgCmdNum,
																					@Param(name="duml-msg-data", description="DUML message data (hex starting with '0x' or string)") String... dumlMsgData) throws JDTException {
		byte[] dumlMsgSrcB = Utils.getBytesFromHexOrIntString(dumlMsgSrc, 255);
		byte[] dumlMsgTgtB = Utils.getBytesFromHexOrIntString(dumlMsgTgt, 255);
		byte[] dumlMsgSeqB = Utils.getBytesFromHexOrIntString(dumlMsgSeq, 65535);
		byte[] dumlMsgCmdSetB = Utils.getBytesFromHexOrIntString(dumlMsgCmdSet, 255);
		byte[] dumlMsgCmdNumB = Utils.getBytesFromHexOrIntString(dumlMsgCmdNum, 255);
		byte[] dumlMsgDataB = new byte[0];

		if (dumlMsgData != null) {
			ByteArrayBuilder bab = new ByteArrayBuilder();
			for (String entry: dumlMsgData) {
				bab.append(Utils.getBytesFromHexOrString(entry));
			}
			dumlMsgDataB = bab.toBytes();
		}

		// Now form and fill new message
		Message1 outMessage = new Message1();
		outMessage.setMessageSource(dumlMsgSrcB[0]);
		outMessage.setMessageTarget(dumlMsgTgtB[0]);
		try {
			outMessage.setMessageSequence(dumlMsgSeqB);
		} catch (DumlException e) {
			throw new JDTException();
		}
		outMessage.setMessageFlagWantResponse(dumlMsgWantRsp);
		outMessage.setMessageCommandSet(dumlMsgCmdSetB[0]);
		outMessage.setMessageCommandNum(dumlMsgCmdNumB[0]);
		outMessage.setMessageData(dumlMsgDataB);

		Encoder1 encoder1 = new Encoder1();
		Decoder1 decoder1 = new Decoder1();
		Printer1 printer1 = new Printer1();

		// Encode message
		byte[] messageBytes = encoder1.encodeMessage(outMessage);

		// Print output message
		try {
			decoder1.enqueueBytes(messageBytes, messageBytes.length);
			Message1 decMessage = decoder1.getMessage();
			thisShell.outputSimple("Your message is:");
			thisShell.outputSimple(printer1.printDumlMessage(decMessage));
		} catch (DumlException e) {
			thisShell.outputSimple("Exception while decoding formed (!) DUML: " + e.getMessage());
			return;
		}

		// Open port and send output message
		SerialPort port = SerialPort.getCommPort(serialName);

		// Try to open port firstly
		thisShell.outputSimple("Opening port...");
		if (port.openPort()) {

			// Send message
			thisShell.outputSimple("Port is opened, sending...");
			port.writeBytes(messageBytes, messageBytes.length);
			thisShell.outputSimple("Message sent");

			// Wait for response if needed
			if (outMessage.getMessageFlagWantResponse()) {
				thisShell.outputSimple("Waiting for response...");

				long startMillis = System.currentTimeMillis();
				boolean gotAnswer = false;

				while (!gotAnswer) {
					if (System.currentTimeMillis() > startMillis + RESP_WAIT_TIMEOUT_MS) {
						thisShell.outputSimple("Timeout while waiting for response!");
						break;
					}

					// If there are some bytes available, process it
					if (port.bytesAvailable() > 0) {
						// Read input data
						byte[] readBuffer = new byte[port.bytesAvailable()];
						int numRead = port.readBytes(readBuffer, readBuffer.length);

						try {
							// Enqueue data to Decoder1 (and implicitly try to decode it)
							decoder1.enqueueBytes(readBuffer, numRead);

							// Print out some messages if it was decoded and enqueued
							while (decoder1.getMessagesCount() > 0) {
								Message1 inMessage = decoder1.getMessage();

								// Check if it is our response
								if (Arrays.equals(inMessage.getMessageSequence(), outMessage.getMessageSequence()) &&
									  inMessage.getMessageSource() == outMessage.getMessageTarget() &&
									  inMessage.getMessageTarget() == outMessage.getMessageSource() &&
									  inMessage.getMessageFlagIsResponse()) {
									thisShell.outputSimple("Got response message:");
									thisShell.outputSimple(printer1.printDumlMessage(inMessage));
									gotAnswer = true;
									break; // Get out from current loop
								}
							}
						} catch (DumlException e) {
							// Some exception decoding incoming messages, do nothing ???
						}
					} else {
						// If bytes not available, just sleep some time
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// Sleep was interrupted, do nothing
						}
					}
				}
			}

			// Close port
			port.closePort();
		} else {
			thisShell.outputSimple("Can't open specified serial port!");
		}
	}

	@Command(name="duml-send-arbitrary-message", abbrev="dsam", description="Send any DUML message to device and (if you need) wait/decode response")
	public void cliDumlSendArbitraryMessage(@Param(name="serial-name", description="Serial port name (hex starting with '0x' or integer)") String serialName,
																					@Param(name="duml-msg-src", description="DUML message source (hex starting with '0x' or integer)") String dumlMsgSrc,
																					@Param(name="duml-msg-tgt", description="DUML message target (hex starting with '0x' or integer)") String dumlMsgTgt,
																					@Param(name="duml-msg-seq", description="DUML message sequence (hex starting with '0x' or integer)") String dumlMsgSeq,
																					@Param(name="duml-msg-want-rsp", description="DUML message 'want response' (WACK) flag ('true' if you want response, 'false' otherwise)") boolean dumlMsgWantRsp,
																					@Param(name="duml-msg-cmd-set", description="DUML message command set (hex starting with '0x' or integer)") String dumlMsgCmdSet,
																					@Param(name="duml-msg-cmd-num", description="DUML message command number (hex starting with '0x' or integer)") String dumlMsgCmdNum) throws JDTException {
		cliDumlSendArbitraryMessage(serialName, dumlMsgSrc, dumlMsgTgt, dumlMsgSeq, dumlMsgWantRsp, dumlMsgCmdSet, dumlMsgCmdNum, null);
	}
}
