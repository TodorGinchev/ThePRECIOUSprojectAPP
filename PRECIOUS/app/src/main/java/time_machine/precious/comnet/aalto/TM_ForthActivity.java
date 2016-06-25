package time_machine.precious.comnet.aalto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import aalto.comnet.thepreciousproject.R;


public class TM_ForthActivity extends Fragment {

    public ImageButton okButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.tm_layout4, null);
        okButton = (ImageButton) v.findViewById(R.id.button);
        return v;
    }

}
