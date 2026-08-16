package com.sonymobile.calendar.permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.sonymobile.calendar.GeneralPreferences;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.ShowPersonalInfoActivity;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.utils.UiUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class PermissionAcceptanceActivity extends Activity {
    public static final String ACTION_REQUEST_PERMISSION = "com.sonymobile.calendar.REQUEST_PERMISSIONS";
    public static final String DESCRIPTION_KEY = "keyDescription";
    public static final String DESCRIPTION_LINK_KEY = "keyDescriptionLink";
    public static final String KEY_REMEMBER_MY_SELECTION = "preference_RememberSelection";
    public static final int MSG_CHECK_REMEMBER = 1001;
    public static final String PERMISSIONS_ITEM_KEY = "keyPermissionItem";
    public static final String PERMISSIONS_KEY = "keyPermission";
    private static final Map<String, Integer> PERMISSION_MAP;
    public static final String PERMISSION_TITLE_KEY = "keyPermissionTitle";
    private static final Map<Integer, String> PREFERENCE_MAP;
    private static final String SELECTED_ITEMS_KEY = "keySelectedItems";
    public static final String TAG = "PermissionAcceptanceActivity";
    private TextView mDescriptionText;
    private Handler mHandler;
    private TextView mLinkDescriptionText;
    private ListView mPermissionList;
    private TextView mPermissionListTitle;
    private CheckBox mRememberMySelection;

    @Override // android.app.Activity
    public void onBackPressed() {
    }

    static {
        HashMap map = new HashMap();
        PERMISSION_MAP = map;
        Integer numValueOf = Integer.valueOf(R.string.read_contact_permission);
        map.put("android.permission.READ_CONTACTS", numValueOf);
        Integer numValueOf2 = Integer.valueOf(R.string.retrieve_location_info_permission);
        map.put("android.permission.ACCESS_FINE_LOCATION", numValueOf2);
        map.put("android.permission.ACCESS_COARSE_LOCATION", numValueOf2);
        Integer numValueOf3 = Integer.valueOf(R.string.use_wifi_mobile_data_permission);
        map.put("android.permission.INTERNET", numValueOf3);
        Integer numValueOf4 = Integer.valueOf(R.string.access_installed_package_list_permission);
        map.put("android.permission.QUERY_ALL_PACKAGES", numValueOf4);
        HashMap map2 = new HashMap();
        PREFERENCE_MAP = map2;
        map2.put(numValueOf, GeneralPreferences.KEY_READ_CONTACTS);
        map2.put(numValueOf2, GeneralPreferences.KEY_RETRIEVE_LOCATION_INFO);
        map2.put(numValueOf2, GeneralPreferences.KEY_RETRIEVE_LOCATION_INFO);
        map2.put(numValueOf3, GeneralPreferences.KEY_USE_WIFI_AND_MOBILE_DATA);
        map2.put(numValueOf4, GeneralPreferences.KEY_ACCESS_PACKAGE_LIST);
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.permisson_acceptence_activity_dialog);
        String action = getIntent().getAction();
        this.mPermissionList = (ListView) findViewById(R.id.permission_list);
        this.mRememberMySelection = (CheckBox) findViewById(R.id.remember_selection);
        this.mDescriptionText = (TextView) findViewById(R.id.description);
        this.mLinkDescriptionText = (TextView) findViewById(R.id.dialog_privacy_link);
        this.mPermissionListTitle = (TextView) findViewById(R.id.permission_list_title);
        this.mHandler = new Handler(getMainLooper()) { // from class: com.sonymobile.calendar.permissions.PermissionAcceptanceActivity.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                super.handleMessage(message);
                if (message.what == 1001) {
                    PermissionAcceptanceActivity.this.mRememberMySelection.setChecked(true);
                }
            }
        };
        if (ACTION_REQUEST_PERMISSION.equals(action)) {
            checkPermissionsAndInitViewIfNeeded();
            return;
        }
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (bundle != null && bundle.containsKey(SELECTED_ITEMS_KEY)) {
            arrayList = bundle.getIntegerArrayList(SELECTED_ITEMS_KEY);
        } else {
            getSelectedItemsFromSp(arrayList);
        }
        initViewChina(arrayList);
    }

    private void getSelectedItemsFromSp(ArrayList<Integer> arrayList) {
        for (Integer num : PREFERENCE_MAP.keySet()) {
            if (Utils.getSharedPreference((Context) this, PREFERENCE_MAP.get(num), false)) {
                arrayList.add(num);
            }
        }
    }

    @Override // android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (ACTION_REQUEST_PERMISSION.equals(getIntent().getAction())) {
            return;
        }
        bundle.putIntegerArrayList(SELECTED_ITEMS_KEY, ((PermissionAdapterChina) this.mPermissionList.getAdapter()).getSelectedItems());
    }

    private void checkPermissionsAndInitViewIfNeeded() {
        this.mRememberMySelection.setVisibility(8);
        this.mPermissionListTitle.setVisibility(8);
        Parcelable[] parcelableArray = getIntent().getExtras().getParcelableArray(PERMISSIONS_ITEM_KEY);
        if (parcelableArray != null && parcelableArray.length != 0) {
            initView(parcelableArray);
        } else {
            finish();
        }
    }

    private void initView(Parcelable[] parcelableArr) {
        ((Button) findViewById(R.id.ok_button)).setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.permissions.PermissionAcceptanceActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PermissionAcceptanceActivity.this.finish();
            }
        });
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setText(R.string.permissions_description_dialog_title);
        textView.setTextColor(UiUtils.getPrimaryColor(this));
        this.mDescriptionText.setText(R.string.permissions_description_dialog_text);
        this.mPermissionList.setAdapter((ListAdapter) new PermissionAdapter(this, parcelableArr));
    }

    private CharSequence createClickableText(CharSequence charSequence) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        for (URLSpan uRLSpan : (URLSpan[]) spannableStringBuilder.getSpans(0, charSequence.length(), URLSpan.class)) {
            ClickableSpan clickableSpan = new ClickableSpan() { // from class: com.sonymobile.calendar.permissions.PermissionAcceptanceActivity.3
                @Override // android.text.style.ClickableSpan
                public void onClick(View view) {
                    PermissionAcceptanceActivity.this.startActivity(new Intent(PermissionAcceptanceActivity.this.getApplicationContext(), (Class<?>) ShowPersonalInfoActivity.class));
                }

                @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
                public void updateDrawState(TextPaint textPaint) {
                    super.updateDrawState(textPaint);
                    textPaint.setUnderlineText(false);
                }
            };
            int spanStart = spannableStringBuilder.getSpanStart(uRLSpan);
            int spanEnd = spannableStringBuilder.getSpanEnd(uRLSpan);
            spannableStringBuilder.removeSpan(uRLSpan);
            spannableStringBuilder.setSpan(clickableSpan, spanStart, spanEnd, 33);
        }
        return spannableStringBuilder;
    }

    private void initViewChina(ArrayList<Integer> arrayList) {
        String string;
        String[] strArr;
        CharSequence text;
        Bundle extras = getIntent().getExtras();
        String str = null;
        if (extras != null) {
            String[] stringArray = extras.getStringArray(PERMISSIONS_KEY);
            String string2 = getString(extras.getInt(DESCRIPTION_KEY));
            text = getText(extras.getInt(DESCRIPTION_LINK_KEY));
            string = getString(extras.getInt(PERMISSION_TITLE_KEY));
            strArr = stringArray;
            str = string2;
        } else {
            string = null;
            strArr = null;
            text = null;
        }
        ((Button) findViewById(R.id.ok_button)).setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.permissions.PermissionAcceptanceActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PermissionAdapterChina permissionAdapterChina = (PermissionAdapterChina) PermissionAcceptanceActivity.this.mPermissionList.getAdapter();
                ArrayList<Integer> selectedItems = permissionAdapterChina.getSelectedItems();
                PermissionAcceptanceActivity permissionAcceptanceActivity = PermissionAcceptanceActivity.this;
                Utils.setSharedPreference(permissionAcceptanceActivity, PermissionAcceptanceActivity.KEY_REMEMBER_MY_SELECTION, permissionAcceptanceActivity.mRememberMySelection.isChecked());
                for (int i = 0; i < permissionAdapterChina.getCount(); i++) {
                    Integer item = permissionAdapterChina.getItem(i);
                    Utils.setSharedPreference(PermissionAcceptanceActivity.this, (String) PermissionAcceptanceActivity.PREFERENCE_MAP.get(item), selectedItems.contains(item));
                }
                PermissionAcceptanceActivity.this.setResult(-1);
                PermissionAcceptanceActivity.this.finish();
            }
        });
        if (!TextUtils.isEmpty(str)) {
            this.mDescriptionText.setText(str);
        }
        if (!TextUtils.isEmpty(text)) {
            this.mLinkDescriptionText.setText(createClickableText(text));
            this.mLinkDescriptionText.setMovementMethod(LinkMovementMethod.getInstance());
        }
        if (!TextUtils.isEmpty(string)) {
            this.mPermissionListTitle.setText(string);
            this.mPermissionListTitle.setTextColor(getResources().getColor(R.color.calendar_primary_text_color));
        }
        if (strArr != null) {
            Integer[] numArr = new Integer[strArr.length];
            for (int i = 0; i < strArr.length; i++) {
                numArr[i] = PERMISSION_MAP.get(strArr[i]);
            }
            PermissionAdapterChina permissionAdapterChina = new PermissionAdapterChina(this, numArr, this.mHandler);
            this.mPermissionList.setAdapter((ListAdapter) permissionAdapterChina);
            setListViewHeightBasedOnChildren(this.mPermissionList);
            if (arrayList != null) {
                permissionAdapterChina.setSelectedItems(arrayList);
            }
        }
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) {
            return;
        }
        int measuredHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View view = adapter.getView(i, null, listView);
            view.measure(0, 0);
            measuredHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = measuredHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(layoutParams);
    }

    private static class PermissionAdapterChina extends ArrayAdapter<Integer> {
        private Handler mHandler;
        private ArrayList<Integer> mSelectedItems;

        public PermissionAdapterChina(Context context, Integer[] numArr, Handler handler) {
            super(context, R.layout.permission_list_item, numArr);
            ArrayList<Integer> arrayList = new ArrayList<>();
            this.mSelectedItems = arrayList;
            Collections.addAll(arrayList, numArr);
            this.mHandler = handler;
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            final CheckedTextView checkedTextView = (CheckedTextView) super.getView(i, view, viewGroup);
            final Integer item = getItem(i);
            checkedTextView.setText(getContext().getString(item.intValue()));
            checkedTextView.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.permissions.PermissionAcceptanceActivity.PermissionAdapterChina.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    CheckedTextView checkedTextView2 = checkedTextView;
                    checkedTextView2.setChecked(!checkedTextView2.isChecked());
                    if (checkedTextView.isChecked()) {
                        PermissionAdapterChina.this.mSelectedItems.add(item);
                    } else {
                        PermissionAdapterChina.this.mSelectedItems.remove(item);
                    }
                    PermissionAdapterChina.this.notifyDataSetChanged();
                    if (PermissionAdapterChina.this.mSelectedItems.size() == PermissionAdapterChina.this.getCount()) {
                        PermissionAdapterChina.this.mHandler.sendEmptyMessage(1001);
                    }
                }
            });
            checkedTextView.setChecked(this.mSelectedItems.contains(item));
            return checkedTextView;
        }

        public ArrayList<Integer> getSelectedItems() {
            return this.mSelectedItems;
        }

        public void setSelectedItems(ArrayList<Integer> arrayList) {
            this.mSelectedItems = arrayList;
        }
    }

    private static class PermissionAdapter extends ArrayAdapter<Parcelable> {
        private static final int DEFAULT_LAYOUT_ID = -1;

        public PermissionAdapter(Context context, Parcelable[] parcelableArr) {
            super(context, -1, parcelableArr);
        }

        @Override // android.widget.ArrayAdapter, android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = View.inflate(getContext(), R.layout.permission_item, null);
                view.setTag(new ViewHolder(view));
            }
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            PermissionItem permissionItem = (PermissionItem) getItem(i);
            viewHolder.mDescription.setText(permissionItem.getDescription(getContext()));
            viewHolder.mLabel.setText(permissionItem.getLabel(getContext()));
            viewHolder.mIcon.setImageResource(permissionItem.getIconId());
            return view;
        }

        private static class ViewHolder {
            TextView mDescription;
            ImageView mIcon;
            TextView mLabel;

            public ViewHolder(View view) {
                this.mLabel = (TextView) view.findViewById(R.id.label);
                this.mDescription = (TextView) view.findViewById(R.id.description);
                this.mIcon = (ImageView) view.findViewById(R.id.permission_icon);
            }
        }
    }

    public static Intent getPermissionAcceptanceIntent(Context context, String[] strArr, int i, int i2, int i3) {
        Intent intent = new Intent(context, (Class<?>) PermissionAcceptanceActivity.class);
        intent.putExtra(PERMISSIONS_KEY, strArr);
        intent.putExtra(PERMISSION_TITLE_KEY, i2);
        intent.putExtra(DESCRIPTION_KEY, i);
        intent.putExtra(DESCRIPTION_LINK_KEY, i3);
        return intent;
    }
}
