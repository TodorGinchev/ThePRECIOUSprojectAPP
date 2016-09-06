package precious_rule_system.journeyview.view.popup;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import precious_rule_system.journeyview.view.JourneyView;
import precious_rule_system.journeyview.helpers.Utilities;

/**
 * Created by christopher on 01.09.16.
 */

public class PopupFooterView extends RelativeLayout {

    Context context;
    JourneyView parent;
    int width;
    int height;

    public PopupFooterView(Context context, JourneyView parent, int width, int height) {
        super(context);
        this.context = context;
        this.parent = parent;
        this.width = width;
        this.height = height;
        this.initialize(parent);
    }

    private void initialize(final JourneyView parent) {

        PopupFooterBackground bg = new PopupFooterBackground(context);
        this.addView(bg,new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        TextView label = new TextView(context);
        label.setText("Awesome");
        label.setId(Utilities.generateViewId());
        label.setTextColor(Color.rgb(255,255,255));

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.closePopupButtonClicked();
            }
        });

        this.addView(label,params);
    }
}
