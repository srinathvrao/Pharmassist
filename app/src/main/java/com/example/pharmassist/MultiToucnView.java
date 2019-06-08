package com.example.pharmassist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class MultiToucnView extends View {

    ArrayList<Integer> nopoints;

    static ArrayList<Float> xPoints = new ArrayList<>();
    static ArrayList<Float> yPoints = new ArrayList<>();
    ArrayList<Integer> selectiedPoints = new ArrayList<>();

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    final int MAX_NUMBER_OF_POINT = 10;
    float[] x = new float[MAX_NUMBER_OF_POINT];
    float[] y = new float[MAX_NUMBER_OF_POINT];
    boolean[] touching = new boolean[MAX_NUMBER_OF_POINT];


    public void gotPoints(ArrayList a) {
        Log.i("POINTS",String.valueOf(a.size()));
        for(int i=0;i<a.size();i++)
            Log.i("POINTS",String.valueOf(a.get(i)));
        selectiedPoints.clear();
        MainActivity.sensorFlag = 0;
        Intent intent = new Intent(getContext(),MedicineRecognised.class);
        intent.putExtra("x",nopoints);
        //intent.putExtra("y",yPoints);
        getContext().startActivity(intent);
        //finish();

    }

    public MultiToucnView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MultiToucnView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiToucnView(Context context) {
        super(context);
        init();
    }

    void init() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        for(int i = 0; i < MAX_NUMBER_OF_POINT; i++){
            if(touching[i]){
                canvas.drawCircle(x[i], y[i], 50f, paint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = (event.getAction() & MotionEvent.ACTION_MASK);
        int pointCount = event.getPointerCount();

        Log.i("intercepted",String.valueOf(pointCount));

        nopoints = new ArrayList<>();

        for(int i=0;i<event.getPointerCount();i++)
            nopoints.add(1);

        gotPoints(nopoints);





        /*for (int i = 0; i < pointCount; i++) {
            int id = event.getPointerId(i);


            //Ignore pointer higher than our max.
            if(id < MAX_NUMBER_OF_POINT){
                x[id] = (int)event.getX(i);
                y[id] = (int)event.getY(i);

                if((action == MotionEvent.ACTION_DOWN)
                        ||(action == MotionEvent.ACTION_POINTER_DOWN)
                        ||(action == MotionEvent.ACTION_MOVE)){
                    touching[id] = true;
                }else{
                    touching[id] = false;
                }
            }
        } */

        invalidate();
        return true;

    }

}