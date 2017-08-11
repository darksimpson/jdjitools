package com.darksimpson.jdjitools.duml;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

public class Decoder1 {
	private ByteBuffer inputBuffer;
	private Queue<Message1> outputMessages;

	private static final int INPUT_BUFFER_SIZE = 65536;

	public Decoder1() {
		inputBuffer = ByteBuffer.allocate(INPUT_BUFFER_SIZE);
		outputMessages = new LinkedList<>();
	}

	public void enqueueBytes(byte[] bytes, int length) throws DumlException {
		if (length > bytes.length) {
			throw new DumlException("Specified length (" + length + ") is more than input data size (" + bytes.length + ")!");
		}
		if (length > inputBuffer.capacity()) {
			throw new DumlException("Trying to write more data (" + length + ") than buffer size (" + inputBuffer.capacity() + ")!");
		}
		if (length > inputBuffer.remaining()) {
			inputBuffer.clear();
		}
		inputBuffer.put(bytes, 0, length);
		inputBuffer.flip();
		processDecode();
		inputBuffer.compact();
	}

	public int getMessagesCount() {
		return outputMessages.size();
	}

	public int getDebugNoBytesAvailForFrameBody() {
		return debugNoBytesAvailForFrameBody;
	}

	public Message1 getMessage() throws DumlException {
		if (!outputMessages.isEmpty()) {
			return outputMessages.remove();
		} else {
			throw new DumlException("No DUML messages available in queue!");
		}
	}

	private void processDecode() {
		int debugIterCnt = 0;

		while (inputBuffer.position() < inputBuffer.limit()) {
			// Fetch next byte from input buffer and check for SOF
			if (inputBuffer.get() == (byte) 0x55) {
				// Probably found a SOF, proceed next if we have needed amount of bytes in input buffer for frame header
				if (inputBuffer.limit() - inputBuffer.position() > 3) {
					debugIncompleteFrameHeaderArm = false;
					// Allocate frame header
					byte[] fh = new byte[4];
					// Hard-write SOF for CRC calculation
					fh[0] = (byte) 0x55;
					// Fetch frame header
					inputBuffer.get(fh, 1, 3);
					// Check that version and CRC8 matches
					if ((fh[2] == (byte) 0x04) && (Utils.calcDjiCrc8(fh, 3) == fh[3])) {
						// Version and CRC8 match, proceed if we have needed amount of bytes in input buffer for rest of frame
						if (inputBuffer.limit() - inputBuffer.position() > fh[1] - 4) {
							debugIncompleteFrameBodyArm = false;
							// Allocate full frame
							byte[] ff = new byte[fh[1]];
							// Copy frame header for CRC calculation
							System.arraycopy(fh, 0, ff, 0, 4);
							// Fetch rest of frame
							inputBuffer.get(ff, 4, fh[1] - 4);
							// Get CRC16 form the end of frame
							int crc16 = ((ff[ff.length-1] << 8) & 0xFF00) | (ff[ff.length-2] & 0xFF);
							// Check that CRC16 match
							if (Utils.calcDjiCrc16(ff, ff.length - 2) == crc16) {
								// All is ok, create and fill the message
								Message1 message = new Message1();
								message.setMessageSource(ff[4]);
								message.setMessageTarget(ff[5]);
								message.setMessageSequence(((ff[7] << 8) & 0xFF00) | (ff[6] & 0xFF));
								if ((ff[8] & (byte) 0x80) != 0) {
									message.setMessageFlagIsResponse(true);
								}
								if ((ff[8] & (byte) 0x40) != 0) {
									message.setMessageFlagWantResponse(true);
								}
								message.debugSetMessageFlagsByte(ff[8]); // For debug purposes
								message.setMessageCommandSet(ff[9]);
								message.setMessageCommandNum(ff[10]);
								byte[] messageData = new byte[ff.length - 13];
								System.arraycopy(ff, 11, messageData, 0, messageData.length);
								message.setMessageData(messageData);
								outputMessages.add(message);
							} else {
								// Found frame with corrupted CRC16
								// TODO: Signal about it somehow?
								debugCrc16Mismatch += 1;
							}
						} else {
							// We do not have needed amount of bytes for the rest of frame in input buffer,
							// rewind back to found SOF for further processing and return
							inputBuffer.position(inputBuffer.position() - 4);
							debugNoBytesAvailForFrameBody += 1;
							debugIncompleteFrameBodyArm = true;
							return;
						}
					} else {
						// Version or CRC8 mismatch, rewind back to next byte after found false SOF
						inputBuffer.position(inputBuffer.position() - 3);
						debugCrc8Mismatch += 1;
					}
				} else {
					// We do not have needed amount of bytes for frame header in input buffer,
					// rewind back to found SOF for further processing and return
					inputBuffer.position(inputBuffer.position() - 1);
					debugNoBytesAvailForFrameHeader += 1;
					debugIncompleteFrameHeaderArm = true;
					return;
				}
			}

			if ((debugIncompleteFrameHeaderArm || debugIncompleteFrameBodyArm) && (debugIterCnt > 0)) {
				if (debugIncompleteFrameHeaderArm) {
					debugIncompleteFrameHeaderErrors += 1;
					debugIncompleteFrameHeaderArm = false;
				}
				if (debugIncompleteFrameBodyArm) {
					debugIncompleteFrameBodyErrors += 1;
					debugIncompleteFrameBodyArm = false;
				}
			}

			debugIterCnt += 1;
		}
	}

	// Debug things

	private int debugCrc8Mismatch;
	private int debugCrc16Mismatch;
	private int debugNoBytesAvailForFrameHeader;
	private int debugNoBytesAvailForFrameBody;

	private boolean debugIncompleteFrameHeaderArm;
	private boolean debugIncompleteFrameBodyArm;

	private int debugIncompleteFrameHeaderErrors;
	private int debugIncompleteFrameBodyErrors;

	public int debugGetBufferAvailable() {
		return inputBuffer.remaining();
	}

	public int debugGetCrc8Mismatch() {
		return debugCrc8Mismatch;
	}

	public int debugGetCrc16Mismatch() {
		return debugCrc16Mismatch;
	}

	public int debugGetNoBytesAvailForFrameHeader() {
		return debugNoBytesAvailForFrameHeader;
	}
}
