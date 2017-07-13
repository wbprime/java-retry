package me.wbprime.retry.api;

import java.util.function.Predicate;

/**
 * Determine if an attempt could be considered as ACCEPT
 *
 * ACCEPT means the retry is successful, and no more attempts would be performed.
 *
 * An ACCEPT attempt may be returning successfully, returning null, returning false, throwing an
 * expected exception.
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
public interface AcceptablePolicy<T> extends Predicate<AttemptResult<T>> {
    @Override
    boolean test(final AttemptResult<T> attemptResult);
}
