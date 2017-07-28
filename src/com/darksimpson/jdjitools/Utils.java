package com.darksimpson.jdjitools;

import javax.xml.bind.DatatypeConverter;

public class Utils {
	public static String bytesToHexString(byte[] array) {
		return DatatypeConverter.printHexBinary(array);
	}

	public static byte[] hexStringToBytes(String s) {
		return DatatypeConverter.parseHexBinary(s);
	}
}
