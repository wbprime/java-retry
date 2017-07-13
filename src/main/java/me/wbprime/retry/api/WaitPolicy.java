package me.wbprime.retry.api;

import java.time.Duration;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
public interface WaitPolicy {
    void waiting(final Duration time) throws InterruptedException;
}
