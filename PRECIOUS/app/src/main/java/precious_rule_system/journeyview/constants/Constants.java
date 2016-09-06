package precious_rule_system.journeyview.constants;

import android.graphics.Color;

/**
 * Created by christopher on 04.09.16.
 */

public class Constants {

    // a constant that specifies the aspect ratio in which assets were created
    public static final double constantWidthToHeightRatio = 0.5622188906;

    // positions
    public static final double pathOffsetPosition = 0.5;

    // sizes
    public static final float pathWidth = 0.03f;
    public static final float pathWidthMaxDp = 10;
    public static final float rewardEventSize = 0.05f;
    public static final float rewardEventMaxDp = 16;
    public static final float rewardMilestoneSize = 0.08f;
    public static final float rewardMilestoneMaxDp = 25;
    public static final float playerStrokeWidth = 5.0f;

    public static final float playerIndicatorCircleWidth = 0.086f;
    public static final float playerIndicatorCircleWithMaxDp = 30;

    // colors
    public static final int pathColor = Color.rgb(226, 222, 219);
    public static final int rewardEventColor = Color.rgb(249, 190, 112);
    public static final int rewardMilestoneColor = Color.rgb(54, 54, 54);
    public static final int rewardMilestoneStarColor = Color.parseColor("#ccc7c5");
    public static final int playerCircleColor = Color.rgb(249, 190, 112);
    public static final float playerIndicatorTrianglePadding = 20.0f;
    public static final float playerIndicatorCorners = 8.0f;
    public static final float playerIndicatorTextPadding = 5.0f;
    public static final int playerIndicatorBackgroundColor = Color.rgb(54, 54, 54);
    public static final int playerIndicatorTextColor = Color.parseColor("#f6f6f6");

    // page calculation
    public static final int pointsPerPage = 1000;

    // others
    public static final int colorAnimationDuration = 500;

    /**
     * Landscape enum
     */
    public enum Landscape {

        OCEAN("ocean", "The Ocean", Color.parseColor("#454c5e"), true),
        ISLAND("island", "The Island", Color.parseColor("#d6d6b2"), true),
        DESERT("desert", "The Desert", Color.parseColor("#e2e259"), true),
        MOUNTAIN("mountain", "The Mountains", Color.parseColor("#eeeae9"), false),
        TREES("trees", "The Forest", Color.parseColor("#4b6852"), true),
        SKY("sky", "The Sky", Color.parseColor("#5fb7f9"), true),
        SPACE("space", "Space", Color.parseColor("#191938"), true);


        private final String text;
        private final String niceText;
        private final int color;
        private final boolean isFlipable;

        private Landscape(final String text, final String niceText, int color, boolean flippable) {
            this.text = text; this.niceText = niceText; this.color = color; this.isFlipable = flippable;
        }
        public String toString() { return text; }
        public String getNiceText() { return niceText; }
        public int getColor() { return color; }
        public boolean isFlipable() { return isFlipable; }
    }

    /**
     * Size enum
     */
    public enum Size {

        SMALL("small"),
        MEDIUM("medium"),
        LARGE("large");

        private final String text;
        private Size(final String text) { this.text = text; }
        public String toString() { return text; }
    }


    /**
     * Function that returns the landscape for a specific position or level
     * @param pos
     * @return
     */
    public static Landscape getLandscape(int pos) {
        if (pos <= 0) {
            return Landscape.OCEAN;
        } else if (pos == 1) {
            return Landscape.ISLAND;
        } else if (pos == 2) {
            return Landscape.DESERT;
        } else if (pos == 3) {
            return Landscape.TREES;
        } else if (pos == 4){
            return Landscape.MOUNTAIN;
        } else if (pos == 5) {
            return Landscape.SKY;
        } else {
            return Landscape.SPACE;
        }
    }



}
