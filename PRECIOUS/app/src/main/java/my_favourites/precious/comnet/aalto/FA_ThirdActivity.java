package my_favourites.precious.comnet.aalto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import aalto.comnet.thepreciousproject.R;


public class FA_ThirdActivity extends Fragment  {
    public static final String TAG = "FA_ThirdActivity";
    private View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fa_layout3, null);

        return v;
    }

}
