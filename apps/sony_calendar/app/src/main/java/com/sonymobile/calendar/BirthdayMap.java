package com.sonymobile.calendar;

import android.util.Pair;
import com.sonymobile.calendar.birthday.ContactBirthday;
import java.util.ArrayList;
import java.util.HashMap;

/* JADX INFO: loaded from: classes2.dex */
public class BirthdayMap extends HashMap<Pair<Integer, Integer>, ArrayList<ContactBirthday>> {
    private static final long serialVersionUID = 1;

    public static ArrayList<ContactBirthday> getNewBirthdayList() {
        return new ArrayList<>();
    }
}
