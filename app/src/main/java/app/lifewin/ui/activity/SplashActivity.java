package app.lifewin.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import app.lifewin.R;
import app.lifewin.constants.AppConstants;
import app.lifewin.preferences.MySharedPreference;
import app.lifewin.utils.EncryptUtils;
import app.lifewin.utils.Logger;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }


        setContentView(R.layout.activity_splash);
        printHashKey();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //if user is login than landing on the home screen else open the login options screen
                if (MySharedPreference.getInstance(getApplicationContext()).getLogin()) {
                    Intent mIntent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(mIntent);
                } else {
//                    Intent mIntent = new Intent(SplashActivity.this, HomeActivity.class);
                    Intent mIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(mIntent);
                }
                finish();
            }
        }, 1000);

        mkDirFile();

    }

    @Override
    public void onBackPressed() {
    }

    /**
     * Method is used for fetching the key-hash of facebook.
     */
    private void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "app.lifewin", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Logger.e("TAG", "printHashKey() Hash Key: " + hashKey + "Passs:-" + EncryptUtils.newPassword());
            }
        } catch (NoSuchAlgorithmException e) {
            Logger.e("TAG", "printHashKey()");
        } catch (Exception e) {
            Logger.e("TAG", "printHashKey()");
        }
    }

    private void mkDirFile(){
        File mFile=new File(AppConstants.CHILD_FOLDER_PROFILE_IMAGES);
        if(!mFile.isDirectory()){
            mFile.mkdir();
        }
    }
}
