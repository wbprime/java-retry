package me.wbprime.retry.impl;

import com.google.common.util.concurrent.Uninterruptibles;

import me.wbprime.retry.api.WaitPolicy;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
final class WaitPolicies {
    private WaitPolicies() {
        throw new AssertionError("Construction forbidden");
    }

    public static WaitPolicy waitInterruptedly() {
        return new InterruptedlySleep();
    }

    public static WaitPolicy waitUninterruptedly() {
        return new InterruptedlySleep();
    }

    private static final class InterruptedlySleep implements WaitPolicy {
        @Override
        public void waiting(final Duration time) throws InterruptedException {
            Thread.sleep(time.toMillis());
        }

        @Override
        public String toString() {
            return "InterruptedlySleep{}";
        }
    }

    private static final class UninterruptedlySleep implements WaitPolicy {
        @Override
        public void waiting(final Duration time) throws InterruptedException {
            Uninterruptibles.sleepUninterruptibly(time.toMillis(), TimeUnit.MILLISECONDS);
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("Interrupted in UninterruptedlySleep");
            }
        }

        @Override
        public String toString() {
            return "UninterruptedlySleep{}";
        }
    }
}
