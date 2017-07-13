package me.wbprime.retry.impl;

import com.google.common.collect.ImmutableList;

import me.wbprime.retry.api.AbortablePolicy;
import me.wbprime.retry.api.AcceptPolicy;
import me.wbprime.retry.api.AcceptablePolicy;
import me.wbprime.retry.api.AttemptListener;
import me.wbprime.retry.api.AttemptPolicy;
import me.wbprime.retry.api.AttemptResult;
import me.wbprime.retry.api.WaitTimeLimitPolicy;
import me.wbprime.retry.exception.RetryFailedException;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static me.wbprime.retry.impl.WaitTimeLimitPolicies.fixedWaitTime;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
public final class RETRY {
    public static class Retryer<T> {
        private final Callable<T> callable;

        private String name;

        private ImmutableList.Builder<AttemptListener<T>> attemptListeners =
                ImmutableList.builder();
        private ImmutableList.Builder<Predicate<AttemptResult<T>>> acceptables =
                ImmutableList.builder();
        private ImmutableList.Builder<Predicate<AttemptResult<T>>> stoppables =
                ImmutableList.builder();

        /* Nullable */
        private WaitTimeLimitPolicy<T> waitTimeLimitPolicy;
        /* Nullable */
        private AttemptPolicy<T> attemptPolicy;
        /* Nullable */
        private AcceptPolicy<T> acceptPolicy;

        public Retryer(/* May be null */final Callable<T> callable) {
            this.callable = checkNotNull(callable);
        }

        public Retryer<T> named(final String str) {
            this.name = checkNotNull(str);
            return this;
        }

        /**
         * Handler executed each time try failed
         *
         * @return this
         */
        public Retryer<T> onRetry(final AttemptListener<T> listener) {
            attemptListeners.add(checkNotNull(listener));
            return this;
        }

        /**
         * STOP means the retrying would exit and return a value as if no exception happened, even through
         * an exception is thrown during the execution.
         *
         * If STOP is wanted for an exception thrown during the execution,
         * {@link Retryer#returningMapping} must be called to translate the exception to a value.
         *
         * @param acceptablePolicy
         * @return
         */
        public Retryer<T> stopIf(final AcceptablePolicy<T> acceptablePolicy) {
            acceptables.add(checkNotNull(acceptablePolicy));
            return this;
        }

        public Retryer<T> stopIfResult() {
            acceptables.add(AcceptablePolicies.onlyIfReturning());
            return this;
        }

        public Retryer<T> stopIfResult(/* Nullable */ final T result) {
            acceptables.add(AcceptablePolicies.hasResult(result));
            return this;
        }

        public Retryer<T> stopIfException(final Class<? extends Exception> ex) {
            acceptables.add(AcceptablePolicies.hasException(ex));
            return this;
        }

        /**
         * To perform exception to value translating when specifying a {@link AcceptPolicy}
         *
         * If an exception is accepted, the mapped value in {@code map} will be returned.
         * If no value is found mapped, then the exception is thrown again.
         *
         * @param map
         * @return
         */
        public Retryer<T> returningMapping(final Map<Throwable, T> map) {
            acceptPolicy = AcceptPolicies.fromMap(checkNotNull(map));
            return this;
        }

        /**
         * To perform exception to value translating when specifying a {@link AcceptPolicy}
         *
         * @param defaultValue nullable
         * @return
         */
        public Retryer<T> returningOrDefault(final T defaultValue) {
            acceptPolicy = AcceptPolicies.returnResultOrDefault(defaultValue);
            return this;
        }

        /**
         * ABORT means the retrying would terminate and thrown a
         * {@link RetryFailedException} as if a exception thrown,
         * even through no exception is thrown during the execution.
         *
         * @param n max retry count
         * @return this
         */
        public Retryer<T> abortAfterMaxRetries(final long n) {
            stoppables.add(AbortablePolicies.maxTimes(n));
            return this;
        }

        public Retryer<T> abortIf(final AbortablePolicy<T> abortablePolicy) {
            stoppables.add(checkNotNull(abortablePolicy));
            return this;
        }

        public Retryer<T> abortIfResult(/* Nullable */ final T result) {
            stoppables.add(AbortablePolicies.hasResult(result));
            return this;
        }

        public Retryer<T> abortIfException(final Class<? extends Exception> ex) {
            stoppables.add(AbortablePolicies.hasException(ex));
            return this;
        }

        public Retryer<T> waitAfterEachRetry(final long time, final TimeUnit unit) {
            waitTimeLimitPolicy = fixedWaitTime(time, unit);
            return this;
        }

        public Retryer<T> waitAfterEachRetry(final Duration time) {
            waitTimeLimitPolicy = fixedWaitTime(time);
            return this;
        }

        public Retryer<T> waitAfterEachRetry(final WaitTimeLimitPolicy<T> policy) {
            waitTimeLimitPolicy = checkNotNull(policy);
            return this;
        }

        public Retryer<T> rertyPolicy(final AttemptPolicy<T> policy) {
            attemptPolicy = checkNotNull(policy);
            return this;
        }

        public Retryer<T> rertyPolicy(final ExecutorService executorService) {
            attemptPolicy = AttemptPolicies.unlimitTimeUninterrupedly(
                    checkNotNull(executorService)
            );
            return this;
        }

        public Retryer<T> rertyPolicy(
                final Duration limitTime, final ExecutorService executorService
        ) {
            attemptPolicy = AttemptPolicies.fixedLimitTimeUninterrupedly(
                    checkNotNull(limitTime), checkNotNull(executorService)
            );
            return this;
        }

        /**
         * @throws InterruptedException if interrupted
         * @throws IllegalStateException if no result is returned
         * @throws RetryFailedException if retry abort
         */
        public T retry() throws InterruptedException, IllegalStateException, RetryFailedException {
            final RetryConfig<T> config = new RetryConfig<>();
            config.setName((null != name) ? name : "");
            config.setAttemptListeners(attemptListeners.build());
            config.setAcceptablePolicy(AcceptablePolicies.or(acceptables.build()));
            config.setAbortablePolicy(AbortablePolicies.or(stoppables.build()));
            config.setWaitTimeLimitPolicy(null != waitTimeLimitPolicy ? waitTimeLimitPolicy :
                    fixedWaitTime(0L));
            config.setAttemptPolicy(
                    (null != attemptPolicy) ?
                            attemptPolicy
                            : AttemptPolicies.unlimitTimeUninterrupedly()
            );
            config.setWaitPolicy(WaitPolicies.waitInterruptedly());
            config.setAcceptPolicy(null != acceptPolicy ?
                    acceptPolicy : AcceptPolicies.returnResult());

            final RetriableCallable<T> operation = new RetriableCallable<>(
                    callable, config
            );
            return operation.call();
        }
    }

    public static Retryer<Object> fromRunnable(final Runnable runnable) {
        return new Retryer<>(Executors.callable(runnable));
    }

    public static <T> Retryer<T> fromCallable(final Callable<T> callable) {
        return new Retryer<>(callable);
    }
}
