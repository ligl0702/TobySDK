package com.android.camera.util;

import android.os.Build;

/**
 * Created by guoliangli on 2016/1/15.
 */
public class CameraPlatformFactory {

    public  static IPlatform creator(){
        return new PlatFormFactory();
    }
}
