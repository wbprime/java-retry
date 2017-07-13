package me.wbprime.retry.exception;

import me.wbprime.retry.api.AttemptResult;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
public class RetryFailedException extends RuntimeException {
    private RetryFailedException(final String msg) {
        super("Retry failed as " + msg);
    }

    private RetryFailedException(final String msg, final String result) {
        super("Retry failed as " + msg + " with result = " + result.toString());
    }

    private RetryFailedException(final String msg, final Throwable ex) {
        super("Retry failed as " + msg + " with exception", ex);
    }

    public static <T> RetryFailedException create(final AttemptResult<T> result) {
        if (result.hasException()) {
            return new RetryFailedException(result.toString(), result.getException());
        }

        if (result.hasResult()) {
            return new RetryFailedException(result.toString(), result.getResult().toString());
        }

        return new RetryFailedException(result.toString());
    }
}
