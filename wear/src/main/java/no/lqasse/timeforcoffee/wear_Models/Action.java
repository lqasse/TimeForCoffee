package no.lqasse.timeforcoffee.wear_Models;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by lassedrevland on 04.05.15.
 */
public class Action {

    private String label;
    private String description;
    private int duration;

    public Action(String label, String duration, String description) {
        this.label = label;
        this.duration = Integer.valueOf(duration);
        this.description = description;
    }

    public Action(String label, int duration,String description) {
        this.label = label;
        this.duration = duration;
        this.description = description;
    }

    public String getTitle() {
        return label;
    }

    public int getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Action:"+label+":"+ duration;
    }


    public JSONArray getJSON() throws JSONException{

        JSONArray item = new JSONArray();
        item.put(label);
        item.put(duration);
        item.put(description);

        return item;

    }

    public String getSummaryString(){
        return this.getTitle() + " (" +getDuration() + ")";
    }
}
