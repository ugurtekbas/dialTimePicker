# dialTimePicker
A custom time picker library for Android.
<br>
As a result of needing a fixed time picker for pre-lollipop devices, for my
[alarm app](https://play.google.com/store/apps/details?id=com.ugurtekbas.alarmshuffle) i developed a dial time picker view. 
Calculations in the main class mostly based on [erz05's view.](https://github.com/erz05/TimePicker)

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-dialTimePicker-green.svg?style=true)](https://android-arsenal.com/details/1/3244)

<H2>Images</H2>
<img width="270px" height="480" src="/images/1.png" />
<img width="270px" height="480" src="/images/2.png" />
<br>

<H2>Usage</H2>
Gradle Import:

Add the specific repository to your build file:
```groovy
repositories {
    maven {
        url "https://jitpack.io"
    }
}
```
Add the dependency in your build file (do not forget to specify the correct qualifier, usually 'aar'):
```groovy
dependencies {
    compile 'com.github.ugurtekbas:dialTimePicker:69ba769f45'
}
```

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
        app:trackSize="20dp"
        app:dialRadius="60dp"
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
//Diasble/enable the picker 
picker.setEnabled(false);
//Set dial's size
picker.setTrackSize(20);
//Set adjuster's size
setDialRadiusDP(60);
//Initialize picker's time
picker.initTime(9,0);
//Set TimeChangedListener
picker.setTimeChangedListener(this);

//TimeChangeListener method
public void timeChanged(Date date){
}

```
<H2>License</H2>
    Copyright 2016 Ugur Tekbas

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
