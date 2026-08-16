package com.android.ex.chips;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import com.sonyericsson.calendar.util.RecurrenceRuleParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public class RecipientAlternatesAdapter extends CursorAdapter {
    public static final int MAX_LOOKUPS = 50;
    public static final int QUERY_TYPE_EMAIL = 0;
    public static final int QUERY_TYPE_PHONE = 1;
    private static final String TAG = "RecipAlternates";
    private static final Map<String, String> sCorrectedPhotoUris = new HashMap();
    private OnCheckedItemChangedListener mCheckedItemChangedListener;
    private int mCheckedItemPosition;
    private final long mCurrentId;
    private final StateListDrawable mDeleteDrawable;
    private final Long mDirectoryId;
    private DropdownChipLayouter mDropdownChipLayouter;

    interface OnCheckedItemChangedListener {
        void onCheckedItemChanged(int i);
    }

    public interface RecipientMatchCallback {
        void matchesFound(Map<String, RecipientEntry> map);

        void matchesNotFound(Set<String> set);
    }

    public static void getMatchingRecipients(Context context, BaseRecipientAdapter baseRecipientAdapter, ArrayList<String> arrayList, Account account, RecipientMatchCallback recipientMatchCallback) {
        getMatchingRecipients(context, baseRecipientAdapter, arrayList, 0, account, recipientMatchCallback);
    }

    public static void getMatchingRecipients(Context context, BaseRecipientAdapter baseRecipientAdapter, ArrayList<String> arrayList, int i, Account account, RecipientMatchCallback recipientMatchCallback) {
        Queries.Query query;
        if (i == 0) {
            query = Queries.EMAIL;
        } else {
            query = Queries.PHONE;
        }
        Queries.Query query2 = query;
        int iMin = Math.min(50, arrayList.size());
        HashSet hashSet = new HashSet();
        StringBuilder sb = new StringBuilder();
        for (int i2 = 0; i2 < iMin; i2++) {
            Rfc822Token[] rfc822TokenArr = Rfc822Tokenizer.tokenize(arrayList.get(i2).toLowerCase());
            hashSet.add(rfc822TokenArr.length > 0 ? rfc822TokenArr[0].getAddress() : arrayList.get(i2));
            sb.append("?");
            if (i2 < iMin - 1) {
                sb.append(RecurrenceRuleParser.VALUE_SEPARATOR);
            }
        }
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "Doing reverse lookup for " + hashSet.toString());
        }
        String[] strArr = new String[hashSet.size()];
        hashSet.toArray(strArr);
        HashMap<String, RecipientEntry> map = new HashMap<>();
        if (context.checkCallingOrSelfPermission("android.permission.READ_CONTACTS") == 0) {
            Cursor cursorQuery = context.getContentResolver().query(query2.getContentUri(), query2.getProjection(), query2.getProjection()[1] + " IN (" + sb.toString() + ")", strArr, null);
            try {
                map = processContactEntries(cursorQuery, null);
                recipientMatchCallback.matchesFound(map);
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            } catch (Throwable th) {
                if (cursorQuery == null) {
                    throw th;
                }
                try {
                    cursorQuery.close();
                    throw th;
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                    throw th;
                }
            }
        }
        HashSet hashSet2 = new HashSet();
        getMatchingRecipientsFromDirectoryQueries(context, map, hashSet, account, hashSet2, query2, recipientMatchCallback);
        getMatchingRecipientsFromExtensionMatcher(baseRecipientAdapter, hashSet2, recipientMatchCallback);
    }

    private static void getMatchingRecipientsFromDirectoryQueries(Context context, Map<String, RecipientEntry> map, Set<String> set, Account account, Set<String> set2, Queries.Query query, RecipientMatchCallback recipientMatchCallback) {
        List<BaseRecipientAdapter.DirectorySearchParams> list;
        List<BaseRecipientAdapter.DirectorySearchParams> list2;
        boolean z = context.checkCallingOrSelfPermission("android.permission.READ_CONTACTS") == 0;
        if (map.size() < set.size()) {
            if (z) {
                Cursor cursorQuery = context.getContentResolver().query(BaseRecipientAdapter.DirectoryListQuery.URI, BaseRecipientAdapter.DirectoryListQuery.PROJECTION, null, null, null);
                if (cursorQuery != null) {
                    try {
                        list2 = BaseRecipientAdapter.setupOtherDirectories(context, cursorQuery, account);
                    } catch (Throwable th) {
                        if (cursorQuery == null) {
                            throw th;
                        }
                        try {
                            cursorQuery.close();
                            throw th;
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                            throw th;
                        }
                    }
                } else {
                    list2 = null;
                }
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                list = list2;
            } else {
                list = null;
            }
            HashSet<String> hashSet = new HashSet();
            for (String str : set) {
                if (!map.containsKey(str)) {
                    hashSet.add(str);
                }
            }
            set2.addAll(hashSet);
            if (list == null || !z) {
                return;
            }
            Cursor cursor = null;
            for (String str2 : hashSet) {
                Cursor cursor2 = null;
                Long lValueOf = null;
                int i2 = 0;
                while (i2 < list.size()) {
                    Cursor cursorDoQuery;
                    try {
                        cursorDoQuery = doQuery(str2, 1, Long.valueOf(list.get(i2).directoryId), account, context.getContentResolver(), query);
                    } catch (Exception e) {
                        cursorDoQuery = null;
                    }
                    if (cursorDoQuery == null || cursorDoQuery.getCount() == 0) {
                        if (cursorDoQuery != null) {
                            cursorDoQuery.close();
                        }
                        i2++;
                    } else {
                        lValueOf = Long.valueOf(list.get(i2).directoryId);
                        cursor2 = cursorDoQuery;
                        break;
                    }
                }
                if (cursor2 != null) {
                    try {
                        HashMap<String, RecipientEntry> mapProcessContactEntries = processContactEntries(cursor2, lValueOf);
                        Iterator<String> it = mapProcessContactEntries.keySet().iterator();
                        while (it.hasNext()) {
                            set2.remove(it.next());
                        }
                        recipientMatchCallback.matchesFound(mapProcessContactEntries);
                    } finally {
                        cursor2.close();
                    }
                }
                cursor = cursor2;
            }
        }
    }

    public static void getMatchingRecipientsFromExtensionMatcher(BaseRecipientAdapter baseRecipientAdapter, Set<String> set, RecipientMatchCallback recipientMatchCallback) {
        Map<String, RecipientEntry> matchingRecipients;
        if (baseRecipientAdapter != null && (matchingRecipients = baseRecipientAdapter.getMatchingRecipients(set)) != null && matchingRecipients.size() > 0) {
            recipientMatchCallback.matchesFound(matchingRecipients);
            Iterator<String> it = matchingRecipients.keySet().iterator();
            while (it.hasNext()) {
                set.remove(it.next());
            }
        }
        recipientMatchCallback.matchesNotFound(set);
    }

    private static HashMap<String, RecipientEntry> processContactEntries(Cursor cursor, Long l) {
        HashMap<String, RecipientEntry> map = new HashMap<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String string = cursor.getString(1);
                map.put(string, getBetterRecipient(map.get(string), RecipientEntry.constructTopLevelEntry(cursor.getString(0), cursor.getInt(7), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getLong(4), l, cursor.getLong(5), cursor.getString(6), true, cursor.getString(8))));
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "Received reverse look up information for " + string + " RESULTS:  NAME : " + cursor.getString(0) + " CONTACT ID : " + cursor.getLong(4) + " ADDRESS :" + cursor.getString(1));
                }
            } while (cursor.moveToNext());
        }
        return map;
    }

    static RecipientEntry getBetterRecipient(RecipientEntry recipientEntry, RecipientEntry recipientEntry2) {
        if (recipientEntry2 == null) {
            return recipientEntry;
        }
        if (recipientEntry == null) {
            return recipientEntry2;
        }
        if (!TextUtils.isEmpty(recipientEntry.getDisplayName()) && TextUtils.isEmpty(recipientEntry2.getDisplayName())) {
            return recipientEntry;
        }
        if (!TextUtils.isEmpty(recipientEntry2.getDisplayName()) && TextUtils.isEmpty(recipientEntry.getDisplayName())) {
            return recipientEntry2;
        }
        if (!TextUtils.equals(recipientEntry.getDisplayName(), recipientEntry.getDestination()) && TextUtils.equals(recipientEntry2.getDisplayName(), recipientEntry2.getDestination())) {
            return recipientEntry;
        }
        if (!TextUtils.equals(recipientEntry2.getDisplayName(), recipientEntry2.getDestination()) && TextUtils.equals(recipientEntry.getDisplayName(), recipientEntry.getDestination())) {
            return recipientEntry2;
        }
        if (!(recipientEntry.getPhotoThumbnailUri() == null && recipientEntry.getPhotoBytes() == null) && recipientEntry2.getPhotoThumbnailUri() == null && recipientEntry2.getPhotoBytes() == null) {
            return recipientEntry;
        }
        if ((recipientEntry2.getPhotoThumbnailUri() != null || recipientEntry2.getPhotoBytes() != null) && recipientEntry.getPhotoThumbnailUri() == null) {
            recipientEntry.getPhotoBytes();
        }
        return recipientEntry2;
    }

    private static Cursor doQuery(CharSequence charSequence, int i, Long l, Account account, ContentResolver contentResolver, Queries.Query query) {
        Uri.Builder builderAppendQueryParameter = query.getContentFilterUri().buildUpon().appendPath(charSequence.toString()).appendQueryParameter("limit", String.valueOf(i + 5));
        if (l != null) {
            builderAppendQueryParameter.appendQueryParameter("directory", String.valueOf(l));
        }
        if (account != null) {
            builderAppendQueryParameter.appendQueryParameter("name_for_primary_account", account.name);
            builderAppendQueryParameter.appendQueryParameter("type_for_primary_account", account.type);
        }
        return contentResolver.query(builderAppendQueryParameter.build(), query.getProjection(), null, null, null);
    }

    public RecipientAlternatesAdapter(Context context, long j, Long l, String str, long j2, int i, OnCheckedItemChangedListener onCheckedItemChangedListener, DropdownChipLayouter dropdownChipLayouter, StateListDrawable stateListDrawable) {
        super(context, getCursorForConstruction(context, j, l, str, i), 0);
        this.mCheckedItemPosition = -1;
        this.mCurrentId = j2;
        this.mDirectoryId = l;
        this.mCheckedItemChangedListener = onCheckedItemChangedListener;
        this.mDropdownChipLayouter = dropdownChipLayouter;
        this.mDeleteDrawable = stateListDrawable;
    }

    private static Cursor getCursorForConstruction(Context context, long j, Long l, String str, int i) {
        Uri uriBuild;
        Cursor cursorQuery;
        Uri contentUri;
        String str2 = null;
        if (context.checkCallingOrSelfPermission("android.permission.READ_CONTACTS") == -1) {
            return null;
        }
        if (i == 0) {
            if (l == null || str == null) {
                contentUri = Queries.EMAIL.getContentUri();
            } else {
                Uri.Builder builderBuildUpon = ContactsContract.Contacts.getLookupUri(j, str).buildUpon();
                builderBuildUpon.appendPath("entities").appendQueryParameter("directory", String.valueOf(l));
                contentUri = builderBuildUpon.build();
                str2 = "vnd.android.cursor.item/email_v2";
            }
            cursorQuery = context.getContentResolver().query(contentUri, Queries.EMAIL.getProjection(), Queries.EMAIL.getProjection()[4] + " = ?", new String[]{String.valueOf(j)}, null);
        } else {
            if (str == null) {
                uriBuild = Queries.PHONE.getContentUri();
            } else {
                Uri.Builder builderBuildUpon2 = ContactsContract.Contacts.getLookupUri(j, str).buildUpon();
                builderBuildUpon2.appendPath("entities").appendQueryParameter("directory", String.valueOf(l));
                uriBuild = builderBuildUpon2.build();
                str2 = "vnd.android.cursor.item/phone_v2";
            }
            cursorQuery = context.getContentResolver().query(uriBuild, Queries.PHONE.getProjection(), Queries.PHONE.getProjection()[4] + " = ?", new String[]{String.valueOf(j)}, null);
        }
        Cursor cursorRemoveUndesiredDestinations = removeUndesiredDestinations(cursorQuery, str2, str);
        cursorQuery.close();
        return cursorRemoveUndesiredDestinations;
    }

    static Cursor removeUndesiredDestinations(Cursor cursor, String str, String str2) {
        String string;
        int i;
        int i2;
        String string2;
        int i3;
        MatrixCursor matrixCursor = new MatrixCursor(cursor.getColumnNames(), cursor.getCount());
        HashSet hashSet = new HashSet();
        cursor.moveToPosition(-1);
        while (true) {
            string = null;
            i = 9;
            i2 = 7;
            if (!cursor.moveToNext()) {
                string2 = null;
                i3 = 0;
                break;
            }
            if ("vnd.android.cursor.item/name".equals(cursor.getString(9))) {
                string = cursor.getString(0);
                string2 = cursor.getString(6);
                i3 = cursor.getInt(7);
                break;
            }
        }
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            if (str == null || str.equals(cursor.getString(i))) {
                String string3 = cursor.getString(1);
                if (!hashSet.contains(string3)) {
                    hashSet.add(string3);
                    Object[] objArr = new Object[10];
                    objArr[0] = cursor.getString(0);
                    objArr[1] = cursor.getString(1);
                    objArr[2] = Integer.valueOf(cursor.getInt(2));
                    objArr[3] = cursor.getString(3);
                    objArr[4] = Long.valueOf(cursor.getLong(4));
                    objArr[5] = Long.valueOf(cursor.getLong(5));
                    objArr[6] = cursor.getString(6);
                    objArr[i2] = Integer.valueOf(cursor.getInt(i2));
                    objArr[8] = cursor.getString(8);
                    objArr[i] = cursor.getString(i);
                    if (objArr[0] == null) {
                        objArr[0] = string;
                    }
                    if (objArr[6] == null) {
                        objArr[6] = string2;
                    }
                    if (((Integer) objArr[i2]).intValue() == 0) {
                        objArr[i2] = Integer.valueOf(i3);
                    }
                    if (objArr[8] == null) {
                        objArr[8] = str2;
                    }
                    String str3 = (String) objArr[6];
                    if (str3 != null) {
                        Map<String, String> map = sCorrectedPhotoUris;
                        if (map.containsKey(str3)) {
                            objArr[6] = map.get(str3);
                        } else if (str3.indexOf(63) != str3.lastIndexOf(63)) {
                            String[] strArrSplit = str3.split("\\?");
                            StringBuilder sb = new StringBuilder();
                            for (int i4 = 0; i4 < strArrSplit.length; i4++) {
                                if (i4 == 1) {
                                    sb.append("?");
                                } else if (i4 > 1) {
                                    sb.append("&");
                                }
                                sb.append(strArrSplit[i4]);
                            }
                            String string4 = sb.toString();
                            sCorrectedPhotoUris.put(str3, string4);
                            objArr[6] = string4;
                        }
                    }
                    matrixCursor.addRow(objArr);
                    i = 9;
                    i2 = 7;
                }
            }
        }
        return matrixCursor;
    }

    @Override // android.widget.CursorAdapter, android.widget.Adapter
    public long getItemId(int i) {
        Cursor cursor = getCursor();
        if (!cursor.moveToPosition(i)) {
            return -1L;
        }
        cursor.getLong(5);
        return -1L;
    }

    public RecipientEntry getRecipientEntry(int i) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(i);
        return RecipientEntry.constructTopLevelEntry(cursor.getString(0), cursor.getInt(7), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getLong(4), this.mDirectoryId, cursor.getLong(5), cursor.getString(6), true, cursor.getString(8));
    }

    @Override // android.widget.CursorAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(i);
        if (view == null) {
            view = this.mDropdownChipLayouter.newView(DropdownChipLayouter.AdapterType.RECIPIENT_ALTERNATES);
        }
        if (cursor.getLong(5) == this.mCurrentId) {
            this.mCheckedItemPosition = i;
            OnCheckedItemChangedListener onCheckedItemChangedListener = this.mCheckedItemChangedListener;
            if (onCheckedItemChangedListener != null) {
                onCheckedItemChangedListener.onCheckedItemChanged(i);
            }
        }
        bindView(view, view.getContext(), cursor);
        return view;
    }

    @Override // android.widget.CursorAdapter
    public void bindView(View view, Context context, Cursor cursor) {
        int position = cursor.getPosition();
        this.mDropdownChipLayouter.bindView(view, null, getRecipientEntry(position), position, DropdownChipLayouter.AdapterType.RECIPIENT_ALTERNATES, null, this.mDeleteDrawable);
    }

    @Override // android.widget.CursorAdapter
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return this.mDropdownChipLayouter.newView(DropdownChipLayouter.AdapterType.RECIPIENT_ALTERNATES);
    }
}
