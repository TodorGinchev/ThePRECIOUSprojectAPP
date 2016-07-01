package confidence_ruler.precious.comnet.aalto;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;


public class CR_FragmentAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter{

    public CR_FragmentAdapter(FragmentManager fm) {
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
        Fragment fragment = new CR_FirstActivity();
        switch(position){
            case 0:
                fragment = new CR_FirstActivity();
                break;
            case 1:
                fragment = new CR_SecondActivity();
                break;
            case 2:
                fragment = new CR_ThirdActivity();
                break;
            case 3:
                fragment = new CR_ForthActivity();
                break;
        }
        return fragment;
    }

    //@override
    public int getCount() {
        // TODO Auto-generated method stub
        return 4;
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
        }
        return title;
    }
}