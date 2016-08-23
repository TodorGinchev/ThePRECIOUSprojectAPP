package rules.managers.action;

import java.util.Date;

import rules.data.Data;
import rules.entities.actions.Action;
import rules.types.RuleTypes;

/**
 * Created by christopher on 11.08.16.
 */

public interface ActionManagerInterface {
    public void handleAction(Action action) throws Exception;
}
