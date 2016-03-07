package com.android.camera.util;

/**
 * Created by guoliangli on 2015/12/29.
 */
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.WindowManager;

public class BrightnessTools {

    private static final int MIN_BRIGHTNESS=130;

    public static boolean isAutoBrightness(ContentResolver aContentResolver) {

        boolean automicBrightness = false;

        try {

            automicBrightness = Settings.System.getInt(aContentResolver,

                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;

        } catch (SettingNotFoundException e)

        {

            e.printStackTrace();

        }

        return automicBrightness;
    }

    public static int getScreenBrightness(Activity activity) {

        int nowBrightnessValue = 0;

        ContentResolver resolver = activity.getContentResolver();

        try {

            nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);

        } catch (Exception e) {

            e.printStackTrace();

        }

        return nowBrightnessValue;
    }


    public static void setBrightness(Activity activity, int brightness) {

        // Settings.System.putInt(activity.getContentResolver(),

        // Settings.System.SCREEN_BRIGHTNESS_MODE,

        // Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();

        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);

        activity.getWindow().setAttributes(lp);
    }


    public static void stopAutoBrightness(Activity activity) {

        Settings.System.putInt(activity.getContentResolver(),

                Settings.System.SCREEN_BRIGHTNESS_MODE,

                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }


    public static void startAutoBrightness(Activity activity) {

        Settings.System.putInt(activity.getContentResolver(),

                Settings.System.SCREEN_BRIGHTNESS_MODE,

                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);

    }


    public static void saveBrightness(ContentResolver resolver, int brightness) {

        Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");

        android.provider.Settings.System.putInt(resolver, "screen_brightness", brightness);

        // resolver.registerContentObserver(uri, true, myContentObserver);

        resolver.notifyChange(uri, null);
    }


    public static void setScreenBrightness(Activity mActivity,int brightness){
        if(BrightnessTools.isAutoBrightness(mActivity.getContentResolver())){
            stopAutoBrightness(mActivity);
        }
        setBrightness(mActivity, brightness);
    }

    public static void BrightnessAdjusting(Activity mActivity,int currentBrightness){

        if(currentBrightness<MIN_BRIGHTNESS){
            setScreenBrightness(mActivity,MIN_BRIGHTNESS);
        }
    }

    public static void recoverBrightness(Activity mActivity,boolean isBrightObserverChanged,boolean isAutoBrightness,int mCurrentBrightness){
        //If the user to manually adjust the brightness,don't  processing
        if(!isBrightObserverChanged){
            if(isAutoBrightness){
                startAutoBrightness(mActivity);
            }else{
                setBrightness(mActivity, mCurrentBrightness);
            }
        }
    }
}