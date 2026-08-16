package com.sonymobile.calendar.tablet;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class TabletPreferenceHeadersFragment extends ListFragment {
    public static final String SELECTED_ITEM = "selectedItem";
    int mSelectedListItem = 0;
    String[] mPreferenceHeaders = new String[2];

    @Override // androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        Resources resources = getResources();
        this.mPreferenceHeaders[0] = resources.getString(R.string.preferences_alerts_title);
        this.mPreferenceHeaders[1] = resources.getString(R.string.preferences_general_title);
        if (bundle != null) {
            this.mSelectedListItem = bundle.getInt(SELECTED_ITEM);
        } else {
            presentPreferencePanel(this.mSelectedListItem);
        }
        getListView().setChoiceMode(1);
        getListView().setItemChecked(0, true);
        if (bundle != null) {
            this.mSelectedListItem = bundle.getInt(SELECTED_ITEM, 0);
        }
    }

    @Override // androidx.fragment.app.ListFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        setListAdapter(new SelectableArrayAdapter(getContext(), R.layout.simple_selectable_list_item, this.mPreferenceHeaders));
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(SELECTED_ITEM, this.mSelectedListItem);
    }

    @Override // androidx.fragment.app.ListFragment
    public void onListItemClick(ListView listView, View view, int i, long j) {
        super.onListItemClick(listView, view, i, j);
        presentPreferencePanel(i);
    }

    void presentPreferencePanel(int i) {
        Fragment tabletViewPreferences;
        this.mSelectedListItem = i;
        getListView().setItemChecked(i, true);
        getListView().invalidateViews();
        Utils.clearBackStack(this);
        if (i == 1) {
            tabletViewPreferences = new TabletViewPreferences();
        } else {
            tabletViewPreferences = new TabletReminderPreferences();
        }
        FragmentTransaction fragmentTransactionBeginTransaction = getFragmentManager().beginTransaction();
        fragmentTransactionBeginTransaction.replace(R.id.preferencePanel, tabletViewPreferences);
        fragmentTransactionBeginTransaction.commit();
    }

    private class SelectableArrayAdapter<String> extends ArrayAdapter<String> {
        private static final int SELECTED_BACKGROUND = 3;
        private static final int SELECTED_TEXT = 2;
        private static final int UNSELECTED_BACKGROUND = 1;
        private static final int UNSELECTED_TEXT = 0;
        private final int[] mTextItemColors;

        public SelectableArrayAdapter(Context context, int i, String[] stringArr) {
            super(context, i, stringArr);
            this.mTextItemColors = new int[]{TabletPreferenceHeadersFragment.this.getResources().getColor(android.R.color.secondary_text_light), TabletPreferenceHeadersFragment.this.getResources().getColor(android.R.color.white), TabletPreferenceHeadersFragment.this.getResources().getColor(android.R.color.primary_text_dark), UiUtils.getAccentColor(getContext())};
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2 = super.getView(i, view, viewGroup);
            CheckedTextView checkedTextView = (CheckedTextView) view2;
            if (i == TabletPreferenceHeadersFragment.this.mSelectedListItem) {
                int[] iArr = this.mTextItemColors;
                setTextItemColors(checkedTextView, iArr[2], iArr[3]);
            } else {
                int[] iArr2 = this.mTextItemColors;
                setTextItemColors(checkedTextView, iArr2[0], iArr2[1]);
            }
            return view2;
        }

        private void setTextItemColors(CheckedTextView checkedTextView, int i, int i2) {
            checkedTextView.setTextColor(i);
            checkedTextView.setBackgroundColor(i2);
        }
    }
}
