package no.lqasse.timeforcoffee;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import no.lqasse.timeforcoffee.models.TimerSet;

/**
 * Created by lassedrevland on 04.05.15.
 */
public class PreferencesManager {
    private static final String PREFERENCES_NAMESPACE = "no.lqasse.timeforcoffe.settings";
    private static final String TIMERS_IDENTIFICATOR = "TIMERS";

    private static PreferencesManager instance;
    private ArrayList<TimerSet> timers = new ArrayList<>();
    private ArrayList<Listener> listeners = new ArrayList<>();


    private SharedPreferences preferences;


    public static PreferencesManager getInstance(){
        if (instance == null){
            instance = new PreferencesManager();
            return instance;
        } else {
            return instance;
        }
    }

    public void loadPreferences(Context context){
        preferences = context.getSharedPreferences(PREFERENCES_NAMESPACE, context.MODE_PRIVATE);

        Set<String> timers = preferences.getStringSet(TIMERS_IDENTIFICATOR, null);
        this.timers.clear();
        for (String timerJSON : timers){
            try {
                this.timers.add(new TimerSet(timerJSON));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        Collections.sort(this.timers);
        notifyListeners();



    }

    public ArrayList<TimerSet> getTimers() {
        return timers;

    }


    public void putTimer(TimerSet newTimer){

        timers.add(newTimer);
        Collections.sort(this.timers);

        Set<String> timers = new HashSet<>();

        for (TimerSet timer: this.timers){
            try {
                timers.add(timer.getJSON());
            } catch (JSONException e){
                e.printStackTrace();
            }
        }


        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(TIMERS_IDENTIFICATOR,timers);
        editor.commit();

        notifyListeners();


    }

    public  void printTimers(){

        Set<String> timers = preferences.getStringSet(TIMERS_IDENTIFICATOR, null);


        if (TIMERS_IDENTIFICATOR != null){
            for (String timer: timers){
                Log.d("Timer in prefs", timer);

            }
        }
    }


    public void deleteTimer(TimerSet timerToDelete){
        timers.remove(timerToDelete);

        Set<String> timers = new HashSet<>();

        for (TimerSet timer: this.timers){
            try {
                timers.add(timer.getJSON());
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

            SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet(TIMERS_IDENTIFICATOR,timers);
            editor.commit();

        notifyListeners();



    }

    public void deleteTimer(int index){
        deleteTimer(timers.get(index));
    }
    public interface Listener{
        void dataChanged();
    }

    public void registerListener(Listener listener){
        listeners.add(listener);
    }

    public void unRegisterListener(Listener listener){
        listeners.remove(listener);
    }

    private void notifyListeners(){
        for (Listener listener : listeners){
         listener.dataChanged();
        }
    }



}
