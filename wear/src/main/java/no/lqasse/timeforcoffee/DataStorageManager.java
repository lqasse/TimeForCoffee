package no.lqasse.timeforcoffee;

import android.content.Context;
import android.os.FileObserver;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import no.lqasse.timeforcoffee.wear_Models.TimerSet;

/**
 * Created by lassedrevland on 13.05.15.
 */
public class DataStorageManager {
    private final static String TIMERS_FILENAME = "timers.txt";
    private FileObserver fileObserver;

    private ArrayList<TimerSet> timers = new ArrayList<>();

    File file;
    private Context context;

    public void observe(final Context context){
        fileObserver = new FileObserver(context.getFilesDir().getPath()) {
            @Override
            public void onEvent(int i, String s) {
                if (i == FileObserver.MODIFY){
                    ((Observer) DataStorageManager.this.context).onDataChanged();
                }
            }
        };
        fileObserver.startWatching();
    }

    public void loadFile(Context context){
        this.context = context;
        file = new File(context.getFilesDir(), TIMERS_FILENAME);
    }

    public void write(ArrayList<TimerSet> timers){
        if (context == null){
            return;
        }
        try {
            JSONArray array = new JSONArray();
            FileOutputStream outputStream = new FileOutputStream(file);
            for (TimerSet timer : timers){
                array.put(new JSONObject(timer.getJSON()));
            }
            String jsonArray = array.toString();
            outputStream.write(jsonArray.getBytes());
            outputStream.close();

        } catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void write(){
        write(this.timers);
    }

    public void refresh(ArrayList<TimerSet> timers){
        this.timers.clear();
        timers.clear();
        if (context == null){
            return;
        }

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String result = br.readLine();
            br.close();
            Log.d("DataStorageManager", result);

            JSONArray data = new JSONArray(result);

            for (int i = 0;i<data.length();i++){
             try {
                 timers.add(new TimerSet(data.getJSONObject(i)));
             } catch (JSONException e){
                 Log.d("JSONException", data.getJSONObject(i).toString() + " could not be parsed");
                 e.printStackTrace();
             }
            }

        }catch (IOException e){
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }

        this.timers.addAll(timers);

        return;

    }

    public void put(TimerSet timer){
        timers.add(timer);
        write();
    }

    public void replace(TimerSet oldTimer, TimerSet newTimer){
        timers.remove(oldTimer);
        timers.add(newTimer);
        write();
    }

    public void delete(TimerSet timer){
        timers.remove(timer);
        write();
    }

    public  interface Observer{
        void onDataChanged();
    }







}
