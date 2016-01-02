package no.lqasse.timeforcoffee.Utilities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;

import no.lqasse.timeforcoffee.Models.TimerSet;

/**
 * Created by lassedrevland on 05.05.15.
 */
public class WearHandler implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{


    private static final String OPEN_WEAR_APP_KEY = "startapp";
    private static final String TIMERLIST_KEY = "timers";
    private static final String DATAMAP_DIRECTORY = "/timeforcoffee";
    private static final String LOG_IDENTIFIER = "WearHandler";

    private Context context;
    private GoogleApiClient mGoogleApiClient;

    public WearHandler(Context context) {
        this.context = context;

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    public void start(){
        mGoogleApiClient.connect();
    }


    public void stop(){
        if ( null != mGoogleApiClient && mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void sync(ArrayList<TimerSet> timers, Boolean openApp){

        ArrayList<String> timersJSON = new ArrayList<>();
        for (TimerSet timer :timers){
            try {
                timersJSON.add(timer.getJSON());
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(DATAMAP_DIRECTORY);
        DataMap map = putDataMapReq.getDataMap();
        map.putBoolean(OPEN_WEAR_APP_KEY, openApp);
        map.putStringArrayList(TIMERLIST_KEY, timersJSON);
        map.putLong("timestamp", new Date().getTime());
        PutDataRequest putDataRequest = putDataMapReq.asPutDataRequest();

        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);

        Log.d(LOG_IDENTIFIER, "Syncing to datamap");
        Log.d(LOG_IDENTIFIER, timers.toString());
    }

}
