package no.lqasse.timeforcoffee.Utilities;

import android.content.Intent;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;



import no.lqasse.timeforcoffee.CompanionAppActivity;


/**
 * Created by lassedrevland on 07.01.16.
 */
public class LaunchFromWearListenerService extends WearableListenerService {
    private static final String OPEN_APP_PATH = "/open-companion-app";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        DataMap dataMap;
        for (DataEvent event : dataEvents){
            if (event.getType() == DataEvent.TYPE_CHANGED){
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(OPEN_APP_PATH)){
                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();

                    Intent i ;

                        i = new Intent(this, CompanionAppActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);

                }
            }
        }
        super.onDataChanged(dataEvents);
    }
}
