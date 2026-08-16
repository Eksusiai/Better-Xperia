package com.sonymobile.calendar.tablet;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.sonymobile.calendar.CalendarSettingsActivity;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.linkedin.LinkedInUtils;

/* JADX INFO: loaded from: classes2.dex */
public class TabletCalendarPreferenceActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!Utils.isTabletDevice(this)) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setClassName(this, CalendarSettingsActivity.class.getName());
            intent.setFlags(537001984);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.tablet_preference_activity);
        this.mToolbar = (Toolbar) findViewById(R.id.toolbarPreferenceTablet);
        setTitle(R.string.preferences_title);
        this.mToolbar.setBackgroundResource(R.color.toolbar_background_color);
        setSupportActionBar(this.mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayOptions(28);
        }
        Utils.setToolbarBackNavigation(this, this.mToolbar);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                finish();
                return true;
            }
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        LinkedInUtils.handleSyncWithLinkedInResult(this, i, i2, intent);
    }

    public void replacePreferenceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransactionBeginTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransactionBeginTransaction.replace(R.id.preferencePanel, fragment);
        fragmentTransactionBeginTransaction.addToBackStack(null);
        fragmentTransactionBeginTransaction.commitAllowingStateLoss();
    }

    public void presentPreferencePanel(Bundle bundle, String str, String str2) {
        FragmentTransaction fragmentTransactionBeginTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransactionBeginTransaction.replace(R.id.preferencePanel, TabletViewPreferences.instantiate(this, str, bundle));
        fragmentTransactionBeginTransaction.addToBackStack(str);
        fragmentTransactionBeginTransaction.commitAllowingStateLoss();
    }
}
