package no.lqasse.timeforcoffee;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

import no.lqasse.timeforcoffee.Main.MainActivity;
import no.lqasse.timeforcoffee.Models.TimerSet;

/**
 * Created by lassedrevland on 04.05.15.
 */
public class DatamapListenerService extends WearableListenerService {
    public static final String OPEN_WEAR_APP_KEY = "startapp";
    public static final String TIMERLIST_KEY = "timers";
    public static final String DATAMAP_DIRECTORY = "/timeforcoffee";

    private ArrayList<TimerSet> timers = new ArrayList<>();
    private DataStorageManager dataStorageManager;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        DataMap dataMap;
        for (DataEvent event : dataEvents){
            if (event.getType() == DataEvent.TYPE_CHANGED){
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(DATAMAP_DIRECTORY)){
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();

                    ArrayList<String> timers = dataMap.getStringArrayList(TIMERLIST_KEY);
                    dataStorageManager = new DataStorageManager();
                    dataStorageManager.loadFile(DatamapListenerService.this);

                    this.timers.clear();
                    for (String s : timers) {
                        try {
                            this.timers.add(new TimerSet(s));
                        } catch (JSONException e) {
                            Log.d("Main Wear", "JSON exception");
                        }
                    }

                    Collections.sort(this.timers);
                    dataStorageManager.write(this.timers);

                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(TIMERLIST_KEY, timers);
                    Intent i ;

                    Log.d("onDataChanged", timers.toString());
                    if (dataMap.getBoolean(OPEN_WEAR_APP_KEY)){
                        i = new Intent(this, MainActivity.class);
                        i.putExtras(bundle);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                }
            }
        }
        super.onDataChanged(dataEvents);
    }






}
