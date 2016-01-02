package no.lqasse.timeforcoffee;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import no.lqasse.timeforcoffee.Models.TimerSet;

/**
 * Created by lassedrevland on 04.05.15.
 */
public class TimerListAdapter extends ArrayAdapter<TimerSet> {

    private Context context;
    private ArrayList<TimerSet> timers;
    public TimerListAdapter(Context context, ArrayList<TimerSet> timers){
        super(context,R.layout.timer_list_item,timers);
        this.context = context;
        this.timers = timers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View layout = inflater.inflate(R.layout.timer_list_item, parent, false);

        TextView title = (TextView) layout.findViewById(R.id.timerTitle);
        TextView actions = (TextView) layout.findViewById(R.id.actions);
        TextView details = (TextView) layout.findViewById(R.id.details);


        title.setText(timers.get(position).getTitle());
        actions.setText(timers.get(position).getAllActions());
        details.setText(timers.get(position).getSummary());

        return layout;
    }
}
