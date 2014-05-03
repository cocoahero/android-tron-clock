package com.cocoahero.android.TronClock.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.cocoahero.android.TronClock.R;

public class TronClockActivity extends Activity {

    // ------------------------------------------------------------------------
    // Instance Variables
    // ------------------------------------------------------------------------

    private Timer mClock;

    private TextView mTextDisplay;

    private DateFormat mTimeFormat;

    private final Runnable mDrawRunnable = new Runnable() {
        @Override
        public void run() {
            TronClockActivity.this.onUpdateDisplay();
        }
    };

    // ------------------------------------------------------------------------
    // Activity Lifecycle
    // ------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_clock);

        this.mTextDisplay = (TextView) this.findViewById(R.id.display);

        this.mTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        this.onUpdateDisplay();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.mClock = new Timer();
        this.mClock.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                TronClockActivity.this.onClockTick();
            }
        }, 0, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.mClock.cancel();
        this.mClock = null;
    }

    // ------------------------------------------------------------------------
    // Private Methods
    // ------------------------------------------------------------------------

    private void onClockTick() {
        if (this.mTextDisplay != null) {
            this.mTextDisplay.post(this.mDrawRunnable);
        }
    }

    private void onUpdateDisplay() {
        this.mTextDisplay.setText(this.mTimeFormat.format(new Date()));
    }
}
