package no.lqasse.timeforcoffee;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import no.lqasse.timeforcoffee.models.TimerSet;

/**
 * Created by lassedrevland on 04.05.15.
 */
public class ListFragment extends Fragment implements DeleteDialog.Listener, PreferencesManager.Listener {

    private View layout;
    private ListView timerList;
    FloatingActionButton fab;
    private TimerListAdapter adaper;
    private CompanionAppActivity activity;
    private ArrayList<TimerSet> timers = new ArrayList<>();

    private DataStorageManager dataStorageManager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (CompanionAppActivity) activity;

        dataStorageManager = DataStorageManager.getInstance();
        dataStorageManager.refresh(timers);



    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_companion_app, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_sync:
                activity.syncData(false);
                return true;
            case R.id.action_start_app:
                activity.syncData(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        layout = inflater.inflate(R.layout.list_fragment,container,false);
        timerList = (ListView) layout.findViewById(R.id.timerList);
        fab = (FloatingActionButton) layout.findViewById(R.id.fab);
        adaper = new TimerListAdapter(activity,timers);
        timerList.setAdapter(adaper);
        adaper.notifyDataSetChanged();




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onNewTimer(layout);
            }
        });



        timerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                activity.onListItemClick(position);
            }

        });

        timerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                DeleteDialog fragment = DeleteDialog.newInstance(ListFragment.this, timers.get(position), position);
                fragment.show(getFragmentManager(), "DeleteORedit");

                return true;
            }
        });




        return layout;
    }


    public interface Listener{
        void onNewTimer(View layout);
        void syncData(Boolean openApp);
        void onListItemClick(int index);
    }


    @Override
    public void onDestroyView() {

        ((ViewGroup) layout).removeAllViews();
        //7preferencesManager.unRegisterListener(this);



        super.onDestroyView();
    }

    @Override
    public void onDeleteClick(int indexToDelete) {
       dataStorageManager.delete(dataStorageManager.getBufferedTimers().get(indexToDelete));
       timers.clear();
        timers.addAll(dataStorageManager.getBufferedTimers());
        adaper.notifyDataSetChanged();

    }

    @Override
    public void dataChanged() {
        adaper.notifyDataSetChanged();
        activity.syncData(false);
    }
}
