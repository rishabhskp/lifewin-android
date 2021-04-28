package app.lifewin;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;

import app.lifewin.ui.views.EditTextField;
import app.lifewin.ui.views.TextField;
import app.lifewin.utils.Logger;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


/**
 * This class is used to manage application level stuff.
 */


public class LifeWinApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "sQocFcqMYLnI1XvhXYpUkAvZGXnsDO2vxKIMhw65", "Gx7yXeQYqVcX9GNvIATsEAOwuPQyErb9TuHlGozk");
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/OPENSANS_REGULAR.TTF")
                        .setFontAttrId(R.attr.fontPath)
                        .addCustomStyle(TextField.class, R.attr.textFieldStyle)
                        .addCustomStyle(EditTextField.class, R.attr.textFieldStyle)
                        .build()
        );

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Logger.e("App Termnate:--->");

    }
}
