package importance_ruler.precious.comnet.aalto;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;


public class IRFirstActivity extends Fragment {
    public static final String PREFS_NAME = "IRsubappPreferences";
    public static View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.ir_layout1, null);
        SeekBar sb = (SeekBar) v.findViewById(R.id.seekBar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences preferences = ImportanceRulerActivity.appConext.getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("IRseekbarProgress", progress);
                editor.apply();
                TextView tv1 = (TextView) IRFirstActivity.v .findViewById(R.id.textView11);
                tv1.setText(Integer.toString(progress+1));
//                TextView tv9 = (TextView) IRFirstActivity.v .findViewById(R.id.textView9);
//                TextView tv10 = (TextView) IRFirstActivity.v .findViewById(R.id.textView10);
//                int tv9Color = 220*progress/10;
//                tv9.setTextColor(Color.rgb(tv9Color,tv9Color,tv9Color));
//                int tv10Color = 220*(10-progress)/10;
//                tv10.setTextColor(Color.rgb(tv10Color,tv10Color,tv10Color));
            }
        });
        SharedPreferences preferences = ImportanceRulerActivity.appConext.getSharedPreferences(PREFS_NAME, 0);
        sb.setProgress(preferences.getInt("IRseekbarProgress",5));
        TextView tv = (TextView) v.findViewById(R.id.textView11);
        tv.setText(Integer.toString(preferences.getInt("IRseekbarProgress",5)+1));
        return v;
    }
}
