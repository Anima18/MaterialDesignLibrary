package com.chris.materialdesignlibrary.ui;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import materialdesigndemo.chris.com.customedittext.R;
import com.chris.materialdesignlibrary.util.Density;


/**
 * Created by Administrator on 2016/4/28.
 */
public class MaterialTextEdit extends AppCompatEditText {

    private boolean floatingLabelAble;
    private String floatingLabelText;
    private int floatingLabelTextColor;
    private int floatingLabelTextSize;
    private int baseColor;
    private int primaryColor = -1;
    private int underlineColor = -1;
    private int errorTextColor = Color.RED;

    private int floatingLabelPadding;
    private boolean floatingLabelShown;
    private int bottomLineCount = 0;
    /**
     * inner top padding
     */
    private int innerPaddingTop;

    /**
     * inner bottom padding
     */
    private int innerPaddingBottom;

    /**
     * inner left padding
     */
    private int innerPaddingLeft;

    /**
     * inner right padding
     */
    private int innerPaddingRight;
    /**
     * the spacing between the main text and the inner top padding.
     */
    private int extraPaddingTop;

    /**
     * the spacing between the main text and the inner bottom padding.
     */
    private int extraPaddingBottom;

    /**
     * the extra spacing between the main text and the left, actually for the left icon.
     */
    private int extraPaddingLeft;

    /**
     * the extra spacing between the main text and the right, actually for the right icon.
     */
    private int extraPaddingRight;
    /**
     * the floating label's focusFraction
     */
    private float focusFraction;
    /**
     * animation fraction of the floating label (0 as totally hidden).
     */
    private float floatingLabelFraction;
    /**
     * error text for manually invoked {@link #setError(CharSequence)}
     */
    private String tempErrorText;

    ObjectAnimator labelAnimator;
    ObjectAnimator labelFocusAnimator;
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    StaticLayout staticLayout;
    private ArgbEvaluator focusEvaluator = new ArgbEvaluator();
    OnFocusChangeListener innerFocusChangeListener;
    private int bottomSpacing;

    public MaterialTextEdit(Context context) {
        super(context);
        setAttributes(context, null);
    }

    public MaterialTextEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(context, attrs);
    }

    public MaterialTextEdit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttributes(context, attrs);
    }

    public void setAttributes(Context context, AttributeSet attrs) {
        // retrieve the default primaryColor
        int defaultPrimaryColor;
        TypedValue primaryColorTypedValue = new TypedValue();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                context.getTheme().resolveAttribute(android.R.attr.colorAccent, primaryColorTypedValue, true);
                defaultPrimaryColor = primaryColorTypedValue.data;
            } else {
                throw new RuntimeException("SDK_INT less than LOLLIPOP");
            }
        } catch (Exception e) {
            try {
                int colorPrimaryId = getResources().getIdentifier("colorPrimary", "attr", getContext().getPackageName());
                if (colorPrimaryId != 0) {
                    context.getTheme().resolveAttribute(colorPrimaryId, primaryColorTypedValue, true);
                    defaultPrimaryColor = primaryColorTypedValue.data;
                } else {
                    throw new RuntimeException("colorPrimary not found");
                }
            } catch (Exception e1) {
                defaultPrimaryColor = baseColor;
            }
        }
        primaryColor = defaultPrimaryColor;

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MaterialTextEdit);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);

            if(attr == R.styleable.MaterialTextEdit_floatingLabelAble) {
                floatingLabelAble = array.getBoolean(attr, false);
            }else if(attr == R.styleable.MaterialTextEdit_floatingLabelText) {
                floatingLabelText = array.getString(attr);
            }else if(attr == R.styleable.MaterialTextEdit_floatingLabelTextColor) {
                floatingLabelTextColor = array.getColor(attr, getResources().getColor(R.color.colorPrimary));
            }else if(attr == R.styleable.MaterialTextEdit_floatingLabelTextSize) {
                floatingLabelTextSize = array.getDimensionPixelSize(attr, getResources().getDimensionPixelSize(R.dimen.floating_label_text_size));
            }else if(attr == R.styleable.MaterialTextEdit_baseColor) {
                baseColor = array.getColor(attr, Color.BLACK);
            }else if(attr == R.styleable.MaterialTextEdit_primaryColor) {
                primaryColor = array.getColor(attr, defaultPrimaryColor);
            }else if(attr == R.styleable.MaterialTextEdit_underlineColor) {
                underlineColor = array.getColor(attr, -1);
            }else if(attr == R.styleable.MaterialTextEdit_errorTextColor) {
                errorTextColor = array.getColor(attr, Color.RED);
            }

            /*switch (attr) {
                case R.styleable.MaterialTextEdit_floatingLabelAble:
                    floatingLabelAble = array.getBoolean(attr, false);
                    break;
                case R.styleable.MaterialTextEdit_floatingLabelText:
                    floatingLabelText = array.getString(attr);
                    break;
                case R.styleable.MaterialTextEdit_floatingLabelTextColor:
                    floatingLabelTextColor = array.getColor(attr, getResources().getColor(R.color.colorPrimary));
                    break;
                case R.styleable.MaterialTextEdit_floatingLabelTextSize:
                    floatingLabelTextSize = array.getDimensionPixelSize(attr, getResources().getDimensionPixelSize(R.dimen.floating_label_text_size));
                    break;
                case R.styleable.MaterialTextEdit_baseColor:
                    baseColor = array.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.MaterialTextEdit_primaryColor:
                    primaryColor = array.getColor(attr, defaultPrimaryColor);
                    break;
                case R.styleable.MaterialTextEdit_underlineColor:
                    underlineColor = array.getColor(attr, -1);
                    break;
                case R.styleable.MaterialTextEdit_errorTextColor:
                    errorTextColor = array.getColor(attr, Color.RED);
                    break;
            }*/
        }
        array.recycle();

        int padding = 0;
        int[] paddings = new int[]{
                android.R.attr.padding, // 0
                android.R.attr.paddingLeft, // 1
                android.R.attr.paddingTop, // 2
                android.R.attr.paddingRight, // 3
                android.R.attr.paddingBottom // 4
        };
        TypedArray paddingsTypedArray = context.obtainStyledAttributes(attrs, paddings);
        int m = paddingsTypedArray.getIndexCount();
        for (int i = 0; i < m; i++) {
            int attr = array.getIndex(i);

            switch (attr) {
                case android.R.attr.padding:
                    padding = paddingsTypedArray.getDimensionPixelSize(attr, 0);
                    break;
                case android.R.attr.paddingLeft:
                    innerPaddingLeft = paddingsTypedArray.getDimensionPixelSize(attr, padding);
                    break;
                case android.R.attr.paddingTop:
                    innerPaddingTop = paddingsTypedArray.getDimensionPixelSize(attr, padding);
                    break;
                case android.R.attr.paddingRight:
                    innerPaddingRight = paddingsTypedArray.getDimensionPixelSize(attr, padding);
                    break;
                case android.R.attr.paddingBottom:
                    innerPaddingBottom = paddingsTypedArray.getDimensionPixelSize(attr, padding);
                    break;
            }
        }
        paddingsTypedArray.recycle();

        init(context);
    }

    public void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(null);
        } else {
            setBackgroundDrawable(null);
        }

        initPadding();
        initFloatingLabel();
    }

    public void initPadding() {
        bottomSpacing = floatingLabelPadding = getResources().getDimensionPixelSize(R.dimen.inner_components_spacing);
        extraPaddingTop = floatingLabelAble ? floatingLabelTextSize + floatingLabelPadding : floatingLabelPadding;
        textPaint.setTextSize(floatingLabelTextSize);
        Paint.FontMetrics textMetrics = textPaint.getFontMetrics();
        extraPaddingBottom = (int)((textMetrics.descent - textMetrics.ascent)*bottomLineCount  + bottomSpacing *2);
        super.setPadding(innerPaddingLeft, innerPaddingTop + extraPaddingTop, innerPaddingRight, innerPaddingBottom + extraPaddingBottom);
    }

    private void initFloatingLabel() {
        // observe the text changing
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (floatingLabelAble) {
                    if (s.length() == 0) {
                        if (floatingLabelShown) {
                            floatingLabelShown = false;
                           getLabelAnimator().reverse();
                        }
                    } else if (!floatingLabelShown) {
                        floatingLabelShown = true;
                        getLabelAnimator().start();
                    }
                }
            }
        });
        // observe the focus state to animate the floating label's text color appropriately
        innerFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (floatingLabelAble) {
                    if (hasFocus) {
                        getLabelFocusAnimator().start();
                    } else {
                        getLabelFocusAnimator().reverse();
                    }
                }
                /*if (validateOnFocusLost && !hasFocus) {
                    validate();
                }
                if (outerFocusChangeListener != null) {
                    outerFocusChangeListener.onFocusChange(v, hasFocus);
                }*/
            }
        };
        super.setOnFocusChangeListener(innerFocusChangeListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i("MaterialTextEdit", "onDraw");
        int startX = getScrollX() + getPaddingLeft();
        int endX = getScrollX() + getWidth() - getPaddingRight();
        int lineStartY = getScrollY() + getHeight() - getPaddingBottom();
        // draw the underline
        lineStartY += (bottomSpacing - getPixel(2));
        if (!isEnabled()) { // disabled
            //paint.setColor(underlineColor != -1 ? underlineColor : baseColor & 0x00ffffff | 0x44000000);
            paint.setColor(underlineColor != -1 ? underlineColor : (baseColor & 0x00ffffff | 0x44000000));
            float interval = getPixel(1);
            for (float xOffset = 0; xOffset < getWidth(); xOffset += interval * 3) {
                canvas.drawRect(startX + xOffset, lineStartY, startX + xOffset + interval, lineStartY + getPixel(1), paint);
            }
        }else if (!TextUtils.isEmpty(tempErrorText)) { // error
            paint.setColor(errorTextColor);
            canvas.drawRect(startX, lineStartY, endX, lineStartY + getPixel(2), paint);
        }else if (hasFocus()) { // focused
            paint.setColor(primaryColor);
            canvas.drawRect(startX, lineStartY, endX, lineStartY + getPixel(2), paint);
        } else { // normal
            paint.setColor(underlineColor != -1 ? underlineColor : (baseColor & 0x00ffffff | 0x44000000));
            canvas.drawRect(startX, lineStartY, endX, lineStartY + getPixel(1), paint);
        }

        //draw the bottom text
        if(!TextUtils.isEmpty(tempErrorText)) {
            canvas.save();
            textPaint.setTextSize(floatingLabelTextSize);
            // calculate the text color
            textPaint.setColor(errorTextColor);

            if(staticLayout == null) {
                staticLayout = new StaticLayout(tempErrorText,textPaint, getWidth() , Layout.Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
            }
            canvas.translate(startX , lineStartY + bottomSpacing);
            staticLayout.draw(canvas);
            canvas.restore();
            // draw the floating label
            //canvas.drawText(tempErrorText.toString(), errorTextStartX, errorTextStartY, textPaint);
        }

        // draw the floating label
        if(hasFocus()) {
            if (floatingLabelText == null) {
                floatingLabelText = getHint().toString();
            }
        }
        if (floatingLabelAble && !TextUtils.isEmpty(floatingLabelText)/* && floatingLabelFraction != 0*/) {
            textPaint.setTextSize(floatingLabelTextSize);
            // calculate the text color
            textPaint.setColor(floatingLabelTextColor != -1 ? floatingLabelTextColor : (baseColor & 0x00ffffff | 0x44000000));

            // calculate the horizontal position
            float floatingLabelWidth = textPaint.measureText(floatingLabelText.toString());
            int floatingLabelStartX;
            if ((getGravity() & Gravity.RIGHT) == Gravity.RIGHT ) {
                floatingLabelStartX = (int) (endX - floatingLabelWidth);
            } else if ((getGravity() & Gravity.LEFT) == Gravity.LEFT) {
                floatingLabelStartX = startX;
            } else {
                floatingLabelStartX = startX + (int) (innerPaddingLeft + (getWidth() -innerPaddingLeft - innerPaddingRight - floatingLabelWidth) / 2);
            }

            // calculate the vertical position
            int distance = floatingLabelPadding;
            //int floatingLabelStartY = (int) (innerPaddingTop + floatingLabelTextSize + floatingLabelPadding - distance + getScrollY());
            int floatingLabelStartY = (int) (innerPaddingTop + floatingLabelTextSize + floatingLabelPadding - distance * floatingLabelFraction + getScrollY());


            // calculate the alpha
            int alpha = ((int) (floatingLabelFraction * 0xff * (0.74f * focusFraction * (isEnabled() ? 1 : 0) + 0.26f) * (floatingLabelTextColor != -1 ? 1 : Color.alpha(floatingLabelTextColor) / 256f)));
            textPaint.setAlpha(alpha);

            // draw the floating label
            canvas.drawText(floatingLabelText.toString(), floatingLabelStartX, floatingLabelStartY, textPaint);
        }

        super.onDraw(canvas);
    }

    private int getPixel(int dp) {
        return Density.dp2px(getContext(), dp);
    }

    @Override
    public void setError(CharSequence errorText) {
        tempErrorText = errorText == null ? null : errorText.toString();
        if(tempErrorText != null) {
            float width = textPaint.measureText(tempErrorText);
            double z = (double)(width/getWidth());
            bottomLineCount = (int) Math.ceil(z);
            initPadding();
        }else if(bottomLineCount != 0) {
            bottomLineCount = 0;
            initPadding();
        }
        postInvalidate();
    }

    public float getFloatingLabelFraction() {
        return floatingLabelFraction;
    }

    public void setFloatingLabelFraction(float floatingLabelFraction) {
        this.floatingLabelFraction = floatingLabelFraction;
        invalidate();
    }

    public float getFocusFraction() {
        return focusFraction;
    }

    public void setFocusFraction(float focusFraction) {
        this.focusFraction = focusFraction;
        invalidate();
    }

    private ObjectAnimator getLabelAnimator() {
        if (labelAnimator == null) {
            labelAnimator = ObjectAnimator.ofFloat(this, "floatingLabelFraction", 0f, 1f);
        }
        labelAnimator.setDuration(300);
        return labelAnimator;
    }

    private ObjectAnimator getLabelFocusAnimator() {
        if (labelFocusAnimator == null) {
            labelFocusAnimator = ObjectAnimator.ofFloat(this, "focusFraction", 0f, 1f);
        }
        return labelFocusAnimator;
    }
}
