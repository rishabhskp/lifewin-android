package app.lifewin.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import app.lifewin.R;
import app.lifewin.parse.interfaces.IParseQueryResult;
import app.lifewin.parse.utils.ParseUtils;
import app.lifewin.utils.DialogUtils;
import app.lifewin.utils.NetworkStatus;
import app.lifewin.utils.ProgressDialogUtil;
import app.lifewin.utils.Toaster;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ForgotPasswordActivity extends Activity implements IParseQueryResult {


    @InjectView(R.id.ed_email_id)
    EditText edEmailId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.inject(this);
    }


    @OnClick(R.id.tv_next)
    public void onNextClick() {
        boolean isValidate = onValidation();
        if (isValidate) {
            if (NetworkStatus.isInternetOn(getApplicationContext())) {
                ProgressDialogUtil.getInstance().showProgressDialog(ForgotPasswordActivity.this);
                ParseUtils.getInstance().onForgotPasswordByEmailPQuery(ForgotPasswordActivity.this, edEmailId.getText().toString().trim());
            } else {
                Toaster.show(getApplicationContext(), R.string.err_internet_connection_error);
            }
        }
    }

    @OnClick(R.id.iv_back)
    public void onBack(){
        finish();
    }

    /**
     * Method is used for validate the input fields.
     *
     * @return true if all validation satisfy otherwise false
     */
    private boolean onValidation() {
        String emailId = edEmailId.getText().toString().trim();
        if (emailId.length() == 0) {
            edEmailId.requestFocus();
            Toaster.show(ForgotPasswordActivity.this, R.string.val_login_email_empty);
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) {
            edEmailId.requestFocus();
            Toaster.show(ForgotPasswordActivity.this, R.string.val_login_email_invalid);
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

        ProgressDialogUtil.getInstance().dismissProgressDialog();
        if(obj instanceof String){
            try {
                JSONObject object=new JSONObject((String) obj);
                if(object.getInt("status")==200){
                    Toast.makeText(ForgotPasswordActivity.this, R.string.txt_success_password_send, Toast.LENGTH_SHORT).show();
                    finish();
                }else if(object.getInt("status")==201){
                    new DialogUtils().showAlertDialog(ForgotPasswordActivity.this,
                            getResources().getString(R.string.app_name),getResources().getString(R.string.err_email_not_found));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onParseQueryFail(Object obj) {
        ProgressDialogUtil.getInstance().dismissProgressDialog();
        if (obj instanceof ParseException) {
            if(((ParseException)obj).getCode()==205){
                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.val_forgot_password_email)+edEmailId+".", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
