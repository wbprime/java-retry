package me.wbprime.retry.api;

import java.time.Duration;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
public interface WaitTimeLimitPolicy<T> {
    Duration limitTime(final AttemptResult<T> c);
}
