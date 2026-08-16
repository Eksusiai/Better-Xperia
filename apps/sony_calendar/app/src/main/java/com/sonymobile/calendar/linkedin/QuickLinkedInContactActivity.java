package com.sonymobile.calendar.linkedin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.linkedin.platform.DeepLinkHelper;
import com.linkedin.platform.errors.LIDeepLinkError;
import com.linkedin.platform.listeners.DeepLinkListener;
import com.sonyericsson.calendar.util.EmailIntentUtil;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.generativeartwork.GenerativeArtWorkManager;
import com.sonymobile.calendar.linkedin.backend.LinkedInCommunicationService;
import com.sonymobile.calendar.linkedin.model.LinkedInContact;
import com.sonymobile.calendar.linkedin.model.LinkedInImageLoader;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class QuickLinkedInContactActivity extends AppCompatActivity {
    public static final String CONTACT_LINKEDIN_EXTRA = "contactLinkedInExtra";
    public static final String CONTACT_LINKEDIN_EXTRA_EMAIL_FROM_EVENT = "contactLinkedInExtraEmailFromEvent";
    BroadcastReceiver connectionInvateReceiver = new BroadcastReceiver() { // from class: com.sonymobile.calendar.linkedin.QuickLinkedInContactActivity.7
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(LinkedInCommunicationService.REQUEST_SUCCESS_PARAMETER, false)) {
                Toast.makeText(QuickLinkedInContactActivity.this, R.string.linkedin_add_to_your_network_success_message, 1).show();
            } else {
                Toast.makeText(QuickLinkedInContactActivity.this, R.string.linkedin_add_to_your_network_error_message, 1).show();
            }
        }
    };
    protected TextView linkedInConnectionNumber;
    protected TextView linkedInContactCompany;
    protected TextView linkedInContactCompanyViewProfile;
    protected ImageView linkedInContactConnectWith;
    protected TextView linkedInContactEmail;
    protected QuickContactBadge linkedInContactImage;
    protected TextView linkedInContactLocation;
    protected TextView linkedInContactName;
    protected TextView linkedInContactPosition;
    protected LinkedInContact mContact;
    private Uri mContactUri;
    private String mEmailFromEventDetails;
    protected String mFirstName;
    protected String mLastName;
    protected ViewGroup mViewProfile;

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Utils.isBuildVersionLollipopOrPlus()) {
            getWindow().setStatusBarColor(0);
        }
        setContentView(R.layout.linked_in_profil_view);
        ((TextView) findViewById(R.id.email_label)).setTextColor(UiUtils.getPrimaryColor(this));
        ((TextView) findViewById(R.id.view_linkedin_profile)).setTextColor(UiUtils.getPrimaryColor(this));
        if (Utils.isTabletDevice(this)) {
            ((TextView) findViewById(R.id.view_linkedid_search_profile)).setTextColor(UiUtils.getPrimaryColor(this));
        }
        Intent intent = getIntent();
        this.mContact = (LinkedInContact) intent.getParcelableExtra(CONTACT_LINKEDIN_EXTRA);
        this.mEmailFromEventDetails = intent.getStringExtra(CONTACT_LINKEDIN_EXTRA_EMAIL_FROM_EVENT);
        this.mContactUri = intent.getData();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        registerReceiver(this.connectionInvateReceiver, new IntentFilter(LinkedInCommunicationService.MESSAGE_CONTACT_INVITE), 4);
        init();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        unregisterReceiver(this.connectionInvateReceiver);
    }

    protected void init() {
        String email = this.mContact.getEmail();
        this.mFirstName = this.mContact.getFirstName();
        this.mLastName = this.mContact.getLastName();
        StringBuilder sb = new StringBuilder(this.mFirstName);
        sb.append(" ");
        sb.append(this.mLastName);
        this.linkedInContactImage = (QuickContactBadge) findViewById(R.id.linkedin_contact_image);
        if (!TextUtils.isEmpty(this.mContact.getPhotoUrl())) {
            LinkedInImageLoader.makeImageDownloadRequest(this, this.linkedInContactImage, this.mContact.getPhotoUrl());
        } else {
            this.linkedInContactImage.setImageBitmap(GenerativeArtWorkManager.getInstance(this).renderGawPhoto(sb.toString(), email));
        }
        findViewById(R.id.linkediin_small_logo_view).setVisibility(0);
        TextView textView = (TextView) findViewById(R.id.linkedin_contact_name);
        this.linkedInContactName = textView;
        textView.setText(sb);
        TextView textView2 = (TextView) findViewById(R.id.linkedin_contact_position);
        this.linkedInContactPosition = textView2;
        textView2.setText(this.mContact.getHeadline());
        TextView textView3 = (TextView) findViewById(R.id.linkedin_contact_company);
        this.linkedInContactCompany = textView3;
        textView3.setText(this.mContact.getIndustry());
        TextView textView4 = (TextView) findViewById(R.id.linkedin_contact_location);
        this.linkedInContactLocation = textView4;
        textView4.setText(this.mContact.getLocation());
        TextView textView5 = (TextView) findViewById(R.id.linkedin_contact_email);
        this.linkedInContactEmail = textView5;
        if (TextUtils.isEmpty(email)) {
            email = this.mEmailFromEventDetails;
        }
        textView5.setText(email);
        this.linkedInContactConnectWith = (ImageView) findViewById(R.id.connect_on_linkedin);
        if (this.mContact.isInConnections() || !Utils.isDataTrafficEnabled(this)) {
            this.linkedInContactConnectWith.setVisibility(8);
        } else {
            this.linkedInContactConnectWith.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.linkedin.QuickLinkedInContactActivity.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    QuickLinkedInContactActivity quickLinkedInContactActivity = QuickLinkedInContactActivity.this;
                    LinkedInUtils.sendConnectionRequest(quickLinkedInContactActivity, quickLinkedInContactActivity.mContact);
                }
            });
        }
        String connectionString = LinkedInUtils.getConnectionString(this, this.mContact.getConnectionLevel());
        if (!TextUtils.isEmpty(connectionString)) {
            TextView textView6 = (TextView) findViewById(R.id.linkedin_connection_number);
            this.linkedInConnectionNumber = textView6;
            textView6.setTextColor(UiUtils.getPrimaryColor(this));
            this.linkedInConnectionNumber.setText(connectionString);
            this.linkedInConnectionNumber.setVisibility(0);
        }
        TextView textView7 = (TextView) findViewById(R.id.linkedin_contact_company_view);
        this.linkedInContactCompanyViewProfile = textView7;
        textView7.setText(this.mContact.getHeadline());
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.view_linkedid_profile);
        this.mViewProfile = viewGroup;
        viewGroup.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.linkedin.QuickLinkedInContactActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (!Utils.checkPackageAvailable(QuickLinkedInContactActivity.this, "com.linkedin.android")) {
                    LinkedInUtils.startSyncWithLinkedInActivity(QuickLinkedInContactActivity.this, LinkedInUtils.LinkedInActivationEntrypoint.UNKNOWN_ENTRYPOINT);
                    return;
                }
                DeepLinkHelper deepLinkHelper = DeepLinkHelper.getInstance();
                QuickLinkedInContactActivity quickLinkedInContactActivity = QuickLinkedInContactActivity.this;
                deepLinkHelper.openOtherProfile(quickLinkedInContactActivity, quickLinkedInContactActivity.mContact.getLinkedinId(), new DeepLinkListener() { // from class: com.sonymobile.calendar.linkedin.QuickLinkedInContactActivity.2.1
                    @Override // com.linkedin.platform.listeners.DeepLinkListener
                    public void onDeepLinkError(LIDeepLinkError lIDeepLinkError) {
                    }

                    @Override // com.linkedin.platform.listeners.DeepLinkListener
                    public void onDeepLinkSuccess() {
                    }
                });
            }
        });
        initSendEmailAndContactsContainers();
        setTouchToClose();
    }

    protected void initSendEmailAndContactsContainers() {
        ((ViewGroup) findViewById(R.id.send_email_layout)).setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.linkedin.QuickLinkedInContactActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                QuickLinkedInContactActivity.this.sendEmail();
            }
        });
        if (this.mContactUri == null) {
            ViewGroup viewGroup = (ViewGroup) findViewById(R.id.add_to_contacts_layout);
            if (Utils.isReadContactsEnabled(this)) {
                viewGroup.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.linkedin.QuickLinkedInContactActivity.4
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        QuickLinkedInContactActivity.this.addToContacts();
                    }
                });
            } else {
                viewGroup.setVisibility(8);
            }
            findViewById(R.id.show_in_contacts_layout).setVisibility(8);
            return;
        }
        ViewGroup viewGroup2 = (ViewGroup) findViewById(R.id.show_in_contacts_layout);
        viewGroup2.setVisibility(0);
        findViewById(R.id.add_to_contacts_layout).setVisibility(8);
        if (Utils.isReadContactsEnabled(this)) {
            viewGroup2.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.linkedin.QuickLinkedInContactActivity.5
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setData(QuickLinkedInContactActivity.this.mContactUri);
                    QuickLinkedInContactActivity.this.startActivity(intent);
                }
            });
        } else {
            viewGroup2.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendEmail() {
        Intent genericSendEmailIntent = EmailIntentUtil.getGenericSendEmailIntent(new String[]{this.linkedInContactEmail.getText().toString()}, null, null);
        if (Utils.isIntentRecipientAvailable(this, genericSendEmailIntent)) {
            startActivity(genericSendEmailIntent);
        } else {
            Toast.makeText(this, R.string.quick_response_email_failed, 1).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addToContacts() {
        Intent intent = new Intent("android.intent.action.INSERT_OR_EDIT");
        intent.setType("vnd.android.cursor.item/contact");
        intent.putExtra("email", this.linkedInContactEmail.getText().toString());
        intent.putExtra("name", this.mFirstName + " " + this.mLastName);
        startActivity(intent);
        finish();
    }

    protected void setTouchToClose() {
        findViewById(R.id.touch_to_close).setOnTouchListener(new View.OnTouchListener() { // from class: com.sonymobile.calendar.linkedin.QuickLinkedInContactActivity.6
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 8) {
                    QuickLinkedInContactActivity.this.finish();
                }
                return true;
            }
        });
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        LinkedInUtils.handleSyncWithLinkedInResult(this, i, i2, intent);
        super.onActivityResult(i, i2, intent);
    }
}
