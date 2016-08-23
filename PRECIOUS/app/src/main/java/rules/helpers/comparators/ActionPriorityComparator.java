package rules.helpers.comparators;

import java.util.Comparator;

import rules.entities.Rule;
import rules.entities.actions.Action;

/**
 * Created by christopher on 07.06.16.
 */

public class ActionPriorityComparator implements Comparator<Action> {

    @Override
    public int compare(Action x, Action y)
    {
        if (x.getPriority() < y.getPriority())
        {
            return -1;
        }
        if (x.getPriority() > y.getPriority())
        {
            return 1;
        }
        return 0;
    }
}