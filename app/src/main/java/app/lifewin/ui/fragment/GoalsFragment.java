package app.lifewin.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import app.lifewin.adapter.GoalDragNDropAdapter;
import app.lifewin.constants.AppConstants;
import app.lifewin.interfaces.IOpenGroupListener;
import app.lifewin.model.app.GoalBean;
import app.lifewin.parse.interfaces.IParseQueryResult;
import app.lifewin.parse.utils.ParseUtils;
import app.lifewin.ui.views.GoalDragNDropListView;
import app.lifewin.utils.AppUtils;
import app.lifewin.utils.ProgressDialogUtil;
import butterknife.ButterKnife;
import butterknife.InjectView;


public class GoalsFragment extends Fragment implements IOpenGroupListener, IParseQueryResult {


    /**
     * children items with a key and value list
     */
    private Map<String, ArrayList<GoalBean>> children = Collections
            .synchronizedMap(new LinkedHashMap<String, ArrayList<GoalBean>>());
    @InjectView(R.id.list_view_customizer)
    GoalDragNDropListView dndListView;
    @InjectView(R.id.ed_add_task)
    EditText edAddTask;
    @InjectView(R.id.ll_c)
    LinearLayout ll_c;
    private String serviceType;
    private GoalDragNDropAdapter mGoalDragNDropAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_add_goals, container, false);
        ButterKnife.inject(this, mView);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
        dndListView.setDragOnLongPress(true);
        mGoalDragNDropAdapter = new GoalDragNDropAdapter(getActivity(),GoalsFragment.this, GoalsFragment.this, children, ll_c);
        dndListView.setAdapter(mGoalDragNDropAdapter);
        edAddTask.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addNewGoal();
                }
                return false;
            }
        });

    }


    /**
     * simple function to fill the list
     */

    public void getData() {
        serviceType=AppConstants.USERS_GOALS;
        ProgressDialogUtil.getInstance().showProgressDialog(getActivity());
        ParseUtils.getInstance().onFetchGoalsList(GoalsFragment.this, AppConstants.USERS_GOALS, AppUtils.getCurrentDateInMillies());


    }

    private void addNewGoal() {
        GoalBean tObj = new GoalBean(edAddTask.getText().toString().trim());
        edAddTask.setText("");
        // save on parse database...
        HashMap<String, Object> mValues = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String date = sdf.format(new Date());
        mValues.put("date", date);
        long dateInMillis = AppUtils.getCurrentDateInMillies();
        mValues.put("date_in_millis", dateInMillis);
        mValues.put("created_time_millis", System.currentTimeMillis());
        mValues.put("name", tObj.getTitle());
        mValues.put("start_time", dateInMillis);
        mValues.put("end_time", dateInMillis + (10 * AppConstants.ONE_DAY_INTERVAL));
        mValues.put("no_of_days", 10);
        mValues.put("status", AppConstants.GOAL_STATUS_ARRAY[0]);

        ParseUtils.getInstance().onSaveTaskPQuery(GoalsFragment.this, AppConstants.USERS_GOALS, mValues);

    }

    @Override
    public void onGroupSelected(int pos) {
        if (!dndListView.isGroupExpanded(pos)) {
            dndListView.expandGroup(pos);
        }
    }

    @Override
    public void onParseQuerySuccess(Object obj) {


        if (obj instanceof String) {
            ProgressDialogUtil.getInstance().dismissProgressDialog();
            try {

                JSONObject mObject = new JSONObject((String) obj);
                if (mObject.getInt("code") == 200) {
                    if (mObject.getString("table_name").equalsIgnoreCase(AppConstants.USERS_GOALS)) {
                        if (mObject.getString("purpose").equalsIgnoreCase("add_goal")) {
                            //TODO ....
                            GoalBean tObj = new GoalBean(mObject.getString("name"));
                            tObj.setId(mObject.getString("object_id"));
                            tObj.setTimeInMillis(mObject.getLong("date_in_millis"));
                            tObj.setCreatedTimeMillis(mObject.getLong("created_time_millis"));
                            tObj.setStartTime(mObject.getLong("start_time"));
                            tObj.setEndTime(mObject.getLong("end_time"));
                            tObj.setDays(mObject.getInt("no_of_days"));
                            tObj.setType(AppConstants.GOAL_STATUS_ARRAY[0]);
                            ArrayList<GoalBean> dataList = children.get(getResources().getString(R.string.txt_present));
                            dataList.add(0, tObj);
                            children.put(getResources().getString(R.string.txt_present), dataList);
                            mGoalDragNDropAdapter = new GoalDragNDropAdapter(getActivity(),GoalsFragment.this, GoalsFragment.this, children, null);
                            dndListView.setAdapter(mGoalDragNDropAdapter);
                            dndListView.expandGroup(1);
                            dndListView.expandGroup(0);
                        } else if (mObject.getString("purpose").equalsIgnoreCase("update_task_id")) {
                            long keyVal = mObject.getLong("created_time_millis");
                            ArrayList<GoalBean> dataList = children.get(getResources().getString(R.string.txt_present));
                            boolean isToday = false;
                            for (int i = 0; i < dataList.size(); i++) {
                                GoalBean tObj = dataList.get(i);
                                if (tObj.getTimeInMillis() == keyVal) {
                                    isToday = true;
                                    tObj.setId(mObject.getString("object_id"));
                                    break;
                                }
                            }
                            if (isToday) {
                                children.put(getResources().getString(R.string.txt_present), dataList);
                                mGoalDragNDropAdapter.notifyDataSetChanged();

                                return;
                            }
                            //TODO For tomorrow List Update
                            ArrayList<GoalBean> dataListTomorrow = children.get(getResources().getString(R.string.txt_future));
                            for (int i = 0; i < dataListTomorrow.size(); i++) {
                                GoalBean tObj = dataListTomorrow.get(i);
                                if (tObj.getTimeInMillis() == keyVal) {
                                    isToday = true;
                                    tObj.setId(mObject.getString("object_id"));
                                    break;
                                }
                            }
                            if (isToday) {
                                children.put(getResources().getString(R.string.txt_future), dataListTomorrow);
                                mGoalDragNDropAdapter.notifyDataSetChanged();
                                return;
                            }


                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {

            List<ParseObject> mObjList = (List<ParseObject>) obj;
            if(serviceType.equals(AppConstants.USERS_GOALS)) {
                ArrayList<GoalBean> mPresentBean = new ArrayList<>();
                ArrayList<GoalBean> mFutureBean = new ArrayList<>();
                //TODO Set the data in the list...
                ArrayList<String> groups = new ArrayList<String>();
                groups.add(getResources().getString(R.string.txt_present));
                groups.add(getResources().getString(R.string.txt_future));
                children.clear();
                //TODO get the last day of the week...
                ArrayList<String> mGoalsList = new ArrayList<String>();
                ArrayList<Long> mGoalsCreatedList = new ArrayList<Long>();
                for (int i = 0; i < mObjList.size(); i++) {
                    ParseObject pObj = mObjList.get(i);
                    GoalBean objGoal = new GoalBean(pObj.getString("name"));
                    objGoal.setId(pObj.getObjectId());
                    objGoal.setTimeInMillis(pObj.getLong("date_in_millis"));
                    objGoal.setCreatedTimeMillis(pObj.getLong("created_time_millis"));
                    objGoal.setStartTime(pObj.getLong("start_time"));
                    objGoal.setEndTime(pObj.getLong("end_time"));
                    objGoal.setDays(pObj.getInt("no_of_days"));
                    long databaseDate = pObj.getLong("date_in_millis");
                    if (objGoal.getStartTime() > AppUtils.getCurrentDateInMillies()) {
                        objGoal.setType(AppConstants.GOAL_STATUS_ARRAY[1]);
                        mFutureBean.add(objGoal);
                    } else {
                        objGoal.setType(AppConstants.GOAL_STATUS_ARRAY[0]);
                        mPresentBean.add(objGoal);
                    }
                    mGoalsCreatedList.add(pObj.getLong("created_time_millis"));
                    mGoalsList.add(pObj.getObjectId());
                }

                children.put(getResources().getString(R.string.txt_present), mPresentBean);
                children.put(getResources().getString(R.string.txt_future), mFutureBean);
                if(mGoalsList.isEmpty()){
                    ProgressDialogUtil.getInstance().dismissProgressDialog();
                    mGoalDragNDropAdapter = new GoalDragNDropAdapter(getActivity(),GoalsFragment.this, GoalsFragment.this, children, null);
                    dndListView.setAdapter(mGoalDragNDropAdapter);
                }else {
                    serviceType = AppConstants.USERS_GOALS_STATUS;
                    ParseUtils.getInstance().getGoalsStatus(GoalsFragment.this, AppConstants.USERS_GOALS_STATUS, mGoalsList,mGoalsCreatedList);
                }
            }else if(serviceType.equals(AppConstants.USERS_GOALS_STATUS)){
                //TODO Add the data in the bean...
                ArrayList<GoalBean> mPresentBean = children.get(getResources().getString(R.string.txt_present));
                for(int i=0;i<mPresentBean.size();i++){
                    boolean isDone=false;
                    String goalId=mPresentBean.get(i).getId();
                    long goalCreatedTime=mPresentBean.get(i).getCreatedTimeMillis();
                    ArrayList<Long> mCompletedGoalsList=new ArrayList<>();
                    for(int j=0;j<mObjList.size();j++){
                        if(goalId.equals(mObjList.get(j).getString("goal_id")) ||
                                goalCreatedTime==mObjList.get(j).getLong("goal_created_time")){
                            mCompletedGoalsList.add(mObjList.get(j).getLong("date_in_millis"));
                            if(mObjList.get(j).getLong("date_in_millis")==AppUtils.getCurrentDateInMillies()){
                                isDone=true;
                            }
                        }
                    }
                    GoalBean mGoalObject=mPresentBean.get(i);
                    mGoalObject.setListOfCompletedGoalsDate(mCompletedGoalsList);
                    mGoalObject.setIsCompleted(isDone);
                    mPresentBean.remove(i);
                    mPresentBean.add(i,mGoalObject);

                }
                //TODO Getting all information...
                ProgressDialogUtil.getInstance().dismissProgressDialog();
                children.put(getResources().getString(R.string.txt_present),mPresentBean);
                mGoalDragNDropAdapter = new GoalDragNDropAdapter(getActivity(),GoalsFragment.this, GoalsFragment.this, children, null);
                dndListView.setAdapter(mGoalDragNDropAdapter);
                dndListView.expandGroup(1);
                dndListView.expandGroup(0);
            }


        }
    }

    @Override
    public void onParseQueryFail(Object obj) {

    }
}
