package me.wbprime.retry.impl;

import com.google.common.collect.ImmutableList;

import me.wbprime.retry.api.AbortablePolicy;
import me.wbprime.retry.api.AttemptResult;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
final class AbortablePolicies {
    private AbortablePolicies() {
        throw new AssertionError("Construction forbidden");
    }

    public static <T> AbortablePolicy<T> maxTimes(final long n) {
        return new MaxTimesAbort<>(n);
    }

    public static <T> AbortablePolicy<T> or(
            final Collection<Predicate<AttemptResult<T>>> predicates
    ) {
        return predicates.isEmpty() ? neverAbort() :
                new PredicatesBasedAbortablePolicy<>(predicates);
    }

    public static <T> AbortablePolicy<T> neverAbort() {
        return new NeverAbort<>();
    }

    public static <T> AbortablePolicy<T> hasException(final Class<? extends Exception> excepted) {
        return new HasExceptions<>(ImmutableList.of(excepted));
    }

    public static <T> AbortablePolicy<T> hasResult(final T excepted) {
        return new HasResults<>(ImmutableList.of(excepted));
    }

    private static final class HasResults<T> implements AbortablePolicy<T> {
        private final Iterable<T> expected;

        public HasResults(final Iterable<T> expected) {
            this.expected = expected;
        }

        @Override
        public boolean test(final AttemptResult<T> attemptResult) {
            if (attemptResult.hasResult()) {
                for (final T val : expected) {
                    if (Objects.equals(attemptResult.getResult(), val)) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("HasResults{");
            sb.append("expected=").append(expected);
            sb.append('}');
            return sb.toString();
        }
    }

    private static final class HasExceptions<T> implements AbortablePolicy<T> {
        private final Iterable<Class<? extends Exception>> expected;

        public HasExceptions(final Iterable<Class<? extends Exception>> expected) {
            this.expected = expected;
        }

        @Override
        public boolean test(final AttemptResult<T> attemptResult) {
            if (attemptResult.hasException()) {
                for (final Class<? extends Exception> ex : expected) {
                    if (ex.isInstance(attemptResult.getException())) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("HasExceptions{");
            sb.append("expected=").append(expected);
            sb.append('}');
            return sb.toString();
        }
    }

    private static final class MaxTimesAbort<T> implements AbortablePolicy<T> {
        private final long nn;

        public MaxTimesAbort(final long nn) {
            this.nn = nn;
        }

        @Override
        public boolean test(final AttemptResult<T> c) {
            return c.totalAttempts() >= nn;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("MaxTimesAbort{");
            sb.append("nn=").append(nn);
            sb.append('}');
            return sb.toString();
        }
    }

    private static final class NeverAbort<T> implements AbortablePolicy<T> {
        @Override
        public boolean test(final AttemptResult<T> c) {
            return false;
        }

        @Override
        public String toString() {
            return "NeverAbort{}";
        }
    }

    private static final class PredicatesBasedAbortablePolicy<T> implements AbortablePolicy<T> {
        private final Iterable<Predicate<AttemptResult<T>>> predicates;

        PredicatesBasedAbortablePolicy(
                final Iterable<Predicate<AttemptResult<T>>> predicates
        ) {
            this.predicates = predicates;
        }

        @Override
        public boolean test(final AttemptResult<T> attemptResult) {
            for (final Predicate<AttemptResult<T>> predicate : predicates) {
                if (predicate.test(attemptResult)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("PredicatesBasedAbortablePolicy{");
            sb.append("predicates=").append(predicates);
            sb.append('}');
            return sb.toString();
        }
    }
}
