package com.sonymobile.calendar.linkedin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import com.sonymobile.calendar.R;

/* JADX INFO: loaded from: classes2.dex */
public class LinkedInSearchFragment extends Fragment implements SearchLinkedInInterface {
    private static final int SEARCH_RESULTS_ACTIVITY_REQUEST_CODE = 1;
    public static final String TAG = "LinkedInSearchFragment";
    private String mCompany;
    private EditText mCompanyEditText;
    private String mEmailForStore;
    private String mFirstName;
    private EditText mFirstNameEditText;
    private String mLastName;
    private EditText mLastNameEditText;
    private String mPosition;
    private EditText mPositionEditText;
    private String mTitle;
    private EditText mTitleEditText;

    public static LinkedInSearchFragment newInstance(String str, String str2, String str3) {
        LinkedInSearchFragment linkedInSearchFragment = new LinkedInSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LinkedInUtils.LINKEDIN_FIRST_NAME, str);
        bundle.putString(LinkedInUtils.LINKEDIN_LAST_NAME, str2);
        bundle.putString(LinkedInUtils.LINKEDIN_COMPANY, "");
        bundle.putString(LinkedInUtils.LINKEDIN_TITLE, "");
        bundle.putString(LinkedInUtils.LINKEDIN_POSITION, "");
        bundle.putString(LinkedInUtils.LINKEDIN_EMAIL_FOR_STORE, str3);
        linkedInSearchFragment.setArguments(bundle);
        return linkedInSearchFragment;
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
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.linked_in_people_search, viewGroup, false);
        init(viewInflate);
        return viewInflate;
    }

    private void init(View view) {
        EditText editText = (EditText) view.findViewById(R.id.linkein_search_first_name);
        this.mFirstNameEditText = editText;
        editText.setText(this.mFirstName);
        EditText editText2 = (EditText) view.findViewById(R.id.linkein_search_last_name);
        this.mLastNameEditText = editText2;
        editText2.setText(this.mLastName);
        EditText editText3 = (EditText) view.findViewById(R.id.linkein_search_company);
        this.mCompanyEditText = editText3;
        editText3.setText(this.mCompany);
        EditText editText4 = (EditText) view.findViewById(R.id.linkein_search_title);
        this.mTitleEditText = editText4;
        editText4.setText(this.mTitle);
        EditText editText5 = (EditText) view.findViewById(R.id.linkein_search_position);
        this.mPositionEditText = editText5;
        editText5.setText(this.mPosition);
        view.findViewById(R.id.focus_steel).requestFocus();
    }

    @Override // com.sonymobile.calendar.linkedin.SearchLinkedInInterface
    public void performSearchOnLinkedIn() {
        Intent intent = new Intent(getActivity(), (Class<?>) SearchResultActivity.class);
        intent.putExtra(LinkedInUtils.LINKEDIN_FIRST_NAME, this.mFirstNameEditText.getText().toString());
        intent.putExtra(LinkedInUtils.LINKEDIN_LAST_NAME, this.mLastNameEditText.getText().toString());
        intent.putExtra(LinkedInUtils.LINKEDIN_COMPANY, this.mCompanyEditText.getText().toString());
        intent.putExtra(LinkedInUtils.LINKEDIN_TITLE, this.mTitleEditText.getText().toString());
        intent.putExtra(LinkedInUtils.LINKEDIN_POSITION, this.mPositionEditText.getText().toString());
        intent.putExtra(LinkedInUtils.LINKEDIN_EMAIL_FOR_STORE, this.mEmailForStore);
        startActivityForResult(intent, 1);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1 && i2 == -1) {
            getActivity().finish();
        }
    }
}
