package com.toby.sdk.utils.lang;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import com.toby.sdk.utils.android.Tools;
import com.toby.sdk.utils.android.Trace;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {

	public static boolean isChars(String letters) {
		for (int i = 0; i < letters.length(); i++) {
			if (letters.charAt(i) >= 'a' && letters.charAt(i) <= 'z') {
				continue;
			}
			else if (letters.charAt(i) >= 'A' && letters.charAt(i) <= 'Z') {
				continue;
			}
			else {
				return false;
			}
		}
		return true;
	}

	public static boolean isNumber(String numbers) {
		if (numbers == null || numbers.length() == 0) {
			return false;
		}
		for (int i = 0; i < numbers.length(); i++) {
			if (!(numbers.charAt(i) >= '0' && numbers.charAt(i) <= '9')) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 将value前面补0,直到达到指定的长度
	 * 
	 * @param length
	 * @param value
	 * @return
	 */
	public static String formatNumber(int length, int value) {
		StringBuffer sb = new StringBuffer();
		sb.append(value);
		while (sb.length() < length) {
			sb.insert(0, "0");
		}
		return sb.toString();
	}

	/**
	 * 判断传入字符串是否为null或""或"null"
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return str == null || "".equals(str.trim()) || "null".equals(str);
	}

	
	/**
	 * 判断SDcard空间是否小于某值
	 * @param sizeMb
	 * @return
	 */
	public static boolean isAvaiableSpace(int sizeMb) {
		boolean ishasSpace = false;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String sdcard = Environment.getExternalStorageDirectory().getPath();
			StatFs statFs = new StatFs(sdcard);
			long blockSize = statFs.getBlockSize();
			long blocks = statFs.getAvailableBlocks();
			long availableSpare = (blocks * blockSize) / (1024 * 1024);
			if (availableSpare < sizeMb) {
				ishasSpace = true;
			}
		}
		return ishasSpace;
	}
	
	/**
	 * 将语音message时间格式化 , 格式为 x′ m″
	 * @param time
	 * @return
	 */
	public static String voiceTimeFormat(int time) {
		if (time > 59) {
			int s = time / 60;
			int m = time % 60;
			return String.valueOf(s) + "′ " + String.valueOf(m) + "″"; 
		}
		return String.valueOf(time) + "″";
	}
	
	public static boolean validateEmail(String email){  
        //Pattern pattern = Pattern.compile("[0-9a-zA-Z]*.[0-9a-zA-Z]*@[a-zA-Z]*.[a-zA-Z]*", Pattern.LITERAL);   
        if(email == null){  
            return false;  
        }  
        //验证开始   
        //不能有连续的.   
        if(email.indexOf("..") != -1){  
            return false;  
        }  
        //必须带有@   
        int atCharacter = email.indexOf("@");  
        if (atCharacter == -1) {  
            return false;  
        }  
        //最后一个.必须在@之后,且不能连续出现   
        if(atCharacter > email.lastIndexOf('.') || atCharacter+1 == email.lastIndexOf('.')){  
            return false;  
        }  
        //不能以.,@结束和开始   
        if (email.endsWith(".") || email.endsWith("@") || email.startsWith(".") || email.startsWith("@")) {  
            return false;  
        }  
        return true;  
    }


    public static File getOutputMediaFile(String fileName,Context context) {

        Trace.d("Common===", "" + fileName);

        File mediaStorageDir = new File(
                Tools.getSDPath(),
                Tools.getTempPath(context));
        if (!mediaStorageDir.exists()) {

            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File mediaFile;

        if (fileName == null || "".equals(fileName)) {
            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    fileName);
        }
        Trace.i("susie", mediaFile.getPath());
        return mediaFile;
    }

}
