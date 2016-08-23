package rules.helpers.comparators;

import java.util.Comparator;
import rules.entities.Rule;

/**
 * Created by christopher on 07.06.16.
 */
public class RulePriorityComparator implements Comparator<Rule> {

    @Override
    public int compare(Rule x, Rule y)
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