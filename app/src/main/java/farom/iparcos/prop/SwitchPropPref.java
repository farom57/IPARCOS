package farom.iparcos.prop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.widget.Toast;

import java.util.ArrayList;

import farom.iparcos.Application;
import farom.iparcos.R;
import laazotea.indi.Constants;
import laazotea.indi.Constants.SwitchStatus;
import laazotea.indi.client.INDIElement;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDISwitchElement;
import laazotea.indi.client.INDISwitchProperty;
import laazotea.indi.client.INDIValueException;

public class SwitchPropPref extends PropPref {

    public SwitchPropPref(Context context, INDIProperty prop) {
        super(context, prop);
    }

    /**
     * Create the summary rich-text string
     *
     * @return the summary
     */
    @Override
    protected Spannable createSummary() {
        StringBuilder temp = new StringBuilder();
        ArrayList<INDIElement> elements = prop.getElementsAsList();

        int[] starts = new int[elements.size()];
        int[] ends = new int[elements.size()];
        starts[0] = 0;

        for (int i = 0; i < elements.size(); i++) {
            starts[i] = temp.length();
            temp.append(elements.get(i).getLabel()).append(" ");
            ends[i] = temp.length();
        }

        Spannable summaryText = new SpannableString(temp.toString());

        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

        for (int i = 0; i < elements.size(); i++) {
            if (((INDISwitchElement) (elements.get(i))).getValue() == SwitchStatus.ON) {
                summaryText.setSpan(boldSpan, starts[i], ends[i], 0);
            }
        }

        return summaryText;
    }

    @Override
    protected void onClick() {
        SwitchRequestFragment newFragment = new SwitchRequestFragment();
        newFragment.setArguments((INDISwitchProperty) prop, this);
        newFragment.show(((Activity) getContext()).getFragmentManager(), "request");
    }

    public static class SwitchRequestFragment extends DialogFragment {

        private INDISwitchProperty prop;
        private PropPref propPref;

        public void setArguments(INDISwitchProperty prop, PropPref propPref) {
            this.prop = prop;
            this.propPref = propPref;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            final ArrayList<INDIElement> elements = prop.getElementsAsList();
            String[] elementsString = new String[elements.size()];
            final boolean[] elementsChecked = new boolean[elements.size()];
            for (int i = 0; i < elements.size(); i++) {
                elementsString[i] = elements.get(i).getLabel();
                elementsChecked[i] = ((INDISwitchElement) (elements.get(i))).getValue() == SwitchStatus.ON;
            }

            builder.setMultiChoiceItems(elementsString, elementsChecked,
                    new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            elementsChecked[which] = isChecked;
                        }
                    });

            builder.setTitle(prop.getLabel());

            if (prop.getPermission() != Constants.PropertyPermissions.RO) {
                builder.setPositiveButton(R.string.send_request, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            for (int i = 0; i < elements.size(); i++) {
                                elements.get(i).setDesiredValue(elementsChecked[i] ? SwitchStatus.ON : SwitchStatus.OFF);
                            }

                        } catch (INDIValueException | IllegalArgumentException e) {
                            Toast toast = Toast.makeText(Application.getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG);
                            toast.show();
                            Application.log(Application.getContext().getResources().getString(R.string.error) + e.getLocalizedMessage());
                        }
                        propPref.sendChanges();
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