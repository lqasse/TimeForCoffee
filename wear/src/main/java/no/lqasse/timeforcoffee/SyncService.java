package no.lqasse.timeforcoffee;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

import no.lqasse.timeforcoffee.Models.TimerSet;

/**
 * Created by lassedrevland on 07.05.15.
 */
public class SyncService extends Service{
    private DataStorageManager dataStorageManager;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getExtras() != null){
            loadExtras(intent);

            dataStorageManager = new DataStorageManager();
            dataStorageManager.loadFile(this);
            dataStorageManager.write(timers);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    protected ArrayList<TimerSet> timers = new ArrayList<>();

    protected void loadExtras(Intent intent) {
        timers.clear();
        Bundle extras = intent.getExtras();

        ArrayList<String> timers = extras.getStringArrayList(DatamapListenerService.TIMERLIST_KEY);
        Log.d("SyncService Loaded: ", timers.toString());

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}




