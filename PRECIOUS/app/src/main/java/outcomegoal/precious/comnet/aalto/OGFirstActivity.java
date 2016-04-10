package outcomegoal.precious.comnet.aalto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import aalto.comnet.thepreciousproject.R;


public class OGFirstActivity  extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.og_layout1, null);

        return v;
    }
}
