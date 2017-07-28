package com.darksimpson.jdjitools.primitives;

import com.darksimpson.jdjitools.JDTException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DeriveKey {
	public byte[] deriveKey(byte[] inKey, String inParam, int outKeyLen) throws JDTException {
		// Do some checks
		if (inKey.length != 32) {
			throw new JDTException("Invalid input key length, must be exactly 16 bytes (32 symbols in hexadecimal representation)");
		}
		if (outKeyLen < 1 || outKeyLen > 32) {
			throw new JDTException("Invalid output key length, must be more than 0 and less than 32 bytes");
		}

		byte[] paramBytes;

		// Prepare input parameter
		if (inParam.length() <= 32) {
			// If parameter is less than 32 bytes, complete it with PKCS#7-like padding.
			// If it is exactly 32 bytes long, leave it as is.
			paramBytes = new byte[32];
			byte[] tpb = inParam.getBytes(StandardCharsets.ISO_8859_1);
			System.arraycopy(tpb, 0, paramBytes, 0, tpb.length);
			for (int i = tpb.length; i < 32; i++) {
				paramBytes[i] = (byte)(32 - tpb.length);
			}
		} else {
			// If parameter is more than 32 bytes, compute SHA-256 hash on it
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-256");
				paramBytes = digest.digest(inParam.getBytes(StandardCharsets.ISO_8859_1));
			} catch (NoSuchAlgorithmException e) {
				throw new JDTException("Can't perform SHA-256 hash: " + e.getMessage());
			}
		}

		byte[] derivedKeyFull;

		// AES-256-CBC encode prepared parameter with input key
		try {
			IvParameterSpec iv = new IvParameterSpec(new byte[16]);
			SecretKey k = new SecretKeySpec(inKey, 0, inKey.length, "AES");
			Cipher c = Cipher.getInstance("AES/CBC/NoPadding");
			c.init(Cipher.ENCRYPT_MODE, k, iv);
			derivedKeyFull = c.doFinal(paramBytes);
		} catch (Exception e) {
			throw new JDTException("Can't perform AES encryption: " + e.getMessage());
		}

		byte[] derivedKeyOut = new byte[outKeyLen];

		// Use only needed bytes of derived key
		System.arraycopy(derivedKeyFull, 0, derivedKeyOut, 0, outKeyLen);

		return derivedKeyOut;
	}
}
