package importance_ruler.precious.comnet.aalto;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

public class IRFragmentAdapter extends FragmentStatePagerAdapter implements IconPagerAdapter{

    public IRFragmentAdapter(FragmentManager fm) {
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
        Fragment fragment = new IRFirstActivity();
        switch(position){
            case 0:
                fragment = new IRFirstActivity();
                break;
            case 1:
                fragment = new IRSecondActivity();
                break;
            case 2:
                fragment = new IRThirdActivity();
                break;
            case 3:
                fragment = new IRForthActivity();
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