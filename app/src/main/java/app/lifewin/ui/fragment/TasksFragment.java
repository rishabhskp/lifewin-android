package app.lifewin.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import app.lifewin.R;
import app.lifewin.adapter.TaskDragNDropAdapter;
import app.lifewin.constants.AppConstants;
import app.lifewin.interfaces.IOpenGroupListener;
import app.lifewin.model.app.TaskBean;
import app.lifewin.parse.interfaces.IParseQueryResult;
import app.lifewin.parse.utils.ParseUtils;
import app.lifewin.preferences.MySharedPreference;
import app.lifewin.ui.activity.CompletedTaskActivity;
import app.lifewin.ui.activity.HomeActivity;
import app.lifewin.ui.views.TaskDragNDropListView;
import app.lifewin.utils.AppUtils;
import app.lifewin.utils.HideKeyboard;
import app.lifewin.utils.ProgressDialogUtil;
import butterknife.ButterKnife;
import butterknife.InjectView;


public class TasksFragment extends Fragment implements IOpenGroupListener, IParseQueryResult {


    /**
     * children items with a key and value list
     */
    private Map<String, ArrayList<TaskBean>> children = Collections
            .synchronizedMap(new LinkedHashMap<String, ArrayList<TaskBean>>());

    @InjectView(R.id.list_view_customizer)
    TaskDragNDropListView dndListView;
    @InjectView(R.id.ed_add_task)
    EditText edAddTask;

    private TaskDragNDropAdapter mTaskDragNDropAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_add_task, container, false);
        ButterKnife.inject(this, mView);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fetchUsersTask();
//        dndListView.setDragOnLongPress(true);
//        mTaskDragNDropAdapter = new TaskDragNDropAdapter(getActivity(), TasksFragment.this, children);
//        dndListView.setAdapter(mTaskDragNDropAdapter);
        edAddTask.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onAddNewTask();
                }
                return false;
            }
        });
        dndListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (groupPosition == 4) {
                    //TODO open the Completed Task list
                    Intent mIntent = new Intent(getActivity(), CompletedTaskActivity.class);
                    startActivity(mIntent);
                }
                dndListView.setSelection(groupPosition);

//                dndListView.setSelectionFromTop(groupPosition, 0);
                return false;
            }
        });

//        dndListView.setDragOnLongPress(true);
//        dndListView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                dndListView.setDragOnLongPress(true);
//                return true;
//            }
//        });

    }


    private void fetchUsersTask() {
        if (MySharedPreference.getInstance(getActivity()).getWeekStartingTime() == 0) {
            AppUtils.getWeekFirstDate(getActivity());
        }
        ProgressDialogUtil.getInstance().showProgressDialog(getActivity());
        ParseUtils.getInstance().onFetchTasksList(TasksFragment.this, AppConstants.USERS_TASKS, MySharedPreference.getInstance(getActivity()).getWeekStartingTime());
    }

    /**
     * Method is used for saving the data in database and showing in the list.
     */
    private void onAddNewTask() {
        TaskBean tObj = new TaskBean(edAddTask.getText().toString().trim(), 2);
        edAddTask.setText("");
        HashMap<String, Object> mValues = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String date = sdf.format(new Date());
        mValues.put("date", date);
        try {
            SimpleDateFormat sdfM = new SimpleDateFormat("MM/dd/yyyy");
            Date dateM = sdfM.parse(date);
            mValues.put("date_in_millis", dateM.getTime());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            mValues.put("date_in_millis", System.currentTimeMillis());
        }
        mValues.put("points", tObj.getPoints());
        mValues.put("name", tObj.getTitle());
        mValues.put("created_time_millis", System.currentTimeMillis());
        mValues.put("status", AppConstants.STATUS_ARRAY[1]);
        ParseUtils.getInstance().onSaveTaskPQuery(TasksFragment.this, AppConstants.USERS_TASKS, mValues);
    }

    @Override
    public void onGroupSelected(int pos) {
        if (!dndListView.isGroupExpanded(pos)) {
            dndListView.expandGroup(pos);
        }
    }

    @Override
    public void onParseQuerySuccess(Object obj) {
        ProgressDialogUtil.getInstance().dismissProgressDialog();
        if (obj instanceof String) {
            try {
                JSONObject mObject = new JSONObject((String) obj);
                if (mObject.getInt("code") == 200) {
                    if (mObject.getString("table_name").equalsIgnoreCase(AppConstants.USERS_TASKS)) {
                        if (mObject.getString("purpose").equalsIgnoreCase("add_task")) {
                            //TODO ....
                            TaskBean tObj = new TaskBean(mObject.getString("task_name"), mObject.getInt("points"));
                            tObj.setId(mObject.getString("object_id"));
                            tObj.setTimeInMillies(mObject.getLong("date_in_millis"));
                            tObj.setCreatedTimeInMillis(mObject.getLong("created_time_millis"));
                            ArrayList<TaskBean> dataList = children.get(getResources().getString(R.string.txt_today));
                            dataList.add(0, tObj);
                            children.put(getResources().getString(R.string.txt_today), dataList);
                            mTaskDragNDropAdapter.notifyDataSetChanged();
                        } else if (mObject.getString("purpose").equalsIgnoreCase("update_task_id")) {
                            long keyVal = mObject.getLong("created_time_millis");
                            ArrayList<TaskBean> dataList = children.get(getResources().getString(R.string.txt_today));
                            boolean isToday = false;
                            for (int i = 0; i < dataList.size(); i++) {
                                TaskBean tObj = dataList.get(i);
                                if (tObj.getTimeInMillies() == keyVal) {
                                    isToday = true;
                                    tObj.setId(mObject.getString("object_id"));
                                    break;
                                }
                            }
                            if (isToday) {
                                children.put(getResources().getString(R.string.txt_today), dataList);
                                mTaskDragNDropAdapter.notifyDataSetChanged();
                                return;
                            }
                            //TODO For tomorrow List Update
                            ArrayList<TaskBean> dataListTomorrow = children.get(getResources().getString(R.string.txt_tomorrow));
                            for (int i = 0; i < dataListTomorrow.size(); i++) {
                                TaskBean tObj = dataListTomorrow.get(i);
                                if (tObj.getTimeInMillies() == keyVal) {
                                    isToday = true;
                                    tObj.setId(mObject.getString("object_id"));
                                    break;
                                }
                            }
                            if (isToday) {
                                children.put(getResources().getString(R.string.txt_tomorrow), dataListTomorrow);
                                mTaskDragNDropAdapter.notifyDataSetChanged();
                                return;
                            }

                            //TODO For This week List Update
                            ArrayList<TaskBean> dataListWeek = children.get(getResources().getString(R.string.txt_this_week));
                            for (int i = 0; i < dataListWeek.size(); i++) {
                                TaskBean tObj = dataListWeek.get(i);
                                if (tObj.getTimeInMillies() == keyVal) {
                                    isToday = true;
                                    tObj.setId(mObject.getString("object_id"));
                                    break;
                                }
                            }
                            if (isToday) {
                                children.put(getResources().getString(R.string.txt_this_week), dataListWeek);
                                mTaskDragNDropAdapter.notifyDataSetChanged();
                                return;
                            }


                            //TODO For Later List Update
                            ArrayList<TaskBean> dataListLater = children.get(getResources().getString(R.string.txt_later));
                            for (int i = 0; i < dataListLater.size(); i++) {
                                TaskBean tObj = dataListLater.get(i);
                                if (tObj.getTimeInMillies() == keyVal) {
                                    isToday = true;
                                    tObj.setId(mObject.getString("object_id"));
                                    break;
                                }
                            }
                            if (isToday) {
                                children.put(getResources().getString(R.string.txt_later), dataListLater);
                                mTaskDragNDropAdapter.notifyDataSetChanged();
                                return;
                            }


                            //TODO For Later List Update
                            ArrayList<TaskBean> dataListComplete = children.get(getResources().getString(R.string.txt_complete));
                            for (int i = 0; i < dataListComplete.size(); i++) {
                                TaskBean tObj = dataListComplete.get(i);
                                if (tObj.getTimeInMillies() == keyVal) {
                                    isToday = true;
                                    tObj.setId(mObject.getString("object_id"));
                                    break;
                                }
                            }
                            if (isToday) {
//                                children.put(getResources().getString(R.string.txt_complete), dataListComplete);
                                mTaskDragNDropAdapter.notifyDataSetChanged();
                                dndListView.setDragOnLongPress(true);
                                return;
                            }

                        }
                    } else if (mObject.getString("table_name").equalsIgnoreCase(AppConstants.USERS_POINTS)) {
                        //TODO
                        MySharedPreference.getInstance(getActivity()).setTodayPoints
                                (MySharedPreference.getInstance(getActivity()).getTodayPoints() + mObject.getInt("points"));
                        MySharedPreference.getInstance(getActivity()).setWeeklyPoints(MySharedPreference.getInstance(getActivity()).getWeeklyPoints() + mObject.getInt("points"));
                        ((HomeActivity) getActivity()).updatePoints();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            List<ParseObject> mObjList = (List<ParseObject>) obj;

            ArrayList<TaskBean> mTodayBean = new ArrayList<>();
            ArrayList<TaskBean> mTomorrowBean = new ArrayList<>();
            ArrayList<TaskBean> mThisWeekBean = new ArrayList<>();
            ArrayList<TaskBean> mLaterBean = new ArrayList<>();
            ArrayList<TaskBean> mCompletedBean = new ArrayList<>();

            long currentDateInMillies;
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String date = sdf.format(new Date());
            try {
                SimpleDateFormat sdfM = new SimpleDateFormat("MM/dd/yyyy");
                Date dateM = sdfM.parse(date);
                currentDateInMillies = dateM.getTime();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
                currentDateInMillies = System.currentTimeMillis();
            }

            children.clear();
            //TODO get the last day of the week...

            for (int i = 0; i < mObjList.size(); i++) {
                ParseObject pObj = mObjList.get(i);
                TaskBean objTask = new TaskBean(pObj.getString("name"), pObj.getInt("points"));
                objTask.setId(pObj.getObjectId());
                objTask.setTimeInMillies(pObj.getLong("date_in_millis"));
                objTask.setCreatedTimeInMillis(pObj.getLong("created_time_millis"));
                objTask.setDate(pObj.getString("date"));
                long databaseDate = pObj.getLong("date_in_millis");
                if (pObj.getString("status").equalsIgnoreCase(AppConstants.STATUS_ARRAY[0])) {
//                    mCompletedBean.add(objTask);
                } else if (pObj.getString("status").equalsIgnoreCase(AppConstants.STATUS_ARRAY[2])) {
                    mLaterBean.add(objTask);
                } else {
                    if (databaseDate >= currentDateInMillies && databaseDate < (currentDateInMillies + AppConstants.ONE_DAY_INTERVAL)) {
                        mTodayBean.add(objTask);
                    } else if (databaseDate >= (currentDateInMillies + AppConstants.ONE_DAY_INTERVAL) && databaseDate < (currentDateInMillies + 2 * AppConstants.ONE_DAY_INTERVAL)) {
                        mTomorrowBean.add(objTask);
                    } else {
                        mThisWeekBean.add(objTask);
                    }
                }
            }
            children.put(getResources().getString(R.string.txt_today), mTodayBean);
            children.put(getResources().getString(R.string.txt_tomorrow), mTomorrowBean);
            children.put(getResources().getString(R.string.txt_this_week), mThisWeekBean);
            children.put(getResources().getString(R.string.txt_later), mLaterBean);
            children.put(getResources().getString(R.string.txt_complete), mCompletedBean);
            dndListView.setDragOnLongPress(true);
            mTaskDragNDropAdapter = new TaskDragNDropAdapter(getActivity(), TasksFragment.this, children);
            dndListView.setAdapter(mTaskDragNDropAdapter);
            dndListView.expandGroup(3);
            dndListView.expandGroup(2);
            dndListView.expandGroup(1);
            dndListView.expandGroup(0);

        }
    }

    @Override
    public void onParseQueryFail(Object obj) {
        ProgressDialogUtil.getInstance().dismissProgressDialog();
//        Toaster.show(getActivity(),((ParseException)obj).getLocalizedMessage());
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HideKeyboard.keyboardHide(getActivity());
            }
        }, 800);
    }
}
