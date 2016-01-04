package no.lqasse.timeforcoffee.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.wearable.view.WatchViewStub;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;

import no.lqasse.timeforcoffee.Models.TimerSet;
import no.lqasse.timeforcoffee.R;
import no.lqasse.timeforcoffee.TimerActivity;
import no.lqasse.timeforcoffee.TimerListAdapter;

/**
 * Created by lassedrevland on 02.01.16.
 */
public class ListFragment extends android.support.v4.app.Fragment {
    View layout;
    MainActivity main;
    TimerListAdapter timerListAdapter;
    ListView listView;
    Activity activity;
    public static ListFragment newInstance(MainActivity main) {
        Bundle args = new Bundle();
        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        fragment.setMain(main);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.list_fragment,container,false);
        timerListAdapter = new TimerListAdapter(activity,main.getTimers());

        final WatchViewStub stub = (WatchViewStub) layout.findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {
                listView = (ListView) layout.findViewById(R.id.timerList);
                listView.setAdapter(timerListAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        TimerSet selectedTimer = main.getTimers().get(position);
                        try {
                            Intent i = new Intent(main, TimerActivity.class);
                            i.putExtra(TimerActivity.TIMER_INTENT_KEY, selectedTimer.getJSON());
                            startActivity(i);
                        } catch (JSONException e) {

                        }
                    }
                });
            }
        });



        return layout;
    }

    public void setMain(MainActivity main) {
        this.main = main;
    }

}
