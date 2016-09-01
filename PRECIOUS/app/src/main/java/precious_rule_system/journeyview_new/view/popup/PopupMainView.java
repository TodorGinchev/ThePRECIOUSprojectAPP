package precious_rule_system.journeyview_new.view.popup;

import android.content.Context;
import android.graphics.Color;
import android.widget.RelativeLayout;

import precious_rule_system.journeyview_new.view.ViewWrapper;
import precious_rule_system.rewardsystem.entities.RewardEvent;

/**
 * Created by christopher on 01.09.16.
 */

public class PopupMainView extends RelativeLayout {

    public PopupMainView(Context context, ViewWrapper viewWrapper) {
        super(context);
        this.setBackgroundColor(Color.parseColor("#ffffff"));
    }

    public void update(RewardEvent e) {

    }
}
