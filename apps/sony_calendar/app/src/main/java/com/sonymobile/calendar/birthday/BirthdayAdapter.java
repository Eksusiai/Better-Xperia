package com.sonymobile.calendar.birthday;
import com.sonymobile.calendar.SafeTime;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.sonyericsson.calendar.util.IAsyncServiceResultHandler;
import com.sonymobile.calendar.PhoneNumber;
import com.sonymobile.calendar.R;
import com.sonymobile.calendar.Utils;
import com.sonymobile.calendar.utils.PackageUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class BirthdayAdapter extends ArrayAdapter<ContactBirthday> {
    private static final float SEPARATOR_ALPHA = 0.3f;
    private static final String SKETCH_INTENT = "com.sonymobile.notes.NEW_SKETCH";
    private static final String SMS_LABEL = "sms:";
    private static final String SMS_SCHEME = "sms";
    private static final String TELEPHONE_LABEL = "tel:";
    private ArrayList<ContactBirthday> birthdayList;
    private Context context;
    private final Drawable mDefaultPhoneIcon;
    private final Drawable mDefaultSmsIcon;
    private final Drawable mSketchIcon;
    private OnPhoneNumberPickedListener onPhoneNumberPickedListener;

    static class ViewHolder {
        TextView age;
        ImageView interactionGroupSeperator;
        TextView name;
        View phoneGroup;
        ImageView phoneIcon;
        ImageView photo;
        ProgressBar progressBar;
        View sketchGroup;
        ImageView sketchIcon;
        ImageView sketchSeperator;
        View smsGroup;
        ImageView smsIcon;
        ImageView smsSeperator;

        ViewHolder() {
        }
    }

    public BirthdayAdapter(Context context, int i, ArrayList<ContactBirthday> arrayList) {
        super(context, i, arrayList);
        this.onPhoneNumberPickedListener = new OnPhoneNumberPickedListener() { // from class: com.sonymobile.calendar.birthday.BirthdayAdapter.6
            @Override // com.sonymobile.calendar.birthday.OnPhoneNumberPickedListener
            public void onNumberPicked(String str, String str2) {
                if (str2.equals(BirthdayAdapter.TELEPHONE_LABEL)) {
                    Intent intent = new Intent("android.intent.action.DIAL");
                    intent.setData(Uri.parse(BirthdayAdapter.TELEPHONE_LABEL + str));
                    BirthdayAdapter.this.context.startActivity(intent);
                } else {
                    Intent intent2 = new Intent("android.intent.action.VIEW");
                    intent2.setData(Uri.fromParts(BirthdayAdapter.SMS_SCHEME, str, null));
                    BirthdayAdapter.this.context.startActivity(intent2);
                }
            }
        };
        this.birthdayList = arrayList;
        this.context = context;
        this.mDefaultPhoneIcon = PackageUtils.resolveAppIcon(context, PackageUtils.AppType.DIALER);
        this.mDefaultSmsIcon = PackageUtils.resolveAppIcon(context, PackageUtils.AppType.SMS);
        if (isSketchEnabled()) {
            this.mSketchIcon = PackageUtils.resolveAppIcon(context, PackageUtils.AppType.SKETCH);
        } else {
            this.mSketchIcon = null;
        }
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = View.inflate(this.context, R.layout.birthday_item, null);
        }
        ViewHolder viewHolder = view.getTag() instanceof ViewHolder ? (ViewHolder) view.getTag() : null;
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            view.setTag(viewHolder);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.age = (TextView) view.findViewById(R.id.age);
            viewHolder.photo = (ImageView) view.findViewById(R.id.photo);
            viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            viewHolder.interactionGroupSeperator = (ImageView) view.findViewById(R.id.interactionGroupSeperator);
            viewHolder.sketchSeperator = (ImageView) view.findViewById(R.id.sketchSeperator);
            viewHolder.phoneGroup = view.findViewById(R.id.phoneGroup);
            viewHolder.smsSeperator = (ImageView) view.findViewById(R.id.smsSeperator);
            viewHolder.smsGroup = view.findViewById(R.id.smsGroup);
            viewHolder.sketchGroup = view.findViewById(R.id.sketchGroup);
            viewHolder.smsIcon = (ImageView) view.findViewById(R.id.sendSMS);
            viewHolder.phoneIcon = (ImageView) view.findViewById(R.id.phoneCall);
            viewHolder.sketchIcon = (ImageView) view.findViewById(R.id.sketch);
            viewHolder.smsIcon.setImageDrawable(this.mDefaultSmsIcon);
            viewHolder.phoneIcon.setImageDrawable(this.mDefaultPhoneIcon);
            viewHolder.sketchIcon.setImageDrawable(this.mSketchIcon);
        }
        ContactBirthday contactBirthday = this.birthdayList.get(i);
        viewHolder.interactionGroupSeperator.setAlpha(0.3f);
        viewHolder.name.setText(contactBirthday.name);
        viewHolder.sketchGroup.setVisibility(8);
        viewHolder.sketchSeperator.setVisibility(8);
        if (isSketchEnabled()) {
            viewHolder.sketchGroup.setVisibility(0);
            viewHolder.sketchSeperator.setVisibility(0);
            viewHolder.sketchSeperator.setAlpha(0.3f);
            viewHolder.sketchGroup.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.birthday.BirthdayAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    BirthdayAdapter.this.context.startActivity(new Intent(BirthdayAdapter.SKETCH_INTENT));
                }
            });
        }
        viewHolder.photo.setVisibility(4);
        viewHolder.progressBar.setVisibility(0);
        setContactPhoto(viewHolder.photo, viewHolder.progressBar, contactBirthday.contactId);
        viewHolder.phoneGroup.setVisibility(8);
        viewHolder.smsGroup.setVisibility(8);
        viewHolder.smsSeperator.setVisibility(8);
        if (contactBirthday.hasPhoneNumber) {
            if (Utils.deviceCanPerformCall(this.context)) {
                viewHolder.phoneGroup.setVisibility(0);
                viewHolder.interactionGroupSeperator.setVisibility(0);
            }
            int i2 = showSmsGroup() ? 0 : 8;
            viewHolder.smsGroup.setVisibility(i2);
            viewHolder.smsSeperator.setVisibility(i2);
            if (i2 == 0) {
                viewHolder.smsSeperator.setAlpha(0.3f);
            }
            setPhoneNumbers(contactBirthday, viewHolder.phoneGroup, viewHolder.smsGroup);
        }
        viewHolder.age.setVisibility(8);
        if (contactBirthday.year > 0) {
            Time time = new SafeTime(Utils.getTimeZone(this.context, null));
            time.set(Utils.getDisplayTime());
            int age = contactBirthday.getAge(time);
            if (age >= 0) {
                viewHolder.age.setVisibility(0);
                viewHolder.age.setText(this.context.getResources().getQuantityString(R.plurals.birthday_age, age, Integer.valueOf(age)));
            }
        }
        return view;
    }

    private void setContactPhoto(ImageView imageView, ProgressBar progressBar, String str) {
        Bitmap photo = ContactPhotoService.getInstance().getPhoto(this.context, str);
        progressBar.setVisibility(8);
        imageView.setVisibility(0);
        imageView.setImageBitmap(photo);
    }

    private void setPhoneNumbers(final ContactBirthday contactBirthday, View view, View view2) {
        contactBirthday.primaryNumber = PhoneNumberService.getInstance().getPrimaryNumber(contactBirthday);
        contactBirthday.phoneNumbers = PhoneNumberService.getInstance().getAllNumbers(contactBirthday);
        if (contactBirthday.phoneNumbers.isEmpty()) {
            return;
        }
        view.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.birthday.BirthdayAdapter.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view3) {
                String str;
                if (TextUtils.isEmpty(contactBirthday.primaryNumber) && contactBirthday.phoneNumbers.size() != 1) {
                    BirthdayAdapter.this.showNumberPicker(contactBirthday.phoneNumbers, BirthdayAdapter.TELEPHONE_LABEL, contactBirthday.name);
                    return;
                }
                if (contactBirthday.primaryNumber.length() > 0) {
                    str = contactBirthday.primaryNumber;
                } else {
                    str = contactBirthday.phoneNumbers.get(0).number;
                }
                Intent intent = new Intent("android.intent.action.DIAL");
                intent.setData(Uri.parse(BirthdayAdapter.TELEPHONE_LABEL + str));
                BirthdayAdapter.this.context.startActivity(intent);
            }
        });
        view2.setOnClickListener(new View.OnClickListener() { // from class: com.sonymobile.calendar.birthday.BirthdayAdapter.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view3) {
                String str;
                if (TextUtils.isEmpty(contactBirthday.primaryNumber) && contactBirthday.phoneNumbers.size() != 1) {
                    BirthdayAdapter.this.showNumberPicker(contactBirthday.phoneNumbers, BirthdayAdapter.SMS_LABEL, contactBirthday.name);
                    return;
                }
                if (contactBirthday.primaryNumber.length() > 0) {
                    str = contactBirthday.primaryNumber;
                } else {
                    str = contactBirthday.phoneNumbers.get(0).number;
                }
                BirthdayAdapter.this.context.startActivity(new Intent("android.intent.action.SENDTO", Uri.fromParts(BirthdayAdapter.SMS_SCHEME, str, null)));
            }
        });
    }

    public void notifyDataSetChanged(ArrayList<ContactBirthday> arrayList) {
        this.birthdayList = arrayList;
        ContactPhotoService.getInstance().requestContactPhotos(this.context, arrayList, new IAsyncServiceResultHandler() { // from class: com.sonymobile.calendar.birthday.BirthdayAdapter.4
            @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
            public void onResult(Object obj, Object obj2) {
                BirthdayAdapter.this.notifyDataSetChanged();
            }
        });
        PhoneNumberService.getInstance().requestLoad(this.context, arrayList, new IAsyncServiceResultHandler() { // from class: com.sonymobile.calendar.birthday.BirthdayAdapter.5
            @Override // com.sonyericsson.calendar.util.IAsyncServiceResultHandler
            public void onResult(Object obj, Object obj2) {
                BirthdayAdapter.this.notifyDataSetChanged();
            }
        });
        notifyDataSetChanged();
    }

    private boolean isSketchEnabled() {
        if (!Utils.isPackageListAccessEnable(this.context)) {
            return false;
        }
        PackageManager packageManager = this.context.getPackageManager();
        try {
            return packageManager.getApplicationEnabledSetting(PackageUtils.SKETCH_PACKAGE_NAME) == 1 || packageManager.getApplicationEnabledSetting(PackageUtils.SKETCH_PACKAGE_NAME) == 0;
        } catch (IllegalArgumentException unused) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showNumberPicker(ArrayList<PhoneNumber> arrayList, String str, String str2) {
        PhoneNumberPicker phoneNumberPickerNewInstance = PhoneNumberPicker.newInstance(arrayList, str, str2);
        phoneNumberPickerNewInstance.setOnPhoneNumberClickedListener(this.onPhoneNumberPickedListener);
        phoneNumberPickerNewInstance.show(((AppCompatActivity) this.context).getSupportFragmentManager(), PhoneNumberPicker.TAG);
    }

    public void setDialogListener(Fragment fragment) {
        ((PhoneNumberPicker) fragment).setOnPhoneNumberClickedListener(this.onPhoneNumberPickedListener);
    }

    private boolean showSmsGroup() {
        return Utils.isIntentRecipientAvailable(this.context, new Intent("android.intent.action.SENDTO", Uri.fromParts(SMS_SCHEME, "+123121234567", null)));
    }
}
