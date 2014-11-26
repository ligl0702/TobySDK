package com.toby.sdk.utils.lang;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class SecurityUtils {

	public static byte[] shaDigest(String domainname, String pwd) {
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA");
			return sha.digest((domainname + ":" + pwd).getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}
	}
	
	public static String MD5String(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
	

	public static byte[] md5Key(long robotId, byte[] credential, byte[] randomKey) {
		MessageDigest encrytMd5;
		try {
			encrytMd5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			return null;
		}
		ByteBuffer md5Buff = ByteBuffer.allocate(64);
		byte[] robotidBytes = longToByteArray(robotId);
		md5Buff.put(robotidBytes);
		md5Buff.put(credential);
		md5Buff.put(randomKey);

		byte[] tokenMd5 = new byte[md5Buff.position()];
		md5Buff.flip();
		md5Buff.get(tokenMd5, 0, tokenMd5.length);
		byte[] result = encrytMd5.digest(tokenMd5);

		return result;
	}

	public static byte[] md5Key(byte[] credential) {
		MessageDigest encrytMd5;
		try {
			encrytMd5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			return null;
		}
		ByteBuffer md5Buff = ByteBuffer.allocate(64);
		md5Buff.put(credential);
		
		byte[] tokenMd5 = new byte[md5Buff.position()];
		md5Buff.flip();
		md5Buff.get(tokenMd5, 0, tokenMd5.length);
		byte[] result = encrytMd5.digest(tokenMd5);

		return result;
	}
	
	public static byte[] longToByteArray(long l)
	{
		byte[] result = new byte[8];
		result[0] = (byte) (l & 0xFF);
		result[1] = (byte) ((l >> 8) & 0xFF);
		result[2] = (byte) ((l >> 16) & 0xFF);
		result[3] = (byte) ((l >> 24) & 0xFF);
		result[4] = (byte) ((l >> 32) & 0xFF);
		result[5] = (byte) ((l >> 40) & 0xFF);
		result[6] = (byte) ((l >> 48) & 0xFF);
		result[7] = (byte) ((l >> 56) & 0xFF);
		return result;
	}

    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.reset();

            messageDigest.update(str.getBytes("UTF-8"));
        } catch (Exception ex) {
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString().toLowerCase(Locale.getDefault());
    }

}
