package com.toby.sdk.utils.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.wole.lepai.R;
import com.wole.lepai.constant.ConstantKeys;
import com.wole.lepai.constant.ConstantProperties;
import com.wole.lepai.model.VideoBaseBean;
import com.wole.lepai.utils.android.bitmap.PicassoTransformation;
import com.wole.lepai.utils.android.gesture.ScreenUtil;

import java.io.*;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Tools {

    public static String[] videoFormats = {".mp4", ".3gp", ".ts", ".webm", ".mkv",
            ".mpg", ".rmvb", ".mpeg", ".mov", ".vob", "ogg"};

    // 网络状况
    /**
     * 没有网络
     */
    public static final int NETWORKTYPE_INVALID = 0;
    /**
     * 2G网络
     */
    public static final int NETWORKTYPE_2G = 2;
    /**
     * 3G和3G以上网络，或统称为快速网络
     */
    public static final int NETWORKTYPE_3G = 3;
    /**
     * wifi网络
     */
    public static final int NETWORKTYPE_WIFI = 4;
    /**
     * wap网络
     */
    public static final int NETWORKTYPE_WAP = 1;
    /**
     * 判断当前的网络状态
     */
    public static final int WIFI = 1;
    public static final int NOTCONNECT = -1;
    public static final int MOBILE = 2;
    // TAG
    public static String TAG = "Tools";

    public enum NetType {
        NONE, WIFI, CELLULAR
    }

    private static String[] EFFECT_VIDEO_TYPE = {".wmv", ".avi", ".dat", ".asf", ".rm", ".rmvb", ".ram"
            , ".mpg", ".mpeg", ".3gp", ".mov", ".mp4", ".m4v", ".dvix", ".dv", ".dat", ".mkv"
            , ".flv", ".vob", ".ram", ".qt", ".divx", ".cpk", ".fli", ".flc", ".mod"};

    // 新增 拼接signature(验证参数签名)
    // author：zhanghao
    /*
     * public String getSignature(HashMap<String, Object> hashMap) { Set
     * entrySet = hashMap.entrySet(); Iterator iterator = entrySet.iterator();
     * String stemp = ""; while (iterator.hasNext()) { Map.Entry keyValue =
     * (Map.Entry) iterator.next(); Object key = keyValue.getKey(); Object value
     * = keyValue.getValue(); Trace.i("getSignature:", "getSignature:" + "key:"
     * + key.toString() + "value:" + value.toString()); stemp +=
     * value.toString(); } if (!"".equals(stemp)) { stemp +=
     * ConstantKeys.SIGNATURE_KEY; } Trace.i("getSignature2:", "getSignature2:"
     * + stemp); return stemp; }
     */

    public static void hideStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
    }

    public static void showStatusBar(Activity activity) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(attrs);
    }

    @SuppressLint("DefaultLocale")
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

    public static String getMD5ByteList(List<byte[]> datalList) {
        if (datalList == null || datalList.size() == 0) {
            return null;
        }
        try {
            MessageDigest messageDigest = null;
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            for (int i = 0; i < datalList.size(); i++) {
                messageDigest.update(datalList.get(i));
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean isNoNetwork(Context context) {
        return NetType.NONE == getNetType(context);
    }

    public static boolean is2G3GNetwork(Context context) {
        return NetType.CELLULAR == getNetType(context);
    }

    public static NetType getNetType(Context context) {
        ConnectivityManager conManger = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conManger == null) {
            return NetType.NONE;
        }

        NetworkInfo networkInfo = conManger.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return NetType.NONE;
        }

        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return NetType.WIFI;
        }
        return NetType.CELLULAR;
    }

    public static String parseTime(int duration) {
        DecimalFormat mDecimalFormat = new DecimalFormat("00");
        long minute = duration / 60000;
        long second = (duration % 60000) / 1000;
        String time = mDecimalFormat.format(minute) + ":"
                + mDecimalFormat.format(second);
        return time;
    }

    /**
     * video时长时间显示：**"**
     */
    public static String parseVideoTime(long duration) {
        DecimalFormat mDecimalFormat = new DecimalFormat("00");
        long minute = duration / (60 * 1000);
        long second = (duration / 1000) % 60;
        String time = mDecimalFormat.format(minute) + "\' "
                + mDecimalFormat.format(second) + "\"";
        return time;
    }

    /**
     * 云点播观看记录的时间显示
     */
    public static String parsePlayRecordTime(int duration) {
        DecimalFormat mDecimalFormat = new DecimalFormat("00");
        long minute = duration / 60000;
        long second = (duration % 60000) / 1000;
        String time = mDecimalFormat.format(minute) + ":"
                + mDecimalFormat.format(second);
        return time;
    }

    /**
     * 半角转换为全角,解决TextView排版不整齐问题
     * 
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        if (input == null) {
            input = "";
        }
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    @SuppressLint("DefaultLocale")
    public static String getPlayTimes(double times) {
        if (times > 100000) {
            float t = (float) (times + 999) / 10000;
            return String.format(Locale.getDefault(), "%.1f万", t);
        }
        return String.format(Locale.getDefault(), "%.0f", times);
    }

    public static File getOutputMediaFile(String fileName, Context context) {

        File mediaStorageDir = new File(
                Tools.getSDPath(),
                Tools.getTempPath(context));

        /*
         * File mediaStorageDir = new File(
         * Tools.getSDPath(),ConstantKeys.LEPAI_DIRECTORY );
         */
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
                    "VID_" + timeStamp + videoFormats[0]);
        } else {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    fileName);
        }
        Trace.i(TAG, mediaFile.getPath());
        return mediaFile;
    }

    public static void hideKeyBoard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            ((InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(activity.getCurrentFocus()
                            .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS
                    );
        }
    }

    public static void showKeyBoard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            ((InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showSoftInputFromInputMethod(activity.getCurrentFocus()
                            .getWindowToken(), InputMethodManager.SHOW_FORCED);
        }
    }

    public static int getWordCount(String s) {
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255)
                length++;
            else
                length += 2;

        }
        return length;
    }

    public static String parseTime(String milliseconds, SimpleDateFormat sdf) {

        Calendar calendar = Calendar.getInstance();
        milliseconds = handleTime(milliseconds);
        try {
            calendar.setTimeInMillis(Long.parseLong(milliseconds));
        } catch (Exception e) {
            return "";
        }
        String time = sdf.format(calendar.getTime());
        return time;
    }

    public static String compareTimeForTimeLine(Date date, Date tData) {
        Calendar todayCalendar = Calendar.getInstance();
        Calendar curCalendar = Calendar.getInstance();
        curCalendar.setTime(date);
        if (tData != null) {
            todayCalendar.setTime(tData);
        } else {
            todayCalendar.getTime();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String targetDate = format.format(date);
        String result = targetDate;
        int curYear = curCalendar.get(Calendar.YEAR);
        int curDay = curCalendar.get(Calendar.DAY_OF_YEAR);

        int toYear = todayCalendar.get(Calendar.YEAR);
        int toDay = todayCalendar.get(Calendar.DAY_OF_YEAR);
        String preResult = "";
        if (curYear == toYear) {
            int subDay = curDay - toDay;
            if (subDay == 0) {
                preResult = "今天#";
            } else if (subDay == -1) {
                preResult = "昨天#";
            } else if (subDay == -2) {
                preResult = "前天#";
            }
        }
        result = preResult + result;
        return result;
    }

    public static String compareTimeForTimeLine(Date date) {
        return compareTimeForTimeLine(date, null);
    }

    public static String handleTimeToDateFormat(String time) {
        String formatTime = handleTime(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(time));

    }

    public static String handlerTimeToDataFormat(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(time));
    }

    public static String parseCurrentTimeByDateFormat(long timeStamp, SimpleDateFormat format) {
        Date date = new Date(timeStamp);
        return format.format(date);
    }

    public static String handleTime(String time) {
        int len = time.length();
        // 默认为13位时间
        while (len < 13) {
            time += "0";
            len++;
        }
        return time;
    }

    public static long handleTime(long time) {
        // 默认为13位时间
        while (time < 1000000000000L) {
            time = time * 10;
        }
        return time;
    }

    public static String getFilePathFromUri(Context context, Uri uri) {
        String[] selectionStrs = new String[] {
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.DATA};
        Trace.i("Uri now", uri.toString());
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, selectionStrs,
                    null, null, null);
            if (cursor != null) {
                Trace.i("getFilePathFromUri,cursor not null", "not null");
                cursor.moveToFirst();
                return cursor.getString(1);
            } else {
                Trace.i("getFilePathFromUri,cursor null", "null");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "";
    }

    public static String getCommentSource(String type, String origin) {
        if (origin != null && !"".equals(origin) && !"v".equals(origin)) {
            if ("56video_3g".equals(origin)) {
                return "56视频客户端";
            } else if ("56mv_3g".equals(origin)) {
                return "56音乐汇客户端";
            } else if ("cz_wbjh_3g".equals(origin)) {
                return "微播江湖客户端";
            }
        }

        if (type != null) {
            if ("web".equals(type)) {
                return "56网";
            } else if ("sina".equals(type)) {
                return "新浪微博";
            } else if ("renren".equals(type)) {
                return "人人网";
            } else if ("tqq".equals(type)) {
                return "腾讯微博";
            } else if ("web3g".equals(type)) {
                return "56网";
            } else if ("rrk".equals(type)) {
                return "人人K歌";
            }
        }

        return "56网";

    }

    public static boolean isSDCardExists() {
        return (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState()));
    }

    
    
    // 获取sdcard的剩余空间
    // unit 返回unit为单位的数据
    public static long readSDCard(String unit) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            if (!sdcardDir.exists()) {
                return 0;
            } else {
                StatFs sf = new StatFs(sdcardDir.getPath());
                long blockSize = sf.getBlockSize();
                // long blockCount = sf.getBlockCount();
                long availCount = sf.getAvailableBlocks();
                // blockCount+",总大小:"+blockSize*blockCount/1024+"KB");
                // availCount*blockSize/1024+"KB");
                if (unit.equalsIgnoreCase("Byte")) {
                    return availCount * blockSize;
                } else if (unit.equalsIgnoreCase("KB")) {
                    return availCount * blockSize / 1024;
                } else if (unit.equalsIgnoreCase("MB")) {
                    return availCount * blockSize / 1024 / 1024;
                } else if (unit.equalsIgnoreCase("GB")) {
                    return availCount * blockSize / 1024 / 1024 / 1024;
                }
            }
        }
        return 0;
    }

    /**
     * 获取存储空间的剩余可用空间，返回long类型*
     */

    public static long readAvailableSize(String filePath) {

        File file = new File(filePath);
        if (file.exists()) {
            StatFs sf = new StatFs(file.getPath());
            long blockSize = sf.getBlockSize();
            long availCount = sf.getAvailableBlocks();
            return blockSize * availCount;
        } else {

            return 0;
        }
    }

    // 转换成可读占用空间
    public static String byte2GMK(Long leftSize) {

        DecimalFormat format = new DecimalFormat("#.0");
        String suffix = "B";
        double tempG = 0;
        double tempM = 0;
        double tempK = 0;

        if ((leftSize >> 30) > 0) {
            suffix = "G";
            tempG = (double) (leftSize >> 20) / 1024.0;
            return format.format(tempG) + suffix;
        } else if ((leftSize >> 20) > 0) {
            suffix = "M";
            tempM = (double) (leftSize >> 10) / 1024.0;
            return format.format(tempM) + suffix;
        } else if ((leftSize >> 10) > 0) {
            suffix = "K";
            tempK = (double) (leftSize) / 1024.0;
            return format.format(tempK) + suffix;
        } else {
            return leftSize + suffix;
        }

    }

    /**
     * 获取指定目录下 的内存使用情况 long[0]:可用空间；long[1]:已用空间*
     */
    public static Long[] readSDCardLeft(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {

            file.mkdir();
        }
        boolean exists = file.canExecute();

        if (exists) {

            StatFs sf = new StatFs(filePath);
            long blockSize = sf.getBlockSize();
            long availCount = sf.getAvailableBlocks();
            long availSize = blockSize * availCount;
            long usedSize = 0;
            File[] fileList = file.listFiles();
            if (fileList.length == 0)
                return new Long[] {availSize, usedSize};
            for (int i = 0; i < fileList.length; i++) {
                try {
                    FileInputStream fis = new FileInputStream(fileList[i]);
                    usedSize = usedSize + fis.available();
                    fis.close();
                } catch (Exception e) {

                    usedSize = usedSize + 0;
                }
            }
            return new Long[] {availSize, usedSize};
        }
        return new Long[] {(long) 0, (long) 0};

    }

    public static long getAllBlockCount() {
        File root = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(root.getAbsolutePath());
        int blockCount = statFs.getBlockCount();
        int blockSize = statFs.getBlockSize();
        return blockSize * blockCount;
    }

    public static long getAvailableBlockCount() {
        File root = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(root.getAbsolutePath());
        int blockSize = statFs.getBlockSize();
        int availableBlocks = statFs.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static void deleteVideoFile(String fvid, String videopath) {
        // File sdCard = Environment.getExternalStorageDirectory();
        // String rootPath = sdCard.getPath() + "/56/";
        // File file1 = new File(rootPath + fvid + ".mp4");
        // File file2 = new File(rootPath + fvid);
        // if (file1.exists()) {
        // file1.delete();
        // }
        // if (file2.exists()) {
        // file2.delete();
        // }
        File delFile = new File(videopath);
        if (delFile.exists()) {
            delFile.delete();
        }
    }

    public static boolean isVideoExist(String fvid) {
        File sdCard = Environment.getExternalStorageDirectory();
        String rootPath = sdCard.getPath() + "/" + ConstantKeys.LEPAI_DIRECTORY + "/";
        String filepath = rootPath + fvid;
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * 下载的路径
     * 
     * @throws
     * @author xiangtao
     */

    public static String getSavePath() {
        File sdCard = Environment.getExternalStorageDirectory();
        String rootPath = sdCard.getPath() + "/" + ConstantKeys.LEPAI_DIRECTORY;
        File file = new File(rootPath);
        if (!file.exists()) {
            file.mkdir();
        }
        String savePath = rootPath + "/";
        return savePath;
    }

    private static String getFilePath(String filepath) {
        String storagePath = filepath + "/" + ConstantKeys.LEPAI_DIRECTORY;
        File file = new File(storagePath);
        if (!file.exists()) {
            file.mkdir();
        }
        String savePath = storagePath + "/";
        return savePath;
    }

    /**
     * 获得手机的主要存储设备的路径，String[0]:master; String[1]:second
     * 如果在获取路径的执行中失败，则默认使用android api ：Environment.getExternalStorageDirectory()
     * 获取master路径， second为空； 4.3三星原生系统不适用，另想辙吧 修改为只负责获取外置sdcard的路径 *
     */
    public static String getExtenalPathsOld(String apiPath) {

        // String storage_master="";
        String storage_second = "";
        // String
        // system_storage=Environment.getExternalStorageDirectory().getPath();
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("/dev/"))
                    continue;
                if (line.contains("secure"))
                    continue;
                if (line.contains("/system"))
                    continue;
                if (line.contains("/data"))
                    continue;
                if (line.contains("/cache"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.contains("firmware"))
                    continue;

                if (line.contains("fat") || line.contains("fuse")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        if (!columns[1].equals(apiPath)) {
                            // storage_master=columns[1];
                            // }else{
                            storage_second = columns[1];
                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return storage_second;
    }

    /**
     * 获得手机的主要存储设备的路径，String[0]:master; String[1]:second
     * 如果在获取路径的执行中失败，则默认使用android api ：Environment.getExternalStorageDirectory()
     * 获取master路径， second为空；并能自动创建相应的文件夹 *
     */
    public static String[] getExtenalPaths() {

        String storage_master = "";
        String storage_second = "";
        String system_storage = Environment.getExternalStorageDirectory().getPath();
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("ls");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            String subSystem_storage = system_storage.substring(1);
            while ((line = br.readLine()) != null) {
                line = line.toLowerCase();
                if (!line.startsWith("storage"))
                    continue;
                if (line.startsWith("storage")) {
                    File rootFile = new File(line.trim());
                    File[] childFiles = rootFile.listFiles();
                    for (File childfile : childFiles) {
                        String filepath = childfile.getPath();
                        if ((filepath.toLowerCase().contains("sdcard")) &&
                                (childfile.canExecute())) {
                            if ((filepath.equals(subSystem_storage)) || (filepath.toLowerCase().contains("sdcard0"))) {
                                storage_master = filepath;
                            } else {
                                storage_second = filepath;
                            }
                        }
                    }
                }
            }
            if (storage_master.equals("")) {
                File rootFile = new File("sdcard");
                if ((rootFile.exists()) && (rootFile.canExecute())) {
                    storage_master = rootFile.getPath();
                    storage_second = "";
                }
            }
            if (storage_second.equals("")) {
                storage_second = getExtenalPathsOld(system_storage);
                if (storage_second.contains("/shell/")) {
                    storage_second = "";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new String[] {getSavePath(), ""};
        }
        return new String[] {storage_master.equals("") ? "" : getFilePath(storage_master),
                storage_second.equals("") ? "nocard" : getFilePath(storage_second)};
    }

    /**
     * 删除没有下载完的apk
     */
    public static boolean deleteAPK(Context context) {
        String rootPath = context.getCacheDir().getPath();
        File file = new File(rootPath + context.getPackageName() + ".apk");
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    private static String FormetFileSize(long fileS) {// 转换文件大小
        if (fileS == 0) {
            return "0M";
        }
        DecimalFormat df = new DecimalFormat("#.0");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    // 第三方登录生成token
    public static String generateToken(String token) {
        String accessToken = com.wole.lepai.utils.lang.Base64.encode(token.getBytes());
        accessToken = accessToken.replaceAll("\\+", "-").replaceAll("/", "_").replaceAll("=", "");
        return accessToken;
    }

    public static Bitmap getVideoThumbnail(String videoPath) {
        Trace.i(TAG, "getVideoThumbnail:" + videoPath);
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath,
                MediaStore.Images.Thumbnails.MINI_KIND);
        if (bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth(),
                    bitmap.getHeight(), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        } else {
            bitmap = getThumbFromMediaMeta(videoPath);
        }
        return bitmap;
    }

    @SuppressLint("NewApi")
    private static Bitmap getThumbFromMediaMeta(String videoPath) {
        try {
            MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
            if (VERSION.SDK_INT >= 14) {
                mRetriever.setDataSource(videoPath,
                        new HashMap<String, String>());
            } else {
                mRetriever.setDataSource(videoPath);
            }
            Bitmap bitmap = null;
            if (VERSION.SDK_INT >= 10) {
                bitmap = mRetriever.getFrameAtTime();
            }
            if (bitmap != null) {
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static boolean isVideo(String path) {
        if (path.contains("content://media")) {
            return true;
        }
        if (path.contains("/" + ConstantKeys.LEPAI_DIRECTORY + "/")) {// 56下载目录不做判断
            if (path.endsWith("mp3") || path.endsWith("jpg")
                    || path.endsWith("png") || path.endsWith("jpeg")
                    || path.endsWith("txt") || path.endsWith("bmp")) {
                return false;
            }
            return true;
        }
        for (String format : videoFormats) {
            if (path.endsWith(format)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkValidateVedioType(String videoPath) {

        try {
            int index = Math.max(videoPath.lastIndexOf("."), 0);
            int length = videoPath.length();
            String videoType = videoPath.substring(index, length);
            for (int i = 0; i < EFFECT_VIDEO_TYPE.length; i++) {
                if (EFFECT_VIDEO_TYPE[i].equals(videoType)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName)
                && currentPackageName.equals(context.getPackageName())) {
            return true;
        }

        return false;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        if (VERSION.SDK_INT >= 12) {
            return getRealPathFromURISDK14(context, contentUri);
        } else {
            return getRealPathFromURISDK8(context, contentUri);
        }
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURISDK14(Context context, Uri contentUri) {
        String videoPath = "";
        try {
            String[] proj = {MediaStore.Video.Media.DATA};
            CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            videoPath = cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return videoPath;

    }

    public static String getRealPathFromURISDK8(Context context, Uri contentUri) {
        String videoPath = "";
        try {
            String[] proj = {MediaStore.Video.Media.DATA};
            Cursor cursor = ((Activity) context).managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            videoPath = cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return videoPath;

    }

    public static long getUnusedMemory(Context mContext) {
        long MEM_UNUSED;
        ActivityManager am = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        MEM_UNUSED = mi.availMem / 1024;
        return MEM_UNUSED;
    }

    public static void checkNetworkWithWarning(Context context) {
        if (Tools.getNetType(context) == NetType.NONE) {
            Toast.makeText(context,
                    context.getResources().getString(R.string.no_network),
                    Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public static SpannableString getSpanString(Context context,
            String description, int limit) {
        int len = description.length();
        String temp = null;
        if (len > 20) {
            String expand = context.getString(R.string.expand);
            temp = description.substring(0, 20);
            temp += "[" + expand + "]";
            SpannableString spanString = new SpannableString(temp);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(context
                    .getResources().getColor(R.color.color_text_common));
            spanString.setSpan(colorSpan, temp.indexOf(expand),
                    temp.indexOf(expand) + expand.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            return spanString;
        } else {
            temp = description.substring(0, len);
            return new SpannableString(temp);
        }
    }

    public static String getChannelIconUrl(String icon, String prefix,
            boolean pressed) {
        if (icon != null && prefix != null) {
            if (pressed) {
                return String.format(Locale.getDefault(), prefix, icon,
                        "pressed");
            } else {
                return String.format(Locale.getDefault(), prefix, icon,
                        "normal");
            }
        }
        return null;
    }

    public static void keepScreenOn(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static void disableScreenOn(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static String formatFileSize(double size) {
        DecimalFormat format = new DecimalFormat("#.0");
        String fSize = null;
        if (size != 0) {
            float length = (float) size / 1024 / 1024;
            if (length > 1) {
                BigDecimal cc = new BigDecimal(length).setScale(2,
                        BigDecimal.ROUND_HALF_UP);
                fSize = format.format(cc.doubleValue());
                return fSize + "M";
            } else {
                fSize = format.format(size / 1024);

                return fSize + "K";
            }
        } else {
            return "0K";
        }
    }

    /**
     * 图片的缩放方法
     * 
     * @param bgimage ：源图片资源
     * @param newWidth ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
            double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    public static boolean isAppCacheDirExists(Context mContext) {
        File cache = mContext.getCacheDir();
        File[] fileList = cache.listFiles();
        for (File file : fileList) {
            Trace.e("yu.liu12", "cache file is " + file.getName());
        }
        return cache.length() > 0;
    }

    public static String formatPlayCount(long count) {
        DecimalFormat df = new DecimalFormat("###.0");
        String suffix = "";
        double temp = 0;
        if (count > 100000000) {
            temp = (double) count / 100000000;
            suffix = "亿";
        } else if (count > 10000) {
            temp = (double) count / 10000;
            suffix = "万";
        } else {
            temp = count;
        }
        return df.format(temp) + suffix;
    }

    public static int[] getVideoImgeWidthAndHeight(Context context) {
        int width = ScreenUtil.getPotraitWidth(context);
        int height = width;
        return new int[] {width, height};
    }

    class CropSquareTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "square()";
        }
    }

    /**
     * 通过已缓存的文件，得到bitmap图片 如果是分享到微信（或朋友圈）的图片，大小要限制在32KB以内
     * 
     * @param maxSize 文件大小限制 单位：KB
     * @return bitmap
     */
    @SuppressLint("NewApi")
    public static Bitmap getCachedBmp(Context context, VideoBaseBean video, double maxSize) {
        if (video == null) {
            return null;
        }
        try {
            Bitmap cachedBitmap = Picasso.with(context).load(video.video_img).transform(PicassoTransformation.getStrokeTransformation(context)).getCachedBitmap();
            if (cachedBitmap == null) {
                cachedBitmap = Picasso.with(context).load(video.video_mimg).transform(PicassoTransformation.getStrokeTransformation(context)).getCachedBitmap();
                if (cachedBitmap == null) {
                    cachedBitmap = Picasso.with(context).load(video.video_bimg).transform(PicassoTransformation.getStrokeTransformation(context)).getCachedBitmap();
                }
            }
            if (cachedBitmap != null) {
                int size;
                if (VERSION.SDK_INT >= 14) {
                    size = cachedBitmap.getByteCount();
                } else {
                    size = cachedBitmap.getRowBytes() * cachedBitmap.getHeight();
                }
                // 将字节换成KB
                double mid = size / 1024;
                // 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
                if (mid > maxSize && maxSize > 0) {
                    // 获取bitmap大小 是允许最大大小的多少倍
                    double i = mid / maxSize;
                    // 开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
                    // （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
                    cachedBitmap = Tools.zoomImage(cachedBitmap,
                            cachedBitmap.getWidth() / Math.sqrt(i),
                            cachedBitmap.getHeight() / Math.sqrt(i));
                }
                return cachedBitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param myContext
     * @param ASSETS_NAME
     * @param savePath
     * @param saveName
     * @description 拷贝素材到SD卡缓存目录
     * @author liguoliang
     */
    public static void copy(Context myContext, String ASSETS_NAME,
            String savePath, String saveName) {
        String filename = savePath + "/" + saveName;

        File dir = new File(savePath);
        // 如果目录不中存在，创建这个目录
        if (!dir.exists())
            dir.mkdir();
        try {
            if (!(new File(filename)).exists()) {
                InputStream is = myContext.getResources().getAssets()
                        .open(ASSETS_NAME);
                FileOutputStream fos = new FileOutputStream(filename);
                byte[] buffer = new byte[7168];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取SD卡路径
     * 
     * @return
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
            return sdDir.toString();
        }
        return null;

    }

    /**
     * 获取lepai目录
     * 
     * @return
     */
    public static String getLepaiPath() {

        String path = getSDPath() + "/" + ConstantKeys.LEPAI_DIRECTORY;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        return path;
    }

    /**
     * 得到系统cache目录
     * 
     * @param context
     * @return
     */
    public static String getCacheDir(Context context) {
        boolean sdCardExist = false;
        try{
            sdCardExist = Environment.getExternalStorageState()
                    .equals(Environment.MEDIA_MOUNTED);
            if (sdCardExist) {
                return context.getExternalCacheDir().getAbsolutePath();
            } else {
                return context.getCacheDir().getAbsolutePath();
            }
        }catch(NullPointerException e){
          //  e.printStackTrace();
            return context.getCacheDir().getAbsolutePath();
        }
    }

    /**
     * 获取素材MV路径
     * 
     * @param context
     * @return
     */
    public static String getMVPath(Context context) {

        String path = getCacheDir(context) + File.separator + ConstantKeys.MV;

        return path;
    }

    /**
     * 获取素材Music路径
     * 
     * @param context
     * @return
     */
    public static String getMusicPath(Context context) {
        String path = getCacheDir(context) + File.separator + ConstantKeys.MUSIC;
        return path;

    }

    /**
     * 得到分段视频路径列表（TXT）
     * 
     * @return
     */
    public static String getVideoListTxtPath(Context context) {
        String path = Tools.getTempPath(context) + File.separator
                + ConstantProperties.MERGE_FILE_LIST;
        File file = new File(path);
        if (file.exists()) {
            return path;
        } else {
            return null;
        }
    }

    // 获取temp目录
    public static String getTempPath(Context context) {
        String path = getCacheDir(context) + File.separator + ConstantKeys.TEMP;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        return path;
    }

    // 获取logo水印
    public static String getLogoPath(Context context) {
        String pic = getCacheDir(context) + File.separator + ConstantKeys.PIC + "/water.png";
        return pic;

    }

    // 获取字幕水印
    public static String getSubtitlePath(Context context) {
        String pic = getCacheDir(context) + File.separator + ConstantKeys.PIC + "/subtitle.png";
        return pic;
    }

    // 获取合并视频输出路径（拍摄后合并的MP4作为源视频在特效编辑页面进行各种处理）
    public static String getSubVideoPath(Context context) {
        String path = getCacheDir(context) + File.separator + ConstantKeys.OUTPUT;
        File dic = new File(path);
        if (!dic.exists()) {
            dic.mkdir();
        }
        String output = path + File.separator + ConstantProperties.SUBVIDEO_FILE;
        return output;
    }

    // 获取output目录
    public static String getOutPutPath(Context context) {
        String path = getCacheDir(context) + File.separator + ConstantKeys.OUTPUT;
        File dic = new File(path);
        if (!dic.exists()) {
            dic.mkdir();
        }
        return path;
    }

    //特效临时预览文件
    public static String getOutPutVideo(Context context){
        String path=getOutPutPath(context) + File.separator + ConstantProperties.PREVIEW_FILE;
        return path;
    }

    //用于保持界面常亮
    public static  void keepScreenAlive(Context context){
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "com.wole.lepai");
        wl.acquire();
        wl.release();
    }

    public static void setSexPic(Context context, int sex, TextView tv) {
        Drawable drawable = null;
        if (sex == 1) {
            drawable = context.getResources().getDrawable(R.drawable.ic_common_male);
        } else if (sex == 2) {
            drawable = context.getResources().getDrawable(R.drawable.ic_common_female);
        } else {
            tv.setCompoundDrawables(null, null, null, null);
            return;
        }
        drawable.setBounds(0, 0, DipPixUtil.dip2px(context, 18), DipPixUtil.dip2px(context, 18));
        tv.setCompoundDrawables(null, null, drawable, null);
    }

    public static void setSexPic(Context mContext, int sex, TextView tv, int marginID) {
        Drawable drawable = null;
        if (sex == 2) {
            drawable = mContext.getResources().getDrawable(R.drawable.ic_common_female);
        } else if (sex == 1) {
            drawable = mContext.getResources().getDrawable(R.drawable.ic_common_male);
        } else {
            tv.setCompoundDrawables(null, null, null, null);
            return;
        }
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        tv.setCompoundDrawablePadding((int) mContext.getResources().getDimension(marginID));
        tv.setCompoundDrawables(null, null, drawable, null);
    }

    public static int random(int min, int max) {

        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    public static void clearTempVideo(Context context) {
        String path = Tools.getTempPath(context);
        File tempDictory = new File(path);
        if (tempDictory.exists()) {
            if (tempDictory.isDirectory()) {
                // 清空temp目录下所有文件text 和 mp4
                File[] fileList = tempDictory.listFiles();
                for (int i = 0; i < fileList.length; i++) {
                    File file = fileList[i];
                    file.delete();
                    Trace.d(TAG, "删除文件-->>" + i);
                }
            }
        }
    }

    public static String StringFilter(String str) throws PatternSyntaxException {
        str = str.replaceAll("【", "[").replaceAll("】", "]").replaceAll("！", "!");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    // 删除文件或者文件目录（文件夹里有文件也可删除）
    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }

    // 检测是否有拍照权限
    public static boolean checkCameraPermission(Context context) {

        try {
            context.enforceCallingPermission("android.permission.RECORD_AUDIO", "audio");
            context.enforceCallingPermission("android.permission.CAMERA", "camera");
            return true;
        } catch (SecurityException e) {
            return false;
        }

        /*
         * int audio_flag=context.checkCallingPermission(
         * "android.permission.RECORD_AUDIO"); int
         * camera_flag=context.checkCallingPermission
         * ("android.permission.CAMERA"); boolean has_audio=false; boolean
         * has_camera=false; if(audio_flag== PackageManager.PERMISSION_DENIED ){
         * Trace.d(TAG,"拒绝了录音权限"); has_audio=false; } else
         * if(audio_flag==PackageManager.PERMISSION_GRANTED ){
         * Trace.d(TAG,"同意了录音权限"); has_audio=true; }
         * if(camera_flag==PackageManager.PERMISSION_DENIED ){
         * Trace.d(TAG,"拒绝了Camera权限"); has_camera=false; } else
         * if(camera_flag==PackageManager.PERMISSION_GRANTED ){
         * Trace.d(TAG,"同意了Camera权限"); has_camera=true; }
         * if(has_audio&&has_camera){ Trace.d(TAG,"都同意了"); return true; }else{
         * return false; }
         */
    }

    public static Bitmap getBigBitmap(int res, Context context, int width, int height) {
        BitmapFactory.Options opt = getBigBitmapOPtion(res, context, width, height);
        InputStream isp = context.getResources().openRawResource(res);
        Bitmap bitmap = BitmapFactory.decodeStream(isp, null, opt);
        return bitmap;
    }

    public static Bitmap getBigBitmap(int res, Context context) {
        BitmapFactory.Options opt = getBigBitmapOPtion(res, context, -1, -1);
        InputStream isp = context.getResources().openRawResource(res);
        Bitmap bitmap = BitmapFactory.decodeStream(isp, null, opt);
        return bitmap;
    }
    
    public static Bitmap getBigBitmap(int res, Context context,boolean noScale) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inDensity = context.getResources().getDisplayMetrics().densityDpi;
        opt.inTargetDensity = opt.inDensity;
        opt.inScaled = false;
        opt.inJustDecodeBounds = false;
        InputStream isp = context.getResources().openRawResource(res);
        Bitmap bitmap = BitmapFactory.decodeStream(isp, null, opt);
        return bitmap;
    }

    private static BitmapFactory.Options getBigBitmapOPtion(int res, Context context, int width, int height) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        if (width != -1) {
            opt.inJustDecodeBounds = true;
            InputStream isp = context.getResources().openRawResource(res);
            Bitmap bitmap = BitmapFactory.decodeStream(isp, null, opt);
            // 284 358
            int outWidth = opt.outWidth;
            int outHeight = opt.outHeight;
            width = DipPixUtil.dip2px(context, 284);
            height = DipPixUtil.dip2px(context, 358);
            if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {
              //  int sampleSize = (outWidth / width + outHeight / height) / 2;
                float wScale = Float.valueOf(outWidth) / width;
                float hScale = Float.valueOf(outHeight) / height;
                float max =  Math.max(wScale, hScale);
                opt.inSampleSize = (int) (max == 0 ? 1 : max);
            }
        }
        opt.inDensity = context.getResources().getDisplayMetrics().densityDpi;
        opt.inScaled = true;
        opt.inJustDecodeBounds = false;
        return opt;
    }

    public static void rejustBitmap() {

    }
    
    public static boolean isServiceRunning(Context context,String className) {
        ActivityManager myManager = (ActivityManager) context.getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(className)) {
                return true;
            }
        }
        return false;
    }
}
