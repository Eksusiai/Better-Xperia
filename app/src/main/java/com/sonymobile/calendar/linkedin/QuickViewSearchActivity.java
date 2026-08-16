package com.sonymobile.calendar.linkedin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.generativeartwork.GenerativeArtWorkManager;

/* JADX INFO: loaded from: classes2.dex */
public class QuickViewSearchActivity extends QuickLinkedInContactActivity {
    public static final String CONTACT_PICTURE_FROM_CONTACT_APP = "pictureFromContactApplication";
    private Bitmap mContactBitmap;
    private String mEmail;

    @Override // com.sonymobile.calendar.linkedin.QuickLinkedInContactActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        this.mFirstName = intent.getStringExtra(LinkedInUtils.LINKEDIN_FIRST_NAME);
        this.mLastName = intent.getStringExtra(LinkedInUtils.LINKEDIN_LAST_NAME);
        this.mEmail = intent.getStringExtra(LinkedInUtils.LINKEDIN_EMAIL);
        this.mContactBitmap = (Bitmap) intent.getParcelableExtra(CONTACT_PICTURE_FROM_CONTACT_APP);
        if (bundle != null) {
            this.mFirstName = bundle.getString(LinkedInUtils.LINKEDIN_FIRST_NAME);
            this.mLastName = bundle.getString(LinkedInUtils.LINKEDIN_LAST_NAME);
            this.mEmail = bundle.getString(LinkedInUtils.LINKEDIN_EMAIL);
        }
    }

    @Override // com.sonymobile.calendar.linkedin.QuickLinkedInContactActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override // com.sonymobile.calendar.linkedin.QuickLinkedInContactActivity
    protected void init() {
        StringBuilder sb = new StringBuilder(this.mFirstName);
        sb.append(" ");
        sb.append(this.mLastName);
        this.linkedInContactName = (TextView) findViewById(R.id.linkedin_contact_name);
        this.linkedInContactName.setText(sb.toString());
        this.linkedInContactImage = (QuickContactBadge) findViewById(R.id.linkedin_contact_image);
        if (this.mContactBitmap != null) {
            this.linkedInContactImage.setImageBitmap(this.mContactBitmap);
            findViewById(R.id.linkediin_small_logo_view).setVisibility(8);
        } else {
            this.linkedInContactImage.setImageBitmap(GenerativeArtWorkManager.getInstance(this).renderGawPhoto(sb.toString(), this.mEmail));
        }
        this.linkedInContactPosition = (TextView) findViewById(R.id.linkedin_contact_position);
        this.linkedInContactPosition.setVisibility(8);
        this.linkedInContactCompany = (TextView) findViewById(R.id.linkedin_contact_company);
        this.linkedInContactCompany.setVisibility(8);
        this.linkedInContactLocation = (TextView) findViewById(R.id.linkedin_contact_location);
        this.linkedInContactLocation.setVisibility(8);
        this.linkedInContactEmail = (TextView) findViewById(R.id.linkedin_contact_email);
        this.linkedInContactEmail.setText(this.mEmail);
        ((ViewGroup) findViewById(R.id.view_linkedid_profile_layout)).setVisibility(8);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.start_search_layout);
        viewGroup.setVisibility(Utils.isDataTrafficEnabled(this) ? 0 : 8);
        viewGroup.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.linkedin.QuickViewSearchActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (!LinkedInUtils.isLinkedInSyncValid(QuickViewSearchActivity.this)) {
                    QuickViewSearchActivity quickViewSearchActivity = QuickViewSearchActivity.this;
                    Toast.makeText(quickViewSearchActivity, quickViewSearchActivity.getString(R.string.connect_with_linekdin_to_search_message), 1).show();
                    return;
                }
                Intent intent = new Intent(QuickViewSearchActivity.this, (Class<?>) LinkedInSearchActivity.class);
                intent.putExtra(LinkedInUtils.LINKEDIN_FIRST_NAME, QuickViewSearchActivity.this.mFirstName);
                intent.putExtra(LinkedInUtils.LINKEDIN_LAST_NAME, QuickViewSearchActivity.this.mLastName);
                intent.putExtra(LinkedInUtils.LINKEDIN_EMAIL_FOR_STORE, QuickViewSearchActivity.this.mEmail);
                QuickViewSearchActivity.this.startActivity(intent);
                QuickViewSearchActivity.this.finish();
            }
        });
        super.initSendEmailAndContactsContainers();
        super.setTouchToClose();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(LinkedInUtils.LINKEDIN_FIRST_NAME, this.mFirstName);
        bundle.putString(LinkedInUtils.LINKEDIN_LAST_NAME, this.mLastName);
        bundle.putString(LinkedInUtils.LINKEDIN_EMAIL, this.mEmail);
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.mFirstName = bundle.getString(LinkedInUtils.LINKEDIN_FIRST_NAME);
        this.mLastName = bundle.getString(LinkedInUtils.LINKEDIN_LAST_NAME);
        this.mEmail = bundle.getString(LinkedInUtils.LINKEDIN_EMAIL);
    }
}
