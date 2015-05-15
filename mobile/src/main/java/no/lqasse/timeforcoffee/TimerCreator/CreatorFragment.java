package no.lqasse.timeforcoffee.TimerCreator;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import org.json.JSONException;

import java.util.ArrayList;

import no.lqasse.timeforcoffee.ActionListAdapter;
import no.lqasse.timeforcoffee.CompanionAppActivity;
import no.lqasse.timeforcoffee.DataStorageManager;
import no.lqasse.timeforcoffee.PreferencesManager;
import no.lqasse.timeforcoffee.R;
import no.lqasse.timeforcoffee.models.Action;
import no.lqasse.timeforcoffee.models.TimerSet;

/**
 * Created by lassedrevland on 04.05.15.
 */
public class CreatorFragment extends Fragment  implements AddActionDialog.Listener{

    private ArrayList<Action> actions = new ArrayList<>();
    private Button saveButton;
    private Button addTimerButton;
    private EditText timerTitle;
    private EditText temperatureField;
    private EditText coffeeAmountField;
    private EditText waterAmountField;
    private ListView actionList;

    private EditText actionTitleField;
    private EditText actionDescriptionField;
    private EditText actionDurationField;
    private Button addActionButton;
    private TimerSet timer;

    private int index = -1;

    private CompanionAppActivity activity;

    private ActionListAdapter adapter;
    private PreferencesManager preferencesManager;
    private DataStorageManager dataStorageManager;



    public static CreatorFragment newEditInstance(int index){
        CreatorFragment fragment = new CreatorFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        fragment.setArguments(args);


        return fragment;

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (CompanionAppActivity) activity;
        //preferencesManager = PreferencesManager.getInstance();
        dataStorageManager = dataStorageManager.getInstance();

       if (getArguments()!=null){

           index = getArguments().getInt("index");

           timer = dataStorageManager.getBufferedTimers().get(index);


       }


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.creator_fragment,container,false);
        View footer = inflater.inflate(R.layout.action_list_footer,null);
        View header = inflater.inflate(R.layout.action_list_header, null);

        saveButton = (Button)layout.findViewById(R.id.saveButton);

        coffeeAmountField= (EditText) header.findViewById(R.id.coffeeAmountField);
        temperatureField = (EditText) header.findViewById(R.id.waterTemperatureField);
        waterAmountField = (EditText) header.findViewById(R.id.waterAmountField);
        timerTitle = (EditText)       header.findViewById(R.id.nameField);

        actionList = (ListView) layout.findViewById(R.id.actionList);


        adapter = new ActionListAdapter(activity,actions);
        actionList.setAdapter(adapter);
        addActionButton =             (Button) footer.findViewById(R.id.addButton);
        actionDurationField =       (EditText) footer.findViewById(R.id.durationField);
        actionDescriptionField =    (EditText) footer.findViewById(R.id.descriptionField);
        actionTitleField =          (EditText) footer.findViewById(R.id.titleField);


        addActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String duration = actionDurationField.getText().toString();
                if (actionTitleField.equals("")){
                    actionTitleField.setHintTextColor(Color.RED);
                }else if (duration.equals("") || duration.equals("0")){
                    actionDurationField.setHintTextColor(Color.RED);
                } else {
                    Action action = new Action(
                            actionTitleField.getText().toString(),
                            actionDurationField.getText().toString(),
                            actionDescriptionField.getText().toString());
                    actions.add(action);

                    actionTitleField.setHintTextColor(getResources().getColor(R.color.secondary_text));
                    actionDurationField.setHintTextColor(getResources().getColor(R.color.secondary_text));
                    actionTitleField.setText("");
                    actionDurationField.setText("");
                    actionDescriptionField.setText("");
                    adapter.notifyDataSetChanged();
                }



            }
        });


        actionList.addFooterView(footer);
        actionList.addHeaderView(header);

        actionList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {



            }
        });

        if (timer != null){
            timerTitle.setText(timer.getTitle());
            actions.addAll(timer.getActions());
            coffeeAmountField.setText(timer.getCoffeeAmount());
            waterAmountField.setText(timer.getWaterAmount());
            temperatureField.setText(timer.getTemperature());
        }

        actionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                AddActionDialog addDialog = AddActionDialog.newEditInstance(actions.get(position-1), position-1);
                addDialog.setTargetFragment(CreatorFragment.this, 0);
                addDialog.show(getFragmentManager(), "addDialog");
            }
        });




        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String title = timerTitle.getText().toString();
                String waterAmount = waterAmountField.getText().toString();
                String coffeeAmount = coffeeAmountField.getText().toString();
                String temperature = temperatureField.getText().toString();

                if (title.equals("")){


                    timerTitle.setHintTextColor(Color.RED);
                    timerTitle.setHint("Title can not be empty");

                } else if (actions.size() == 0){

                    actionTitleField.setHintTextColor(Color.RED);
                    actionDurationField.setHintTextColor(Color.RED);
                } else {

                    if (index == -1){

                        dataStorageManager.put(new TimerSet(title,temperature,waterAmount,coffeeAmount,actions));
                    }  else{
                        dataStorageManager.replace(timer, new TimerSet(title,temperature,waterAmount,coffeeAmount,actions));

                    }

                    activity.timerSaved();

                }


            }
        });


        return layout;
    }

    public interface Listener{
        void timerSaved();

    }

    public void onDialogAddClick(Action action, int index) {

        if (index == -1){
            actions.add(action);



        } else {
            actions.remove(index);
            actions.add(index, action);
        }

        adapter.notifyDataSetChanged();


    }


}
