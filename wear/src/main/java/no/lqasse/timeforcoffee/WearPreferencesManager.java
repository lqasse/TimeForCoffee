package no.lqasse.timeforcoffee;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.Preference;
import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import no.lqasse.timeforcoffee.Models.TimerSet;

/**
 * Created by lassedrevland on 04.05.15.
 */
public class WearPreferencesManager implements Preference.OnPreferenceChangeListener {
    public static final String PREFERENCES_NAMESPACE = "no.lqasse.timeforcoffe.settings";
    public static final String TIMERS_IDENTIFICATOR = "TIMERS";
    public static final String TIMERLIST_KEY = "timers";

    private boolean loaded = false;
    private Context context;

    protected SharedPreferences preferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (context != null && key.equals(TIMERS_IDENTIFICATOR)){

                if (!(context instanceof SyncService)){
                    ((Listener) context).preferencesChanged();
                    Log.d("WearPreferenceManager", "Preferences changed");
                }

            }

        }
    };


    public WearPreferencesManager(Context context){
        this.context = context;

        getPreferences();
    }

    public void getPreferences(){
        loaded = false;
        GetTimersAsync get = new GetTimersAsync();
        get.execute(context);
        preferences = context.getSharedPreferences(PREFERENCES_NAMESPACE, context.MODE_MULTI_PROCESS);



    }

    public ArrayList<TimerSet> getTimers(){

        Set<String> timerHashMap = new HashSet<>();


        timerHashMap.addAll(preferences.getStringSet(TIMERS_IDENTIFICATOR, null));


        Log.d("PrefsManager", preferences.getAll().toString());
        ArrayList<TimerSet> timers = new ArrayList<>();

        if (timerHashMap != null){
            for (String timer: timerHashMap){
                try {
                    timers.add(new TimerSet(timer));
                } catch (JSONException e){
                    Log.d("JSONException", timer);
                    e.printStackTrace();
                }


            }
        }

        Collections.sort(timers);

        return timers;
    }


    public void putTimers(ArrayList<TimerSet> timers){

        Set<String> timersSet = new HashSet<>();

        for (TimerSet timer:timers){
            try {
                timersSet.add(timer.getJSON());
            } catch (JSONException e){
                e.printStackTrace();
                Log.d("Preferences", "JSON ERROR");
            }
        }


        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(TIMERS_IDENTIFICATOR,timersSet);
        editor.commit();


    }


    public class GetTimersAsync extends AsyncTask<Context, Void, SharedPreferences>{
        private SharedPreferences sharedPreferences;

        @Override
        protected SharedPreferences doInBackground(Context... contexts) {
            sharedPreferences = contexts[0].getSharedPreferences(PREFERENCES_NAMESPACE, contexts[0].MODE_PRIVATE);


            return sharedPreferences;
        }

        @Override
        protected void onPostExecute(SharedPreferences preferences) {
            loaded = true;
            this.sharedPreferences = preferences;

            preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

            ((Listener) context).preferencesLoaded();

            Set<String> timerHashMap = preferences.getStringSet(TIMERS_IDENTIFICATOR, null);

            ArrayList<TimerSet> timers = new ArrayList<>();

            if (timerHashMap != null){
                for (String timer: timerHashMap){
                    try {
                        TimerSet t = new TimerSet(timer);

                        timers.add(t);
                    } catch (JSONException e){
                        Log.d("JSONException", timer);
                        e.printStackTrace();
                    }


                }
            }

            super.onPostExecute(preferences);
        }
    }

    public interface Listener{
        void preferencesLoaded();
        void preferencesChanged();
    }

    public void unregisterPreferenceListener(){
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        return false;
    }


}
