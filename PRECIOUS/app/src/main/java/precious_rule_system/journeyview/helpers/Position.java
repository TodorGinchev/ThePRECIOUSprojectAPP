package precious_rule_system.journeyview.helpers;

/**
 * Created by christopher on 04.09.16.
 */

public class Position {

    public boolean relative;
    public float left;
    public float top;

    public Position(float left, float top, boolean relative) {
        this.top = top;
        this.left = left;
        this.relative = relative;
    }

}
