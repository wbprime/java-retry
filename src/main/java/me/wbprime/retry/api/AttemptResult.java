package me.wbprime.retry.api;

import java.time.Duration;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
public interface AttemptResult<T> {
    /**
     * @return the name
     */
    String name();

    /**
     * Count of total attempts
     *
     * @return attempts times count
     */
    long totalAttempts();

    /**
     * Elapsed time since the start of first attempt
     *
     * @return
     */
    Duration totalElapsedTime();

    /**
     * Elapsed time for current attempt
     *
     * @return
     */
    Duration currentElapsedTime();

    /**
     * Test if a result is returned
     *
     * @return true if a result is returned, false if throwns an exception
     */
    boolean hasResult();

    /**
     * Get the result if available
     *
     * @return the returned result
     * @throws IllegalStateException if no result returned
     */
    T getResult() throws IllegalStateException;

    /**
     * Test if an exception is thrown
     *
     * @return true if an exception is thrown, false if returned a result
     */
    boolean hasException();

    /**
     * Get the exception if available
     *
     * @return the thrown exception
     * @throws IllegalStateException if no exception thrown
     */
    Throwable getException() throws IllegalStateException;
}
