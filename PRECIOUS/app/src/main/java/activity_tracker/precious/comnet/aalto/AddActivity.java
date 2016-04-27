package activity_tracker.precious.comnet.aalto;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import aalto.comnet.thepreciousproject.R;


public class AddActivity extends FragmentActivity {

    public static String TAG = "AddActivity";
    public static final String UP_PREFS_NAME = "UploaderPreferences";
    private static int ActivityPosition;
    private static String ActivityType;
    private static TextView tvDate;
    private static TextView tvStartTime;
    private static TextView tvEndTime;
    private static TextView tvDuration;
    private static int startHour;
    private static int startMinute;
    private static int endHour;
    private static int endMinute;
    private static int durationHour;
    private static int durationMinute;
    private static int intensitySpinnerPosition;

    /**
     *
     * @param savedInstanceState
     */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_add_activity_layout);

        ActivityType = getString(R.string.walk);
        ActivityPosition=27;
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvEndTime = (TextView) findViewById(R.id.tvEndTime);
        tvDuration = (TextView) findViewById(R.id.tvDuration);
        tvDate.setTextColor(getResources().getColor(R.color.selfMonitoring));
        tvStartTime.setTextColor(getResources().getColor(R.color.selfMonitoring));
        tvEndTime.setTextColor(getResources().getColor(R.color.selfMonitoring));
        tvDuration.setTextColor(getResources().getColor(R.color.selfMonitoring));
        startHour=-1;
        startMinute=-1;
        endHour=-1;
        endMinute=-1;
        durationHour=-1;
        durationMinute=-1;
        intensitySpinnerPosition=1;

        Spinner spinner = (Spinner) findViewById(R.id.spinnerIntensity);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pa_intensities, R.layout.spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(intensitySpinnerPosition);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                intensitySpinnerPosition=position;
                updateStepsCaloriesInfo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                intensitySpinnerPosition=1;
            }
        });

                //Get screen size and calculate object sizes
//        Display display = getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int screen_width = size.x;
//        int screen_height = size.y;
//        //Set Main layout size
//        RelativeLayout rl = (RelativeLayout) findViewById(R.id.rl_add_activity);
//        rl.getLayoutParams().height = (int)(0.9*screen_height);  // change height of the layout
//        rl.getLayoutParams().width = (int)(0.9*screen_width);  // change height of the layout


//        ImageButton tvActivity = (ImageButton) findViewById(R.id.selected_pa_iv);
//        tvActivity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setActivityType();
//            }
//        });
    }

    /**
     *
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

//    /**
//     *
//     * @param v
//     */
//    public void closeView (View v){
//        Intent i = new Intent(this, PromptSaveInfo.class);
//        startActivityForResult(i, 1002);
//    }

    /**
     *
     */

    /**
     *
     */
    @Override protected void onActivityResult (int requestCode,
                                               int resultCode, Intent data){
        if (requestCode==1001 && resultCode==RESULT_OK) {
            ImageButton ibActivity = (ImageButton) findViewById(R.id.selected_pa_iv);
            TextView tvActivityType = (TextView) findViewById(R.id.tvActivityTitle);
            ActivityType = data.getExtras().getString("activity");
            ActivityPosition = data.getExtras().getInt("activity_position");
            tvActivityType.setText(ActivityType);
            ibActivity.setImageResource(atUtils.getPAdrawableID(this,ActivityPosition));
            updateStepsCaloriesInfo();
        }
//        else if (requestCode==1002 && resultCode==RESULT_OK) {
//            boolean saveInfo = data.getExtras().getBoolean("saveInfo");
//            if (saveInfo) {
//                Toast.makeText(this, "Activity saved", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//            else {
//                Toast.makeText(this, "Activity not saved", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
    }


    /*
     * HANDLE TOUCH EVENTS
     */

    public void onCancelTouched(View v){
        finish();
    }
    public void onSaveTouched(View v){
        Toast.makeText(this,"I am implementing the saving, deleting and drawing of PA right now",Toast.LENGTH_LONG).show();
        finish();
    }
    public void onDeleteTouched(View v){
        Toast.makeText(this,"I am implementing the saving, deleting and drawing of PA right now",Toast.LENGTH_LONG).show();
        finish();
    }


    public void onSelecPAButtonTouched(View v){
        Intent i = new Intent(this, ChooseActivity.class);
        startActivityForResult(i, 1001);
//        ImageButton tvActivity = (ImageButton) findViewById(R.id.selected_pa_iv);

    }

    public void onDateTouched(View v){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(this.getSupportFragmentManager(), "datePicker");
        updateStepsCaloriesInfo();
    }
    public void onStartTimeTouched(View v){
        // TODO Auto-generated method stub
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                startHour=selectedHour;
                startMinute=selectedMinute;
                if(selectedMinute>9)
                    tvStartTime.setText(selectedHour + ":" + selectedMinute);
                else
                    tvStartTime.setText(selectedHour + ":0" + selectedMinute);
                tvStartTime.setTextColor(getResources().getColor(R.color.add_activity_text));

                if (durationHour!=-1) {
                    int endTimeInMin = (startHour + durationHour) * 60 + startMinute + durationMinute;
                    if (endTimeInMin / 60 <= 23)
                        endHour = endTimeInMin / 60;
                    else
                        endHour = endTimeInMin / 60 - 24;
                    endMinute = endTimeInMin % 60;
                    if (endMinute > 9)
                        tvEndTime.setText(endHour + ":" + endMinute);
                    else
                        tvEndTime.setText(endHour + ":0" + endMinute);
                    tvEndTime.setTextColor(getResources().getColor(R.color.add_activity_text));
                }
                else if(endHour!=-1){
                    int duration = (endHour*60+endMinute)-(startHour*60+startMinute);
                    if(duration<0)
                        duration += 24*60;
                    durationHour=duration/60;
                    durationMinute=duration%60;
                    tvDuration.setText(durationHour + "h" + durationMinute+"min");
                    tvDuration.setTextColor(getResources().getColor(R.color.add_activity_text));
                }

                updateStepsCaloriesInfo();
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle(getString(R.string.activity_started));
        mTimePicker.show();
    }
    public void onEndTimeTouched(View v){
        // TODO Auto-generated method stub
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                endHour=selectedHour;
                endMinute=selectedMinute;
                if(selectedMinute>9)
                    tvEndTime.setText(selectedHour + ":" + selectedMinute);
                else
                    tvEndTime.setText(selectedHour + ":0" + selectedMinute);
                tvEndTime.setTextColor(getResources().getColor(R.color.add_activity_text));
                if (durationHour!=-1) {
                    int startTimeMin = (endHour*60+endMinute)-(durationHour*60+durationMinute);
                    if (startTimeMin / 60 >= 0)
                        startHour = startTimeMin / 60;
                    else
                        startHour = startTimeMin / 60 + 23;
                    startMinute =  startTimeMin % 60;
                    if(startMinute<0)
                        startMinute+=60;
                    if (startMinute > 9)
                        tvStartTime.setText(startHour + ":" + startMinute);
                    else
                        tvStartTime.setText(startHour + ":0" + startMinute);
                    tvStartTime.setTextColor(getResources().getColor(R.color.add_activity_text));
                }
                else if(startHour!=-1){
                    int duration = (endHour*60+endMinute)-(startHour*60+startMinute);
                    durationHour=duration/60;
                    durationMinute=duration%60;
                    tvDuration.setText(durationHour + "h" + durationMinute+"min");
                    tvDuration.setTextColor(getResources().getColor(R.color.add_activity_text));
                }
                updateStepsCaloriesInfo();
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle(getString(R.string.activity_ended));
        mTimePicker.show();
    }
    public void onDurationTouched(View v){
        // TODO Auto-generated method stub
//        Calendar mcurrentTime = Calendar.getInstance();
        int hour = 0;//mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = 0;//mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                durationHour=selectedHour;
                durationMinute=selectedMinute;
                tvDuration.setText(selectedHour + "h" + selectedMinute+"min");
                tvDuration.setTextColor(getResources().getColor(R.color.add_activity_text));
                if(startHour!=-1){
                    int endTimeMin = (durationHour+startHour)*60 + durationMinute+startMinute;
                    endHour=endTimeMin/60;
                    if(endHour>23)
                        endHour-=24;
                    endMinute=endTimeMin%60;
                    if (startMinute > 9)
                        tvEndTime.setText(endHour + ":" + endMinute);
                    else
                        tvEndTime.setText(endHour + ":0" + endMinute);
                    tvEndTime.setTextColor(getResources().getColor(R.color.add_activity_text));
                }
                else if(endHour!=-1){
                    int startTimeMin = (endHour*60+endMinute)-(durationHour*60+durationMinute);
                    if (startTimeMin / 60 >= 0)
                        startHour = startTimeMin / 60;
                    else
                        startHour = startTimeMin / 60 + 23;
                    startMinute=startTimeMin%60;
                    if(startMinute<0)
                        startMinute+=60;
                    if (startMinute > 9)
                        tvStartTime.setText(startHour + ":" + startMinute);
                    else
                        tvStartTime.setText(startHour + ":0" + startMinute);
                    tvStartTime.setTextColor(getResources().getColor(R.color.add_activity_text));
                }
                updateStepsCaloriesInfo();
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle(getString(R.string.activity_duration));
        mTimePicker.show();
    }
    /**
     *
     * Configure datePicker
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            setTvDate(year, month + 1, day);
        }

        public static void setTvDate(int year, int month, int dayOfMonth){
            tvDate.setText(" " + month + "/" + dayOfMonth+"/"+year);
        }
    }


    /**
     *
     */
    public void updateStepsCaloriesInfo(){
        if(ActivityPosition!=-1 && durationHour!=-1 && !ActivityType.equals("-1")) {
            String ActivityTypeNoSpace = ActivityType.replace(" ", "_");
            try {
                Log.i(TAG,"Looking for array with id="+ActivityTypeNoSpace);
                int activity_id = getResources().getIdentifier(ActivityTypeNoSpace, "array", this.getPackageName());
                Log.i(TAG, "Id found=" + activity_id);
                int steps = getResources().getIntArray(activity_id)[1];
                if(steps==0)
                    steps = (int)(steps*0.8);
                else if(steps==2)
                    steps = (int)(steps*1.2);
                Log.i(TAG,"Steps 1="+steps);
                steps = steps * (durationHour * 60 + durationMinute);
                TextView tvSteps = (TextView) findViewById(R.id.tvSteps);
                tvSteps.setText(steps + "");
                Log.i(TAG, "STEPS=_" + steps);
                SharedPreferences preferences = this.getSharedPreferences(UP_PREFS_NAME, 0);
                if(preferences.getString("weight","0").equals("?"))
                    Toast.makeText(this,R.string.weight_not_available,Toast.LENGTH_LONG).show();
                int weight = Integer.parseInt(preferences.getString("weight","0"));
                Log.i(TAG,"Weight= "+preferences.getString("weight","0"));
                long calories = (long)steps*(long)weight*1640/20000/136;
                TextView tvCalories = (TextView) findViewById(R.id.tvCalories);
                Log.i(TAG, "Calories=_" + calories);
                tvCalories.setText(calories+"kcal");

            } catch (Exception e) {
                Log.e(TAG, "Error:", e);
            }
//            http://keisan.casio.com/exec/system/1350891527
        }
    }
}

