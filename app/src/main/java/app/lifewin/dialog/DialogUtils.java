package app.lifewin.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.lifewin.R;
import app.lifewin.interfaces.IEditTaskListener;
import app.lifewin.interfaces.IImagePickOption;
import app.lifewin.utils.AppUtils;

/**
 * Class is used for showing the dialogs.
 */
public class DialogUtils {

    /**
     * Dialog for showing the image pick option
     *
     * @param context             Activity context
     * @param mIasImagePickOption Interface for callback
     * @param title               Title of the dialog
     */
    public void showImagePickDialog(Context context, final IImagePickOption mIasImagePickOption, String title) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder
                .setMessage(title)
                .setCancelable(true)
                .setPositiveButton(R.string.txt_signup_gallery, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new Thread() {
                            public void run() {
                                mIasImagePickOption.onImagePickOption(1);
                            }
                        }.start();
                    }
                })
                .setNegativeButton(R.string.txt_signup_camera, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mIasImagePickOption.onImagePickOption(2);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    /**
     * Dialog for edit task information like name,points.
     *
     * @param context           Activity Context
     * @param taskTitle         Task Title
     * @param taskPoints        Task points
     * @param position          Task position in the list
     * @param objectId          TaskId
     * @param iEditTaskListener Interface for callback
     */
    public void showEditTaskDialog(final Context context, final String taskTitle, final String taskPoints,
                                   final String position, String objectId, final IEditTaskListener iEditTaskListener) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_edit_task);
        final EditText edTaskTitle = (EditText) dialog.findViewById(R.id.ed_task_title);
        final EditText edTaskPoints = (EditText) dialog.findViewById(R.id.ed_task_points);
        edTaskPoints.setText(taskPoints);
        edTaskTitle.setText(taskTitle);
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
                String mTaskTitle = edTaskTitle.getText().toString().trim();
                String mTaskPoints = edTaskPoints.getText().toString().trim();
                if (TextUtils.isEmpty(mTaskTitle)) {
                    Toast.makeText(context, R.string.txt_err_input_task, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mTaskPoints)) {
                    Toast.makeText(context, R.string.txt_err_input_task_points, Toast.LENGTH_SHORT).show();
                } else if (mTaskPoints.equals(taskPoints) && mTaskTitle.equals(taskTitle)) {
                    dialog.dismiss();
                } else {
                    //TODO Update task info on database and list
//                    if (NetworkStatus.isInternetOn(context)) {
                        iEditTaskListener.getTaskInfo(mTaskTitle, mTaskPoints, position);
                        dialog.dismiss();
//                    } else {
//                        Toast.makeText(context, R.string.err_internet_connection_error, Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();
//                    }
                }
            }
        });
        dialog.show();
    }

    /**
     * @param context
     * @param goalTitle
     * @param startDate
     * @param endDate
     * @param position
     * @param iEditTaskListener
     */
    public void showEditGoalDialog(final Context context, final String goalTitle, final String startDate,
                                   final String endDate, final String position, final IEditTaskListener iEditTaskListener) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_edit_goal);
        final EditText edTitle = (EditText) dialog.findViewById(R.id.ed_goal_title);
        final TextView tvStartTime = (TextView) dialog.findViewById(R.id.tv_start_date);
        final TextView tvEndTime = (TextView) dialog.findViewById(R.id.tv_end_date);
        tvStartTime.setText("Start Date : " + startDate);
        tvEndTime.setText("Due Date : " + endDate);
        edTitle.setText(goalTitle);
        Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        dialog.show();
        dialog.findViewById(R.id.rl_parent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tvStartTime.findViewById(R.id.tv_start_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] dateValue = startDate.split("/");
                DatePickerDialog dialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tvStartTime.setText("Start Date : " + (monthOfYear + 1) + "/" + dayOfMonth + "/" + year);

                            }
                        }, Integer.parseInt(dateValue[2]), Integer.parseInt(dateValue[0]) - 1, Integer.parseInt(dateValue[1]));
                dialog.show();
            }
        });

        tvEndTime.findViewById(R.id.tv_end_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] dateValue = endDate.split("/");
                DatePickerDialog dialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tvEndTime.setText("Due Date : " + (monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                            }
                        }, Integer.parseInt(dateValue[2]), Integer.parseInt(dateValue[0]) - 1, Integer.parseInt(dateValue[1]));
                dialog.show();
            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mTaskTitle = edTitle.getText().toString().trim();
                if (TextUtils.isEmpty(mTaskTitle)) {
                    Toast.makeText(context, R.string.txt_err_input_goal, Toast.LENGTH_SHORT).show();
                } else {
                    Date startDate = null;
                    Date endDate = null;
                    try {
                        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy"); // Month.Day.Year
//                        startDate = formatter.parse(tvStartTime.getText().toString().replace("Start Date :", ""));
                        endDate = formatter.parse(tvEndTime.getText().toString().replace("Due Date :", ""));
                        /*if (endDate.before(startDate)) {
                            Toast.makeText(context, R.string.err_enddate_before_startdate, Toast.LENGTH_SHORT).show();
                        }else */
                        if (endDate.before(new Date(AppUtils.getCurrentDateInMillies()))) {
                            Toast.makeText(context, R.string.err_enddate_before_today, Toast.LENGTH_SHORT).show();
                        } else {

                            //TODO Update task info on database and list
//                            if (NetworkStatus.isInternetOn(context)) {
                                iEditTaskListener.getTaskInfo(mTaskTitle, tvStartTime.getText().toString().replace("Start Date :", "")
                                        + "~!@" + tvEndTime.getText().toString().replace("End Date :", ""), position);
                                dialog.dismiss();
//                            } else {
//                                Toast.makeText(context, R.string.err_internet_connection_error, Toast.LENGTH_SHORT).show();
//                                dialog.dismiss();
//                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        dialog.show();


    }

    /**
     * Dialog for delete the object from the list.
     *
     * @param mContext          Activity context
     * @param position          Position of object in list
     * @param iEditTaskListener Callback Listener
     * @param title             Title of dialog
     * @param msg               Message of dialog
     */
    public void showDeleteDialog(final Context mContext, final String position,
                                 final IEditTaskListener iEditTaskListener, String title, String msg) {
        final Dialog dialog = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_confirmatiom);
        Button btnOk = (Button) dialog.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        TextView tvMsg = (TextView) dialog.findViewById(R.id.tv_msg);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
        tvMsg.setText(msg);
        tvTitle.setText(title);
        dialog.show();
        View mParent = dialog.findViewById(R.id.rl_parent);
        mParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (NetworkStatus.isInternetOn(mContext)) {
                    iEditTaskListener.getDeleteTask(position);
                    dialog.dismiss();
//                } else {
//                    Toast.makeText(mContext, R.string.err_internet_connection_error, Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                }
            }
        });
        dialog.show();
    }
}
