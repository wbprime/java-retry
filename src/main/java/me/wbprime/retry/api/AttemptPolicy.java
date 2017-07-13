package me.wbprime.retry.api;

import java.util.concurrent.Callable;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
public interface AttemptPolicy<T> {
    T attempt(
            final Callable<T> callable, final AttemptContext context
    ) throws Exception;
}
