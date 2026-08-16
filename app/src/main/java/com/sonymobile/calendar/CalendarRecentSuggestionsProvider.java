package com.sonymobile.calendar;

import android.content.SearchRecentSuggestionsProvider;

/* JADX INFO: loaded from: classes2.dex */
public class CalendarRecentSuggestionsProvider extends SearchRecentSuggestionsProvider {
    public static final int MODE = 1;

    @Override // android.content.SearchRecentSuggestionsProvider, android.content.ContentProvider
    public boolean onCreate() {
        setupSuggestions(Utils.getSearchAuthority(getContext()), 1);
        return super.onCreate();
    }
}
