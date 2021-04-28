package app.lifewin.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class DialogUtils {



    public void showAlertDialog(Context context,String title,String message){

        final AlertDialog.Builder dialogB=new AlertDialog.Builder(context);
        dialogB.setTitle(title);
        dialogB.setMessage(message);
        dialogB.setCancelable(false);
        dialogB.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertD=dialogB.create();
        alertD.show();
    }

}
