package com.tny.game.codec;

import java.io.*;
import java.util.Base64;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/9/10 11:52 上午
 */
public interface ObjectCodec<T> {

	boolean isPlaintext();

	byte[] encode(T value) throws IOException;

	void encode(T value, OutputStream output) throws IOException;

	T decode(byte[] bytes) throws IOException;

	T decode(InputStream input) throws IOException;

	default String formatBytes(byte[] data) {
		if (data == null) {
			return null;
		}
		if (isPlaintext()) {
			return new String(data, CoderCharsets.DEFAULT);
		} else {
			return Base64.getUrlEncoder().encodeToString(data);
		}
	}

	default byte[] parseBytes(String data) {
		if (data == null) {
			return null;
		}
		if (isPlaintext()) {
			return data.getBytes(CoderCharsets.DEFAULT);
		} else {
			return Base64.getUrlDecoder().decode(data);
		}
	}

}