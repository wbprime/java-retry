package me.wbprime.retry.impl;

import me.wbprime.retry.exception.RetryFailedException;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
public class RETRYTest {
    @Test
    public void returning_allDefault() throws Exception {
        final AtomicInteger counter = new AtomicInteger(0);
        final RETRY.Retryer<Integer> retryer = RETRY.fromCallable(
                () -> counter.getAndIncrement()
        ).named("retry-test");

        final Integer result = retryer.retry();

        assertThat(result).isEqualTo(0);
        assertThat(counter).hasValue(1);
    }

    @Test
    public void returning_stopIfResult() throws Exception {
        final AtomicInteger counter = new AtomicInteger(0);
        final RETRY.Retryer<Integer> retryer = RETRY.fromCallable(
                () -> counter.getAndIncrement()
        ).named("retry-test").stopIfResult(4);

        final Integer result = retryer.retry();

        assertThat(result).isEqualTo(4);
        assertThat(counter).hasValue(5);
    }

    @Test
    public void returning_stopIfExceptionWithoutMapping() throws Exception {
        final AtomicInteger counter = new AtomicInteger(0);
        final RETRY.Retryer<Integer> retryer = RETRY.fromCallable(
                () -> {
                    final int val = counter.getAndIncrement();
                    if (4 == val) {
                        throw new IllegalArgumentException("Illegal " + val);
                    }
                    return val;
                }
        ).named("retry-test")
                .stopIfException(IllegalArgumentException.class);

        assertThatThrownBy(() -> retryer.retry()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void returning_stopIfExceptionWithMapping() throws Exception {
        final AtomicInteger counter = new AtomicInteger(0);
        final RETRY.Retryer<Integer> retryer = RETRY.fromCallable(
                () -> {
                    final int val = counter.getAndIncrement();
                    if (4 == val) {
                        throw new IllegalArgumentException("Illegal " + val);
                    }
                    return val;
                }
        ).named("retry-test")
                .stopIfException(IllegalArgumentException.class)
                .returningOrDefault(10000);

        final Integer result = retryer.retry();

        assertThat(result).isEqualTo(10000);
    }

    @Test
    public void throwing() throws Exception {
        final AtomicInteger counter = new AtomicInteger(0);
        final RETRY.Retryer<Integer> retryer = RETRY.fromCallable(
                (Callable<Integer>) () -> {
                    throw new IllegalArgumentException("Illegal " + counter.getAndIncrement());
                }
        ).named("retry-test")
                .abortAfterMaxRetries(5L);

        assertThatThrownBy(() -> retryer.retry())
                .isInstanceOf(RetryFailedException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class);
        assertThat(counter).hasValue(5);
    }
}