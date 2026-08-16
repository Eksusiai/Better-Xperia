package com.sonymobile.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.sonymobile.calendar.linkedin.LinkedInUtils;
import com.sonymobile.calendar.tablet.TabletCalendarPreferenceActivity;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarSettingsActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.preference_with_toolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.pref_toolbar);
        this.mToolbar = toolbar;
        toolbar.setBackgroundColor(UiUtils.getPrimaryColor(getApplicationContext()));
        Toolbar toolbar2 = (Toolbar) findViewById(R.id.pref_toolbar_head);
        toolbar2.setBackgroundResource(R.color.toolbar_background_color);
        setSupportActionBar(toolbar2);
        setTitle(R.string.preferences_title);
        getSupportActionBar().setDisplayOptions(12);
        Utils.setToolbarBackNavigation(this, toolbar2);
        UiUtils.setNavigationBar(this);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment fragmentFindFragmentById = supportFragmentManager.findFragmentById(R.id.pref_content_frame);
        if (fragmentFindFragmentById == null) {
            fragmentFindFragmentById = new GeneralPreferences();
        }
        supportFragmentManager.beginTransaction().replace(R.id.pref_content_frame, fragmentFindFragmentById).commit();
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
                return false;
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        LinkedInUtils.handleSyncWithLinkedInResult(this, i, i2, intent);
        super.onActivityResult(i, i2, intent);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onMultiWindowModeChanged(boolean z) {
        super.onMultiWindowModeChanged(z);
        if (Utils.isTabletDevice(this)) {
            switchToTabletMode();
        }
    }

    private void switchToTabletMode() {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setClassName(this, TabletCalendarPreferenceActivity.class.getName());
        intent.setFlags(537001984);
        startActivity(intent);
        finish();
    }

    public void updateToolbar(boolean z) {
        if (z) {
            this.mToolbar.setVisibility(0);
        } else {
            this.mToolbar.setVisibility(8);
        }
    }

    public Toolbar getToolbar() {
        return this.mToolbar;
    }
}
