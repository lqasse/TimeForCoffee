package no.lqasse.timeforcoffee.Main;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import no.lqasse.timeforcoffee.wear_Models.Preferences;
import no.lqasse.timeforcoffee.R;

/**
 * Created by lassedrevland on 02.01.16.
 */
public class SettingsFragment extends android.support.v4.app.Fragment {
    Preferences preferences;
    private View layout;
    CheckBox vibrate;

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.settings_fragment, container, false);
        vibrate = (CheckBox) layout.findViewById(R.id.vibrate);

        vibrate.setChecked(preferences.vibrate);
        vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.vibrate = isChecked;
                Preferences.set(preferences, getActivity());
            }
        });


        return layout;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        preferences = Preferences.get(getActivity());
    }
}
