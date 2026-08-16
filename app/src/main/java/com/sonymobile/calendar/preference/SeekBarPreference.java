package com.sonymobile.calendar.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.SeekBar;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.sonymobile.calendar.R;

/* JADX INFO: loaded from: classes2.dex */
public class SeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener {
    private static final int PROGRESS_MAX = 100;
    private static final int PROGRESS_MIN = 0;
    private int mProgress;
    private boolean mTrackingTouch;

    public SeekBarPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R.layout.seekbar_preference_view);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        SeekBar seekBar = (SeekBar) preferenceViewHolder.findViewById(R.id.seekbar);
        seekBar.setProgress(this.mProgress);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override // androidx.preference.Preference
    protected void onSetInitialValue(boolean z, Object obj) {
        setProgress(z ? getPersistedInt(this.mProgress) : ((Integer) obj).intValue());
    }

    @Override // androidx.preference.Preference
    protected Object onGetDefaultValue(TypedArray typedArray, int i) {
        return Integer.valueOf(typedArray.getInt(i, 0));
    }

    public void setProgress(int i) {
        setProgress(i, true);
    }

    private void setProgress(int i, boolean z) {
        if (i > 100) {
            i = 100;
        } else if (i < 0) {
            i = 0;
        }
        if (i != this.mProgress) {
            this.mProgress = i;
            persistInt(i);
            if (z) {
                notifyChanged();
            }
        }
    }

    private void syncProgress(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        if (progress != this.mProgress) {
            if (callChangeListener(Integer.valueOf(progress))) {
                setProgress(progress, false);
            } else {
                seekBar.setProgress(this.mProgress);
            }
        }
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (!z || this.mTrackingTouch) {
            return;
        }
        syncProgress(seekBar);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
        this.mTrackingTouch = true;
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        this.mTrackingTouch = false;
        if (seekBar.getProgress() != this.mProgress) {
            syncProgress(seekBar);
        }
    }
}
