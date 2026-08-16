package com.android.ex.chips;

import com.sonymobile.calendar.R;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;

import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.QwertyKeyListener;
import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.android.ex.chips.recipientchip.DrawableRecipientChip;
import com.android.ex.chips.recipientchip.InvisibleRecipientChip;
import com.android.ex.chips.recipientchip.ReplacementDrawableSpan;
import com.android.ex.chips.recipientchip.VisibleRecipientChip;
import com.google.common.primitives.Ints;
import com.sonyericsson.calendar.util.RecurrenceRuleParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class RecipientEditTextView extends MultiAutoCompleteTextView implements AdapterView.OnItemClickListener, ActionMode.Callback, RecipientAlternatesAdapter.OnCheckedItemChangedListener, GestureDetector.OnGestureListener, DialogInterface.OnDismissListener, View.OnClickListener, TextView.OnEditorActionListener, DropdownChipLayouter.ChipDeleteListener {
    private static final int AVATAR_POSITION_END = 0;
    private static final int AVATAR_POSITION_START = 1;
    static final int CHIP_LIMIT = 2;
    private static final char COMMIT_CHAR_SEMICOLON = ';';
    private static final long DISMISS_DELAY = 300;
    private static final int MAX_CHIPS_PARSED = 50;
    private static final String TAG = "RecipientEditTextView";
    private final Runnable mAddTextWatcher;
    private ListPopupWindow mAddressPopup;
    private View mAlternatePopupAnchor;
    private AdapterView.OnItemClickListener mAlternatesListener;
    private ListPopupWindow mAlternatesPopup;
    private boolean mAttachedToWindow;
    private int mAvatarPosition;
    private boolean mBackFromRestore;
    private int mCheckedItem;
    private Drawable mChipBackground;
    private Drawable mChipDelete;
    private float mChipFontSize;
    private float mChipHeight;
    private int mChipTextEndPadding;
    private int mChipTextStartPadding;
    private final int[] mCoords;
    private String mCopyAddress;
    private Dialog mCopyDialog;
    private int mCurrentSuggestionCount;
    private Bitmap mDefaultContactPhoto;
    private Runnable mDelayedShrink;
    private boolean mDisableDelete;
    private boolean mDragEnabled;
    private View mDropdownAnchor;
    private DropdownChipLayouter mDropdownChipLayouter;
    private GestureDetector mGestureDetector;
    private Runnable mHandlePendingChips;
    private Handler mHandler;
    private IndividualReplacementTask mIndividualReplacements;
    private Drawable mInvalidChipBackground;
    private float mLineSpacingExtra;
    private int mMaxLines;
    private ReplacementDrawableSpan mMoreChip;
    private TextView mMoreItem;
    private boolean mNoChips;
    final ArrayList<String> mPendingChips;
    private int mPendingChipsCount;
    private Drawable mProgressDrawable;
    private RecipientEntryItemClickedListener mRecipientEntryItemClickedListener;
    private final Rect mRect;
    private ArrayList<DrawableRecipientChip> mRemovedSpans;
    private boolean mRequiresShrinkWhenNotGone;
    private ScrollView mScrollView;
    private DrawableRecipientChip mSelectedChip;
    private int mSelectedChipBackgroundColor;
    private int mSelectedChipTextColor;
    private boolean mShouldShrink;
    ArrayList<DrawableRecipientChip> mTemporaryRecipients;
    private final int mTextHeight;
    private TextWatcher mTextWatcher;
    private MultiAutoCompleteTextView.Tokenizer mTokenizer;
    private boolean mTriedGettingScrollView;
    private int mUnselectedChipBackgroundColor;
    private int mUnselectedChipTextColor;
    private AutoCompleteTextView.Validator mValidator;
    private Paint mWorkPaint;
    private static final char COMMIT_CHAR_COMMA = ',';
    private static final char COMMIT_CHAR_SPACE = ' ';
    private static final String SEPARATOR = String.valueOf(COMMIT_CHAR_COMMA) + String.valueOf(COMMIT_CHAR_SPACE);
    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\+[0-9]+[\\- \\.]*)?(1?[ ]*\\([0-9]+\\)[\\- \\.]*)?([0-9][0-9\\- \\.][0-9\\- \\.]+[0-9])");
    private static final int DISMISS = 1671672458;

    public interface RecipientEntryItemClickedListener {
        void onRecipientEntryItemClicked(int i, int i2);
    }

    @Override // android.view.ActionMode.Callback
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return false;
    }

    protected void onChipCreated(RecipientEntry recipientEntry) {
    }

    @Override // android.view.ActionMode.Callback
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override // android.view.ActionMode.Callback
    public void onDestroyActionMode(ActionMode actionMode) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    @Override // android.view.ActionMode.Callback
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override // android.widget.MultiAutoCompleteTextView, android.widget.AutoCompleteTextView
    public void performValidation() {
    }

    @Override // android.widget.MultiAutoCompleteTextView, android.widget.AutoCompleteTextView
    protected void replaceText(CharSequence charSequence) {
    }

    public RecipientEditTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mRect = new Rect();
        this.mCoords = new int[2];
        this.mChipBackground = null;
        this.mChipDelete = null;
        this.mWorkPaint = new Paint();
        this.mDropdownAnchor = this;
        this.mPendingChips = new ArrayList<>();
        this.mPendingChipsCount = 0;
        this.mNoChips = false;
        this.mShouldShrink = true;
        this.mRequiresShrinkWhenNotGone = false;
        this.mDragEnabled = false;
        this.mBackFromRestore = false;
        this.mAddTextWatcher = new Runnable() { // from class: com.android.ex.chips.RecipientEditTextView.1
            @Override // java.lang.Runnable
            public void run() {
                if (RecipientEditTextView.this.mTextWatcher == null) {
                    RecipientEditTextView.this.mTextWatcher = new RecipientTextWatcher();
                    RecipientEditTextView recipientEditTextView = RecipientEditTextView.this;
                    recipientEditTextView.addTextChangedListener(recipientEditTextView.mTextWatcher);
                }
            }
        };
        this.mHandlePendingChips = new Runnable() { // from class: com.android.ex.chips.RecipientEditTextView.2
            @Override // java.lang.Runnable
            public void run() {
                if (RecipientEditTextView.this.mBackFromRestore) {
                    RecipientEditTextView.this.mBackFromRestore = false;
                } else {
                    RecipientEditTextView.this.handlePendingChips();
                }
            }
        };
        this.mDelayedShrink = new Runnable() { // from class: com.android.ex.chips.RecipientEditTextView.3
            @Override // java.lang.Runnable
            public void run() {
                RecipientEditTextView.this.shrink();
            }
        };
        setChipDimensions(context, attributeSet);
        setProgressDrawable(context);
        this.mTextHeight = calculateTextHeight();
        ListPopupWindow listPopupWindow = new ListPopupWindow(context);
        this.mAlternatesPopup = listPopupWindow;
        setupPopupWindow(listPopupWindow);
        ListPopupWindow listPopupWindow2 = new ListPopupWindow(context);
        this.mAddressPopup = listPopupWindow2;
        setupPopupWindow(listPopupWindow2);
        this.mCopyDialog = new Dialog(context);
        this.mAlternatesListener = new AdapterView.OnItemClickListener() { // from class: com.android.ex.chips.RecipientEditTextView.4
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                RecipientEditTextView.this.mAlternatesPopup.setOnItemClickListener(null);
                RecipientEditTextView recipientEditTextView = RecipientEditTextView.this;
                recipientEditTextView.replaceChip(recipientEditTextView.mSelectedChip, ((RecipientAlternatesAdapter) adapterView.getAdapter()).getRecipientEntry(i));
                Message messageObtain = Message.obtain(RecipientEditTextView.this.mHandler, RecipientEditTextView.DISMISS);
                messageObtain.obj = RecipientEditTextView.this.mAlternatesPopup;
                RecipientEditTextView.this.mHandler.sendMessageDelayed(messageObtain, 300L);
                RecipientEditTextView.this.clearComposingText();
            }
        };
        setInputType(getInputType() | 524288);
        setOnItemClickListener(this);
        setCustomSelectionActionModeCallback(this);
        this.mHandler = new Handler() { // from class: com.android.ex.chips.RecipientEditTextView.5
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == RecipientEditTextView.DISMISS) {
                    ((ListPopupWindow) message.obj).dismiss();
                } else {
                    super.handleMessage(message);
                }
            }
        };
        RecipientTextWatcher recipientTextWatcher = new RecipientTextWatcher();
        this.mTextWatcher = recipientTextWatcher;
        addTextChangedListener(recipientTextWatcher);
        this.mGestureDetector = new GestureDetector(context, this);
        setOnEditorActionListener(this);
        setDropdownChipLayouter(new DropdownChipLayouter(LayoutInflater.from(context), context));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void setProgressDrawable(Context context) {
        Drawable indeterminateDrawable = ((ProgressBar) LayoutInflater.from(context).inflate(R.layout.search_icon, (ViewGroup) null)).getIndeterminateDrawable();
        this.mProgressDrawable = indeterminateDrawable;
        if (indeterminateDrawable instanceof Animatable) {
            ((Animatable) indeterminateDrawable).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSearchProgress() {
        if (getCompoundDrawablesRelative()[2] == null) {
            setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, (Drawable) null, this.mProgressDrawable, (Drawable) null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearSearchProgress() {
        setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
    }

    private void setupPopupWindow(ListPopupWindow listPopupWindow) {
        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: com.android.ex.chips.RecipientEditTextView.6
            @Override // android.widget.PopupWindow.OnDismissListener
            public void onDismiss() {
                RecipientEditTextView.this.clearSelectedChip();
            }
        });
    }

    private int calculateTextHeight() {
        TextPaint paint = getPaint();
        this.mRect.setEmpty();
        paint.getTextBounds("a", 0, 1, this.mRect);
        this.mRect.left = 0;
        this.mRect.right = 0;
        return this.mRect.height();
    }

    public void setDropdownChipLayouter(DropdownChipLayouter dropdownChipLayouter) {
        this.mDropdownChipLayouter = dropdownChipLayouter;
        dropdownChipLayouter.setDeleteListener(this);
    }

    public void setRecipientEntryItemClickedListener(RecipientEntryItemClickedListener recipientEntryItemClickedListener) {
        this.mRecipientEntryItemClickedListener = recipientEntryItemClickedListener;
    }

    @Override // android.widget.AutoCompleteTextView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mAttachedToWindow = false;
    }

    @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        int dropDownAnchor = getDropDownAnchor();
        if (dropDownAnchor != -1) {
            this.mDropdownAnchor = getRootView().findViewById(dropDownAnchor);
        }
    }

    @Override // android.widget.AutoCompleteTextView
    public void setDropDownAnchor(int i) {
        super.setDropDownAnchor(i);
        if (i != -1) {
            this.mDropdownAnchor = getRootView().findViewById(i);
        }
    }

    @Override // android.widget.TextView.OnEditorActionListener
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        if (commitDefault()) {
            return true;
        }
        if (this.mSelectedChip == null) {
            return focusNext();
        }
        clearSelectedChip();
        return true;
    }

    @Override // android.widget.TextView, android.view.View
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        InputConnection inputConnectionOnCreateInputConnection = super.onCreateInputConnection(editorInfo);
        int i = editorInfo.imeOptions & 255;
        if ((i & 6) != 0) {
            editorInfo.imeOptions = i ^ editorInfo.imeOptions;
            editorInfo.imeOptions |= 6;
        }
        if ((editorInfo.imeOptions & Ints.MAX_POWER_OF_TWO) != 0) {
            editorInfo.imeOptions &= -1073741825;
        }
        editorInfo.actionId = 6;
        editorInfo.actionLabel = Build.VERSION.SDK_INT >= 21 ? null : getContext().getString(R.string.action_label);
        return inputConnectionOnCreateInputConnection;
    }

    DrawableRecipientChip getLastChip() {
        DrawableRecipientChip[] sortedRecipients = getSortedRecipients();
        if (sortedRecipients == null || sortedRecipients.length <= 0) {
            return null;
        }
        return sortedRecipients[sortedRecipients.length - 1];
    }

    public List<RecipientEntry> getSelectedRecipients() {
        DrawableRecipientChip[] drawableRecipientChipArr = (DrawableRecipientChip[]) getText().getSpans(0, getText().length(), DrawableRecipientChip.class);
        ArrayList arrayList = new ArrayList();
        if (drawableRecipientChipArr == null) {
            return arrayList;
        }
        for (DrawableRecipientChip drawableRecipientChip : drawableRecipientChipArr) {
            arrayList.add(drawableRecipientChip.getEntry());
        }
        return arrayList;
    }

    @Override // android.widget.TextView
    public void onSelectionChanged(int i, int i2) {
        DrawableRecipientChip lastChip = getLastChip();
        if (this.mSelectedChip == null && lastChip != null && i < getSpannable().getSpanEnd(lastChip)) {
            setSelection(Math.min(getSpannable().getSpanEnd(lastChip) + 1, getText().length()));
        }
        super.onSelectionChanged(i, i2);
    }

    @Override // android.widget.TextView, android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (!TextUtils.isEmpty(getText())) {
            super.onRestoreInstanceState(null);
            return;
        }
        this.mBackFromRestore = true;
        this.mShouldShrink = false;
        super.onRestoreInstanceState(parcelable);
    }

    @Override // android.widget.TextView, android.view.View
    public Parcelable onSaveInstanceState() {
        clearSelectedChip();
        removeMoreChip();
        return super.onSaveInstanceState();
    }

    @Override // android.widget.TextView
    public void append(CharSequence charSequence, int i, int i2) {
        TextWatcher textWatcher = this.mTextWatcher;
        if (textWatcher != null) {
            removeTextChangedListener(textWatcher);
        }
        super.append(charSequence, i, i2);
        if (!TextUtils.isEmpty(charSequence) && TextUtils.getTrimmedLength(charSequence) > 0) {
            String string = charSequence.toString();
            if (!string.trim().endsWith(String.valueOf(COMMIT_CHAR_COMMA))) {
                String str = SEPARATOR;
                super.append(str, 0, str.length());
                string = string + str;
            }
            if (!TextUtils.isEmpty(string) && TextUtils.getTrimmedLength(string) > 0) {
                this.mPendingChipsCount++;
                this.mPendingChips.add(string);
            }
        }
        if (this.mPendingChipsCount > 0) {
            postHandlePendingChips();
        }
        this.mHandler.post(this.mAddTextWatcher);
    }

    @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
    public void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        if (!z) {
            this.mShouldShrink = true;
            shrink();
        } else {
            this.mShouldShrink = false;
            expand();
        }
    }

    @Override // android.widget.AutoCompleteTextView
    public <T extends ListAdapter & Filterable> void setAdapter(T t) {
        super.setAdapter(t);
        BaseRecipientAdapter baseRecipientAdapter = (BaseRecipientAdapter) t;
        baseRecipientAdapter.registerUpdateObserver(new BaseRecipientAdapter.EntriesUpdatedObserver() { // from class: com.android.ex.chips.RecipientEditTextView.7
            @Override // com.android.ex.chips.BaseRecipientAdapter.EntriesUpdatedObserver
            public void onChanged(List<RecipientEntry> list) {
                if (list != null && list.size() > 0) {
                    RecipientEditTextView.this.scrollBottomIntoView();
                    if (RecipientEditTextView.this.mCurrentSuggestionCount == 0) {
                        RecipientEditTextView recipientEditTextView = RecipientEditTextView.this;
                        recipientEditTextView.announceForAccessibilityCompat(recipientEditTextView.getContext().getString(R.string.accessbility_suggestion_dropdown_opened));
                    }
                }
                RecipientEditTextView.this.mDropdownAnchor.getLocationInWindow(RecipientEditTextView.this.mCoords);
                RecipientEditTextView recipientEditTextView2 = RecipientEditTextView.this;
                recipientEditTextView2.getWindowVisibleDisplayFrame(recipientEditTextView2.mRect);
                int height = ((RecipientEditTextView.this.mRect.bottom - RecipientEditTextView.this.mCoords[1]) - RecipientEditTextView.this.mDropdownAnchor.getHeight()) - RecipientEditTextView.this.getDropDownVerticalOffset();
                RecipientEditTextView recipientEditTextView3 = RecipientEditTextView.this;
                if (height <= 0) {
                    height = -1;
                }
                recipientEditTextView3.setDropDownHeight(height);
                RecipientEditTextView.this.mCurrentSuggestionCount = list == null ? 0 : list.size();
            }
        });
        baseRecipientAdapter.setDropdownChipLayouter(this.mDropdownChipLayouter);
        baseRecipientAdapter.registerSearchObserver(new BaseRecipientAdapter.SearchObserver() { // from class: com.android.ex.chips.RecipientEditTextView.8
            @Override // com.android.ex.chips.BaseRecipientAdapter.SearchObserver
            public void searchStarted() {
                RecipientEditTextView.this.showSearchProgress();
            }

            @Override // com.android.ex.chips.BaseRecipientAdapter.SearchObserver
            public void searchFinished() {
                RecipientEditTextView.this.clearSearchProgress();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void announceForAccessibilityCompat(String str) {
        ViewParent parent;
        if (!((AccessibilityManager) getContext().getSystemService("accessibility")).isEnabled() || Build.VERSION.SDK_INT < 16 || (parent = getParent()) == null) {
            return;
        }
        AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(16384);
        onInitializeAccessibilityEvent(accessibilityEventObtain);
        accessibilityEventObtain.getText().add(str);
        accessibilityEventObtain.setContentDescription(null);
        parent.requestSendAccessibilityEvent(this, accessibilityEventObtain);
    }

    protected void scrollBottomIntoView() {
        if (this.mScrollView == null || !this.mShouldShrink) {
            return;
        }
        getLocationInWindow(this.mCoords);
        int height = getHeight();
        int[] iArr = this.mCoords;
        int i = iArr[1] + height;
        this.mScrollView.getLocationInWindow(iArr);
        int lineCount = this.mCoords[1] + (height / getLineCount());
        if (i > lineCount) {
            this.mScrollView.scrollBy(0, i - lineCount);
        }
    }

    protected ScrollView getScrollView() {
        return this.mScrollView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void shrink() {
        if (this.mTokenizer == null) {
            return;
        }
        DrawableRecipientChip drawableRecipientChip = this.mSelectedChip;
        long contactId = drawableRecipientChip != null ? drawableRecipientChip.getEntry().getContactId() : -1L;
        if (this.mSelectedChip != null && contactId != -1 && !isPhoneQuery() && contactId != -2) {
            clearSelectedChip();
        } else {
            if (getWidth() <= 0) {
                this.mHandler.removeCallbacks(this.mDelayedShrink);
                if (getVisibility() == 8) {
                    this.mRequiresShrinkWhenNotGone = true;
                    return;
                } else {
                    this.mHandler.post(this.mDelayedShrink);
                    return;
                }
            }
            if (this.mPendingChipsCount > 0) {
                postHandlePendingChips();
            } else {
                Editable text = getText();
                int selectionEnd = getSelectionEnd();
                int iFindTokenStart = this.mTokenizer.findTokenStart(text, selectionEnd);
                DrawableRecipientChip[] drawableRecipientChipArr = (DrawableRecipientChip[]) getSpannable().getSpans(iFindTokenStart, selectionEnd, DrawableRecipientChip.class);
                if (drawableRecipientChipArr == null || drawableRecipientChipArr.length == 0) {
                    Editable text2 = getText();
                    int iFindTokenEnd = this.mTokenizer.findTokenEnd(text2, iFindTokenStart);
                    if (iFindTokenEnd < text2.length() && text2.charAt(iFindTokenEnd) == ',') {
                        iFindTokenEnd = movePastTerminators(iFindTokenEnd);
                    }
                    if (iFindTokenEnd != getSelectionEnd()) {
                        handleEdit(iFindTokenStart, iFindTokenEnd);
                    } else {
                        commitChip(iFindTokenStart, selectionEnd, text);
                    }
                }
            }
            this.mHandler.post(this.mAddTextWatcher);
        }
        createMoreChip();
    }

    private void expand() {
        if (!this.mShouldShrink) {
            setMaxLines(Integer.MAX_VALUE);
        }
        removeMoreChip();
        setCursorVisible(true);
        Editable text = getText();
        setSelection((text == null || text.length() <= 0) ? 0 : text.length());
        ArrayList<DrawableRecipientChip> arrayList = this.mTemporaryRecipients;
        if (arrayList == null || arrayList.size() <= 0) {
            return;
        }
        new RecipientReplacementTask().execute(new Void[0]);
        this.mTemporaryRecipients = null;
    }

    private CharSequence ellipsizeText(CharSequence charSequence, TextPaint textPaint, float f) {
        textPaint.setTextSize(this.mChipFontSize);
        if (f <= 0.0f && Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "Max width is negative: " + f);
        }
        return TextUtils.ellipsize(charSequence, textPaint, f, TextUtils.TruncateAt.END);
    }

    private Bitmap createChipBitmap(RecipientEntry recipientEntry, TextPaint textPaint) {
        textPaint.setColor(getDefaultChipTextColor(recipientEntry));
        ChipBitmapContainer chipBitmapContainerCreateChipBitmap = createChipBitmap(recipientEntry, textPaint, getChipBackground(recipientEntry), getDefaultChipBackgroundColor(recipientEntry));
        if (chipBitmapContainerCreateChipBitmap.loadIcon) {
            loadAvatarIcon(recipientEntry, chipBitmapContainerCreateChipBitmap);
        }
        return chipBitmapContainerCreateChipBitmap.bitmap;
    }

    private ChipBitmapContainer createChipBitmap(RecipientEntry recipientEntry, TextPaint textPaint, Drawable drawable, int i) {
        int i2;
        ChipBitmapContainer chipBitmapContainer = new ChipBitmapContainer();
        Rect rect = new Rect();
        if (drawable != null) {
            drawable.getPadding(rect);
        }
        int i3 = (int) this.mChipHeight;
        int i4 = recipientEntry.isValid() ? (i3 - rect.top) - rect.bottom : 0;
        float[] fArr = new float[1];
        textPaint.getTextWidths(" ", fArr);
        CharSequence charSequenceEllipsizeText = ellipsizeText(createChipDisplayText(recipientEntry), textPaint, (((calculateAvailableWidth() - i4) - fArr[0]) - rect.left) - rect.right);
        int iMeasureText = (int) textPaint.measureText(charSequenceEllipsizeText, 0, charSequenceEllipsizeText.length());
        int iMax = Math.max(i4 * 2, (recipientEntry.isValid() ? this.mChipTextStartPadding : this.mChipTextEndPadding) + iMeasureText + this.mChipTextEndPadding + i4 + rect.left + rect.right);
        chipBitmapContainer.bitmap = Bitmap.createBitmap(iMax, i3, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(chipBitmapContainer.bitmap);
        if (drawable != null) {
            drawable.setBounds(0, 0, iMax, i3);
            drawable.draw(canvas);
        } else {
            this.mWorkPaint.reset();
            this.mWorkPaint.setColor(i);
            float f = i3;
            float f2 = f / 2.0f;
            canvas.drawRoundRect(new RectF(0.0f, 0.0f, iMax, f), f2, f2, this.mWorkPaint);
        }
        if (shouldPositionAvatarOnRight()) {
            i2 = this.mChipTextEndPadding + rect.left;
        } else {
            i2 = ((iMax - rect.right) - this.mChipTextEndPadding) - iMeasureText;
        }
        canvas.drawText(charSequenceEllipsizeText, 0, charSequenceEllipsizeText.length(), i2, getTextYOffset(i3), textPaint);
        int i5 = shouldPositionAvatarOnRight() ? (iMax - rect.right) - i4 : rect.left;
        chipBitmapContainer.left = i5;
        chipBitmapContainer.top = rect.top;
        chipBitmapContainer.right = i5 + i4;
        chipBitmapContainer.bottom = i3 - rect.bottom;
        return chipBitmapContainer;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void drawIcon(ChipBitmapContainer chipBitmapContainer, Bitmap bitmap) {
        drawIconOnCanvas(bitmap, new Canvas(chipBitmapContainer.bitmap), new RectF(0.0f, 0.0f, bitmap.getWidth(), bitmap.getHeight()), new RectF(chipBitmapContainer.left, chipBitmapContainer.top, chipBitmapContainer.right, chipBitmapContainer.bottom));
    }

    private boolean shouldPositionAvatarOnRight() {
        boolean z = Build.VERSION.SDK_INT >= 17 && getLayoutDirection() == 1;
        boolean z2 = this.mAvatarPosition == 0;
        if (z) {
            return !z2;
        }
        return z2;
    }

    private void loadAvatarIcon(final RecipientEntry recipientEntry, final ChipBitmapContainer chipBitmapContainer) {
        long contactId = recipientEntry.getContactId();
        boolean z = true;
        if (!isPhoneQuery() ? contactId == -1 || contactId == -2 : contactId == -1) {
            z = false;
        }
        if (z) {
            byte[] photoBytes = recipientEntry.getPhotoBytes();
            if (photoBytes == null) {
                getAdapter().fetchPhoto(recipientEntry, new PhotoManager.PhotoManagerCallback() { // from class: com.android.ex.chips.RecipientEditTextView.9
                    @Override // com.android.ex.chips.PhotoManager.PhotoManagerCallback
                    public void onPhotoBytesPopulated() {
                        onPhotoBytesAsynchronouslyPopulated();
                    }

                    @Override // com.android.ex.chips.PhotoManager.PhotoManagerCallback
                    public void onPhotoBytesAsynchronouslyPopulated() {
                        byte[] photoBytes2 = recipientEntry.getPhotoBytes();
                        tryDrawAndInvalidate(BitmapFactory.decodeByteArray(photoBytes2, 0, photoBytes2.length));
                    }

                    @Override // com.android.ex.chips.PhotoManager.PhotoManagerCallback
                    public void onPhotoBytesAsyncLoadFailed() {
                        tryDrawAndInvalidate(RecipientEditTextView.this.mDefaultContactPhoto);
                    }

                    private void tryDrawAndInvalidate(Bitmap bitmap) {
                        RecipientEditTextView.this.drawIcon(chipBitmapContainer, bitmap);
                        if (Looper.myLooper() == Looper.getMainLooper()) {
                            RecipientEditTextView.this.invalidate();
                        } else {
                            RecipientEditTextView.this.post(new Runnable() { // from class: com.android.ex.chips.RecipientEditTextView.9.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    RecipientEditTextView.this.invalidate();
                                }
                            });
                        }
                    }
                });
            } else {
                drawIcon(chipBitmapContainer, BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length));
            }
        }
    }

    Drawable getChipBackground(RecipientEntry recipientEntry) {
        return recipientEntry.isValid() ? this.mChipBackground : this.mInvalidChipBackground;
    }

    private int getDefaultChipTextColor(RecipientEntry recipientEntry) {
        return recipientEntry.isValid() ? this.mUnselectedChipTextColor : ContextCompat.getColor(getContext(), android.R.color.black);
    }

    private int getDefaultChipBackgroundColor(RecipientEntry recipientEntry) {
        return recipientEntry.isValid() ? this.mUnselectedChipBackgroundColor : ContextCompat.getColor(getContext(), R.color.chip_background_invalid);
    }

    protected float getTextYOffset(int i) {
        return i - ((i - this.mTextHeight) / 2);
    }

    protected void drawIconOnCanvas(Bitmap bitmap, Canvas canvas, RectF rectF, RectF rectF2) {
        Matrix matrix = new Matrix();
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        matrix.reset();
        matrix.setRectToRect(rectF, rectF2, Matrix.ScaleToFit.FILL);
        bitmapShader.setLocalMatrix(matrix);
        this.mWorkPaint.reset();
        this.mWorkPaint.setShader(bitmapShader);
        this.mWorkPaint.setAntiAlias(true);
        this.mWorkPaint.setFilterBitmap(true);
        this.mWorkPaint.setDither(true);
        canvas.drawCircle(rectF2.centerX(), rectF2.centerY(), rectF2.width() / 2.0f, this.mWorkPaint);
        this.mWorkPaint.reset();
        this.mWorkPaint.setColor(0);
        this.mWorkPaint.setStyle(Paint.Style.STROKE);
        this.mWorkPaint.setStrokeWidth(1.0f);
        this.mWorkPaint.setAntiAlias(true);
        canvas.drawCircle(rectF2.centerX(), rectF2.centerY(), (rectF2.width() / 2.0f) - 0.5f, this.mWorkPaint);
        this.mWorkPaint.reset();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public DrawableRecipientChip constructChipSpan(RecipientEntry recipientEntry) {
        TextPaint paint = getPaint();
        float textSize = paint.getTextSize();
        int color = paint.getColor();
        Bitmap bitmapCreateChipBitmap = createChipBitmap(recipientEntry, paint);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmapCreateChipBitmap);
        bitmapDrawable.setBounds(0, 0, bitmapCreateChipBitmap.getWidth(), bitmapCreateChipBitmap.getHeight());
        VisibleRecipientChip visibleRecipientChip = new VisibleRecipientChip(bitmapDrawable, recipientEntry);
        visibleRecipientChip.setExtraMargin(this.mLineSpacingExtra);
        paint.setTextSize(textSize);
        paint.setColor(color);
        return visibleRecipientChip;
    }

    private int calculateOffsetFromBottom(int i) {
        return (-(((getLineCount() - (i + 1)) * ((int) this.mChipHeight)) + getPaddingBottom() + getPaddingTop())) + getDropDownVerticalOffset();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int calculateOffsetFromBottomToTop(int i) {
        return -((int) (((this.mChipHeight + (this.mLineSpacingExtra * 2.0f)) * Math.abs(getLineCount() - i)) + getPaddingBottom()));
    }

    private float calculateAvailableWidth() {
        return (((getWidth() - getPaddingLeft()) - getPaddingRight()) - this.mChipTextStartPadding) - this.mChipTextEndPadding;
    }

    private void setChipDimensions(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.RecipientEditTextView, 0, 0);
        this.mChipBackground = typedArrayObtainStyledAttributes.getDrawable(R.styleable.RecipientEditTextView_chipBackground);
        this.mInvalidChipBackground = typedArrayObtainStyledAttributes.getDrawable(R.styleable.RecipientEditTextView_invalidChipBackground);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.RecipientEditTextView_chipDelete);
        this.mChipDelete = drawable;
        if (drawable == null) {
            this.mChipDelete = ContextCompat.getDrawable(context, R.drawable.ic_cancel_wht_24dp);
        }
        int dimensionPixelSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.RecipientEditTextView_chipPadding, -1);
        this.mChipTextEndPadding = dimensionPixelSize;
        this.mChipTextStartPadding = dimensionPixelSize;
        Resources resources = context.getResources();
        if (this.mChipTextStartPadding == -1) {
            int dimension = (int) resources.getDimension(R.dimen.chip_padding);
            this.mChipTextEndPadding = dimension;
            this.mChipTextStartPadding = dimension;
        }
        int dimension2 = (int) resources.getDimension(R.dimen.chip_padding_start);
        if (dimension2 >= 0) {
            this.mChipTextStartPadding = dimension2;
        }
        int dimension3 = (int) resources.getDimension(R.dimen.chip_padding_end);
        if (dimension3 >= 0) {
            this.mChipTextEndPadding = dimension3;
        }
        this.mDefaultContactPhoto = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_contact_picture);
        this.mMoreItem = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.more_item, (ViewGroup) null);
        float dimensionPixelSize2 = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.RecipientEditTextView_chipHeight, -1);
        this.mChipHeight = dimensionPixelSize2;
        if (dimensionPixelSize2 == -1.0f) {
            this.mChipHeight = resources.getDimension(R.dimen.chip_height);
        }
        float dimensionPixelSize3 = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.RecipientEditTextView_chipFontSize, -1);
        this.mChipFontSize = dimensionPixelSize3;
        if (dimensionPixelSize3 == -1.0f) {
            this.mChipFontSize = resources.getDimension(R.dimen.chip_text_size);
        }
        this.mAvatarPosition = typedArrayObtainStyledAttributes.getInt(R.styleable.RecipientEditTextView_avatarPosition, 1);
        this.mDisableDelete = typedArrayObtainStyledAttributes.getBoolean(R.styleable.RecipientEditTextView_disableDelete, false);
        this.mMaxLines = resources.getInteger(R.integer.chips_max_lines);
        this.mLineSpacingExtra = resources.getDimensionPixelOffset(R.dimen.line_spacing_extra);
        this.mUnselectedChipTextColor = typedArrayObtainStyledAttributes.getColor(R.styleable.RecipientEditTextView_unselectedChipTextColor, ContextCompat.getColor(getContext(), android.R.color.black));
        this.mSelectedChipTextColor = typedArrayObtainStyledAttributes.getColor(R.styleable.RecipientEditTextView_selectedChipTextColor, ContextCompat.getColor(context, android.R.color.white));
        this.mUnselectedChipBackgroundColor = typedArrayObtainStyledAttributes.getColor(R.styleable.RecipientEditTextView_unselectedChipBackgroundColor, ContextCompat.getColor(context, R.color.chip_background));
        this.mSelectedChipBackgroundColor = typedArrayObtainStyledAttributes.getColor(R.styleable.RecipientEditTextView_selectedChipBackgroundColor, ContextCompat.getColor(context, R.color.chip_background_selected));
        typedArrayObtainStyledAttributes.recycle();
    }

    void setMoreItem(TextView textView) {
        this.mMoreItem = textView;
    }

    void setChipBackground(Drawable drawable) {
        this.mChipBackground = drawable;
    }

    void setChipHeight(int i) {
        this.mChipHeight = i;
    }

    public float getChipHeight() {
        return this.mChipHeight;
    }

    public void setOnFocusListShrinkRecipients(boolean z) {
        this.mShouldShrink = z;
    }

    @Override // android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != 0 && i2 != 0) {
            if (this.mPendingChipsCount > 0) {
                postHandlePendingChips();
            } else {
                checkChipWidths();
            }
        }
        if (this.mScrollView != null || this.mTriedGettingScrollView) {
            return;
        }
        ViewParent parent = getParent();
        while (parent != null && !(parent instanceof ScrollView)) {
            parent = parent.getParent();
        }
        if (parent != null) {
            this.mScrollView = (ScrollView) parent;
        }
        this.mTriedGettingScrollView = true;
    }

    private void postHandlePendingChips() {
        this.mHandler.removeCallbacks(this.mHandlePendingChips);
        this.mHandler.post(this.mHandlePendingChips);
    }

    private void checkChipWidths() {
        DrawableRecipientChip[] sortedRecipients = getSortedRecipients();
        if (sortedRecipients != null) {
            for (DrawableRecipientChip drawableRecipientChip : sortedRecipients) {
                Rect bounds = drawableRecipientChip.getBounds();
                if (getWidth() > 0 && bounds.right - bounds.left > (getWidth() - getPaddingLeft()) - getPaddingRight()) {
                    replaceChip(drawableRecipientChip, drawableRecipientChip.getEntry());
                }
            }
        }
    }

    void handlePendingChips() {
        if (getViewWidth() <= 0) {
            return;
        }
        synchronized (this.mPendingChips) {
            if (this.mPendingChipsCount <= 0) {
                return;
            }
            Editable text = getText();
            if (this.mPendingChipsCount <= 50) {
                int i = 0;
                while (i < this.mPendingChips.size()) {
                    String str = this.mPendingChips.get(i);
                    int iIndexOf = text.toString().indexOf(str);
                    int length = (str.length() + iIndexOf) - 1;
                    if (iIndexOf >= 0) {
                        if (length < text.length() - 2 && text.charAt(length) == ',') {
                            length++;
                        }
                        createReplacementChip(iIndexOf, length, text, i < 2 || !this.mShouldShrink);
                    }
                    this.mPendingChipsCount--;
                    i++;
                }
                sanitizeEnd();
            } else {
                this.mNoChips = true;
            }
            ArrayList<DrawableRecipientChip> arrayList = this.mTemporaryRecipients;
            if (arrayList != null && arrayList.size() > 0 && this.mTemporaryRecipients.size() <= 50) {
                if (hasFocus() || this.mTemporaryRecipients.size() <= 2) {
                    new RecipientReplacementTask().execute(new Void[0]);
                    this.mTemporaryRecipients = null;
                } else {
                    IndividualReplacementTask individualReplacementTask = new IndividualReplacementTask();
                    this.mIndividualReplacements = individualReplacementTask;
                    individualReplacementTask.execute(new ArrayList(this.mTemporaryRecipients.subList(0, 2)));
                    if (this.mTemporaryRecipients.size() > 2) {
                        ArrayList<DrawableRecipientChip> arrayList2 = this.mTemporaryRecipients;
                        this.mTemporaryRecipients = new ArrayList<>(arrayList2.subList(2, arrayList2.size()));
                    } else {
                        this.mTemporaryRecipients = null;
                    }
                    createMoreChip();
                }
            } else {
                this.mTemporaryRecipients = null;
                createMoreChip();
            }
            this.mPendingChipsCount = 0;
            this.mPendingChips.clear();
        }
    }

    int getViewWidth() {
        return getWidth();
    }

    void sanitizeEnd() {
        int spanEnd;
        if (this.mPendingChipsCount > 0) {
            return;
        }
        DrawableRecipientChip[] sortedRecipients = getSortedRecipients();
        Spannable spannable = getSpannable();
        if (sortedRecipients == null || sortedRecipients.length <= 0) {
            return;
        }
        ReplacementDrawableSpan moreChip = getMoreChip();
        this.mMoreChip = moreChip;
        if (moreChip != null) {
            spanEnd = spannable.getSpanEnd(moreChip);
        } else {
            spanEnd = getSpannable().getSpanEnd(getLastChip());
        }
        Editable text = getText();
        int length = text.length();
        if (length > spanEnd) {
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "There were extra characters after the last tokenizable entry." + ((Object) text));
            }
            text.delete(spanEnd + 1, length);
        }
    }

    void createReplacementChip(int i, int i2, Editable editable, boolean z) {
        if (alreadyHasChip(i, i2)) {
            return;
        }
        String strSubstring = editable.toString().substring(i, i2);
        String strTrim = strSubstring.trim();
        int iLastIndexOf = strTrim.lastIndexOf(44);
        if (iLastIndexOf != -1 && iLastIndexOf == strTrim.length() - 1) {
            strSubstring = strTrim.substring(0, strTrim.length() - 1);
        }
        RecipientEntry recipientEntryCreateTokenizedEntry = createTokenizedEntry(strSubstring);
        if (recipientEntryCreateTokenizedEntry != null) {
            DrawableRecipientChip drawableRecipientChipConstructChipSpan = null;
            try {
                if (!this.mNoChips) {
                    drawableRecipientChipConstructChipSpan = z ? constructChipSpan(recipientEntryCreateTokenizedEntry) : new InvisibleRecipientChip(recipientEntryCreateTokenizedEntry);
                }
            } catch (NullPointerException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            editable.setSpan(drawableRecipientChipConstructChipSpan, i, i2, 33);
            if (drawableRecipientChipConstructChipSpan != null) {
                if (this.mTemporaryRecipients == null) {
                    this.mTemporaryRecipients = new ArrayList<>();
                }
                drawableRecipientChipConstructChipSpan.setOriginalText(strSubstring);
                this.mTemporaryRecipients.add(drawableRecipientChipConstructChipSpan);
            }
        }
    }

    private static boolean isPhoneNumber(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return PHONE_PATTERN.matcher(str).matches();
    }

    RecipientEntry createTokenizedEntry(String str) {
        String address = null;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        boolean z = true;
        if (isPhoneQuery() && isPhoneNumber(str)) {
            return RecipientEntry.constructFakePhoneEntry(str, true);
        }
        Rfc822Token[] rfc822TokenArr = Rfc822Tokenizer.tokenize(str);
        boolean zIsValid = isValid(str);
        if (zIsValid && rfc822TokenArr != null && rfc822TokenArr.length > 0) {
            String name = rfc822TokenArr[0].getName();
            if (!TextUtils.isEmpty(name)) {
                return RecipientEntry.constructGeneratedEntry(name, rfc822TokenArr[0].getAddress(), zIsValid);
            }
            String address2 = rfc822TokenArr[0].getAddress();
            if (!TextUtils.isEmpty(address2)) {
                return RecipientEntry.constructFakeEntry(address2, zIsValid);
            }
        }
        AutoCompleteTextView.Validator validator = this.mValidator;
        if (validator != null && !zIsValid) {
            String string = validator.fixText(str).toString();
            if (TextUtils.isEmpty(string)) {
                address = string;
            } else if (string.contains(str)) {
                Rfc822Token[] rfc822TokenArr2 = Rfc822Tokenizer.tokenize(string);
                if (rfc822TokenArr2.length > 0) {
                    address = rfc822TokenArr2[0].getAddress();
                } else {
                    address = string;
                    z = zIsValid;
                }
                zIsValid = z;
            } else {
                zIsValid = false;
            }
        }
        if (!TextUtils.isEmpty(address)) {
            str = address;
        }
        return RecipientEntry.constructFakeEntry(str, zIsValid);
    }

    private boolean isValid(String str) {
        AutoCompleteTextView.Validator validator = this.mValidator;
        if (validator == null) {
            return true;
        }
        return validator.isValid(str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String tokenizeAddress(String str) {
        Rfc822Token[] rfc822TokenArr = Rfc822Tokenizer.tokenize(str);
        return (rfc822TokenArr == null || rfc822TokenArr.length <= 0) ? str : rfc822TokenArr[0].getAddress();
    }

    @Override // android.widget.MultiAutoCompleteTextView
    public void setTokenizer(MultiAutoCompleteTextView.Tokenizer tokenizer) {
        this.mTokenizer = tokenizer;
        super.setTokenizer(tokenizer);
    }

    @Override // android.widget.AutoCompleteTextView
    public void setValidator(AutoCompleteTextView.Validator validator) {
        this.mValidator = validator;
        super.setValidator(validator);
    }

    @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View
    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        if (i == 4 && this.mSelectedChip != null) {
            clearSelectedChip();
            return true;
        }
        return super.onKeyPreIme(i, keyEvent);
    }

    @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (i == 61 && keyEvent.hasNoModifiers()) {
            if (this.mSelectedChip != null) {
                clearSelectedChip();
            } else {
                commitDefault();
            }
        }
        return super.onKeyUp(i, keyEvent);
    }

    private boolean focusNext() {
        View viewFocusSearch = focusSearch(KeyEvent.KEYCODE_MEDIA_RECORD);
        if (viewFocusSearch == null) {
            return false;
        }
        viewFocusSearch.requestFocus();
        return true;
    }

    private boolean commitDefault() {
        if (this.mTokenizer == null) {
            return false;
        }
        Editable text = getText();
        int selectionEnd = getSelectionEnd();
        int iFindTokenStart = this.mTokenizer.findTokenStart(text, selectionEnd);
        if (!shouldCreateChip(iFindTokenStart, selectionEnd)) {
            return false;
        }
        int iMovePastTerminators = movePastTerminators(this.mTokenizer.findTokenEnd(getText(), iFindTokenStart));
        if (iMovePastTerminators != getSelectionEnd()) {
            handleEdit(iFindTokenStart, iMovePastTerminators);
            return true;
        }
        return commitChip(iFindTokenStart, selectionEnd, text);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void commitByCharacter() {
        if (this.mTokenizer == null) {
            return;
        }
        Editable text = getText();
        int selectionEnd = getSelectionEnd();
        int iFindTokenStart = this.mTokenizer.findTokenStart(text, selectionEnd);
        if (shouldCreateChip(iFindTokenStart, selectionEnd)) {
            commitChip(iFindTokenStart, selectionEnd, text);
        }
        setSelection(getText().length());
    }

    private boolean commitChip(int i, int i2, Editable editable) {
        char cCharAt;
        BaseRecipientAdapter adapter = getAdapter();
        if (adapter != null && adapter.getCount() > 0 && enoughToFilter() && i2 == getSelectionEnd() && !isPhoneQuery()) {
            int listSelection = getListSelection();
            if (listSelection == -1) {
                submitItemAtPosition(0);
            } else {
                submitItemAtPosition(listSelection);
            }
            dismissDropDown();
            return true;
        }
        int iFindTokenEnd = this.mTokenizer.findTokenEnd(editable, i);
        int i3 = iFindTokenEnd + 1;
        if (editable.length() > i3 && ((cCharAt = editable.charAt(i3)) == ',' || cCharAt == ';')) {
            iFindTokenEnd = i3;
        }
        String strTrim = editable.toString().substring(i, iFindTokenEnd).trim();
        clearComposingText();
        if (strTrim.length() <= 0 || strTrim.equals(" ")) {
            return false;
        }
        RecipientEntry recipientEntryCreateTokenizedEntry = createTokenizedEntry(strTrim);
        if (recipientEntryCreateTokenizedEntry != null) {
            QwertyKeyListener.markAsReplaced(editable, i, i2, "");
            CharSequence charSequenceCreateChip = createChip(recipientEntryCreateTokenizedEntry);
            if (charSequenceCreateChip != null && i > -1 && i2 > -1) {
                editable.replace(i, i2, charSequenceCreateChip);
            }
        }
        if (i2 == getSelectionEnd()) {
            dismissDropDown();
        }
        sanitizeBetween();
        return true;
    }

    void sanitizeBetween() {
        DrawableRecipientChip[] sortedRecipients;
        if (this.mPendingChipsCount <= 0 && (sortedRecipients = getSortedRecipients()) != null && sortedRecipients.length > 0) {
            DrawableRecipientChip drawableRecipientChip = sortedRecipients[sortedRecipients.length - 1];
            DrawableRecipientChip drawableRecipientChip2 = sortedRecipients.length > 1 ? sortedRecipients[sortedRecipients.length - 2] : null;
            int spanEnd = 0;
            int spanStart = getSpannable().getSpanStart(drawableRecipientChip);
            if (drawableRecipientChip2 != null) {
                spanEnd = getSpannable().getSpanEnd(drawableRecipientChip2);
                Editable text = getText();
                if (spanEnd == -1 || spanEnd > text.length() - 1) {
                    return;
                }
                if (text.charAt(spanEnd) == ' ') {
                    spanEnd++;
                }
            }
            if (spanEnd < 0 || spanStart < 0 || spanEnd >= spanStart) {
                return;
            }
            getText().delete(spanEnd, spanStart);
        }
    }

    private boolean shouldCreateChip(int i, int i2) {
        return !this.mNoChips && hasFocus() && enoughToFilter() && !alreadyHasChip(i, i2);
    }

    private boolean alreadyHasChip(int i, int i2) {
        if (this.mNoChips) {
            return true;
        }
        DrawableRecipientChip[] drawableRecipientChipArr = (DrawableRecipientChip[]) getSpannable().getSpans(i, i2, DrawableRecipientChip.class);
        return drawableRecipientChipArr != null && drawableRecipientChipArr.length > 0;
    }

    private void handleEdit(int i, int i2) {
        if (i == -1 || i2 == -1) {
            dismissDropDown();
            return;
        }
        Editable text = getText();
        setSelection(i2);
        String strSubstring = getText().toString().substring(i, i2);
        if (!TextUtils.isEmpty(strSubstring)) {
            RecipientEntry recipientEntryConstructFakeEntry = RecipientEntry.constructFakeEntry(strSubstring, isValid(strSubstring));
            QwertyKeyListener.markAsReplaced(text, i, i2, "");
            CharSequence charSequenceCreateChip = createChip(recipientEntryConstructFakeEntry);
            int selectionEnd = getSelectionEnd();
            if (charSequenceCreateChip != null && i > -1 && selectionEnd > -1) {
                text.replace(i, selectionEnd, charSequenceCreateChip);
            }
        }
        dismissDropDown();
    }

    @Override // android.widget.AutoCompleteTextView, android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (this.mSelectedChip != null && i == 67) {
            ListPopupWindow listPopupWindow = this.mAlternatesPopup;
            if (listPopupWindow != null && listPopupWindow.isShowing()) {
                this.mAlternatesPopup.dismiss();
            }
            removeChip(this.mSelectedChip);
        }
        if ((i == 23 || i == 66) && keyEvent.hasNoModifiers()) {
            if (commitDefault()) {
                return true;
            }
            if (this.mSelectedChip != null) {
                clearSelectedChip();
                return true;
            }
            if (focusNext()) {
                return true;
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    Spannable getSpannable() {
        return getText();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getChipStart(DrawableRecipientChip drawableRecipientChip) {
        return getSpannable().getSpanStart(drawableRecipientChip);
    }

    private int getChipEnd(DrawableRecipientChip drawableRecipientChip) {
        return getSpannable().getSpanEnd(drawableRecipientChip);
    }

    @Override // android.widget.MultiAutoCompleteTextView, android.widget.AutoCompleteTextView
    protected void performFiltering(CharSequence charSequence, int i) {
        boolean zIsCompletedToken = isCompletedToken(charSequence);
        if (enoughToFilter() && !zIsCompletedToken) {
            int selectionEnd = getSelectionEnd();
            DrawableRecipientChip[] drawableRecipientChipArr = (DrawableRecipientChip[]) getSpannable().getSpans(this.mTokenizer.findTokenStart(charSequence, selectionEnd), selectionEnd, DrawableRecipientChip.class);
            if (drawableRecipientChipArr != null && drawableRecipientChipArr.length > 0) {
                dismissDropDown();
                return;
            }
        } else if (zIsCompletedToken) {
            dismissDropDown();
            return;
        }
        super.performFiltering(charSequence, i);
    }

    boolean isCompletedToken(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return false;
        }
        int length = charSequence.length();
        String strTrim = charSequence.toString().substring(this.mTokenizer.findTokenStart(charSequence, length), length).trim();
        if (TextUtils.isEmpty(strTrim)) {
            return false;
        }
        char cCharAt = strTrim.charAt(strTrim.length() - 1);
        return cCharAt == ',' || cCharAt == ';';
    }

    public void clearSelectedChip() {
        DrawableRecipientChip drawableRecipientChip = this.mSelectedChip;
        if (drawableRecipientChip != null) {
            unselectChip(drawableRecipientChip);
            this.mSelectedChip = null;
        }
        setCursorVisible(true);
        setSelection(getText().length());
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isFocused()) {
            return super.onTouchEvent(motionEvent);
        }
        boolean zOnTouchEvent = super.onTouchEvent(motionEvent);
        int action = motionEvent.getAction();
        boolean z = false;
        if (this.mSelectedChip == null) {
            this.mGestureDetector.onTouchEvent(motionEvent);
        }
        if (this.mCopyAddress == null && action == 1) {
            DrawableRecipientChip drawableRecipientChipFindChip = findChip(putOffsetInRange(motionEvent.getX(), motionEvent.getY()));
            if (drawableRecipientChipFindChip != null) {
                DrawableRecipientChip drawableRecipientChip = this.mSelectedChip;
                if (drawableRecipientChip != null && drawableRecipientChip != drawableRecipientChipFindChip) {
                    clearSelectedChip();
                    selectChip(drawableRecipientChipFindChip);
                } else if (drawableRecipientChip == null) {
                    commitDefault();
                    selectChip(drawableRecipientChipFindChip);
                } else {
                    onClick(drawableRecipientChip);
                }
                zOnTouchEvent = true;
                z = true;
            } else {
                DrawableRecipientChip drawableRecipientChip2 = this.mSelectedChip;
                if (drawableRecipientChip2 != null && shouldShowEditableText(drawableRecipientChip2)) {
                    z = true;
                }
            }
        }
        if (action == 1 && !z) {
            clearSelectedChip();
        }
        return zOnTouchEvent;
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [com.android.ex.chips.RecipientEditTextView$10] */
    private void showAlternates(final DrawableRecipientChip drawableRecipientChip, final ListPopupWindow listPopupWindow) {
        AsyncTask<Void, Void, ListAdapter> r0 = new AsyncTask<Void, Void, ListAdapter>() { // from class: com.android.ex.chips.RecipientEditTextView.10
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public ListAdapter doInBackground(Void... voidArr) {
                return RecipientEditTextView.this.createAlternatesAdapter(drawableRecipientChip);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(ListAdapter listAdapter) {
                if (RecipientEditTextView.this.mAttachedToWindow) {
                    int iCalculateOffsetFromBottomToTop = RecipientEditTextView.this.calculateOffsetFromBottomToTop(RecipientEditTextView.this.getLayout().getLineForOffset(RecipientEditTextView.this.getChipStart(drawableRecipientChip)));
                    listPopupWindow.setAnchorView(RecipientEditTextView.this.mAlternatePopupAnchor != null ? RecipientEditTextView.this.mAlternatePopupAnchor : RecipientEditTextView.this);
                    listPopupWindow.setVerticalOffset(iCalculateOffsetFromBottomToTop);
                    listPopupWindow.setAdapter(listAdapter);
                    listPopupWindow.setOnItemClickListener(RecipientEditTextView.this.mAlternatesListener);
                    RecipientEditTextView.this.mCheckedItem = -1;
                    listPopupWindow.show();
                    ListView listView = listPopupWindow.getListView();
                    listView.setChoiceMode(1);
                    if (RecipientEditTextView.this.mCheckedItem != -1) {
                        listView.setItemChecked(RecipientEditTextView.this.mCheckedItem, true);
                        RecipientEditTextView.this.mCheckedItem = -1;
                    }
                }
            }
        };
        r0.execute((Void[]) null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ListAdapter createAlternatesAdapter(DrawableRecipientChip drawableRecipientChip) {
        return new RecipientAlternatesAdapter(getContext(), drawableRecipientChip.getContactId(), drawableRecipientChip.getDirectoryId(), drawableRecipientChip.getLookupKey(), drawableRecipientChip.getDataId(), getAdapter().getQueryType(), this, this.mDropdownChipLayouter, constructStateListDeleteDrawable());
    }

    private ListAdapter createSingleAddressAdapter(DrawableRecipientChip drawableRecipientChip) {
        return new SingleRecipientArrayAdapter(getContext(), drawableRecipientChip.getEntry(), this.mDropdownChipLayouter, constructStateListDeleteDrawable());
    }

    private StateListDrawable constructStateListDeleteDrawable() {
        final StateListDrawable stateListDrawable = new StateListDrawable();
        if (!this.mDisableDelete) {
            this.mHandler.post(new Runnable() { // from class: com.android.ex.chips.RecipientEditTextView.11
                @Override // java.lang.Runnable
                public void run() {
                    stateListDrawable.addState(new int[]{android.R.attr.state_activated}, RecipientEditTextView.this.mChipDelete);
                }
            });
        }
        stateListDrawable.addState(new int[0], null);
        return stateListDrawable;
    }

    @Override // com.android.ex.chips.RecipientAlternatesAdapter.OnCheckedItemChangedListener
    public void onCheckedItemChanged(int i) {
        ListView listView = this.mAlternatesPopup.getListView();
        if (listView != null && listView.getCheckedItemCount() == 0) {
            listView.setItemChecked(i, true);
        }
        this.mCheckedItem = i;
    }

    private int putOffsetInRange(float f, float f2) {
        int iSupportGetOffsetForPosition;
        if (Build.VERSION.SDK_INT >= 14) {
            iSupportGetOffsetForPosition = getOffsetForPosition(f, f2);
        } else {
            iSupportGetOffsetForPosition = supportGetOffsetForPosition(f, f2);
        }
        return putOffsetInRange(iSupportGetOffsetForPosition);
    }

    private int putOffsetInRange(int i) {
        Editable text = getText();
        int length = text.length();
        for (int i2 = length - 1; i2 >= 0 && text.charAt(i2) == ' '; i2--) {
            length--;
        }
        if (i >= length) {
            return i;
        }
        Editable text2 = getText();
        while (i >= 0 && findText(text2, i) == -1 && findChip(i) == null) {
            i--;
        }
        return i;
    }

    private static int findText(Editable editable, int i) {
        if (editable.charAt(i) != ' ') {
            return i;
        }
        return -1;
    }

    private DrawableRecipientChip findChip(int i) {
        Spannable spannable = getSpannable();
        for (DrawableRecipientChip drawableRecipientChip : (DrawableRecipientChip[]) spannable.getSpans(0, spannable.length(), DrawableRecipientChip.class)) {
            int chipStart = getChipStart(drawableRecipientChip);
            int chipEnd = getChipEnd(drawableRecipientChip);
            if (i >= chipStart && i <= chipEnd) {
                return drawableRecipientChip;
            }
        }
        return null;
    }

    String createAddressText(RecipientEntry recipientEntry) {
        String strTrim;
        Rfc822Token[] rfc822TokenArr;
        String displayName = recipientEntry.getDisplayName();
        String destination = recipientEntry.getDestination();
        if (TextUtils.isEmpty(displayName) || TextUtils.equals(displayName, destination)) {
            displayName = null;
        }
        if (isPhoneQuery() && isPhoneNumber(destination)) {
            strTrim = destination.trim();
        } else {
            if (destination != null && (rfc822TokenArr = Rfc822Tokenizer.tokenize(destination)) != null && rfc822TokenArr.length > 0) {
                destination = rfc822TokenArr[0].getAddress();
            }
            strTrim = new Rfc822Token(displayName, destination, null).toString().trim();
        }
        return (this.mTokenizer == null || TextUtils.isEmpty(strTrim) || strTrim.indexOf(RecurrenceRuleParser.VALUE_SEPARATOR) >= strTrim.length() + (-1)) ? strTrim : (String) this.mTokenizer.terminateToken(strTrim);
    }

    String createChipDisplayText(RecipientEntry recipientEntry) {
        String displayName = recipientEntry.getDisplayName();
        String destination = recipientEntry.getDestination();
        if (TextUtils.isEmpty(displayName) || TextUtils.equals(displayName, destination)) {
            displayName = null;
        }
        if (TextUtils.isEmpty(displayName)) {
            return !TextUtils.isEmpty(destination) ? destination : new Rfc822Token(displayName, destination, null).toString();
        }
        return displayName;
    }

    private CharSequence createChip(RecipientEntry recipientEntry) {
        String strCreateAddressText = createAddressText(recipientEntry);
        if (TextUtils.isEmpty(strCreateAddressText)) {
            return null;
        }
        int length = strCreateAddressText.length() - 1;
        SpannableString spannableString = new SpannableString(strCreateAddressText);
        if (!this.mNoChips) {
            try {
                DrawableRecipientChip drawableRecipientChipConstructChipSpan = constructChipSpan(recipientEntry);
                spannableString.setSpan(drawableRecipientChipConstructChipSpan, 0, length, 33);
                drawableRecipientChipConstructChipSpan.setOriginalText(spannableString.toString());
            } catch (NullPointerException e) {
                Log.e(TAG, e.getMessage(), e);
                return null;
            }
        }
        onChipCreated(recipientEntry);
        return spannableString;
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        int iSubmitItemAtPosition;
        RecipientEntryItemClickedListener recipientEntryItemClickedListener;
        if (i >= 0 && (iSubmitItemAtPosition = submitItemAtPosition(i)) > -1 && (recipientEntryItemClickedListener = this.mRecipientEntryItemClickedListener) != null) {
            recipientEntryItemClickedListener.onRecipientEntryItemClicked(iSubmitItemAtPosition, i);
        }
    }

    private int submitItemAtPosition(int i) {
        RecipientEntry recipientEntryCreateValidatedEntry = createValidatedEntry(getAdapter().getItem(i));
        if (recipientEntryCreateValidatedEntry == null) {
            return -1;
        }
        clearComposingText();
        int selectionEnd = getSelectionEnd();
        int iFindTokenStart = this.mTokenizer.findTokenStart(getText(), selectionEnd);
        Editable text = getText();
        QwertyKeyListener.markAsReplaced(text, iFindTokenStart, selectionEnd, "");
        CharSequence charSequenceCreateChip = createChip(recipientEntryCreateValidatedEntry);
        if (charSequenceCreateChip != null && iFindTokenStart >= 0 && selectionEnd >= 0) {
            text.replace(iFindTokenStart, selectionEnd, charSequenceCreateChip);
        }
        sanitizeBetween();
        return selectionEnd - iFindTokenStart;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public RecipientEntry createValidatedEntry(RecipientEntry recipientEntry) {
        AutoCompleteTextView.Validator validator;
        if (recipientEntry == null) {
            return null;
        }
        String destination = recipientEntry.getDestination();
        if (!isPhoneQuery() && recipientEntry.getContactId() == -2) {
            return RecipientEntry.constructGeneratedEntry(recipientEntry.getDisplayName(), destination, recipientEntry.isValid());
        }
        if (RecipientEntry.isCreatedRecipient(recipientEntry.getContactId())) {
            return (TextUtils.isEmpty(recipientEntry.getDisplayName()) || TextUtils.equals(recipientEntry.getDisplayName(), destination) || !((validator = this.mValidator) == null || validator.isValid(destination))) ? RecipientEntry.constructFakeEntry(destination, recipientEntry.isValid()) : recipientEntry;
        }
        return recipientEntry;
    }

    DrawableRecipientChip[] getSortedRecipients() {
        ArrayList arrayList = new ArrayList(Arrays.asList((DrawableRecipientChip[]) getSpannable().getSpans(0, getText().length(), DrawableRecipientChip.class)));
        final Spannable spannable = getSpannable();
        Collections.sort(arrayList, new Comparator<DrawableRecipientChip>() { // from class: com.android.ex.chips.RecipientEditTextView.12
            @Override // java.util.Comparator
            public int compare(DrawableRecipientChip drawableRecipientChip, DrawableRecipientChip drawableRecipientChip2) {
                int spanStart = spannable.getSpanStart(drawableRecipientChip);
                int spanStart2 = spannable.getSpanStart(drawableRecipientChip2);
                if (spanStart < spanStart2) {
                    return -1;
                }
                return spanStart > spanStart2 ? 1 : 0;
            }
        });
        return (DrawableRecipientChip[]) arrayList.toArray(new DrawableRecipientChip[arrayList.size()]);
    }

    ReplacementDrawableSpan getMoreChip() {
        MoreImageSpan[] moreImageSpanArr = (MoreImageSpan[]) getSpannable().getSpans(0, getText().length(), MoreImageSpan.class);
        if (moreImageSpanArr == null || moreImageSpanArr.length <= 0) {
            return null;
        }
        return moreImageSpanArr[0];
    }

    private MoreImageSpan createMoreSpan(int i) {
        String str = String.format(this.mMoreItem.getText().toString(), Integer.valueOf(i));
        this.mWorkPaint.set(getPaint());
        this.mWorkPaint.setTextSize(this.mMoreItem.getTextSize());
        this.mWorkPaint.setColor(this.mMoreItem.getCurrentTextColor());
        int iMeasureText = ((int) this.mWorkPaint.measureText(str)) + this.mMoreItem.getPaddingLeft() + this.mMoreItem.getPaddingRight();
        int i2 = (int) this.mChipHeight;
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(iMeasureText, i2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        Layout layout = getLayout();
        canvas.drawText(str, 0, str.length(), 0.0f, layout != null ? i2 - layout.getLineDescent(0) : i2, this.mWorkPaint);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmapCreateBitmap);
        bitmapDrawable.setBounds(0, 0, iMeasureText, i2);
        return new MoreImageSpan(bitmapDrawable);
    }

    void createMoreChipPlainText() {
        Editable text = getText();
        int iMovePastTerminators = 0;
        for (int i = 0; i < 2; i++) {
            iMovePastTerminators = movePastTerminators(this.mTokenizer.findTokenEnd(text, iMovePastTerminators));
        }
        MoreImageSpan moreImageSpanCreateMoreSpan = createMoreSpan(countTokens(text) - 2);
        SpannableString spannableString = new SpannableString(text.subSequence(iMovePastTerminators, text.length()));
        spannableString.setSpan(moreImageSpanCreateMoreSpan, 0, spannableString.length(), 33);
        text.replace(iMovePastTerminators, text.length(), spannableString);
        this.mMoreChip = moreImageSpanCreateMoreSpan;
    }

    int countTokens(Editable editable) {
        int iMovePastTerminators = 0;
        int i = 0;
        while (iMovePastTerminators < editable.length()) {
            iMovePastTerminators = movePastTerminators(this.mTokenizer.findTokenEnd(editable, iMovePastTerminators));
            i++;
            if (iMovePastTerminators >= editable.length()) {
                break;
            }
        }
        return i;
    }

    void createMoreChip() {
        if (this.mNoChips) {
            createMoreChipPlainText();
            return;
        }
        if (this.mShouldShrink) {
            ReplacementDrawableSpan[] replacementDrawableSpanArr = (ReplacementDrawableSpan[]) getSpannable().getSpans(0, getText().length(), MoreImageSpan.class);
            if (replacementDrawableSpanArr.length > 0) {
                getSpannable().removeSpan(replacementDrawableSpanArr[0]);
            }
            DrawableRecipientChip[] sortedRecipients = getSortedRecipients();
            if (sortedRecipients == null || sortedRecipients.length <= 2) {
                this.mMoreChip = null;
                return;
            }
            Spannable spannable = getSpannable();
            int length = sortedRecipients.length;
            int i = length - 2;
            MoreImageSpan moreImageSpanCreateMoreSpan = createMoreSpan(i);
            this.mRemovedSpans = new ArrayList<>();
            Editable text = getText();
            int i2 = length - i;
            int length2 = 0;
            int spanStart = 0;
            for (int i3 = i2; i3 < sortedRecipients.length; i3++) {
                this.mRemovedSpans.add(sortedRecipients[i3]);
                if (i3 == i2) {
                    spanStart = spannable.getSpanStart(sortedRecipients[i3]);
                }
                if (i3 == sortedRecipients.length - 1) {
                    length2 = spannable.getSpanEnd(sortedRecipients[i3]);
                }
                ArrayList<DrawableRecipientChip> arrayList = this.mTemporaryRecipients;
                if (arrayList == null || !arrayList.contains(sortedRecipients[i3])) {
                    sortedRecipients[i3].setOriginalText(text.toString().substring(spannable.getSpanStart(sortedRecipients[i3]), spannable.getSpanEnd(sortedRecipients[i3])));
                }
                spannable.removeSpan(sortedRecipients[i3]);
            }
            if (length2 < text.length()) {
                length2 = text.length();
            }
            int iMax = Math.max(spanStart, length2);
            int iMin = Math.min(spanStart, length2);
            SpannableString spannableString = new SpannableString(text.subSequence(iMin, iMax));
            spannableString.setSpan(moreImageSpanCreateMoreSpan, 0, spannableString.length(), 33);
            text.replace(iMin, iMax, spannableString);
            this.mMoreChip = moreImageSpanCreateMoreSpan;
            if (isPhoneQuery() || getLineCount() <= this.mMaxLines) {
                return;
            }
            setMaxLines(getLineCount());
        }
    }

    void removeMoreChip() {
        DrawableRecipientChip[] sortedRecipients;
        if (this.mMoreChip != null) {
            Spannable spannable = getSpannable();
            spannable.removeSpan(this.mMoreChip);
            this.mMoreChip = null;
            ArrayList<DrawableRecipientChip> arrayList = this.mRemovedSpans;
            if (arrayList == null || arrayList.size() <= 0 || (sortedRecipients = getSortedRecipients()) == null || sortedRecipients.length == 0) {
                return;
            }
            new RecipientReplacementTask().execute(new Void[0]);
            int spanEnd = spannable.getSpanEnd(sortedRecipients[sortedRecipients.length - 1]);
            Editable text = getText();
            for (DrawableRecipientChip drawableRecipientChip : this.mRemovedSpans) {
                String str = (String) drawableRecipientChip.getOriginalText();
                int iIndexOf = text.toString().indexOf(str, spanEnd);
                int iMin = Math.min(text.length(), str.length() + iIndexOf);
                if (iIndexOf != -1) {
                    text.setSpan(drawableRecipientChip, iIndexOf, iMin, 33);
                }
                spanEnd = iMin;
            }
            this.mRemovedSpans.clear();
        }
    }

    private void selectChip(DrawableRecipientChip drawableRecipientChip) {
        boolean z = true;
        if (shouldShowEditableText(drawableRecipientChip)) {
            CharSequence value = drawableRecipientChip.getValue();
            Editable text = getText();
            Spannable spannable = getSpannable();
            int spanStart = spannable.getSpanStart(drawableRecipientChip);
            int spanEnd = spannable.getSpanEnd(drawableRecipientChip);
            spannable.removeSpan(drawableRecipientChip);
            if (spanEnd - spanStart == text.length() - 1) {
                spanEnd++;
            }
            text.delete(spanStart, spanEnd);
            setCursorVisible(true);
            setSelection(text.length());
            text.append(value);
            this.mSelectedChip = constructChipSpan(RecipientEntry.constructFakeEntry((String) value, isValid(value.toString())));
            return;
        }
        if (drawableRecipientChip.getContactId() != -2 && !getAdapter().forceShowAddress()) {
            z = false;
        }
        if (z && this.mNoChips) {
            return;
        }
        this.mSelectedChip = drawableRecipientChip;
        setSelection(getText().getSpanEnd(this.mSelectedChip));
        setCursorVisible(false);
        if (z) {
            showAddress(drawableRecipientChip, this.mAddressPopup);
        } else {
            showAlternates(drawableRecipientChip, this.mAlternatesPopup);
        }
    }

    private boolean shouldShowEditableText(DrawableRecipientChip drawableRecipientChip) {
        long contactId = drawableRecipientChip.getContactId();
        return contactId == -1 || (!isPhoneQuery() && contactId == -2);
    }

    private void showAddress(final DrawableRecipientChip drawableRecipientChip, final ListPopupWindow listPopupWindow) {
        if (this.mAttachedToWindow) {
            int iCalculateOffsetFromBottomToTop = calculateOffsetFromBottomToTop(getLayout().getLineForOffset(getChipStart(drawableRecipientChip)));
            View view = this.mAlternatePopupAnchor;
            if (view == null) {
                view = this;
            }
            listPopupWindow.setAnchorView(view);
            listPopupWindow.setVerticalOffset(iCalculateOffsetFromBottomToTop);
            listPopupWindow.setAdapter(createSingleAddressAdapter(drawableRecipientChip));
            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.android.ex.chips.RecipientEditTextView.13
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView<?> adapterView, View view2, int i, long j) {
                    RecipientEditTextView.this.unselectChip(drawableRecipientChip);
                    listPopupWindow.dismiss();
                }
            });
            listPopupWindow.show();
            ListView listView = listPopupWindow.getListView();
            listView.setChoiceMode(1);
            listView.setItemChecked(0, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unselectChip(DrawableRecipientChip drawableRecipientChip) {
        int chipStart = getChipStart(drawableRecipientChip);
        int chipEnd = getChipEnd(drawableRecipientChip);
        Editable text = getText();
        this.mSelectedChip = null;
        if (chipStart == -1 || chipEnd == -1) {
            Log.w(TAG, "The chip doesn't exist or may be a chip a user was editing");
            setSelection(text.length());
            commitDefault();
        } else {
            getSpannable().removeSpan(drawableRecipientChip);
            QwertyKeyListener.markAsReplaced(text, chipStart, chipEnd, "");
            text.removeSpan(drawableRecipientChip);
            try {
                if (!this.mNoChips) {
                    text.setSpan(constructChipSpan(drawableRecipientChip.getEntry()), chipStart, chipEnd, 33);
                }
            } catch (NullPointerException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        setCursorVisible(true);
        setSelection(text.length());
        ListPopupWindow listPopupWindow = this.mAlternatesPopup;
        if (listPopupWindow == null || !listPopupWindow.isShowing()) {
            return;
        }
        this.mAlternatesPopup.dismiss();
    }

    @Override // com.android.ex.chips.DropdownChipLayouter.ChipDeleteListener
    public void onChipDelete() {
        DrawableRecipientChip drawableRecipientChip = this.mSelectedChip;
        if (drawableRecipientChip != null) {
            removeChip(drawableRecipientChip);
        }
        dismissPopups();
    }

    private void dismissPopups() {
        ListPopupWindow listPopupWindow = this.mAlternatesPopup;
        if (listPopupWindow != null && listPopupWindow.isShowing()) {
            this.mAlternatesPopup.dismiss();
        }
        ListPopupWindow listPopupWindow2 = this.mAddressPopup;
        if (listPopupWindow2 != null && listPopupWindow2.isShowing()) {
            this.mAddressPopup.dismiss();
        }
        setSelection(getText().length());
    }

    void removeChip(DrawableRecipientChip drawableRecipientChip) {
        Spannable spannable = getSpannable();
        int spanStart = spannable.getSpanStart(drawableRecipientChip);
        int spanEnd = spannable.getSpanEnd(drawableRecipientChip);
        Editable text = getText();
        boolean z = drawableRecipientChip == this.mSelectedChip;
        if (z) {
            this.mSelectedChip = null;
        }
        while (spanEnd >= 0 && spanEnd < text.length() && text.charAt(spanEnd) == ' ') {
            spanEnd++;
        }
        spannable.removeSpan(drawableRecipientChip);
        if (spanStart >= 0 && spanEnd > 0) {
            text.delete(spanStart, spanEnd);
        }
        if (z) {
            clearSelectedChip();
        }
    }

    void replaceChip(DrawableRecipientChip drawableRecipientChip, RecipientEntry recipientEntry) {
        boolean z = drawableRecipientChip == this.mSelectedChip;
        if (z) {
            this.mSelectedChip = null;
        }
        int chipStart = getChipStart(drawableRecipientChip);
        int chipEnd = getChipEnd(drawableRecipientChip);
        getSpannable().removeSpan(drawableRecipientChip);
        Editable text = getText();
        CharSequence charSequenceCreateChip = createChip(recipientEntry);
        if (charSequenceCreateChip != null) {
            if (chipStart == -1 || chipEnd == -1) {
                Log.e(TAG, "The chip to replace does not exist but should.");
                text.insert(0, charSequenceCreateChip);
            } else if (!TextUtils.isEmpty(charSequenceCreateChip)) {
                while (chipEnd >= 0 && chipEnd < text.length() && text.charAt(chipEnd) == ' ') {
                    chipEnd++;
                }
                text.replace(chipStart, chipEnd, charSequenceCreateChip);
            }
        }
        setCursorVisible(true);
        if (z) {
            clearSelectedChip();
        }
    }

    public void onClick(DrawableRecipientChip drawableRecipientChip) {
        if (drawableRecipientChip.isSelected()) {
            clearSelectedChip();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean chipsPending() {
        ArrayList<DrawableRecipientChip> arrayList;
        return this.mPendingChipsCount > 0 || ((arrayList = this.mRemovedSpans) != null && arrayList.size() > 0);
    }

    @Override // android.widget.TextView
    public void removeTextChangedListener(TextWatcher textWatcher) {
        this.mTextWatcher = null;
        super.removeTextChangedListener(textWatcher);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isValidEmailAddress(String str) {
        AutoCompleteTextView.Validator validator;
        return (TextUtils.isEmpty(str) || (validator = this.mValidator) == null || !validator.isValid(str)) ? false : true;
    }

    private class RecipientTextWatcher implements TextWatcher {
        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        private RecipientTextWatcher() {
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            char cCharAt;
            int selectionEnd;
            RecipientEditTextView.this.clearSearchProgress();
            if (!TextUtils.isEmpty(editable)) {
                if (RecipientEditTextView.this.chipsPending()) {
                    return;
                }
                if (RecipientEditTextView.this.mSelectedChip != null) {
                    RecipientEditTextView recipientEditTextView = RecipientEditTextView.this;
                    if (recipientEditTextView.isGeneratedContact(recipientEditTextView.mSelectedChip)) {
                        return;
                    }
                    RecipientEditTextView.this.setCursorVisible(true);
                    RecipientEditTextView recipientEditTextView2 = RecipientEditTextView.this;
                    recipientEditTextView2.setSelection(recipientEditTextView2.getText().length());
                    RecipientEditTextView.this.clearSelectedChip();
                }
                if (editable.length() > 1) {
                    if (RecipientEditTextView.this.lastCharacterIsCommitCharacter(editable)) {
                        RecipientEditTextView.this.commitByCharacter();
                        return;
                    }
                    selectionEnd = RecipientEditTextView.this.getSelectionEnd() > 0 ? RecipientEditTextView.this.getSelectionEnd() - 1 : 0;
                    int length = RecipientEditTextView.this.length() - 1;
                    if (selectionEnd != length) {
                        cCharAt = editable.charAt(selectionEnd);
                    } else {
                        cCharAt = editable.charAt(length);
                    }
                    if (cCharAt != ' ' || RecipientEditTextView.this.isPhoneQuery()) {
                        return;
                    }
                    String string = RecipientEditTextView.this.getText().toString();
                    int iFindTokenStart = RecipientEditTextView.this.mTokenizer.findTokenStart(string, RecipientEditTextView.this.getSelectionEnd());
                    if (RecipientEditTextView.this.isValidEmailAddress(string.substring(iFindTokenStart, RecipientEditTextView.this.mTokenizer.findTokenEnd(string, iFindTokenStart)))) {
                        RecipientEditTextView.this.commitByCharacter();
                        return;
                    }
                    return;
                }
                return;
            }
            Spannable spannable = RecipientEditTextView.this.getSpannable();
            for (DrawableRecipientChip drawableRecipientChip : (DrawableRecipientChip[]) spannable.getSpans(0, RecipientEditTextView.this.getText().length(), DrawableRecipientChip.class)) {
                spannable.removeSpan(drawableRecipientChip);
            }
            if (RecipientEditTextView.this.mMoreChip != null) {
                spannable.removeSpan(RecipientEditTextView.this.mMoreChip);
            }
            RecipientEditTextView.this.clearSelectedChip();
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (i2 - i3 != 1) {
                if (i3 <= i2 || RecipientEditTextView.this.mSelectedChip == null) {
                    return;
                }
                RecipientEditTextView recipientEditTextView = RecipientEditTextView.this;
                if (recipientEditTextView.isGeneratedContact(recipientEditTextView.mSelectedChip) && RecipientEditTextView.this.lastCharacterIsCommitCharacter(charSequence)) {
                    RecipientEditTextView.this.commitByCharacter();
                    return;
                }
                return;
            }
            int selectionStart = RecipientEditTextView.this.getSelectionStart();
            DrawableRecipientChip[] drawableRecipientChipArr = (DrawableRecipientChip[]) RecipientEditTextView.this.getSpannable().getSpans(selectionStart, selectionStart, DrawableRecipientChip.class);
            if (drawableRecipientChipArr.length > 0) {
                DrawableRecipientChip drawableRecipientChip = drawableRecipientChipArr[0];
                Editable text = RecipientEditTextView.this.getText();
                int spanStart = text.getSpanStart(drawableRecipientChip);
                int spanEnd = text.getSpanEnd(drawableRecipientChip) + 1;
                if (spanEnd > text.length()) {
                    spanEnd = text.length();
                }
                text.removeSpan(drawableRecipientChip);
                text.delete(spanStart, spanEnd);
            }
        }
    }

    public boolean lastCharacterIsCommitCharacter(CharSequence charSequence) {
        char cCharAt;
        int selectionEnd = getSelectionEnd() <= 0 ? 0 : getSelectionEnd() - 1;
        int length = length() - 1;
        if (selectionEnd != length) {
            cCharAt = charSequence.charAt(selectionEnd);
        } else {
            cCharAt = charSequence.charAt(length);
        }
        return cCharAt == ',' || cCharAt == ';';
    }

    public boolean isGeneratedContact(DrawableRecipientChip drawableRecipientChip) {
        long contactId = drawableRecipientChip.getContactId();
        return contactId == -1 || (!isPhoneQuery() && contactId == -2);
    }

    void handlePasteClip(ClipData clipData) {
        if (clipData == null) {
            return;
        }
        ClipDescription description = clipData.getDescription();
        if (description.hasMimeType("text/plain") || description.hasMimeType("text/html")) {
            removeTextChangedListener(this.mTextWatcher);
            ClipDescription description2 = clipData.getDescription();
            for (int i = 0; i < clipData.getItemCount(); i++) {
                String mimeType = description2.getMimeType(i);
                if ("text/plain".equals(mimeType) || "text/html".equals(mimeType)) {
                    CharSequence text = clipData.getItemAt(i).getText();
                    if (!TextUtils.isEmpty(text)) {
                        Editable text2 = getText();
                        int selectionStart = getSelectionStart();
                        int selectionEnd = getSelectionEnd();
                        if (selectionStart < 0 || selectionEnd < 1) {
                            text2.append(text);
                        } else if (selectionStart == selectionEnd) {
                            text2.insert(selectionStart, text);
                        } else {
                            text2.append(text, selectionStart, selectionEnd);
                        }
                        handlePasteAndReplace();
                    }
                }
            }
            this.mHandler.post(this.mAddTextWatcher);
        }
    }

    @Override // android.widget.EditText, android.widget.TextView
    public boolean onTextContextMenuItem(int i) {
        if (i == 16908322) {
            handlePasteClip(((ClipboardManager) getContext().getSystemService("clipboard")).getPrimaryClip());
            return true;
        }
        return super.onTextContextMenuItem(i);
    }

    private void handlePasteAndReplace() {
        ArrayList<DrawableRecipientChip> arrayListHandlePaste = handlePaste();
        if (arrayListHandlePaste == null || arrayListHandlePaste.size() <= 0) {
            return;
        }
        new IndividualReplacementTask().execute(arrayListHandlePaste);
    }

    ArrayList<DrawableRecipientChip> handlePaste() {
        String string = getText().toString();
        int iFindTokenStart = this.mTokenizer.findTokenStart(string, getSelectionEnd());
        String strSubstring = string.substring(iFindTokenStart);
        ArrayList<DrawableRecipientChip> arrayList = new ArrayList<>();
        if (iFindTokenStart != 0) {
            DrawableRecipientChip drawableRecipientChipFindChip = null;
            int i = 0;
            int spanEnd = iFindTokenStart;
            while (spanEnd != 0 && drawableRecipientChipFindChip == null && spanEnd != i) {
                int iFindTokenStart2 = this.mTokenizer.findTokenStart(string, spanEnd);
                drawableRecipientChipFindChip = findChip(iFindTokenStart2);
                if (iFindTokenStart2 == iFindTokenStart && drawableRecipientChipFindChip == null) {
                    i = spanEnd;
                    spanEnd = iFindTokenStart2;
                    break;
                }
                i = spanEnd;
                spanEnd = iFindTokenStart2;
            }
            if (spanEnd != iFindTokenStart) {
                if (drawableRecipientChipFindChip != null) {
                    spanEnd = i;
                }
                while (spanEnd < iFindTokenStart) {
                    commitChip(spanEnd, movePastTerminators(this.mTokenizer.findTokenEnd(getText().toString(), spanEnd)), getText());
                    DrawableRecipientChip drawableRecipientChipFindChip2 = findChip(spanEnd);
                    if (drawableRecipientChipFindChip2 == null) {
                        break;
                    }
                    spanEnd = getSpannable().getSpanEnd(drawableRecipientChipFindChip2) + 1;
                    arrayList.add(drawableRecipientChipFindChip2);
                }
            }
        }
        if (isCompletedToken(strSubstring)) {
            Editable text = getText();
            int iIndexOf = text.toString().indexOf(strSubstring, iFindTokenStart);
            commitChip(iIndexOf, text.length(), text);
            arrayList.add(findChip(iIndexOf));
        }
        return arrayList;
    }

    int movePastTerminators(int i) {
        if (i >= length()) {
            return i;
        }
        char cCharAt = getText().toString().charAt(i);
        if (cCharAt == ',' || cCharAt == ';') {
            i++;
        }
        return (i >= length() || getText().toString().charAt(i) != ' ') ? i : i + 1;
    }

    private class RecipientReplacementTask extends AsyncTask<Void, Void, Void> {
        private RecipientReplacementTask() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public DrawableRecipientChip createFreeChip(RecipientEntry recipientEntry) {
            try {
                if (RecipientEditTextView.this.mNoChips) {
                    return null;
                }
                return RecipientEditTextView.this.constructChipSpan(recipientEntry);
            } catch (NullPointerException e) {
                Log.e(RecipientEditTextView.TAG, e.getMessage(), e);
                return null;
            }
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            ArrayList<DrawableRecipientChip> arrayList = new ArrayList<>();
            Collections.addAll(arrayList, RecipientEditTextView.this.getSortedRecipients());
            if (RecipientEditTextView.this.mRemovedSpans != null) {
                arrayList.addAll(RecipientEditTextView.this.mRemovedSpans);
            }
            ArrayList<DrawableRecipientChip> arrayList2 = new ArrayList<>(arrayList.size());
            for (DrawableRecipientChip drawableRecipientChip : arrayList) {
                if (RecipientEntry.isCreatedRecipient(drawableRecipientChip.getEntry().getContactId()) && RecipientEditTextView.this.getSpannable().getSpanStart(drawableRecipientChip) != -1) {
                    arrayList2.add(createFreeChip(drawableRecipientChip.getEntry()));
                } else {
                    arrayList2.add(null);
                }
            }
            processReplacements(arrayList, arrayList2);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            if (RecipientEditTextView.this.mIndividualReplacements != null) {
                RecipientEditTextView.this.mIndividualReplacements.cancel(true);
            }
            final ArrayList<DrawableRecipientChip> arrayList = new ArrayList();
            Collections.addAll(arrayList, RecipientEditTextView.this.getSortedRecipients());
            if (RecipientEditTextView.this.mRemovedSpans != null) {
                arrayList.addAll(RecipientEditTextView.this.mRemovedSpans);
            }
            ArrayList<String> arrayList2 = new ArrayList<>();
            for (DrawableRecipientChip drawableRecipientChip : arrayList) {
                if (drawableRecipientChip != null) {
                    arrayList2.add(RecipientEditTextView.this.createAddressText(drawableRecipientChip.getEntry()));
                }
            }
            BaseRecipientAdapter adapter = RecipientEditTextView.this.getAdapter();
            if (adapter != null) {
                adapter.getMatchingRecipients(arrayList2, new RecipientAlternatesAdapter.RecipientMatchCallback() { // from class: com.android.ex.chips.RecipientEditTextView.RecipientReplacementTask.1
                    @Override // com.android.ex.chips.RecipientAlternatesAdapter.RecipientMatchCallback
                    public void matchesFound(Map<String, RecipientEntry> map) {
                        ArrayList arrayList3 = new ArrayList();
                        for (DrawableRecipientChip drawableRecipientChip2 : arrayList) {
                            RecipientEntry recipientEntryCreateValidatedEntry = (drawableRecipientChip2 == null || !RecipientEntry.isCreatedRecipient(drawableRecipientChip2.getEntry().getContactId()) || RecipientEditTextView.this.getSpannable().getSpanStart(drawableRecipientChip2) == -1) ? null : RecipientEditTextView.this.createValidatedEntry(map.get(RecipientEditTextView.tokenizeAddress(drawableRecipientChip2.getEntry().getDestination())));
                            if (recipientEntryCreateValidatedEntry != null) {
                                arrayList3.add(RecipientReplacementTask.this.createFreeChip(recipientEntryCreateValidatedEntry));
                            } else {
                                arrayList3.add(null);
                            }
                        }
                        RecipientReplacementTask.this.processReplacements(arrayList, arrayList3);
                    }

                    @Override // com.android.ex.chips.RecipientAlternatesAdapter.RecipientMatchCallback
                    public void matchesNotFound(Set<String> set) {
                        ArrayList arrayList3 = new ArrayList(set.size());
                        for (DrawableRecipientChip drawableRecipientChip2 : arrayList) {
                            int spanStart = RecipientEditTextView.this.getSpannable().getSpanStart(drawableRecipientChip2);
                            if (drawableRecipientChip2 != null && RecipientEntry.isCreatedRecipient(drawableRecipientChip2.getEntry().getContactId()) && spanStart != -1) {
                                if (set.contains(drawableRecipientChip2.getEntry().getDestination())) {
                                    arrayList3.add(RecipientReplacementTask.this.createFreeChip(drawableRecipientChip2.getEntry()));
                                } else {
                                    arrayList3.add(null);
                                }
                            } else {
                                arrayList3.add(null);
                            }
                        }
                        RecipientReplacementTask.this.processReplacements(arrayList, arrayList3);
                    }
                });
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void processReplacements(final List<DrawableRecipientChip> list, final List<DrawableRecipientChip> list2) {
            if (list2 == null || list2.size() <= 0) {
                return;
            }
            Runnable runnable = new Runnable() { // from class: com.android.ex.chips.RecipientEditTextView.RecipientReplacementTask.2
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
                @Override // java.lang.Runnable
                public void run() {
                    int spanStart;
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(RecipientEditTextView.this.getText());
                    int i = 0;
                    for (DrawableRecipientChip drawableRecipientChip : list) {
                        DrawableRecipientChip drawableRecipientChip2 = (DrawableRecipientChip) list2.get(i);
                        if (drawableRecipientChip2 != null) {
                            RecipientEntry entry = drawableRecipientChip.getEntry();
                            RecipientEntry entry2 = drawableRecipientChip2.getEntry();
                            if ((RecipientAlternatesAdapter.getBetterRecipient(entry, entry2) == entry2) && (spanStart = spannableStringBuilder.getSpanStart(drawableRecipientChip)) != -1) {
                                int iMin = Math.min(spannableStringBuilder.getSpanEnd(drawableRecipientChip) + 1, spannableStringBuilder.length());
                                spannableStringBuilder.removeSpan(drawableRecipientChip);
                                SpannableString spannableString = new SpannableString(RecipientEditTextView.this.createAddressText(drawableRecipientChip2.getEntry()).trim() + " ");
                                spannableString.setSpan(drawableRecipientChip2, 0, spannableString.length() - 1, 33);
                                spannableStringBuilder.replace(spanStart, Math.min(iMin + 1, spannableStringBuilder.length()), (CharSequence) spannableString);
                                drawableRecipientChip2.setOriginalText(spannableString.toString());
                                list2.set(i, null);
                                list.set(i, drawableRecipientChip2);
                            }
                        }
                        i++;
                    }
                    RecipientEditTextView.this.setText(spannableStringBuilder);
                }
            };
            if (Looper.myLooper() != Looper.getMainLooper()) {
                RecipientEditTextView.this.mHandler.post(runnable);
            } else {
                runnable.run();
            }
        }
    }

    private class IndividualReplacementTask extends AsyncTask<ArrayList<DrawableRecipientChip>, Void, Void> {
        private IndividualReplacementTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(ArrayList<DrawableRecipientChip>... arrayListArr) {
            final ArrayList<DrawableRecipientChip> arrayList = arrayListArr[0];
            ArrayList<String> arrayList2 = new ArrayList<>();
            for (DrawableRecipientChip drawableRecipientChip : arrayList) {
                if (drawableRecipientChip != null) {
                    arrayList2.add(RecipientEditTextView.this.createAddressText(drawableRecipientChip.getEntry()));
                }
            }
            RecipientEditTextView.this.getAdapter().getMatchingRecipients(arrayList2, new RecipientAlternatesAdapter.RecipientMatchCallback() { // from class: com.android.ex.chips.RecipientEditTextView.IndividualReplacementTask.1
                @Override // com.android.ex.chips.RecipientAlternatesAdapter.RecipientMatchCallback
                public void matchesNotFound(Set<String> set) {
                }

                @Override // com.android.ex.chips.RecipientAlternatesAdapter.RecipientMatchCallback
                public void matchesFound(Map<String, RecipientEntry> map) {
                    for (final DrawableRecipientChip drawableRecipientChip2 : arrayList) {
                        final RecipientEntry recipientEntryCreateValidatedEntry = RecipientEditTextView.this.createValidatedEntry(map.get(RecipientEditTextView.tokenizeAddress(drawableRecipientChip2.getEntry().getDestination()).toLowerCase()));
                        if (RecipientEntry.isCreatedRecipient(drawableRecipientChip2.getEntry().getContactId()) && RecipientEditTextView.this.getSpannable().getSpanStart(drawableRecipientChip2) != -1 && recipientEntryCreateValidatedEntry != null) {
                            RecipientEditTextView.this.mHandler.post(new Runnable() { // from class: com.android.ex.chips.RecipientEditTextView.IndividualReplacementTask.1.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    RecipientEditTextView.this.replaceChip(drawableRecipientChip2, recipientEntryCreateValidatedEntry);
                                }
                            });
                        }
                    }
                }
            });
            return null;
        }
    }

    private class MoreImageSpan extends ReplacementDrawableSpan {
        public MoreImageSpan(Drawable drawable) {
            super(drawable);
            setExtraMargin(RecipientEditTextView.this.mLineSpacingExtra);
        }
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onLongPress(MotionEvent motionEvent) {
        DrawableRecipientChip drawableRecipientChipFindChip;
        if (this.mSelectedChip == null && (drawableRecipientChipFindChip = findChip(putOffsetInRange(motionEvent.getX(), motionEvent.getY()))) != null) {
            if (this.mDragEnabled) {
                startDrag(drawableRecipientChipFindChip);
            } else {
                showCopyDialog(drawableRecipientChipFindChip.getEntry().getDestination());
            }
        }
    }

    private int supportGetOffsetForPosition(float f, float f2) {
        if (getLayout() == null) {
            return -1;
        }
        return supportGetOffsetAtCoordinate(supportGetLineAtCoordinate(f2), f);
    }

    private float supportConvertToLocalHorizontalCoordinate(float f) {
        return Math.min((getWidth() - getTotalPaddingRight()) - 1, Math.max(0.0f, f - getTotalPaddingLeft())) + getScrollX();
    }

    private int supportGetLineAtCoordinate(float f) {
        return getLayout().getLineForVertical((int) (Math.min((getHeight() - getTotalPaddingBottom()) - 1, Math.max(0.0f, f - getTotalPaddingLeft())) + getScrollY()));
    }

    private int supportGetOffsetAtCoordinate(int i, float f) {
        return getLayout().getOffsetForHorizontal(i, supportConvertToLocalHorizontalCoordinate(f));
    }

    public void enableDrag() {
        this.mDragEnabled = true;
    }

    private void startDrag(DrawableRecipientChip drawableRecipientChip) {
        String destination = drawableRecipientChip.getEntry().getDestination();
        startDrag(ClipData.newPlainText(destination, destination + COMMIT_CHAR_COMMA), new RecipientChipShadow(drawableRecipientChip), null, 0);
        removeChip(drawableRecipientChip);
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onDragEvent(DragEvent dragEvent) {
        int action = dragEvent.getAction();
        if (action == 1) {
            return dragEvent.getClipDescription().hasMimeType("text/plain");
        }
        if (action == 3) {
            handlePasteClip(dragEvent.getClipData());
            return true;
        }
        if (action != 5) {
            return false;
        }
        requestFocus();
        return true;
    }

    private final class RecipientChipShadow extends View.DragShadowBuilder {
        private final DrawableRecipientChip mChip;

        public RecipientChipShadow(DrawableRecipientChip drawableRecipientChip) {
            this.mChip = drawableRecipientChip;
        }

        @Override // android.view.View.DragShadowBuilder
        public void onProvideShadowMetrics(Point point, Point point2) {
            Rect bounds = this.mChip.getBounds();
            point.set(bounds.width(), bounds.height());
            point2.set(bounds.centerX(), bounds.centerY());
        }

        @Override // android.view.View.DragShadowBuilder
        public void onDrawShadow(Canvas canvas) {
            this.mChip.draw(canvas);
        }
    }

    private void showCopyDialog(String str) {
        int i;
        if (this.mAttachedToWindow) {
            this.mCopyAddress = str;
            this.mCopyDialog.setTitle(str);
            this.mCopyDialog.setContentView(R.layout.copy_chip_dialog_layout);
            this.mCopyDialog.setCancelable(true);
            this.mCopyDialog.setCanceledOnTouchOutside(true);
            Button button = (Button) this.mCopyDialog.findViewById(android.R.id.button1);
            button.setOnClickListener(this);
            if (isPhoneQuery()) {
                i = R.string.copy_number;
            } else {
                i = R.string.copy_email;
            }
            button.setText(getContext().getResources().getString(i));
            this.mCopyDialog.setOnDismissListener(this);
            this.mCopyDialog.show();
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        this.mCopyAddress = null;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        ((ClipboardManager) getContext().getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("", this.mCopyAddress));
        this.mCopyDialog.dismiss();
    }

    protected boolean isPhoneQuery() {
        return getAdapter() != null && getAdapter().getQueryType() == 1;
    }

    @Override // android.widget.AutoCompleteTextView
    public BaseRecipientAdapter getAdapter() {
        return (BaseRecipientAdapter) super.getAdapter();
    }

    public void appendRecipientEntry(RecipientEntry recipientEntry) {
        clearComposingText();
        Editable text = getText();
        DrawableRecipientChip[] sortedRecipients = getSortedRecipients();
        int spanEnd = (sortedRecipients == null || sortedRecipients.length <= 0) ? 0 : text.getSpanEnd(sortedRecipients[sortedRecipients.length - 1]) + 1;
        CharSequence charSequenceCreateChip = createChip(recipientEntry);
        if (charSequenceCreateChip != null) {
            text.insert(spanEnd, charSequenceCreateChip);
        }
    }

    public void removeRecipientEntry(RecipientEntry recipientEntry) {
        for (DrawableRecipientChip drawableRecipientChip : (DrawableRecipientChip[]) getText().getSpans(0, getText().length(), DrawableRecipientChip.class)) {
            RecipientEntry entry = drawableRecipientChip.getEntry();
            if (entry != null && entry.isValid() && entry.isSamePerson(recipientEntry)) {
                removeChip(drawableRecipientChip);
            }
        }
    }

    public void setAlternatePopupAnchor(View view) {
        this.mAlternatePopupAnchor = view;
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i == 8 || !this.mRequiresShrinkWhenNotGone) {
            return;
        }
        this.mRequiresShrinkWhenNotGone = false;
        this.mHandler.post(this.mDelayedShrink);
    }

    private static class ChipBitmapContainer {
        Bitmap bitmap;
        float bottom;
        float left;
        boolean loadIcon;
        float right;
        float top;

        private ChipBitmapContainer() {
            this.loadIcon = true;
        }
    }
}
