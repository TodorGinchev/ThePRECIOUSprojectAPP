package precious_rule_system.journeyview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import precious_rule_system.journeyview.utilities.Utilities;

public class JourneyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        /*Create a relative layout and associated params*/
        RelativeLayout mainLayout = new RelativeLayout(this);
        LayoutParams mainLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        int topPadding = 100;
        int leftPadding = 50;
        int bottomPadding = 20;
        int rightPadding = 20;

        int labelHeightDP = 40;
        int labelWidthDP = 150;
        int valueHeightDP = 40;
        int valueWidthDP = 150;
        int btnHeightDP = 60;
        int btnWidthDP = 150;

        final float scale = this.getResources().getDisplayMetrics().density;

        /*Create pointsLabel, pointsValue and Button views */
        TextView pointsLabel = new TextView(this);
        pointsLabel.setText("Set Points");
        pointsLabel.setId(Utilities.generateViewId());
        pointsLabel.setPadding(leftPadding,rightPadding,topPadding,bottomPadding);
        EditText pointsValue = new EditText(this);
        pointsValue.setInputType(InputType.TYPE_CLASS_NUMBER);
        pointsValue.setText("00");
        pointsValue.setId(Utilities.generateViewId());
        Button btnCreate = new Button(this);
        btnCreate.setText("Create Journey");
        btnCreate.setId(Utilities.generateViewId());

        /*Create params for child views*/
        LayoutParams pointsLabelParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        pointsLabelParams.addRule(RelativeLayout.ALIGN_LEFT);
        pointsLabelParams.height = (int)(labelHeightDP * scale + 0.5f);
        pointsLabelParams.width = (int)(labelWidthDP * scale + 0.5f);


        LayoutParams pointsValueParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        pointsValueParams.addRule(RelativeLayout.RIGHT_OF, pointsLabel.getId());
        pointsValueParams.addRule(RelativeLayout.ALIGN_BOTTOM, pointsLabel.getId());
        pointsValueParams.height = (int) (valueHeightDP * scale + 0.5f);
        pointsValueParams.width = (int) (valueWidthDP * scale + 0.5f);

        LayoutParams btnCreateParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        btnCreateParams.addRule(RelativeLayout.BELOW, pointsValue.getId());

        btnCreateParams.addRule(RelativeLayout.ALIGN_LEFT, pointsValue.getId());
        btnCreateParams.height = (int) (btnHeightDP * scale + 0.5f);
        btnCreateParams.width = (int) (btnWidthDP * scale + 0.5f);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateJourneyView(v);
            }
        });


        mainLayout.addView(pointsLabel,pointsLabelParams);
        mainLayout.addView(pointsValue,pointsValueParams);
        mainLayout.addView(btnCreate,btnCreateParams);

        setContentView(mainLayout, mainLayoutParams);
    }
    public void CreateJourneyView(View view) {
        //Create intent for Journey view Activity and Call it
        Intent intent = new Intent(this, JourneyView.class);
        startActivity(intent);
    }
}

