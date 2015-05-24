package no.lqasse.timeforcoffee;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import no.lqasse.timeforcoffee.Models.TimerSet;
import no.lqasse.timeforcoffee.Utilities.DataStorageManager;

/**
 * Created by lassedrevland on 22.05.15.
 */
public class TimerFragment extends Fragment {

    private DataStorageManager dataStorageManager;
    private Activity activity;
    private TimerSet timer;
    private int index;

    public static TimerFragment newInstance(int index){
        TimerFragment fragment = new TimerFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        fragment.setArguments(args);


        return fragment;

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity =  activity;

        dataStorageManager = dataStorageManager.getInstance();

        if (getArguments()!=null){

            index = getArguments().getInt("index");

            timer = dataStorageManager.getBufferedTimers().get(index);


        }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_creator,container,false);



        return layout;
    }
}
