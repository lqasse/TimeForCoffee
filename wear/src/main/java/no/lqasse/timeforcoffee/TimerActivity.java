package no.lqasse.timeforcoffee;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import org.json.JSONException;

import no.lqasse.timeforcoffee.Main.CanvasManager;
import no.lqasse.timeforcoffee.wear_Models.Preferences;
import no.lqasse.timeforcoffee.wear_Models.TimerSet;

/**
 * Created by lassedrevland on 06.05.15.
 */
public class TimerActivity extends Activity {
    public enum SCREEN_SHAPE{ROUND,SQUARE}
    public static final String TIMER_INTENT_KEY = "timer";
    private static final String ROUND_LAYOUT_TAG_IDENTIFIER = "ROUND";
    private static final int LOAD_ANIMATION_FPS = 20;
    private static final int TIMER_INTERVLL_PER_SEC = 1;
    private static final int TIMER_INTERVAL_MILLIS = 1000 / TIMER_INTERVLL_PER_SEC;
    private static final int LOAD_ANIMATION_MILLISECONDS_PER_INTERVAL = 1000 / LOAD_ANIMATION_FPS;
    private static final int LOAD_ANIMATION_DURATION_MILLIS = 700;
    private static final long VIBRATE_LENGTH = 50;

    private Bitmap backgroundBitmap;
    private Canvas canvas;
    private Vibrator vibrator;
    private TimerSet timerSet;
    private View layout;
    private Handler handler;
    private Preferences preferences;
    private CanvasManager canvasManager;

    private TextView title;
    private TextView count;
    private TextView action1;
    private TextView action2;
    private TextView action3;
    private TextView description;

    private Boolean isStarted = false;
    private int currentActionIndex = 0;
    private int currentSecondsLeft;
    private int loadAnimationProgress;
    private int loadMillis;


    private int chinHeight = 0;

    private Runnable loadAnimation = new Runnable() {
        @Override
        public void run() {
            int duration = timerSet.getAction(currentActionIndex).getDuration();
            long currentSystemTime = SystemClock.uptimeMillis();
            loadAnimationProgress = (int) Math.ceil(duration * (float) loadMillis/(float) LOAD_ANIMATION_DURATION_MILLIS);
            canvasManager.drawMarkersLoading(loadAnimationProgress);
            loadAnimationProgress += Math.floor(duration / 20f);
            layout.invalidate();
            if (loadMillis < LOAD_ANIMATION_DURATION_MILLIS){
                loadMillis += LOAD_ANIMATION_MILLISECONDS_PER_INTERVAL;

               handler.postAtTime(this, currentSystemTime+ LOAD_ANIMATION_MILLISECONDS_PER_INTERVAL);
            }
        }
    };

    private Runnable timer = new Runnable() {
        @Override
        public void run() {
            long currentSystemTime = SystemClock.uptimeMillis();
            currentSecondsLeft--;
            canvasManager.drawMarkers(currentSecondsLeft);
            setCount(currentSecondsLeft);

            if (currentSecondsLeft == 0){
                //setCount(0);
                if (preferences.vibrate){
                    vibrator.vibrate(VIBRATE_LENGTH);
                }
                if (timerSet.getActions().size() > currentActionIndex +1){
                    currentActionIndex++;
                    canvasManager.createMarkers(timerSet.getAction(currentActionIndex).getDuration());
                    setTextFields();
                    loadMillis = 0;
                    loadAnimationProgress = 0;
                    currentSecondsLeft = timerSet.getAction(currentActionIndex).getDuration();
                    setCount(currentSecondsLeft);
                    handler.post(loadAnimation);
                    handler.postAtTime(this, currentSystemTime + TIMER_INTERVAL_MILLIS + LOAD_ANIMATION_DURATION_MILLIS);

                } else {
                    setFinalTextFields();
                    canvasManager.clear();
                    handler.removeCallbacksAndMessages(null);
                }

            } else {
                handler.postAtTime(this, currentSystemTime + TIMER_INTERVAL_MILLIS);
            }
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

        try {
            timerSet = new TimerSet(bundle.getString(TIMER_INTENT_KEY));
        } catch (JSONException e){
            e.printStackTrace();
            finish();
        }

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {
                stub.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                        chinHeight = insets.getSystemWindowInsetBottom();
                        Log.d("CHIN_GET", Integer.toString(chinHeight));
                        // chinHeight = 30;
                        return insets;
                    }
                });

                stub.requestApplyInsets();


                layout = findViewById(R.id.timer_layout);
                title = (TextView) findViewById(R.id.timer_activity_title);
                count = (TextView) findViewById(R.id.timer_activity_count);
                description = (TextView) findViewById(R.id.timer_activity_description);
                action1 = (TextView) findViewById(R.id.timer_activity_action1);
                action2 = (TextView) findViewById(R.id.timer_activity_action2);
                action3 = (TextView) findViewById(R.id.timer_activity_action3);

                currentActionIndex = 0;
                currentSecondsLeft = timerSet.getAction(currentActionIndex).getDuration();
                setCount(currentSecondsLeft);
                setInitialTextFields();

                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isStarted) {
                            count.setVisibility(View.VISIBLE);
                            handler.postDelayed(timer, TIMER_INTERVAL_MILLIS);
                            setTextFields();
                            isStarted = true;
                        }
                    }
                });

                final ViewTreeObserver observer = layout.getViewTreeObserver();
                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        backgroundBitmap = Bitmap.createBitmap(layout.getWidth(), layout.getHeight(), Bitmap.Config.ARGB_8888);
                        canvas = new Canvas(backgroundBitmap);
                        canvasManager = new CanvasManager(getResources(),canvas,getScreenShape(layout),chinHeight);
                        layout.setBackgroundDrawable(new BitmapDrawable(backgroundBitmap));
                        canvasManager.createMarkers(timerSet.getAction(0).getDuration());
                        handler.post(loadAnimation);
                        observer.removeOnGlobalLayoutListener(this);
                    }
                });
            }
        });
    }

    private SCREEN_SHAPE getScreenShape(View layout){
        if ((layout.getTag()).equals(ROUND_LAYOUT_TAG_IDENTIFIER)) {
            return SCREEN_SHAPE.ROUND;
        } else {
            return SCREEN_SHAPE.SQUARE;
        }
    }

    private void setCount(int secsLeft){
        String spacer = "";
        if (secsLeft < 10){
            spacer = "0";
        }
        count.setText(spacer + Integer.toString(secsLeft));
    }

    private void setInitialTextFields(){
        action1.setText(timerSet.getAllActions());
        description.setText(timerSet.getSummary());
        title.setText(timerSet.getTitle());
        action2.setText("");
        action3.setText("");
    }

    private void setTextFields(){
        title.setText(timerSet.getAction(currentActionIndex).getTitle());
        if ((currentActionIndex + 1) < timerSet.getActions().size()){
            action1.setText(timerSet.getAction(currentActionIndex + 1).getSummaryString());
        } else {
            action1.setText("");
        }
        if ((currentActionIndex + 2) < timerSet.getActions().size()){
            action2.setText(timerSet.getAction(currentActionIndex+2).getSummaryString());
        } else {
            action2.setText("");
        }

        if ((currentActionIndex + 3) < timerSet.getActions().size()){
            action3.setText(timerSet.getAction(currentActionIndex+3).getSummaryString());
        } else {
            action3.setText("");
        }
        description.setText(timerSet.getAction(currentActionIndex).getDescription());
    }

    private void setFinalTextFields(){
        title.setText("Done");
        title.setTextColor(getResources().getColor(R.color.primary_dark));
        description.setText("");
        count.setText("00");
        action1.setText("");
        action2.setText("");
        action3.setText("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }
}
