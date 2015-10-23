package com.vmware.vchs.test.client.rest.metrics;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by georgeliu on 14/11/27.
 */
public class HttpRequestMetrics {

    private final AtomicLong activeConnections = new AtomicLong();
    private final AtomicLong scheduledConnections = new AtomicLong();
    private final DurationCounter successfulConnections = new DurationCounter();
    private final DurationCounter failedConnections = new DurationCounter();
    private final DurationCounter requests = new DurationCounter();
    private final DurationCounter tasks = new DurationCounter();

    public HttpRequestMetrics() {
    }

    public AtomicLong getActiveConnections() {
        return activeConnections;
    }

    public AtomicLong getScheduledConnections() {
        return scheduledConnections;
    }

    public DurationCounter getSuccessfulConnections() {
        return successfulConnections;
    }

    public DurationCounter getFailedConnections() {
        return failedConnections;
    }

    public DurationCounter getRequests() {
        return requests;
    }

    public DurationCounter getTasks() {
        return tasks;
    }

    public long getActiveConnectionCount() {
        return activeConnections.get();
    }

    public long getScheduledConnectionCount() {
        return scheduledConnections.get();
    }

    public long getSuccessfulConnectionCount() {
        return successfulConnections.count();
    }

    public long getSuccessfulConnectionAverageDuration() {
        return successfulConnections.averageDuration();
    }

    public long getFailedConnectionCount() {
        return failedConnections.count();
    }

    public long getFailedConnectionAverageDuration() {
        return failedConnections.averageDuration();
    }

    public long getRequestCount() {
        return requests.count();
    }

    public long getRequestAverageDuration() {
        return requests.averageDuration();
    }

    public long getTaskCount() {
        return tasks.count();
    }

    public long getTaskAverageDuration() {
        return tasks.averageDuration();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[activeConnections=").append(activeConnections)
                .append(", scheduledConnections=").append(scheduledConnections)
                .append(", successfulConnections=").append(successfulConnections)
                .append(", failedConnections=").append(failedConnections)
                .append(", requests=").append(requests)
                .append(", tasks=").append(tasks)
                .append("]");
        return builder.toString();
    }

    /**
     * A counter that can measure duration and number of events.
     */
    public static class DurationCounter {
        private final AtomicLong count = new AtomicLong(0);
        private final AtomicLong cumulativeDuration = new AtomicLong(0);

        public void increment(final long startTime) {
            count.incrementAndGet();
            cumulativeDuration.addAndGet(System.currentTimeMillis() - startTime);
        }

        public long count() {
            return count.get();
        }

        public long averageDuration() {
            final long counter = count.get();
            return counter > 0 ? cumulativeDuration.get() / counter : 0;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("[count=").append(count())
                    .append(", averageDuration=").append(averageDuration())
                    .append("]");
            return builder.toString();
        }
    }

}
