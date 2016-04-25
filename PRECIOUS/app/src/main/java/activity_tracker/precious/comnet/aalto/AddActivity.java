package activity_tracker.precious.comnet.aalto;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import aalto.comnet.thepreciousproject.R;


public class AddActivity extends FragmentActivity {

    public static String TAG = "AddActivity";
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

    /**
     *
     * @param savedInstanceState
     */
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_add_activity_layout);

        ActivityType = "-1";
        ActivityPosition=-1;
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvEndTime = (TextView) findViewById(R.id.tvEndTime);
        tvDuration = (TextView) findViewById(R.id.tvDuration);
        startHour=-1;
        startMinute=-1;
        endHour=-1;
        endMinute=-1;
        durationHour=-1;
        durationMinute=-1;

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
        finish();
    }
    public void onDeleteTouched(View v){
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
                }
                else if(endHour!=-1){
                    int duration = (endHour*60+endMinute)-(startHour*60+startMinute);
                    durationHour=duration/60;
                    durationMinute=duration%60;
                    tvDuration.setText(durationHour + "h" + durationMinute+"min");
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
                }
                else if(startHour!=-1){
                    int duration = (endHour*60+endMinute)-(startHour*60+startMinute);
                    durationHour=duration/60;
                    durationMinute=duration%60;
                    tvDuration.setText(durationHour + "h" + durationMinute+"min");
                }
                updateStepsCaloriesInfo();
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle(getString(R.string.activity_ended));
        mTimePicker.show();
    }
    public void onDurationTouched(View v){
        // TODO Auto-generated method stub
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                durationHour=selectedHour;
                durationMinute=selectedMinute;
                tvDuration.setText(selectedHour + "h" + selectedMinute+"min");

                if(startHour!=-1){
                    int endTimeMin = (durationHour+startHour)*60 + durationMinute+startMinute;
                    endHour=endTimeMin/60;
                    if(endHour>23)
                        endHour-=24;
                    endMinute=endTimeMin%60;
                    tvEndTime.setText(endHour + "h" + endMinute+"min");
                }
                else if(endHour!=-1){
                    int startTimeMin = (endHour*60+endMinute)-(durationHour*60+startMinute);
                    if (startTimeMin / 60 >= 0)
                        startHour = startTimeMin / 60;
                    else
                        startHour = startTimeMin / 60 + 23;
                    startMinute=startTimeMin%60;
                    if(startMinute<0)
                        startMinute+=60;
                    tvStartTime.setText(startHour + "h" + startMinute+"min");
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
        if(!ActivityType.equals("-1") && durationHour!=-1) {
            String ActivityTypeNoSpace = ActivityType.replace(" ", "_");
            try {
                int steps = getResources().getInteger(getResources().getIdentifier(ActivityTypeNoSpace, "integer", "aalto.comnet.thepreciousproject"));
                steps = steps * (durationHour * 60 + durationMinute);
                TextView tvSteps = (TextView) findViewById(R.id.tvSteps);
                tvSteps.setText(steps + "");
                Log.i(TAG, "STEPS_WALK_SLOW=_" + steps);

                int calories = steps*1640/20000/136;
                TextView tvCalories = (TextView) findViewById(R.id.tvCalories);
                tvCalories.setText(calories+"");

            } catch (Exception e) {
                Log.e(TAG, "", e);
            }

//            PARA MAÑANA: https://www.verywell.com/pedometer-steps-to-calories-converter-3882595
        }
    }
}

