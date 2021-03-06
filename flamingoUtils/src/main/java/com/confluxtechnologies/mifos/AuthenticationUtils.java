package com.confluxtechnologies.mifos;

import java.io.IOException;
import java.security.MessageDigest;

/**
 * This class encapsulate all the logic related to password hashing
 */
public class AuthenticationUtils {
	MessageDigest messageDigest = null;
	CacheUtils cacheUtils;
	private static final String MIFOSURL = "mifosURL";

	public boolean authenticateUser(String userName, String password) {
		try {
			initializeCacheUtils();
			// get hashed password for the username
			String storedHash = cacheUtils.getValueForKey(userName);
			String inputHash = AuthenticationUtils.computeHash(password);
			if (storedHash.equals(inputHash)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean addUser(String userName, String password, String mifosURL) {
		try {
			initializeCacheUtils();
			cacheUtils.updateCache(userName,
					AuthenticationUtils.computeHash(password), MIFOSURL,
					mifosURL);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String getRegisteredMifosServerURL() {
		return cacheUtils.getValueForKey(MIFOSURL);
	}

	private void initializeCacheUtils() throws IOException {
		if (null == cacheUtils) {
			cacheUtils = new CacheUtils();
		}
	}

	private static String computeHash(String x) throws Exception {
		java.security.MessageDigest d = null;
		d = java.security.MessageDigest.getInstance("SHA-1");
		d.reset();
		d.update(x.getBytes());
		return byteArrayToHexString(d.digest());
	}

	private static String byteArrayToHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

}
