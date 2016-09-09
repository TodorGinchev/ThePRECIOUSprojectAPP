package precious_rule_system.journeyview.view.popup;

import android.content.Context;
import android.widget.RelativeLayout;

import precious_rule_system.journeyview.view.JourneyView;
import precious_rule_system.rewardsystem.entities.RewardEvent;

/**
 * Created by christopher on 01.09.16.
 */

public class Popup extends RelativeLayout {

    PopupHeaderView top;
    PopupMainView main;
    PopupFooterView bottom;

    final int barHeight = 150;

    public Popup(Context context, JourneyView parent, int width, int height) {
        super(context);

        top = new PopupHeaderView(context, parent, width, barHeight);
        main = new PopupMainView(context, parent);
        bottom = new PopupFooterView(context, parent, width, height);

        RelativeLayout.LayoutParams p1 = new RelativeLayout.LayoutParams(width, barHeight);
        this.addView(top, p1);

        RelativeLayout.LayoutParams p2 = new RelativeLayout.LayoutParams(width, height-2*barHeight);
        p2.topMargin = barHeight;
        this.addView(main, p2);

        RelativeLayout.LayoutParams p3 = new RelativeLayout.LayoutParams(width, barHeight);
        p3.topMargin = height-barHeight;
        this.addView(bottom, p3);

    }

    public void setRewardEvent(RewardEvent e) {
        top.update(e);
        main.update(e);
    }

}
