package com.sonymobile.calendar;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.viewpager.widget.ViewPager;
import com.sonymobile.calendar.agendapager.FragmentsPagerAdapter;
import com.sonymobile.calendar.linkedin.LinkedInUtils;
import com.sonymobile.calendar.preference.SortedMultiSelectPreference;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/* JADX INFO: loaded from: classes2.dex */
public class HintPromoteFragment extends PreferenceFragmentCompat implements View.OnClickListener, ViewPager.OnPageChangeListener, PreferenceManager.OnDisplayPreferenceDialogListener {
    private static final String DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG";
    static final String HOLIDAY_PREFERENCES = "com.sonymobile.calendar_preferences";
    static final String KEY_HOLIDAYS = "preferences_national_holidays";
    private static final String KEY_PAGE = "KEY_PAGE";
    static final String NEWLINES = "\\n\\n";
    private String[] actions;
    private String[] descriptions;
    private TypedArray ids;
    private boolean isRTL;
    private ImageView leftImage;
    private int mDispalyWidth;
    private int mDisplayHeight;
    private View nextButton;
    private TextView nextLabel;
    private ViewPager pager;
    private int pagerSize;
    private HintPromotePagerItemFragment[] pages;
    private View prevButton;
    private ImageView rightImage;
    private TextView switcherText;
    private String[] titles;
    private int currentPage = 0;
    SortedMultiSelectPreference mNationalHolidaysPreference = null;

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageScrollStateChanged(int i) {
    }

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageScrolled(int i, float f, int i2) {
    }

    public static HintPromoteFragment newInstance() {
        return new HintPromoteFragment();
    }

    public int getDisplayWidth() {
        return this.mDispalyWidth;
    }

    public int getDisplayHeight() {
        return this.mDisplayHeight;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.isRTL = CalendarApplication.isR2L(getResources());
        if (LinkedInUtils.isLinkedInEnabled(getActivity())) {
            this.titles = getResources().getStringArray(R.array.hint_promotions_title);
            this.ids = getResources().obtainTypedArray(R.array.hint_promotions_ids);
            this.descriptions = getResources().getStringArray(R.array.hint_promotions_descriptions);
            this.actions = getResources().getStringArray(R.array.hint_promotions_actions);
        } else {
            this.titles = getResources().getStringArray(R.array.hint_promotions_title_without_linkedin);
            this.ids = getResources().obtainTypedArray(R.array.hint_promotions_ids_without_linkedin);
            this.descriptions = getResources().getStringArray(R.array.hint_promotions_descriptions_without_linkedin);
            this.actions = getResources().getStringArray(R.array.hint_promotions_actions_without_linkedin);
        }
        int length = this.titles.length;
        this.pagerSize = length;
        this.pages = new HintPromotePagerItemFragment[length];
        Display defaultDisplay = getActivity().getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        this.mDispalyWidth = point.x;
        this.mDisplayHeight = point.y;
        if (bundle != null) {
            this.currentPage = bundle.getInt(KEY_PAGE, 0);
        }
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(KEY_PAGE, this.currentPage);
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View viewInflate = layoutInflater.inflate(R.layout.hint_promote_fragment, viewGroup, false);
        viewInflate.findViewById(R.id.hint_promote_switcher_container).setBackgroundColor(getActivity().getColor(R.color.drawer_background_color));
        ViewPager viewPager = (ViewPager) viewInflate.findViewById(R.id.hint_promote_pager);
        this.pager = viewPager;
        viewPager.setBackgroundColor(getActivity().getColor(R.color.drawer_background_color));
        this.pager.setLayoutDirection(this.isRTL ? 1 : 0);
        this.pager.setAdapter(new FragmentsPagerAdapter(getChildFragmentManager()) { // from class: com.sonymobile.calendar.HintPromoteFragment.1
            @Override // com.sonymobile.calendar.agendapager.FragmentsPagerAdapter
            public Fragment getItem(int i) {
                HintPromotePagerItemFragment hintPromotePagerItemFragmentNewInstance = HintPromotePagerItemFragment.newInstance(HintPromoteFragment.this.ids.getResourceId(HintPromoteFragment.this.isRTL ? (HintPromoteFragment.this.pagerSize - 1) - i : i, 0), HintPromoteFragment.this.isRTL ? (HintPromoteFragment.this.pagerSize - 1) - i : i, HintPromoteFragment.this.titles[HintPromoteFragment.this.isRTL ? (HintPromoteFragment.this.pagerSize - 1) - i : i], HintPromoteFragment.this.descriptions[HintPromoteFragment.this.isRTL ? (HintPromoteFragment.this.pagerSize - 1) - i : i], HintPromoteFragment.this.actions[HintPromoteFragment.this.isRTL ? (HintPromoteFragment.this.pagerSize - 1) - i : i]);
                HintPromoteFragment.this.pages[i] = hintPromotePagerItemFragmentNewInstance;
                return hintPromotePagerItemFragmentNewInstance;
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public int getCount() {
                return HintPromoteFragment.this.pagerSize;
            }
        });
        this.currentPage = this.isRTL ? this.pagerSize - 1 : 0;
        this.pager.setOnPageChangeListener(this);
        this.switcherText = (TextView) viewInflate.findViewById(R.id.hint_promote_switcher_date_label);
        this.prevButton = viewInflate.findViewById(R.id.hint_promote_switcher_prev_btn);
        this.nextButton = viewInflate.findViewById(R.id.hint_promote_switcher_next_btn);
        this.leftImage = (ImageView) this.prevButton.findViewById(R.id.left_btn_image);
        this.rightImage = (ImageView) this.nextButton.findViewById(R.id.right_btn_image);
        this.prevButton.setOnClickListener(this);
        this.nextButton.setOnClickListener(this);
        TextView textView = (TextView) viewInflate.findViewById(R.id.hint_promote_switcher_next_label);
        this.nextLabel = textView;
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.calendar_primary_text_color));
        return viewInflate;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int currentItem = this.pager.getCurrentItem();
        FragmentActivity activity = getActivity();
        if ((view.equals(this.prevButton) && currentItem == 0 && !this.isRTL) || (view.equals(this.prevButton) && currentItem == this.pagerSize - 1 && this.isRTL)) {
            activity.finish();
            return;
        }
        if ((currentItem == this.pagerSize - 1 && view.equals(this.nextButton) && !this.isRTL) || (currentItem == 0 && view.equals(this.nextButton) && this.isRTL)) {
            HintPromoteUtils.removeHintAndPromoteFromListView(activity);
            activity.finish();
            return;
        }
        if ((view.equals(this.prevButton) && !this.isRTL) || (view.equals(this.nextButton) && this.isRTL)) {
            currentItem--;
        } else if ((view.equals(this.nextButton) && !this.isRTL) || (view.equals(this.prevButton) && this.isRTL)) {
            currentItem++;
        }
        this.pager.setCurrentItem(currentItem, true);
        updateSwitcher();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.pager.setCurrentItem(this.currentPage);
        updateSwitcher();
        if (this.isRTL) {
            this.leftImage.setScaleX(-1.0f);
            this.rightImage.setScaleX(-1.0f);
        }
    }

    private void updateSwitcher() {
        this.switcherText.setText(getString(R.string.step) + (!this.isRTL ? this.currentPage + 1 : this.pagerSize - this.currentPage) + "/" + this.pagerSize);
        if (this.currentPage == (!this.isRTL ? this.pagerSize - 1 : 0)) {
            this.nextLabel.setText(R.string.hint_finish);
        } else {
            this.nextLabel.setText(R.string.hint_next);
        }
    }

    @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
    public void onPageSelected(int i) {
        this.currentPage = i;
        updateSwitcher();
        HintPromotePagerItemFragment[] hintPromotePagerItemFragmentArr = this.pages;
        int i2 = this.currentPage;
        if (hintPromotePagerItemFragmentArr[i2] != null) {
            hintPromotePagerItemFragmentArr[i2].pageSelected();
        }
    }

    public void onActionClick(int i) {
        if (i == R.id.holidays_hint_promo_id) {
            showNationalHolidaysSelectionDialog();
        } else {
            if (i != R.id.linkedin_hint_promo_id) {
                return;
            }
            LinkedInUtils.startSyncWithLinkedInActivity(getActivity(), LinkedInUtils.LinkedInActivationEntrypoint.HINT_AND_PROMOTE);
        }
    }

    public void onActionButtonInitialized(Button button, int i) {
        if (i == R.id.linkedin_hint_promo_id) {
            button.setEnabled(!LinkedInUtils.isLinkedInSyncValid(getActivity()));
        }
    }

    private void showNationalHolidaysSelectionDialog() {
        SortedMultiSelectPreference sortedMultiSelectPreference = new SortedMultiSelectPreference(getActivity());
        this.mNationalHolidaysPreference = sortedMultiSelectPreference;
        sortedMultiSelectPreference.setDialogTitle(R.string.preferences_national_holidays_dialog);
        this.mNationalHolidaysPreference.setEntries(getResources().getStringArray(R.array.preferences_national_holidays_labels));
        this.mNationalHolidaysPreference.setEntryValues(getResources().getStringArray(R.array.preferences_national_holidays_values));
        this.mNationalHolidaysPreference.setKey("preferences_national_holidays");
        this.mNationalHolidaysPreference.setTitle(R.string.preferences_national_holidays_title);
        this.mNationalHolidaysPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.sonymobile.calendar.HintPromoteFragment.2
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                if (preference == HintPromoteFragment.this.mNationalHolidaysPreference) {
                    HintPromoteFragment.this.mNationalHolidaysPreference.setValues((HashSet) obj);
                    HintPromoteFragment hintPromoteFragment = HintPromoteFragment.this;
                    hintPromoteFragment.updateNationalHolidaysSummary(hintPromoteFragment.mNationalHolidaysPreference);
                    Set<String> stringSet = HintPromoteFragment.this.getActivity().getSharedPreferences(HintPromoteFragment.HOLIDAY_PREFERENCES, 0).getStringSet("preferences_national_holidays", null);
                    if (stringSet != null && stringSet.size() != 0) {
                        HintPromoteFragment.this.mNationalHolidaysPreference.setValues(stringSet);
                        HintPromoteFragment.this.mNationalHolidaysPreference.setCheckedItems();
                        HintPromoteFragment.this.pages[HintPromoteFragment.this.currentPage].setDescription(HintPromoteFragment.this.pages[HintPromoteFragment.this.currentPage].getDescription() + HintPromoteFragment.this.getEntryLabels(stringSet));
                    } else {
                        HintPromoteFragment.this.pages[HintPromoteFragment.this.currentPage].setDescription(HintPromoteFragment.this.pages[HintPromoteFragment.this.currentPage].getDescription().split(HintPromoteFragment.NEWLINES)[0]);
                    }
                }
                return false;
            }
        });
        updateNationalHolidaysSummary(this.mNationalHolidaysPreference);
        PreferenceManager preferenceManager = getPreferenceManager();
        Set<String> stringSet = getActivity().getSharedPreferences(HOLIDAY_PREFERENCES, 0).getStringSet("preferences_national_holidays", null);
        if (stringSet != null && stringSet.size() != 0) {
            this.mNationalHolidaysPreference.setValues(stringSet);
            this.mNationalHolidaysPreference.setCheckedItems();
            this.pages[this.currentPage].setDescription(this.pages[this.currentPage].getDescription() + getEntryLabels(stringSet));
        } else {
            HintPromotePagerItemFragment[] hintPromotePagerItemFragmentArr = this.pages;
            int i = this.currentPage;
            hintPromotePagerItemFragmentArr[i].setDescription(hintPromotePagerItemFragmentArr[i].getDescription().split(NEWLINES)[0]);
        }
        PreferenceScreen preferenceScreenCreatePreferenceScreen = preferenceManager.createPreferenceScreen(getContext());
        ((Activity) preferenceScreenCreatePreferenceScreen.getContext()).setTheme(R.style.CalendarSettingsTheme);
        preferenceScreenCreatePreferenceScreen.addPreference(this.mNationalHolidaysPreference);
        setPreferenceScreen(preferenceScreenCreatePreferenceScreen);
        this.mNationalHolidaysPreference.show();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        closePreferenceDialogIfNeeded();
    }

    public String getEntryLabels(Set<String> set) {
        String[] stringArray = getResources().getStringArray(R.array.preferences_national_holidays_labels);
        String[] stringArray2 = getResources().getStringArray(R.array.preferences_national_holidays_values);
        StringBuilder sb = new StringBuilder();
        String[] strArr = (String[]) set.toArray(new String[set.size()]);
        for (int i = 0; i < strArr.length; i++) {
            String str = strArr[i];
            for (int i2 = 0; i2 < stringArray2.length; i2++) {
                if (str.compareTo(stringArray2[i2]) == 0) {
                    if (i == strArr.length - 1 && strArr.length >= 1) {
                        sb.append(strArr.length == 1 ? "" : new StringBuilder(getString(R.string.and)).insert(0, ' ').append(' ').toString()).append(stringArray[i2]);
                        if (!Locale.getDefault().getLanguage().equals(Locale.JAPAN.getLanguage())) {
                            sb.append('.');
                            break;
                        }
                        break;
                    }
                    if (i == strArr.length - 2) {
                        sb.append(stringArray[i2]);
                        break;
                    }
                    sb.append(stringArray[i2]).append(", ");
                    break;
                }
            }
        }
        return sb.toString();
    }

    protected void updateNationalHolidaysSummary(MultiSelectListPreference multiSelectListPreference) {
        HashSet<String> hashSet = new HashSet(multiSelectListPreference.getValues());
        CharSequence[] entries = multiSelectListPreference.getEntries();
        StringBuilder sb = new StringBuilder();
        for (String str : hashSet) {
            if (multiSelectListPreference.findIndexOfValue(str) != -1) {
                sb.append(entries[multiSelectListPreference.findIndexOfValue(str)]);
                sb.append(", ");
            }
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        multiSelectListPreference.setSummary(sb);
    }

    private void closePreferenceDialogIfNeeded() {
        Fragment fragmentFindFragmentByTag = getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG);
        if (fragmentFindFragmentByTag != null) {
            ((PreferenceDialogFragmentCompat) fragmentFindFragmentByTag).dismiss();
        }
    }
}
