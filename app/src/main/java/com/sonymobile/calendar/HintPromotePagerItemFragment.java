package com.sonymobile.calendar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.sonymobile.calendar.linkedin.LinkedInUtils;
import com.sonymobile.calendar.utils.UiUtils;
import java.util.Set;

/* JADX INFO: loaded from: classes2.dex */
public class HintPromotePagerItemFragment extends Fragment {
    private static final String KEY_ACTION = "ACTION";
    private static final String KEY_DESCRIPTION = "DESCRIPTION";
    private static final String KEY_ID = "ID";
    private static final String KEY_POSITION = "position";
    private static final String KEY_TITLE = "TITLE";
    private int ID;
    private String action;
    private String description;
    private Button mActionButton;
    private HintPromoteFragment parentFragment;
    private int position = -1;
    private View root;
    private String title;

    public static HintPromotePagerItemFragment newInstance(int i, int i2, String str, String str2, String str3) {
        HintPromotePagerItemFragment hintPromotePagerItemFragment = new HintPromotePagerItemFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ID, i);
        bundle.putInt(KEY_POSITION, i2);
        bundle.putString(KEY_TITLE, str);
        bundle.putString("DESCRIPTION", str2);
        bundle.putString(KEY_ACTION, str3);
        hintPromotePagerItemFragment.setArguments(bundle);
        return hintPromotePagerItemFragment;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        this.parentFragment = (HintPromoteFragment) getParentFragment();
        this.root = layoutInflater.inflate(R.layout.hint_promote_item_fragment, viewGroup, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.ID = arguments.getInt(KEY_ID);
            this.position = arguments.getInt(KEY_POSITION);
            this.title = arguments.getString(KEY_TITLE);
            this.description = arguments.getString("DESCRIPTION");
            this.action = arguments.getString(KEY_ACTION);
            ((TextView) this.root.findViewById(R.id.title)).setText(TextUtils.isEmpty(this.title) ? "" : this.title);
            ((TextView) this.root.findViewById(R.id.title)).setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_primary_text_color));
            if (this.position != -1) {
                ImageView imageView = (ImageView) this.root.findViewById(R.id.illustration);
                boolean zIsPhonePortrait = UiUtils.isPhonePortrait(getActivity());
                int i = R.drawable.calendar_hint_weather;
                if (zIsPhonePortrait) {
                    int i2 = this.position;
                    float intrinsicWidth;
                    float intrinsicHeight;
                    if (i2 == 0) {
                        intrinsicWidth = getResources().getDrawable(R.drawable.calendar_hint_weather).getIntrinsicWidth();
                        intrinsicHeight = getResources().getDrawable(R.drawable.calendar_hint_weather).getIntrinsicHeight();
                    } else if (i2 == 1) {
                        intrinsicWidth = getResources().getDrawable(R.drawable.calendar_hint_color).getIntrinsicWidth();
                        intrinsicHeight = getResources().getDrawable(R.drawable.calendar_hint_color).getIntrinsicHeight();
                    } else {
                        intrinsicWidth = 0.0f;
                        intrinsicHeight = 0.0f;
                    }
                    float f2 = intrinsicWidth / intrinsicHeight;
                    ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                    layoutParams.width = this.parentFragment.getDisplayWidth();
                    layoutParams.height = (int) (layoutParams.width / f2);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }
                if (this.position != 0) {
                    i = R.drawable.calendar_hint_color;
                }
                imageView.setImageResource(i);
            }
            ((TextView) this.root.findViewById(R.id.description)).setText(TextUtils.isEmpty(this.description) ? "" : this.description);
            ((TextView) this.root.findViewById(R.id.description)).setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_primary_text_color));
            this.mActionButton = (Button) this.root.findViewById(R.id.action_button);
            UiUtils.setViewBackgroundToAccentColor(getContext(), this.mActionButton);
            if (TextUtils.isEmpty(this.action)) {
                this.mActionButton.setVisibility(8);
            } else {
                this.mActionButton.setText(this.action);
                this.mActionButton.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.HintPromotePagerItemFragment.1
                    private static final long CLICK_DELAY = 1000;
                    private long lastClickTime = 0;

                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        long jCurrentTimeMillis = System.currentTimeMillis();
                        if (Math.abs(jCurrentTimeMillis - this.lastClickTime) < CLICK_DELAY) {
                            return;
                        }
                        this.lastClickTime = jCurrentTimeMillis;
                        if (HintPromotePagerItemFragment.this.parentFragment == null || !HintPromotePagerItemFragment.this.parentFragment.isAdded()) {
                            return;
                        }
                        HintPromotePagerItemFragment.this.parentFragment.onActionClick(HintPromotePagerItemFragment.this.ID);
                    }
                });
                this.parentFragment.onActionButtonInitialized(this.mActionButton, this.ID);
            }
        }
        return this.root;
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.parentFragment = (HintPromoteFragment) getParentFragment();
        pageSelected();
        if (this.ID == R.id.linkedin_hint_promo_id) {
            this.mActionButton.setEnabled(!LinkedInUtils.isLinkedInSyncValid(getActivity()));
        }
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String str) {
        TextView textView = (TextView) this.root.findViewById(R.id.description);
        if (TextUtils.isEmpty(str)) {
            str = "";
        }
        textView.setText(str);
        ((TextView) this.root.findViewById(R.id.description)).setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_primary_text_color));
    }

    public void pageSelected() {
        if (this.ID == R.id.holidays_hint_promo_id) {
            Set<String> stringSet = getActivity().getSharedPreferences("com.sonymobile.calendar_preferences", 0).getStringSet(GeneralPreferences.KEY_NATIONAL_HOLIDAYS, null);
            if (stringSet != null && stringSet.size() != 0) {
                setDescription(getDescription() + this.parentFragment.getEntryLabels(stringSet));
            } else {
                setDescription(getDescription().split("\\n\\n")[0]);
            }
        }
    }
}
