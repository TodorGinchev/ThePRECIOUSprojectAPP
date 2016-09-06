package precious_rule_system.journeyview.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.util.DisplayMetrics;

import precious_rule_system.journeyview.constants.Constants;
import precious_rule_system.rewardsystem.entities.RewardEvent;

/**
 * Created by christopher on 04.09.16.
 */

public class SizeCalculator {

    Context context;
    private float dpToPx = 0.0f;

    public SizeCalculator(Context context) {
        this.context = context;
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        dpToPx = ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public RectF calculateAssetSize(double unitWidth, double unitHeight, double unitWidthToHeightRatio, int viewWidth, int viewHeight) {

        float desiredWidth;
        float desiredHeight;

        float ratio = (float)(unitWidth/unitHeight) * (float) unitWidthToHeightRatio;
        desiredHeight = (float) viewHeight * (float) unitHeight;
        desiredWidth = desiredHeight * ratio;

        return new RectF(0,0,desiredWidth,desiredHeight);

    }

    public RectF calculatePathSize(double unitWidth, double unitHeight, double unitWidthToHeightRatio, int viewWidth, int viewHeight) {

        float desiredWidth;
        float desiredHeight;

        // the path size will always be dependent on the height, as we need it to be fully filling the height
        desiredHeight = (float) (unitHeight * (double) viewHeight);
        desiredWidth = desiredHeight * (float) unitWidthToHeightRatio * (float) unitWidth;

        return new RectF(0,0,desiredWidth,desiredHeight);

    }

    public float getPathThickness(int width, int height) {
        return Math.min(Constants.pathWidth * width, Constants.pathWidthMaxDp * dpToPx);
    }

    public float getRewardSize(int width, int height, RewardEvent e) {
        if (e.isEvent()) {
            return Math.min(Constants.rewardEventSize * width, Constants.rewardEventMaxDp * dpToPx);
        } else if (e.isMilestone()) {
            return Math.min(Constants.rewardMilestoneSize * width, Constants.rewardMilestoneMaxDp * dpToPx);
        } else {
            return 0.0f;
        }
    }

    public int[] getPlayerIndicatorSizes(int width) {
        int circleSize = (int) Math.round(Constants.playerIndicatorCircleWidth*width);
        circleSize = Math.min(circleSize, Math.round(Constants.playerIndicatorCircleWithMaxDp * dpToPx));
        int indicatorWidth = 2*circleSize;
        int indicatorHeight = (int) Math.round((float) circleSize / 1.5f);
        int[] res = {circleSize, indicatorWidth, indicatorHeight};
        return res;
    }


}
