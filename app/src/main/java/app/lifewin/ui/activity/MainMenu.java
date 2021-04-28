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

import android.support.v4.app.Fragment;

import java.util.LinkedHashMap;

import app.lifewin.R;
import app.lifewin.ui.fragment.GoalsFragment;
import app.lifewin.ui.fragment.HabitsFragment;
import app.lifewin.ui.fragment.HelpFragment;
import app.lifewin.ui.fragment.OverviewFragment;
import app.lifewin.ui.fragment.PomodoroMeterFragment;
import app.lifewin.ui.fragment.SettingsFragment;
import app.lifewin.ui.fragment.TasksFragment;
import app.lifewin.ui.fragment.TimerFragment;
import app.lifewin.ui.fragment.WinStreakFragment;

/**
 * Drawer menu for the app
 */
public class MainMenu {
    private final HomeActivity mActivity;
    private LinkedHashMap<LeftMenuItem, Class<? extends Fragment>> mMenu;

    public MainMenu(HomeActivity a) {
        mActivity = a;
        mMenu = new LinkedHashMap<>();

        mMenu.put(new LeftMenuItem(R.drawable.ic_task,R.string.txt_tasks), TasksFragment.class);
        mMenu.put(new LeftMenuItem(R.drawable.ic_goals,R.string.txt_goals), GoalsFragment.class);
        mMenu.put(new LeftMenuItem(R.drawable.ic_habits,R.string.txt_habits), HabitsFragment.class);
        mMenu.put(new LeftMenuItem(R.drawable.ic_timer,R.string.txt_timer), TimerFragment.class);
        mMenu.put(new LeftMenuItem(R.drawable.ic_promodo_timer,R.string.txt_pomodoro_meter),PomodoroMeterFragment.class);
        mMenu.put(new LeftMenuItem(R.drawable.ic_win_stack,R.string.txt_win_streak), WinStreakFragment.class);
        mMenu.put(new LeftMenuItem(R.drawable.ic_overview,R.string.txt_overview),OverviewFragment.class);
        mMenu.put(new LeftMenuItem(R.drawable.ic_help,R.string.txt_help), HelpFragment.class);
        mMenu.put(new LeftMenuItem(R.drawable.ic_setting,R.string.txt_settings), SettingsFragment.class);
        mMenu.put(new LeftMenuItem(R.drawable.ic_setting,R.string.txt_signout), null);
    }

    public LeftMenuItem[] getEntries() {
        return mMenu.keySet().toArray(new LeftMenuItem[mMenu.size()]);
    }

    public Fragment createFragment(int position)
            throws InstantiationException, IllegalAccessException {
        return mMenu.get(getEntries()[position]).newInstance();
    }


    public class LeftMenuItem{
        private int resId,titleId;

        public LeftMenuItem(int resId,int titleId){
            this.resId=resId;
            this.titleId=titleId;
        }
        public int getResId() {
            return resId;
        }

        public void setResId(int resId) {
            this.resId = resId;
        }

        public int getTitle() {
            return titleId;
        }

        public void setTitle(int titleId) {
            this.titleId = titleId;
        }
    }
}
