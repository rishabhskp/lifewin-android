///*
// * Copyright (C) 2012 Sreekumar SH
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package app.lifewin.adapter;
//
//import android.content.Context;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseExpandableListAdapter;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import app.lifewin.R;
//import app.lifewin.constants.AppConstants;
//import app.lifewin.dialog.DialogUtils;
//import app.lifewin.interfaces.IEditTaskListener;
//import app.lifewin.interfaces.IOpenGroupListener;
//import app.lifewin.model.app.GoalBean;
//import app.lifewin.model.app.TaskBean;
//import app.lifewin.parse.interfaces.IParseQueryResult;
//import app.lifewin.parse.utils.ParseUtils;
//import app.lifewin.preferences.MySharedPreference;
//import app.lifewin.ui.activity.HomeActivity;
//import app.lifewin.ui.fragment.GoalsFragment;
//import app.lifewin.ui.views.calendar.CalendarDay;
//import app.lifewin.ui.views.calendar.MaterialCalendarView;
//import app.lifewin.utils.AppUtils;
//import app.lifewin.utils.NetworkStatus;
//
///**
// * Adapter for the drag and drop listview
// *
// * @author <a href="http://sreekumar.sh" >Sreekumar SH </a>
// *         (sreekumar.sh@gmail.com)
// */
//public final class GoalDragNDropAdapterq extends BaseExpandableListAdapter implements IParseQueryResult, IEditTaskListener {
//
//    private int selectedGroup;
//    private int selectedChild;
//    private Context mContext;
//
//    private GoalsFragment goalsFragment;
//    private LayoutInflater mInflater;
//    private ArrayList<String> groups;
//    private Map<String, ArrayList<GoalBean>> children;
//    private IOpenGroupListener iOpenGroupListener;
//    private MySharedPreference mySharedPreference;
//    private MaterialCalendarView materialCalendarView = null;
//
//    public GoalDragNDropAdapterq(Context context, GoalsFragment goalsFragment, IOpenGroupListener iOpenGroupListener, Map<String, ArrayList<GoalBean>> child, LinearLayout mView) {
//        this.goalsFragment=goalsFragment;
//        init(context, iOpenGroupListener, child, mView);
//    }
//
//    private void init(Context context, IOpenGroupListener iOpenGroupListener,
//                      Map<String, ArrayList<GoalBean>> child, final LinearLayout mView) {
//        // Cache the LayoutInflate to avoid asking for a new one each time.
//        mInflater = LayoutInflater.from(context);
//        groups = new ArrayList<String>();
//        groups.addAll(child.keySet());
//        mContext = context;
//        children = child;
//        this.iOpenGroupListener = iOpenGroupListener;
//        mySharedPreference = MySharedPreference.getInstance(mContext);
//        new Thread() {
//            @Override
//            public void run() {
//                materialCalendarView = new MaterialCalendarView(mContext);
//                materialCalendarView.setSelectionColor(mContext.getResources().getColor(R.color.color_white), false);
////                RelativeLayout.LayoutParams layoutParams =
////                        new RelativeLayout.LayoutParams(materialCalendarView.getWidth(),materialCalendarView.getHeight());
////                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
////                materialCalendarView.setLayoutParams(layoutParams);
//                /*((Activity)mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mView.addView(materialCalendarView);
//                        mView.setTag(-1);
//                    }
//                });*/
//
//            }
//        }.start();
//
//
//    }
//
//    public void onPick(int[] position) {
//        selectedGroup = position[0];
//        selectedChild = position[1];
//    }
//
//    @Override
//    public void getTaskInfo(String title, String points, String position) {
////TODO get the update value and update on the server...
//        String[] startenddate = points.split("~!@");
//        String[] from = position.split("@");
//        GoalBean obj = (GoalBean) (getChild(Integer.parseInt(from[0]), Integer.parseInt(from[1])));
//        HashMap<String, Object> values = new HashMap<String, Object>();
//        values.put("name", title);
//        values.put("start_time", AppUtils.convertDateInMillis(startenddate[0]));
//        values.put("end_time", AppUtils.convertDateInMillis(startenddate[1]));
//        if (NetworkStatus.isInternetOn(mContext)) {
//            String value = mContext.getResources().getString(R.string.txt_present);
//            if (Integer.parseInt(from[0]) == 1) {
//                value = mContext.getResources().getString(R.string.txt_future);
//            }
//            children.get(value).get(Integer.parseInt(from[1])).setTitle(title);
//            children.get(value).get(Integer.parseInt(from[1])).setStartTime((Long) values.get("start_time"));
//            children.get(value).get(Integer.parseInt(from[1])).setEndTime((Long) values.get("end_time"));
//            notifyDataSetChanged();
//            ParseUtils.getInstance().onUpdateTaskDetailsPQuery(GoalDragNDropAdapterq.this, AppConstants.USERS_GOALS, obj.getId(), obj.getCreatedTimeMillis(), values);
//        } else {
//            Toast.makeText(mContext, R.string.err_internet_connection_error, Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    @Override
//    public void getDeleteTask(String position) {
//        String[] from = position.split("@");
//        GoalBean obj = (GoalBean) (getChild(Integer.parseInt(from[0]), Integer.parseInt(from[1])));
//        if (NetworkStatus.isInternetOn(mContext)) {
//            ParseUtils.getInstance().onDeleteGoalsPQuery(GoalDragNDropAdapterq.this, AppConstants.USERS_GOALS, obj.getId(), obj.getCreatedTimeMillis() + "", position);
//        } else {
//            Toast.makeText(mContext, R.string.err_internet_connection_error, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    static class ViewHolder {
//        TextView title;
//        TextView startDate;
//        TextView noDays;
//        ImageView ivDone, ivEdit, ivDelete;
//        RelativeLayout ll_calendarview;
//    }
//
//    public void onDrop(int[] from, int[] to) {
//        if (to[0] > children.size() || to[0] < 0 || to[1] < 0)
//            return;
//        GoalBean tValue = getValue(from);
//        children.get(children.keySet().toArray()[from[0]]).remove(tValue);
//        children.get(children.keySet().toArray()[to[0]]).add(to[1], tValue);
//        selectedGroup = -1;
//        selectedChild = -1;
//        notifyDataSetChanged();
//        //TODO Check the drop frame is open or not if not than open the group.
//        iOpenGroupListener.onGroupSelected(to[0]);
//    }
//
//    private GoalBean getValue(int[] id) {
//        return children.get(children.keySet().toArray()[id[0]]).get(id[1]);
//    }
//
//
//    @Override
//    public Object getChild(int groupPosition, int childPosition) {
//        return children.get(children.keySet().toArray()[groupPosition]).get(childPosition);
//    }
//
//    @Override
//    public long getChildId(int groupPosition, int childPosition) {
//        return childPosition;
//    }
//
//    @Override
//    public View getChildView(int groupPosition, int childPosition,
//                             boolean isLastChild, View convertView, ViewGroup parent) {
//        final ViewHolder holder;
//        if (convertView == null) {
//            convertView = mInflater.inflate(R.layout.include_goal_row_item, null);
//            holder = new ViewHolder();
//            holder.title = (TextView) convertView.findViewById(R.id.tv_title);
//            holder.startDate = (TextView) convertView.findViewById(R.id.tv_date);
//            holder.noDays = (TextView) convertView.findViewById(R.id.tv_days);
//            holder.ivEdit = (ImageView) convertView.findViewById(R.id.iv_edit_task);
//            holder.ivDone = (ImageView) convertView.findViewById(R.id.iv_task_done);
//            holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_del_goal);
//            holder.ll_calendarview = (RelativeLayout) convertView.findViewById(R.id.ll_calendarview);
//            convertView.setTag(R.id.view_holder_tag, holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag(R.id.view_holder_tag);
//        }
//        GoalBean obj = (GoalBean) (getChild(groupPosition, childPosition));
//        holder.title.setText(obj.getTitle());
//        holder.startDate.setText(AppUtils.convertMillisToDate(obj.getStartTime()));
//        holder.noDays.setText((obj.getEndTime()-obj.getStartTime())==0?"1":(obj.getEndTime()-obj.getStartTime())/AppConstants.ONE_DAY_INTERVAL+ " days");
//        holder.ivDone.setTag(groupPosition + "@" + childPosition);
//        holder.ivDone.setOnClickListener(mTaskDoneListener);
//        if (obj.isCompleted()) {
//            holder.ivDone.setVisibility(View.INVISIBLE);
//            holder.ivDone.setOnClickListener(null);
//        } else {
//            holder.ivDone.setVisibility(View.VISIBLE);
//        }
//
//        holder.ivEdit.setTag(groupPosition + "@" + childPosition);
//        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String[] postion = v.getTag().toString().split("@");
//                GoalBean obj = (GoalBean) (getChild(Integer.parseInt(postion[0]), Integer.parseInt(postion[1])));
//
//                new DialogUtils().showEditGoalDialog(mContext, obj.getTitle(), AppUtils.convertMillisToDate(obj.getStartTime()),
//                        AppUtils.convertMillisToDate(obj.getEndTime()), v.getTag().toString(), GoalDragNDropAdapterq.this);
//            }
//        });
//
//        if (groupPosition != selectedGroup && childPosition != selectedChild) {
//            convertView.setVisibility(View.VISIBLE);
//        }
//        holder.ivDelete.setTag(groupPosition + "@" + childPosition);
//        holder.ivDelete.setOnClickListener(onGoalDeleteListener);
//        holder.ll_calendarview.setTag(childPosition);
//        convertView.setTag(R.id.view_holder_pos, groupPosition + "@" + childPosition);
//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String[] pos = v.getTag(R.id.view_holder_pos).toString().split("@");
//                int[] from = new int[2];
//                from[0] = Integer.parseInt(pos[0]);
//                from[1] = Integer.parseInt(pos[1]);
//
//                MaterialCalendarView materialCalendarView= (MaterialCalendarView) holder.ll_calendarview.getChildAt(0);
////                if (materialCalendarView.getParent() != null && from[1] != Integer.parseInt(((RelativeLayout) materialCalendarView.getParent()).getTag().toString())) {
////                    ((RelativeLayout) materialCalendarView.getParent()).removeAllViews();
////                }
//                if (holder.ll_calendarview.getChildCount() == 0) {
//                    ArrayList<Long> mCompletedDate = ((GoalBean) (getChild(from[0], from[1]))).getListOfCompletedGoalsDate();
//                    if (mCompletedDate != null) {
//                        ArrayList<CalendarDay> mlist = new ArrayList<CalendarDay>();
//                        for (int i = 0; i < mCompletedDate.size(); i++) {
//                            Date mDate = new Date();
//                            mDate.setTime(mCompletedDate.get(i));
//                            mlist.add(CalendarDay.from(mDate));
//                        }
//                        materialCalendarView.setBackColorDate(mlist);
//                    }
//                    materialCalendarView.setSelectionColor(mContext.getResources().getColor(R.color.color_app_green), true);
//                    materialCalendarView.setVisibility(View.VISIBLE);
//                    holder.ll_calendarview.setGravity(Gravity.CENTER_HORIZONTAL);
////                    holder.ll_calendarview.addView(materialCalendarView);
//                } else {
//                    materialCalendarView.setSelectionColor(mContext.getResources().getColor(R.color.color_transparent), false);
////                    ArrayList<CalendarDay> mlist = new ArrayList<CalendarDay>();
////                    materialCalendarView.setBackColorDate(mlist);
//                    materialCalendarView.setVisibility(View.GONE);
////                    ((RelativeLayout) materialCalendarView.getParent()).removeAllViews();
//                }
//            }
//        });
//        return convertView;
//    }
//
//
//    private View.OnClickListener onGoalDeleteListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            showDialogForDeleteTask(v.getTag().toString());
//        }
//    };
//
//    private void showDialogForDeleteTask(String tagValue) {
//        String[] pos = tagValue.split("@");
//        int[] from = new int[2];
//        from[0] = Integer.parseInt(pos[0]);
//        from[1] = Integer.parseInt(pos[1]);
//        new DialogUtils().showDeleteDialog(mContext, tagValue, GoalDragNDropAdapterq.this, "Delete Goal", "Do you want to delete goal?");
//    }
//
//    private View.OnLongClickListener mTaskDoneLongListener = new View.OnLongClickListener() {
//        @Override
//        public boolean onLongClick(View v) {
//            String[] pos = v.getTag().toString().split("@");
//            int[] from = new int[2];
//            from[0] = Integer.parseInt(pos[0]);
//            from[1] = Integer.parseInt(pos[1]);
//            int[] to = new int[2];
//            to[0] = 4;
//            to[1] = 0;
//            int points = ((TaskBean) (getChild(from[0], from[1]))).getPoints();
//            mySharedPreference.setTodayPoints(mySharedPreference.getTodayPoints() + points);
//            onDrop(from, to);
//            ((HomeActivity) mContext).updatePoints();
//            return false;
//            //TODO Add current points in the database or current day points
//        }
//    };
//    /**
//     * This listener is used for moving the ongoing task to completed task
//     */
//    private View.OnClickListener mTaskDoneListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            String[] pos = v.getTag().toString().split("@");
//            int[] from = new int[2];
//            from[0] = Integer.parseInt(pos[0]);
//            from[1] = Integer.parseInt(pos[1]);
//            HashMap<String, Object> mValues = new HashMap<String, Object>();
//            long dateInMillis = AppUtils.getCurrentDateInMillies();
//            mValues.put("date_in_millis", dateInMillis);
//            mValues.put("goal_id", ((GoalBean) (getChild(from[0], from[1]))).getId());
//            ParseUtils.getInstance().setGoalCompletedPQuery(GoalDragNDropAdapterq.this, AppConstants.USERS_GOALS_STATUS, mValues, v.getTag().toString());
//
//        }
//    };
//
//    @Override
//    public int getChildrenCount(int groupPosition) {
//        return children.get(children.keySet().toArray()[groupPosition]).size();
//
//    }
//
//    @Override
//    public Object getGroup(int groupPosition) {
//        return groups.get(groupPosition);
//    }
//
//    @Override
//    public int getGroupCount() {
//        return groups.size();
//    }
//
//    @Override
//    public long getGroupId(int groupPosition) {
//        return groupPosition;
//    }
//
//    @Override
//    public View getGroupView(int groupPosition, boolean isExpanded,
//                             View convertView, ViewGroup parent) {
//        ViewHolderGroup holder;
//        if (convertView == null) {
//            convertView = mInflater.inflate(R.layout.include_group_row_item, null);
//            holder = new ViewHolderGroup();
//            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title_item);
//            holder.tvCount = (TextView) convertView.findViewById(R.id.tv_count);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolderGroup) convertView.getTag();
//        }
//
//        holder.tvTitle.setText(groups.get(groupPosition));
//
//        holder.tvCount.setText(children.get(groups.get(groupPosition)).size() + "");
//
//        return convertView;
//    }
//
//
//    static class ViewHolderGroup {
//        TextView tvTitle;
//        TextView tvCount;
//
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return true;
//    }
//
//    @Override
//    public boolean isChildSelectable(int groupPosition, int childPosition) {
//        return true;
//    }
//
//    @Override
//    public void onParseQuerySuccess(Object obj) {
//        //TODO update the ui for complete goal for today
//        if (obj instanceof String) {
//            try {
//                JSONObject mRes = new JSONObject((String) obj);
//                if (mRes.getInt("code") == 200) {
//                    if (mRes.getString("table_name").equals(AppConstants.USERS_GOALS_STATUS)) {
//                        String[] pos = mRes.getString("tag_value").split("@");
//                        int[] mPos = new int[2];
//                        mPos[0] = Integer.parseInt(pos[0]);
//                        mPos[1] = Integer.parseInt(pos[1]);
//                        //TODO update the main bean...
//                        GoalBean mObject = getValue(mPos);
//                        mObject.setIsCompleted(true);
//                        ArrayList<Long> mComGoalList = mObject.getListOfCompletedGoalsDate();
//                        if (mComGoalList == null) {
//                            mComGoalList = new ArrayList<Long>();
//                        }
//                        mComGoalList.add(AppUtils.getCurrentDateInMillies());
//                        mObject.setListOfCompletedGoalsDate(mComGoalList);
//                        notifyDataSetChanged();
//                    } else if (mRes.getString("table_name").equals(AppConstants.USERS_GOALS)) {
//                        String[] from = mRes.getString("tag_value").split("@");
//                        String value = mContext.getResources().getString(R.string.txt_present);
//                        if (Integer.parseInt(from[0]) == 1) {
//                            value = mContext.getResources().getString(R.string.txt_future);
//                        }
//                        children.get(value).remove(Integer.parseInt(from[1]));
//                        notifyDataSetChanged();
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }else if(obj instanceof Boolean){
//            if((boolean)obj) {
//                   ((GoalsFragment)goalsFragment).getData();
//            }
//        }
//    }
//
//    @Override
//    public void onParseQueryFail(Object obj) {
//
//    }
//}