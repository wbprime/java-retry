package me.wbprime.retry.impl;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.Uninterruptibles;

import me.wbprime.retry.api.AttemptContext;
import me.wbprime.retry.api.AttemptPolicy;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
final class AttemptPolicies {
    private AttemptPolicies() {
        throw new AssertionError("Construction forbidden");
    }

    public static <T> AttemptPolicy<T> unlimitTimeUninterrupedly() {
        return new UninterruptedNoTimeLimitDirectAttempt<>();
    }

    public static <T> AttemptPolicy<T> unlimitTimeUninterrupedly(
            final ExecutorService executorService
    ) {
        return new UninterruptedNoTimeLimitExecutorAttempt<>(executorService);
    }

    public static <T> AttemptPolicy<T> fixedLimitTimeUninterrupedly(
            final Duration limitTime,
            final ExecutorService executorService
            ) {
        return new UninterruptedTimeLimitedCallableAttempt<>(executorService, limitTime);
    }

    private static final class UninterruptedTimeLimitedCallableAttempt<T> implements AttemptPolicy<T> {
        private final TimeLimiter timeLimiter;

        private final Duration time;

        UninterruptedTimeLimitedCallableAttempt(
                final ExecutorService executor, final Duration time
        ) {
            this.timeLimiter = new SimpleTimeLimiter(executor);
            this.time = time;
        }

        @Override
        public T attempt(
                final Callable<T> callable, final AttemptContext context
        ) throws Exception {
            return timeLimiter.callWithTimeout(
                    callable, time.toMillis(), TimeUnit.MILLISECONDS, false
            );
        }

        @Override
        public String toString() {
            return "UninterruptedTimeLimitedCallableAttempt{}";
        }
    }

    private static final class UninterruptedNoTimeLimitExecutorAttempt<T> implements AttemptPolicy<T> {
        private final ExecutorService executorService;

        UninterruptedNoTimeLimitExecutorAttempt(final ExecutorService executor) {
            executorService = executor;
        }

        @Override
        public T attempt(
                final Callable<T> callable, final AttemptContext context
        ) throws Exception {
            final Future<T> future = executorService.submit(callable);
            return Uninterruptibles.getUninterruptibly(future);
        }

        @Override
        public String toString() {
            return "UninterruptedNoTimeLimitExecutorAttempt{}";
        }
    }

    private static final class UninterruptedNoTimeLimitDirectAttempt<T> implements AttemptPolicy<T> {
        @Override
        public T attempt(
                final Callable<T> callable, final AttemptContext context
        ) throws Exception {
            return callable.call();
        }

        @Override
        public String toString() {
            return "UninterruptedNoTimeLimitDirectAttempt{}";
        }
    }
}
