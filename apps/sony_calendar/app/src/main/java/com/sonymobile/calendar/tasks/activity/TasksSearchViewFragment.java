package com.sonymobile.calendar.tasks.activity;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.tasks.SearchProvider;
import com.sonymobile.calendar.tasks.TasksItemProvider;
import com.sonymobile.calendar.tasks.widget.SearchViewAdapter;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class TasksSearchViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private SearchViewAdapter mAdapter;

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mAdapter = new SearchViewAdapter(getActivity(), R.layout.tasks_list_childs, null);
        if (getLoaderManager().getLoader(1) == null) {
            getLoaderManager().initLoader(1, null, this);
        } else {
            getLoaderManager().restartLoader(1, null, this);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (!SearchProvider.getInstance().hasActiveQuery()) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
        setHasOptionsMenu(false);
        View viewInflate = layoutInflater.inflate(R.layout.search_view_fragment, viewGroup, false);
        ListView listView = (ListView) viewInflate.findViewById(R.id.search_view_list);
        listView.setDividerHeight(0);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() { // from class: com.sonymobile.calendar.tasks.activity.TasksSearchViewFragment.1
            @Override // android.widget.AbsListView.OnScrollListener
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            }

            @Override // android.widget.AbsListView.OnScrollListener
            public void onScrollStateChanged(AbsListView absListView, int i) {
                TasksSearchViewFragment.this.hideSoftKeyboard();
            }
        });
        listView.setAdapter((ListAdapter) this.mAdapter);
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onContextItemSelected(MenuItem menuItem) {
        return this.mAdapter.onContextItemSelected(menuItem) || super.onContextItemSelected(menuItem);
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return SearchProvider.getInstance().configureLoader(getActivity());
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || getActivity() == null) {
            return;
        }
        this.mAdapter.changeCursor(cursor);
        if (cursor.getCount() > 0) {
            ArrayList<Long> arrayList = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                arrayList.add(Long.valueOf(cursor.getLong(cursor.getColumnIndex("_id"))));
                cursor.moveToNext();
            }
            TasksItemProvider.getInstance().setTaskListIds(arrayList);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService("input_method");
        View currentFocus = getActivity().getCurrentFocus();
        if (inputMethodManager == null || currentFocus == null) {
            return;
        }
        inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }
}
