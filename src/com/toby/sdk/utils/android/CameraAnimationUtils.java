package com.android.camera.util;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

/**
 * Created by guoliangli on 2015/7/28.
 */
public class CameraAnimationUtils {
    public static void showTopSettingsAnimation(View rootView,boolean isCaptureIntent) {
        if (rootView == null)
            return;
//        ScaleAnimation topAnimationShow = new ScaleAnimation(0.1f, 1, 0.1f, 1,
//                Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//        topAnimationShow.setDuration(300);
//        rootView.startAnimation(topAnimationShow);
        RotateAnimation top = new RotateAnimation(90.0f, 0.0f, Animation.RELATIVE_TO_SELF,1.0f, Animation.RELATIVE_TO_SELF,1.0f);
        top.setDuration(300);
        rootView.startAnimation(top);
        rootView.setVisibility(isCaptureIntent?View.GONE:View.VISIBLE);
    }

    public static void showTopSettingsAnimation(View rootView) {
        if (rootView == null)
            return;
//        ScaleAnimation topAnimationShow = new ScaleAnimation(0.1f, 1, 0.1f, 1,
//                Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//        topAnimationShow.setDuration(300);
//        rootView.startAnimation(topAnimationShow);
        RotateAnimation top = new RotateAnimation(90.0f, 0.0f, Animation.RELATIVE_TO_SELF,1.0f, Animation.RELATIVE_TO_SELF,1.0f);
        top.setDuration(300);
        rootView.startAnimation(top);
        rootView.setVisibility(View.VISIBLE);
    }

    public static void dismissViewAnimation(View rootView) {
        if (rootView == null)
            return;
//        ScaleAnimation topAnimationDis = new ScaleAnimation(
//                1f, 0.1f, 1f, 0.1f,
//                Animation.RELATIVE_TO_SELF, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f);
//        topAnimationDis.setDuration(300);
//        rootView.startAnimation(topAnimationDis);
        RotateAnimation top = new RotateAnimation(0.0f, 90.0f, Animation.RELATIVE_TO_SELF,1.0f, Animation.RELATIVE_TO_SELF,1.0f);
        top.setDuration(300);
        rootView.startAnimation(top);
        rootView.setVisibility(View.GONE);
    }
}
