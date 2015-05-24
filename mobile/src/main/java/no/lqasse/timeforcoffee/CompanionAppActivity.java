package no.lqasse.timeforcoffee;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;


import android.view.View;
import android.view.ViewGroup;

import no.lqasse.timeforcoffee.TimerCreator.CreatorFragment;
import no.lqasse.timeforcoffee.Utilities.DataStorageManager;
import no.lqasse.timeforcoffee.Utilities.WearHandler;


public class CompanionAppActivity extends Activity implements
        CreatorFragment.Listener,
        ListFragment.Listener{



    private FragmentManager fragmentManager;
    private WearHandler wearHandler;
    private ViewGroup layout;
    private ListFragment listFragment;
    private PreferencesManager preferencesManager;
    private DataStorageManager dataStorageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companion_app);
        //preferencesManager = PreferencesManager.getInstance();

        dataStorageManager = DataStorageManager.getInstance();
        dataStorageManager.loadFile(this);



        wearHandler = new WearHandler(this);
        fragmentManager = getFragmentManager();


        layout = (ViewGroup) findViewById(R.id.mainLayout);


        listFragment = new ListFragment();

        fragmentManager.beginTransaction()

                .replace(R.id.mainLayout, listFragment)
                .commit();




    }



    @Override
    protected void onStop() {
        wearHandler.stop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        wearHandler.start();
        super.onStart();
    }

    @Override
    public void onNewTimer(View v) {
        Fragment creatorFragment = new CreatorFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.mainLayout, creatorFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void timerSaved() {


        wearHandler.sync(dataStorageManager.getBufferedTimers(), false);        
        listFragment = new ListFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.mainLayout, listFragment)
                .commit();



    }

    @Override
    public void syncData(Boolean openApp) {

        wearHandler.sync(dataStorageManager.getBufferedTimers(), openApp);

    }




    @Override
    public void onListItemClick(int index) {
        CreatorFragment creatorFragment = CreatorFragment.newEditInstance(index);

        fragmentManager.beginTransaction()
                .replace(R.id.mainLayout, creatorFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {

        if (fragmentManager.getBackStackEntryCount() > 0){
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }

    }
}
