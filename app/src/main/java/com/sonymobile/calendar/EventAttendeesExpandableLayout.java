package com.sonymobile.calendar;

import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.sonyericsson.calendar.util.RecurrenceRuleParser;
import com.sonymobile.calendar.generativeartwork.GenerativeArtWorkManager;
import com.sonymobile.calendar.linkedin.LinkedInUtils;
import com.sonymobile.calendar.linkedin.QuickLinkedInContactActivity;
import com.sonymobile.calendar.linkedin.QuickViewSearchActivity;
import com.sonymobile.calendar.linkedin.backend.LinkedInCommunicationService;
import com.sonymobile.calendar.linkedin.model.LinkedInContact;
import com.sonymobile.calendar.linkedin.model.LinkedInImageCache;
import com.sonymobile.calendar.linkedin.model.LinkedInImageLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/* JADX INFO: loaded from: classes2.dex */
public class EventAttendeesExpandableLayout extends LinearLayout {
    static final int EMAIL_ID_COLUMN_INDEX = 0;
    static final int EMAIL_LOOKUP_STRING_COLUMN_INDEX = 1;
    private static final String EXTRA_URI_CONTENT = "uri_content";
    private static final int PRESENCE_PROJECTION_CONTACT_ID_INDEX = 0;
    private static final int PRESENCE_PROJECTION_EMAIL_INDEX = 2;
    private static final int PRESENCE_PROJECTION_PHOTO_ID_INDEX = 3;
    private static final int TOKEN_EMAIL_LOOKUP = 0;
    private BroadcastReceiver linkedInBroadcastReceiver;
    private ArrayList<Attendee> mAttendeeArrayList;
    private LinearLayout mAttendeeHorizontalScrollView;
    private HashSet<String> mAttendeesWithImageEmailsSet;
    private OnReplyClickedListener mCallback;
    private LayoutInflater mLayoutInflater;
    private PresenceQueryHandler mPresenceQueryHandler;
    private LinearLayout mReplyButtons;
    private Button mReplyToAll;
    private Button mReplyToOrganizer;
    private StringBuilder mSelection;
    private ArrayList<String> mSelectionArgs;
    private HashMap<String, ViewHolder> mViewHolders;
    private static final Uri CONTACT_DATA_WITH_PRESENCE_URI = ContactsContract.Data.CONTENT_URI;
    private static final String[] PRESENCE_PROJECTION = {"contact_id", "contact_presence", "data1", "photo_id", "data1"};
    private static final String TAG = "EventAttendeesExpandableLayout";
    static final String[] EMAIL_LOOKUP_PROJECTION = {"contact_id", "lookup"};

    public interface OnReplyClickedListener {
        void onReplyButtonClicked(int i);
    }

    public EventAttendeesExpandableLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mViewHolders = new HashMap<>();
        this.mAttendeesWithImageEmailsSet = new HashSet<>();
        this.mSelectionArgs = new ArrayList<>();
        this.linkedInBroadcastReceiver = new BroadcastReceiver() { // from class: com.sonymobile.calendar.EventAttendeesExpandableLayout.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                ArrayList parcelableArrayListExtra;
                if (intent.getBooleanExtra(LinkedInCommunicationService.REQUEST_SUCCESS_PARAMETER, false) && (parcelableArrayListExtra = intent.getParcelableArrayListExtra(LinkedInCommunicationService.LINKEDIN_CONTACTS_PARAMETER)) != null) {
                    for (int i = 0; i < parcelableArrayListExtra.size(); i++) {
                        LinkedInContact linkedInContact = (LinkedInContact) parcelableArrayListExtra.get(i);
                        String email = linkedInContact.getEmail();
                        ViewHolder viewHolder = (ViewHolder) EventAttendeesExpandableLayout.this.mViewHolders.get(email);
                        if (viewHolder != null) {
                            String photoUrl = linkedInContact.getPhotoUrl();
                            if (TextUtils.isEmpty(photoUrl)) {
                                viewHolder.badge.setImageBitmap(GenerativeArtWorkManager.getInstance(EventAttendeesExpandableLayout.this.getContext()).renderGawPhoto(linkedInContact.getFirstName() + " " + linkedInContact.getLastName(), email));
                            } else {
                                viewHolder.photoId = 1;
                                LinkedInImageLoader.makeImageDownloadRequest(EventAttendeesExpandableLayout.this.getContext(), viewHolder.badge, photoUrl);
                            }
                            EventAttendeesExpandableLayout.this.mAttendeesWithImageEmailsSet.add(email);
                            viewHolder.linkedInLogo.setVisibility(0);
                            viewHolder.contact = linkedInContact;
                        }
                    }
                }
                if (TextUtils.isEmpty(EventAttendeesExpandableLayout.this.mSelection) || !Utils.isReadContactsEnabled(EventAttendeesExpandableLayout.this.getContext())) {
                    EventAttendeesExpandableLayout.this.setGawForAttendeesWithoutImage();
                } else {
                    EventAttendeesExpandableLayout.this.mPresenceQueryHandler.startQuery(0, EventAttendeesExpandableLayout.this.mAttendeeArrayList, EventAttendeesExpandableLayout.CONTACT_DATA_WITH_PRESENCE_URI, EventAttendeesExpandableLayout.PRESENCE_PROJECTION, EventAttendeesExpandableLayout.this.mSelection.toString(), (String[]) EventAttendeesExpandableLayout.this.mSelectionArgs.toArray(new String[EventAttendeesExpandableLayout.this.mSelectionArgs.size()]), null);
                }
            }
        };
        init(context);
    }

    public void registerReceiver(Context context) {
        context.getApplicationContext().registerReceiver(this.linkedInBroadcastReceiver, new IntentFilter(LinkedInCommunicationService.MESSAGE_EMAIL_LOOKUP), 4);
    }

    public void unregisterReceiver(Context context) {
        try {
            context.getApplicationContext().unregisterReceiver(this.linkedInBroadcastReceiver);
        } catch (IllegalArgumentException unused) {
        }
    }

    private void init(Context context) {
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(context);
        this.mLayoutInflater = layoutInflaterFrom;
        layoutInflaterFrom.inflate(R.layout.event_attendees_expandable_layout, (ViewGroup) this, true);
        this.mAttendeeHorizontalScrollView = (LinearLayout) findViewById(R.id.attendee_horizontal_list);
        this.mReplyButtons = (LinearLayout) findViewById(R.id.reply_buttons);
        Button button = (Button) findViewById(R.id.reply_to_organizer);
        this.mReplyToOrganizer = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.EventAttendeesExpandableLayout.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EventAttendeesExpandableLayout.this.mCallback.onReplyButtonClicked(1);
            }
        });
        Button button2 = (Button) findViewById(R.id.reply_to_all);
        this.mReplyToAll = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.EventAttendeesExpandableLayout.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                EventAttendeesExpandableLayout.this.mCallback.onReplyButtonClicked(2);
            }
        });
        this.mPresenceQueryHandler = new PresenceQueryHandler(getContext(), getContext().getContentResolver());
    }

    public void showView(boolean z) {
        if (z) {
            setVisibility(0);
        } else {
            setVisibility(8);
        }
    }

    public void showAttendeesList(boolean z) {
        if (z) {
            setVisibility(0);
        } else {
            setVisibility(8);
        }
    }

    public void showReplyButtons(boolean z) {
        if (z) {
            this.mReplyButtons.setVisibility(0);
        } else {
            this.mReplyButtons.setVisibility(8);
        }
    }

    public void setAttendeesArrayList(ArrayList<Attendee> arrayList) {
        this.mAttendeeArrayList = arrayList;
        if (arrayList.size() > 0) {
            showAttendeesList(true);
            addAttendeesToLayout();
        } else {
            showAttendeesList(false);
        }
    }

    public void setReplyOrganizerButton(String str) {
        if (str == null || str.trim().isEmpty()) {
            this.mReplyToOrganizer.setVisibility(8);
        } else {
            this.mReplyToOrganizer.setVisibility(0);
            this.mReplyToOrganizer.setText(str);
        }
    }

    public void setOnReplyClickedListener(OnReplyClickedListener onReplyClickedListener) {
        this.mCallback = onReplyClickedListener;
    }

    private void addAttendeesToLayout() {
        int i;
        int i2;
        if (this.mAttendeeArrayList.size() == 0) {
            return;
        }
        this.mAttendeeHorizontalScrollView.removeAllViews();
        int size = this.mAttendeeArrayList.size();
        this.mSelectionArgs = new ArrayList<>();
        this.mSelection = new StringBuilder("data1 IN (");
        boolean z = true;
        for (int i3 = 0; i3 < size; i3++) {
            final Attendee attendee = this.mAttendeeArrayList.get(i3);
            if (!attendee.isCalendarOwner) {
                this.mSelectionArgs.add(attendee.email);
                View viewInflate = this.mLayoutInflater.inflate(R.layout.contact_item, (ViewGroup) this.mAttendeeHorizontalScrollView, false);
                viewInflate.setTag(attendee);
                TextView textView = (TextView) viewInflate.findViewById(R.id.name);
                String attendeeNameFromLinkedIn = attendee.name;
                if (attendeeNameFromLinkedIn == null || attendeeNameFromLinkedIn.length() == 0) {
                    attendeeNameFromLinkedIn = getAttendeeNameFromLinkedIn(attendee.email);
                    if (TextUtils.isEmpty(attendeeNameFromLinkedIn)) {
                        attendeeNameFromLinkedIn = attendee.email;
                    }
                }
                if (attendee.isOrganizer) {
                    attendeeNameFromLinkedIn = attendeeNameFromLinkedIn + " (" + getResources().getString(R.string.event_organizer) + ")";
                }
                textView.setText(attendeeNameFromLinkedIn);
                int i4 = attendee.status;
                if (i4 == 1) {
                    i = R.drawable.ic_invite_accept;
                    i2 = R.color.event_response_accept;
                } else if (i4 != 2) {
                    i = R.drawable.ic_invite_maybe;
                    i2 = R.color.event_response_maybe;
                } else {
                    i = R.drawable.ic_invite_decline;
                    i2 = R.color.event_response_decline;
                }
                ImageView imageView = (ImageView) viewInflate.findViewById(R.id.meeting_response_indicator);
                imageView.setImageResource(i);
                imageView.setColorFilter(ContextCompat.getColor(getContext(), i2));
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.badge = (QuickContactBadge) viewInflate.findViewById(R.id.badge);
                viewHolder.linkedInLogo = (ImageView) viewInflate.findViewById(R.id.linkediin_small_logo);
                viewHolder.badge.assignContactFromEmail(attendee.email, true);
                viewHolder.attendeeEmail = attendee.email;
                viewHolder.attendeeName = attendee.name;
                viewHolder.badge.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.EventAttendeesExpandableLayout.3
                    private static final long TIME_DELAY = 500;
                    private long mLastClick;

                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        long jCurrentTimeMillis = System.currentTimeMillis();
                        if (this.mLastClick + TIME_DELAY > jCurrentTimeMillis) {
                            return;
                        }
                        this.mLastClick = jCurrentTimeMillis;
                        Bundle bundle = new Bundle();
                        EventAttendeesExpandableLayout eventAttendeesExpandableLayout = EventAttendeesExpandableLayout.this;
                        QueryHandler queryHandler = eventAttendeesExpandableLayout.new QueryHandler(eventAttendeesExpandableLayout.getContext().getContentResolver(), viewHolder, attendee.email);
                        bundle.putString(EventAttendeesExpandableLayout.EXTRA_URI_CONTENT, attendee.email);
                        queryHandler.startQuery(0, bundle, Uri.withAppendedPath(ContactsContract.CommonDataKinds.Email.CONTENT_LOOKUP_URI, Uri.encode(attendee.email)), EventAttendeesExpandableLayout.EMAIL_LOOKUP_PROJECTION, null, null, null);
                    }
                });
                this.mViewHolders.put(attendee.email, viewHolder);
                ((LinearLayout) viewInflate.findViewById(R.id.contact_name_type)).setTag(textView.getText());
                if (z) {
                    this.mSelection.append('?');
                    z = false;
                } else {
                    this.mSelection.append(", ?");
                }
                this.mAttendeeHorizontalScrollView.addView(viewInflate);
            }
        }
        this.mSelection.append(')');
    }

    private class PresenceQueryHandler extends AsyncQueryHandler {
        Context mContext;

        public PresenceQueryHandler(Context context, ContentResolver contentResolver) {
            super(contentResolver);
            this.mContext = context;
        }

        @Override // android.content.AsyncQueryHandler
        protected void onQueryComplete(int i, Object obj, Cursor cursor) {
            Log.d(EventAttendeesExpandableLayout.TAG, "onQueryComplete: ");
            if (cursor == null) {
                return;
            }
            try {
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    String string = cursor.getString(2);
                    int i2 = cursor.getInt(0);
                    ViewHolder viewHolder = (ViewHolder) EventAttendeesExpandableLayout.this.mViewHolders.get(string);
                    int i3 = cursor.getInt(3);
                    if (viewHolder != null && i3 > 0 && viewHolder.photoId < 0) {
                        EventAttendeesExpandableLayout.this.mAttendeesWithImageEmailsSet.add(string);
                        viewHolder.photoId = i3;
                        ContactsAsyncHelper.getInstance().updateImageViewWithContactPhotoAsync(this.mContext, viewHolder.badge, ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, i2), R.drawable.ic_contact_picture);
                    }
                }
                EventAttendeesExpandableLayout.this.setGawForAttendeesWithoutImage();
            } finally {
                Utils.closeCursor(cursor);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setGawForAttendeesWithoutImage() {
        ViewHolder viewHolder;
        for (Attendee attendee : this.mAttendeeArrayList) {
            Log.d(TAG, "setGawForAttendeesWithoutImage: " + attendee.name);
            if (!this.mAttendeesWithImageEmailsSet.contains(attendee.email) && (viewHolder = this.mViewHolders.get(attendee.email)) != null) {
                viewHolder.badge.setImageBitmap(GenerativeArtWorkManager.getInstance(getContext()).renderGawPhoto(attendee.name, attendee.email));
            }
        }
    }

    private void doLinkedInEmailLookup(ArrayList<Attendee> arrayList) {
        Context context = getContext();
        ArrayList<String> arrayList2 = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            String str = arrayList.get(i).email;
            if (!TextUtils.isEmpty(str) && LinkedInImageCache.getInstance().getContactWithEmail(str) == null && !TextUtils.isEmpty(str)) {
                arrayList2.add(str);
            }
        }
        Intent intent = new Intent(context, (Class<?>) LinkedInCommunicationService.class);
        intent.setAction(LinkedInCommunicationService.ACTION_EMAIL_LOOKUP);
        intent.putStringArrayListExtra(LinkedInCommunicationService.EMAILS_PARAMETER, arrayList2);
        context.startService(intent);
    }

    private void doLinkedInCacheLookup(ArrayList<Attendee> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            Attendee attendee = arrayList.get(i);
            String str = attendee.email;
            if (!TextUtils.isEmpty(str)) {
                LinkedInContact contactWithEmail = LinkedInImageCache.getInstance().getContactWithEmail(str);
                ViewHolder viewHolder = this.mViewHolders.get(str);
                if (contactWithEmail != null) {
                    viewHolder.contact = contactWithEmail;
                    viewHolder.photoId = 1;
                    String photoUrl = contactWithEmail.getPhotoUrl();
                    if (TextUtils.isEmpty(photoUrl)) {
                        viewHolder.badge.setImageBitmap(GenerativeArtWorkManager.getInstance(getContext()).renderGawPhoto(attendee.name, str));
                    } else {
                        this.mAttendeesWithImageEmailsSet.add(str);
                        Bitmap bitmap = LinkedInImageLoader.getCache().getBitmap(contactWithEmail.getPhotoUrl());
                        if (bitmap != null) {
                            viewHolder.badge.setImageBitmap(bitmap);
                        } else {
                            LinkedInImageLoader.makeImageDownloadRequest(getContext(), viewHolder.badge, photoUrl);
                        }
                    }
                    viewHolder.linkedInLogo.setVisibility(0);
                    viewHolder.contact = contactWithEmail;
                    if (TextUtils.isEmpty(arrayList.get(i).name)) {
                        String str2 = contactWithEmail.getFirstName() + " " + contactWithEmail.getLastName();
                        arrayList.get(i).name = str2;
                        viewHolder.attendeeEmail = str2;
                        TextView textView = (TextView) this.mAttendeeHorizontalScrollView.getChildAt(i - 1).findViewById(R.id.name);
                        textView.setText(str2);
                        textView.invalidate();
                    }
                }
            }
        }
        doLinkedInEmailLookup(this.mAttendeeArrayList);
    }

    public void updateContactPictures() {
        ArrayList<Attendee> arrayList = this.mAttendeeArrayList;
        if (arrayList == null || arrayList.size() <= 0) {
            return;
        }
        if (LinkedInUtils.isLinkedInSyncValid(getContext()) && Utils.isDataTrafficEnabled(getContext())) {
            doLinkedInCacheLookup(this.mAttendeeArrayList);
            return;
        }
        if (Utils.isReadContactsEnabled(getContext())) {
            PresenceQueryHandler presenceQueryHandler = this.mPresenceQueryHandler;
            ArrayList<Attendee> arrayList2 = this.mAttendeeArrayList;
            Uri uri = CONTACT_DATA_WITH_PRESENCE_URI;
            String[] strArr = PRESENCE_PROJECTION;
            String string = this.mSelection.toString();
            ArrayList<String> arrayList3 = this.mSelectionArgs;
            presenceQueryHandler.startQuery(0, arrayList2, uri, strArr, string, (String[]) arrayList3.toArray(new String[arrayList3.size()]), null);
            return;
        }
        setGawForAttendeesWithoutImage();
    }

    private String getAttendeeNameFromLinkedIn(String str) {
        LinkedInContact contactWithEmail = LinkedInImageCache.getInstance().getContactWithEmail(str);
        if (contactWithEmail == null) {
            return null;
        }
        return contactWithEmail.getFirstName() + " " + contactWithEmail.getLastName();
    }

    private static class ViewHolder {
        String attendeeEmail;
        String attendeeName;
        QuickContactBadge badge;
        LinkedInContact contact;
        ImageView linkedInLogo;
        int photoId;

        private ViewHolder() {
            this.photoId = -1;
        }
    }

    public static class Attendee {
        String email;
        boolean isCalendarOwner;
        boolean isOrganizer;
        String name;
        int status;

        Attendee(String str, String str2, int i, boolean z, boolean z2, int i2) {
            this.name = str;
            this.email = str2;
            this.isOrganizer = z;
            this.isCalendarOwner = z2;
            this.status = i2;
        }
    }

    private class QueryHandler extends AsyncQueryHandler {
        String email;
        Uri mContactUri;
        ViewHolder viewHolder;

        public QueryHandler(ContentResolver contentResolver, ViewHolder viewHolder, String str) {
            super(contentResolver);
            this.viewHolder = viewHolder;
            this.email = str;
        }

        /* JADX WARN: Code duplicated, block: B:13:0x0021  */
        @Override // android.content.AsyncQueryHandler
        protected void onQueryComplete(int i, Object obj, Cursor cursor) {
            Uri lookupUri;
            if (i == 0 && cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        lookupUri = ContactsContract.Contacts.getLookupUri(cursor.getLong(0), cursor.getString(1));
                    } else {
                        lookupUri = null;
                    }
                } catch (Throwable th) {
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            } else {
                lookupUri = null;
            }
            if (cursor != null) {
                cursor.close();
            }
            this.mContactUri = lookupUri;
            if (!LinkedInUtils.isLinkedInSyncValid(EventAttendeesExpandableLayout.this.getContext()) && Utils.isReadContactsEnabled(EventAttendeesExpandableLayout.this.getContext())) {
                if (this.mContactUri != null) {
                    viewInContactsApp();
                    return;
                } else {
                    showAddToContactsDialog();
                    return;
                }
            }
            if (this.viewHolder.contact != null) {
                showQuickLinkedInContactActivity();
            } else {
                showQuickViewSearchActivity();
            }
        }

        private void showQuickViewSearchActivity() {
            String str;
            String str2 = "";
            if (!TextUtils.isEmpty(this.viewHolder.attendeeName) && this.viewHolder.attendeeName.contains(RecurrenceRuleParser.VALUE_SEPARATOR)) {
                String[] strArrSplit = this.viewHolder.attendeeName.split(", ");
                str2 = strArrSplit[1].split(" ")[0];
                str = strArrSplit[0];
            } else if (!TextUtils.isEmpty(this.viewHolder.attendeeName) && this.viewHolder.attendeeName.contains(" ")) {
                String[] strArrSplit2 = this.viewHolder.attendeeName.split(" ");
                if (strArrSplit2.length == 0) {
                    return;
                }
                String str3 = strArrSplit2[0];
                str = strArrSplit2.length > 1 ? strArrSplit2[1] : "";
                str2 = str3;
            } else if (TextUtils.isEmpty(this.viewHolder.attendeeName)) {
                str = "";
            } else {
                str2 = this.viewHolder.attendeeName;
                str = "";
            }
            Intent intent = new Intent(EventAttendeesExpandableLayout.this.getContext(), (Class<?>) QuickViewSearchActivity.class);
            intent.putExtra(LinkedInUtils.LINKEDIN_FIRST_NAME, str2);
            intent.putExtra(LinkedInUtils.LINKEDIN_LAST_NAME, str);
            intent.putExtra(LinkedInUtils.LINKEDIN_EMAIL, this.viewHolder.attendeeEmail);
            Drawable drawable = this.viewHolder.badge.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                intent.putExtra(QuickViewSearchActivity.CONTACT_PICTURE_FROM_CONTACT_APP, ((BitmapDrawable) drawable).getBitmap());
            }
            intent.setData(this.mContactUri);
            EventAttendeesExpandableLayout.this.getContext().startActivity(intent);
        }

        private void showQuickLinkedInContactActivity() {
            Intent intent = new Intent(EventAttendeesExpandableLayout.this.getContext(), (Class<?>) QuickLinkedInContactActivity.class);
            intent.putExtra(QuickLinkedInContactActivity.CONTACT_LINKEDIN_EXTRA, this.viewHolder.contact);
            intent.putExtra(QuickLinkedInContactActivity.CONTACT_LINKEDIN_EXTRA_EMAIL_FROM_EVENT, this.email);
            intent.setData(this.mContactUri);
            EventAttendeesExpandableLayout.this.getContext().startActivity(intent);
        }

        private void showAddToContactsDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(EventAttendeesExpandableLayout.this.getContext(), R.style.AlertDialogTheme);
            String string = EventAttendeesExpandableLayout.this.getContext().getResources().getString(R.string.add_to_contact);
            builder.setTitle(string);
            builder.setMessage(string + " " + this.viewHolder.attendeeEmail);
            builder.setPositiveButton(R.string.clr_strings_button_title_ok_txt, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.EventAttendeesExpandableLayout.QueryHandler.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent("android.intent.action.INSERT_OR_EDIT");
                    intent.setType("vnd.android.cursor.item/contact");
                    intent.putExtra("email", QueryHandler.this.viewHolder.attendeeEmail);
                    intent.putExtra("name", QueryHandler.this.viewHolder.attendeeName);
                    EventAttendeesExpandableLayout.this.getContext().startActivity(intent);
                }
            });
            builder.setNegativeButton(R.string.clr_strings_button_title_cancel_txt, new DialogInterface.OnClickListener() { // from class: com.sonymobile.calendar.EventAttendeesExpandableLayout.QueryHandler.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        }

        private void viewInContactsApp() {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(this.mContactUri);
            EventAttendeesExpandableLayout.this.getContext().startActivity(intent);
        }
    }
}
