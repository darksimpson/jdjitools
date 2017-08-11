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
}
