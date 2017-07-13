package me.wbprime.retry.api;

import java.util.function.Consumer;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
public interface AttemptListener<T> extends Consumer<AttemptResult<T>> {
    @Override
    void accept(final AttemptResult<T> result);
}
