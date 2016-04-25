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
            case 3:
                fragment = new OGForthActivity();
                break;
            case 4:
                fragment = new OGFifthActivity();
                break;
        }
        return fragment;
    }

    //@override
    public int getCount() {
        // TODO Auto-generated method stub
        return 5;
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
            case 3:
                title = "Forth";
                break;
            case 4:
                title = "Forth";
                break;
        }
        return title;
    }
}