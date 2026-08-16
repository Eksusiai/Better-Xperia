package com.sonymobile.calendar.linkedin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.linkedin.backend.LinkedInCommunicationService;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class SearchResultActivity extends AppCompatActivity {
    BroadcastReceiver connectionInvateReceiver = new BroadcastReceiver() { // from class: com.sonymobile.calendar.linkedin.SearchResultActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(LinkedInCommunicationService.REQUEST_SUCCESS_PARAMETER, false)) {
                Toast.makeText(SearchResultActivity.this, R.string.linkedin_add_to_your_network_success_message, 1).show();
            } else {
                Toast.makeText(SearchResultActivity.this, R.string.linkedin_add_to_your_network_error_message, 1).show();
            }
        }
    };

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.linked_in_search_container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundResource(R.color.toolbar_background_color);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra(LinkedInUtils.LINKEDIN_FIRST_NAME);
        String stringExtra2 = intent.getStringExtra(LinkedInUtils.LINKEDIN_LAST_NAME);
        String stringExtra3 = intent.getStringExtra(LinkedInUtils.LINKEDIN_COMPANY);
        String stringExtra4 = intent.getStringExtra(LinkedInUtils.LINKEDIN_TITLE);
        String stringExtra5 = intent.getStringExtra(LinkedInUtils.LINKEDIN_POSITION);
        String stringExtra6 = intent.getStringExtra(LinkedInUtils.LINKEDIN_EMAIL_FOR_STORE);
        initActionBar();
        if (bundle == null) {
            SearchResultFragment searchResultFragmentNewInstance = SearchResultFragment.newInstance(stringExtra, stringExtra2, stringExtra3, stringExtra4, stringExtra5, stringExtra6);
            FragmentTransaction fragmentTransactionBeginTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransactionBeginTransaction.add(R.id.container, searchResultFragmentNewInstance);
            fragmentTransactionBeginTransaction.commit();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        registerReceiver(this.connectionInvateReceiver, new IntentFilter(LinkedInCommunicationService.MESSAGE_CONTACT_INVITE), 4);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        unregisterReceiver(this.connectionInvateReceiver);
    }

    protected void initActionBar() {
        View viewInflate = getLayoutInflater().inflate(R.layout.custom_action_bar, (ViewGroup) null);
        TextView textView = (TextView) viewInflate.findViewById(R.id.actionbar_title);
        textView.setText(getResources().getString(R.string.search_results_linkedin));
        if (UiUtils.isPhoneLandscape(this)) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.black));
        } else {
            textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setCustomView(viewInflate);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
