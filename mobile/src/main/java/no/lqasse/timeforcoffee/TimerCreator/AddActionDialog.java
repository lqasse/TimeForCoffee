package no.lqasse.timeforcoffee.TimerCreator;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import no.lqasse.timeforcoffee.R;
import no.lqasse.timeforcoffee.models.Action;

/**
 * Created by lassedrevland on 10.05.15.
 */
public class AddActionDialog extends DialogFragment {
    private static final String TITLE_BUNDLEKEY = "title";
    private static final String DESCRIPTION_BUNDLEKEY = "description";
    private static final String DURATION_BUNDLEKEY = "duration";
    private static final String INDEX_BUNDLEKEY = "index";


    private TextView titleField;
    private TextView descriptionField;
    private TextView durationField;
    private Button saveButton;

    private Listener callback;

    private int index = -1;

    public static AddActionDialog newEditInstance(Action action, int index){
        AddActionDialog editDialog = new AddActionDialog();
        Bundle args = new Bundle();
        args.putString(TITLE_BUNDLEKEY, action.getTitle());
        args.putString(DESCRIPTION_BUNDLEKEY, action.getDescription());
        args.putInt(DURATION_BUNDLEKEY, action.getDuration());
        args.putInt(INDEX_BUNDLEKEY, index);

        editDialog.setArguments(args);

        return editDialog;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_action_fragment, container, false);

        getDialog().setTitle("Add action");

        callback = (Listener) getTargetFragment();

        titleField = (TextView) v.findViewById(R.id.titleField);
        descriptionField = (TextView) v.findViewById(R.id.descriptionField);
        durationField = (TextView) v.findViewById(R.id.durationField);
        saveButton = (Button) v.findViewById(R.id.addButton);

        if (getArguments()!= null){
            index = getArguments().getInt(INDEX_BUNDLEKEY);
            titleField.setText(getArguments().getString(TITLE_BUNDLEKEY));
            descriptionField.setText(getArguments().getString(DESCRIPTION_BUNDLEKEY));
            durationField.setText(String.valueOf(getArguments().getInt(DURATION_BUNDLEKEY)));


        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleField.getText().toString();
                int duration = Integer.valueOf(durationField.getText().toString());
                String description = descriptionField.getText().toString();

                callback.onDialogAddClick(new Action(title, duration, description), index);

                dismiss();
            }
        });





        return v;
    }

    public interface Listener{
        void onDialogAddClick(Action action, int index);
    }
}
