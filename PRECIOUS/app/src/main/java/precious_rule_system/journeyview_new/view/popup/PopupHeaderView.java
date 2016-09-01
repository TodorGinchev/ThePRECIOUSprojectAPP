package precious_rule_system.journeyview_new.view.popup;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;
import precious_rule_system.journeyview_new.utilities.Utilities;
import precious_rule_system.journeyview_new.view.ViewWrapper;
import precious_rule_system.rewardsystem.entities.RewardEvent;

/**
 * Created by christopher on 01.09.16.
 */

public class PopupHeaderView extends RelativeLayout {

    Context context;
    ViewWrapper parent;
    int width;
    int height;

    TextView headerLabel;

    public PopupHeaderView(Context context, ViewWrapper parent, int width, int height) {
        super(context);
        this.context = context;
        this.parent = parent;
        this.width = width;
        this.height = height;
        this.initialize(parent);
    }

    private void initialize(final ViewWrapper parent) {

        PopupHeaderBackground bg = new PopupHeaderBackground(context);
        this.addView(bg,new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        headerLabel = new TextView(context);
        headerLabel.setText("Header");
        headerLabel.setId(Utilities.generateViewId());
        headerLabel.setTextColor(Color.rgb(255,255,255));
        headerLabel.setPadding(20,0,0,0);

        LayoutParams headerLabelParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        headerLabelParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        headerLabelParams.leftMargin = 5;
        headerLabelParams.addRule(RelativeLayout.CENTER_VERTICAL);

        this.addView(headerLabel,headerLabelParams);

        Cross c = new Cross(context, 5, Color.rgb(255,255,255));
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.closePopupButtonClicked();
            }
        });

        LayoutParams btnParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        btnParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        btnParams.addRule(RelativeLayout.CENTER_VERTICAL);
        btnParams.rightMargin = 20;
        int h = Math.round((float) this.height /3);
        btnParams.height = h;
        btnParams.width = h;

        this.addView(c, btnParams);

    }

    public void update(RewardEvent e) {
        if (e.isMilestone()) {
            headerLabel.setText("Milestone");
        } else {
            headerLabel.setText("Point Increase");
        }
    }


}
