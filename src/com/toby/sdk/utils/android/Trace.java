package com.toby.sdk.utils.android;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * Log
 */
public class Trace {

    // Constants are the same as in android.util.Log, levels
    public static final int ERROR = 6;
    public static final int WARN = 5;
    public static final int INFO = 4;
    public static final int DEBUG = 3;
    public static final int VERBOSE = 2;

    public static boolean _isLog = true;
    private static String TAG = "56_lepai";
    
    private static boolean WRITE_TO_FILE = false;//加入把log写入SD卡的功能
    private static String MYLOG_PATH_SDCARD_DIR="/sdcard/";// 日志文件在sdcard中的路径
    private static SimpleDateFormat myLogSdf = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");// 日志的输出格式
    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式
    private static String MYLOGFILEName = "Log.txt";// 本类输出的日志文件名称


    public static void i(String tag, String message) {
        trace(tag, message, INFO);
    }

    public static void w(String tag, String message) {
        trace(tag, message, WARN);
    }

    public static void e(String tag, String message) {
        trace(tag, message, ERROR);
    }

    public static void d(String tag, String message) {
        trace(tag, message, DEBUG);
    }

    public static void i(String message) {
        i(TAG, message);
    }

    public static void w(String message) {
        w(TAG, message);
    }

    public static void e(String message) {
        e(TAG, message);
    }

    public static void d(String message) {
        d(TAG, message);
    }

    public static void trace(String tag, String message, int level) {
        // if you want to choose print some logs, please add "&&level==" after
        // _isLog
        if (_isLog) {
            long sec = (System.currentTimeMillis() / 1000) % 1000;
            StringBuilder b = new StringBuilder("[")
                    .append(Thread.currentThread().getName()).append("] ")
                    .append("@").append(sec).append(" ").append(message);

            Log.println(level, tag, b.toString());
        }
//        if (WRITE_TO_FILE) {
//            writeLogtoFile(tag, message);
//        }
    }
    
    /**
     * 把日志写入SD卡中，方便跟进Log，或者某些无法在LogCat中抓取的，可在Sd卡中查看
     * @return
     * **/
    private static void writeLogtoFile(String tag, String text) {// 新建或打开日志文件
        Date nowtime = new Date();
        String needWriteFiel = logfile.format(nowtime);
        String needWriteMessage = myLogSdf.format(nowtime) + "    " + tag + "    " + text;
        File file = new File(MYLOG_PATH_SDCARD_DIR, needWriteFiel
                + MYLOGFILEName);
        try {
            FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
