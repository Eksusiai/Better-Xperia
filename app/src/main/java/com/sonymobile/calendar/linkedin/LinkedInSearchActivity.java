package com.sonymobile.calendar.linkedin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class LinkedInSearchActivity extends AppCompatActivity {
    private SearchLinkedInInterface mSearchLinkedIn;

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
        String stringExtra3 = intent.getStringExtra(LinkedInUtils.LINKEDIN_EMAIL_FOR_STORE);
        initActionBar();
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (bundle == null) {
            LinkedInSearchFragment linkedInSearchFragmentNewInstance = LinkedInSearchFragment.newInstance(stringExtra, stringExtra2, stringExtra3);
            setSearchLinkedInInterface(linkedInSearchFragmentNewInstance);
            FragmentTransaction fragmentTransactionBeginTransaction = supportFragmentManager.beginTransaction();
            fragmentTransactionBeginTransaction.add(R.id.container, linkedInSearchFragmentNewInstance, LinkedInSearchFragment.TAG);
            fragmentTransactionBeginTransaction.commit();
            return;
        }
        LifecycleOwner lifecycleOwnerFindFragmentByTag = supportFragmentManager.findFragmentByTag(LinkedInSearchFragment.TAG);
        if (lifecycleOwnerFindFragmentByTag != null) {
            setSearchLinkedInInterface((SearchLinkedInInterface) lifecycleOwnerFindFragmentByTag);
        }
    }

    protected void initActionBar() {
        View viewInflate = getLayoutInflater().inflate(R.layout.custom_action_bar, (ViewGroup) null);
        TextView textView = (TextView) viewInflate.findViewById(R.id.actionbar_title);
        textView.setText(getResources().getString(R.string.search_on_linkedin_title));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.linkedin_search_menu, menu);
        MenuItem menuItemFindItem = menu.findItem(R.id.action_linkedin_search);
        if (!UiUtils.isPhoneLandscape(this)) {
            return true;
        }
        menuItemFindItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_menu_search_holo_light));
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        SearchLinkedInInterface searchLinkedInInterface;
        if (menuItem.getItemId() == 16908332) {
            finish();
        } else if (menuItem.getItemId() == R.id.action_linkedin_search && (searchLinkedInInterface = this.mSearchLinkedIn) != null) {
            searchLinkedInInterface.performSearchOnLinkedIn();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void setSearchLinkedInInterface(SearchLinkedInInterface searchLinkedInInterface) {
        this.mSearchLinkedIn = searchLinkedInInterface;
    }
}
