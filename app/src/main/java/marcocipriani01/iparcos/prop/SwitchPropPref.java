package marcocipriani01.iparcos.prop;

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

import marcocipriani01.iparcos.Application;
import marcocipriani01.iparcos.R;
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
        ArrayList<INDIElement> elements = prop.getElementsAsList();
        if (elements.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            int[] starts = new int[elements.size()];
            int[] ends = new int[elements.size()];
            starts[0] = 0;
            for (int i = 0; i < elements.size(); i++) {
                starts[i] = stringBuilder.length();
                stringBuilder.append(elements.get(i).getLabel()).append(" ");
                ends[i] = stringBuilder.length();
            }
            Spannable summaryText = new SpannableString(stringBuilder.toString());
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            for (int i = 0; i < elements.size(); i++) {
                if (((INDISwitchElement) (elements.get(i))).getValue() == SwitchStatus.ON) {
                    summaryText.setSpan(boldSpan, starts[i], ends[i], 0);
                }
            }
            return summaryText;

        } else {
            return new SpannableString(getContext().getString(R.string.no_indi_elements));
        }
    }

    @Override
    protected void onClick() {
        if (!getSummary().toString().equals(getContext().getString(R.string.no_indi_elements))) {
            SwitchRequestFragment requestFragment = new SwitchRequestFragment();
            requestFragment.setArguments((INDISwitchProperty) prop, this);
            requestFragment.show(((Activity) getContext()).getFragmentManager(), "request");
        }
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
            final Context context = getActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            final ArrayList<INDIElement> elements = prop.getElementsAsList();
            String[] elementsString = new String[elements.size()];
            final boolean[] elementsChecked = new boolean[elements.size()];
            for (int i = 0; i < elements.size(); i++) {
                INDISwitchElement switchElement = (INDISwitchElement) (elements.get(i));
                elementsString[i] = switchElement.getLabel();
                elementsChecked[i] = switchElement.getValue() == SwitchStatus.ON;
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
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            for (int i = 0; i < elements.size(); i++) {
                                elements.get(i).setDesiredValue(elementsChecked[i] ? SwitchStatus.ON : SwitchStatus.OFF);
                            }

                        } catch (INDIValueException | IllegalArgumentException e) {
                            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            Application.log(context.getResources().getString(R.string.error) + e.getLocalizedMessage());
                        }
                        propPref.sendChanges();
                    }
                });
                builder.setNegativeButton(R.string.cancel_request, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

            } else {
                builder.setNegativeButton(R.string.back_request, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
            }

            return builder.create();
        }
    }
}