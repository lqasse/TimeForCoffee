package no.lqasse.timeforcoffee;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

import no.lqasse.timeforcoffee.Models.TimerSet;

/**
 * Created by lassedrevland on 06.05.15.
 */
public class TimerActivity extends Activity {

    private enum SCREEN_SHAPE{ROUND,SQUARE}
    private static final int TIMER_INTERVAL_MILLIS = 100;
    public static final String TIMER_INTENT_KEY = "timer";
    public static final String LOG_IDENIFIER = "TimerACtivity";

    private SCREEN_SHAPE screenShape;

    //Canvas
    final int EDGE_OFFSET = 10;
    final float ZERO_DEGREES_POINT = 270f;
    int         LAYOUT_WIDTH    ;
    int         LAYOUT_HEIGHT   ;
    float       CENTER_X      ;
    float       CENTER_Y;
    float       RADIUS;
    Bitmap backgroundBitmap;
    Canvas canvas;
    Path progressPathSquare = new Path();
    Path progressPathRound = new Path();


    //Views
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
    float current_sec_progress = 0f;
    private Boolean isStarted = false;
    int current_time_left;
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {

            drawProgress(current_count, timer.getAction(current_action_index).getDuration());
            current_time_left = timer.getAction(current_action_index).getDuration() - (int) Math.floor(current_count);
            String count_string = Integer.toString(current_time_left);
            count.setText(count_string);

            if (current_count >= (float)timer.getAction(current_action_index).getDuration()){



                if (timer.getTimerActions().size() > current_action_index+1){
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
            current_sec_progress = current_count%1f;
            current_count += 0.1f;
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

                if (((String) layout.getTag()).equals("ROUND")) {
                    screenShape = SCREEN_SHAPE.ROUND;
                } else {
                    screenShape = SCREEN_SHAPE.SQUARE;
                }


                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isStarted) {
                            clearCanvas();
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





    private void clearCanvas(){

        int   LAYOUT_WIDTH   = layout.getWidth();
        int   LAYOUT_HEIGHT  = layout.getHeight();
        float CENTER_X     = LAYOUT_WIDTH /2f;


        Resources res = getResources();
        int bgColor             = res.getColor(R.color.icons);
        Paint bgPaint           = new Paint();
        bgPaint                 .setColor(bgColor);


        backgroundBitmap = Bitmap.createBitmap(LAYOUT_WIDTH, LAYOUT_HEIGHT, Bitmap.Config.ARGB_8888);

        canvas = new Canvas(backgroundBitmap);



        layout.setBackgroundDrawable(new BitmapDrawable(backgroundBitmap));
    }

    private void drawProgress(float progress, int target){
        LAYOUT_WIDTH   = layout.getWidth();
        LAYOUT_HEIGHT  = layout.getHeight();
        CENTER_X     = LAYOUT_WIDTH /2f;
        CENTER_Y      = LAYOUT_HEIGHT /2f;
        RADIUS       = CENTER_X - EDGE_OFFSET;

        final float PORGRESSBAR_WIDTH = 13f;
        final float percentCompleted = progress/(float)target;
        final float degreesCompleted = percentCompleted*360f;

        Resources res = getResources();
        int primary             = res.getColor(R.color.primary);
        Paint primaryPaint      = new Paint();
        primaryPaint.setColor(primary);
        primaryPaint.setAntiAlias(true);
        primaryPaint.setStrokeWidth(PORGRESSBAR_WIDTH);
        primaryPaint.setStyle(Paint.Style.STROKE);
        primaryPaint.setTextSize(RADIUS * 2f);
        int bgColor = res.getColor(R.color.icons);
        Paint bgPaint = new Paint();
        bgPaint.setColor(bgColor);


        Paint textcolor = new Paint();
        textcolor.setColor(res.getColor(R.color.accent));
        textcolor.setTextSize(LAYOUT_HEIGHT);
        textcolor.setAntiAlias(true);
        textcolor.setStyle(Paint.Style.STROKE);
        textcolor.setStrokeWidth(1f);
        textcolor.setTextAlign(Paint.Align.CENTER);
        textcolor.setFontFeatureSettings("font-family: sans-serif-light;");



        canvas.drawColor(bgColor);

        if (screenShape == SCREEN_SHAPE.ROUND) {

            primaryPaint.setStrokeWidth(20f);

            float space = 2f;
            float sweep = 360f/(float)target;

            int alpha = Math.round(255f * (1f-current_sec_progress));
            RectF oval = new RectF(0,0,LAYOUT_WIDTH,LAYOUT_HEIGHT);

            //canvas.drawText(Integer.toString(current_time_left),CENTER_X,CENTER_Y+LAYOUT_HEIGHT/2f,textcolor);

            for (int i = 0;i<current_time_left;i++){
                float startAngle = sweep *(float) i - 90;

                if (i == current_time_left - 1){
                    primaryPaint.setAlpha(alpha);
                }
                canvas.drawArc(oval,startAngle ,sweep - space,false,primaryPaint);
                canvas.drawOval(oval, textcolor);
            }
        }

        else if (screenShape == SCREEN_SHAPE.SQUARE) {
            //Create progressbar paths;
            progressPathSquare.moveTo(CENTER_X, CENTER_Y - RADIUS);
            progressPathSquare.lineTo(CENTER_X + RADIUS, CENTER_Y - RADIUS);
            progressPathSquare.lineTo(CENTER_X + RADIUS, CENTER_Y + RADIUS);
            progressPathSquare.lineTo(CENTER_X - RADIUS, CENTER_Y + RADIUS);
            progressPathSquare.lineTo(CENTER_X - RADIUS, CENTER_Y - RADIUS);
            progressPathSquare.lineTo(CENTER_X, CENTER_Y - RADIUS);

            PathMeasure measure = new PathMeasure(progressPathSquare,false);
            float length = measure.getLength();

            PathEffect dashPathEffect = new DashPathEffect(new float[] {length,length}, length*percentCompleted);
            PathEffect cornerPathEffect = new CornerPathEffect(10f);
            ComposePathEffect composePathEffect = new ComposePathEffect(dashPathEffect,cornerPathEffect);
            primaryPaint.setPathEffect(composePathEffect);

            canvas.drawPath(progressPathSquare, primaryPaint);
        }


        layout.setBackgroundDrawable(new BitmapDrawable(backgroundBitmap));
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
