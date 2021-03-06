package precious_rule_system.rules.jobs;

import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import precious_rule_system.rules.jobs.priorities.Priority;

import rules.entities.actions.Action;
import rules.managers.processing.Processor;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

/**
 * Created by christopher on 10.08.16.
 */

public class ProcessingJob extends Job {

    private Processor rule;

    public ProcessingJob(Processor rule) {
        super(new Params(Priority.PROCESSING + rule.getPriority()).addTags("PROCESSING"));
        this.rule = rule;
    }

    @Override
    public void onAdded() {}

    @Override
    public void onRun() throws Throwable {
        Action[] actions = rule.process();
        for (Action a : actions) {
            ActionJob actionJob = new ActionJob(a);
            PRECIOUS_APP.getRuleSystem().addJob(actionJob);
        }
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
