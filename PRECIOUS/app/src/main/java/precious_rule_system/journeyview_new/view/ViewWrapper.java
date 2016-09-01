package precious_rule_system.journeyview_new.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import precious_rule_system.journeyview_new.DataManager;
import precious_rule_system.journeyview_new.assets.JourneyAssets;
import precious_rule_system.journeyview_new.grid.JourneyGrid;
import precious_rule_system.journeyview_new.grid.JourneyGridAdapter;
import precious_rule_system.journeyview_new.grid.JourneyGridAdapterDelegate;
import precious_rule_system.journeyview_new.grid.JourneyGridDelegate;
import precious_rule_system.journeyview_new.page.JourneyPageViewWrapperDelegate;
import precious_rule_system.journeyview_new.utilities.Layout;
import precious_rule_system.journeyview_new.utilities.Utilities;
import precious_rule_system.journeyview_new.view.popup.Popup;
import precious_rule_system.rewardsystem.entities.RewardEvent;
import rules.helpers.Tuple;

/**
 * Created by christopher on 01.09.16.
 */

public class ViewWrapper extends RelativeLayout implements JourneyPageViewWrapperDelegate, JourneyGridDelegate {

    JourneyGrid grid;
    JourneyGridAdapter adapter;
    ViewDisplay display;
    Context context;
    DataManager data;
    private Layout layout;
    private JourneyAssets assets;
    private Popup popup;
    private RelativeLayout.LayoutParams popupParams;
    View overlay;
    int maxOverlayAlpha = 240;
    int popupOpenPosition;
    int popupClosedPosition;

    int width;
    int height;

    public ViewWrapper(Context context, int width, int height, Layout layout, JourneyAssets assets, DataManager data) {
        super(context);
        this.data = data;
        this.context = context;
        this.width = width;
        this.height = height;
        this.layout = layout;
        this.assets = assets;
        this.setupDisplayView();
        this.setupGridView();
        this.setupPopup();
    }

    private void setupGridView() {
        grid = new JourneyGrid(this.context, this);
        grid.setId(Utilities.generateViewId());
        adapter = new JourneyGridAdapter(this.width, this.height, this.context, this, this.data, this.assets, this.layout);
        grid.setAdapter(adapter);

        int currentItem = this.data.getPlayerItem();
        grid.setSelection(currentItem);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.addView(grid, 0, params);
    }

    private void setupDisplayView() {
        display = new ViewDisplay(this.context);
        display.setId(Utilities.generateViewId());

        int width = this.width/2;
        int height = this.height/8;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.leftMargin = 10;
        params.topMargin = 10;

        int points = this.data.getNumberOfPoints();
        int currentItem = this.data.getPlayerItem();
        this.addView(display, params);

        this.display.updateLabel(this.data.getLabelForItem(currentItem));
        this.display.updatePoints(points);

    }

    private void setupPopup() {

        int width = this.width/5*3;
        int height = this.height/2;
        popup = new Popup(context, this, width, height);

        popupParams = new RelativeLayout.LayoutParams(width, height);

        popupOpenPosition = (this.height-height)/2;
        popupClosedPosition = (this.height-height)/2 + this.height;

        popupParams.leftMargin = (this.width-width)/2;
        popupParams.topMargin = popupClosedPosition;

        overlay = new View(context);
        overlay.setBackgroundColor(Color.parseColor("#363636"));
        overlay.getBackground().setAlpha(0);

        this.addView(overlay, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        this.addView(popup, popupParams);
    }

    public void rewardEventClicked(RewardEvent e) {
        popup.setRewardEvent(e);
        System.out.println("Reward clicked!");
        blendInPopup(popup, popupParams);
    }

    public void closePopupButtonClicked() {
        System.out.println("Popupclose clicked!");
        blendOutPopup(popup, popupParams);
    }

    public void onScroll(int item) {
        this.display.updateLabel(this.data.getLabelForItem(item));
    }

    public void updatedPoints() {
        int points = this.data.getNumberOfPoints();
        this.display.updatePoints(points);
        this.grid.invalidate();
    }

    public void blendInPopup(final Popup popup, final RelativeLayout.LayoutParams params) {

        final int start = popupClosedPosition;
        final int stop = popupOpenPosition;
        final int diff = Math.abs(stop-start);

        overlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        ValueAnimator anim = ValueAnimator.ofInt(start, stop);
        anim.setDuration(500);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                int value = (int) animation.getAnimatedValue();
                float current = -(float) (value-start) / (float) diff;
                overlay.getBackground().setAlpha(Math.round(current*maxOverlayAlpha));
                params.topMargin = value;
                popup.setLayoutParams(params);

            }
        });
        anim.start();

    }

    public void blendOutPopup(final Popup popup, final RelativeLayout.LayoutParams params) {

        final int start = popupOpenPosition;
        final int stop = popupClosedPosition;
        final int diff = Math.abs(stop-start);

        overlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        ValueAnimator anim = ValueAnimator.ofInt(start, stop);
        anim.setDuration(500);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                float current = 1.0f - (float) (value-start) / (float) diff;
                overlay.getBackground().setAlpha(Math.round(current*maxOverlayAlpha));
                params.topMargin = value;
                popup.setLayoutParams(params);
            }
        });
        anim.start();

    }





}
