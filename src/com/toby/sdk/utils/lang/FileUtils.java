package com.toby.sdk.utils.lang;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import com.toby.sdk.utils.android.Trace;
import java.io.*;
import java.util.ArrayList;

public class FileUtils {
    public static final int BUFSIZE = 2048;

    public static boolean prepareDir(String filePath) {
        if (!filePath.endsWith(File.separator)) {
            return false;
        }
        File file = new File(filePath);
        if (file.exists() || file.mkdirs()) {
            Log.i("prepareDir", "create folder:" + filePath + ",result:true");
            return true;
        } else {
            Log.i("prepareDir", "create folder:" + filePath + ",result:false");
            return false;
        }
    }

    public static boolean createFile(String filePath) {
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        if (parentFile.exists() == false) {
            prepareDir(parentFile.getAbsolutePath());
        }
        try {
            boolean result = file.createNewFile();
            Log.i("createFile", "create folder:" + filePath + ",result:" + result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("createFile", "create folder:" + filePath + ",result:false");
            return false;
        }
    }

    public static long getFileDirSize(File file) {
        long size = 0;
        if (!file.exists())
            return 0;
        File[] flist = file.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileDirSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    public static String getFileSize(long lvalue) {
        float ftmp = (float) lvalue;
        if (lvalue > 0) {
            // float res = lvalue / 1048576;
            java.text.DecimalFormat df = new java.text.DecimalFormat("0.0");
            float size = ftmp / 1048576;
            if (size < 0.1) {
                return "0.1M";
            } else {
                Trace.i("FileSize", "divide=" + (ftmp / 1048576) + " format=" + df.format(ftmp / 1048576));
                return String.valueOf(df.format(ftmp / 1048576)) + "M";
            }
        }
        return "0.0M";
    }

    /**
     * 将带file://的路径从/sdcard开始截取
     * 
     * @param path
     * @return
     */
    public static String formatPath(String path) {
        if (path.indexOf("/sd") > -1) {
            return path.substring(path.indexOf("/sd"));
        }
        return path;
    }

    public static byte[] fileToByteArray(Context context, String path) {
        InputStream is = null;
        byte[] data = null;
        try {
            File file = null;
            if (ContentResolver.SCHEME_CONTENT.equals(Uri.parse(path)
                    .getScheme())) {
                ContentResolver cr = context.getContentResolver();
                Uri imageUri = Uri.parse(path);
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = cr
                        .query(imageUri, projection, null, null, null);
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                file = new File(cursor.getString(column_index));
            } else if (ContentResolver.SCHEME_FILE.equals(Uri.parse(path)
                    .getScheme())) {
                file = new File(Uri.parse(path).getPath());
            } else {
                file = new File(path);
            }
            is = new FileInputStream(file);
            data = new byte[is.available()];
            int i = 0;
            int temp = 0;
            while ((temp = is.read()) != -1) {
                data[i] = (byte) temp;
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static Uri getUri(File file) {
        if (file != null) {
            return Uri.fromFile(file);
        }
        return null;
    }

    public static File getFile(Uri uri) {
        if (uri != null) {
            String filepath = uri.getPath();
            if (filepath != null) {
                return new File(filepath);
            }
        }
        return null;
    }

    public static File getFile(String curdir, String file) {
        String separator = "/";
        if (curdir.endsWith("/")) {
            separator = "";
        }
        File clickedFile = new File(curdir + separator + file);
        return clickedFile;
    }

    public static File contentUriToFile(Context context, Uri uri) {
        File file = null;
        if (uri != null) {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor actualimagecursor = ((Activity) context).managedQuery(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();

            String img_path = actualimagecursor.getString(actual_image_column_index);
            file = new File(img_path);
        }
        return file;
    }

    public static InputStream getFileInputStream(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return is;
    }

    public static void deleteMkdir(File file){
        if(file != null && file.exists()){
            if(file.isDirectory()){
                File[] files = file.listFiles();
                for(File f : files){
                    if(f.isDirectory()){
                        deleteMkdir(f);
                    }else{
                        deleteFile(f);
                    }
                }
            }else{
                deleteFile(file);
            }
        }
    }
    
    public static boolean deleteFile(File file) {
        boolean delete = false;
        try {
            delete = file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delete;
    }

    public static boolean deleteFile(String path) {
        boolean delete = false;
        try {
            File file = new File(path);
            if (file.exists()) {
                delete = file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delete;
    }

    public static File getFile(File curdir, String file) {
        return getFile(curdir.getAbsolutePath(), file);
    }

    public static File getPathWithoutFilename(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                // no file to be split off. Return everything
                return file;
            }
            else {
                String filename = file.getName();
                String filepath = file.getAbsolutePath();

                // Construct path without file name.
                String pathwithoutname = filepath.substring(0, filepath.length() - filename.length());
                if (pathwithoutname.endsWith("/")) {
                    pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
                }
                return new File(pathwithoutname);
            }
        }
        return null;
    }

    public static byte[] getByteArrayByFile(File file) {
        BufferedInputStream stream = null;
        int size = (int) file.length();
        byte[] buffer = new byte[size];
        try {
            FileInputStream in = new FileInputStream(file);
            stream = new BufferedInputStream(in);
            stream.read(buffer);
        } catch (Exception e) {
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer;
    }

    /**
     * @param f - 指定的目录
     * @param buff
     */
    public static void saveByteToFile(File f, byte[] buff) {
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            fOut.write(buff);
            fOut.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveByteToPath(String path, ArrayList<byte[]> buffs) {
        FileOutputStream fOut = null;
        File f = new File(path);
        try {
            fOut = new FileOutputStream(f);
            for (int i = 0; i < buffs.size(); i++)
            {
                fOut.write(buffs.get(i));
            }
            fOut.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveByteToFile(File f, ArrayList<byte[]> buffs) {
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            for (int i = 0; i < buffs.size(); i++)
            {
                fOut.write(buffs.get(i));
            }
            fOut.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存图片到SDCard的默认路径下. 屏刷新图库
     * 
     * @param context - eg. hello.jpg
     * @param f
     * @throws java.io.IOException
     */
    public static void saveByteToSDCard(Context context, File f, byte[] buff)
            throws IOException {
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            fOut.write(buff);
            fOut.flush();
            refreshAlbum(context, f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存图片到指定路径下
     */
    public static void saveByteToData(Context context, Bitmap bitmap, String fileName)
            throws IOException {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void refreshAlbum(Context context, File imageFile) {
        if (context != null) {
            Uri localUri = Uri.fromFile(imageFile);
            Intent localIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            context.sendBroadcast(localIntent);
        }
    }

    public static void copyFile(String fileFromPath, String fileToPath)
            throws Exception {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(fileFromPath);
            out = new FileOutputStream(fileToPath);
            int length = in.available();
            int len = (length % BUFSIZE == 0) ? (length / BUFSIZE) : (length / BUFSIZE + 1);
            byte[] temp = new byte[BUFSIZE];
            for (int i = 0; i < len; i++) {
                in.read(temp);
                out.write(temp);
            }
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
    }



}
