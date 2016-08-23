package precious_rule_system.journeyview;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import precious_rule_system.journeyview.utilities.Utilities;

import java.util.List;

/**
 * Created by khatt on 8/10/2016.
 */
public class LandscapeFrame extends FrameLayout {

    LandscapeView backgroundView;
    LinearLayout controlsView;
    Utilities.LandscapeType type;
    private int steps;
    private List<Milestone> milestones;

    public LandscapeFrame(Context context, Utilities.LandscapeType landscapeType) {
        super(context);
    }
}
