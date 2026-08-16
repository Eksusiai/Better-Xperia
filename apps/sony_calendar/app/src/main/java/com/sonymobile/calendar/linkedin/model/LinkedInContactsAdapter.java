package com.sonymobile.calendar.linkedin.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.generativeartwork.GenerativeArtWorkManager;
import com.sonymobile.calendar.linkedin.LinkedInUtils;
import com.sonymobile.calendar.utils.UiUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class LinkedInContactsAdapter extends BaseAdapter {
    private boolean mBusy;
    private Context mContext;
    private ArrayList<LinkedInContact> mLinkedinContacts;
    private int mSearchResultLayout;

    @Override // android.widget.Adapter
    public long getItemId(int i) {
        return i;
    }

    public void setBusy(boolean z) {
        this.mBusy = z;
    }

    static class ViewHolder {
        ImageView connectOnLinkedIn;
        TextView connectionNumber;
        QuickContactBadge contactBadge;
        TextView contactCompany;
        TextView contactLocation;
        TextView contactName;
        TextView contactPosition;
        ImageView linkedInLogo;

        ViewHolder() {
        }
    }

    public LinkedInContactsAdapter(Context context, int i, ArrayList<LinkedInContact> arrayList) {
        this.mSearchResultLayout = i;
        this.mContext = context;
        ArrayList<LinkedInContact> arrayList2 = new ArrayList<>();
        this.mLinkedinContacts = arrayList2;
        arrayList2.addAll(arrayList);
    }

    @Override // android.widget.Adapter
    public int getCount() {
        ArrayList<LinkedInContact> arrayList = this.mLinkedinContacts;
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return this.mLinkedinContacts.get(i);
    }

    @Override // android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(this.mContext, this.mSearchResultLayout, null);
        }
        ViewHolder viewHolder = view.getTag() instanceof ViewHolder ? (ViewHolder) view.getTag() : null;
        final LinkedInContact linkedInContact = (LinkedInContact) getItem(i);
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.contactBadge = (QuickContactBadge) view.findViewById(R.id.linkedin_search_result_contact_image);
            viewHolder.linkedInLogo = (ImageView) view.findViewById(R.id.linkediin_small_logo_view);
            viewHolder.contactName = (TextView) view.findViewById(R.id.linkedin_search_result_contact_name);
            viewHolder.contactPosition = (TextView) view.findViewById(R.id.linkedin_search_result_contact_position);
            viewHolder.contactLocation = (TextView) view.findViewById(R.id.linkedin_search_result_contact_location);
            viewHolder.contactCompany = (TextView) view.findViewById(R.id.linkedin_search_result_contact_company);
            viewHolder.connectOnLinkedIn = (ImageView) view.findViewById(R.id.item_connect_on_linkedin);
            viewHolder.connectionNumber = (TextView) view.findViewById(R.id.linkedin_search_result_connection_number);
            view.setTag(viewHolder);
        }
        if (linkedInContact.hasPhotoUrl()) {
            if (!this.mBusy) {
                Bitmap bitmap = LinkedInImageLoader.getCache().getBitmap(linkedInContact.getPhotoUrl());
                if (bitmap != null) {
                    viewHolder.contactBadge.setImageBitmap(bitmap);
                } else {
                    LinkedInImageLoader.makeImageDownloadRequest(this.mContext, viewHolder.contactBadge, linkedInContact.getPhotoUrl());
                }
                setImageVisibility(viewHolder, 0);
            } else {
                setImageVisibility(viewHolder, 4);
            }
        } else {
            if (!this.mBusy) {
                viewHolder.contactBadge.setImageBitmap(GenerativeArtWorkManager.getInstance(this.mContext).renderGawPhoto(linkedInContact.getFirstName() + " " + linkedInContact.getLastName(), linkedInContact.getEmail()));
                setImageVisibility(viewHolder, 0);
            } else {
                setImageVisibility(viewHolder, 4);
            }
        }
        viewHolder.contactName.setText(linkedInContact.getFirstName() + " " + linkedInContact.getLastName());
        viewHolder.contactCompany.setText(linkedInContact.getIndustry());
        viewHolder.contactLocation.setText(linkedInContact.getLocation());
        viewHolder.contactPosition.setText(linkedInContact.getHeadline());
        if (linkedInContact.isInConnections()) {
            viewHolder.connectOnLinkedIn.setVisibility(4);
        } else {
            viewHolder.connectOnLinkedIn.setVisibility(0);
            viewHolder.connectOnLinkedIn.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.linkedin.model.LinkedInContactsAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    LinkedInUtils.sendConnectionRequest((Activity) LinkedInContactsAdapter.this.mContext, linkedInContact);
                }
            });
        }
        String connectionString = LinkedInUtils.getConnectionString(this.mContext, linkedInContact.getConnectionLevel());
        if (!TextUtils.isEmpty(connectionString)) {
            viewHolder.connectionNumber.setText(connectionString);
            viewHolder.connectionNumber.setVisibility(0);
            viewHolder.connectionNumber.setTextColor(UiUtils.getPrimaryColor(this.mContext));
        }
        return view;
    }

    private void setImageVisibility(ViewHolder viewHolder, int i) {
        viewHolder.contactBadge.setVisibility(i);
        viewHolder.linkedInLogo.setVisibility(i);
    }

    public ArrayList<LinkedInContact> toArrayList() {
        return this.mLinkedinContacts;
    }

    public void addAll(ArrayList<LinkedInContact> arrayList) {
        ArrayList<LinkedInContact> arrayList2 = this.mLinkedinContacts;
        if (arrayList2 != null) {
            arrayList2.addAll(arrayList);
            notifyDataSetChanged();
        }
    }
}
