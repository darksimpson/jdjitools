package com.darksimpson.jdjitools;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;

public class Utils {
	public static String bytesToHexString(byte[] array) {
		return DatatypeConverter.printHexBinary(array);
	}

	public static byte[] hexStringToBytes(String s) {
		return DatatypeConverter.parseHexBinary(s);
	}

	public static byte[] singleByteStringToBytes(String text) {
		byte[] result = new byte[text.length()];

		for (int i = 0; i < text.length(); i++) {
			result[i] = (byte)text.charAt(i);
		}

		return result;
	}

	public static String singleByteStringToString(String text, Charset charset) {
		byte[] textBytes = Utils.singleByteStringToBytes(text);
		return new String(textBytes, charset);
	}

	public static String singleByteStringToString(String text) {
	  return singleByteStringToString(text, Charset.defaultCharset());
	}

	public static byte[] getBytesFromHexOrIntString(String inStr, int maxValue) throws JDTException {
		if (maxValue < 1) {
			throw new JDTException("Maximum value can't be less than 1!");
		}
		if (inStr.isEmpty()) {
			throw new JDTException("Input number can't be empty!");
		}

		int howManyBytes = 0;
		int tmpMax = maxValue;
		while (tmpMax != 0) {
			tmpMax >>= 8;
			howManyBytes++;
		}

		byte[] resBytes = new byte[howManyBytes];

		int tmpInt;

		try {
			if (inStr.startsWith("0x")) {
				tmpInt = Integer.valueOf(inStr.substring(2), 16);
			} else {
				tmpInt = Integer.valueOf(inStr, 10);
			}
		} catch (NumberFormatException e) {
			throw new JDTException("Error interpreting '" + inStr + "' as hex or integer!");
		}

		if (tmpInt > maxValue || tmpInt < 0) {
			throw new JDTException("Input number '" + inStr + "' is greater than maximum (" + String.valueOf(maxValue) + ") or less than 0!");
		}

		for (int i = 0; i < resBytes.length; i++) {
			resBytes[i] = (byte) ((tmpInt & (0xFF << (i * 8))) >> (i * 8));
		}

		return resBytes;
	}

	public static byte[] getBytesFromHexOrString(String inStr) throws JDTException {
		if (inStr.isEmpty()) {
			throw new JDTException("Input hex or string can't be empty!");
		}

		if (inStr.startsWith("0x")) {
			try {
				return hexStringToBytes(inStr.substring(2));
			} catch (Exception e) {
				throw new JDTException("Error interpreting '" + inStr + "' as hex!");
			}
		} else {
			return singleByteStringToBytes(inStr);
		}
	}
}
