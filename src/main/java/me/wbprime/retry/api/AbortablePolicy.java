package me.wbprime.retry.api;

import java.util.function.Predicate;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
public interface AbortablePolicy<T> extends Predicate<AttemptResult<T>> {
    /**
     * Test if retry could be continued or not
     *
     * @param c failed reason context
     * @return true if retry should continue, otherwise false
     */
    @Override
    boolean test(final AttemptResult<T> c);
}
