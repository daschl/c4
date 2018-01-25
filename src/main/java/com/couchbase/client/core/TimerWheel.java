/*
 * Copyright (c) 2018 Couchbase, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.couchbase.client.core;

import com.couchbase.client.core.msg.Request;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This {@link TimerWheel} is a thin wrapper around the netty implementation
 * to provide convenience method and adapt it for Java 8 a bit more.
 *
 * <p>By default this {@link TimerWheel} has a tick duration/precision of
 * 100 milliseconds, which should be enough for I/O bound use cases.</p>
 *
 * @since 2.0.0
 */
public class TimerWheel {

  /**
   * Pre-create a {@link TimeoutException} for better performance, since where the
   * timeout happens the stack trace is useless anyways.
   */
  private static final TimeoutException TIMEOUT_EXCEPTION = new TimeoutException();

  static {
    TIMEOUT_EXCEPTION.setStackTrace(new StackTraceElement[0]);
  }

  /**
   * The actual timer wheel implementation from netty.
   */
  private final HashedWheelTimer timer;

  /**
   * Private constructor to create the {@link TimerWheel}.
   */
  private TimerWheel() {
    timer = new HashedWheelTimer(new DefaultThreadFactory("cb-timer", true));
    timer.start();
  }

  /**
   * Creates a new {@link TimerWheel} with default options.
   *
   * @return the newly created {@link TimerWheel}.
   */
  public static TimerWheel create() {
    return new TimerWheel();
  }

  /**
   * Schedules a closure/task to be executed if the timeout fires.
   *
   * @param task    the task to schedule once the timeout is reached.
   * @param timeout the timeout when to wakeup and run the task given.
   */
  public Timer schedule(final Consumer<Timer> task, final Duration timeout) {
    return com.couchbase.client.core.Timer.wrap(timer.newTimeout(
      t -> task.accept(Timer.wrap(t)),
      timeout.getNano(),
      TimeUnit.NANOSECONDS
    ));
  }

  /**
   * Convenience method to schedule a {@link Request} to track its timeout.
   *
   * @param request the message to track for timeout.
   * @param <R>     the generic type of the message, not used at this point.
   */
  public <R> void scheduleTimeout(final Request<R> request) {
    final Timeout timeout = timer.newTimeout(
        t -> request.fail(TIMEOUT_EXCEPTION),
        request.timeout().toNanos(),
        TimeUnit.NANOSECONDS
      );
    request.response().whenComplete(
        (R value, Throwable throwable) -> timeout.cancel()
    );
  }

  /**
   * Returns the number of currently scheduled tasks.
   *
   * @return the number of scheduled tasks.
   */
  public long scheduledTasks() {
    return timer.pendingTimeouts();
  }

  /**
   * Shutdown this timer permanently.
   *
   * @return the set of still-pending timeouts at the time after shutdown.
   */
  public Set<Timer> shutdown() {
    return timer
      .stop()
      .stream()
      .map(Timer::wrap)
      .collect(Collectors.toSet());
  }

}
