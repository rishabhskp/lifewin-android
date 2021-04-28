/*
 * Copyright (C) 2012 Sreekumar SH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.lifewin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.lifewin.R;
import app.lifewin.constants.AppConstants;
import app.lifewin.dialog.DialogUtils;
import app.lifewin.interfaces.IEditTaskListener;
import app.lifewin.interfaces.IOpenGroupListener;
import app.lifewin.model.app.TaskBean;
import app.lifewin.parse.interfaces.IParseQueryResult;
import app.lifewin.parse.utils.ParseUtils;
import app.lifewin.preferences.MySharedPreference;
import app.lifewin.ui.activity.HomeActivity;
import app.lifewin.ui.fragment.TasksFragment;
import app.lifewin.utils.AppUtils;

/**
 * Adapter for the drag and drop listview
 */
public final class TaskDragNDropAdapter extends BaseExpandableListAdapter implements IEditTaskListener, IParseQueryResult {

    private int selectedGroup;
    private int selectedChild;
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<String> groups;
    private Map<String, ArrayList<TaskBean>> children;
    private IOpenGroupListener iOpenGroupListener;
    private IParseQueryResult iParseQueryResult;
    private MySharedPreference mySharedPreference;

    public TaskDragNDropAdapter(Context context, TasksFragment iOpenGroupListener, Map<String, ArrayList<TaskBean>> child) {
        init(context, iOpenGroupListener, child);
    }

    private void init(Context context, TasksFragment iOpenGroupListener,
                      Map<String, ArrayList<TaskBean>> child) {
        // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
        groups = new ArrayList<String>();
        groups.addAll(child.keySet());
        mContext = context;
        children = child;
        this.iOpenGroupListener = (IOpenGroupListener) iOpenGroupListener;
        this.iParseQueryResult = (IParseQueryResult) iOpenGroupListener;
        mySharedPreference = MySharedPreference.getInstance(mContext);

    }

    public void onPick(int[] position) {
        selectedGroup = position[0];
        selectedChild = position[1];
    }

    @Override
    public void getTaskInfo(String title, String points, String position) {
        String[] from = position.split("@");
        TaskBean obj = (TaskBean) (getChild(Integer.parseInt(from[0]), Integer.parseInt(from[1])));
        HashMap<String, Object> values = new HashMap<String, Object>();
        values.put("name", title);
        values.put("points", Integer.parseInt(points));
//        if (NetworkStatus.isInternetOn(mContext)) {
            String value = mContext.getResources().getString(R.string.txt_today);
            if (Integer.parseInt(from[0]) == 1) {
                value = mContext.getResources().getString(R.string.txt_tomorrow);
            } else if (Integer.parseInt(from[0]) == 2) {
                value = mContext.getResources().getString(R.string.txt_this_week);
            } else if (Integer.parseInt(from[0]) == 3) {
                value = mContext.getResources().getString(R.string.txt_later);
            }
            children.get(value).get(Integer.parseInt(from[1])).setTitle(title);
            children.get(value).get(Integer.parseInt(from[1])).setPoints(Integer.parseInt(points));
            notifyDataSetChanged();
            ParseUtils.getInstance().onUpdateTaskDetailsPQuery(TaskDragNDropAdapter.this, AppConstants.USERS_TASKS, obj.getId(), obj.getCreatedTimeInMillis(), values);
//        } else {
//            Toast.makeText(mContext, R.string.err_internet_connection_error, Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void getDeleteTask(String position) {
        String[] from = position.split("@");
        TaskBean obj = (TaskBean) (getChild(Integer.parseInt(from[0]), Integer.parseInt(from[1])));
//        if (NetworkStatus.isInternetOn(mContext)) {
            String value = mContext.getResources().getString(R.string.txt_today);
            if (Integer.parseInt(from[0]) == 1) {
                value = mContext.getResources().getString(R.string.txt_tomorrow);
            } else if (Integer.parseInt(from[0]) == 2) {
                value = mContext.getResources().getString(R.string.txt_this_week);
            } else if (Integer.parseInt(from[0]) == 3) {
                value = mContext.getResources().getString(R.string.txt_later);
            }
            children.get(value).remove(Integer.parseInt(from[1]));
            notifyDataSetChanged();
            ParseUtils.getInstance().onDeletePQuery(TaskDragNDropAdapter.this, AppConstants.USERS_TASKS, obj.getId(), obj.getCreatedTimeInMillis() + "");
//        } else {
//            Toast.makeText(mContext, R.string.err_internet_connection_error, Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onParseQuerySuccess(Object obj) {

    }

    @Override
    public void onParseQueryFail(Object obj) {

    }

    static class ViewHolder {
        TextView title;
        TextView points;
        ImageView ivDone, ivEdit, ivDelete;
    }

    /**
     * Method is calling when user drop the view ... on other category
     * Need to update the database for task time.
     *
     * @param from
     * @param to
     */
    public void onDrop(int[] from, int[] to) {
        if (to[0] > children.size() || to[0] < 0 || to[1] < 0)
            return;
        TaskBean tValue = getValue(from);
        if (from[0] != to[0])
            updateTaskDatabase(tValue, to[0]);
        children.get(children.keySet().toArray()[from[0]]).remove(tValue);
        if (to[0] != 4) {
            children.get(children.keySet().toArray()[to[0]]).add(to[1], tValue);
        }
        selectedGroup = -1;
        selectedChild = -1;
        notifyDataSetChanged();
        //TODO Check the drop frame is open or not if not than open the group.
        if (to[0] != 4) {
            iOpenGroupListener.onGroupSelected(to[0]);
        }
    }

    /**
     * Method is used for updating the database for task...
     */
    private void updateTaskDatabase(TaskBean object, int dropPosition) {
        switch (dropPosition) {
            case 0:
                // Add the task into tdoday list
                ParseUtils.getInstance().onUpdateTasksList(iParseQueryResult, object.getId(), object.getCreatedTimeInMillis(), AppUtils.getCurrentDateInMillies());
                break;
            case 1:
                ParseUtils.getInstance().onUpdateTasksList(iParseQueryResult, object.getId(), object.getCreatedTimeInMillis(), AppUtils.getCurrentDateInMillies() + AppConstants.ONE_DAY_INTERVAL);
                break;
            case 2:
                ParseUtils.getInstance().onUpdateTasksList(iParseQueryResult, object.getId(), object.getCreatedTimeInMillis(), AppUtils.getCurrentDateInMillies() + (AppConstants.ONE_DAY_INTERVAL * 2));
                break;
            case 3:
                ParseUtils.getInstance().onUpdateLaterTasksList(iParseQueryResult, object.getId(), object.getCreatedTimeInMillis(), AppConstants.STATUS_ARRAY[2]);
                break;
            case 4:
                //Complete task and add the points into points table.
                ParseUtils.getInstance().onCompleteTask(iParseQueryResult, object.getId(), object.getCreatedTimeInMillis(), object.getPoints());
                break;

        }

    }

    private TaskBean getValue(int[] id) {
        return children.get(children.keySet().toArray()[id[0]]).get(id[1]);
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children.get(children.keySet().toArray()[groupPosition]).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.include_task_row_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.points = (TextView) convertView.findViewById(R.id.tv_points);
            holder.ivEdit = (ImageView) convertView.findViewById(R.id.iv_edit_task);
            holder.ivDone = (ImageView) convertView.findViewById(R.id.iv_task_done);
            holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_del_task);
            convertView.setTag(R.id.view_holder_tag, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.id.view_holder_tag);
        }
        TaskBean obj = (TaskBean) (getChild(groupPosition, childPosition));
        if (groups.get(groupPosition).equalsIgnoreCase(mContext.getResources().getString(R.string.txt_complete))) {
            holder.ivDone.setVisibility(View.GONE);
            holder.ivEdit.setVisibility(View.GONE);
            holder.ivDone.setOnClickListener(null);
            holder.ivEdit.setOnClickListener(null);
        } else {
            holder.ivDone.setVisibility(View.VISIBLE);
            holder.ivEdit.setVisibility(View.VISIBLE);
        }
        holder.title.setText(obj.getTitle());
        holder.points.setText(obj.getPoints() + " points");
        holder.ivDone.setTag(groupPosition + "@" + childPosition);
        convertView.setTag(R.id.view_holder_pos, groupPosition + "@" + childPosition);
        holder.ivDone.setOnClickListener(mTaskDoneListener);
        holder.ivEdit.setTag(groupPosition + "@" + childPosition);
        holder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogForEditTask(v.getTag().toString());
            }
        });
        holder.ivDelete.setTag(groupPosition + "@" + childPosition);
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogForDeleteTask(v.getTag().toString());
            }
        });
        if (groupPosition != selectedGroup && childPosition != selectedChild) {
            convertView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }


    private void showDialogForEditTask(String tagValue) {
        String[] pos = tagValue.split("@");
        int[] from = new int[2];
        from[0] = Integer.parseInt(pos[0]);
        from[1] = Integer.parseInt(pos[1]);
        TaskBean mTaskBean = (TaskBean) (getChild(from[0], from[1]));
        new DialogUtils().showEditTaskDialog(mContext, mTaskBean.getTitle(), mTaskBean.getPoints() + "", tagValue, mTaskBean.getId(), TaskDragNDropAdapter.this);
    }


    private void showDialogForDeleteTask(String tagValue) {
        String[] pos = tagValue.split("@");
        int[] from = new int[2];
        from[0] = Integer.parseInt(pos[0]);
        from[1] = Integer.parseInt(pos[1]);
        new DialogUtils().showDeleteDialog(mContext, tagValue, TaskDragNDropAdapter.this, "Delete Task", "Do you want to delete task?");
    }

    /**
     * This listener is used for moving the ongoing task to completed task
     */
    private View.OnClickListener mTaskDoneListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String[] pos = v.getTag().toString().split("@");
            int[] from = new int[2];
            from[0] = Integer.parseInt(pos[0]);
            from[1] = Integer.parseInt(pos[1]);
            TaskBean tValue = getValue(from);
            children.get(children.keySet().toArray()[from[0]]).remove(tValue);
            updateTaskDatabase(tValue, 4);
            notifyDataSetChanged();
//            mySharedPreference.setTodayPoints(mySharedPreference.getTodayPoints() + points);
//            onDrop(from, to);
            ((HomeActivity) mContext).updatePoints();
            //TODO Add current points in the database or current day points
        }
    };

    /**
     * This listener is used for moving the ongoing task to completed task
     */
    private View.OnLongClickListener mTaskDoneLongListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            String[] pos = v.getTag(R.id.view_holder_pos).toString().split("@");
            int[] from = new int[2];
            from[0] = Integer.parseInt(pos[0]);
            from[1] = Integer.parseInt(pos[1]);
            int[] to = new int[2];
            to[0] = 4;
            to[1] = 0;
            int points = ((TaskBean) (getChild(from[0], from[1]))).getPoints();
//            mySharedPreference.setTodayPoints(mySharedPreference.getTodayPoints() + points);
            onDrop(from, to);
            ((HomeActivity) mContext).updatePoints();
            return false;
        }


    };

    @Override
    public int getChildrenCount(int groupPosition) {
        return children.get(children.keySet().toArray()[groupPosition]).size();

    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ViewHolderGroup holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.include_group_row_item, null);
            holder = new ViewHolderGroup();
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title_item);
            holder.tvCount = (TextView) convertView.findViewById(R.id.tv_count);
            holder.rlBg = (RelativeLayout) convertView.findViewById(R.id.rl_grp_bg_row);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderGroup) convertView.getTag();
        }


        holder.tvTitle.setText(groups.get(groupPosition));
        if (groupPosition != 4) {
            holder.tvCount.setText(children.get(groups.get(groupPosition)).size() + "");
        } else {
            holder.tvCount.setText("");
        }

        holder.rlBg.setBackgroundColor(mContext.getResources().getColor(R.color.color_white));
        if (!isExpanded) {
            if (children.get(groups.get(groupPosition)).size() > 0) {
                holder.rlBg.setBackgroundColor(mContext.getResources().getColor(R.color.color_app_light_green));
            }
        }

        return convertView;
    }


    static class ViewHolderGroup {
        TextView tvTitle;
        TextView tvCount;
        RelativeLayout rlBg;

    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}