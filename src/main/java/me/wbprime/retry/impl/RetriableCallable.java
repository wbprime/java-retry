package me.wbprime.retry.impl;

import com.google.common.base.Stopwatch;

import me.wbprime.retry.api.AbortablePolicy;
import me.wbprime.retry.api.AcceptPolicy;
import me.wbprime.retry.api.AcceptablePolicy;
import me.wbprime.retry.api.AttemptContext;
import me.wbprime.retry.api.AttemptListener;
import me.wbprime.retry.api.AttemptPolicy;
import me.wbprime.retry.api.AttemptResult;
import me.wbprime.retry.api.WaitPolicy;
import me.wbprime.retry.api.WaitTimeLimitPolicy;
import me.wbprime.retry.exception.RetryFailedException;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
class RetriableCallable<T> implements Callable<T> {
    private final String name;
    private final Callable<T> callable;

    private final AttemptPolicy<T> attemptPolicy ;

    private final AcceptablePolicy<T> acceptablePolicy ;
    private final AcceptPolicy<T> acceptPolicy ;

    private final AbortablePolicy<T> abortablePolicy;

    private final WaitTimeLimitPolicy<T> waitTimeLimitPolicy ;
    private final WaitPolicy waitPolicy ;

    private Iterable<AttemptListener<T>> listeners = Collections.emptyList();

    public RetriableCallable(final Callable<T> callable, final RetryConfig<T> config) {
        this.callable = checkNotNull(callable, "callable");
        checkNotNull(config, "retryConfig");

        this.name = checkNotNull(config.getName(), "name");

        this.attemptPolicy = checkNotNull(config.getAttemptPolicy(), "attemptPolicy");

        this.acceptablePolicy = checkNotNull(config.getAcceptablePolicy(), "acceptablePolicy");
        this.acceptPolicy = checkNotNull(config.getAcceptPolicy(), "acceptPolicy");

        this.abortablePolicy = checkNotNull(config.getAbortablePolicy(), "abortablePolicy");

        this.waitTimeLimitPolicy =
                checkNotNull(config.getWaitTimeLimitPolicy(), "waitTimeLimitPolicy");
        this.waitPolicy = checkNotNull(config.getWaitPolicy(), "waitPolicy");

        this.listeners = checkNotNull(config.getAttemptListeners(), "attemptListens");
    }

    // May return null
    public T call() throws InterruptedException, RetryFailedException {
        AttemptContext context = AttemptContexts.starting(name);

        while (true) {
            AttemptResult<T> attemptResult = null;

            final Stopwatch stopwatch = Stopwatch.createStarted();
            try {
                final T val = attemptPolicy.attempt(callable, context);

                attemptResult = AttemptResults.returned(val, context, stopwatch.elapsed());
            } catch (Throwable e) {
                attemptResult = AttemptResults.thrown(e, context, stopwatch.elapsed());
            }

            if (Thread.interrupted()) {
                throw RetryFailedException.create(attemptResult);
            }

            if (acceptablePolicy.test(attemptResult)) {
                // This attempt is ACCEPTED
                return acceptPolicy.accept(attemptResult);
            } else {
                // Listeners
                for (final AttemptListener<T> listener: listeners) {
                    listener.accept(attemptResult);
                }
            }

            // This attempt is REJECTED
            // A REJECTED attempt can be aborted or retried
            if (abortablePolicy.test(attemptResult)) {
                throw RetryFailedException.create(attemptResult);
            } else {
                final Duration waitDuration = waitTimeLimitPolicy.limitTime(attemptResult);
                try {
                    waitPolicy.waiting(waitDuration);
                } catch (InterruptedException e) {
                    throw RetryFailedException.create(attemptResult);
                }
            }

            context = AttemptContexts.attemptAgain(context, stopwatch.elapsed());

            if (Thread.interrupted()) {
                throw RetryFailedException.create(attemptResult);
            }
        }
    }
}
