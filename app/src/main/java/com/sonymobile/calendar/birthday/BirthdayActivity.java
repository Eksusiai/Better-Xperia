package com.sonymobile.calendar.birthday;
import com.sonymobile.calendar.SafeTime;

import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.permissions.PermissionHandlerActivity;
import com.sonymobile.calendar.permissions.PermissionItem;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class BirthdayActivity extends PermissionHandlerActivity {
    public static final String DISPLAYED_TIME = "displayed_time";
    private BirthdayIconHeader mBirthdayHeader;
    private ListView mBirthdayListView;
    private ContactBirthdayLoaderBinder mBirthdayLoader;
    private Time mSelectedTime;

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.birthday_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        UiUtils.setViewBackgroundToPrimaryColor(this, toolbar);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.birthday_activity));
        ListView listView = (ListView) findViewById(R.id.birthday_listview);
        this.mBirthdayListView = listView;
        listView.setChoiceMode(1);
        this.mBirthdayListView.setVerticalScrollBarEnabled(false);
        this.mBirthdayHeader = (BirthdayIconHeader) findViewById(R.id.birthday_icon_header);
        Bundle extras = getIntent().getExtras();
        Long lValueOf = Long.valueOf(Utils.getDisplayTime());
        if (extras != null && extras.containsKey(DISPLAYED_TIME)) {
            lValueOf = Long.valueOf(extras.getLong(DISPLAYED_TIME));
        }
        Time time = new SafeTime(Utils.getTimeZone(this, null));
        this.mSelectedTime = time;
        time.set(lValueOf.longValue());
        this.mBirthdayHeader.update(this.mSelectedTime);
        if (isEssentialPermissionsGranted()) {
            initBirthdayLoader();
        }
    }

    private void initBirthdayLoader() {
        this.mBirthdayLoader = new ContactBirthdayLoaderBinder(this, this.mBirthdayListView, this.mSelectedTime);
        Fragment fragmentFindFragmentByTag = getSupportFragmentManager().findFragmentByTag(PhoneNumberPicker.TAG);
        if (fragmentFindFragmentByTag != null) {
            this.mBirthdayLoader.setDialogListener(fragmentFindFragmentByTag);
        }
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public PermissionItem[] getRequiredPermission() {
        return new PermissionItem[]{new PermissionItem("android.permission.READ_CALENDAR", null, null, R.drawable.ic_calendar_event, true), new PermissionItem("android.permission.READ_CONTACTS", null, null, R.drawable.ic_contact, true)};
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public void onRequestPermissionResult(String[] strArr, int[] iArr) {
        ContactBirthdayLoaderBinder contactBirthdayLoaderBinder = this.mBirthdayLoader;
        if (contactBirthdayLoaderBinder == null) {
            initBirthdayLoader();
        } else {
            contactBirthdayLoaderBinder.refreshAdapter();
        }
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        ContactBirthdayLoaderBinder contactBirthdayLoaderBinder = this.mBirthdayLoader;
        if (contactBirthdayLoaderBinder != null) {
            contactBirthdayLoaderBinder.refreshAdapter();
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayOptions(4, 4);
        }
        TextView textView = (TextView) findViewById(R.id.actionbar_title);
        if (textView != null) {
            textView.setText(getResources().getString(R.string.birthday_activity));
        }
        return super.onCreateOptionsMenu(menu);
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
