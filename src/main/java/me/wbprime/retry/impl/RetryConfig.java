package me.wbprime.retry.impl;

import me.wbprime.retry.api.AbortablePolicy;
import me.wbprime.retry.api.AcceptPolicy;
import me.wbprime.retry.api.AcceptablePolicy;
import me.wbprime.retry.api.AttemptListener;
import me.wbprime.retry.api.AttemptPolicy;
import me.wbprime.retry.api.WaitPolicy;
import me.wbprime.retry.api.WaitTimeLimitPolicy;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
class RetryConfig<T> {
    private String name;

    private AttemptPolicy<T> attemptPolicy;

    private AcceptablePolicy<T> acceptablePolicy;
    private AcceptPolicy<T> acceptPolicy;

    private AbortablePolicy<T> abortablePolicy;

    private WaitPolicy waitPolicy;
    private WaitTimeLimitPolicy<T> waitTimeLimitPolicy;

    private Iterable<AttemptListener<T>> attemptListeners;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public AttemptPolicy<T> getAttemptPolicy() {
        return attemptPolicy;
    }

    public void setAttemptPolicy(final AttemptPolicy<T> attemptPolicy) {
        this.attemptPolicy = attemptPolicy;
    }

    public AcceptablePolicy<T> getAcceptablePolicy() {
        return acceptablePolicy;
    }

    public void setAcceptablePolicy(final AcceptablePolicy<T> acceptablePolicy) {
        this.acceptablePolicy = acceptablePolicy;
    }

    public AcceptPolicy<T> getAcceptPolicy() {
        return acceptPolicy;
    }

    public void setAcceptPolicy(final AcceptPolicy<T> acceptPolicy) {
        this.acceptPolicy = acceptPolicy;
    }

    public AbortablePolicy<T> getAbortablePolicy() {
        return abortablePolicy;
    }

    public void setAbortablePolicy(final AbortablePolicy<T> abortablePolicy) {
        this.abortablePolicy = abortablePolicy;
    }

    public WaitPolicy getWaitPolicy() {
        return waitPolicy;
    }

    public void setWaitPolicy(final WaitPolicy waitPolicy) {
        this.waitPolicy = waitPolicy;
    }

    public WaitTimeLimitPolicy<T> getWaitTimeLimitPolicy() {
        return waitTimeLimitPolicy;
    }

    public void setWaitTimeLimitPolicy(final WaitTimeLimitPolicy<T> waitTimeLimitPolicy) {
        this.waitTimeLimitPolicy = waitTimeLimitPolicy;
    }

    public Iterable<AttemptListener<T>> getAttemptListeners() {
        return attemptListeners;
    }

    public void setAttemptListeners(final Iterable<AttemptListener<T>> attemptListeners) {
        this.attemptListeners = attemptListeners;
    }
}
