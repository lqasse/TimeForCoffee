package no.lqasse.timeforcoffee.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import no.lqasse.timeforcoffee.R;
import no.lqasse.timeforcoffee.Models.Action;

/**
 * Created by lassedrevland on 04.05.15.
 */
public class ActionListAdapter extends ArrayAdapter<Action> {

    private ArrayList<Action> actions;
    private Context context;
    public  ActionListAdapter(Context context, ArrayList<Action> actions){
        super(context, R.layout.action_list_item,actions);

        this.actions = actions;
        this.context = context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);


        View layout = inflater.inflate(R.layout.action_list_item, parent, false);

        TextView title = (TextView) layout.findViewById(R.id.title);
        TextView description = (TextView) layout.findViewById(R.id.description);
        TextView duration = (TextView) layout.findViewById(R.id.duration);

        title.setText(actions.get(position).getTitle());
        duration.setText(String.valueOf(actions.get(position).getDuration()));
        description.setText(actions.get(position).getDescription());




        return layout;
    }
}
