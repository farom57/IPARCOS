package org.indilib.i4j.iparcos;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author marcocipriani01
 */
public abstract class LongPressHandler {

    private final Handler handler = new Handler();
    protected final View incrementalView;
    protected final View decrementalView;
    protected boolean autoIncrement = false;
    protected boolean autoDecrement = false;
    protected final long delay;

    private final Runnable counterRunnable = new Runnable() {
        @Override
        public void run() {
            if (autoIncrement) {
                increment();
                handler.postDelayed(this, delay);
            } else if (autoDecrement) {
                decrement();
                handler.postDelayed(this, delay);
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    public LongPressHandler(View incrementView, View decrementView, long delay) {
        this.delay = delay;
        this.incrementalView = incrementView;
        this.decrementalView = decrementView;

        this.decrementalView.setOnClickListener(v -> decrement());
        this.decrementalView.setOnLongClickListener(v -> {
            autoDecrement = true;
            handler.postDelayed(counterRunnable, LongPressHandler.this.delay);
            return false;
        });
        this.decrementalView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && autoDecrement) {
                autoDecrement = false;
            }
            return false;
        });

        this.incrementalView.setOnClickListener(v -> increment());
        this.incrementalView.setOnLongClickListener(v -> {
            autoIncrement = true;
            handler.postDelayed(counterRunnable, LongPressHandler.this.delay);
            return false;
        });
        this.incrementalView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && autoIncrement) {
                autoIncrement = false;
            }
            return false;
        });
    }

    public void stop() {
        autoDecrement = autoIncrement = false;
        incrementalView.setOnClickListener(null);
        incrementalView.setOnLongClickListener(null);
        incrementalView.setOnTouchListener(null);
        decrementalView.setOnClickListener(null);
        decrementalView.setOnLongClickListener(null);
        decrementalView.setOnTouchListener(null);
    }

    protected abstract void increment();

    protected abstract void decrement();
}