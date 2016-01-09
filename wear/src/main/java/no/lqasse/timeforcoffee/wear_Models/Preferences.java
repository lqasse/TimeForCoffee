package no.lqasse.timeforcoffee.wear_Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.CompoundButton;

/**
 * Created by lassedrevland on 09.01.16.
 */
public class Preferences {
    public static final String PREFERENCE_KEY = "preferences";
    public static final String VIBRATE_KEY = "vibrate";
    public Boolean vibrate;

    public static void set(Preferences preferences, Context context){

        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_KEY,Context.MODE_PRIVATE).edit();

        editor.putBoolean(VIBRATE_KEY, preferences.vibrate);

        editor.commit();


    }

    public static Preferences get(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_KEY,Context.MODE_PRIVATE);

        Preferences preferences = new Preferences();

        preferences.vibrate = sharedPreferences.getBoolean(VIBRATE_KEY, true);

        return preferences;



    }
}
