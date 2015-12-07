package com.informationUpload.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.informationUpload.R;

/**
 * @author zhjch
 * @version V1.0
 * @ClassName: TitleView
 * @Date 2015/12/7
 * @Description: ${TODO}(用一句话描述该文件做什么)
 */
public class TitleView extends RelativeLayout {
    private final String LOG_TAG = "TitleView";

    private final static int DEFAULT_MAX_TITLE_TEXT_DISPLAYING_COUNT = 8;
    private static final float DEFAULT_TITLE_TEXT_SIZE = 20.0f;
    private static final float TEXT_SIZE_SCALE = 2.0f;
    private static final float MINI_ALLOWED_TEXT_SIZE = 14.0f;

    private Context mContext;
    private TextView mTitle;
    private ImageView mBack;
    private ImageView mRightImage;
    private TextView mRightText;
    private TextView mLeftText;
    private boolean mTitleTextSizeFlexible;
    private int mMaximumTitleTextDisplayingCount;
    private int mAvailableTitleTextSpace;
    private Rect mTitleTextRect;
    private int mWidthPixels;

    public TitleView(Context context) {
        this(context, null, 0);
    }

    public TitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        mBack = (ImageView) inflater.inflate(R.layout.title_view_back, null);
        mTitle = (TextView) inflater.inflate(R.layout.title_view_title, null);
        mRightText = (TextView) inflater.inflate(R.layout.title_view_right_text, null);
        mRightImage = (ImageView) inflater.inflate(R.layout.title_view_right_image, null);
        mLeftText = (TextView) inflater.inflate(R.layout.title_view_left_text, null);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleView);

        mTitleTextSizeFlexible = a.getBoolean(R.styleable.TitleView_title_text_size_flexible, false);
        mMaximumTitleTextDisplayingCount = a.getInt(R.styleable.TitleView_maximum_title_text_displaying_count, DEFAULT_MAX_TITLE_TEXT_DISPLAYING_COUNT);
        int titleCornerSize = context.getResources().getDimensionPixelSize(R.dimen.title_corner_size);
        if (mTitleTextSizeFlexible) {
            mWidthPixels = context.getResources().getDisplayMetrics().widthPixels;
            mTitle.setMaxEms(mMaximumTitleTextDisplayingCount);
            mAvailableTitleTextSpace = mWidthPixels - 2 * titleCornerSize;
            mTitleTextRect = new Rect();
        }

        String title = a.getString(R.styleable.TitleView_title_text);
        setTitleText(title);

        LayoutParams layoutParams;

        if (mTitleTextSizeFlexible) {
            layoutParams = new LayoutParams(mAvailableTitleTextSpace, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        else {
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        layoutParams.setMargins(context.getResources().getDimensionPixelSize(R.dimen.title_view_height),
                0,
                context.getResources().getDimensionPixelSize(R.dimen.title_view_height),
                0);
        layoutParams.addRule(CENTER_IN_PARENT);
        mTitle.setLayoutParams(layoutParams);

        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(CENTER_VERTICAL);
        mBack.setLayoutParams(layoutParams);

        boolean hideBottomLine = a.getBoolean(R.styleable.TitleView_hide_title_bottom_line, false);
        if (!hideBottomLine) {
            int left = getPaddingLeft();
            int right = getPaddingRight();
            int top = getPaddingTop();
            int bottom = getPaddingBottom();
            setBackgroundResource(R.drawable.title_view_line_background);
            setPadding(left, top, right, bottom);
        } else {
            setBackgroundDrawable(null);
        }

        Drawable rightDrawable = a.getDrawable(R.styleable.TitleView_title_right_image);

        String rightText = a.getString(R.styleable.TitleView_title_right_text);

        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(ALIGN_PARENT_RIGHT);
        layoutParams.addRule(CENTER_VERTICAL);
        mRightImage.setImageDrawable(rightDrawable);
        mRightImage.setLayoutParams(layoutParams);
        if (rightDrawable != null) {
            mRightImage.setVisibility(View.VISIBLE);
        } else {
            mRightImage.setVisibility(View.GONE);
        }

        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(ALIGN_PARENT_RIGHT);
        layoutParams.addRule(CENTER_VERTICAL);
        mRightText.setText(rightText);
        mRightText.setLayoutParams(layoutParams);
        if (rightDrawable == null && rightText != null) {
            mRightText.setVisibility(View.VISIBLE);
        } else {
            mRightText.setVisibility(View.GONE);
        }

        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(BELOW, R.id.title_text);
        layoutParams.addRule(CENTER_HORIZONTAL);

        //left text

        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(ALIGN_PARENT_LEFT);
        layoutParams.addRule(CENTER_VERTICAL);
        mLeftText.setVisibility(View.GONE);
        mLeftText.setLayoutParams(layoutParams);
        String leftText = a.getString(R.styleable.TitleView_title_left_text);
        if (leftText != null) {
            setLeftText(leftText);
        }

        // Default height for this title view
        boolean allowedFlexibleHeight = a.getBoolean(R.styleable.TitleView_title_view_allowed_flexible_height, false);
        if (!allowedFlexibleHeight) {
            ViewGroup.LayoutParams titleLayoutParam = getLayoutParams();
            if (titleLayoutParam == null) {
                titleLayoutParam =  generateDefaultLayoutParams();
                titleLayoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            titleLayoutParam.height = context.getResources().getDimensionPixelSize(R.dimen.title_view_height);
            setLayoutParams(titleLayoutParam);
        }

        a.recycle();

        addView(mRightImage);
        addView(mRightText);
        addView(mBack);
        addView(mTitle);
        addView(mLeftText);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    private void delegateTouchView(final View view) {
        post(new Runnable() {
            @Override
            public void run() {
                Rect delegateArea = new Rect();
                View delegate = view;
                delegate.getHitRect(delegateArea);
                Rect candidateRect = new Rect();
                getHitRect(candidateRect);
                int titleCornerWidth = getResources().getDimensionPixelSize(R.dimen.title_corner_size);
                delegateArea.left -= Math.abs(candidateRect.width()) / 2 - titleCornerWidth;
                delegateArea.right += Math.abs(candidateRect.width()) / 2 - titleCornerWidth;
                delegateArea.top -= Math.abs(candidateRect.height());
                TouchDelegate expandedArea = new TouchDelegate(delegateArea, delegate);
                if (View.class.isInstance(delegate.getParent())) {
                    ((View) delegate.getParent()).setTouchDelegate(expandedArea);
                }
            }
        });
    }

    private void displayRightIcon() {
        mRightImage.setVisibility(View.VISIBLE);
        mRightText.setVisibility(View.GONE);
    }

    public void displayRightText() {
        mRightText.setVisibility(View.VISIBLE);
        mRightImage.setVisibility(View.GONE);
    }

    private void displayLeftText() {
        mLeftText.setVisibility(View.VISIBLE);
        mBack.setVisibility(View.GONE);
    }

    private void displayLeftIcon() {
        mLeftText.setVisibility(View.GONE);
        mBack.setVisibility(View.VISIBLE);
    }

    public void hidLeftView() {
        mLeftText.setVisibility(View.INVISIBLE);
        mBack.setVisibility(View.INVISIBLE);
    }

    public void hidRightView() {
        mRightText.setVisibility(View.GONE);
        mRightImage.setVisibility(View.GONE);
    }

    public void setRightViewEnabled(boolean enabled) {
        mRightImage.setEnabled(enabled);
        mRightText.setEnabled(enabled);
    }

    public void setLeftViewEnabled(boolean enabled) {
        mLeftText.setEnabled(enabled);
        mRightText.setEnabled(enabled);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public TitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setTitleText(CharSequence titleText) {
        if (mTitleTextSizeFlexible) {
            updateTextSizeAndEms(titleText, DEFAULT_TITLE_TEXT_SIZE);
        }
        mTitle.setText(titleText);
    }

    private void updateTextSizeAndEms(CharSequence titleText, float initTextSize) {
        if (titleText != null) {
            CharSequence measuredTexts = titleText;
            if (titleText.length() > mMaximumTitleTextDisplayingCount) {
                measuredTexts = titleText.subSequence(0, mMaximumTitleTextDisplayingCount);
            }
            int count = 0;
            float textSize;
            do  {
                textSize = initTextSize - (count++) * TEXT_SIZE_SCALE;
                mTitle.setTextSize(textSize);
                mTitle.getPaint().getTextBounds((String) measuredTexts, 0, measuredTexts.length(), mTitleTextRect);
            }
            while (mTitleTextRect.width() > mAvailableTitleTextSpace
                    && textSize > MINI_ALLOWED_TEXT_SIZE);

            count = 0;
            while (mTitleTextRect.width() > mAvailableTitleTextSpace
                    && mMaximumTitleTextDisplayingCount - count > DEFAULT_MAX_TITLE_TEXT_DISPLAYING_COUNT) {
                measuredTexts = titleText.subSequence(0, mMaximumTitleTextDisplayingCount - (++count));
                mTitle.getPaint().getTextBounds((String) measuredTexts, 0, measuredTexts.length(), mTitleTextRect);
            }
            mTitle.setEms(mMaximumTitleTextDisplayingCount - count);
        }
    }

    public void setTitleText(int titleResId) {
        if (!mTitleTextSizeFlexible) {
            mTitle.setText(titleResId);
        }
        else {
            setTitleText(mContext.getString(titleResId));
        }
    }

    public void setLeftText(CharSequence text) {
        mLeftText.setText(text);
        displayLeftText();
    }

    public void setLeftTextColor(int leftTextColor) {
        mLeftText.setTextColor(leftTextColor);
    }

    public void setLeftText(CharSequence text, int leftTextColor) {
        setLeftText(text);
        setLeftTextColor(leftTextColor);
    }

    public TextView getTitleTextView() {
        return mTitle;
    }

    public void setOnLeftAreaClickListener(OnClickListener listener) {
        mBack.setOnClickListener(listener);
        mLeftText.setOnClickListener(listener);
    }

    public void setLeftImageDrawable(Drawable drawable) {
        mBack.setImageDrawable(drawable);
        displayLeftIcon();
    }

    public void setLeftImageResource(int resId) {
        mBack.setImageResource(resId);
        displayLeftIcon();
    }

    public void setLeftImageBitmap(Bitmap bitmap) {
        mBack.setImageBitmap(bitmap);
        displayLeftIcon();
    }

    public void setOnRightAreaClickListener(OnClickListener listener) {
        mRightText.setOnClickListener(listener);
        mRightImage.setOnClickListener(listener);
    }


    public void setRightText(int resId) {
        mRightText.setText(resId);
        displayRightText();
    }

    public void setRightText(CharSequence text) {
        mRightText.setText(text);
        displayRightText();
    }

    public void setRightText(CharSequence rightText, int rightTextColor) {
        setRightTextColor(rightTextColor);
        setRightText(rightText);
    }

    public void setRightTextColor(int rightTextColor) {
        mRightText.setTextColor(rightTextColor);
    }

    public void setRightImageDrawable(Drawable drawable) {
        mRightImage.setImageDrawable(drawable);
        displayRightIcon();
    }

    public void setRightImageResource(int resId) {
        mRightImage.setImageResource(resId);
        displayRightIcon();
    }

    public void setRightImageBitmap(Bitmap bitmap) {
        mRightImage.setImageBitmap(bitmap);
        displayRightIcon();
    }

    public View getRightView() {
        if (mRightImage != null && mRightImage.getVisibility() == View.VISIBLE) {
            return mRightImage;
        } else if (mRightText != null && mRightText.getVisibility() == View.VISIBLE) {
            return mRightText;
        }
        return null;
    }

    public TextView getLeftTextView() {
        return mLeftText;
    }
}
