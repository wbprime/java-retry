package me.wbprime.retry.impl;

import com.google.common.collect.ImmutableList;

import me.wbprime.retry.api.AcceptablePolicy;
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
final class AcceptablePolicies {
    private AcceptablePolicies() {
        throw new AssertionError("Construction forbidden");
    }

    public static <T> AcceptablePolicy<T> or(
            final Collection<Predicate<AttemptResult<T>>> predicates
    ) {
        return predicates.isEmpty() ? onlyIfReturning() :
                new PredicatesBasedAcceptablePolicy<>(predicates);
    }

    public static <T> AcceptablePolicy<T> onlyIfNoThrowing() {
        return onlyIfReturning();
    }

    public static <T> AcceptablePolicy<T> onlyIfReturning() {
        return new ResultAvailable<>();
    }

    public static <T> AcceptablePolicy<T> onlyIfThrowing() {
        return onlyIfNoReturning();
    }

    public static <T> AcceptablePolicy<T> onlyIfNoReturning() {
        return new ExceptionAvailable<>();
    }

    public static <T> AcceptablePolicy<T> hasException(final Class<? extends Exception> excepted) {
        return new HasExceptions<>(ImmutableList.of(excepted));
    }

    public static <T> AcceptablePolicy<T> hasResult(final T excepted) {
        return new HasResults<>(ImmutableList.of(excepted));
    }

    public static <T> AcceptablePolicy<T> hasResults(final Iterable<T> excepted) {
        return new HasResults<>(excepted);
    }

    private static final class HasResults<T> implements AcceptablePolicy<T> {
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

    private static final class HasExceptions<T> implements AcceptablePolicy<T> {
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

    private static final class ResultAvailable<T> implements AcceptablePolicy<T> {
        @Override
        public boolean test(final AttemptResult<T> attemptResult) {
            return attemptResult.hasResult();
        }

        @Override
        public String toString() {
            return "ResultAvailable{}";
        }
    }

    private static final class ExceptionAvailable<T> implements AcceptablePolicy<T> {
        @Override
        public boolean test(final AttemptResult<T> attemptResult) {
            return attemptResult.hasException();
        }

        @Override
        public String toString() {
            return "ExceptionAvailable{}";
        }
    }

    private static final class PredicatesBasedAcceptablePolicy<T> implements AcceptablePolicy<T> {
        private final Iterable<Predicate<AttemptResult<T>>> predicates;

        PredicatesBasedAcceptablePolicy(
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
            final StringBuilder sb = new StringBuilder("PredicatesBasedAcceptablePolicy{");
            sb.append("predicates=").append(predicates);
            sb.append('}');
            return sb.toString();
        }
    }
}
