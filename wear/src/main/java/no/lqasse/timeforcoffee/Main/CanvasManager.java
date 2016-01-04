package no.lqasse.timeforcoffee.Main;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.ArrayList;

import no.lqasse.timeforcoffee.R;
import no.lqasse.timeforcoffee.TimerActivity;

/**
 * Created by lassedrevland on 04.01.16.
 */
public class CanvasManager {
    private static final float MARKER_SPACING = 2f;
    private Canvas canvas;
    private Resources resources;
    private Paint shadow = new Paint();
    private Paint primaryPaint = new Paint();
    private Paint backgroundPaint = new Paint();
    private int layoutHeight;
    private int layoutWidth;
    private ArrayList<Path> markers = new ArrayList<>();
    private TimerActivity.SCREEN_SHAPE screenShape;
    private RectF screenEdge;
    private RectF outerMarkerEdge;
    private RectF innerMarkerEdge;
    private Path faceInnerEdge;
    private Path faceEdge;

    public CanvasManager(Resources res, Canvas canvas, int layoutHeight, int layoutWidth, TimerActivity.SCREEN_SHAPE screenShape){
        this.resources = res;
        this.canvas = canvas;
        this.layoutHeight = layoutHeight;
        this.layoutWidth = layoutWidth;
        this.screenShape = screenShape;
        setColors();
        drawBackground();
        createShapes();
    }

    private void setColors(){
        shadow.setColor(resources.getColor(R.color.black));
        shadow.setAntiAlias(true);
        shadow.setStrokeWidth(5);
        shadow.setStyle(Paint.Style.STROKE);
        shadow.setAlpha(50);
        shadow.setShadowLayer(5, 0, 0, Color.parseColor("#AA000000"));

        primaryPaint.setColor(resources.getColor(R.color.primary));
        primaryPaint.setAntiAlias(true);
        primaryPaint.setStyle(Paint.Style.FILL);
        primaryPaint.setShadowLayer(4, 0, 0, Color.parseColor("#AA000000"));

        backgroundPaint.setColor(resources.getColor(R.color.icons));
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setStrokeWidth(2f);
    }


    public void createShapes(){
        screenEdge = new RectF(0,0, layoutWidth, layoutHeight);
        outerMarkerEdge = new RectF(-70, -70, layoutWidth + 70, layoutHeight + 70);
        innerMarkerEdge = new RectF(10, 10, layoutWidth - 10, layoutHeight - 10);

        faceInnerEdge = new Path();
        faceEdge = new Path();
        if (screenShape == TimerActivity.SCREEN_SHAPE.ROUND){
            faceInnerEdge.addOval(innerMarkerEdge, Path.Direction.CW);
            faceEdge.addOval(screenEdge, Path.Direction.CW);
        } else {
            faceInnerEdge.addRect(innerMarkerEdge, Path.Direction.CW);
            faceEdge.addRect(screenEdge, Path.Direction.CW);
        }

    }

    public void clear(){
        drawBackground();
        drawShadow();
    }

    public void createMarkers(int duration){
        int centerX = layoutWidth / 2;
        int centerY = layoutHeight / 2;
        float markerWidth = 360f / (float) duration;
        float TWO_PI = (float) Math.PI * 2f;
        markers.clear();

        for (int i = 0; i < duration; i++) {
            Path marker = new Path();
            float rot = (float) i / duration * TWO_PI;
            float X = centerX + (float) Math.sin(rot) * layoutWidth;
            float Y = centerY + (float) -Math.cos(rot) * layoutWidth;

            float startAngle = markerWidth * (float) i - 90;

            marker.addArc(outerMarkerEdge, startAngle, markerWidth - MARKER_SPACING);

            marker.lineTo(centerX, centerY);
            marker.lineTo(X, Y);
            marker.close();

            marker.op(faceInnerEdge, Path.Op.DIFFERENCE);
            markers.add(marker);
        }
    }

    public void drawMarkers(int secondsLeft){
        drawBackground();
        Path allMarkers = new Path();
        for (int i = secondsLeft-1;i>=0;i--){
            allMarkers.addPath(markers.get(i));
        }
        canvas.drawPath(allMarkers, primaryPaint);
        drawShadow();
    }

    public void drawMarkersLoading(int secondsLoaded){
        drawBackground();
        int index = markers.size() - secondsLoaded;
        Path allMarkers = new Path();
        for (int i = markers.size()-1; i>= index;i--){
            allMarkers.addPath(markers.get(i));

        }
        canvas.drawPath(allMarkers, primaryPaint);
        drawShadow();
    }

    public void drawShadow(){
        if (screenShape == TimerActivity.SCREEN_SHAPE.ROUND){
            canvas.drawOval(screenEdge,shadow);
        } else {
            canvas.drawRect(screenEdge,shadow);
        }
    }

    public void drawBackground(){
        canvas.drawColor(Color.WHITE);
    }


}
