package no.lqasse.timeforcoffee;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import no.lqasse.timeforcoffee.Models.TimerSet;

public class MainActivity extends TimerIntentReceiver implements DataStorageManager.Observer{
    private static final String LOG_IDENTIFIER = "MainActivity";

    private TextView mTextView;
    private ListView timerList;
    private ArrayAdapter<TimerSet> adapter;
    private TimerSet selectedTimer = null;
    private FragmentManager fragmentManager;
    TimerActivity timerFragment;

    private DataStorageManager dataStorageManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataStorageManager = new DataStorageManager();
        dataStorageManager.loadFile(this);
        dataStorageManager.observe(this);


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        fragmentManager = getFragmentManager();

        adapter = new TimerListAdapter(MainActivity.this, timers);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {

            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {
                timerList = (ListView) findViewById(R.id.timerList);
                timerList.setAdapter(adapter);
                Log.d("Mainapp Started", timerList.toString());


                if (getIntent().hasExtra(WearPreferencesManager.TIMERLIST_KEY)) {
                    loadExtras(getIntent());
                } else {
                    dataStorageManager.refresh(timers);
                }


                timerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        selectedTimer = timers.get(position);
                        try {
                            Intent i = new Intent(MainActivity.this, TimerActivity.class);
                            i.putExtra(TimerActivity.TIMER_INTENT_KEY, selectedTimer.getJSON());
                            startActivity(i);
                        } catch (JSONException e) {
                            log("Could not parse timer to json");

                        }


                    }
                });
            }
        });






    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.hasExtra(DatamapListenerService.TIMERLIST_KEY)){
            loadExtras(intent);
        }


        adapter.notifyDataSetChanged();
    }



    private void log(String data){
        Log.d(LOG_IDENTIFIER, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDataChanged() {
        dataStorageManager.refresh(this.timers);
        adapter.notifyDataSetChanged();
    }
}
