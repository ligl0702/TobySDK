package com.toby.sdk.utils.lang;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberUtils {

	/**
	 * 四舍五入
	 *
	 * @param ori
	 *            原始数
	 * @param keepNum
	 *            需要保留的小数位数
	 * @return
	 */
	public static double roundDecimals(double ori, int keepNum) {
		return Math.round(ori * Math.pow(10, keepNum)) / Math.pow(10, keepNum);
	}

	public static byte[] getBytesFromLong(Long val) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeLong(val);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos.toByteArray();
	}
	
	public static void norRepeatRandom(int[] arr) {
		if (arr == null || arr.length <= 0)
			return;

		int length = arr.length;
		int[] seed = new int[length];
		for (int i = 0; i < length; i++) {
			seed[i] = i;
		}

		Random ran = new Random();
		for (int i = 0; i < seed.length; i++) {
			int j = ran.nextInt(seed.length - i);
			arr[i] = seed[j];
			// 将最后一个未用的数字放到这里
			seed[j] = seed[seed.length - 1 - i];
		}
		// System.out.println("ranArr:" + Arrays.toString(arr));
	}
	
	public static boolean isAllNum(String text){
		if(TextUtils.isEmpty(text)){
			return false;
		}else{
			String regString = "^[0-9_]+$";
			Pattern pattern = Pattern.compile(regString);
			Matcher matcher = pattern.matcher(text);
			if(matcher.find()){
				return true;
			}else{
				return false;
			}
		}
	}
	
	
}
