package no.lqasse.timeforcoffee.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Collections;
import no.lqasse.timeforcoffee.DataStorageManager;
import no.lqasse.timeforcoffee.DatamapListenerService;
import no.lqasse.timeforcoffee.Models.TimerSet;
import no.lqasse.timeforcoffee.R;

public class MainActivity extends FragmentActivity implements DataStorageManager.Observer {
    private static final String LOG_IDENTIFIER = "MainActivity";
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private ArrayList<TimerSet> timers = new ArrayList<>();
    private DataStorageManager dataStorageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataStorageManager = new DataStorageManager();
        dataStorageManager.loadFile(this);
        dataStorageManager.observe(this);

                viewPager = (ViewPager) findViewById(R.id.pager);
                pagerAdapter = new PagerAdapter(getSupportFragmentManager(),MainActivity.this);
                viewPager.setAdapter(pagerAdapter);
                if (getIntent().hasExtra("timers")) {
                    loadExtras(getIntent());
                } else {
                    dataStorageManager.refresh(timers);
                }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(DatamapListenerService.TIMERLIST_KEY)){
            loadExtras(intent);
        }
    }

    private void loadExtras(Intent intent){
        Log.d("TimerIntentReceiver", "LoadExtras");
        timers.clear();
        Bundle extras = intent.getExtras();
        ArrayList<String> timers = extras.getStringArrayList(DatamapListenerService.TIMERLIST_KEY);
        Log.d("TimerIntentReceiver", timers.toString());
        for(String s:timers){
            try {
                this.timers.add(new TimerSet(s));
            } catch (JSONException e){
                Log.d("Main Wear", "JSON exception");
            }
        }
        Collections.sort(timers);
    }

    public ArrayList<TimerSet> getTimers(){
        return timers;
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
    }
}
