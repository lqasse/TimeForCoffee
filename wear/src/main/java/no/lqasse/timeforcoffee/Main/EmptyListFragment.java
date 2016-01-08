package no.lqasse.timeforcoffee.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.wearable.activity.ConfirmationActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import javax.xml.datatype.Duration;

import no.lqasse.timeforcoffee.CompanionAppLauncher;
import no.lqasse.timeforcoffee.R;

/**
 * Created by lassedrevland on 07.01.16.
 */
public class EmptyListFragment extends Fragment {
    View layout;
    Button openApp;
    CompanionAppLauncher launcher;

    public static EmptyListFragment newInstance(){
        return new EmptyListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        launcher = new CompanionAppLauncher(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.emptylist_fragment,container,false);
        openApp = (Button) layout.findViewById(R.id.openApp);
        openApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.sendOpenAppMessage();
                Intent intent = new Intent(getActivity(), ConfirmationActivity.class);
                intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
                startActivity(intent);

            }
        });

        Toast.makeText(getActivity(),R.string.empty_timerlist,Toast.LENGTH_LONG).show();
        return layout;
    }

    @Override
    public void onDestroy() {
        launcher.stop();
        super.onDestroy();
    }
}
