package precious_rule_system.rules.settings;

/**
 * Created by christopher on 11.08.16.
 */

public class RuleSettings {
    // settings related to the queue
    public static int maxConsumerCount = 1;
    public static int minConsumerCount = 1;
    public static int jobsPerConsumer = 1;
    public static int consumerKeepAliveSeconds = 120;

    // time interval between tries to synchronise database
    public static long timeIntervalBetweenSyncsInMs = 0;//86400000;
}
