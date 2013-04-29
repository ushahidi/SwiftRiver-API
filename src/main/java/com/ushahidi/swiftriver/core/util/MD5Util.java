/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/agpl.html>
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.core.util;

import java.io.*;
import java.security.*;

public class MD5Util {

	/**
	 * Return hex representation for the given byte array
	 * 
	 * @param array
	 * @return
	 */
	public static String hex(byte[] array) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; ++i) {
			sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(
					1, 3));
		}
		return sb.toString();
	}

	/**
	 * Return an MD5 hex digest for the given message.
	 * 
	 * @param message
	 * @return
	 */
	public static String md5Hex(String message) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return hex(md.digest(message.getBytes("CP1252")));
		} catch (NoSuchAlgorithmException e) {
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}
	
	/**
	 * Returns an MD5 hex digest for the given message. The method
	 * concatenates all the parameters to a single string then
	 * computes the digest of the concatenated string
	 * 
	 * @param message
	 * @return
	 */
	public static String md5Hex(String... message) {
		String input = "";
		for (String token: message) {
			input += token;
		}

		return md5Hex(input);
	}
}