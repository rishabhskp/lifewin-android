/*
Copyright 2015 Google Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package app.lifewin.ui.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import app.lifewin.R;
import app.lifewin.adapter.LeftMenuAdapter;
import app.lifewin.constants.AppConstants;
import app.lifewin.parse.interfaces.IParseQueryResult;
import app.lifewin.parse.utils.ParseUtils;
import app.lifewin.preferences.MySharedPreference;
import app.lifewin.utils.AppUtils;
import app.lifewin.utils.HideKeyboard;
import app.lifewin.utils.ImageDisplay;
import app.lifewin.utils.Logger;
import app.lifewin.utils.NetworkStatus;
import app.lifewin.utils.ProgressDialogUtil;
import app.lifewin.utils.TakeImageClass;
import app.lifewin.utils.Toaster;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Tha app's main activity
 */
public class HomeActivity extends AppCompatActivity implements IParseQueryResult {
    static final String PREF_LAST_SCREEN_ID = "selected_screen_id";
    static final String PREF_OPEN_DRAWER_AT_STARTUP = "open_drawer_at_startup";

    private ActionBarDrawerToggle mDrawerToggle;
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.navigation_drawer)
    FrameLayout mDrawerView;
    @InjectView(R.id.navigation_drawer_menu)
    ListView mDrawerMenu;
    @InjectView(R.id.navigation_drawer_scrim)
    View mDrawerScrim;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.bg_trans)
    View bgView;
    @InjectView(R.id.ll_add_points)
    LinearLayout llAddPoints;
    @InjectView(R.id.ed_add_points)
    EditText edAddPoints;
    @InjectView(R.id.tv_points)
    TextView tvTotalPoints;


    private MainMenu mMainMenu;
    private Fragment mFragment;

    private LeftMenuAdapter leftMenuAdapter;
    private View mHeaderView;
    private TextView tvTitle;
    private View iv_menu;
    private boolean isMenuUpdate;

    private MySharedPreference mySharedPreference;
    //TODO
    private ProfileUpdateBroadcast profileUpdateBroadcast;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);
        mySharedPreference = MySharedPreference.getInstance(getApplicationContext());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        createCutomActionBarTitle();
        TypedArray colorPrimaryDark =
                getTheme().obtainStyledAttributes(new int[]{R.attr.colorPrimaryDark});
        mDrawerLayout.setStatusBarBackgroundColor(colorPrimaryDark.getColor(0, 0xFF000000));
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        colorPrimaryDark.recycle();
        String profileImage;
        if (TextUtils.isEmpty(MySharedPreference.getInstance(HomeActivity.this).getUserProfileImage())) {
            profileImage = MySharedPreference.getInstance(HomeActivity.this).getUserProfileImageF();
        } else {
            profileImage = MySharedPreference.getInstance(HomeActivity.this).getUserProfileImage();
        }
        setLeftMenuHeader(MySharedPreference.getInstance(HomeActivity.this).getName(), profileImage);
        mDrawerMenu.addHeaderView(mHeaderView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Set the drawer width accordingly with the guidelines: window_width - toolbar_height.
            toolbar.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (left == 0 && top == 0 && right == 0 && bottom == 0) {
                        return;
                    }
                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    float logicalDensity = metrics.density;
                    int maxWidth = (int) Math.ceil(320 * logicalDensity);
                    DrawerLayout.LayoutParams params =
                            (DrawerLayout.LayoutParams) mDrawerView.getLayoutParams();
                    int newWidth = view.getWidth() - view.getHeight();
                    params.width = ((newWidth > maxWidth ? maxWidth : newWidth)) - 100;
                    mDrawerView.setLayoutParams(params);
                }
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            mDrawerView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
                @Override
                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                    // Set scrim height to match status bar height.
                    mDrawerScrim.setLayoutParams(new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            insets.getSystemWindowInsetTop()));
                    return insets;
                }
            });
        }


        mMainMenu = new MainMenu(this);
        leftMenuAdapter = new LeftMenuAdapter(HomeActivity.this, mMainMenu.getEntries());
        mMainMenu.getEntries();
        mDrawerMenu.setAdapter(leftMenuAdapter);
        mDrawerMenu.setOnItemClickListener(onItemClickListener);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                // The user learned how to open the drawer. Do not open it for him anymore.
                HideKeyboard.keyboardHide(HomeActivity.this);
                getAppPreferences().edit()
                        .putBoolean(PREF_OPEN_DRAWER_AT_STARTUP, false).apply();
                super.onDrawerOpened(drawerView);
            }
        };

        boolean activityResumed = (savedState != null);
        boolean openDrawer = getAppPreferences().getBoolean(PREF_OPEN_DRAWER_AT_STARTUP, false);
        int lastScreenId = getAppPreferences().getInt(PREF_LAST_SCREEN_ID, 0);
        selectItem(0);
        if (!activityResumed && openDrawer) {
            mDrawerLayout.openDrawer(mDrawerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        profileUpdateBroadcast = new ProfileUpdateBroadcast();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(profileUpdateBroadcast, new IntentFilter("profile_update"));
        updatePoints();
    }


    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                Intent mIntent = new Intent(HomeActivity.this, EditProfileActivity.class);
                startActivity(mIntent);
                return;
            }
            selectItem(position - 1);
            mDrawerLayout.closeDrawer(mDrawerView);
        }
    };

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {
        super.onSupportActionModeStarted(mode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Set a status bar color while in action mode (text copy&paste)
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_900));
        }
    }

    @OnClick(R.id.bg_trans)
    void onHideView() {
        onDismissTransView();
    }

    @OnClick(R.id.iv_add)
    void showPointView(){
        llAddPoints.setVisibility(View.VISIBLE);
        bgView.setVisibility(View.VISIBLE);
    }
    @OnClick(R.id.tv_cancel)
    void onCancel() {
        onDismissTransView();
    }

    @OnClick(R.id.tv_ok)
    void onOk() {
        if (TextUtils.isEmpty(edAddPoints.getText().toString().trim())) {
            Toast.makeText(HomeActivity.this, R.string.val_enter_points, Toast.LENGTH_SHORT).show();
        } else {
            //TODO update in the database
            HideKeyboard.keyboardHide(HomeActivity.this);
            onAddPointsP(Integer.parseInt(edAddPoints.getText().toString().trim()), "manually");
        }
    }

    @OnClick(R.id.tv_2_points)
    void on2Points() {
        HideKeyboard.keyboardHide(HomeActivity.this);
        onAddPointsP(2, "manually");
    }

    @OnClick(R.id.tv_5_points)
    void on5Points() {
        HideKeyboard.keyboardHide(HomeActivity.this);
        onAddPointsP(5, "manually");
    }


    @OnClick(R.id.tv_10_points)
    void on10Points() {
        HideKeyboard.keyboardHide(HomeActivity.this);
        onAddPointsP(10, "manually");
    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {
        super.onSupportActionModeFinished(mode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Reset status bar to transparent when leaving action mode (text copy&paste)
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
        }
    }


    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCurrentDate();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    /**
     * Method is used for check the current data with last sync date.If date is changed in that case
     * need to update the UI and data.
     */
    private void checkCurrentDate() {
        String currentDate = MySharedPreference.getInstance(getApplicationContext()).getLastSyncDate();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String date = sdf.format(new Date());
        if (!currentDate.equalsIgnoreCase(date)) {
//TODO Reset the data on Fregement...
            MySharedPreference.getInstance(getApplicationContext()).setTodayPoints(0);
            fetchWeeklyPoints();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow_menu, menu);
        return true;
    }

    private void selectItem(int pos) {
        if (pos < 0 || pos >= mMainMenu.getEntries().length) {
            pos = 0;
        }
        if (pos == mMainMenu.getEntries().length - 1) {
            //TODO for Logout
            if (NetworkStatus.isInternetOn(HomeActivity.this)) {
                ProgressDialogUtil.getInstance().showProgressDialog(HomeActivity.this);
                ParseUtils.getInstance().onLogoutPUser(HomeActivity.this);
            } else {
                Toaster.show(HomeActivity.this, R.string.err_internet_connection_error);
            }
        } else {
            if (tvTitle != null)
                tvTitle.setText(mMainMenu.getEntries()[pos].getTitle());
//        getSupportActionBar().setTitle(mMainMenu.getEntries()[pos].getTitle());
            String nextFragmentTag = "FRAGMENT_TAG_" + Integer.toString(pos);
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (currentFragment != null && nextFragmentTag.equals(currentFragment.getTag())) {
                return;
            }
            Fragment recycledFragment = getSupportFragmentManager().findFragmentByTag(nextFragmentTag);
            try {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (currentFragment != null) {
                    transaction.detach(currentFragment);
                }
                if (recycledFragment != null) {
                    transaction.attach(recycledFragment);
                } else {
                    mFragment = mMainMenu.createFragment(pos);
                    transaction.add(R.id.container, mMainMenu.createFragment(pos), nextFragmentTag);
                }
                transaction.commit();
                getSupportFragmentManager().executePendingTransactions();
                // The header takes the first position.
                mDrawerMenu.setItemChecked(pos + 1, true);
                getAppPreferences().edit().putInt(PREF_LAST_SCREEN_ID, pos).apply();
            } catch (InstantiationException e) {
                Logger.w("Error while instantiating the selected fragment" + e.getLocalizedMessage());
            } catch (IllegalAccessException e) {
                Logger.w("Error while instantiating the selected fragment" + e.getLocalizedMessage());
            }
        }
    }

    private SharedPreferences getAppPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_search) {

        }/* else if (item.getItemId() == R.id.item_add) {
            llAddPoints.setVisibility(View.VISIBLE);
            bgView.setVisibility(View.VISIBLE);
        }*/
        return (mDrawerToggle.onOptionsItemSelected(item)
                || super.onOptionsItemSelected(item));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(profileUpdateBroadcast);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TakeImageClass.REQUEST_CODE_TAKE_PICTURE || requestCode == TakeImageClass.REQUEST_CODE_GALLERY || requestCode == TakeImageClass.REQUEST_CODE_CROP_IMAGE) {

        }
    }


    public interface RefreshableFragment {
        void refresh();
    }

    /**
     * Method for creating the header view for left menu.
     *
     * @param name      UserName to Display
     * @param imagePath ImagePath to display
     * @return
     */
    private void setLeftMenuHeader(String name, String imagePath) {
        mHeaderView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.include_left_menu_header, null);
        ImageView imageView = (ImageView) mHeaderView.findViewById(R.id.iv_user_img);
        TextView tvName = (TextView) mHeaderView.findViewById(R.id.tv_name);
        tvName.setText(name);
        tvName.setTag(name);
        if (!TextUtils.isEmpty(imagePath)) {
            new ImageDisplay().scaleAndLoadBitmap(HomeActivity.this, imagePath, imageView);
            imageView.setTag(imagePath);
        } else {
            imageView.setTag("image");
        }
    }

    private void updateLeftMenuID() {
        if (mHeaderView != null) {

            if (mHeaderView.findViewById(R.id.iv_user_img) != null && mHeaderView.findViewById(R.id.iv_user_img).getTag() != null && !mHeaderView.findViewById(R.id.iv_user_img).getTag().equals(MySharedPreference.getInstance(HomeActivity.this).getUserProfileImage())) {
                String imagePath = MySharedPreference.getInstance(HomeActivity.this).getUserProfileImage();
                if (!TextUtils.isEmpty(imagePath)) {
                    ImageView imageView = (ImageView) mHeaderView.findViewById(R.id.iv_user_img);
                    new ImageDisplay().scaleAndLoadBitmap(HomeActivity.this, imagePath, imageView);
                    imageView.setTag(imagePath);
                }
            }
            if (mHeaderView.findViewById(R.id.tv_name) != null && mHeaderView.findViewById(R.id.tv_name).getTag() != null && !mHeaderView.findViewById(R.id.tv_name).getTag().equals(MySharedPreference.getInstance(HomeActivity.this).getName())) {
                String name = MySharedPreference.getInstance(HomeActivity.this).getName();
                TextView tvName = (TextView) mHeaderView.findViewById(R.id.tv_name);
                tvName.setText(name);
                tvName.setTag(name);
            }

        }
    }

    private void createCutomActionBarTitle() {
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.include_custom_header, null);
        tvTitle = (TextView) v.findViewById(R.id.tv_title);
        iv_menu = v.findViewById(R.id.iv_menu);
        //assign the view to the actionbar
        this.getSupportActionBar().setCustomView(v);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        this.getSupportActionBar().setHomeButtonEnabled(true);
        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(mDrawerView);
            }
        });
    }


    private class ProfileUpdateBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateLeftMenuID();
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.gc();
                }
            }, 3000);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * This method is used for adding the points in the parse database.
     *
     * @param points Points Values
     * @param type   Points types
     */
    private void onAddPointsP(int points, String type) {
//        if (NetworkStatus.isInternetOn(HomeActivity.this)) {
            onDismissTransView();
            ProgressDialogUtil.getInstance().showProgressDialog(HomeActivity.this);
            HashMap<String, Object> mValues = new HashMap<String, Object>();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String date = sdf.format(new Date());
            mValues.put("date", date);
            try {
                SimpleDateFormat sdfM = new SimpleDateFormat("MM/dd/yyyy");
                Date dateM = sdfM.parse(date);
                mValues.put("date_in_millis", dateM.getTime()); //TODO
            } catch (java.text.ParseException e) {
                e.printStackTrace();
                mValues.put("date_in_millis", System.currentTimeMillis()); //TODO
            }
//            mValues.put("user_id", MySharedPreference.getInstance(getApplicationContext()).getUserId());
            mValues.put("points_type", type);
            mValues.put("points", points);
            ParseUtils.getInstance().onSavePointsPQuery(HomeActivity.this, AppConstants.USERS_POINTS, mValues);
//        } else {
//            mySharedPreference.setTodayPoints(mySharedPreference.getTodayPoints() + points);
//            mySharedPreference.setWeeklyPoints(mySharedPreference.getWeeklyPoints() + points);
//            //TODO Remove it
//            onDismissTransView();
//            updatePoints();
//            //END
//            Toast.makeText(HomeActivity.this, R.string.err_internet_connection_error, Toast.LENGTH_SHORT).show();
//        }
    }


    private void onDismissTransView() {
        edAddPoints.setText("");
        bgView.setVisibility(View.GONE);
        llAddPoints.setVisibility(View.GONE);
    }

    private void onFetchUserTodayPoints() {


    }

    public void updatePoints() {
        tvTotalPoints.setText(mySharedPreference.getTodayPoints() + "/" + mySharedPreference.getWeeklyPoints());
    }

    @Override
    public void onParseQuerySuccess(Object obj) {
        ProgressDialogUtil.getInstance().dismissProgressDialog();
        if (obj instanceof String) {
            try {
                JSONObject json = new JSONObject((String) obj);
                if (json.getInt("code") == 200) {
                    if (json.getString("table_name").equalsIgnoreCase(AppConstants.USERS_POINTS)) {
                        int points = json.getInt("points");
                        mySharedPreference.setTodayPoints(mySharedPreference.getTodayPoints() + points);
                        mySharedPreference.setWeeklyPoints(mySharedPreference.getWeeklyPoints() + points);
                        updatePoints();
                    } else if (json.getString("table_name").equals("weekly_points")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                        String date = sdf.format(new Date());
                        mySharedPreference.setLastSyncDate(date);
                        updatePoints();
                    }
                } else if (json.getInt("code") == 201) {
                    MySharedPreference.getInstance(this).resetAll();
                    Intent mIntent = new Intent(this, LoginActivity.class);
                    startActivity(mIntent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onParseQueryFail(Object obj) {
        ProgressDialogUtil.getInstance().dismissProgressDialog();
        if (obj instanceof ParseException) {
            Toast.makeText(getApplicationContext(), ((ParseException) obj).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //TODO fetch weekly points.
    public void fetchWeeklyPoints() {
        String firstDay = MySharedPreference.getInstance(getApplicationContext()).getFirstDay();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String todayDay = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());
        if (firstDay.equalsIgnoreCase(todayDay)) {
            //TODO Week First Day...
            MySharedPreference.getInstance(getApplicationContext()).setWeeklyPoints(0);
            ParseUtils.getInstance().onFetchTotalPoints(HomeActivity.this, AppConstants.USERS_POINTS, AppUtils.getCurrentDateInMillies(), AppUtils.getCurrentDateInMillies() + AppConstants.ONE_DAY_INTERVAL);
        } else {
            Calendar cal = Calendar.getInstance();

            int weekDay = 0;
            if (firstDay.equalsIgnoreCase("Saturday")) {
                weekDay = Calendar.SATURDAY;
            } else if (firstDay.equalsIgnoreCase("Sunday")) {
                weekDay = Calendar.SUNDAY;
            } else if (firstDay.equalsIgnoreCase("Monday")) {
                weekDay = Calendar.MONDAY;
            }

            while (cal.get(Calendar.DAY_OF_WEEK) != weekDay)
                cal.add(Calendar.DAY_OF_WEEK, -1);

            Logger.e("Date = " + cal.getTimeInMillis() + " " + cal.getTime());
            long currentTim = 0,weekStartTime=0;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                String dateCC = sdf.format(cal.getTime());
                SimpleDateFormat sdfM = new SimpleDateFormat("MM/dd/yyyy");
                Date dateMC = sdfM.parse(dateCC);
                weekStartTime = dateMC.getTime();


                String dateC = sdf.format(new Date());
                Date dateM = sdfM.parse(dateC);
                currentTim = dateM.getTime();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
//TODO Make Query for fetching the points...
            ParseUtils.getInstance().onFetchTotalPoints(HomeActivity.this, AppConstants.USERS_POINTS, weekStartTime, currentTim + AppConstants.ONE_DAY_INTERVAL);

//            Calendar cal = Calendar.getInstance();
//            cal.add(Calendar.DATE, -7);
//            Logger.e("Date = "+ cal.getTime());
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
//            String dateP = sdf.format(cal.getTime());
//            Logger.e("Date = "+ dateP);
        }

    }
}
