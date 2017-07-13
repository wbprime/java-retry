package me.wbprime.retry.impl;

import me.wbprime.retry.api.AcceptPolicy;
import me.wbprime.retry.api.AttemptResult;
import me.wbprime.retry.exception.RetryFailedException;

import java.util.Map;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
final class AcceptPolicies {
    private AcceptPolicies() {
        throw new AssertionError("Construction forbidden");
    }

    public static <T> AcceptPolicy<T> returnResult() {
        return new ReturningResult<>();
    }

    public static <T> AcceptPolicy<T> returnResultOrDefault(final T val) {
        return new ReturningDefaultResult<>(val);
    }

    public static <T> AcceptPolicy<T> fromMap(final Map<Throwable, T> map) {
        return new MapBasedResult<>(map);
    }

    private static final class ReturningResult<T> implements AcceptPolicy<T> {
        @Override
        public T accept(final AttemptResult<T> attemptResult) {
            return attemptResult.getResult();
        }

        @Override
        public String toString() {
            return "ReturningResult{}";
        }
    }

    private static final class ReturningDefaultResult<T> implements AcceptPolicy<T> {
        private final T defaultValue;

        ReturningDefaultResult(final T defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public T accept(final AttemptResult<T> attemptResult) {
            if (attemptResult.hasResult())
            return attemptResult.getResult();
            else
                return defaultValue;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ReturningDefaultResult{");
            sb.append("defaultValue=").append(defaultValue);
            sb.append('}');
            return sb.toString();
        }
    }

    private static final class MapBasedResult<T> implements AcceptPolicy<T> {
        private final Map<Throwable, T> map;

        MapBasedResult(final Map<Throwable, T> map) {
            this.map = map;
        }

        @Override
        public T accept(final AttemptResult<T> attemptResult) {
            if (attemptResult.hasException()) {
                final Throwable ex = attemptResult.getException();
                if (map.containsKey(ex)) {
                    return map.get(ex);
                } else {
                    throw RetryFailedException.create(attemptResult);
                }
            }

            return attemptResult.getResult();
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("MapBasedResult{");
            sb.append("map=").append(map);
            sb.append('}');
            return sb.toString();
        }
    }
}
