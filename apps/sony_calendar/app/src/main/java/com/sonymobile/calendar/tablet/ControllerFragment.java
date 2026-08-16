package com.sonymobile.calendar.tablet;

import android.text.format.Time;
import androidx.fragment.app.Fragment;
import com.sonymobile.calendar.Navigator;

/* JADX INFO: loaded from: classes2.dex */
public class ControllerFragment extends Fragment implements Navigator {
    @Override // com.sonymobile.calendar.Navigator
    public CharSequence getDateString() {
        return null;
    }

    @Override // com.sonymobile.calendar.Navigator
    public long getSelectedTimeInMillis() {
        return 0L;
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goTo(Time time, boolean z) {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToNext(float f) {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToPrevious(float f) {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void goToToday() {
    }

    @Override // com.sonymobile.calendar.Navigator
    public void updateActionBar(Time time) {
    }
}
