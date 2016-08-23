package precious_rule_system.rules.jobs;

import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.precious.christopher.precious_rule_system.RulesApplication;
import com.precious.christopher.precious_rule_system.rules.jobs.priorities.Priority;

import rules.entities.actions.Action;

/**
 * Created by christopher on 10.08.16.
 */

public class ActionJob extends Job {

    private Action action;

    public ActionJob(Action action) {
        super(new Params(Priority.ACTION + action.getPriority()).addTags("ACTION"));
        this.action = action;
    }

    @Override
    public void onAdded() {}

    @Override
    public void onRun() throws Throwable {
        RulesApplication.getRuleSystem().getActionManager().handleAction(this.action);
    }

    @Override
    protected void onCancel(@CancelReason int cancelReason, @Nullable Throwable throwable) {}

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount,
                                                     int maxRunCount) {
        if (runCount > 2) {
            return RetryConstraint.CANCEL;
        } else {
            return RetryConstraint.RETRY;
        }
    }
}
