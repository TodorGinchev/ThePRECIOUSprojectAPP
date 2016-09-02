package precious_rule_system.journeyview_new;

import android.content.Context;
import android.graphics.Color;

import precious_rule_system.journeyview_new.DataManager;
import precious_rule_system.rewardsystem.entities.RewardEvent;

/**
 * Created by christopher on 02.09.16.
 */

public class DataUIManager {

    DataManager data;
    Context context;

    /**
     * Keys for storing data in user preferences
     */
    private final String lastStoredDateKey = "journeyView.lastStoredDate";

    /**
     * Sizes, colors, etc. for rendering
     */
    private static final float rewardEventSize = 0.05f;
    private static final float rewardMilestoneSize = 0.08f;
    private static final float rewardPointIncreaseSize = 0.03f;
    public static final float pathThinkness = 0.03f;
    public static final int gridBackgroundColor = Color.parseColor("#eeeae9");

    /**
     * Constants w.r.t how points are rendered
     */
    private static final float pointsPerPage = 150f;


    public DataUIManager(Context context, DataManager data, int width, int height) {
        this.data = data;
        this.context = context;
    }







    /**
     * Converts a number of points to a relative position on a page
     * @param points (not accumulated, but individually)
     * @return
     */
    private static float convertPointsToPagePosition(float points) {
        return points/pointsPerPage;
    }

    /**
     * Convenience methods return the size of reward items
     */
    public static final float getRewardSize(RewardEvent e, int width, int height) {
        if (e.isEvent()) return getRewardEventSize(width, height);
        if (e.isMilestone()) return getRewardMilestoneSize(width, height);
        if (e.isPointIncrease()) return getRewardPointIncreaseSize(width, height);
        return 0.0f;
    }

    public static final float getRewardEventSize(int width, int height) {
        return (float) width * rewardEventSize;
    }

    public static final float getRewardMilestoneSize(int width, int height) {
        return (float) width * rewardMilestoneSize;
    }

    public static final float getRewardPointIncreaseSize(int width, int height) {
        return (float) width * rewardPointIncreaseSize;
    }

}
