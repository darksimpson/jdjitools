package com.darksimpson.jdjitools;

import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.Shell;
import asg.cliche.ShellFactory;
import com.darksimpson.jdjitools.duml.Decoder1;
import com.darksimpson.jdjitools.duml.Message1;
import com.darksimpson.jdjitools.primitives.DecryptFTPFile;
import com.darksimpson.jdjitools.primitives.DeriveKey;
import com.fazecast.jSerialComm.SerialPort;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

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
			"especially: @freaky123, @hostile, @the_lord and many others...";
	}

	@Command(name="derive-key", abbrev="dk", description="DJI primitive for ciphering/scrambling/authentication key derivation (with variable derived key length)")
	public String cliDeriveKey(@Param(name="src-key", description="Source key (32 symbols hexadecimal string)") String srcKey,
														 @Param(name="src-param", description="Source parameter (any length string)") String srcParam,
														 @Param(name="out-key-len", description="Wanted output (derived) key length (from 1 to 32)") Integer outKeyLen) throws JDTException {
		byte[] keyBytes;

		try {
			keyBytes = Utils.hexStringToBytes(srcKey);
		} catch (Exception e) {
			throw new JDTException("Error converting key " + srcKey + " from hexadecimal string to binary: " + e.getMessage());
		}

		DeriveKey dk = new DeriveKey();

		thisShell.outputSimple("Derived key is:");

		return Utils.bytesToHexString(dk.deriveKey(keyBytes, srcParam, outKeyLen));
	}

	@Command(name="derive-key", abbrev="dk", description="DJI primitive for ciphering/scrambling/authentication key derivation (with default derived key length of 16 bytes)")
	public String cliDeriveKey(@Param(name="src-key", description="Source key (32 symbols hexadecimal string)") String srcKey,
														 @Param(name="src-param", description="Source parameter (any length string)") String srcParam) throws JDTException {
		return cliDeriveKey(srcKey, srcParam, 16);
	}

	@Command(name="decrypt-ftp-file", abbrev="dff", description="Decrypt file downloaded from DJI device on-board FTP daemon (different input and output files)")
	public void cliDecryptFTPFile(@Param(name="in-file-name", description="Encrypted (input) file name to read data from") String inFileName,
																@Param(name="out-file-name", description="Decrypted (output) file name to write data to") String outFileName) throws JDTException {
		thisShell.outputSimple("Decrypting file '" + inFileName + "'...");

		DecryptFTPFile df = new DecryptFTPFile();

		df.decryptFTPFile(new File(inFileName), new File(outFileName));

		thisShell.outputSimple("Done decrypting to '" + outFileName + "'");
	}

	@Command(name="decrypt-ftp-file", abbrev="dff", description="Decrypt file downloaded from DJI device on-board FTP daemon (same input and output file)")
	public void cliDecryptFTPFile(@Param(name="file-name", description="Encrypted file name, contents will be overwritten with decrypted data") String fileName) throws JDTException {
		cliDecryptFTPFile(fileName, fileName);
	}

	@Command(name="list-serial-ports", abbrev="lsp", description="List serial ports available in your system")
	public void cliListSerialPorts() throws JDTException {
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

	@Command(name="monitor-duml", abbrev="md", description="Decode and print all incoming DUML traffic")
	public void cliMonitorDuml(@Param(name="serial-name", description="Serial port name") String serialName) throws JDTException {
		SerialPort port = SerialPort.getCommPort(serialName);

		// Try to open port firstly
		if (port.openPort()) {
			thisShell.outputSimple("Now monitoring incoming DUML...");

			// Worker DUML monitoring thread class
			class DumlMonitorThread extends Thread {
				public void run() {
					thisShell.outputSimple("");
					Decoder1 decoder1 = new Decoder1();
					try {
						while (!this.isInterrupted())
						{
							// Sleep this thread till some bytes will be available on serial input
							while (port.bytesAvailable() == 0) {
								try {
									Thread.sleep(20);
								} catch (InterruptedException e) {
									thisShell.outputSimple("Monitoring was interrupted");
									return; // We need to exit from this thread worker explicitly!
								}
							}

							// Read serial input data
							byte[] readBuffer = new byte[port.bytesAvailable()];
							int numRead = port.readBytes(readBuffer, readBuffer.length);

							// Enqueue data to Decoder1 (and implicitly try to decode it)
							decoder1.enqueueBytes(readBuffer, numRead);

							// Print out some messages if it was decoded and enqueued
							while (decoder1.getMessagesCount() > 0) {
								Message1 message = decoder1.getMessage();
								String messageStr = String.format("Src: 0x%02X, Tgt: 0x%02X, Seq: 0x%04X, [%s %s], CmdSet: 0x%02X, CmdNum: 0x%02X, Data:\n",
									message.getMessageSource(), message.getMessageTarget(), message.getMessageSequence(),
									(message.getMessageFlagIsResponse() ? "RESP," : "REQ, "),
									(message.getMessageFlagWantResponse() ? "WACK " : "DWACK"),
									message.getMessageCommandSet(), message.getMessageCommandNum());
								messageStr += "0x" + Utils.bytesToHexString(message.getMessageData()) + "\n";
								messageStr += new String(message.getMessageData(), Charset.defaultCharset()) + "\n";
								thisShell.outputSimple(messageStr);
							}
						}
					} catch (Exception e) {
						thisShell.outputSimple("Exception while monitoring DUML: " + e.getMessage());
					}
				}
			}

			// Start new DUML monitoring thread
			DumlMonitorThread monitorThread = new DumlMonitorThread();
			monitorThread.setName("DUML monitor thread");
			monitorThread.start();

			// Pause this thread (wait for input)
			thisShell.inputSimple("Press return to exit monitor ");

			// Interrupt and kill monitoring thread
			monitorThread.interrupt();
			try {
				monitorThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Close serial port
			port.closePort();
		} else {
			thisShell.outputSimple("Can't open specified serial port!");
		}
	}
}
