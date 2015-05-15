package no.lqasse.timeforcoffee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

import no.lqasse.timeforcoffee.models.TimerSet;

/**
 * Created by lassedrevland on 07.05.15.
 */
public abstract class TimerIntentReceiver extends Activity {

    protected ArrayList<TimerSet> timers = new ArrayList<>();

    protected void loadExtras(Intent intent){
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





}
