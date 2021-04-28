package app.lifewin.utils;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import app.lifewin.constants.AppConstants;
import app.lifewin.cropper.CropImageActivity;


/**
 * This is Activity to initiate image taking either from gallery or by camera
 *
 */
public class TakeImageClass {

    public static final String TAG = "LIFEWIN";
    public static final int REQUEST_CODE_GALLERY = 15;
    public static final int REQUEST_CODE_TAKE_PICTURE = 16;
    public static final int REQUEST_CODE_CROP_IMAGE = 17;
    public static String TEMP_PHOTO_FILE_NAME = "profile_pic.jpg";
    public static String sImagePath = null;
    private Activity mActivity;
    private File mFileTemp;
    private Uri mImageCaptureUri;

    public TakeImageClass(Activity activity) {
        mActivity = activity;
        String state = Environment.getExternalStorageState();
        sImagePath = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            TEMP_PHOTO_FILE_NAME = System.currentTimeMillis() + "_image.jpg";
            mFileTemp = new File(AppConstants.CHILD_FOLDER_PROFILE_IMAGES, TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(activity.getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }
    }

    public TakeImageClass(Activity activity, String filePath) {
        mActivity = activity;
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public void setFilePath(String filePath) {
        sImagePath = filePath;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(filePath);
        }
    }

    public void takePicture() {
        TEMP_PHOTO_FILE_NAME = System.currentTimeMillis() + "_image.jpg";
        mFileTemp = new File(AppConstants.CHILD_FOLDER_PROFILE_IMAGES, TEMP_PHOTO_FILE_NAME);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mImageCaptureUri = Uri.fromFile(mFileTemp);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                intent.putExtra("return-data", true);
                mActivity.startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
                Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                scanIntent.setData(Uri.fromFile(mFileTemp));
                mActivity.sendBroadcast(scanIntent);
            } else {
                Toast.makeText(mActivity, "SD Card Is Not Available Can Not Capture the Image", Toast.LENGTH_LONG).show();
                /*
                 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
				 */
                //				mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
            }

        } catch (ActivityNotFoundException e) {

            e.printStackTrace();
            Logger.d(TAG, "cannot take picture");
        }
    }

    public void openGallery() {
        TEMP_PHOTO_FILE_NAME = System.currentTimeMillis() + "_image.jpg";
        mFileTemp = new File(AppConstants.CHILD_FOLDER_PROFILE_IMAGES, TEMP_PHOTO_FILE_NAME);

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        mActivity.startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
    }

    private void startCropImage(int orientationInDegree) {

        Intent intent = new Intent(mActivity, CropImageActivity.class);
        intent.putExtra(CropImageActivity.IMAGE_PATH, mFileTemp.getPath());
        //intent.putExtra(CropImage.IMAGE_PATH, getRealPathFromURI(mImageCaptureUri));
        intent.putExtra(CropImageActivity.SCALE, true);
        intent.putExtra(CropImageActivity.ASPECT_X, 0);
        intent.putExtra(CropImageActivity.ASPECT_Y, 0);
        intent.putExtra(CropImageActivity.ORIENTATION_IN_DEGREES, orientationInDegree);
        mActivity.startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = mActivity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public int getCameraPhotoOrientation(Context context) {
        int rotate = 0;
        try {

            File imageFile = new File(mFileTemp.getPath());
            context.getContentResolver().notifyChange(mImageCaptureUri, null);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Logger.i("RotateImage", "Exif orientation: " + orientation);
            Logger.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.e("in on activity result of take image class", "requestCode=>>>>>" + requestCode);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_GALLERY:

                try {

                    InputStream inputStream = mActivity.getContentResolver().openInputStream(data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                    copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();
                    startCropImage(0);

                } catch (Exception e) {
                    Logger.e(TAG, "Error while creating temp file");
                    e.printStackTrace();
                }
                break;
            case REQUEST_CODE_TAKE_PICTURE:
                startCropImage(getCameraPhotoOrientation(mActivity));
                break;
            case REQUEST_CODE_CROP_IMAGE:
                Logger.e("Take Image Class ", "mTempFile Path =>" + mFileTemp.getPath());
                String path = data.getStringExtra(CropImageActivity.IMAGE_PATH);
                if (path == null) {
                    return;
                }
                Logger.e("Yake Image Class ", "mTempFile Path =>" + mFileTemp.getPath());
                TakeImageClass.sImagePath = mFileTemp.getPath();
                break;
        }
    }


}