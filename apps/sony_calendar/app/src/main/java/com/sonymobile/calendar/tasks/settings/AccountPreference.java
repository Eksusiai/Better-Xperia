package com.sonymobile.calendar.tasks.settings;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.sonyericsson.calendar.util.CalendarColorService;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.tasks.model.TaskAccount;
import com.sonymobile.calendar.utils.UiUtils;

/* JADX INFO: loaded from: classes2.dex */
public class AccountPreference extends Preference {
    private TaskAccount mAccount;
    private Context mContext;
    public Switch mSwitchBox;

    public AccountPreference(Context context, AttributeSet attributeSet, TaskAccount taskAccount) {
        super(context, attributeSet);
        setLayoutResource(R.layout.tasks_settings_account_item);
        this.mAccount = taskAccount;
        this.mContext = context;
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        Drawable drawable = ((ImageView) preferenceViewHolder.findViewById(R.id.color)).getDrawable();
        if (this.mAccount.accountColor != 0) {
            drawable.setColorFilter(UiUtils.getDisplayColorFromColor(this.mAccount.accountColor), PorterDuff.Mode.MULTIPLY);
        } else {
            CalendarColorService.getInstance().requestColorByTaskAccountId(this.mContext, new ColorServiceResultHandler(drawable), 1, this.mAccount.id, false, false);
        }
        Switch r11 = (Switch) preferenceViewHolder.findViewById(R.id.switchBox);
        this.mSwitchBox = r11;
        r11.setChecked(this.mAccount.visibility == 1);
        this.mSwitchBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.sonymobile.calendar.tasks.settings.AccountPreference.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                TaskAccountManager.getInstance().updateVisibility(AccountPreference.this.mContext, z, AccountPreference.this.mAccount.id);
                AccountPreference.this.mAccount.visibility = z ? 1 : 0;
            }
        });
    }

    private static class ColorServiceResultHandler implements IAsyncServiceResultHandler {
        private Drawable drawable;

        public ColorServiceResultHandler(Drawable drawable) {
            this.drawable = drawable;
        }

        @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
        public void onResult(Object obj, Object obj2) {
            if (((Integer) obj2).intValue() == 1) {
                this.drawable.setColorFilter(UiUtils.getDisplayColorFromColor(((Integer) obj).intValue()), PorterDuff.Mode.MULTIPLY);
            }
        }
    }
}
