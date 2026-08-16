package com.android.ex.chips;

import com.sonymobile.calendar.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.util.Rfc822Tokenizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MarginLayoutParamsCompat;

/* JADX INFO: loaded from: classes.dex */
public class DropdownChipLayouter {
    private int mAutocompleteDividerMarginStart;
    private final Context mContext;
    private ChipDeleteListener mDeleteListener;
    private final LayoutInflater mInflater;
    private Queries.Query mQuery;

    public enum AdapterType {
        BASE_RECIPIENT,
        RECIPIENT_ALTERNATES,
        SINGLE_RECIPIENT
    }

    public interface ChipDeleteListener {
        void onChipDelete();
    }

    protected int getDeleteResId() {
        return android.R.id.icon1;
    }

    protected int getDestinationResId() {
        return android.R.id.text1;
    }

    protected int getDestinationTypeResId() {
        return android.R.id.text2;
    }

    protected int getDisplayNameResId() {
        return android.R.id.title;
    }

    protected int getPhotoResId() {
        return android.R.id.icon;
    }

    public DropdownChipLayouter(LayoutInflater layoutInflater, Context context) {
        this.mInflater = layoutInflater;
        this.mContext = context;
        this.mAutocompleteDividerMarginStart = context.getResources().getDimensionPixelOffset(R.dimen.chip_wrapper_start_padding);
    }

    public void setQuery(Queries.Query query) {
        this.mQuery = query;
    }

    public void setDeleteListener(ChipDeleteListener chipDeleteListener) {
        this.mDeleteListener = chipDeleteListener;
    }

    public void setAutocompleteDividerMarginStart(int i) {
        this.mAutocompleteDividerMarginStart = i;
    }

    public View bindView(View view, ViewGroup viewGroup, RecipientEntry recipientEntry, int i, AdapterType adapterType, String str) {
        return bindView(view, viewGroup, recipientEntry, i, adapterType, str, null);
    }

    /* JADX WARN: Code duplicated, block: B:11:0x004c  */
    public View bindView(View view, ViewGroup viewGroup, RecipientEntry recipientEntry, int i, AdapterType adapterType, String str, StateListDrawable stateListDrawable) {
        boolean z = false;
        CharSequence[] styledResults = getStyledResults(str, recipientEntry.getDisplayName(), recipientEntry.getDestination());
        CharSequence charSequence = styledResults[0];
        CharSequence address = styledResults[1];
        CharSequence destinationType = getDestinationType(recipientEntry);
        View viewReuseOrInflateView = reuseOrInflateView(view, viewGroup, adapterType);
        ViewHolder viewHolder = new ViewHolder(viewReuseOrInflateView);
        int i2 = AnonymousClass2.$SwitchMap$com$android$ex$chips$DropdownChipLayouter$AdapterType[adapterType.ordinal()];
        if (i2 == 1) {
            if (TextUtils.isEmpty(charSequence) || TextUtils.equals(charSequence, address)) {
                charSequence = address;
                if (recipientEntry.isFirstLevel()) {
                    address = null;
                }
            }
            boolean zIsFirstLevel = recipientEntry.isFirstLevel();
            if (!zIsFirstLevel) {
                charSequence = null;
            }
            if (viewHolder.topDivider != null) {
                viewHolder.topDivider.setVisibility(i != 0 ? 8 : 0);
                MarginLayoutParamsCompat.setMarginStart((ViewGroup.MarginLayoutParams) viewHolder.topDivider.getLayoutParams(), this.mAutocompleteDividerMarginStart);
            }
            if (viewHolder.bottomDivider != null) {
                MarginLayoutParamsCompat.setMarginStart((ViewGroup.MarginLayoutParams) viewHolder.bottomDivider.getLayoutParams(), this.mAutocompleteDividerMarginStart);
            }
            z = zIsFirstLevel;
        } else if (i2 != 2) {
            if (i2 != 3) {
                z = true;
            } else {
                address = Rfc822Tokenizer.tokenize(recipientEntry.getDestination())[0].getAddress();
                z = true;
                destinationType = null;
            }
        } else if (i != 0) {
            charSequence = null;
        } else {
            z = true;
        }
        bindTextToView(charSequence, viewHolder.displayNameView);
        bindTextToView(address, viewHolder.destinationView);
        bindTextToView(destinationType, viewHolder.destinationTypeView);
        bindIconToView(z, recipientEntry, viewHolder.imageView, adapterType);
        bindDrawableToDeleteView(stateListDrawable, recipientEntry.getDisplayName(), viewHolder.deleteView);
        return viewReuseOrInflateView;
    }

    /* JADX INFO: renamed from: com.android.ex.chips.DropdownChipLayouter$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$android$ex$chips$DropdownChipLayouter$AdapterType;

        static {
            int[] iArr = new int[AdapterType.values().length];
            $SwitchMap$com$android$ex$chips$DropdownChipLayouter$AdapterType = iArr;
            try {
                iArr[AdapterType.BASE_RECIPIENT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$ex$chips$DropdownChipLayouter$AdapterType[AdapterType.RECIPIENT_ALTERNATES.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$ex$chips$DropdownChipLayouter$AdapterType[AdapterType.SINGLE_RECIPIENT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public View newView(AdapterType adapterType) {
        return this.mInflater.inflate(getItemLayoutResId(adapterType), (ViewGroup) null);
    }

    protected View reuseOrInflateView(View view, ViewGroup viewGroup, AdapterType adapterType) {
        int itemLayoutResId = getItemLayoutResId(adapterType);
        if (AnonymousClass2.$SwitchMap$com$android$ex$chips$DropdownChipLayouter$AdapterType[adapterType.ordinal()] == 3) {
            itemLayoutResId = getAlternateItemLayoutResId(adapterType);
        }
        return view != null ? view : this.mInflater.inflate(itemLayoutResId, viewGroup, false);
    }

    protected void bindTextToView(CharSequence charSequence, TextView textView) {
        if (textView == null) {
            return;
        }
        if (charSequence != null) {
            textView.setText(charSequence);
            textView.setVisibility(0);
        } else {
            textView.setVisibility(8);
        }
    }

    protected void bindIconToView(boolean z, RecipientEntry recipientEntry, ImageView imageView, AdapterType adapterType) {
        if (imageView == null) {
            return;
        }
        if (z) {
            int i = AnonymousClass2.$SwitchMap$com$android$ex$chips$DropdownChipLayouter$AdapterType[adapterType.ordinal()];
            if (i == 1) {
                byte[] photoBytes = recipientEntry.getPhotoBytes();
                if (photoBytes != null && photoBytes.length > 0) {
                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length));
                } else {
                    imageView.setImageResource(getDefaultPhotoResId());
                }
            } else if (i == 2) {
                Uri photoThumbnailUri = recipientEntry.getPhotoThumbnailUri();
                if (photoThumbnailUri != null) {
                    imageView.setImageURI(photoThumbnailUri);
                } else {
                    imageView.setImageResource(getDefaultPhotoResId());
                }
            }
            imageView.setVisibility(0);
            return;
        }
        imageView.setVisibility(8);
    }

    protected void bindDrawableToDeleteView(final StateListDrawable stateListDrawable, String str, ImageView imageView) {
        if (imageView == null) {
            return;
        }
        if (stateListDrawable == null) {
            imageView.setVisibility(8);
            return;
        }
        Resources resources = this.mContext.getResources();
        imageView.setImageDrawable(stateListDrawable);
        imageView.setContentDescription(resources.getString(R.string.dropdown_delete_button_desc, str));
        if (this.mDeleteListener != null) {
            imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.ex.chips.DropdownChipLayouter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (stateListDrawable.getCurrent() != null) {
                        DropdownChipLayouter.this.mDeleteListener.onChipDelete();
                    }
                }
            });
        }
    }

    protected CharSequence getDestinationType(RecipientEntry recipientEntry) {
        return this.mQuery.getTypeLabel(this.mContext.getResources(), recipientEntry.getDestinationType(), recipientEntry.getDestinationLabel()).toString().toUpperCase();
    }

    protected int getItemLayoutResId(AdapterType adapterType) {
        int i = AnonymousClass2.$SwitchMap$com$android$ex$chips$DropdownChipLayouter$AdapterType[adapterType.ordinal()];
        if (i == 1) {
            return R.layout.chips_autocomplete_recipient_dropdown_item;
        }
        if (i == 2) {
            return R.layout.chips_recipient_dropdown_item;
        }
        return R.layout.chips_recipient_dropdown_item;
    }

    protected int getAlternateItemLayoutResId(AdapterType adapterType) {
        int i = AnonymousClass2.$SwitchMap$com$android$ex$chips$DropdownChipLayouter$AdapterType[adapterType.ordinal()];
        if (i == 1) {
            return R.layout.chips_autocomplete_recipient_dropdown_item;
        }
        if (i == 2) {
            return R.layout.chips_recipient_dropdown_item;
        }
        return R.layout.chips_recipient_dropdown_item;
    }

    protected int getDefaultPhotoResId() {
        return R.drawable.ic_contact_picture;
    }

    protected CharSequence[] getStyledResults(String str, String... strArr) {
        int iIndexOf;
        if (isAllWhitespace(str)) {
            return strArr;
        }
        CharSequence[] charSequenceArr = new CharSequence[strArr.length];
        boolean z = false;
        for (int i = 0; i < strArr.length; i++) {
            String str2 = strArr[i];
            if (str2 != null) {
                if (!z && (iIndexOf = str2.toLowerCase().indexOf(str.toLowerCase())) != -1) {
                    SpannableStringBuilder spannableStringBuilderValueOf = SpannableStringBuilder.valueOf(str2);
                    spannableStringBuilderValueOf.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this.mContext, R.color.chips_dropdown_text_highlighted)), iIndexOf, str.length() + iIndexOf, 33);
                    charSequenceArr[i] = spannableStringBuilderValueOf;
                    z = true;
                } else {
                    charSequenceArr[i] = str2;
                }
            }
        }
        return charSequenceArr;
    }

    private static boolean isAllWhitespace(String str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    protected class ViewHolder {
        public final View bottomDivider;
        public final ImageView deleteView;
        public final TextView destinationTypeView;
        public final TextView destinationView;
        public final TextView displayNameView;
        public final ImageView imageView;
        public final View topDivider;

        public ViewHolder(View view) {
            this.displayNameView = (TextView) view.findViewById(DropdownChipLayouter.this.getDisplayNameResId());
            this.destinationView = (TextView) view.findViewById(DropdownChipLayouter.this.getDestinationResId());
            this.destinationTypeView = (TextView) view.findViewById(DropdownChipLayouter.this.getDestinationTypeResId());
            this.imageView = (ImageView) view.findViewById(DropdownChipLayouter.this.getPhotoResId());
            this.deleteView = (ImageView) view.findViewById(DropdownChipLayouter.this.getDeleteResId());
            this.topDivider = view.findViewById(R.id.chip_autocomplete_top_divider);
            this.bottomDivider = view.findViewById(R.id.chip_autocomplete_bottom_divider);
        }
    }
}
