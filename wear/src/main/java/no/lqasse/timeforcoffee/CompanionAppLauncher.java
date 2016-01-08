package no.lqasse.timeforcoffee;

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
public class CompanionAppLauncher implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{


    private static final String OPEN_APP_PATH = "/open-companion-app";

    private Context context;
    private GoogleApiClient mGoogleApiClient;

    public CompanionAppLauncher(Context context) {
        this.context = context;

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        start();
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
        Log.d("CompanionAppL", "CONNECTED TO DATAMAP");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("CompanionAppL", "CONNECTED TO DATAMAP FAILED");
    }

    public void sendOpenAppMessage(){


        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(OPEN_APP_PATH);
        DataMap map = putDataMapReq.getDataMap();
        map.putLong("timestamp", new Date().getTime());
        PutDataRequest putDataRequest = putDataMapReq.asPutDataRequest();

        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
        Log.d("WEAR", "Sending message");


    }

}
