package com.sonymobile.calendar.tasks.settings;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class SettingsActivity extends AppCompatActivity {
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.tasks_settings_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle(getResources().getString(R.string.preferences_title));
        toolbar.setBackgroundResource(R.color.toolbar_background_color);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UiUtils.setNavigationBar(this);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment fragmentFindFragmentById = supportFragmentManager.findFragmentById(R.id.task_settings_pref_content_frame);
        if (fragmentFindFragmentById == null) {
            fragmentFindFragmentById = new SettingsFragment();
        }
        supportFragmentManager.beginTransaction().replace(R.id.task_settings_pref_content_frame, fragmentFindFragmentById).commit();
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar == null) {
            return true;
        }
        supportActionBar.setDisplayOptions(4, 4);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
