package app.lifewin.utils;

import android.app.ProgressDialog;
import android.content.Context;

import app.lifewin.R;


public class ProgressDialogUtil {
    private ProgressDialog mProgressDialog;
    private static ProgressDialogUtil mInstance;

    public static ProgressDialogUtil getInstance(){
        if(mInstance==null){
            mInstance=new ProgressDialogUtil();
        }
        return mInstance;
    }

    /**
     * Show the progress dialog...
     * @param context Context should be activity context
     */
    public void showProgressDialog(Context context){
        if(mProgressDialog==null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage(context.getString(R.string.msg_plz_wait));
//            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }else if(mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }

        mProgressDialog.show();
    }

    /**
     * Hide the progress dialog...
     */
    public void dismissProgressDialog(){
        if(mProgressDialog!=null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
            mProgressDialog=null;
        }
    }

}
