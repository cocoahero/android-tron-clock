package com.cocoahero.android.TronClock.view;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;

import com.cocoahero.android.TronClock.R;

public class TronClockView extends View implements OnPreDrawListener {

    // ------------------------------------------------------------------------
    // Instance Variables
    // ------------------------------------------------------------------------

    private Timer mClock;

    private final int mArcWidth;

    private final int mArcPadding;

    private final Calendar mCalendar;

    private final Paint mHoursPaint;

    private final Paint mMinutesPaint;

    private final Paint mSecondsPaint;

    private RectF mHoursBounds = new RectF();

    private RectF mMinutesBounds = new RectF();

    private RectF mSecondsBounds = new RectF();

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    public TronClockView(Context context) {
        this(context, null);
    }

    public TronClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TronClockView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);

        this.mCalendar = Calendar.getInstance();

        this.getViewTreeObserver().addOnPreDrawListener(this);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TronClockView, 0, 0);

        int hColor = a.getColor(R.styleable.TronClockView_hoursColor, 0);

        int mColor = a.getColor(R.styleable.TronClockView_minutesColor, 0);

        int sColor = a.getColor(R.styleable.TronClockView_secondsColor, 0);

        int arcWidth = a.getDimensionPixelSize(R.styleable.TronClockView_arcWidth, 15);

        int arcPadding = a.getDimensionPixelSize(R.styleable.TronClockView_arcPadding, 0);

        a.recycle();

        Paint base = new Paint();
        base.setStrokeWidth(arcWidth);
        base.setAntiAlias(true);
        base.setStyle(Style.STROKE);

        this.mArcWidth = arcWidth;
        this.mArcPadding = arcPadding;

        this.mHoursPaint = new Paint(base);
        this.mHoursPaint.setColor(hColor);

        this.mMinutesPaint = new Paint(base);
        this.mMinutesPaint.setColor(mColor);

        this.mSecondsPaint = new Paint(base);
        this.mSecondsPaint.setColor(sColor);
    }

    // ------------------------------------------------------------------------
    // View Lifecycle
    // ------------------------------------------------------------------------

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        if (visibility == VISIBLE) {
            this.startClock();
        }
        else {
            this.stopClock();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        this.stopClock();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int arcW = this.mArcWidth;
        int arcP = this.mArcPadding;

        int xpad = this.getPaddingLeft() + this.getPaddingRight();
        int ypad = this.getPaddingTop() + this.getPaddingBottom();

        xpad += arcW;
        ypad += arcW;

        float diameter = Math.min(w - xpad, h - ypad);

        RectF bounds = new RectF(0, 0, diameter, diameter);
        bounds.offsetTo(this.getPaddingLeft(), this.getPaddingTop());
        bounds.offset(arcW / 2f, arcW / 2f);

        this.mHoursBounds = new RectF(bounds);
        this.mMinutesBounds = new RectF(bounds);
        this.mMinutesBounds.inset(arcW + arcP, arcW + arcP);
        this.mSecondsBounds = new RectF(bounds);
        this.mSecondsBounds.inset((arcW + arcP) * 2, (arcW + arcP) * 2);
    }

    @Override
    public boolean onPreDraw() {
        this.mCalendar.setTime(new Date());

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float x = canvas.getWidth() / 2f;
        float y = canvas.getHeight() / 2f;

        canvas.rotate(-90f, x, y);

        int hrs = this.mCalendar.get(Calendar.HOUR_OF_DAY);
        int min = this.mCalendar.get(Calendar.MINUTE);
        int sec = this.mCalendar.get(Calendar.SECOND);
        int mls = this.mCalendar.get(Calendar.MILLISECOND);

        mls += sec * 1000;

        float hDeg = ((hrs / 24f) * 360);
        float mDeg = ((min / 60f) * 360);
        float sDeg = ((mls / 60000f) * 360);

        canvas.drawArc(this.mHoursBounds, 0, hDeg, false, this.mHoursPaint);
        canvas.drawArc(this.mMinutesBounds, 0, mDeg, false, this.mMinutesPaint);
        canvas.drawArc(this.mSecondsBounds, 0, sDeg, false, this.mSecondsPaint);
    }

    private void startClock() {
        this.stopClock();
        this.mClock = new Timer();
        this.mClock.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                TronClockView.this.postInvalidate();
            }
        }, 0, 1000 / 45);
    }

    private void stopClock() {
        if (this.mClock != null) {
            this.mClock.cancel();
            this.mClock = null;
        }
    }
}
