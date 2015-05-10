package picker.ugurtekbas.com.Picker;

import android.content.Context;
import android.graphics.Paint;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.Calendar;
import java.util.Date;

import picker.ugurtekbas.com.library.R;


public class Picker extends View{

    private Paint paint;
    private RectF rectF;

    private float width,height,min,padding,radius,
            dialRadius,offset,slopX,slopY,posX,posY,
            dialX,dialY;

    private int hour,minutes,tmp,previousHour;
    private int textColor   = Color.WHITE;
    private int clockColor  = Color.parseColor("#0f9280");
    private int dialColor   = Color.parseColor("#FF9F5B");
    private int canvasColor = Color.parseColor("#2D2D2E");
    private double angle,degrees;
    private boolean isMoving,amPm,disableTouch,hourFormat,firstRun=true;
    private String hStr,mStr,amPmStr;

    private TypedArray typedArray;
    private TimeChangedListener timeListener;

    public Picker(Context context) {
        super(context);
        init(context,null);
    }

    public Picker(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context, attrs);
    }


    private void init(Context context,AttributeSet attrs) {
        angle = (-Math.PI / 2) + .001;
        hourFormat  =   DateFormat.is24HourFormat(getContext());
        amPm    =   (Calendar.getInstance().get(Calendar.AM_PM)==0) ? true:false;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setTextAlign(Paint.Align.CENTER);

        rectF = new RectF();

        if (attrs!=null){
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.Picker);
            if(typedArray != null){
                textColor   = typedArray.getColor(R.styleable.Picker_textColor, textColor);
                clockColor  = typedArray.getColor(R.styleable.Picker_clockColor, clockColor);
                dialColor   = typedArray.getColor(R.styleable.Picker_dialColor, dialColor);
                canvasColor = typedArray.getColor(R.styleable.Picker_canvasColor,canvasColor);
                hourFormat  = typedArray.getBoolean(R.styleable.Picker_hourFormat,hourFormat);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        min = Math.min(width, height);
        setMeasuredDimension((int) min, (int) min);

        offset = min * 0.5f;
        padding = min / 20;
        radius = min / 2 - (padding * 2);
        dialRadius = radius / 7;
        rectF.set(-radius, -radius, radius, radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(offset, offset);
        canvas.drawColor(canvasColor);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(255);

        if (firstRun){
            setFirstTime();
        }else {
            //Rad to Deg
            degrees = (Math.toDegrees(angle) + 90) % 360;
            degrees = (degrees + 360) % 360;

            //get AM/PM
            if (hourFormat){
                hour = ((int) degrees / 15) % 24;
                minutes = ((int) (degrees * 4)) % 60;
                mStr = (minutes < 10) ? "0" + minutes : minutes + "";
                amPmStr="";
            }else{
                hour = ((int)degrees / 30) % 12;
                if(hour == 0) hour = 12;
                //get Minutes
                minutes = ((int) (degrees * 2)) % 60;
                mStr = (minutes < 10) ? "0" + minutes : minutes + "";
                //AM-PM
                if ((hour == 12 && previousHour == 11) || (hour == 11 && previousHour == 12)) amPm = !amPm;
                amPmStr = amPm ? "AM" : "PM";
            }
        }

        previousHour = hour;

        paint.setColor(textColor);
        paint.setTextSize(min / 5);
        //the text which shows time
        hStr = (hour < 10) ? "0" + hour : hour + "";
        canvas.drawText(hStr + ":" + mStr, 0, paint.getTextSize() / 4, paint);
        paint.setTextSize(min / 10);
        canvas.drawText(amPmStr, 0, paint.getTextSize() * 2, paint);

        //clocks dial
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(min / 25);
        paint.setColor(clockColor);
        canvas.drawOval(rectF, paint);
        /////////////////////////////

        //small circle t adjust time
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(dialColor);
        paint.setAlpha(255);

        calculatePointerPosition(angle);
        canvas.drawCircle(dialX, dialY, dialRadius, paint);

    }

    public void setFirstTime(){
        int firstHour;
        Calendar cal    =   Calendar.getInstance();
        minutes = cal.get(Calendar.MINUTE);
        mStr = (minutes < 10) ? "0" + minutes : minutes + "";
        if (hourFormat){
            firstHour = cal.get(Calendar.HOUR_OF_DAY);
            hour =  cal.get(Calendar.HOUR_OF_DAY);
            amPmStr="";

            degrees = ((firstHour%24)*15) + ((minutes%60)/4);
        }else{
            firstHour = cal.get(Calendar.HOUR);
            hour = cal.get(Calendar.HOUR);
            if(hour == 0) hour = 12;
            if ((hour == 12 && previousHour == 11) || (hour == 11 && previousHour == 12)) amPm = !amPm;
            amPmStr = amPm ? "AM" : "PM";
            degrees = ((firstHour%12)*30) + ((minutes%60)/2);
        }
        angle   =   Math.toRadians(degrees) - (Math.PI/2);

        firstRun    =   false;
    }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (disableTouch) return false;
            getParent().requestDisallowInterceptTouchEvent(true);

            posX = event.getX() - offset;
            posY = event.getY() - offset;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    calculatePointerPosition(angle);
                    if (posX >= (dialX - dialRadius) &&
                            posX <= (dialX + dialRadius) &&
                            posY >= (dialY - dialRadius) &&
                            posY <= (dialY + dialRadius)) {

                        slopX = posX - dialX;
                        slopY = posY - dialY;
                        isMoving = true;
                        invalidate();
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isMoving) {
                        angle = (float) Math.atan2(posY - slopY, posX - slopX);
                        if(timeListener!=null){
                            timeListener.timeChanged(getTime());
                        }
                        invalidate();
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(false);
                        return false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isMoving = false;
                    invalidate();
                    break;
            }

            return true;
        }

    private void calculatePointerPosition(double angle) {
        dialX = (float) (radius * Math.cos(angle));
        dialY = (float) (radius * Math.sin(angle));
    }

    public int getCurrentHour(){
        int currentHour =   hour;
        if (hourFormat){
            return currentHour;
        }else{
            if (amPm){
                if (currentHour==12){
                    currentHour=0;
                }
            }else{
                //PM
                if (currentHour<12){
                    currentHour +=12;
                }
            }
            return currentHour;
        }
    }
    public int getCurrentMin(){

        return minutes;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public void setClockColor(int clockColor) {
        this.clockColor = clockColor;
        invalidate();
    }

    public void setDialColor(int dialColor) {
        this.dialColor = dialColor;
        invalidate();
    }

    public void setCanvasColor(int canvasColor){
        this.canvasColor = canvasColor;
        invalidate();
    }

    public void disableTouch(boolean disableTouch) {
        this.disableTouch = disableTouch;
    }

    public void setHourFormat(boolean format) {
        this.hourFormat = format;
    }

    public String getAmPM(){
        return this.amPmStr;
    }

    public Date getTime() {
        Calendar calendar = Calendar.getInstance();
        tmp = hour;
        if (!amPm) {
            if (tmp < 12) tmp += 12;
        } else {
            if (tmp == 12) tmp = 0;
        }

        calendar.set(Calendar.HOUR_OF_DAY, tmp);
        calendar.set(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    public void setTimeChangedListener(TimeChangedListener timeChangedListener){
        this.timeListener = timeChangedListener;
    }
}