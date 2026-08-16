package com.android.ex.chips;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.text.util.Rfc822Token;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class BaseRecipientAdapter extends BaseAdapter implements Filterable, AccountSpecifier, PhotoManager.PhotoManagerCallback {
    static final int ALLOWANCE_FOR_DUPLICATES = 5;
    private static final boolean DEBUG = false;
    private static final int DEFAULT_PREFERRED_MAX_RESULT_COUNT = 10;
    private static final int MESSAGE_SEARCH_PENDING = 1;
    private static final int MESSAGE_SEARCH_PENDING_DELAY = 1000;
    static final String PRIMARY_ACCOUNT_NAME = "name_for_primary_account";
    static final String PRIMARY_ACCOUNT_TYPE = "type_for_primary_account";
    public static final int QUERY_TYPE_EMAIL = 0;
    public static final int QUERY_TYPE_PHONE = 1;
    private static final String TAG = "BaseRecipientAdapter";
    private Account mAccount;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    protected CharSequence mCurrentConstraint;
    private final DelayedMessageHandler mDelayedMessageHandler;
    private DropdownChipLayouter mDropdownChipLayouter;
    private List<RecipientEntry> mEntries;
    private EntriesUpdatedObserver mEntriesUpdatedObserver;
    private LinkedHashMap<Long, List<RecipientEntry>> mEntryMap;
    private Set<String> mExistingDestinations;
    private List<RecipientEntry> mNonAggregatedEntries;
    private PhotoManager mPhotoManager;
    protected final int mPreferredMaxResultCount;
    private final Queries.Query mQueryMode;
    private final int mQueryType;
    private int mRemainingDirectoryCount;
    private SearchObserver mSearchObserver;
    private List<RecipientEntry> mTempEntries;

    public static final class DirectorySearchParams {
        public String accountName;
        public String accountType;
        public CharSequence constraint;
        public long directoryId;
        public String directoryType;
        public String displayName;
        public DirectoryFilter filter;
    }

    public interface EntriesUpdatedObserver {
        void onChanged(List<RecipientEntry> list);
    }

    public interface SearchObserver {
        void searchFinished();

        void searchStarted();
    }

    public boolean forceShowAddress() {
        return false;
    }

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public Map<String, RecipientEntry> getMatchingRecipients(Set<String> set) {
        return null;
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getViewTypeCount() {
        return 1;
    }

    @Override // com.android.ex.chips.PhotoManager.PhotoManagerCallback
    public void onPhotoBytesAsyncLoadFailed() {
    }

    @Override // com.android.ex.chips.PhotoManager.PhotoManagerCallback
    public void onPhotoBytesPopulated() {
    }

    static /* synthetic */ int access$710(BaseRecipientAdapter baseRecipientAdapter) {
        int i = baseRecipientAdapter.mRemainingDirectoryCount;
        baseRecipientAdapter.mRemainingDirectoryCount = i - 1;
        return i;
    }

    protected static class DirectoryListQuery {
        public static final int ACCOUNT_NAME = 1;
        public static final int ACCOUNT_TYPE = 2;
        public static final int DISPLAY_NAME = 3;
        public static final int ID = 0;
        public static final int PACKAGE_NAME = 4;
        public static final int TYPE_RESOURCE_ID = 5;
        public static final Uri URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "directories");
        public static final String[] PROJECTION = {"_id", "accountName", "accountType", "displayName", "packageName", "typeResourceId"};

        protected DirectoryListQuery() {
        }
    }

    protected static class TemporaryEntry {
        public final long contactId;
        public final long dataId;
        public final String destination;
        public final String destinationLabel;
        public final int destinationType;
        public final Long directoryId;
        public final String displayName;
        public final int displayNameSource;
        public final String lookupKey;
        public final String thumbnailUriString;

        public TemporaryEntry(String str, String str2, int i, String str3, long j, Long l, long j2, String str4, int i2, String str5) {
            this.displayName = str;
            this.destination = str2;
            this.destinationType = i;
            this.destinationLabel = str3;
            this.contactId = j;
            this.directoryId = l;
            this.dataId = j2;
            this.thumbnailUriString = str4;
            this.displayNameSource = i2;
            this.lookupKey = str5;
        }

        public TemporaryEntry(Cursor cursor, Long l) {
            this.displayName = cursor.getString(0);
            this.destination = cursor.getString(1);
            this.destinationType = cursor.getInt(2);
            this.destinationLabel = cursor.getString(3);
            this.contactId = cursor.getLong(4);
            this.directoryId = l;
            this.dataId = cursor.getLong(5);
            this.thumbnailUriString = cursor.getString(6);
            this.displayNameSource = cursor.getInt(7);
            this.lookupKey = cursor.getString(8);
        }
    }

    private static class DefaultFilterResult {
        public final List<RecipientEntry> entries;
        public final LinkedHashMap<Long, List<RecipientEntry>> entryMap;
        public final Set<String> existingDestinations;
        public final List<RecipientEntry> nonAggregatedEntries;
        public final List<DirectorySearchParams> paramsList;

        public DefaultFilterResult(List<RecipientEntry> list, LinkedHashMap<Long, List<RecipientEntry>> linkedHashMap, List<RecipientEntry> list2, Set<String> set, List<DirectorySearchParams> list3) {
            this.entries = list;
            this.entryMap = linkedHashMap;
            this.nonAggregatedEntries = list2;
            this.existingDestinations = set;
            this.paramsList = list3;
        }
    }

    private final class DefaultFilter extends Filter {
        private DefaultFilter() {
        }

        @Override // android.widget.Filter
        protected Filter.FilterResults performFiltering(CharSequence charSequence) {
            Filter.FilterResults filterResults = new Filter.FilterResults();
            if (TextUtils.isEmpty(charSequence)) {
                BaseRecipientAdapter.this.clearTempEntries();
                return filterResults;
            }
            Cursor cursorDoQuery = null;
            try {
                BaseRecipientAdapter baseRecipientAdapter = BaseRecipientAdapter.this;
                cursorDoQuery = baseRecipientAdapter.doQuery(charSequence, baseRecipientAdapter.mPreferredMaxResultCount, null);
                if (cursorDoQuery != null) {
                    LinkedHashMap linkedHashMap = new LinkedHashMap();
                    ArrayList arrayList = new ArrayList();
                    HashSet hashSet = new HashSet();
                    while (cursorDoQuery.moveToNext()) {
                        BaseRecipientAdapter.putOneEntry(new TemporaryEntry(cursorDoQuery, null), true, linkedHashMap, arrayList, hashSet);
                    }
                    List listConstructEntryList = BaseRecipientAdapter.this.constructEntryList(linkedHashMap, arrayList);
                    filterResults.values = new DefaultFilterResult(listConstructEntryList, linkedHashMap, arrayList, hashSet, BaseRecipientAdapter.this.searchOtherDirectories(hashSet));
                    filterResults.count = listConstructEntryList.size();
                }
                return filterResults;
            } finally {
                if (cursorDoQuery != null) {
                    cursorDoQuery.close();
                }
            }
        }

        @Override // android.widget.Filter
        protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
            BaseRecipientAdapter.this.mCurrentConstraint = charSequence;
            BaseRecipientAdapter.this.clearTempEntries();
            if (filterResults.values != null) {
                DefaultFilterResult defaultFilterResult = (DefaultFilterResult) filterResults.values;
                BaseRecipientAdapter.this.mEntryMap = defaultFilterResult.entryMap;
                BaseRecipientAdapter.this.mNonAggregatedEntries = defaultFilterResult.nonAggregatedEntries;
                BaseRecipientAdapter.this.mExistingDestinations = defaultFilterResult.existingDestinations;
                BaseRecipientAdapter.this.cacheCurrentEntriesIfNeeded(defaultFilterResult.entries.size(), defaultFilterResult.paramsList == null ? 0 : defaultFilterResult.paramsList.size());
                BaseRecipientAdapter.this.updateEntries(defaultFilterResult.entries);
                if (defaultFilterResult.paramsList != null) {
                    BaseRecipientAdapter.this.startSearchOtherDirectories(charSequence, defaultFilterResult.paramsList, BaseRecipientAdapter.this.mPreferredMaxResultCount - defaultFilterResult.existingDestinations.size());
                    return;
                }
                return;
            }
            BaseRecipientAdapter.this.updateEntries(Collections.emptyList());
        }

        @Override // android.widget.Filter
        public CharSequence convertResultToString(Object obj) {
            RecipientEntry recipientEntry = (RecipientEntry) obj;
            String displayName = recipientEntry.getDisplayName();
            String destination = recipientEntry.getDestination();
            return (TextUtils.isEmpty(displayName) || TextUtils.equals(displayName, destination)) ? destination : new Rfc822Token(displayName, destination, null).toString();
        }
    }

    protected List<DirectorySearchParams> searchOtherDirectories(Set<String> set) {
        int size = this.mPreferredMaxResultCount - set.size();
        Cursor cursorQuery = null;
        if (size <= 0) {
            return null;
        }
        try {
            cursorQuery = this.mContentResolver.query(DirectoryListQuery.URI, DirectoryListQuery.PROJECTION, null, null, null);
            return setupOtherDirectories(this.mContext, cursorQuery, this.mAccount);
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    protected class DirectoryFilter extends Filter {
        private int mLimit;
        private final DirectorySearchParams mParams;

        public DirectoryFilter(DirectorySearchParams directorySearchParams) {
            this.mParams = directorySearchParams;
        }

        public synchronized void setLimit(int i) {
            this.mLimit = i;
        }

        public synchronized int getLimit() {
            return this.mLimit;
        }

        @Override // android.widget.Filter
        protected Filter.FilterResults performFiltering(CharSequence charSequence) {
            Filter.FilterResults filterResults = new Filter.FilterResults();
            Cursor cursorDoQuery = null;
            filterResults.values = null;
            filterResults.count = 0;
            if (!TextUtils.isEmpty(charSequence)) {
                ArrayList arrayList = new ArrayList();
                try {
                    cursorDoQuery = BaseRecipientAdapter.this.doQuery(charSequence, getLimit(), Long.valueOf(this.mParams.directoryId));
                    if (cursorDoQuery != null) {
                        while (cursorDoQuery.moveToNext()) {
                            arrayList.add(new TemporaryEntry(cursorDoQuery, Long.valueOf(this.mParams.directoryId)));
                        }
                    }
                    if (cursorDoQuery != null) {
                        cursorDoQuery.close();
                    }
                    if (!arrayList.isEmpty()) {
                        filterResults.values = arrayList;
                        filterResults.count = arrayList.size();
                    }
                } catch (Throwable th) {
                    if (cursorDoQuery != null) {
                        cursorDoQuery.close();
                    }
                    throw th;
                }
            }
            return filterResults;
        }

        @Override // android.widget.Filter
        protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
            BaseRecipientAdapter.this.mDelayedMessageHandler.removeDelayedLoadMessage();
            if (TextUtils.equals(charSequence, BaseRecipientAdapter.this.mCurrentConstraint)) {
                if (filterResults.count > 0) {
                    Iterator it = ((ArrayList) filterResults.values).iterator();
                    while (it.hasNext()) {
                        BaseRecipientAdapter.this.putOneEntry((TemporaryEntry) it.next(), this.mParams.directoryId == 0);
                    }
                }
                BaseRecipientAdapter.access$710(BaseRecipientAdapter.this);
                if (BaseRecipientAdapter.this.mRemainingDirectoryCount > 0) {
                    BaseRecipientAdapter.this.mDelayedMessageHandler.sendDelayedLoadMessage();
                } else {
                    BaseRecipientAdapter.this.mSearchObserver.searchFinished();
                }
                if (filterResults.count > 0 || BaseRecipientAdapter.this.mRemainingDirectoryCount == 0) {
                    BaseRecipientAdapter.this.clearTempEntries();
                }
            }
            BaseRecipientAdapter baseRecipientAdapter = BaseRecipientAdapter.this;
            baseRecipientAdapter.updateEntries(baseRecipientAdapter.constructEntryList());
        }
    }

    private final class DelayedMessageHandler extends Handler {
        private DelayedMessageHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (BaseRecipientAdapter.this.mRemainingDirectoryCount > 0) {
                BaseRecipientAdapter baseRecipientAdapter = BaseRecipientAdapter.this;
                baseRecipientAdapter.updateEntries(baseRecipientAdapter.constructEntryList());
            }
        }

        public void sendDelayedLoadMessage() {
            sendMessageDelayed(obtainMessage(1, 0, 0, null), 1000L);
        }

        public void removeDelayedLoadMessage() {
            removeMessages(1);
        }
    }

    public BaseRecipientAdapter(Context context) {
        this(context, 10, 0);
    }

    public BaseRecipientAdapter(Context context, int i) {
        this(context, i, 0);
    }

    public BaseRecipientAdapter(int i, Context context) {
        this(context, 10, i);
    }

    public BaseRecipientAdapter(int i, Context context, int i2) {
        this(context, i2, i);
    }

    public BaseRecipientAdapter(Context context, int i, int i2) {
        this.mDelayedMessageHandler = new DelayedMessageHandler();
        this.mContext = context;
        ContentResolver contentResolver = context.getContentResolver();
        this.mContentResolver = contentResolver;
        this.mPreferredMaxResultCount = i;
        this.mPhotoManager = new DefaultPhotoManager(contentResolver);
        this.mQueryType = i2;
        if (i2 == 0) {
            this.mQueryMode = Queries.EMAIL;
        } else if (i2 == 1) {
            this.mQueryMode = Queries.PHONE;
        } else {
            this.mQueryMode = Queries.EMAIL;
            Log.e(TAG, "Unsupported query type: " + i2);
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    public int getQueryType() {
        return this.mQueryType;
    }

    public void setDropdownChipLayouter(DropdownChipLayouter dropdownChipLayouter) {
        this.mDropdownChipLayouter = dropdownChipLayouter;
        dropdownChipLayouter.setQuery(this.mQueryMode);
    }

    public DropdownChipLayouter getDropdownChipLayouter() {
        return this.mDropdownChipLayouter;
    }

    public void setPhotoManager(PhotoManager photoManager) {
        this.mPhotoManager = photoManager;
    }

    public PhotoManager getPhotoManager() {
        return this.mPhotoManager;
    }

    public void getMatchingRecipients(ArrayList<String> arrayList, RecipientAlternatesAdapter.RecipientMatchCallback recipientMatchCallback) {
        RecipientAlternatesAdapter.getMatchingRecipients(getContext(), this, arrayList, getAccount(), recipientMatchCallback);
    }

    @Override // com.android.ex.chips.AccountSpecifier
    public void setAccount(Account account) {
        this.mAccount = account;
    }

    @Override // android.widget.Filterable
    public Filter getFilter() {
        return new DefaultFilter();
    }

    public static List<DirectorySearchParams> setupOtherDirectories(Context context, Cursor cursor, Account account) {
        PackageManager packageManager = context.getPackageManager();
        ArrayList arrayList = new ArrayList();
        DirectorySearchParams directorySearchParams = null;
        while (cursor.moveToNext()) {
            long j = cursor.getLong(0);
            if (j != 1) {
                DirectorySearchParams directorySearchParams2 = new DirectorySearchParams();
                String string = cursor.getString(4);
                int i = cursor.getInt(5);
                directorySearchParams2.directoryId = j;
                directorySearchParams2.displayName = cursor.getString(3);
                directorySearchParams2.accountName = cursor.getString(1);
                directorySearchParams2.accountType = cursor.getString(2);
                if (string != null && i != 0) {
                    try {
                        directorySearchParams2.directoryType = packageManager.getResourcesForApplication(string).getString(i);
                        if (directorySearchParams2.directoryType == null) {
                            Log.e(TAG, "Cannot resolve directory name: " + i + "@" + string);
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e(TAG, "Cannot resolve directory name: " + i + "@" + string, e);
                    }
                }
                if (account != null && account.name.equals(directorySearchParams2.accountName) && account.type.equals(directorySearchParams2.accountType)) {
                    directorySearchParams = directorySearchParams2;
                } else {
                    arrayList.add(directorySearchParams2);
                }
            }
        }
        if (directorySearchParams != null) {
            arrayList.add(1, directorySearchParams);
        }
        return arrayList;
    }

    protected void startSearchOtherDirectories(CharSequence charSequence, List<DirectorySearchParams> list, int i) {
        int size = list.size();
        for (int i2 = 1; i2 < size; i2++) {
            DirectorySearchParams directorySearchParams = list.get(i2);
            directorySearchParams.constraint = charSequence;
            if (directorySearchParams.filter == null) {
                directorySearchParams.filter = new DirectoryFilter(directorySearchParams);
            }
            directorySearchParams.filter.setLimit(i);
            directorySearchParams.filter.filter(charSequence);
        }
        this.mSearchObserver.searchStarted();
        this.mRemainingDirectoryCount = size - 1;
        this.mDelayedMessageHandler.sendDelayedLoadMessage();
    }

    protected void putOneEntry(TemporaryEntry temporaryEntry, boolean z) {
        putOneEntry(temporaryEntry, z, this.mEntryMap, this.mNonAggregatedEntries, this.mExistingDestinations);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void putOneEntry(TemporaryEntry temporaryEntry, boolean z, LinkedHashMap<Long, List<RecipientEntry>> linkedHashMap, List<RecipientEntry> list, Set<String> set) {
        if (set.contains(temporaryEntry.destination)) {
            return;
        }
        set.add(temporaryEntry.destination);
        if (!z) {
            list.add(RecipientEntry.constructTopLevelEntry(temporaryEntry.displayName, temporaryEntry.displayNameSource, temporaryEntry.destination, temporaryEntry.destinationType, temporaryEntry.destinationLabel, temporaryEntry.contactId, temporaryEntry.directoryId, temporaryEntry.dataId, temporaryEntry.thumbnailUriString, true, temporaryEntry.lookupKey));
        } else {
            if (linkedHashMap.containsKey(Long.valueOf(temporaryEntry.contactId))) {
                linkedHashMap.get(Long.valueOf(temporaryEntry.contactId)).add(RecipientEntry.constructSecondLevelEntry(temporaryEntry.displayName, temporaryEntry.displayNameSource, temporaryEntry.destination, temporaryEntry.destinationType, temporaryEntry.destinationLabel, temporaryEntry.contactId, temporaryEntry.directoryId, temporaryEntry.dataId, temporaryEntry.thumbnailUriString, true, temporaryEntry.lookupKey));
                return;
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(RecipientEntry.constructTopLevelEntry(temporaryEntry.displayName, temporaryEntry.displayNameSource, temporaryEntry.destination, temporaryEntry.destinationType, temporaryEntry.destinationLabel, temporaryEntry.contactId, temporaryEntry.directoryId, temporaryEntry.dataId, temporaryEntry.thumbnailUriString, true, temporaryEntry.lookupKey));
            linkedHashMap.put(Long.valueOf(temporaryEntry.contactId), arrayList);
        }
    }

    protected List<RecipientEntry> constructEntryList() {
        return constructEntryList(this.mEntryMap, this.mNonAggregatedEntries);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<RecipientEntry> constructEntryList(LinkedHashMap<Long, List<RecipientEntry>> linkedHashMap, List<RecipientEntry> list) {
        ArrayList arrayList = new ArrayList();
        Iterator<Map.Entry<Long, List<RecipientEntry>>> it = linkedHashMap.entrySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            List<RecipientEntry> value = it.next().getValue();
            int size = value.size();
            for (int i2 = 0; i2 < size; i2++) {
                RecipientEntry recipientEntry = value.get(i2);
                arrayList.add(recipientEntry);
                this.mPhotoManager.populatePhotoBytesAsync(recipientEntry, this);
                i++;
            }
            if (i > this.mPreferredMaxResultCount) {
                break;
            }
        }
        if (i <= this.mPreferredMaxResultCount) {
            for (RecipientEntry recipientEntry2 : list) {
                if (i > this.mPreferredMaxResultCount) {
                    break;
                }
                arrayList.add(recipientEntry2);
                this.mPhotoManager.populatePhotoBytesAsync(recipientEntry2, this);
                i++;
            }
        }
        return arrayList;
    }

    public void registerUpdateObserver(EntriesUpdatedObserver entriesUpdatedObserver) {
        this.mEntriesUpdatedObserver = entriesUpdatedObserver;
    }

    protected void updateEntries(List<RecipientEntry> list) {
        this.mEntries = list;
        this.mEntriesUpdatedObserver.onChanged(list);
        notifyDataSetChanged();
    }

    protected void cacheCurrentEntriesIfNeeded(int i, int i2) {
        if (i != 0 || i2 <= 1) {
            return;
        }
        cacheCurrentEntries();
    }

    protected void cacheCurrentEntries() {
        this.mTempEntries = this.mEntries;
    }

    protected void clearTempEntries() {
        this.mTempEntries = null;
    }

    protected List<RecipientEntry> getEntries() {
        List<RecipientEntry> list = this.mTempEntries;
        return list != null ? list : this.mEntries;
    }

    protected void fetchPhoto(RecipientEntry recipientEntry, PhotoManager.PhotoManagerCallback photoManagerCallback) {
        this.mPhotoManager.populatePhotoBytesAsync(recipientEntry, photoManagerCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Cursor doQuery(CharSequence charSequence, int i, Long l) {
        Uri.Builder builderAppendQueryParameter = this.mQueryMode.getContentFilterUri().buildUpon().appendPath(charSequence.toString()).appendQueryParameter("limit", String.valueOf(i + 5));
        if (l != null) {
            builderAppendQueryParameter.appendQueryParameter("directory", String.valueOf(l));
        }
        Account account = this.mAccount;
        if (account != null) {
            builderAppendQueryParameter.appendQueryParameter(PRIMARY_ACCOUNT_NAME, account.name);
            builderAppendQueryParameter.appendQueryParameter(PRIMARY_ACCOUNT_TYPE, this.mAccount.type);
        }
        System.currentTimeMillis();
        Cursor cursorQuery = this.mContentResolver.query(builderAppendQueryParameter.build(), this.mQueryMode.getProjection(), null, null, null);
        System.currentTimeMillis();
        return cursorQuery;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        List<RecipientEntry> entries = getEntries();
        if (entries != null) {
            return entries.size();
        }
        return 0;
    }

    @Override // android.widget.Adapter
    public RecipientEntry getItem(int i) {
        return getEntries().get(i);
    }

    @Override // android.widget.BaseAdapter, android.widget.Adapter
    public int getItemViewType(int i) {
        return getEntries().get(i).getEntryType();
    }

    @Override // android.widget.BaseAdapter, android.widget.ListAdapter
    public boolean isEnabled(int i) {
        return getEntries().get(i).isSelectable();
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        RecipientEntry recipientEntry = getEntries().get(i);
        CharSequence charSequence = this.mCurrentConstraint;
        return this.mDropdownChipLayouter.bindView(view, viewGroup, recipientEntry, i, DropdownChipLayouter.AdapterType.BASE_RECIPIENT, charSequence == null ? null : charSequence.toString());
    }

    public Account getAccount() {
        return this.mAccount;
    }

    @Override // com.android.ex.chips.PhotoManager.PhotoManagerCallback
    public void onPhotoBytesAsynchronouslyPopulated() {
        notifyDataSetChanged();
    }

    public void registerSearchObserver(SearchObserver searchObserver) {
        this.mSearchObserver = searchObserver;
    }
}
