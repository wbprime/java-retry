package me.wbprime.retry.api;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
public interface AcceptPolicy<T> {
    T accept(final AttemptResult<T> attemptResult);
}
