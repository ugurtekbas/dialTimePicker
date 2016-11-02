package picker.ugurtekbas.com.Picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import java.util.Calendar;
import java.util.Date;

import picker.ugurtekbas.com.library.R;

/**
 * Custom time picker which extends Android's View class
 * @author Ugur Tekbas
 * on 10.05.2015.
 */
public class Picker extends View {

    private final Paint paint;
    private final RectF rectF;
    private final Xfermode dialXferMode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private static final int AN_HOUR_AS_MINUTES = 60;
    private static final int A_DAY_AS_HOURS = 24;
    private static final int HALF_DAY_AS_HOURS = 12;
    public static final boolean AM = true;
    public static final boolean PM = false;

    private float min, radius, dialRadius, offset, slopX, slopY, dialX, dialY;
    private int hour, minutes, previousHour;
    private int textColor = Color.BLACK;
    private int clockColor = Color.parseColor("#0f9280");
    private int dialColor = Color.parseColor("#FF9F5B");
    private int canvasColor = Color.TRANSPARENT;
    private int trackSize = -1, dialRadiusDP = -1;
    private double angle, degrees;
    private boolean isMoving, amPm, disableTouch, hourFormat, firstRun = true, manuelAdjust;
    private String hStr, mStr, amPmStr;

    private TimeChangedListener timeListener;

    public Picker(Context context) {
        this(context, null);
    }

    public Picker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Picker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setTextAlign(Paint.Align.CENTER);

        rectF = new RectF();

        angle = (-Math.PI / 2) + .001;
        hourFormat = DateFormat.is24HourFormat(getContext());
        amPm = Calendar.getInstance().get(Calendar.AM_PM) == 0;

        loadAppThemeDefaults();
        loadAttributes(attrs);
    }

    /**
     * Sets default theme attributes for picker
     * These will be used if picker's attributes aren't set
     */
    private void loadAppThemeDefaults() {
        TypedValue typedValue = new TypedValue();

        TypedArray a = getContext().obtainStyledAttributes(typedValue.data, new int[]{
                R.attr.colorAccent,
                android.R.attr.textColorPrimary,
                R.attr.colorControlNormal});

        dialColor = a.getColor(0, dialColor);
        textColor = a.getColor(1, textColor);
        clockColor = a.getColor(2, clockColor);

        a.recycle();
    }

    /**
     * Sets picker's attributes from xml file
     * @param attrs
     */
    private void loadAttributes(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Picker);

            if (typedArray != null) {
                textColor = typedArray.getColor(R.styleable.Picker_textColor, textColor);
                dialColor = typedArray.getColor(R.styleable.Picker_dialColor, dialColor);
                clockColor = typedArray.getColor(R.styleable.Picker_clockColor, clockColor);
                canvasColor = typedArray.getColor(R.styleable.Picker_canvasColor, canvasColor);
                hourFormat = typedArray.getBoolean(R.styleable.Picker_hourFormat, hourFormat);
                trackSize = typedArray.getDimensionPixelSize(R.styleable.Picker_trackSize, trackSize);
                dialRadiusDP = typedArray.getDimensionPixelSize(R.styleable.Picker_dialRadius, dialRadiusDP);

                typedArray.recycle();
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float width = MeasureSpec.getSize(widthMeasureSpec);
        float height = MeasureSpec.getSize(heightMeasureSpec);

        min = Math.min(width, height);
        setMeasuredDimension((int) min, (int) min);

        offset = min * 0.5f;
        float padding = min / 20;
        radius = min / 2 - (padding * 2);
        dialRadius = dialRadiusDP != -1 ? dialRadiusDP : radius / 7;
        rectF.set(-radius, -radius, radius, radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(offset, offset);
        canvas.drawColor(canvasColor);

        if (firstRun) {
            Calendar cal = Calendar.getInstance();
            minutes = cal.get(Calendar.MINUTE);
            hour = cal.get(Calendar.HOUR_OF_DAY);
            initTime(hour, minutes);
        } else {
            //Rad to Deg
            degrees = (Math.toDegrees(angle) + 90) % 360;
            degrees = (degrees + 360) % 360;

            //get AM/PM
            if (hourFormat) {
                hour = ((int) degrees / 15) % A_DAY_AS_HOURS;
                /**
                 * When minutes are set programmatically, because of rounding issues,
                 * new value of minutes might be different than the one is set.
                 * To avoid that if statement checks if time setting is done programmatically or
                 * by touch gestures.
                 */
                if(manuelAdjust){
                    minutes = ((int) (degrees * 4)) % AN_HOUR_AS_MINUTES;
                    manuelAdjust = false;
                }

                mStr = (minutes < 10) ? "0" + minutes : minutes + "";
                amPmStr = "";
            } else {
                if(manuelAdjust){
                    //get Minutes
                    minutes = ((int) (degrees * 2)) % AN_HOUR_AS_MINUTES;
                    manuelAdjust = false;
                }

                hour = ((int) degrees / 30) % HALF_DAY_AS_HOURS;
                if (hour == 0) hour = HALF_DAY_AS_HOURS;

                mStr = (minutes < 10) ? "0" + minutes : minutes + "";
                //AM-PM
                if ((hour == 12 && previousHour == 11) || (hour == 11 && previousHour == 12)) {
                    amPm = !amPm;
                }
                amPmStr = amPm ? "AM" : "PM";
            }
        }

        previousHour = hour;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(textColor);
        paint.setAlpha(isEnabled() ? paint.getAlpha() : 77);
        paint.setTextSize(min / 5);
        //the text which shows time
        hStr = (hour < 10) ? "0" + hour : hour + "";
        canvas.drawText(hStr + ":" + mStr, 0, paint.getTextSize() / 4, paint);
        paint.setTextSize(min / 10);
        canvas.drawText(amPmStr, 0, paint.getTextSize() * 2, paint);

        //clocks dial
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(trackSize != -1 ? trackSize : min / 25);
        paint.setColor(clockColor);
        paint.setAlpha(isEnabled() ? paint.getAlpha() : 77);
        canvas.drawOval(rectF, paint);

        //small circle t adjust time
        calculatePointerPosition(angle);

        paint.setStyle(Paint.Style.FILL);
        paint.setAlpha(0);
        paint.setXfermode(dialXferMode);
        canvas.drawCircle(dialX, dialY, dialRadius, paint);

        paint.setColor(dialColor);
        paint.setAlpha(isEnabled() ? paint.getAlpha() : 77);
        paint.setXfermode(null);
        canvas.drawCircle(dialX, dialY, dialRadius, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (disableTouch || !isEnabled()) return false;

        manuelAdjust = true;

        getParent().requestDisallowInterceptTouchEvent(true);

        float posX = event.getX() - offset;
        float posY = event.getY() - offset;

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
                    if (timeListener != null) {
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

    public int getCurrentHour() {
        int currentHour = hour;
        if (hourFormat) {
            return currentHour;
        } else {
            if (amPm) {
                if (currentHour == 12) {
                    currentHour = 0;
                }
            } else {
                //PM
                if (currentHour < 12) {
                    currentHour += 12;
                }
            }
            return currentHour;
        }
    }

    public int getCurrentMin() {
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

    public void setCanvasColor(int canvasColor) {
        this.canvasColor = canvasColor;
        invalidate();
    }

    /**
     * To set dial's size
     * @param trackSize
     */
    public void setTrackSize(int trackSize) {
        this.trackSize = trackSize;
    }

    /**
     * To set adjuster's size
     * @param dialRadiusDP
     */
    public void setDialRadiusDP(int dialRadiusDP) {
        this.dialRadiusDP = dialRadiusDP;
    }

    /**
     * To disable/enable the picker
     * @param disableTouch
     */
    public void disableTouch(boolean disableTouch) {
        this.disableTouch = disableTouch;
    }

    public void setHourFormat(boolean format) {
        this.hourFormat = format;
    }

    public String getAmPM() {
        return this.amPmStr;
    }

    public Date getTime() {
        Calendar calendar = Calendar.getInstance();
        int tmp = hour;

        if (!amPm) {
            if (tmp < 12) tmp += 12;
        } else {
            if (tmp == 12) tmp = 0;
        }

        calendar.set(Calendar.HOUR_OF_DAY, tmp);
        calendar.set(Calendar.MINUTE, minutes);
        return calendar.getTime();
    }

    public void setTimeChangedListener(TimeChangedListener timeChangedListener) {
        this.timeListener = timeChangedListener;
    }

    /**
     * This method is used to set picker's time
     * @param hour
     * @param minute
     */
    public void setTime(int hour, int minute){
        if(!isTimeValid(hour, minute, true)){
            throw new IllegalStateException(getResources().getString(R.string.outOfRangeExceptionMessage));
        }

        // To handle AM/PM decision when time is set
        if(amPm && hour > 11){
            amPm = !amPm;
        }else if(!amPm && (hour < HALF_DAY_AS_HOURS || hour == A_DAY_AS_HOURS)){
            amPm = !amPm;
        }

        this.initTime(hour,minute);
        this.invalidate();
    }

    /**
     * This method is used to set picker's time with AM/PM value
     * @param hour
     * @param minute
     * @param midday
     */
    public void setTime(int hour, int minute, boolean midday){
        if(!isTimeValid(hour, minute, false)){
            throw new IllegalStateException(getResources().getString(R.string.outOfRangeExceptionMessage2));
        }

        this.setHourFormat(false);
        this.amPm = midday;
        this.initTime(hour,minute);
        this.invalidate();
    }

    /***
     * This method is used to initialize picker's time
     * @param hour
     * @param minutes
     */
    private void initTime(int hour, int minutes) {
        this.hour = hour;
        this.minutes = minutes;
        this.firstRun = true;
        mStr = (minutes < 10) ? "0" + minutes : minutes + "";
        if (hourFormat) {
            amPmStr = "";
            degrees = ((hour % A_DAY_AS_HOURS) * 15) + ((minutes % AN_HOUR_AS_MINUTES) / 4);
        } else {
            if (hour == 0) hour = HALF_DAY_AS_HOURS;
            if ((hour == 12 && previousHour == 11) || (hour == 11 && previousHour == 12)) {
                amPm = !amPm;
            }

            amPmStr = amPm ? "AM" : "PM";
            degrees = ((hour % HALF_DAY_AS_HOURS) * 30) + ((minutes % AN_HOUR_AS_MINUTES) / 2);
        }
        angle = Math.toRadians(degrees) - (Math.PI / 2);

        firstRun = false;
    }

    /**
     * Checks if time values are between valid range
     * @param hour
     * @param minute
     * @param is24Hour if time is set as 24hour format
     * @return true if value is valid, false otherwise
     */
    private boolean isTimeValid(int hour, int minute, boolean is24Hour){
        if(is24Hour){
            return ((hour >= 0 && hour <= A_DAY_AS_HOURS)
                    && (minute >= 0 && minute <= AN_HOUR_AS_MINUTES));
        }else{
            return ((hour >= 0 && hour <= HALF_DAY_AS_HOURS)
                    && (minute >= 0 && minute <= AN_HOUR_AS_MINUTES));
        }
    }

}