package com.sonymobile.calendar.linkedin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.fragment.app.ListFragment;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.linkedin.backend.LinkedInCommunicationService;
import com.sonymobile.calendar.linkedin.model.LinkedInContact;
import com.sonymobile.calendar.linkedin.model.LinkedInContactsAdapter;
import com.sonymobile.calendar.linkedin.model.LinkedInImageCache;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class SearchResultFragment extends ListFragment {
    private static final String KEY_LINKEDIN_CONTACTS = "key_linkedin_contacts";
    private String mCompany;
    private String mEmailForStore;
    private String mFirstName;
    private String mLastName;
    private ListView mListView;
    private View mListViewHeader;
    private String mPosition;
    private String mTitle;
    private boolean mAbleToSearch = true;
    private BroadcastReceiver linkedInNameBroadcastReceiver = new BroadcastReceiver() { // from class: com.sonymobile.calendar.linkedin.SearchResultFragment.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(LinkedInCommunicationService.REQUEST_SUCCESS_PARAMETER, false)) {
                ArrayList<LinkedInContact> parcelableArrayListExtra = intent.getParcelableArrayListExtra(LinkedInCommunicationService.LINKEDIN_CONTACTS_PARAMETER);
                LinkedInContactsAdapter linkedInContactsAdapter = (LinkedInContactsAdapter) SearchResultFragment.this.getListAdapter();
                int size = parcelableArrayListExtra == null ? 0 : parcelableArrayListExtra.size();
                if (size != 0 || (linkedInContactsAdapter != null && !linkedInContactsAdapter.isEmpty())) {
                    if (SearchResultFragment.this.mListView.getHeaderViewsCount() == 0) {
                        SearchResultFragment.this.mListView.addHeaderView(SearchResultFragment.this.mListViewHeader);
                    }
                    if (linkedInContactsAdapter == null || linkedInContactsAdapter.isEmpty()) {
                        SearchResultFragment.this.setListAdapter(new LinkedInContactsAdapter(SearchResultFragment.this.getActivity(), R.layout.linked_in_search_result_item, parcelableArrayListExtra));
                        SearchResultFragment.this.mAbleToSearch = true;
                        return;
                    } else if (size == 0) {
                        SearchResultFragment.this.mAbleToSearch = false;
                        return;
                    } else {
                        SearchResultFragment.this.mAbleToSearch = true;
                        linkedInContactsAdapter.addAll(parcelableArrayListExtra);
                        return;
                    }
                }
                SearchResultFragment searchResultFragment = SearchResultFragment.this;
                searchResultFragment.setEmptyText(searchResultFragment.getResources().getString(R.string.search_results_linkedin_no_results));
                SearchResultFragment.this.setListAdapter(null);
                SearchResultFragment.this.mAbleToSearch = false;
                return;
            }
            Toast.makeText(SearchResultFragment.this.getActivity(), SearchResultFragment.this.getResources().getString(R.string.linkedin_search_network_error_message), 1).show();
            SearchResultFragment.this.getActivity().setResult(0);
            SearchResultFragment.this.getActivity().finish();
            SearchResultFragment.this.mAbleToSearch = true;
        }
    };

    public static SearchResultFragment newInstance(String str, String str2, String str3, String str4, String str5, String str6) {
        SearchResultFragment searchResultFragment = new SearchResultFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LinkedInUtils.LINKEDIN_FIRST_NAME, str);
        bundle.putString(LinkedInUtils.LINKEDIN_LAST_NAME, str2);
        bundle.putString(LinkedInUtils.LINKEDIN_COMPANY, str3);
        bundle.putString(LinkedInUtils.LINKEDIN_TITLE, str4);
        bundle.putString(LinkedInUtils.LINKEDIN_POSITION, str5);
        bundle.putString(LinkedInUtils.LINKEDIN_EMAIL_FOR_STORE, str6);
        searchResultFragment.setArguments(bundle);
        return searchResultFragment;
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        this.mFirstName = arguments.getString(LinkedInUtils.LINKEDIN_FIRST_NAME);
        this.mLastName = arguments.getString(LinkedInUtils.LINKEDIN_LAST_NAME);
        this.mCompany = arguments.getString(LinkedInUtils.LINKEDIN_COMPANY);
        this.mTitle = arguments.getString(LinkedInUtils.LINKEDIN_TITLE);
        this.mPosition = arguments.getString(LinkedInUtils.LINKEDIN_POSITION);
        this.mEmailForStore = arguments.getString(LinkedInUtils.LINKEDIN_EMAIL_FOR_STORE);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (bundle != null) {
            ArrayList parcelableArrayList = bundle.getParcelableArrayList(KEY_LINKEDIN_CONTACTS);
            if (parcelableArrayList != null) {
                setListAdapter(new LinkedInContactsAdapter(getActivity(), R.layout.linked_in_search_result_item, parcelableArrayList));
            } else {
                setEmptyText(getResources().getString(R.string.search_results_linkedin_no_results));
                setListAdapter(null);
                this.mAbleToSearch = false;
            }
        } else {
            startSearchLookup(0);
        }
        ListView listView = getListView();
        this.mListView = listView;
        listView.setOnScrollListener(new AbsListView.OnScrollListener() { // from class: com.sonymobile.calendar.linkedin.SearchResultFragment.1
            @Override // android.widget.AbsListView.OnScrollListener
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == 0) {
                    SearchResultFragment.this.setBusy(false);
                } else if (i == 1) {
                    SearchResultFragment.this.setBusy(true);
                } else {
                    if (i != 2) {
                        return;
                    }
                    SearchResultFragment.this.setBusy(true);
                }
            }

            @Override // android.widget.AbsListView.OnScrollListener
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                if (i + i2 == i3 - 10 && SearchResultFragment.this.mAbleToSearch) {
                    SearchResultFragment.this.startSearchLookup(i3);
                }
            }
        });
        this.mListViewHeader = ((LayoutInflater) getActivity().getApplicationContext().getSystemService("layout_inflater")).inflate(R.layout.linkedin_search_results_header, (ViewGroup) this.mListView, false);
        if (this.mListView.getHeaderViewsCount() != 0 || this.mListView.getCount() <= 0) {
            return;
        }
        this.mListView.addHeaderView(this.mListViewHeader);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBusy(boolean z) {
        LinkedInContactsAdapter linkedInContactsAdapter = (LinkedInContactsAdapter) getListAdapter();
        if (linkedInContactsAdapter == null) {
            return;
        }
        linkedInContactsAdapter.setBusy(z);
        if (z) {
            return;
        }
        linkedInContactsAdapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSearchLookup(int i) {
        this.mAbleToSearch = false;
        Intent intent = new Intent(getActivity(), (Class<?>) LinkedInCommunicationService.class);
        intent.setAction(LinkedInCommunicationService.ACTION_PEOPLE_SEARCH);
        intent.putExtra(LinkedInCommunicationService.FIRST_NAME_PARAMETER, this.mFirstName);
        intent.putExtra(LinkedInCommunicationService.LAST_NAME_PARAMETER, this.mLastName);
        intent.putExtra(LinkedInCommunicationService.COMPANY_PARAMETER, this.mCompany);
        intent.putExtra(LinkedInCommunicationService.TITLE_PARAMETER, this.mTitle);
        intent.putExtra(LinkedInCommunicationService.POSITION_PARAMETER, this.mPosition);
        intent.putExtra(LinkedInCommunicationService.OFFSET_PARAMETER, i);
        getActivity().startService(intent);
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(this.linkedInNameBroadcastReceiver, new IntentFilter(LinkedInCommunicationService.MESSAGE_PEOPLE_SEARCH), 4);
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(this.linkedInNameBroadcastReceiver);
    }

    @Override // androidx.fragment.app.ListFragment
    public void onListItemClick(ListView listView, View view, int i, long j) {
        if (i == 0) {
            return;
        }
        LinkedInImageCache.getInstance().putContactInCache((LinkedInContact) listView.getAdapter().getItem(i), this.mEmailForStore);
        Toast.makeText(getActivity(), getResources().getString(R.string.update_linkedin_contact_in_calendar_message), 1).show();
        getActivity().setResult(-1);
        getActivity().finish();
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        LinkedInContactsAdapter linkedInContactsAdapter = (LinkedInContactsAdapter) getListAdapter();
        bundle.putParcelableArrayList(KEY_LINKEDIN_CONTACTS, linkedInContactsAdapter != null ? linkedInContactsAdapter.toArrayList() : null);
    }
}
