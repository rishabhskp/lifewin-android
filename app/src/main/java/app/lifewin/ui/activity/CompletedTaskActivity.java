package app.lifewin.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import app.lifewin.R;
import app.lifewin.constants.AppConstants;
import app.lifewin.dialog.DialogUtils;
import app.lifewin.interfaces.IEditTaskListener;
import app.lifewin.model.app.TaskBean;
import app.lifewin.parse.interfaces.IParseQueryResult;
import app.lifewin.parse.utils.ParseUtils;
import app.lifewin.utils.ProgressDialogUtil;
import app.lifewin.utils.Toaster;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class CompletedTaskActivity extends Activity implements IParseQueryResult, IEditTaskListener {


    private ArrayList<TaskBean> mTaskList = new ArrayList<TaskBean>();
    private CompleteTaskAdapter mCompleteTaskAdapter;
    @InjectView(R.id.lv_list)
    ListView lvCompletedTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_task);
        ButterKnife.inject(this);


        mCompleteTaskAdapter=new CompleteTaskAdapter();
        lvCompletedTask.setAdapter(mCompleteTaskAdapter);
        //TODO Make query for getting all completed task...
        ProgressDialogUtil.getInstance().showProgressDialog(CompletedTaskActivity.this);
        List<String> mQList = new ArrayList<String>(1);
        mQList.add("status");
        List<String> mQPList = new ArrayList<String>(1);
        mQPList.add(AppConstants.STATUS_ARRAY[0]);
        ParseUtils.getInstance().onFetchCompletedTaskQuery(CompletedTaskActivity.this, AppConstants.USERS_TASKS, mQList, mQPList);
    }

    @OnClick(R.id.iv_back)
    public void onBackPress() {
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }


    @Override
    public void onParseQuerySuccess(Object obj) {
        ProgressDialogUtil.getInstance().dismissProgressDialog();
        if (obj != null) {
            if (obj instanceof String) {

            } else {
                List<ParseObject> mList = (List<ParseObject>) obj;
                mTaskList.clear();
                for (int i = 0; i < mList.size(); i++) {
                    TaskBean objT = new TaskBean(mList.get(i).getString("name"), mList.get(i).getInt("points"));
                    objT.setId(mList.get(i).getObjectId());
                    objT.setCreatedTimeInMillis(mList.get(i).getLong("created_time_millis"));
                    mTaskList.add(objT);
                }
                mCompleteTaskAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onParseQueryFail(Object obj) {
        ProgressDialogUtil.getInstance().dismissProgressDialog();
        if (obj instanceof ParseException) {
            Toaster.show(CompletedTaskActivity.this, ((ParseException) obj).getMessage());
        }
    }

    @Override
    public void getTaskInfo(String title, String points, String position) {

    }

    @Override
    public void getDeleteTask(String position) {
        ParseUtils.getInstance().onDeletePQuery(CompletedTaskActivity.this, AppConstants.USERS_TASKS,
                mTaskList.get(Integer.parseInt(position)).getId(), mTaskList.get(Integer.parseInt(position)).getCreatedTimeInMillis() + "");
        mTaskList.remove(Integer.parseInt(position));
        mCompleteTaskAdapter.notifyDataSetChanged();
    }

    private class CompleteTaskAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTaskList.size();
        }

        @Override
        public Object getItem(int position) {
            return mTaskList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(CompletedTaskActivity.this).inflate(R.layout.include_task_row_item, null);
                convertView.findViewById(R.id.iv_edit_task).setVisibility(View.GONE);
                convertView.findViewById(R.id.iv_task_done).setVisibility(View.GONE);
                holder.title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.points = (TextView) convertView.findViewById(R.id.tv_points);
                holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_del_task);
                convertView.setTag(R.id.view_holder_tag, holder);
            } else {
                holder = (ViewHolder) convertView.getTag(R.id.view_holder_tag);
            }
            holder.title.setText(mTaskList.get(position).getTitle());
            holder.points.setText(mTaskList.get(position).getPoints() + " points");
            holder.ivDelete.setTag(position);
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DialogUtils().showDeleteDialog(CompletedTaskActivity.this, v.getTag().toString(), CompletedTaskActivity.this, "Delete Task", "Do you want to delete task?");

                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView title;
            TextView points;
            ImageView ivDone, ivEdit, ivDelete;
        }

    }
}
