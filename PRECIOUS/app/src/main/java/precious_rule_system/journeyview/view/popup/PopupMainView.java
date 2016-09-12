package precious_rule_system.journeyview.view.popup;

import android.content.Context;
import android.graphics.Color;
import android.widget.RelativeLayout;

import precious_rule_system.journeyview.view.JourneyView;
import precious_rule_system.rewardsystem.entities.RewardEvent;

/**
 * Created by christopher on 01.09.16.
 */

public class PopupMainView extends RelativeLayout {

    public PopupMainView(Context context, JourneyView viewWrapper) {
        super(context);
        this.setBackgroundColor(Color.parseColor("#ffffff"));
    }

    public void update(RewardEvent e) {

    }
}
