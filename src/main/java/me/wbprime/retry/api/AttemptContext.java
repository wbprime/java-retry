package me.wbprime.retry.api;

import java.time.Duration;

/**
 * TODO add brief description here
 *
 * Copyright © 2016 Elvis Wang <mail@wbprime.me>. All rights reserved.
 *
 * @author Elvis Wang
 */
public interface AttemptContext {
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
}
