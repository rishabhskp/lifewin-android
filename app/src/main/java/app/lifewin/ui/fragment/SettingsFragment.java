package app.lifewin.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.lifewin.R;
import app.lifewin.constants.AppConstants;
import app.lifewin.parse.interfaces.IParseQueryResult;
import app.lifewin.parse.utils.ParseUtils;
import app.lifewin.preferences.MySharedPreference;
import app.lifewin.ui.activity.ChangePasswordActivity;
import app.lifewin.ui.activity.CompletedTaskActivity;
import app.lifewin.ui.activity.EditProfileActivity;
import app.lifewin.ui.activity.HomeActivity;
import app.lifewin.ui.activity.LoginActivity;
import app.lifewin.utils.Logger;
import app.lifewin.utils.NetworkStatus;
import app.lifewin.utils.ProgressDialogUtil;
import app.lifewin.utils.Toaster;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class SettingsFragment extends Fragment implements IParseQueryResult {


    @InjectView(R.id.tv_first_day_res)
    TextView tvFirstDayRes;

    @InjectView(R.id.tv_hourly_rate_res)
    TextView tvHourlyRateRes;

    @InjectView(R.id.tv_pomodoro_timer_res)
    TextView tvPomodoroTimerRes;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.inject(this, mView);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tvFirstDayRes.setText(MySharedPreference.getInstance(getActivity()).getFirstDay());
        tvHourlyRateRes.setText(MySharedPreference.getInstance(getActivity()).getHourlyRate());
        tvPomodoroTimerRes.setText(MySharedPreference.getInstance(getActivity()).getPomodoroMeter());
    }

    @OnClick({R.id.tv_first_day_res, R.id.tv_first_day})
    public void onFirstDayClick() {
        showFirstDayDialog();
    }

    @OnClick({R.id.tv_hourly_rate_res, R.id.tv_hourly_rate})
    public void onHourlyRateClick() {
        showHourlyRateDialog();
    }

    @OnClick({R.id.tv_pomodoro_timer_res, R.id.tv_pomodoro_timer})
    public void onPomodoroTimerClick() {
        showPomodoroTimerDialog();
    }


    @OnClick(R.id.tv_profile)
    public void onProfileClick() {
        Intent mIntent = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(mIntent);
    }

    @OnClick(R.id.tv_change_password)
    public void onChangePassword(){
        Intent mIntent = new Intent(getActivity(), ChangePasswordActivity.class);
        startActivity(mIntent);
    }
    @OnClick(R.id.tv_completed_task)
    public void onCompletedTask(){
        Intent mIntent = new Intent(getActivity(), CompletedTaskActivity.class);
        startActivity(mIntent);
    }


    private void showFirstDayDialog() {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_first_day);
        TextView txtSaturdayTit = (TextView) dialog.findViewById(R.id.txtSaturdayTit);
        TextView txtSundayTit = (TextView) dialog.findViewById(R.id.txtSundayTit);
        TextView txtMondayTit = (TextView) dialog.findViewById(R.id.txtMondayTit);

        View mParent = dialog.findViewById(R.id.rl_parent);
        mParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        txtSaturdayTit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tvFirstDayRes.setText(R.string.txt_saturday);
                HashMap<String, Object> mUpdateValue = new HashMap<String, Object>();
                mUpdateValue.put(AppConstants.FIRST_DAY, getActivity().getString(R.string.txt_saturday));
                onUpdateParseDatabase(mUpdateValue);
                dialog.dismiss();
            }
        });

        txtSundayTit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tvFirstDayRes.setText(R.string.txt_sunday);
                HashMap<String, Object> mUpdateValue = new HashMap<String, Object>();
                mUpdateValue.put(AppConstants.FIRST_DAY, getActivity().getString(R.string.txt_sunday));
                onUpdateParseDatabase(mUpdateValue);
                dialog.dismiss();
            }
        });
        txtMondayTit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tvFirstDayRes.setText(R.string.txt_monday);
                HashMap<String, Object> mUpdateValue = new HashMap<String, Object>();
                mUpdateValue.put(AppConstants.FIRST_DAY, getActivity().getString(R.string.txt_monday));
                onUpdateParseDatabase(mUpdateValue);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showHourlyRateDialog() {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_hourly_rate);
        final EditText edHourlyRate = (EditText) dialog.findViewById(R.id.edHourlyRate);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        dialog.show();
        View mParent = dialog.findViewById(R.id.rl_parent);
        mParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = edHourlyRate.getText().toString().trim();
                if (TextUtils.isEmpty(value)) {
                    Toast.makeText(getActivity(), R.string.txt_err_input_hrate, Toast.LENGTH_SHORT).show();
                } else {
//                    tvHourlyRateRes.setText(value);
                    HashMap<String, Object> mUpdateValue = new HashMap<String, Object>();
                    mUpdateValue.put(AppConstants.HOURLY_RATE, value);
                    onUpdateParseDatabase(mUpdateValue);
                    dialog.dismiss();
                }
            }
        });
    }

    private void showPomodoroTimerDialog() {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_pomodoro);
        dialog.show();

        TextView txtPomodoro1 = (TextView) dialog.findViewById(R.id.txtPomodoro1);
        TextView txtPomodoro2 = (TextView) dialog.findViewById(R.id.txtPomodoro2);
        TextView txtPomodoro3 = (TextView) dialog.findViewById(R.id.txtPomodoro3);
        View mParent = dialog.findViewById(R.id.rl_parent);
        mParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        txtPomodoro1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tvPomodoroTimerRes.setText(R.string.txt_25m_5m);
                HashMap<String, Object> mUpdateValue = new HashMap<String, Object>();
                mUpdateValue.put(AppConstants.POMODORO_METER, getActivity().getString(R.string.txt_25m_5m));
                onUpdateParseDatabase(mUpdateValue);
                dialog.dismiss();
            }
        });

        txtPomodoro2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tvPomodoroTimerRes.setText(R.string.txt_45m_15m);
                HashMap<String, Object> mUpdateValue = new HashMap<String, Object>();
                mUpdateValue.put(AppConstants.POMODORO_METER, getActivity().getString(R.string.txt_45m_15m));
                onUpdateParseDatabase(mUpdateValue);
                dialog.dismiss();
            }
        });
        txtPomodoro3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tvPomodoroTimerRes.setText(R.string.txt_50m_10m);
                HashMap<String, Object> mUpdateValue = new HashMap<String, Object>();
                mUpdateValue.put(AppConstants.POMODORO_METER, getActivity().getString(R.string.txt_50m_10m));
                onUpdateParseDatabase(mUpdateValue);
                dialog.dismiss();
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    private void onUpdateParseDatabase(HashMap<String, Object> mUpdateValue) {
        if (NetworkStatus.isInternetOn(getActivity())) {
            for (Map.Entry<String, Object> entry : mUpdateValue.entrySet()) {
                String key = entry.getKey();
                String value = (String) entry.getValue();
                if (key.equalsIgnoreCase(AppConstants.FIRST_DAY)) {
//                    tvFirstDayRes.setText(value);
                    MySharedPreference.getInstance(getActivity()).setWeekStartingTime(0);
                } else if (key.equalsIgnoreCase(AppConstants.POMODORO_METER)) {
//                    tvPomodoroTimerRes.setText(value);
                } else if (key.equalsIgnoreCase(AppConstants.HOURLY_RATE)) {
//                    tvHourlyRateRes.setText(value);

                }
            }
//            ProgressDialogUtil.getInstance().showProgressDialog(getActivity());
            List<String> mWhereClause = new ArrayList<String>(1);
            List<String> mArgsValue = new ArrayList<String>(1);
            mWhereClause.add("user_id");
            mArgsValue.add(MySharedPreference.getInstance(getActivity()).getUserId());
            ParseUtils.getInstance().onUpdateObjectByFieldPQuery(SettingsFragment.this, AppConstants.USER_SETTINGS,
                    mWhereClause, mArgsValue, mUpdateValue);
        } else {
            Toaster.show(getActivity(), R.string.err_internet_connection_error);
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
                    Logger.e("Key:->" + mJson.getString("key") + "   Value:->" + mJson.getString("value"));
                    if (key.equalsIgnoreCase(AppConstants.FIRST_DAY)) {
                        tvFirstDayRes.setText(value);
                        MySharedPreference.getInstance(getActivity()).setFirstDay(value);
                        //TODO Update the points...
                        MySharedPreference.getInstance(getActivity()).setPointsSyncDatabase(false);
                        MySharedPreference.getInstance(getActivity()).setTaskSyncDatabase(false);
                        MySharedPreference.getInstance(getActivity()).setWeekStartingTime(0);
                        ((HomeActivity)getActivity()).fetchWeeklyPoints();
                    } else if (key.equalsIgnoreCase(AppConstants.POMODORO_METER)) {
                        tvPomodoroTimerRes.setText(value);
                        MySharedPreference.getInstance(getActivity()).setPomodoroMeter(value);
                    } else if (key.equalsIgnoreCase(AppConstants.HOURLY_RATE)) {
                        MySharedPreference.getInstance(getActivity()).setHourlyRate(value);
                        tvHourlyRateRes.setText(value);

                    } else if (key.equalsIgnoreCase("logout")) {
                        //TODO Logout...
                        MySharedPreference.getInstance(getActivity()).resetAll();
                        Intent mIntent = new Intent(getActivity(), LoginActivity.class);
                        getActivity().startActivity(mIntent);
                        getActivity().finish();
                        return;
                    }else if(key.equalsIgnoreCase("password")){
                        ProgressDialogUtil.getInstance().showProgressDialog(getActivity());
                        ParseUtils.getInstance().onLogoutPUser(SettingsFragment.this);
                    }
                }
//                Toaster.show(getActivity(), "Updated Successfuly");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onParseQueryFail(Object obj) {
        ProgressDialogUtil.getInstance().dismissProgressDialog();
        if (obj instanceof ParseException) {
            Toast.makeText(getActivity(), ((ParseException) obj).getMessage(), Toast.LENGTH_SHORT).show();
            tvFirstDayRes.setText(MySharedPreference.getInstance(getActivity()).getFirstDay());
            tvPomodoroTimerRes.setText(MySharedPreference.getInstance(getActivity()).getPomodoroMeter());
            tvHourlyRateRes.setText(MySharedPreference.getInstance(getActivity()).getHourlyRate());
        }
    }


}
