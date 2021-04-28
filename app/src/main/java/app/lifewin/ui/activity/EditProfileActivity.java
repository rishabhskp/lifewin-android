package app.lifewin.ui.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import app.lifewin.R;
import app.lifewin.dialog.DialogUtils;
import app.lifewin.interfaces.IImagePickOption;
import app.lifewin.parse.interfaces.IParseQueryResult;
import app.lifewin.parse.utils.ParseUtils;
import app.lifewin.preferences.MySharedPreference;
import app.lifewin.utils.ImageDisplay;
import app.lifewin.utils.NetworkStatus;
import app.lifewin.utils.ProgressDialogUtil;
import app.lifewin.utils.TakeImageClass;
import app.lifewin.utils.Toaster;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class EditProfileActivity extends Activity implements IImagePickOption,IParseQueryResult {


    @InjectView(R.id.ed_name)
    EditText edName;

    @InjectView(R.id.iv_image)
    ImageView ivImage;

    private boolean isEditTextEnabled;
    private String mProfileImagePath;
    private TakeImageClass mTakeImageClass;
    private ContentResolver mContentResolver;
    private Bitmap mBitmap;
    private String imagePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.inject(this);
        mTakeImageClass = new TakeImageClass(EditProfileActivity.this);
        edName.setText(MySharedPreference.getInstance(getApplicationContext()).getName());
        edName.setEnabled(false);
        mContentResolver = getContentResolver();
        if(TextUtils.isEmpty(MySharedPreference.getInstance(EditProfileActivity.this).getUserProfileImage())){
            imagePath = MySharedPreference.getInstance(EditProfileActivity.this).getUserProfileImageF();
        }else{
            imagePath = MySharedPreference.getInstance(EditProfileActivity.this).getUserProfileImage();
        }
        new ImageDisplay().scaleAndLoadBitmap(EditProfileActivity.this, imagePath, ivImage);
    }


    @OnClick(R.id.iv_back)
    public void onBackPress() {
        finish();
    }


    @OnClick(R.id.iv_edit_name)
    public void onEditNameClick() {
        if (isEditTextEnabled) {
            if(edName.getText().toString().trim().length()>0) {
                HashMap<String, Object> mUpdateValue = new HashMap<String, Object>();
                mUpdateValue.put("name", edName.getText().toString().trim());
                onUpdateParseDatabase(mUpdateValue);
            }else{
                edName.setText(MySharedPreference.getInstance(EditProfileActivity.this).getName());
            }
        }
        isEditTextEnabled = !isEditTextEnabled;
        edName.setEnabled(isEditTextEnabled);

    }
    private void onUpdateParseDatabase(HashMap<String, Object> mUpdateValue) {
        if (NetworkStatus.isInternetOn(EditProfileActivity.this)) {
            ProgressDialogUtil.getInstance().showProgressDialog(EditProfileActivity.this);
            ParseUtils.getInstance().onUpdateParseUser(EditProfileActivity.this, mUpdateValue);
        } else {
            Toaster.show(EditProfileActivity.this, R.string.err_internet_connection_error);
        }
    }

    private void onUpdateUserImage(String mProfileImagePath) {
        if (NetworkStatus.isInternetOn(EditProfileActivity.this)) {
            ProgressDialogUtil.getInstance().showProgressDialog(EditProfileActivity.this);
            ParseUtils.getInstance().onUploadUserImagePQuery(EditProfileActivity.this, mProfileImagePath);
        } else {
            Toaster.show(EditProfileActivity.this, R.string.err_internet_connection_error);
        }
    }

    @OnClick(R.id.iv_edit_image)
    public void onEditImageClick() {
        onImageClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ivImage.setImageBitmap(null);
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap=null;
        }

        ButterKnife.reset(this);
    }


    @Override
    public void onImagePickOption(int option) {
        if (option == 1) {
            mTakeImageClass.openGallery();
        } else if (option == 2) {
            mTakeImageClass.takePicture();
        }
    }

    /**
     * Handle the callback when user click on image for pick image from gallery or camera
     */
    protected void onImageClick() {
        new DialogUtils().showImagePickDialog(EditProfileActivity.this, EditProfileActivity.this, getString(R.string.txt_signup_header_title));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TakeImageClass.REQUEST_CODE_TAKE_PICTURE || requestCode == TakeImageClass.REQUEST_CODE_GALLERY || requestCode == TakeImageClass.REQUEST_CODE_CROP_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case TakeImageClass.REQUEST_CODE_TAKE_PICTURE:
                        mTakeImageClass.onActivityResult(requestCode, resultCode, data);
                        break;
                    case TakeImageClass.REQUEST_CODE_GALLERY:
                        mTakeImageClass.onActivityResult(requestCode, resultCode, data);
                        break;
                    case TakeImageClass.REQUEST_CODE_CROP_IMAGE:
                        mTakeImageClass.onActivityResult(requestCode, resultCode, data);
                        if (TakeImageClass.sImagePath != null) {
                            mProfileImagePath = TakeImageClass.sImagePath;
                            mBitmap=BitmapFactory.decodeFile(mProfileImagePath);
                            if (ivImage != null) {
                                ivImage.setImageBitmap(mBitmap);
                            }
//                            getBitmap(mProfileImagePath,500);
                            onUpdateUserImage(mProfileImagePath);

                        }

                        break;
                }
            }
        }
    }

    @Override
    public void onParseQuerySuccess(Object obj) {
        ProgressDialogUtil.getInstance().dismissProgressDialog();
        if (obj instanceof String) {
            try {
                JSONObject mJson = new JSONObject((String) obj);
                if (mJson.getBoolean("status")) {
                    String key = mJson.getString("key");
                    String value = mJson.getString("value");
                    if (key.equalsIgnoreCase("name")) {
                        MySharedPreference.getInstance(getApplicationContext()).setName(value);
                    }else if(key.equalsIgnoreCase("user_image")){
                        MySharedPreference.getInstance(EditProfileActivity.this).setUserProfileImage(mProfileImagePath);
                    }
                }
                LocalBroadcastManager.getInstance(EditProfileActivity.this).sendBroadcast(new Intent("profile_update"));
                Toaster.show(EditProfileActivity.this, "Updated Successfuly");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onParseQueryFail(Object obj) {
        ProgressDialogUtil.getInstance().dismissProgressDialog();
        if(obj instanceof ParseException){
            Toast.makeText(EditProfileActivity.this, ((ParseException) obj).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }






}


