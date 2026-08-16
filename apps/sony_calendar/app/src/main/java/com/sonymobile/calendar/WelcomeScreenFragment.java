package com.sonymobile.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.sonymobile.calendar.linkedin.LinkedInUtils;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class WelcomeScreenFragment extends Fragment {
    private WelcomeScreenCloseListener mWelcomeScreenCloseListener;

    public interface WelcomeScreenCloseListener {
        void onWelcomeScreenClose();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof WelcomeScreenCloseListener) {
            this.mWelcomeScreenCloseListener = (WelcomeScreenCloseListener) activity;
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.welcome_screen, viewGroup, false);
        UiUtils.setNavigationBar(getActivity());
        return viewInflate;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        init();
    }

    private void init() {
        final FragmentActivity activity = getActivity();
        TextView textView = (TextView) activity.findViewById(R.id.get_started);
        UiUtils.setViewBackgroundToAccentColor(getContext(), textView);
        textView.requestFocus();
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.WelcomeScreenFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                WelcomeScreenFragment.this.goToCalendar();
            }
        });
        if (LinkedInUtils.isLinkedInSyncValid(activity)) {
            activity.findViewById(R.id.connect_with_linkedin_welcome_label).setVisibility(4);
            activity.findViewById(R.id.connect_with_linkedin_welcome).setVisibility(4);
        } else {
            ((Button) activity.findViewById(R.id.connect_with_linkedin_welcome)).setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.WelcomeScreenFragment.2
                private static final long CLICK_DELAY = 500;
                private long mLastClick;

                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    if (Math.abs(this.mLastClick - jCurrentTimeMillis) < CLICK_DELAY) {
                        return;
                    }
                    this.mLastClick = jCurrentTimeMillis;
                    LinkedInUtils.startSyncWithLinkedInActivity(activity, LinkedInUtils.LinkedInActivationEntrypoint.WELCOME_SCREEN);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void goToCalendar() {
        Utils.disableWelcomeScreen(getActivity());
        WelcomeScreenCloseListener welcomeScreenCloseListener = this.mWelcomeScreenCloseListener;
        if (welcomeScreenCloseListener != null) {
            welcomeScreenCloseListener.onWelcomeScreenClose();
        }
    }
}
