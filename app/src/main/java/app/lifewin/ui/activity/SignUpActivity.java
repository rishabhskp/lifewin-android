package app.lifewin.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import app.lifewin.R;
import app.lifewin.constants.AppConstants;
import app.lifewin.interfaces.ISocialMediaLoginResponse;
import app.lifewin.model.app.SocialMediaUserBean;
import app.lifewin.parse.interfaces.IParseQueryResult;
import app.lifewin.parse.utils.ParseUtils;
import app.lifewin.preferences.MySharedPreference;
import app.lifewin.social_media.facebook.FacebookLogin;
import app.lifewin.social_media.gplus.GooglePlusLogin;
import app.lifewin.utils.EncryptUtils;
import app.lifewin.utils.HideKeyboard;
import app.lifewin.utils.NetworkStatus;
import app.lifewin.utils.ProgressDialogUtil;
import app.lifewin.utils.Toaster;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpActivity extends Activity implements IParseQueryResult, ISocialMediaLoginResponse {


    @InjectView(R.id.ed_email_id)
    EditText edEmailId;
    @InjectView(R.id.ed_password)
    EditText edPassword;
    @InjectView(R.id.ed_confirm_password)
    EditText edCPassword;

    @InjectView(R.id.ed_name)
    EditText edName;
    private FacebookLogin mFacebookLogin;
    private boolean isSocialFacebookConnect;
    private int iterationCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.inject(this);

    }

    @OnClick(R.id.iv_back)
    public void onBack(){
        finish();
    }
    @OnClick(R.id.tv_next)
    public void onRegistration() {
        boolean isValidate = onValidation();
        if (isValidate) {
            if (NetworkStatus.isInternetOn(getApplicationContext())) {
                HideKeyboard.keyboardHide(SignUpActivity.this);
                ProgressDialogUtil.getInstance().showProgressDialog(SignUpActivity.this);
                HashMap<String, Object> mValues = new HashMap<String, Object>();
                mValues.put("name", edName.getText().toString().trim());
                mValues.put("user_type", "app");
                mValues.put("is_setting", false);
                byte[] data = new byte[0];
                try {
                    data = edPassword.getText().toString().getBytes("UTF-8");
                    String base64 = Base64.encodeToString(data, Base64.DEFAULT);
                    mValues.put("token", base64);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                ParseUtils.getInstance().onSignUpPQuery(SignUpActivity.this, edEmailId.getText().toString().trim(), edPassword.getText().toString().trim(), edEmailId.getText().toString().trim(), mValues);
            } else {
                Toaster.show(getApplicationContext(), R.string.err_internet_connection_error);
            }
        }
    }

    /**
     * Method is used for validate the input fields.
     *
     * @return true if all validation satisfy otherwise false
     */
    private boolean onValidation() {
        String name = edName.getText().toString().trim();
        String emailId = edEmailId.getText().toString().trim();
        String password = edPassword.getText().toString().trim();
        String cpassword=edCPassword.getText().toString().trim();
        if (name.length() == 0) {
            edName.requestFocus();
            Toaster.show(SignUpActivity.this, R.string.val_name_empty);
            return false;
        } else if (emailId.length() == 0) {
            edEmailId.requestFocus();
            Toaster.show(SignUpActivity.this, R.string.val_login_email_empty);
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
            edEmailId.requestFocus();
            Toaster.show(SignUpActivity.this, R.string.val_login_email_invalid);
            return false;
        } else if (password.length() == 0 ) {
            edPassword.requestFocus();
            Toaster.show(SignUpActivity.this, R.string.val_login_password_empty);
            return false;
        } else if (password.length() < 4) {
            edPassword.requestFocus();
            Toaster.show(SignUpActivity.this, R.string.val_login_password_min_length);
            return false;
        } else if (cpassword.length() == 0 ) {
            edCPassword.requestFocus();
            Toaster.show(SignUpActivity.this, R.string.val_reenter_password_empty);
            return false;
        } else if (!cpassword.equalsIgnoreCase(password)) {
            edCPassword.requestFocus();
            Toaster.show(SignUpActivity.this, R.string.val_password_mismatch);
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }


    @Override
    public void onParseQuerySuccess(Object obj) {

        if (obj instanceof ParseUser) {
            //TODO Get info of the user
            ParseUser object = (ParseUser) obj;
            MySharedPreference.getInstance(getApplicationContext()).setUserId(object.getObjectId());
            MySharedPreference.getInstance(getApplicationContext()).setLogin(true);
            MySharedPreference.getInstance(getApplicationContext()).setName(object.getString("name"));
            MySharedPreference.getInstance(getApplicationContext()).setUserProfileImage(object.getString("user_image"));
            //TODO Getting the default settings...
            List<String> whereClause = new ArrayList<String>();
            whereClause.add(AppConstants.USER_ID);
            List<String> argsValues = new ArrayList<String>();
            argsValues.add(object.getObjectId());
            ParseUtils.getInstance().onFetchPQuery(SignUpActivity.this, AppConstants.USER_SETTINGS, whereClause, argsValues);

        }else if(obj instanceof String){
            ProgressDialogUtil.getInstance().dismissProgressDialog();
            try {
                JSONObject mJson=new JSONObject((String)obj);
                if(mJson.getInt("status")==200 && mJson.getString("type").equalsIgnoreCase("app")){
                    Toast.makeText(SignUpActivity.this, R.string.txt_success_signup, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getApplicationContext(),R.string.msg_verify_email_address,Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            ProgressDialogUtil.getInstance().dismissProgressDialog();
            ArrayList<ParseObject> object = (ArrayList<ParseObject>) obj; //object.get(0).keySet()
            if (object.size() > 0) {
                MySharedPreference.getInstance(getApplicationContext()).setFirstDay(object.get(0).getString(AppConstants.FIRST_DAY));
                MySharedPreference.getInstance(getApplicationContext()).setPomodoroMeter(object.get(0).getString(AppConstants.POMODORO_METER));
                MySharedPreference.getInstance(getApplicationContext()).setHourlyRate(object.get(0).getString(AppConstants.HOURLY_RATE));
            }
            Intent mIntent = new Intent(SignUpActivity.this, HomeActivity.class);
            startActivity(mIntent);
            LocalBroadcastManager.getInstance(SignUpActivity.this).sendBroadcast(new Intent("finish"));
            finish();
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onParseQueryFail(Object obj) {
        ProgressDialogUtil.getInstance().dismissProgressDialog();
        if (obj instanceof ParseException) {
            if (((ParseException) obj).getCode() == 202) {
                //TODO Username already taken
                Toast.makeText(SignUpActivity.this, R.string.val_signup_email, Toast.LENGTH_SHORT).show();
            }

        }
    }

    @OnClick(R.id.iv_facebook_login)
    public void onFacebookClick() {
        if (NetworkStatus.isInternetOn(getApplicationContext())) {
            isSocialFacebookConnect = true;
            LoginManager.getInstance().logInWithReadPermissions(SignUpActivity.this, Arrays.asList("public_profile", "email", "user_birthday"));
        } else {
            Toaster.show(getApplicationContext(), R.string.err_internet_connection_error);
        }
    }

    @OnClick(R.id.iv_google_login)
    public void onGooglePlusClick() {
        if (NetworkStatus.isInternetOn(getApplicationContext())) {
            isSocialFacebookConnect = false;
            ProgressDialogUtil.getInstance().showProgressDialog(SignUpActivity.this);
            GooglePlusLogin googlePlusLogin = GooglePlusLogin.getInstance(SignUpActivity.this);
            googlePlusLogin.signInGooglePlus();
        } else {
            Toaster.show(getApplicationContext(), R.string.err_internet_connection_error);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isSocialFacebookConnect) {
            try {
                if (mFacebookLogin != null) {
                    mFacebookLogin.getCallbackManager().onActivityResult(requestCode, resultCode, data);
                }
            } catch (Exception e) {
                iterationCount++;
                e.printStackTrace();
                if (iterationCount < 3)
                    LoginManager.getInstance().logInWithReadPermissions(SignUpActivity.this, Arrays.asList("public_profile", "email"));

            }
        } else {
            if (requestCode == 200 && resultCode == -1 && data == null) {
                ProgressDialogUtil.getInstance().dismissProgressDialog();
                onGooglePlusClick();
            }else if(requestCode == 200 && resultCode == 0 && data == null){
                ProgressDialogUtil.getInstance().dismissProgressDialog();
            }
        }

    }

    @Override
    public void onSocialResult(Object result) {
        ProgressDialogUtil.getInstance().dismissProgressDialog();
        if (result instanceof SocialMediaUserBean) {
            SocialMediaUserBean obj = (SocialMediaUserBean) result;
            if (NetworkStatus.isInternetOn(getApplicationContext())) {
                ProgressDialogUtil.getInstance().showProgressDialog(SignUpActivity.this);
                HashMap<String, Object> mValues = new HashMap<String, Object>();
                mValues.put("name", obj.getName());
                mValues.put("user_type", obj.getSocialType());
                mValues.put("image", obj.getProfilepic());
                mValues.put("user_social_id", obj.getId());
                String password= EncryptUtils.newPassword();
                try {
                    byte[] data = new byte[0];
                    data = password.getBytes("UTF-8");
                    String base64 = Base64.encodeToString(data, Base64.DEFAULT);
                    mValues.put("token", base64);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ParseUtils.getInstance().onSocialLoginPQuery(SignUpActivity.this, obj.getEmail(), password, obj.getEmail(), mValues);
            } else {
                Toaster.show(getApplicationContext(), R.string.err_internet_connection_error);
            }

        }
    }
}
