package pa_state_of_change.precious.comnet.aalto;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

public class PA_SOC_FragmentAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter{

    public PA_SOC_FragmentAdapter(FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
    }

    //@override
    public int getIconResId(int index) {
        // TODO Auto-generated method stub
        return 0;
    }

    //@override
    public Fragment getItem(int position)
    {
        Fragment fragment = new PA_SOC_FirstActivity();
        switch(position){
            case 0:
                fragment = new PA_SOC_FirstActivity();
                break;
            case 1:
                fragment = new PA_SOC_SecondActivity();
                break;
            case 2:
                fragment = new PA_SOC_ThirdActivity();
                break;
        }
        return fragment;
    }

    //@override
    public int getCount() {
        // TODO Auto-generated method stub
        return 3;
    }

    //@override
    public CharSequence getPageTitle(int position){
        String title = "";
        switch(position){
            case 0:
                title = "First";
                break;
            case 1:
                title = "Second";
                break;
            case 2:
                title = "Third";
                break;
        }
        return title;
    }
}