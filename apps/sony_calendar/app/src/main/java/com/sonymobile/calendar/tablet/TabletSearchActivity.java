package com.sonymobile.calendar.tablet;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.actions.SearchIntents;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.SearchActivity;
import com.sonymobile.calendar.permissions.PermissionHandlerActivity;
import com.sonymobile.calendar.permissions.PermissionItem;
import com.sonymobile.calendar.utils.PermissionUtils;
import com.sonymobile.calendar.utils.UiUtils;
import com.sonymobile.lunar.lib.LunarContract;

/* JADX INFO: loaded from: classes2.dex */
public class TabletSearchActivity extends PermissionHandlerActivity {
    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Build.VERSION.SDK_INT >= 24 && isInMultiWindowMode()) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setClassName(this, SearchActivity.class.getName());
            intent.putExtra(LunarContract.EXTRA_EVENT_BEGIN_TIME, 0);
            intent.putExtra(SearchIntents.EXTRA_QUERY, getIntent().getStringExtra(SearchIntents.EXTRA_QUERY));
            intent.setFlags(537001984);
            startActivity(intent);
            finish();
            return;
        }
        if (isEssentialPermissionsGranted()) {
            initFragment();
        }
    }

    private void initFragment() {
        getLoaderManager();
        setContentView(R.layout.tablet_layout_search);
        if (((TabletAgendaControllerFragment) getSupportFragmentManager().findFragmentById(R.id.search_result)) == null) {
            TabletAgendaControllerFragment tabletAgendaControllerFragment = new TabletAgendaControllerFragment();
            FragmentTransaction fragmentTransactionBeginTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransactionBeginTransaction.replace(R.id.search_result, tabletAgendaControllerFragment);
            fragmentTransactionBeginTransaction.show(tabletAgendaControllerFragment);
            fragmentTransactionBeginTransaction.commit();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle(getResources().getString(R.string.search_results));
        UiUtils.setViewBackgroundToPrimaryColor(this, toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public PermissionItem[] getRequiredPermission() {
        return new PermissionItem[]{PermissionUtils.getEssentialCalendarPermissionItem(this)};
    }

    @Override // com.sonymobile.calendar.permissions.PermissionHandlerActivity
    public void onRequestPermissionResult(String[] strArr, int[] iArr) {
        initFragment();
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
