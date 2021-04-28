package app.lifewin.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import app.lifewin.R;
import app.lifewin.constants.AppConstants;
import app.lifewin.parse.interfaces.IParseQueryResult;
import app.lifewin.parse.utils.ParseUtils;
import app.lifewin.ui.views.calendar.CalendarDay;
import app.lifewin.ui.views.calendar.MaterialCalendarView;
import app.lifewin.ui.views.calendar.OnDateSelectedListener;
import app.lifewin.utils.AppUtils;
import app.lifewin.utils.ProgressDialogUtil;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class WinStreakFragment extends Fragment implements IParseQueryResult {

    @InjectView(R.id.mcv_calendar)
    MaterialCalendarView mcvCalendar;
    @InjectView(R.id.tv_current_overall)
    TextView tvCurrentOverall;
    @InjectView(R.id.iv_task_done)
    ImageView ivWinStreak;
    private int overallCount = 0, overallTemp = 0, current = 0;
    private long lastContinue = 0;
    /**
     * List of completed task time.
     */
    List<Long> mCompletedList = new ArrayList<Long>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_win_streak, container, false);
        ButterKnife.inject(this, mView);
        getWinStreakList();
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mcvCalendar.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
        mcvCalendar.setSelectionColor(getResources().getColor(R.color.color_app_green));
        mcvCalendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                HashMap<String, Object> mValues = new HashMap<String, Object>();
                long dateInMillis = AppUtils.convertDateInMillis((date.getMonth() + 1) + "/" + date.getDay() + "/" + date.getYear());
                mValues.put("date_in_millis", dateInMillis);
                mValues.put("win_streak_time", System.currentTimeMillis());
                if (mCompletedList.contains(dateInMillis)) {
                    ParseUtils.getInstance().removeWinStreakPQuery(WinStreakFragment.this, AppConstants.USERS_WIN_STREAK, dateInMillis);
                } else {
                    ParseUtils.getInstance().setWinStreakPQuery(WinStreakFragment.this, AppConstants.USERS_WIN_STREAK, mValues);
                }
            }
        });
    }

    @OnClick(R.id.iv_task_done)
    public void onDoneClick() {
        long dateInMillis = AppUtils.getCurrentDateInMillies();
        if(ivWinStreak.isSelected()){
            ParseUtils.getInstance().removeWinStreakPQuery(WinStreakFragment.this, AppConstants.USERS_WIN_STREAK, dateInMillis);
        }else {
            HashMap<String, Object> mValues = new HashMap<String, Object>();
            mValues.put("date_in_millis", dateInMillis);
            mValues.put("win_streak_time", System.currentTimeMillis());
            ParseUtils.getInstance().setWinStreakPQuery(WinStreakFragment.this, AppConstants.USERS_WIN_STREAK, mValues);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onParseQuerySuccess(Object obj) {
        ProgressDialogUtil.getInstance().dismissProgressDialog();
        if (obj instanceof String) {
            try {
                JSONObject mObj = new JSONObject((String) obj);
                long value = mObj.getLong("created_time");
                if (mObj.getString("type").equalsIgnoreCase("add")) {
                    mCompletedList.add(value);
                    calCulateCurrentAndOverall();
                    tvCurrentOverall.setText("Current : " + current + " | Overall : " + overallCount);
                } else {
                    if(value==AppUtils.getCurrentDateInMillies()){
                        ivWinStreak.setSelected(false);
                    }
                    Date mDate = new Date();
                    mDate.setTime(value);
                    mcvCalendar.setDateSelected(mDate, false);
                    mCompletedList.remove(value);
                    calCulateCurrentAndOverall();
                    tvCurrentOverall.setText("Current : " + current + " | Overall : " + overallCount);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //TODO Parse WinStreak List..
            List<ParseObject> mObjList = (List<ParseObject>) obj;
            overallCount = 0;
            overallTemp = 0;
            current = 0;
            mCompletedList.clear();
            int size = mObjList.size();
            for (int i = 0; i < size; i++) {
                mCompletedList.add(mObjList.get(i).getLong("date_in_millis"));
                Date mDate = new Date();
                mDate.setTime(mCompletedList.get(i));
                mcvCalendar.setDateSelected(mDate, true);
            }
            calCulateCurrentAndOverall();
            tvCurrentOverall.setText("Current : " + current + " | Overall : " + overallCount);
           /* ivTaskDone.setSelected(false);
            if (mObjList.size() > 1) {
                for (int i = 0; i < mObjList.size(); i++) {
                    if (i != 0) {
                        if (mObjList.get(i).getLong("date_in_millis") == mObjList.get(i - 1).getLong("date_in_millis") + AppConstants.ONE_DAY_INTERVAL) {
                            overallTemp++;
                            current++;
                            lastContinue = mObjList.get(i).getLong("date_in_millis");
                        } else {
                            if (mObjList.get(i).getLong("date_in_millis") == AppUtils.getCurrentDateInMillies()) {
                                current = 1;
                            } else if (mObjList.get(i).getLong("date_in_millis") != AppUtils.getCurrentDateInMillies() - AppConstants.ONE_DAY_INTERVAL) {
                                current = 0;
                            }
                            if (overallCount < overallTemp) {
                                overallCount = overallTemp;
                            }
                            overallTemp = 1;
                        }
                        if (mObjList.get(i).getLong("date_in_millis") == AppUtils.getCurrentDateInMillies()) {
                            ivTaskDone.setVisibility(View.GONE);
                        }
                    } else {
                        if (mObjList.get(i).getLong("date_in_millis") == AppUtils.getCurrentDateInMillies() - AppConstants.ONE_DAY_INTERVAL) {
                            current = 1;
                        }
                        overallTemp = 1;
                    }
                    Date mDate = new Date();
                    mDate.setTime(mObjList.get(i).getLong("date_in_millis"));
                    mcvCalendar.setDateSelected(mDate, true);
                }
                if (mObjList.get(mObjList.size() - 1).getLong("date_in_millis") == AppUtils.getCurrentDateInMillies() || mObjList.get(mObjList.size() - 1).getLong("date_in_millis") == AppUtils.getCurrentDateInMillies() - AppConstants.ONE_DAY_INTERVAL) {

                } else {
                    current = 0;
                }
                if (overallTemp > overallCount) {
                    overallCount = overallTemp;
                    overallTemp = 0;
                }
                tvCurrentOverall.setText("Current : " + current + " | Overall : " + overallCount);
            } else if (mObjList.size() == 1) {
                overallCount = 1;
                current = 0;

                if (mObjList.get(0).getLong("date_in_millis") == AppUtils.getCurrentDateInMillies()) {
                    current = 1;
                    ivTaskDone.setSelected(true);
                } else if (mObjList.get(0).getLong("date_in_millis") == AppUtils.getCurrentDateInMillies() - AppConstants.ONE_DAY_INTERVAL) {
                    current = 1;
                }
                lastContinue = mObjList.get(0).getLong("date_in_millis");
                Date mDate = new Date();
                mDate.setTime(mObjList.get(0).getLong("date_in_millis"));
                mcvCalendar.setDateSelected(mDate, true);
                tvCurrentOverall.setText("Current : " + current + " | Overall : " + overallCount);

            }*/
        }

    }

    @Override
    public void onParseQueryFail(Object obj) {
        ProgressDialogUtil.getInstance().dismissProgressDialog();

    }


    private void getWinStreakList() {
        ProgressDialogUtil.getInstance().showProgressDialog(getActivity());
        ParseUtils.getInstance().onFetchWinStreakList(WinStreakFragment.this, AppConstants.USERS_WIN_STREAK);
    }

    private void calCulateCurrentAndOverall() {
        ivWinStreak.setSelected(false);
        if (mCompletedList.size() == 0) {
            current = 0;
            overallCount = 0;
        } else if (mCompletedList.size() == 1) {
            current = 0;
            overallCount = 0;
            if (mCompletedList.get(0) == AppUtils.getCurrentDateInMillies()) {
                current = 1;
                ivWinStreak.setSelected(true);
            } else if (mCompletedList.get(0) == AppUtils.getCurrentDateInMillies() - AppConstants.ONE_DAY_INTERVAL) {
                current = 1;
            }
            overallCount = 1;

        } else {
            Collections.sort(mCompletedList);
            int size = mCompletedList.size();
            current = 1;
            overallTemp = 1;
            overallCount = 1;
            for (int i = 0; i < size - 1; i++) {
                if (mCompletedList.get(i) == mCompletedList.get(i + 1) - AppConstants.ONE_DAY_INTERVAL) {
                    overallTemp++;
                } else {
                    if (overallCount < overallTemp) {
                        overallCount = overallTemp;
                    }
                    overallTemp = 1;
                }
            }
            if (overallTemp > overallCount) {
                overallCount = overallTemp;
            }
            for (int i = size - 1; i >= 0; i--) {
                if (i == size - 1 && mCompletedList.get(i) != AppUtils.getCurrentDateInMillies()) {
                    if (mCompletedList.get(i) != AppUtils.getCurrentDateInMillies() - AppConstants.ONE_DAY_INTERVAL) {
                        current = 0;
                        break;
                    } else {
                        //Yesterday done
                    }
                } else if (i == size - 1 && mCompletedList.get(i) == AppUtils.getCurrentDateInMillies()) {
                    //TODay Done
                    Date mDate = new Date();
                    mDate.setTime(mCompletedList.get(i));
                    mcvCalendar.setDateSelected(mDate, true);
                    ivWinStreak.setSelected(true);
                }else if(mCompletedList.get(i)==(mCompletedList.get(size-1)- ((size-1)-i)*AppConstants.ONE_DAY_INTERVAL)){
                    current++;
                }else{
                    break;
                }
            }
        }
    }


}
