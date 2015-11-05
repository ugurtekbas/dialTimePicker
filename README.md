# dialTimePicker
A custom time picker library for Android.
<br>
As a result of needing a fixed time picker for pre-lollipop devices, for my
[alarm app](https://play.google.com/store/apps/details?id=com.ugurtekbas.alarmshuffle) i developed a dial time picker view. 
Calculations in the main class mostly based on [erz05's view.](https://github.com/erz05/TimePicker)

<H2>Images</H2>
<img width="270px" height="480" src="/images/1.png" />
<img width="270px" height="480" src="/images/2.png" />
<br>

<H2>Usage</H2>
```xml

<picker.ugurtekbas.com.Picker.Picker
        android:id="@+id/picker"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />
        
<!-- or with some attributes in xml -->        
<picker.ugurtekbas.com.Picker.Picker
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/amPicker"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_centerInParent="true"
        app:hourFormat="false"
        app:canvasColor="#ffffff"
        app:textColor="#000000"
        />
```

```java
Picker picker = (Picker) findViewById(R.id.picker);
//or 
Picker picker = new Picker(context);

//Set background color
picker.setCanvasColor(Color.WHITE);
//Set dial color
picker.setDialColor(Color.ORANGE);
//Set clock color
picker.setClockColor(Color.CYAN);
//Set text color
picker.setTextColor(Color.BLACK);
//Enable 24 or 12 hour clock
picker.setHourFormat(true);
//get current hour
picker.getCurrentHour();
//get current minutes
picker.getCurrentMin();
//Set TimeChangedListener
picker.setTimeChangedListener(this);

//TimeChangeListener method
public void timeChanged(Date date){
}

```
