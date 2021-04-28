package app.lifewin.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import app.lifewin.R;
import app.lifewin.parse.interfaces.IParseQueryResult;
import app.lifewin.parse.utils.ParseUtils;
import app.lifewin.utils.NetworkStatus;
import app.lifewin.utils.ProgressDialogUtil;
import app.lifewin.utils.Toaster;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class ChangePasswordActivity extends Activity implements IParseQueryResult {

    @InjectView(R.id.ed_cpassword)
    EditText edCurrentPassword;
    @InjectView(R.id.ed_new_password)
    EditText edNewPassword;
    @InjectView(R.id.ed_confirm_password)
    EditText edCNewPassword;

    private String currentPassword = "", userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.inject(this);

        try {
            userName = ParseUser.getCurrentUser().getUsername();
            byte[] data = Base64.decode(ParseUser.getCurrentUser().getString("token"), Base64.DEFAULT);
            currentPassword = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.iv_back)
    public void onBackPress() {
        finish();
    }

    @OnClick(R.id.tv_next)
    public void onSubmit() {
        boolean isValidate = onValidation();
        if (isValidate) {
            if (NetworkStatus.isInternetOn(getApplicationContext())) {
                ProgressDialogUtil.getInstance().showProgressDialog(ChangePasswordActivity.this);
                ParseUtils.getInstance().onChangePassword(ChangePasswordActivity.this, edNewPassword.getText().toString().trim());
            } else {
                Toaster.show(getApplicationContext(), R.string.err_internet_connection_error);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    /**
     * Method is used for validate the input fields.
     *
     * @return true if all validation satisfy otherwise false
     */
    private boolean onValidation() {
        String cPassword = edCurrentPassword.getText().toString().trim();
        String newPassword = edNewPassword.getText().toString().trim();
        String newCPassword = edCNewPassword.getText().toString().trim();
        if (!cPassword.equalsIgnoreCase(currentPassword)) {
            edCurrentPassword.requestFocus();
            Toaster.show(this, R.string.val_incorroect_cpassword);
            return false;
        } else if (newPassword.length() == 0) {
            edNewPassword.requestFocus();
            Toaster.show(this, R.string.val_empty_new_password);
            return false;
        } else if (newPassword.length() < 4) {
            edNewPassword.requestFocus();
            Toaster.show(this, R.string.val_login_password_min_length);
            return false;
        } else if (newCPassword.length() == 0) {
            edCurrentPassword.requestFocus();
            Toaster.show(this, R.string.val_reenter_password_empty);
            return false;
        } else if (!newCPassword.equalsIgnoreCase(newPassword)) {
            edCurrentPassword.requestFocus();
            Toaster.show(this, R.string.val_password_mismatch);
            return false;
        }
        return true;
    }

    @Override
    public void onParseQuerySuccess(Object obj) {
        if (obj instanceof String) {
            try {
                JSONObject jsonObject = new JSONObject((String) obj);
                if (jsonObject.getBoolean("status")) {
                    //TODO send the email-with password
                    ParseUtils.getInstance().onLoginPQuery(ChangePasswordActivity.this, userName, edCNewPassword.getText().toString().trim(), false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (obj instanceof ParseUser) {
            ProgressDialogUtil.getInstance().dismissProgressDialog();
            Toaster.show(this, R.string.txt_success_password_change);
            finish();
        }
    }

    @Override
    public void onParseQueryFail(Object obj) {
        ProgressDialogUtil.getInstance().dismissProgressDialog();
        if (obj instanceof ParseException) {
            Toaster.show(ChangePasswordActivity.this, ((ParseException) obj).getMessage());
        }
    }
}
