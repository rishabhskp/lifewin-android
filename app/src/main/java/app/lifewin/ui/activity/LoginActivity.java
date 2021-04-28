package app.lifewin.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
import app.lifewin.utils.Logger;
import app.lifewin.utils.NetworkStatus;
import app.lifewin.utils.ProgressDialogUtil;
import app.lifewin.utils.Toaster;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class LoginActivity extends Activity implements ISocialMediaLoginResponse, IParseQueryResult {

    @InjectView(R.id.ed_email_id)
    EditText edEmailId;
    @InjectView(R.id.ed_password)
    EditText edPassword;
    @InjectView(R.id.iv_facebook_login)
    View ivImage;

    private FacebookLogin mFacebookLogin;
    private ActivityFinishBroadcast activityFinishBroadcast;
    private boolean isSocialFacebookConnect;
    private int iterationCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        mFacebookLogin = FacebookLogin.getInstance(LoginActivity.this);
        mFacebookLogin.registerLoginCallBack();
        activityFinishBroadcast = new ActivityFinishBroadcast();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(activityFinishBroadcast, new IntentFilter("finish"));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(activityFinishBroadcast);
    }


    @OnClick(R.id.tv_forgot_password)
    void onForgotPassword() {
        Intent mIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(mIntent);
    }

    @OnClick(R.id.tv_signup)
    void onSignUp() {
        Intent mIntent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(mIntent);
    }

    @OnClick(R.id.tv_next)
    public void onLogin() {
        boolean isValidate = onValidation();
        if (isValidate) {
            if (NetworkStatus.isInternetOn(getApplicationContext())) {
                ProgressDialogUtil.getInstance().showProgressDialog(LoginActivity.this);
                ParseUtils.getInstance().onLoginPQuery(LoginActivity.this, edEmailId.getText().toString().trim(), edPassword.getText().toString().trim(), false);
            } else {
                Toaster.show(getApplicationContext(), R.string.err_internet_connection_error);
            }
        }


    }

    @OnClick(R.id.iv_facebook_login)
    public void onFacebookClick() {
        if (NetworkStatus.isInternetOn(getApplicationContext())) {
            isSocialFacebookConnect = true;
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
        } else {
            Toaster.show(getApplicationContext(), R.string.err_internet_connection_error);
        }

    }

    @OnClick(R.id.iv_google_login)
    public void onGooglePlusClick() {
        if (NetworkStatus.isInternetOn(getApplicationContext())) {
            isSocialFacebookConnect = false;
            Logger.e("Starting time:->" + System.currentTimeMillis());
            ProgressDialogUtil.getInstance().showProgressDialog(LoginActivity.this);
            GooglePlusLogin googlePlusLogin = GooglePlusLogin.getInstance(LoginActivity.this);
            googlePlusLogin.signInGooglePlus();
        } else {
            Toaster.show(getApplicationContext(), R.string.err_internet_connection_error);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * Method is used for validate the input fields.
     *
     * @return
     */
    private boolean onValidation() {

        String emailId = edEmailId.getText().toString().trim();
        String password = edPassword.getText().toString().trim();
        if (emailId.length() == 0) {
            edEmailId.requestFocus();
            Toaster.show(LoginActivity.this, R.string.val_login_email_empty);
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
            edEmailId.requestFocus();
            Toaster.show(LoginActivity.this, R.string.val_login_email_invalid);
            return false;
        } else if (password.length() == 0) {
            edPassword.requestFocus();
            Toaster.show(LoginActivity.this, R.string.val_login_password_empty);
            return false;
        } else if (password.length() < 4) {
            edPassword.requestFocus();
            Toaster.show(LoginActivity.this, R.string.val_login_password_min_length);
            return false;
        }
        return true;
    }


    @Override
    public void onSocialResult(Object result) {
        //TODO Hit the social media webservice
        Logger.e("End time:->" + System.currentTimeMillis());
        if (result instanceof SocialMediaUserBean) {
            SocialMediaUserBean obj = (SocialMediaUserBean) result;
            if (NetworkStatus.isInternetOn(getApplicationContext())) {
                ProgressDialogUtil.getInstance().showProgressDialog(LoginActivity.this);

                HashMap<String, Object> mValues = new HashMap<String, Object>();
                mValues.put("name", obj.getFirstName() + " " + obj.getLastName());
                mValues.put("user_type", obj.getSocialType());
                mValues.put("image", obj.getProfilepic());
                mValues.put("user_social_id", obj.getId());
                String password = EncryptUtils.newPassword();

                try {
                    byte[] data = new byte[0];
                    data = password.getBytes("UTF-8");
                    String base64 = Base64.encodeToString(data, Base64.DEFAULT);
                    mValues.put("token", base64);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ParseUtils.getInstance().onSocialLoginPQuery(LoginActivity.this, obj.getEmail(), password, obj.getEmail(), mValues);

            } else {
                Toaster.show(getApplicationContext(), R.string.err_internet_connection_error);
            }

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
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));

            }
        } else {
            if (requestCode == 200 && resultCode == -1 && data == null) {
                ProgressDialogUtil.getInstance().dismissProgressDialog();
                onGooglePlusClick();
            } else if (requestCode == 200 && resultCode == 0 && data == null) {
                ProgressDialogUtil.getInstance().dismissProgressDialog();
            }
        }


    }

    @Override
    public void onParseQuerySuccess(Object obj) {

        if (obj instanceof ParseUser) {
            //TODO Get info of the user
            ParseUser object = (ParseUser) obj;
            MySharedPreference.getInstance(getApplicationContext()).setUserId(object.getObjectId());
            MySharedPreference.getInstance(getApplicationContext()).setLogin(true);
            MySharedPreference.getInstance(getApplicationContext()).setName(object.getString("name"));
            MySharedPreference.getInstance(getApplicationContext()).setUserProfileImageF(object.getString("image"));
            MySharedPreference.getInstance(getApplicationContext()).setUserProfileImage(object.getString("user_image"));
            ParseUser parseUser = (ParseUser) obj;
            if (parseUser.getParseFile("user_image") != null) {
                MySharedPreference.getInstance(getApplicationContext()).setUserProfileImage(parseUser.getParseFile("user_image").getUrl());
            }
            //TODO Getting the default settings...
            List<String> whereClause = new ArrayList<String>();
            whereClause.add(AppConstants.USER_ID);
            List<String> argsValues = new ArrayList<String>();
            argsValues.add(object.getObjectId());
            ParseUtils.getInstance().onFetchPQuery(LoginActivity.this, AppConstants.USER_SETTINGS, whereClause, argsValues);

        } else if (obj instanceof String) {
            try {
                ProgressDialogUtil.getInstance().dismissProgressDialog();
                JSONObject jsonObject = new JSONObject((String) obj);
                if (jsonObject.getInt("status") == 201) {
                    Toast.makeText(getApplicationContext(), R.string.err_email_not_verify, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } else {
            ProgressDialogUtil.getInstance().dismissProgressDialog();
            ArrayList<ParseObject> object = (ArrayList<ParseObject>) obj; //object.get(0).keySet()
            if (object.size() > 0) {
                MySharedPreference.getInstance(getApplicationContext()).setFirstDay(object.get(0).getString(AppConstants.FIRST_DAY));
                MySharedPreference.getInstance(getApplicationContext()).setPomodoroMeter(object.get(0).getString(AppConstants.POMODORO_METER));
                MySharedPreference.getInstance(getApplicationContext()).setHourlyRate(object.get(0).getString(AppConstants.HOURLY_RATE));
                fetchWeeklyPoints();
            }
            Intent mIntent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(mIntent);
            finish();
        }
    }

    @Override
    public void onParseQueryFail(Object obj) {
        ProgressDialogUtil.getInstance().dismissProgressDialog();
        if (obj instanceof ParseException) {
            if (((ParseException) obj).getCode() == 101) {
                Toast.makeText(LoginActivity.this, R.string.val_login_credential, Toast.LENGTH_SHORT).show();
            } else if (((ParseException) obj).getCode() == 202) {
                Toast.makeText(LoginActivity.this, R.string.val_signup_email, Toast.LENGTH_SHORT).show();
            }

        }
    }


    private class ActivityFinishBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }


    //TODO fetch weekly points.
    private void fetchWeeklyPoints() {
        String firstDay = MySharedPreference.getInstance(getApplicationContext()).getFirstDay();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String todayDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        if (firstDay.equalsIgnoreCase(todayDay)) {
            MySharedPreference.getInstance(getApplicationContext()).setWeeklyPoints(0);
        } else {
            Calendar cal = Calendar.getInstance();

            int weekDay = 0;
            if (firstDay.equalsIgnoreCase("Saturday")) {
                weekDay = Calendar.SATURDAY;
            } else if (firstDay.equalsIgnoreCase("Sunday")) {
                weekDay = Calendar.SUNDAY;
            } else if (firstDay.equalsIgnoreCase("Monday")) {
                weekDay = Calendar.MONDAY;
            }

            while (cal.get(Calendar.DAY_OF_WEEK) != weekDay)
                cal.add(Calendar.DAY_OF_WEEK, -1);

            Logger.e("Date = " + cal.getTimeInMillis() + " " + cal.getTime());
            long currentTim = 0;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                String dateC = sdf.format(new Date());
                SimpleDateFormat sdfM = new SimpleDateFormat("MM/dd/yyyy");
                Date dateM = sdfM.parse(dateC);
                currentTim = dateM.getTime();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            ParseUtils.getInstance().onFetchTotalPoints(LoginActivity.this, AppConstants.USERS_POINTS, cal.getTimeInMillis(), currentTim+AppConstants.ONE_DAY_INTERVAL);

        }

    }

}
