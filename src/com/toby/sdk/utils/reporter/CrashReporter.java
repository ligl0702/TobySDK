package com.toby.sdk.utils.reporter;
import android.text.format.DateFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

/***
 * @description 此类用于程序crash崩溃，将其写入txt文本
 * @author toby
 * @time 2014/11/26
 */


/***
 * 主要用法
 *
 * private void initCrashReporter(){
            mCrashReporter = CrashReporter.getInstance(mUserEnvCreator.ABS_LEPAI_CRASH_DIR);
            mCrashReporter.attachCurrentThreadUncatchExceptionHandler();
 }
 *
 */
public class CrashReporter extends BaseReporter {

    static CrashReporter mCrashReporterInstance;

    String mCrashDirPath;

    private CrashReporter(String crashDirPath) {
        mCrashDirPath = crashDirPath;
        if (!mCrashDirPath.endsWith(File.separator)) {
            mCrashDirPath += File.separator;
        }
    }

    public static final synchronized CrashReporter getInstance(String crashDirPath) {
        if (mCrashReporterInstance == null) {
            mCrashReporterInstance = new CrashReporter(crashDirPath);
        }
        return mCrashReporterInstance;
    }

    private UncaughtExceptionHandler mAttachHandler = new UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            ex.printStackTrace();
            Date now = new Date();
            String dirName = mCrashDirPath;
            File dir = new File(dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = dirName + "/" + DateFormat.format("yyyyMMdd_aahhmmss", now) + ".txt";
            PrintWriter ps = null;
            try {
                ps = new PrintWriter(fileName);
            } catch (FileNotFoundException e) {
                return;
            }
            String msg = String.format("crashed by uncaught exception, thread: %s\r\n",
                    thread.getName());
            ps.write(msg);
            ex.printStackTrace(ps);
            ps.flush();
            ps.close();
            System.exit(0);
        }
    };

    private UncaughtExceptionHandler mUnattachHandler = new UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            System.exit(0);
        }
    };

    public void attachCurrentThreadUncatchExceptionHandler() {

        Thread.currentThread().setUncaughtExceptionHandler(mAttachHandler);
    }

    public void unattachCurrentThreadUncatchExceptionHandler() {

        Thread.currentThread().setUncaughtExceptionHandler(mUnattachHandler);
    }

}
