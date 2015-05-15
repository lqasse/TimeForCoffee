package no.lqasse.timeforcoffee.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lassedrevland on 04.05.15.
 */
public class TimerSet implements Comparable<TimerSet>{

    private static final String TITLE_JSON_KEY = "title";
    private static final String COFFEE_JSON_KEY = "coffee";
    private static final String TEMPERATURE_JSON_KEY = "temperature";
    private static final String ACTIONS_JSON_KEY = "actions";
    private static final String WATER_JSON_KEY = "water";
    private ArrayList<Action> actions = new ArrayList<>();
    private String title;
    private int totalTime;
    private String temperature = "";
    private String coffeeAmount = "";
    private String waterAmount = "";


    public TimerSet(JSONObject data) throws JSONException{


        this.title =            data.getString(TITLE_JSON_KEY);
        this.coffeeAmount =     data.getString(COFFEE_JSON_KEY);
        this.temperature =      data.getString(TEMPERATURE_JSON_KEY);
        this.waterAmount =      data.getString(WATER_JSON_KEY);


        JSONArray actionsJSON = data.getJSONArray(ACTIONS_JSON_KEY);


        this.actions.clear();
        for ( int i = 0; i<actionsJSON.length(); i++){
            JSONArray item = actionsJSON.getJSONArray(i);
            this.actions.add(new Action(item.getString(0), item.getInt(1), item.getString(2)));

        }

        calculateTotalDuration();
    }


    public TimerSet(String json) throws JSONException{
        JSONObject data = new JSONObject(json);

        this.title =            data.getString(TITLE_JSON_KEY);
        this.coffeeAmount =     data.getString(COFFEE_JSON_KEY);
        this.temperature =      data.getString(TEMPERATURE_JSON_KEY);
        this.waterAmount =      data.getString(WATER_JSON_KEY);


        JSONArray actionsJSON = data.getJSONArray(ACTIONS_JSON_KEY);


        this.actions.clear();
        for ( int i = 0; i<actionsJSON.length(); i++){
            JSONArray item = actionsJSON.getJSONArray(i);
            this.actions.add(new Action(item.getString(0), item.getInt(1), item.getString(2)));

        }

        calculateTotalDuration();




    }

    private void calculateTotalDuration(){
        int totalTime =0;
        for (Action action:this.actions){
            totalTime += action.getDuration();
        }
        this.totalTime = totalTime;
    }


    public TimerSet(String title, String temperature, String waterAmount, String coffeeAmount,ArrayList<Action> actions) {
        this.actions = actions;
        this.title = title;
        this.waterAmount = waterAmount;
        this.coffeeAmount = coffeeAmount;
        this.temperature = temperature;

        calculateTotalDuration();

    }


    public ArrayList<Action> getActions() {
        return actions;
    }

    public Action getAction(int index){
        return actions.get(index);
    }

    public String getTitle() {
        return title;
    }

    public String getDetailsSummary(){
        return coffeeAmount +" / " + waterAmount +" / " + temperature;
    }
    public String getTemperature() {
        return temperature;
    }

    public String getCoffeeAmount() {
        return coffeeAmount;
    }

    public String getWaterAmount() {
        return waterAmount;
    }

    public int getTotalTime() {

        return totalTime;
    }

    @Override
    public String toString() {
        String timers = "";
        for (Action action:actions){
            timers += action.toString();
        }

        return "Series:"+ totalTime +": " + timers;
    }

    public String getJSON() throws JSONException{

        JSONArray actionsJSON = new JSONArray();
        JSONObject timerJSON = new JSONObject();
        timerJSON.put(TITLE_JSON_KEY, title);
        timerJSON.put(COFFEE_JSON_KEY,coffeeAmount);
        timerJSON.put(TEMPERATURE_JSON_KEY,temperature);
        timerJSON.put(WATER_JSON_KEY,waterAmount);


        for (Action a:actions){
            actionsJSON.put(a.getJSON());
        }
        timerJSON.put(ACTIONS_JSON_KEY, actionsJSON);

        return timerJSON.toString();
    }
    public String getNextActionsString(int current_index){
        String out = "";
        for (int i = current_index+1;i<actions.size();i++){
            out += actions.get(i).getSummaryString() +", ";
        }

        return out;


    }

    public String getAllActionsString(){
        String out = "";
        for (int i = 0;i<actions.size();i++){
            out += actions.get(i).getSummaryString() +", ";

        }

        return out;
    }

    @Override
    public int compareTo(TimerSet timerSet) {

        return this.title.compareTo(timerSet.title);
    }
}
