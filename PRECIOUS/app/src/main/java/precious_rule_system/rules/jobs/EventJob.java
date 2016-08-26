package precious_rule_system.rules.jobs;

import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.CancelReason;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import precious_rule_system.rules.jobs.priorities.Priority;

import rules.entities.Rule;
import rules.managers.events.EventProcessor;
import rules.managers.processing.Processor;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

/**
 * Created by christopher on 10.08.16.
 */

public class EventJob extends Job {

    private final static String TAG = "Rules.Events";
    EventProcessor event;

    public EventJob(EventProcessor event) {
        super(new Params(Priority.EVENT).addTags("EVENT"));
        this.event = event;
    }

    @Override
    public void onAdded() {}

    @Override
    public void onRun() throws Throwable {
        Rule[] gatheredRules = event.process();
        Log.d(TAG, "Trigger "+ event.getKey() + " received, found " + gatheredRules.length + " rules to process");
        for (Rule r : gatheredRules) {
            Processor processor = new Processor(r, PRECIOUS_APP.getRuleSystem().getDatabase(), PRECIOUS_APP.getRuleSystem().getDataManager());
            ProcessingJob job = new ProcessingJob(processor);
            PRECIOUS_APP.getRuleSystem().addJob(job);
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
