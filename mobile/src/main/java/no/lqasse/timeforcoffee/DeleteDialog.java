package no.lqasse.timeforcoffee;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import no.lqasse.timeforcoffee.Models.TimerSet;

/**
 * Created by lassedrevland on 09.05.15.
 */
public class DeleteDialog extends DialogFragment {

    private String title;
    private int index;

    public static DeleteDialog newInstance(ListFragment targetFragment, TimerSet timer, int index){
        DeleteDialog dialog = new DeleteDialog();
        Bundle args  = new Bundle();
        args.putString("title", timer.getTitle());
        args.putInt("index", index);
        dialog.setTargetFragment(targetFragment,0);
        dialog.setArguments(args);
        return dialog;
    }




    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {

        title = getArguments().getString("title");
        index = getArguments().getInt("index");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());


        dialogBuilder
                .setTitle(title)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ( (Listener) getTargetFragment()).onDeleteClick(index);

                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {



                    }
                });






        return dialogBuilder.create();
    }

    public interface Listener{
        void onDeleteClick(int indexToDelete);

    }


}
