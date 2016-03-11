package outcomegoal.precious.comnet.aalto;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

public class OGFragmentAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter{

    public OGFragmentAdapter(FragmentManager fm) {
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
        Fragment fragment = new OGFirstActivity();
        switch(position){
            case 0:
                fragment = new OGFirstActivity();
                break;
            case 1:
                fragment = new OGSecondActivity();
                break;
            case 2:
                fragment = new OGThirdActivity();
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