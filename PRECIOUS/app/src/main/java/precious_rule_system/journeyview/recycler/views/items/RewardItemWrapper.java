package precious_rule_system.journeyview.recycler.views.items;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import precious_rule_system.journeyview.data.StatePageRewardEvent;

/**
 * Created by christopher on 08.09.16.
 */

public class RewardItemWrapper extends RelativeLayout {

    Context context;
    int innerSize;

    public StatePageRewardEvent event;
    public float[] pivot;

    public RewardItemWrapper(Context context, StatePageRewardEvent event, int innerSize) {
        super(context);
        this.context = context;
        this.event = event;
        this.innerSize = innerSize;

        this.setupItemView();
    }

    private void setupItemView() {
        RewardItemView v = new RewardItemView(this.context, event, innerSize);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(v);
    }
}
