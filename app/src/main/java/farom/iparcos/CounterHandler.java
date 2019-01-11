package farom.iparcos;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Noman Rafique
 * @author marcocipriani01
 * @see <a href="https://stackoverflow.com/a/41466381">Continuously increase integer value as the button is pressed</a>
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class CounterHandler {

    private final Handler handler = new Handler();
    private View incrementalView;
    private View decrementalView;
    private int minValue;
    private int maxValue;
    private int currentValue;
    private int steps;
    private long delay;
    private boolean isCycle;
    private boolean autoIncrement = false;
    private boolean autoDecrement = false;
    private CounterListener listener;

    private Runnable counterRunnable = new Runnable() {
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

    public CounterHandler(View incrementalView, View decrementalView, int minValue,
                          int maxValue, int initialValue, int steps,
                          long delay, boolean isCycle, CounterListener listener) {
        this(incrementalView, decrementalView, minValue, maxValue, initialValue, steps, delay, isCycle, listener, true);
    }

    public CounterHandler(View incrementalView, View decrementalView, int minValue,
                          int maxValue, int initialValue, int steps,
                          long delay, boolean isCycle, CounterListener listener, boolean setNow) {
        if ((minValue != -1) && (maxValue != -1)) {
            if (maxValue <= minValue) {
                throw new IllegalArgumentException("Max value < min value!");
            }
            if (minValue >= maxValue) {
                throw new IllegalArgumentException("Min value > max value!");
            }
        }
        this.minValue = minValue;
        this.maxValue = maxValue;
        if (((minValue != -1) && (initialValue < minValue)) || ((maxValue != -1) && (initialValue > maxValue))) {
            throw new IllegalArgumentException("Initial value out of bounds!");
        }
        this.currentValue = initialValue;
        this.steps = steps;
        this.delay = delay;
        this.isCycle = isCycle;
        this.incrementalView = incrementalView;
        this.decrementalView = decrementalView;
        this.listener = listener;

        this.decrementalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement();
            }
        });
        this.decrementalView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                autoDecrement = true;
                handler.postDelayed(counterRunnable, CounterHandler.this.delay);
                return false;
            }
        });
        this.decrementalView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && autoDecrement) {
                    autoDecrement = false;
                }
                return false;
            }
        });

        this.incrementalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment();
            }
        });
        this.incrementalView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                autoIncrement = true;
                handler.postDelayed(counterRunnable, CounterHandler.this.delay);
                return false;
            }
        });
        this.incrementalView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && autoIncrement) {
                    autoIncrement = false;
                }
                return false;
            }
        });

        if (this.listener != null && setNow) {
            this.listener.onIncrement(this.incrementalView, this.currentValue);
            this.listener.onDecrement(this.decrementalView, this.currentValue);
        }
    }

    public void stop() {
        listener = null;
        autoDecrement = autoIncrement = false;
        incrementalView.setOnClickListener(null);
        incrementalView.setOnLongClickListener(null);
        incrementalView.setOnTouchListener(null);
        decrementalView.setOnClickListener(null);
        decrementalView.setOnLongClickListener(null);
        decrementalView.setOnTouchListener(null);
    }

    public int getValue() {
        return currentValue;
    }

    public void setValue(int newValue) {
        if ((maxValue != -1) && (newValue > maxValue)) {
            currentValue = maxValue;

        } else if ((minValue != -1) && (newValue < minValue)) {
            currentValue = minValue;

        } else {
            currentValue = newValue;
        }
    }

    public void setMaxValue(int maxValue) {
        if (maxValue <= minValue) {
            throw new IllegalArgumentException("Max value < min value!");
        }
        this.maxValue = maxValue;
        if (currentValue > this.maxValue) {
            currentValue = this.maxValue;
        }
    }

    public void setMinValue(int minValue) {
        if (minValue >= maxValue) {
            throw new IllegalArgumentException("Min value > max value!");
        }
        this.minValue = minValue;
        if (currentValue < this.minValue) {
            currentValue = this.minValue;
        }
    }

    private void increment() {
        int number = this.currentValue;
        if (maxValue != -1) {
            if (number + steps <= maxValue) {
                number += steps;

            } else if (isCycle) {
                number = minValue == -1 ? 0 : minValue;
            }

        } else {
            number += steps;
        }
        if (number != this.currentValue && listener != null) {
            this.currentValue = number;
            listener.onIncrement(incrementalView, this.currentValue);
        }
    }

    private void decrement() {
        int number = this.currentValue;
        if (minValue != -1) {
            if (number - steps >= minValue) {
                number -= steps;

            } else if (isCycle) {
                number = maxValue == -1 ? 0 : maxValue;
            }

        } else {
            number -= steps;
        }
        if (number != this.currentValue && listener != null) {
            this.currentValue = number;
            listener.onDecrement(decrementalView, this.currentValue);
        }
    }

    public interface CounterListener {

        void onIncrement(View view, int number);

        void onDecrement(View view, int number);
    }
}