package org.indilib.i4j.iparcos.prop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import org.indilib.i4j.Constants;
import org.indilib.i4j.client.INDIElement;
import org.indilib.i4j.client.INDINumberElement;
import org.indilib.i4j.client.INDINumberProperty;
import org.indilib.i4j.client.INDIProperty;
import org.indilib.i4j.client.INDIValueException;

import java.util.ArrayList;
import java.util.List;

import org.indilib.i4j.iparcos.IPARCOSApp;
import org.indilib.i4j.iparcos.R;

public class NumberPropPref extends PropPref<INDINumberElement> {

    public NumberPropPref(Context context, INDIProperty<INDINumberElement> prop) {
        super(context, prop);
    }

    /**
     * Create the summary rich-text string
     *
     * @return the summary
     */
    @Override
    protected Spannable createSummary() {
        List<INDINumberElement> elements = prop.getElementsAsList();
        int count = elements.size();
        if (count > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            int i;
            stringBuilder.append(elements.get(0).getLabel()).append(": ");
            for (i = 0; i < count - 1; i++) {
                stringBuilder.append(elements.get(i).getValueAsString()).append(", ")
                        .append(elements.get(i + 1).getLabel()).append(": ");
            }
            stringBuilder.append(elements.get(i).getValueAsString());
            return new SpannableString(stringBuilder.toString());

        } else {
            return new SpannableString(getContext().getString(R.string.no_indi_elements));
        }
    }

    @Override
    protected void onClick() {
        Context context = getContext();
        if (!getSummary().toString().equals(context.getString(R.string.no_indi_elements))) {
            NumberRequestFragment requestFragment = new NumberRequestFragment();
            requestFragment.setArguments((INDINumberProperty) prop, this);
            requestFragment.show(((FragmentActivity) context).getSupportFragmentManager(), "request");
        }
    }

    public static class NumberRequestFragment extends DialogFragment {

        private INDINumberProperty prop;
        private PropPref<INDINumberElement> propPref;

        public void setArguments(INDINumberProperty prop, PropPref<INDINumberElement> propPref) {
            this.prop = prop;
            this.propPref = propPref;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Context context = getActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final List<INDINumberElement> elements = prop.getElementsAsList();
            final ArrayList<EditText> editTextViews = new ArrayList<>(elements.size());
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int padding = IPARCOSApp.getAppResources().getDimensionPixelSize(R.dimen.padding_medium);
            layoutParams.setMargins(padding, 0, padding, 0);

            for (INDINumberElement element : elements) {
                TextView textView = new TextView(context);
                textView.setText(element.getLabel());
                textView.setPadding(padding, padding, padding, 0);
                layout.addView(textView, layoutParams);
                EditText editText = new EditText(context);
                editText.setText(element.getValueAsString());
                editText.setPadding(padding, padding, padding, padding);
                editText.setEnabled(prop.getPermission() != Constants.PropertyPermissions.RO);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editTextViews.add(editText);
                layout.addView(editText, layoutParams);
                final double step = element.getStep();
                final double min = element.getMin();
                final double max = element.getMax();
                final int interval = (int) ((max - min) / step);
                if (interval <= 1000) {
                    SeekBar seekBar = new SeekBar(context);
                    seekBar.setPadding(padding *2 , padding, padding * 2, padding);
                    seekBar.setMax(interval);
                    seekBar.setProgress((int) ((element.getValue() - min) / step));
                    final SeekBar.OnSeekBarChangeListener changeListener = new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            editText.setText(String.valueOf((int) (min + (progress * step))));
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    };
                    seekBar.setOnSeekBarChangeListener(changeListener);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            try {
                                double value = Double.parseDouble(s.toString());
                                if ((value >= min) && (value <= max)) {
                                    seekBar.setOnSeekBarChangeListener(null);
                                    seekBar.setProgress((int) ((value - min) / step));
                                    seekBar.setOnSeekBarChangeListener(changeListener);
                                }
                            } catch (NumberFormatException ignored) {

                            }
                        }
                    });
                    layout.addView(seekBar, layoutParams);
                }
            }

            ScrollView scrollView = new ScrollView(context);
            scrollView.addView(layout);
            builder.setView(scrollView);
            builder.setTitle(prop.getLabel());

            if (prop.getPermission() != Constants.PropertyPermissions.RO) {
                builder.setPositiveButton(R.string.send_request, (dialog, id) -> {
                    try {
                        for (int i = 0; i < elements.size(); i++) {
                            INDIElement element = elements.get(i);
                            String s = editTextViews.get(i).getText().toString();
                            if (element.checkCorrectValue(s)) {
                                element.setDesiredValue(s);
                            }
                        }
                    } catch (INDIValueException | IllegalArgumentException e) {
                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        IPARCOSApp.log(IPARCOSApp.getAppResources().getString(R.string.error) + e.getLocalizedMessage());
                    }
                    propPref.sendChanges();
                });
                builder.setNegativeButton(R.string.cancel_request, (dialog, id) -> {

                });

            } else {
                builder.setNegativeButton(R.string.back_request, (dialog, id) -> {

                });
            }
            return builder.create();
        }
    }
}