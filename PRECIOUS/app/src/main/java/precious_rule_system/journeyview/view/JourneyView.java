package precious_rule_system.journeyview.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import precious_rule_system.journeyview.constants.Constants;
import precious_rule_system.journeyview.recycler.RecyclerWrapper;
import precious_rule_system.journeyview.view.popup.Popup;
import precious_rule_system.journeyview.helpers.Utilities;
import precious_rule_system.rewardsystem.entities.RewardEvent;
import ui.precious.comnet.aalto.precious.ui_MainActivity;

/**
 * Created by christopher on 04.09.16.
 */

public class JourneyView extends RelativeLayout {

    ui_MainActivity.JourneyStore store;
    Context context;
    int currentBackgroundColor = Constants.Landscape.MOUNTAIN.getColor();
    boolean isAnimatingBackgroundColor = false;
    ValueAnimator colorAnimation;

    JourneyViewDisplay display;
    Popup popup;
    private RelativeLayout.LayoutParams popupParams;
    View overlay;

    int maxOverlayAlpha = 240;
    int popupOpenPosition;
    int popupClosedPosition;

    public JourneyView(Context context, ui_MainActivity.JourneyStore store) {
        super(context);
        this.context = context;
        this.store = store;
        this.store.setJourneyView(this);
        this.setBackgroundColor(currentBackgroundColor);
        this.setupRecycler();
        this.setupDisplayView();
    }

    public void setBackgroundColorAnimated(final int color) {

        if (getCurrentBackgroundColor() == color) return;
        if (getIsAnimatingBackgroundColor()) return;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                if (getIsAnimatingBackgroundColor()) {
                    colorAnimation.cancel();
                    colorAnimation.end();
                }

                int colorFrom = getCurrentBackgroundColor();
                final int colorTo = color;

                colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.setDuration(Constants.colorAnimationDuration);

                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int newColor = (int) animation.getAnimatedValue();
                        setBackgroundColor(newColor);
                        setCurrentBackgroundColor(newColor);
                        if (newColor == colorTo) {
                            setIsAnimatingBackgroundColor(false);
                        }
                    }
                });

                setIsAnimatingBackgroundColor(true);
                colorAnimation.start();

            }
        });
    }

    public synchronized int getCurrentBackgroundColor() {
        return this.currentBackgroundColor;
    }

    public synchronized void setCurrentBackgroundColor(int color) {
        this.currentBackgroundColor = color;
    }

    public synchronized boolean getIsAnimatingBackgroundColor() {
        return isAnimatingBackgroundColor;
    }

    public synchronized void setIsAnimatingBackgroundColor(boolean set) {
        this.isAnimatingBackgroundColor = set;
    }

    private void setupRecycler() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        RecyclerWrapper recyclerView = new RecyclerWrapper(this.context, this.store);
        this.addView(recyclerView, 0, params);
    }

    private void setupDisplayView() {

        display = new JourneyViewDisplay(this.store, this.context);
        display.setId(Utilities.generateViewId());

        // TODO
        int width = 600;
        int height = 300;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.leftMargin = 10;
        params.topMargin = 10;

        int points = 0;
        this.addView(display, params);

        this.display.updateLabel("");
        this.display.updatePoints(points);

    }

    public synchronized void updatePoints(int points) {
        this.display.updatePoints(points);
    }

    public synchronized void updateLabel(String label) {
        this.display.updateLabel(label);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (popup != null && overlay != null) return;

        // TODO: Adjust popup, put values into constants

        int width = this.getWidth()/5*3;
        int height = this.getHeight()/2;

        popup = new Popup(this.context, this, width, height);
        popupParams = new RelativeLayout.LayoutParams(width, height);

        popupOpenPosition = (this.getHeight()-height)/2;
        popupClosedPosition = (this.getHeight()-height)/2 + this.getHeight();

        popupParams.leftMargin = (this.getWidth()-width)/2;
        popupParams.topMargin = popupClosedPosition;

        overlay = new View(context);
        overlay.setBackgroundColor(Color.parseColor("#363636"));
        overlay.getBackground().setAlpha(0);

        this.addView(overlay, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        this.addView(popup, popupParams);
    }

    public void rewardEventClicked(RewardEvent e) {
        popup.setRewardEvent(e);
        blendInPopup(popup, popupParams);
        //store.activity.startPopupActivityForRewardEvent(e);
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

    public void closePopupButtonClicked() {
        blendOutPopup(popup, popupParams);
    }

}
