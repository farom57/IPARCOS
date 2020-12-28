package marcocipriani01.iparcos.prop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import org.indilib.i4j.Constants;
import org.indilib.i4j.client.INDIProperty;
import org.indilib.i4j.client.INDISwitchElement;
import org.indilib.i4j.client.INDISwitchProperty;
import org.indilib.i4j.client.INDIValueException;

import java.util.List;

import marcocipriani01.iparcos.IPARCOSApp;
import marcocipriani01.iparcos.R;

public class SwitchPropPref extends PropPref<INDISwitchElement> {

    public SwitchPropPref(Context context, INDIProperty<INDISwitchElement> prop) {
        super(context, prop);
    }

    /**
     * Create the summary rich-text string
     *
     * @return the summary
     */
    @Override
    protected Spannable createSummary() {
        List<INDISwitchElement> elements = prop.getElementsAsList();
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
                if (((INDISwitchElement) (elements.get(i))).getValue() == Constants.SwitchStatus.ON) {
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
            requestFragment.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "request");
        }
    }

    public static class SwitchRequestFragment extends DialogFragment {

        private INDISwitchProperty prop;
        private PropPref<INDISwitchElement> propPref;

        public void setArguments(INDISwitchProperty prop, PropPref<INDISwitchElement> propPref) {
            this.prop = prop;
            this.propPref = propPref;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Context context = getActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            final List<INDISwitchElement> elements = prop.getElementsAsList();
            String[] elementsString = new String[elements.size()];
            final boolean[] elementsChecked = new boolean[elements.size()];
            for (int i = 0; i < elements.size(); i++) {
                INDISwitchElement switchElement = (INDISwitchElement) (elements.get(i));
                elementsString[i] = switchElement.getLabel();
                elementsChecked[i] = switchElement.getValue() == Constants.SwitchStatus.ON;
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
                                elements.get(i).setDesiredValue(elementsChecked[i] ? Constants.SwitchStatus.ON : Constants.SwitchStatus.OFF);
                            }

                        } catch (INDIValueException | IllegalArgumentException e) {
                            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            if (context != null) {
                                IPARCOSApp.log(context.getResources().getString(R.string.error) + e.getLocalizedMessage());
                            }
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