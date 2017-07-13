package me.wbprime.retry.impl;

import me.wbprime.retry.api.AttemptContext;
import me.wbprime.retry.api.AttemptResult;

import java.time.Duration;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
final class AttemptResults {
    private AttemptResults() {
        throw new AssertionError("Construction forbidden");
    }

    public static <T> AttemptResult<T> returned(
            final T result, final AttemptContext context, final Duration elapsed
    ) {
        return new ReturnedResult<>(result, context, elapsed);
    }

    public static <T> AttemptResult<T> thrown(
            final Throwable exception, final AttemptContext context, final Duration elapsed
    ) {
        return new ThrownResult<>(exception, context, elapsed);
    }

    private static final class ThrownResult<T> extends ContextBasedResult<T> implements AttemptResult<T> {
        private final Throwable exception;

        ThrownResult(final Throwable e, final AttemptContext context, final Duration elapsed) {
            super(context, elapsed);

            this.exception = e;
        }

        @Override
        public boolean hasResult() {
            return false;
        }

        @Override
        public T getResult() throws IllegalStateException {
            throw new IllegalStateException(
                    "No result returned, try getException() to get the thrown exception"
            );
        }

        @Override
        public boolean hasException() {
            return true;
        }

        @Override
        public Throwable getException() throws IllegalStateException {
            return exception;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ThrownResult");
            sb.append(super.toString());
            return sb.toString();
        }
    }

    private static final class ReturnedResult<T> extends ContextBasedResult<T> implements AttemptResult<T> {
        private final T result;

        ReturnedResult(final T r, final AttemptContext context, final Duration elapsed) {
            super(context, elapsed);

            this.result = r;
        }

        @Override
        public boolean hasResult() {
            return true;
        }

        @Override
        public T getResult() throws IllegalStateException {
            return result;
        }

        @Override
        public boolean hasException() {
            return false;
        }

        @Override
        public Throwable getException() throws IllegalStateException {
            throw new IllegalStateException(
                    "No exception thrown, try getResult() to get the returned result"
            );
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ReturnedResult");
            sb.append(super.toString());
            return sb.toString();
        }
    }

    private static abstract class ContextBasedResult<T> implements AttemptResult<T> {
        private final String name;
        private final long total;
        private final Duration curElapsed;
        private final Duration allElapsed;

        ContextBasedResult(final AttemptContext context, final Duration elapsed) {
            this.name = context.name();
            this.total = context.totalAttempts();
            this.curElapsed = elapsed;
            this.allElapsed = elapsed.plus(context.totalElapsedTime());

        }

        @Override
        public final String name() {
            return name;
        }

        @Override
        public final long totalAttempts() {
            return total;
        }

        @Override
        public final Duration totalElapsedTime() {
            return allElapsed;
        }

        @Override
        public final Duration currentElapsedTime() {
            return curElapsed;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("{");
            sb.append("name='").append(name).append('\'');
            sb.append(", total=").append(total);
            sb.append(", curElapsed=").append(curElapsed.toMillis()).append("ms");
            sb.append(", allElapsed=").append(allElapsed.toMillis()).append("ms");
            sb.append('}');
            return sb.toString();
        }
    }
}
