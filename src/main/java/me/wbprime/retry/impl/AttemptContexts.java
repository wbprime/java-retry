package me.wbprime.retry.impl;

import me.wbprime.retry.api.AttemptContext;

import java.time.Duration;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
final class AttemptContexts {
    private AttemptContexts() {
        throw new AssertionError("Construction forbidden");
    }

    public static AttemptContext starting(final String name) {
        return new SimpleAttemptContext(name);
    }

    public static AttemptContext attemptAgain(final AttemptContext context, final Duration elapsed) {
        return new SimpleAttemptContext(
                context.name(),
                context.totalAttempts() + 1,
                elapsed.plus(context.totalElapsedTime())
        );
    }

    private static final class SimpleAttemptContext implements AttemptContext {
        private final String name;
        private final long totalCount;
        private final Duration totalDuration;

        public SimpleAttemptContext(final String str) {
            this(str, 1L, Duration.ofMillis(0L));
        }

        public SimpleAttemptContext(final String str, final long count, final Duration time) {
            this.name = str;
            this.totalCount = count;
            this.totalDuration = time;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public long totalAttempts() {
            return totalCount;
        }

        @Override
        public Duration totalElapsedTime() {
            return totalDuration;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("AttemptContext{");
            sb.append("name='").append(name).append('\'');
            sb.append(", totalCount=").append(totalCount);
            sb.append(", totalDuration=").append(totalDuration);
            sb.append('}');
            return sb.toString();
        }
    }
}
