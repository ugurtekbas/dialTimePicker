package picker.ugurtekbas.com.DialTimePicker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import picker.ugurtekbas.com.Picker.Picker;

/**
 * For sample app, his controls two fragments with different time pickers.
 * @author Ugur Tekbas
 */
public class MainFragment extends Fragment{

    int layoutID;

    public static MainFragment newInstance(int layoutID){
        MainFragment fragment = new MainFragment();
        fragment.layoutID = layoutID;
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putInt("layoutID", layoutID);
    }

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        if(bundle != null && bundle.containsKey("layoutID")){
            layoutID = bundle.getInt("layoutID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(layoutID, container, false);

        if (layoutID==R.layout.ampm_picker){
            final Picker  picker2 =   (Picker)v.findViewById(R.id.amPicker);
            picker2.setClockColor(getResources().getColor(R.color.clockColor));
            picker2.setDialColor(getResources().getColor(R.color.dialColor));
            picker2.setTime(12, 45, Picker.AM);
            picker2.setTrackSize(20);
            picker2.setDialRadiusDP(60);

            final CheckBox checkBox =   (CheckBox)v.findViewById(R.id.checkbox);
            picker2.setEnabled(checkBox.isChecked());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    picker2.setEnabled(isChecked);
                }
            });
        }else{
            final Picker  picker1 =   (Picker)v.findViewById(R.id.picker);
            picker1.setDialAdjust(false);
            final TextView et =  (TextView)v.findViewById(R.id.et);
            final Button btn  =   (Button)v.findViewById(R.id.btn);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String minute = Integer.toString(picker1.getCurrentMin());
                    if (picker1.getCurrentMin() < 10) {
                        minute = "0" + minute;
                    }
                    et.setText("It's " + picker1.getCurrentHour() + ":" + minute);
                }
            });
        }

        return v;
    }
}
