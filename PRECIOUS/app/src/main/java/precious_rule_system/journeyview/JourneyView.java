package precious_rule_system.journeyview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import precious_rule_system.journeyview.utilities.Utilities;


public class JourneyView extends AppCompatActivity {

    ScrollView scroll;
    LinearLayout linearLayout;
    TextView textView;
    LandscapeView view1;
    LandscapeView view2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitializeView();
        setContentView(scroll);
    }

    private void InitializeView() {

        scroll = new ScrollView(this);
        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        view1 = new LandscapeView(this, Utilities.LandscapeType.MOUNTAIN);
        view2 = new LandscapeView(this, Utilities.LandscapeType.MOUNTAIN);
        linearLayout.addView(view1);
        linearLayout.addView(view2);

        scroll.addView(linearLayout, new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}