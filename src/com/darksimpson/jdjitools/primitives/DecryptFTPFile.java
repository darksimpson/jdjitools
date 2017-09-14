package com.darksimpson.jdjitools.primitives;

import com.darksimpson.jdjitools.JDTException;
import com.darksimpson.jdjitools.Utils;
import sun.misc.IOUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;

public class DecryptFTPFile {
	private static final String iv1Str = "0123456789abcdef";
	private static final String key1Str = "this-aes-key\0\0\0\0";

	private static final String iv2Str = "0123456789abcdef";
	private static final String key2Str = "YP1Nag7ZR&Dj\0\0\0\0";

	public void decryptFTPFile(int keyNum, File inFile, File outFile) throws JDTException {
		if (inFile == null) {
			throw new JDTException("Input file must not be null");
		}
		if (outFile == null) {
			throw new JDTException("Output file must not be null");
		}
		if (keyNum < 1 || keyNum > 2) {
			throw new JDTException("Key number must be between 1 and 2");
		}

		FileInputStream fis;

		try {
			fis = new FileInputStream(inFile);
		} catch (FileNotFoundException e) {
			throw new JDTException("Input file must exist in file system and must be accessible: " + e.getMessage());
		}

		byte[] encBytes;

		try {
			encBytes = IOUtils.readFully(fis, -1, true);
		} catch (IOException e) {
			throw new JDTException("Error reading input file: " + e.getMessage());
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				throw new JDTException("Error closing input file: " + e.getMessage());
			}
		}

		byte[] decBytes; // = new byte[encBytes.length];

		byte[] ivBytes = new byte[16];
		byte[] keyBytes = new byte[16];

		switch (keyNum) {
			case 1: {
				ivBytes = Utils.singleByteStringToBytes(iv1Str);
				keyBytes = Utils.singleByteStringToBytes(key1Str);
				break;
			}
			case 2: {
				ivBytes = Utils.singleByteStringToBytes(iv2Str);
				keyBytes = Utils.singleByteStringToBytes(key2Str);
				break;
			}
		}

		try {
			IvParameterSpec iv = new IvParameterSpec(ivBytes);
			SecretKey k = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
			Cipher c = Cipher.getInstance("AES/CBC/NoPadding");
			c.init(Cipher.DECRYPT_MODE, k, iv);
			decBytes = c.doFinal(encBytes);
		} catch (Exception e) {
			throw new JDTException("Can't perform AES decryption: " + e.getMessage());
		}

		// Calc real length (without padding)
		int padSize = decBytes[decBytes.length-1];
		if (padSize < 1 || padSize > 16) {
			// Incorrect padding size
			throw new JDTException("Padding size is '" + String.valueOf(padSize) + "' (needed to be > 0 and < 16), decrypted info is erroneous");
		}
		int realLen = decBytes.length - padSize;

		FileOutputStream fos;

		try {
			fos = new FileOutputStream(outFile);
		} catch (FileNotFoundException e) {
			throw new JDTException("Output file must be accessible: " + e.getMessage());
		}

		try {
			fos.write(decBytes, 0, realLen);
		} catch (IOException e) {
			throw new JDTException("Error writing output file: " + e.getMessage());
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				throw new JDTException("Error closing output file: " + e.getMessage());
			}
		}
	}
}
