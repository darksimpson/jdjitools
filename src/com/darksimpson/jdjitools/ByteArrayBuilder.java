package com.darksimpson.jdjitools;

import java.util.Arrays;

public class ByteArrayBuilder {
	private byte[] values = new byte[0];

	ByteArrayBuilder append(byte[] _bytes) {
		int offset = values.length;
		values = Arrays.copyOf(values, offset+_bytes.length);
		System.arraycopy(_bytes, 0, values, offset, _bytes.length);
		return this;
	}

	byte[] toBytes() {
		return values;
	}
}
