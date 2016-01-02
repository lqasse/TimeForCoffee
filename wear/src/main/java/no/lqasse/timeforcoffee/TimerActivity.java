package no.lqasse.timeforcoffee;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import no.lqasse.timeforcoffee.Main.MainActivity;
import no.lqasse.timeforcoffee.Models.Preferences;
import no.lqasse.timeforcoffee.Models.TimerSet;

/**
 * Created by lassedrevland on 06.05.15.
 */
public class TimerActivity extends Activity {

    private enum SCREEN_SHAPE{ROUND,SQUARE}
    private static final int TIMER_INTERVAL_MILLIS = 50; //20fps
    private static final int INTERVALS_PER_SECOND = 1000 / TIMER_INTERVAL_MILLIS;
    public static final String TIMER_INTENT_KEY = "timer";
    public static final String LOG_IDENIFIER = "TimerACtivity";
    private long VIBRATE_LENGTH = 50;

    static final float[] accelerationValues = {
            0f         ,0f
            ,0.4f      ,0.4f
            ,0.575f    ,0.575f
            ,0.675f    ,0.675f
            ,0.75f     ,0.75f
            ,0.8f      ,0.8f
            ,0.875f    ,0.875f
            ,0.925f    ,0.925f
            ,0.950f    ,0.950f
            ,0.975f    ,0.975f
    };

    private SCREEN_SHAPE screenShape;

    //Canvas
    final int EDGE_OFFSET = 10;
    final float ZERO_DEGREES_POINT = 270f;
    int         LAYOUT_WIDTH    ;
    int         LAYOUT_HEIGHT   ;
    float       CENTER_X      ;
    float       CENTER_Y;
    float  radius;
    Bitmap backgroundBitmap;
    Canvas canvas;
    Path progressPathSquare = new Path();
    Path progressPathRound = new Path();

    Paint primaryPaint = new Paint();
    Paint textPaint = new Paint();
    Paint backgroundProgressPaint = new Paint();
    float strokeWidth = 20f;

    private Vibrator vibrator;
    private TextView title;
    private TextView count;
    private TextView nextTimers;
    private TextView description;

    private TimerSet timer;
    private View layout;
    private Handler handler;
    private Preferences preferences;

    float current_sec_progress = 0f;
    private Boolean isStarted = false;
    float current_time_left;

    private int currentActionIndex = 0;

    private int currentMillisLeft = 0;
    private int currentDuration = 0;
    private int currentSecondsLeft;

    private int loadProgress;
    private int loadMillis;
    private static  final int LOAD_ANIMATION_DURATION_MILLIS = 700;

    private Runnable loadAnimation = new Runnable() {
        @Override
        public void run() {
            int duration = timer.getAction(currentActionIndex).getDuration();

            int layoutWidth   = layout.getWidth();
            int layoutHeight  = layout.getHeight();
            int centerX = LAYOUT_WIDTH /2;
            int centerY = LAYOUT_HEIGHT /2;
            float sweepAngle = 360f/(float)duration;
            float sweepSpace = 2f;
            radius = centerX - EDGE_OFFSET;

            RectF screenEdge = new RectF(0,0,layoutWidth,layoutHeight);

            primaryPaint.setStrokeWidth(strokeWidth);

            loadProgress = (int) Math.ceil(duration * (float) loadMillis/(float) LOAD_ANIMATION_DURATION_MILLIS);

            for (int i = 0;i <= loadProgress;i++){
                float startAngle = sweepAngle *(float) i - 90;
                canvas.drawArc(screenEdge, startAngle, sweepAngle - sweepSpace, false, primaryPaint);
            }

            loadProgress += Math.floor(duration / 20f);
            layout.invalidate();
            Log.d("Loading", Integer.toString(loadProgress));
            if (loadMillis < LOAD_ANIMATION_DURATION_MILLIS){
                loadMillis += TIMER_INTERVAL_MILLIS;
                long currentSystemTime = SystemClock.uptimeMillis();
               handler.postAtTime(this, currentSystemTime+ TIMER_INTERVAL_MILLIS);
            }
        }
    };

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long currentSystemTime = SystemClock.uptimeMillis();
            currentDuration = timer.getAction(currentActionIndex).getDuration();

            if (currentMillisLeft == 0){
                currentMillisLeft = INTERVALS_PER_SECOND;
                currentSecondsLeft--;
            }
            currentMillisLeft--;

            drawProgress(currentSecondsLeft, currentMillisLeft, currentDuration);

            count.setText(Integer.toString(currentSecondsLeft));

            if (currentSecondsLeft == 0 && currentMillisLeft == 0){
                if (preferences.vibrate){
                    vibrator.vibrate(VIBRATE_LENGTH);
                }
                if (timer.getActions().size() > currentActionIndex +1){
                    currentActionIndex++;
                    loadMillis = 0;
                    loadProgress = 0;
                    handler.post(loadAnimation);
                    handler.postAtTime(this, currentSystemTime + TIMER_INTERVAL_MILLIS +LOAD_ANIMATION_DURATION_MILLIS);
                    currentSecondsLeft = timer.getAction(currentActionIndex).getDuration();

                    populateTextFields();
                } else {
                    title.setText("Done");
                    title.setTextColor(getResources().getColor(R.color.primary_dark));
                    description.setText("");
                    count.setText("");
                    nextTimers.setText("");
                    handler.removeCallbacksAndMessages(null);
                }

            } else {
                handler.postAtTime(this, currentSystemTime + TIMER_INTERVAL_MILLIS);
            }

            Log.d("Current time", Integer.toString(currentSecondsLeft) + "." + Integer.toString(currentMillisLeft));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_activity);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        handler = new Handler();
        preferences = Preferences.get(this);
        log("Vibrate: " + Boolean.toString(preferences.vibrate));

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
                layout = findViewById(R.id.timer_layout);
                title = (TextView) findViewById(R.id.timerfragment_title);
                count = (TextView) findViewById(R.id.timerfragment_count);
                nextTimers = (TextView) findViewById(R.id.timerfragment_next_timers);
                description = (TextView) findViewById(R.id.timerfragment_description);

                nextTimers.setText(timer.getAllActions());
                description.setText(timer.getSummary());
                title.setText(timer.getTitle());

                if (( layout.getTag()).equals("ROUND")) {
                    screenShape = SCREEN_SHAPE.ROUND;
                } else {
                    screenShape = SCREEN_SHAPE.SQUARE;
                }

                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isStarted) {
                            count.setVisibility(View.VISIBLE);
                            handler.postDelayed(timerRunnable, TIMER_INTERVAL_MILLIS);
                            currentActionIndex = 0;
                            currentSecondsLeft = timer.getAction(currentActionIndex).getDuration();
                            populateTextFields();
                            isStarted = true;
                        }
                    }
                });

                Resources res = getResources();

                primaryPaint.setColor(res.getColor(R.color.primary));
                primaryPaint.setAntiAlias(true);
                primaryPaint.setStrokeWidth(strokeWidth);
                primaryPaint.setStyle(Paint.Style.STROKE);

                Paint backgroundPaint = new Paint();
                backgroundPaint.setColor(res.getColor(R.color.icons));
                backgroundPaint.setStyle(Paint.Style.FILL);

                textPaint.setColor(res.getColor(R.color.accent));
                textPaint.setTextSize(LAYOUT_HEIGHT);
                textPaint.setAntiAlias(true);
                textPaint.setStyle(Paint.Style.STROKE);
                textPaint.setStrokeWidth(1f);
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setFontFeatureSettings("font-family: sans-serif-light;");

                backgroundProgressPaint.setColor(res.getColor(R.color.primary));
                backgroundProgressPaint.setAntiAlias(true);
                backgroundProgressPaint.setStyle(Paint.Style.FILL);
                backgroundProgressPaint.setStrokeWidth(1f);
                backgroundProgressPaint.setAlpha(50);

                final ViewTreeObserver observer = layout.getViewTreeObserver();
                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        backgroundBitmap = Bitmap.createBitmap(layout.getWidth(), layout.getHeight(), Bitmap.Config.ARGB_8888);
                        canvas = new Canvas(backgroundBitmap);
                        canvas.drawColor(Color.WHITE);
                        layout.setBackgroundDrawable(new BitmapDrawable(backgroundBitmap));

                        handler.post(loadAnimation);
                        observer.removeOnGlobalLayoutListener(this);
                    }
                });
            }
        });
    }

    private void drawProgress(int secondsLeft, int millisLeft, int duration){
        int layoutWidth   = layout.getWidth();
        int layoutHeight  = layout.getHeight();
        int centerX = LAYOUT_WIDTH /2;
        int centerY = LAYOUT_HEIGHT /2;
        float sweepAngle = 360f/(float)duration;
        float sweepSpace = 2f;
        float lastMarkerScaledownFactor = (float) millisLeft/10f;
        radius = centerX - EDGE_OFFSET;

        RectF screenEdge = new RectF(0,0,layoutWidth,layoutHeight);

        canvas.drawColor(Color.WHITE);
        primaryPaint.setStrokeWidth(strokeWidth);
        for (int i = secondsLeft;i>=0;i--){

            float startAngle = sweepAngle *(float) i - 90;

            if (i == secondsLeft){
                primaryPaint.setStrokeWidth(strokeWidth * accelerationValues[millisLeft]);
                canvas.drawArc(screenEdge, -90, startAngle + 90 + (sweepAngle * (millisLeft/20f))
                        , true, backgroundProgressPaint);
            } else {
                primaryPaint.setStrokeWidth(strokeWidth);
            }
            canvas.drawArc(screenEdge, startAngle, sweepAngle - sweepSpace, false, primaryPaint);
        }
    }

    private void drawProgress(float progress, int target){
        LAYOUT_WIDTH   = layout.getWidth();
        LAYOUT_HEIGHT  = layout.getHeight();
        CENTER_X     = LAYOUT_WIDTH /2f;
        CENTER_Y      = LAYOUT_HEIGHT /2f;
        radius = CENTER_X - EDGE_OFFSET;

        final float STROKE_WIDTH = 13f;
        final float percentCompleted = progress/(float)target;
        final float degreesCompleted = percentCompleted*360f;

        Resources res = getResources();
        int primary             = res.getColor(R.color.primary);
        Paint primaryPaint      = new Paint();
        primaryPaint.setColor(primary);
        primaryPaint.setAntiAlias(true);
        primaryPaint.setStrokeWidth(STROKE_WIDTH);
        primaryPaint.setStyle(Paint.Style.STROKE);
        primaryPaint.setTextSize(radius * 2f);
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

            float SPEED_FACTOR = 1f;

            float CURRENT_SECOND_PROGRESS_PERCENT = (progress%1f);

            if(CURRENT_SECOND_PROGRESS_PERCENT < 0.1f){
                CURRENT_SECOND_PROGRESS_PERCENT = 1f;
            }

            float OUTWARD_DISPLACE_FACTOR = Math.max(1f- (CURRENT_SECOND_PROGRESS_PERCENT * SPEED_FACTOR),0.01f);
            float INNWARD_DISPLACE_FACTOR = Math.min((CURRENT_SECOND_PROGRESS_PERCENT *SPEED_FACTOR),1f);

            primaryPaint.setStrokeWidth(20f);

            float space = 2f;
            float sweep = 360f/(float)target;

            int alpha = Math.round(255f * (1f-current_sec_progress));
            RectF oval = new RectF(0,0,LAYOUT_WIDTH,LAYOUT_HEIGHT);


            for (int i = 0;i<current_time_left;i++){
                float startAngle = sweep *(float) i - 90;

                if (i == current_time_left - 1){
                    primaryPaint.setStrokeWidth(STROKE_WIDTH * OUTWARD_DISPLACE_FACTOR);
                    //primaryPaint.setAlpha(alpha);
                }
                canvas.drawArc(oval,startAngle ,sweep - space,false,primaryPaint);
                canvas.drawOval(oval, textcolor);
            }
        }

        else if (screenShape == SCREEN_SHAPE.SQUARE) {
            //Create progressbar paths;
            progressPathSquare.moveTo(CENTER_X, CENTER_Y - radius);
            progressPathSquare.lineTo(CENTER_X + radius, CENTER_Y - radius);
            progressPathSquare.lineTo(CENTER_X + radius, CENTER_Y + radius);
            progressPathSquare.lineTo(CENTER_X - radius, CENTER_Y + radius);
            progressPathSquare.lineTo(CENTER_X - radius, CENTER_Y - radius);
            progressPathSquare.lineTo(CENTER_X, CENTER_Y - radius);

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
        title.setText(timer.getAction(currentActionIndex).getTitle());
        nextTimers.setText(timer.getNextActions(currentActionIndex));
        description.setText(timer.getAction(currentActionIndex).getDescription());
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);

    }
}
