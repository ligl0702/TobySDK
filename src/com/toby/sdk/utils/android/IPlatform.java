package com.android.camera.util;

import android.content.ContentValues;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;

/**
 * Created by guoliangli on 2016/1/20 0020.
 */
public interface IPlatform {

    public String getDefaultPath();

    public void setCameraMode(Camera.Parameters parameters);
    public void setZSDMode(Camera.Parameters parameters);

    public CamcorderProfile createProfile(int mCameraId, int quality) ;

    public void setVideoBitOffSet(MediaRecorder mMediaRecorder);

    public void setSlowMotion(MediaRecorder mMediaRecorder, int speed);

    public void putVideoValues(ContentValues mCurrentVideoValues, int speed) ;

    public void pauseVideo(MediaRecorder mMediaRecorder);

    public void enableRecordingSound(Camera.Parameters mParameters, boolean enable);
}
