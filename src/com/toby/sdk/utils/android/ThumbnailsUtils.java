package com.android.camera.util;

import com.android.camera.ui.CircleRotateImageView;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.View;
import com.android.camera.Storage;
import android.os.Environment;
import com.android.camera2.R;

public class ThumbnailsUtils {
    private static final String TAG = "CAM_ThumbnailsUtils";

    private boolean mIsRegistedObserver = false;
    private ContentResolver mContentResolver;
    private ThumbContentObserver mThumbContentObserver;
    private CircleRotateImageView mRotatePreviewThumb;
    private boolean mShowPreviewThumb = true;
    private static ThumbnailsUtils mThumbnailsUtils;
    public Uri mPreviewPicUri;
    public String mResourceType;

    public static synchronized ThumbnailsUtils getInstance(ContentResolver mContentResolver,
            CircleRotateImageView mRotatePreviewThumb) {

        if (mThumbnailsUtils == null || mThumbnailsUtils.mRotatePreviewThumb != mRotatePreviewThumb) {
            mThumbnailsUtils = new ThumbnailsUtils(mContentResolver, mRotatePreviewThumb);
        }

        return mThumbnailsUtils;
    }

    public static synchronized ThumbnailsUtils getInstance() {
        return mThumbnailsUtils;
    }

    private ThumbnailsUtils(ContentResolver mContentResolver, CircleRotateImageView mRotatePreviewThumb) {
        super();
        this.mContentResolver = mContentResolver;
        this.mThumbContentObserver = new ThumbContentObserver();
        this.mRotatePreviewThumb = mRotatePreviewThumb;
    }

    public void setThumbnailsBitmap() {
        new ThumbnailsAsycTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mContentResolver);
    }

    public boolean showPreviewThumb(){
          return mShowPreviewThumb;
    }

    public boolean isRegistedContentObserver() {
        return mIsRegistedObserver;
    }

    public void registContentObserver(Uri thumbUri) {
        Log.d(TAG, "registContentObserver");
        mContentResolver.registerContentObserver(thumbUri, true, mThumbContentObserver);
        mIsRegistedObserver = true;
    }

    public void unRegistContentObserver() {
        Log.d(TAG, "unRegistContentObserver");
        mContentResolver.unregisterContentObserver(mThumbContentObserver);
        mIsRegistedObserver = false;
    }

    /**
     * Query the latest photo or video and get its thumbnails.
     */
    private class ThumbnailsAsycTask extends AsyncTask<ContentResolver, Void, Bitmap> {
        //Uri baseUri = MediaStore.Files.getContentUri("external");
        Uri baseImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri baseVideoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Uri baseUri = baseImageUri;
        String MEDIA_PROJECTION[] = {
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.MIME_TYPE,
                ImageColumns.ORIENTATION };


        String MEDIA_PROJECTION_VIDEO[] = {
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.MIME_TYPE
        };

//        String selection = String.format("%s = ? And mime_type is not null",
//                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME);
//        String selectionArgs[] = { "Camera"  };

        String selection = CommonUtils.getFilterWhereClause();
        String selectionArgs[] = null;
        //String sortOrder = "_id desc limit 1";
        String sortOrder=MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC, "
                + "_id desc limit 1";
        Cursor mCursorImage = null;
        Cursor mCursorVideo = null;
        Cursor mCursor = null;

        Uri thumbUri;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mIsRegistedObserver) {
                unRegistContentObserver();
            }
        }

        @Override
        protected Bitmap doInBackground(ContentResolver... params) {

            //mCursor = params[0].query(baseUri, MEDIA_PROJECTION, selection,
            //        selectionArgs, sortOrder);

            mCursorImage = params[0].query(baseImageUri, MEDIA_PROJECTION, selection, selectionArgs, sortOrder);
            mCursorVideo = params[0].query(baseVideoUri, MEDIA_PROJECTION_VIDEO, selection, selectionArgs, sortOrder);
            mPreviewPicUri = null;
            if(mCursorImage!=null && mCursorVideo==null){
                 mCursor = mCursorImage;
                 baseUri = baseImageUri;
            } else if(mCursorVideo!=null && mCursorImage==null){
                 mCursor = mCursorVideo;
                 baseUri = baseVideoUri;
            } else if(mCursorVideo!=null && mCursorImage!=null){
                int id_video = 0;
                if(mCursorVideo.getCount()>0){
                     mCursorVideo.moveToLast();
                     id_video = mCursorVideo.getInt(mCursorVideo.getColumnIndex(MediaStore.MediaColumns._ID));
                }
                int id_image = 0;
                if(mCursorImage.getCount()>0){
                    mCursorImage.moveToLast();
                    id_image = mCursorImage.getInt(mCursorImage.getColumnIndex(MediaStore.MediaColumns._ID));
                }
                if(id_video > id_image){
                   mCursor = mCursorVideo;
                   baseUri = baseVideoUri;
                } else {
                   mCursor = mCursorImage;
                   baseUri = baseImageUri;
                }
            }

            if (mCursor != null && mCursor.moveToLast()) {
                String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                String mimeType = mCursor.getString(mCursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
                String id = mCursor.getString(mCursor.getColumnIndex(MediaStore.MediaColumns._ID));
                thumbUri = Uri.parse(baseUri.toString() + "/" + id);

                    if (mimeType.startsWith("video")) {
                        Bitmap videoBitmap = BitmapUtils.createVideoThumbnail(path);
                        mPreviewPicUri = thumbUri;
                        mResourceType = mimeType;
                        return videoBitmap;
                    } else if (mimeType.startsWith("image")) {
                        int  orientation=mCursor.getInt(mCursor.getColumnIndex(ImageColumns.ORIENTATION));
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4;
                        Bitmap imageBitmap = BitmapFactory.decodeFile(path, options);
                        imageBitmap=CameraUtil.rotate(imageBitmap,orientation);
                        mPreviewPicUri = thumbUri;
                        mResourceType = mimeType;
                        return imageBitmap;
                    }

            }
            Log.d(TAG, "No any photo or video found");
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if(result!=null) {
                mRotatePreviewThumb.setBitmap(result);
            }
            else {
                mRotatePreviewThumb.setImageResource(R.drawable.bg_btn);
            }
            if(thumbUri!=null){
                registContentObserver(thumbUri);
            } else if(baseUri!=null){
                registContentObserver(baseUri);
            } else {
                registContentObserver(baseImageUri);
            }

            if (mCursorImage != null) {
                mCursorImage.close();
            }

            if (mCursorVideo != null) {
                mCursorVideo.close();
            }
        }
    }

    /**
     * Listening to the changes to the latest image and video data. eg: (when
     * Camera is running on background, user delete the latest image or video
     * data, when Camera app resume to foreground, the thumbnails UI should also
     * change.)
     */
    private class ThumbContentObserver extends ContentObserver {

        public ThumbContentObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "ThumbContent onChanged...");
           // new ThumbnailsAsycTask().execute(mContentResolver);
        }
    }
}
