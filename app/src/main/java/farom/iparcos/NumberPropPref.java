package farom.iparcos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import laazotea.indi.Constants;
import laazotea.indi.client.INDIElement;
import laazotea.indi.client.INDINumberProperty;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDIValueException;

public class NumberPropPref extends PropPref {

    public NumberPropPref(Context context, INDIProperty prop) {
        super(context, prop);
    }

    /**
     * Create the summary rich-text string
     *
     * @return the summary
     */
    @Override
    protected Spannable createSummary() {
        ArrayList<INDIElement> elements = prop.getElementsAsList();
        if (elements.size() > 0) {
            StringBuilder temp = new StringBuilder();

            int i;
            temp.append(elements.get(0).getLabel()).append(": ");
            for (i = 0; i < elements.size() - 1; i++) {
                temp.append(elements.get(i).getValueAsString());
                temp.append(", ");
                temp.append(elements.get(i + 1).getLabel()).append(": ");
            }
            temp.append(elements.get(i).getValueAsString());

            return new SpannableString(temp.toString());

        } else {
            return new SpannableString("");
        }
    }

    @Override
    protected void onClick() {
        DialogFragment newFragment = new NumberRequestFragment((INDINumberProperty) prop);
        newFragment.show(((Activity) getContext()).getFragmentManager(), "request");
    }

    @SuppressLint("ValidFragment")
    public class NumberRequestFragment extends DialogFragment {
        private INDINumberProperty prop;

        public NumberRequestFragment(INDINumberProperty prop) {
            this.prop = prop;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            final ArrayList<INDIElement> elements = prop.getElementsAsList();
            final ArrayList<EditText> editTextViews = new ArrayList<>(elements.size());

            LinearLayout layout = new LinearLayout(Application.getAppContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            int padding = Application.getAppContext().getResources().getDimensionPixelSize(R.dimen.padding_medium);

            for (int i = 0; i < elements.size(); i++) {
                TextView textView = new TextView(Application.getAppContext());
                textView.setText(elements.get(i).getLabel());

                textView.setPadding(padding, padding, padding, 0);
                layout.addView(textView);

                editTextViews.add(new EditText(Application.getAppContext()));
                editTextViews.get(i).setText(elements.get(i).getValueAsString());
                editTextViews.get(i).setPadding(padding, padding, padding, padding);
                editTextViews.get(i).setEnabled(prop.getPermission() != Constants.PropertyPermissions.RO);
                layout.addView(editTextViews.get(i));
            }

            ScrollView scrollView = new ScrollView(Application.getAppContext());
            scrollView.addView(layout);
            builder.setView(scrollView);

            builder.setTitle(prop.getLabel());

            if (prop.getPermission() != Constants.PropertyPermissions.RO) {
                builder.setPositiveButton(R.string.send_request, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            for (int i = 0; i < elements.size(); i++) {
                                if (elements.get(i).checkCorrectValue(editTextViews.get(i).getText().toString())) {
                                    elements.get(i).setDesiredValue(editTextViews.get(i).getText().toString());
                                }
                            }
                            prop.sendChangesToDriver();

                        } catch (INDIValueException | IOException | IllegalArgumentException e) {
                            Toast toast = Toast.makeText(Application.getAppContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel_request, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

            } else {
                builder.setNegativeButton(R.string.back_request, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
            }
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}