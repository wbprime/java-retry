package me.wbprime.retry.impl;

import me.wbprime.retry.api.AttemptResult;
import me.wbprime.retry.api.WaitTimeLimitPolicy;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
final class WaitTimeLimitPolicies {
    private WaitTimeLimitPolicies() {
        throw new AssertionError("Construction forbidden");
    }

    public static <T> WaitTimeLimitPolicy<T> fixedWaitTime(final long millis) {
        return new FixedTimeLimit<>(Duration.ofMillis(millis));
    }

    public static <T> WaitTimeLimitPolicy<T> fixedWaitTime(final long time, final TimeUnit unit) {
        return new FixedTimeLimit<>(Duration.ofMillis(unit.toMillis(time)));
    }

    public static <T> WaitTimeLimitPolicy<T> fixedWaitTime(final Duration time) {
        return new FixedTimeLimit<>(time);
    }

    private static final class FixedTimeLimit<T> implements WaitTimeLimitPolicy<T> {
        private final Duration duration;

        FixedTimeLimit(final Duration time) {
            this.duration = time;
        }

        @Override
        public Duration limitTime(final AttemptResult<T> c) {
            return duration;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("FixedTimeLimit{");
            sb.append("duration=").append(duration);
            sb.append('}');
            return sb.toString();
        }
    }
}
