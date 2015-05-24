package no.lqasse.timeforcoffee.Utilities;

import android.content.Context;
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
import java.util.Collections;

import no.lqasse.timeforcoffee.Models.TimerSet;

/**
 * Created by lassedrevland on 13.05.15.
 */
public class DataStorageManager {
    private final static String FILENAME = "timers.txt";
    private static DataStorageManager instance;

    private ArrayList<TimerSet> timers = new ArrayList<>();

    File file;
    private Context context;

    public static DataStorageManager getInstance(){
        if (instance == null){
            instance = new DataStorageManager();
            return instance;
        } else{
            return instance;
        }

    }
    public DataStorageManager(){

    }

    public void loadFile(Context context){
        this.context = context;
        file = new File(context.getFilesDir(), FILENAME);
    }

    public ArrayList<TimerSet> getBufferedTimers(){
        return this.timers;
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
        Collections.sort(timers);
        Collections.sort(this.timers);

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





}
