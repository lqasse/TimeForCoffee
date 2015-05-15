package no.lqasse.timeforcoffee;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;

import no.lqasse.timeforcoffee.models.TimerSet;

/**
 * Created by lassedrevland on 06.05.15.
 */
public class TimerActivity extends Activity {
    private static final int TIMER_INTERVAL_MILLIS = 50;
    public static final String TIMER_INTENT_KEY = "timer";
    public static final String LOG_IDENIFIER = "TimerACtivity";
    private MainActivity activity;
    private TextView title;
    private TextView count;
    private TextView nextTimers;
    private TextView description;


    private TimerSet timer;
    private View layout;
    private Handler handler = new Handler();
    private int current_action_index = 0;
    private float current_count = 0.0f;
    private Boolean isStarted = false;
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {

            drawCanvas(current_count, timer.getAction(current_action_index).getDuration());


            String count_string = new DecimalFormat("0").format(((float) timer.getAction(current_action_index).getDuration()) - current_count);
            count.setText(count_string);




            if (current_count >= (float)timer.getAction(current_action_index).getDuration()){


                if (timer.getActions().size() > current_action_index+1){
                    long currentTime = SystemClock.uptimeMillis();
                    current_action_index++;
                    current_count = 0;
                    points.clear();
                    handler.postAtTime(this, currentTime + TIMER_INTERVAL_MILLIS);
                    populateTextFields();

                } else {


                    title.setText("Done");
                    title.setTextColor(getResources().getColor(R.color.primary_dark));
                    description.setText("");
                    count.setText("");
                    nextTimers.setText("");

                }


            } else {
                handler.postDelayed(this,TIMER_INTERVAL_MILLIS);
            }
            current_count += 0.05;
        }
    };


    private ArrayList<PointF> points = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_activity);
        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        try {
            timer = new TimerSet(bundle.getString(TIMER_INTENT_KEY));
        } catch (JSONException e){
            e.printStackTrace();
            log("Could not load timer from JSON, finishing");
            finish();
        }

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {

            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {
                layout = (View) findViewById(R.id.timer_layout);
                title = (TextView) findViewById(R.id.timerfragment_title);
                count = (TextView) findViewById(R.id.timerfragment_count);
                nextTimers = (TextView) findViewById(R.id.timerfragment_next_timers);
                description = (TextView) findViewById(R.id.timerfragment_description);

                nextTimers.setText(timer.getAllActionsString());
                description.setText(timer.getDetailsSummary());
                title.setText(timer.getTitle());

                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isStarted) {
                            count.setVisibility(View.VISIBLE);
                            handler.post(timerRunnable);
                            current_action_index = 0;
                            current_count = 0;
                            points.clear();
                            populateTextFields();
                            isStarted = true;
                        } else {
                            Toast.makeText(TimerActivity.this, "Hold to cancel", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


                layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        finish();
                        return false;
                    }
                });

            }
        });









    }








    public void drawCanvas(float progress,int target){
        Resources res = getResources();
        int bgColor = res.getColor(R.color.icons);
        int timerFaceColor = res.getColor(R.color.primary);


        int layout_width = layout.getWidth();
        int layout_height = layout.getHeight();
        float centerX = layout_width/2f;
        float centerY = layout_height/2f;
        float lenght = centerX;

        float rot = progress/(float)target * (float) Math.PI *2f;
        //Log.d("ROT", Float.toString(rot));
        float xPos = (float) Math.sin(rot) * lenght;
        float yPos = (float) -Math.cos(rot) * lenght;




        Paint dialPaint = new Paint();
        Paint bgPaint = new Paint();
        bgPaint.setColor(bgColor);
        dialPaint.setColor(timerFaceColor);
        dialPaint.setAntiAlias(true);
        bgPaint.setAntiAlias(true);
        Bitmap bg = Bitmap.createBitmap(layout_width,layout_height,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bg);

        //canvas.drawLine(centerX,centerY,centerX+xPos,centerY+yPos,p);

        points.add(new PointF(xPos+centerX,yPos+centerY));

        Path path = new Path();
        path.moveTo(centerX,centerY);
        path.lineTo(centerX,0);

        for (int i = 0;i<points.size();i++){
            path.lineTo(points.get(i).x,points.get(i).y);

        }
        dialPaint.setStyle(Paint.Style.FILL);
        bgPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0,0,layout_width,layout_height,bgPaint);
        canvas.drawPath(path,dialPaint);
        canvas.drawCircle(centerX,centerY,150,bgPaint);


        layout.setBackgroundDrawable(new BitmapDrawable(bg));


    }

    private void log(String data){
        Log.d(LOG_IDENIFIER, data);

    }

    private void populateTextFields(){
        title.setText(timer.getAction(current_action_index).getTitle());
        nextTimers.setText(timer.getNextActionsString(current_action_index));
        description.setText(timer.getAction(current_action_index).getDescription());
    }
}
